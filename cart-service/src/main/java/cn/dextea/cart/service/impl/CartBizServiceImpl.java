package cn.dextea.cart.service.impl;

import cn.dextea.cart.client.ProductInternalClient;
import cn.dextea.cart.client.dto.request.BatchStoreAvailabilityRequest;
import cn.dextea.cart.client.dto.request.BatchStoreAvailabilityRequest.ProductAvailabilityItem;
import cn.dextea.cart.client.dto.request.CartSnapshotRequest;
import cn.dextea.cart.client.dto.response.CartOptionSnapshotResponse;
import cn.dextea.cart.client.dto.response.CartSnapshotResponse;
import cn.dextea.cart.client.dto.response.ProductStoreAvailabilityResponse;
import cn.dextea.cart.converter.CartConverter;
import cn.dextea.cart.dto.request.AddCartItemRequest;
import cn.dextea.cart.dto.request.CartItemSelectionRequest;
import cn.dextea.cart.dto.request.SwitchStoreRequest;
import cn.dextea.cart.dto.request.UpdateCartItemQuantityRequest;
import cn.dextea.cart.dto.response.CartDetailResponse;
import cn.dextea.cart.dto.response.RemovedCartItemResponse;
import cn.dextea.cart.dto.response.SwitchStoreResponse;
import cn.dextea.cart.enums.CartErrorCode;
import cn.dextea.cart.enums.RemoveReason;
import cn.dextea.cart.model.CartItem;
import cn.dextea.cart.model.CartItemSelection;
import cn.dextea.cart.service.CartBizService;
import cn.dextea.cart.util.SkuIdUtil;
import cn.dextea.common.web.response.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartBizServiceImpl implements CartBizService {

    private static final long CART_TTL_SECONDS = 86400L;

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final ProductInternalClient productInternalClient;
    private final CartConverter cartConverter;

    @Override
    public ApiResponse<Void> addItem(Long customerId, AddCartItemRequest request) {
        String cartKey = cartKey(customerId);
        List<Long> optionIds = request.getSelections().stream()
                .map(CartItemSelectionRequest::getOptionId)
                .collect(Collectors.toList());
        String skuId = SkuIdUtil.buildSkuId(request.getProductId(), optionIds);

        HashOperations<String, String, String> hashOps = stringRedisTemplate.opsForHash();
        String existing = hashOps.get(cartKey, skuId);

        if (existing != null) {
            // SKU already in cart: increment quantity
            CartItem item = deserialize(existing);
            item.setQuantity(item.getQuantity() + request.getQuantity());
            hashOps.put(cartKey, skuId, serialize(item));
        } else {
            // Fetch product snapshot from product-service
            CartSnapshotRequest snapshotRequest = CartSnapshotRequest.builder()
                    .productId(request.getProductId())
                    .optionIds(optionIds)
                    .build();
            ApiResponse<CartSnapshotResponse> snapshotResp = productInternalClient.getCartSnapshot(snapshotRequest);

            if (snapshotResp == null || snapshotResp.getData() == null) {
                return fail(CartErrorCode.PRODUCT_NOT_FOUND);
            }

            CartSnapshotResponse snapshot = snapshotResp.getData();
            Map<Long, CartOptionSnapshotResponse> optionById = snapshot.getOptions().stream()
                    .collect(Collectors.toMap(CartOptionSnapshotResponse::getOptionId, o -> o));

            List<CartItemSelection> selections = new ArrayList<>();
            for (CartItemSelectionRequest sel : request.getSelections()) {
                CartOptionSnapshotResponse opt = optionById.get(sel.getOptionId());
                if (opt == null) {
                    return fail(CartErrorCode.INVALID_OPTION);
                }
                selections.add(CartItemSelection.builder()
                        .itemId(opt.getItemId())
                        .itemName(opt.getItemName())
                        .optionId(opt.getOptionId())
                        .optionName(opt.getOptionName())
                        .optionPrice(opt.getOptionPrice())
                        .build());
            }

            BigDecimal unitPrice = snapshot.getBasePrice().add(
                    selections.stream()
                            .map(CartItemSelection::getOptionPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add));

            CartItem newItem = CartItem.builder()
                    .skuId(skuId)
                    .productId(snapshot.getProductId())
                    .productName(snapshot.getProductName())
                    .basePrice(snapshot.getBasePrice())
                    .quantity(request.getQuantity())
                    .selections(selections)
                    .unitPrice(unitPrice)
                    .addedAt(LocalDateTime.now())
                    .build();

            hashOps.put(cartKey, skuId, serialize(newItem));
        }

        refreshTtl(cartKey);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<CartDetailResponse> getCart(Long customerId) {
        HashOperations<String, String, String> hashOps = stringRedisTemplate.opsForHash();
        Map<String, String> entries = hashOps.entries(cartKey(customerId));

        if (entries.isEmpty()) {
            CartDetailResponse empty = CartDetailResponse.builder()
                    .items(List.of())
                    .totalQuantity(0)
                    .totalPrice(BigDecimal.ZERO)
                    .build();
            return ApiResponse.success(empty);
        }

        List<CartItem> items = entries.values().stream()
                .map(this::deserialize)
                .collect(Collectors.toList());

        return ApiResponse.success(cartConverter.toCartDetailResponse(items));
    }

    @Override
    public ApiResponse<Void> updateItemQuantity(Long customerId, UpdateCartItemQuantityRequest request) {
        String cartKey = cartKey(customerId);
        String skuId = request.getSkuId();
        HashOperations<String, String, String> hashOps = stringRedisTemplate.opsForHash();

        String existing = hashOps.get(cartKey, skuId);
        if (existing == null) {
            return fail(CartErrorCode.CART_ITEM_NOT_FOUND);
        }

        if (request.getQuantity() == 0) {
            hashOps.delete(cartKey, skuId);
            if (hashOps.size(cartKey) == 0) {
                stringRedisTemplate.delete(cartKey);
                return ApiResponse.success();
            }
        } else {
            CartItem item = deserialize(existing);
            item.setQuantity(request.getQuantity());
            hashOps.put(cartKey, skuId, serialize(item));
        }

        refreshTtl(cartKey);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<Void> deleteItem(Long customerId, String skuId) {
        String cartKey = cartKey(customerId);
        HashOperations<String, String, String> hashOps = stringRedisTemplate.opsForHash();

        hashOps.delete(cartKey, skuId);

        if (hashOps.size(cartKey) == 0) {
            stringRedisTemplate.delete(cartKey);
        } else {
            refreshTtl(cartKey);
        }

        return ApiResponse.success();
    }

    @Override
    public ApiResponse<Void> clearCart(Long customerId) {
        stringRedisTemplate.delete(cartKey(customerId));
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<SwitchStoreResponse> switchStore(Long customerId, SwitchStoreRequest request) {
        String cartKey = cartKey(customerId);
        HashOperations<String, String, String> hashOps = stringRedisTemplate.opsForHash();
        Map<String, String> entries = hashOps.entries(cartKey);

        if (entries.isEmpty()) {
            return ApiResponse.success(SwitchStoreResponse.builder()
                    .removedItems(List.of())
                    .hasRemovedItems(false)
                    .build());
        }

        List<CartItem> cartItems = entries.values().stream()
                .map(this::deserialize)
                .collect(Collectors.toList());

        // Build availability check request preserving order (one item per SKU)
        List<ProductAvailabilityItem> availabilityItems = cartItems.stream()
                .map(item -> ProductAvailabilityItem.builder()
                        .productId(item.getProductId())
                        .optionIds(item.getSelections().stream()
                                .map(CartItemSelection::getOptionId)
                                .toList())
                        .build())
                .collect(Collectors.toList());

        BatchStoreAvailabilityRequest availabilityRequest = BatchStoreAvailabilityRequest.builder()
                .storeId(request.getStoreId())
                .items(availabilityItems)
                .build();

        ApiResponse<List<ProductStoreAvailabilityResponse>> availabilityResp =
                productInternalClient.checkStoreAvailability(availabilityRequest);

        // Match results by index (product-service preserves request order)
        List<ProductStoreAvailabilityResponse> availabilityList = availabilityResp.getData();

        List<RemovedCartItemResponse> removedItems = new ArrayList<>();
        List<String> skuIdsToDelete = new ArrayList<>();

        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            ProductStoreAvailabilityResponse availability = availabilityList.get(i);

            if (!availability.isProductAvailable()) {
                skuIdsToDelete.add(item.getSkuId());
                removedItems.add(RemovedCartItemResponse.builder()
                        .skuId(item.getSkuId())
                        .productName(item.getProductName())
                        .reason(RemoveReason.PRODUCT_UNAVAILABLE)
                        .build());
            } else if (availability.getUnavailableOptionIds() != null
                    && !availability.getUnavailableOptionIds().isEmpty()) {
                skuIdsToDelete.add(item.getSkuId());
                removedItems.add(RemovedCartItemResponse.builder()
                        .skuId(item.getSkuId())
                        .productName(item.getProductName())
                        .reason(RemoveReason.OPTION_UNAVAILABLE)
                        .build());
            }
        }

        if (!skuIdsToDelete.isEmpty()) {
            hashOps.delete(cartKey, skuIdsToDelete.toArray(new String[0]));
        }

        if (hashOps.size(cartKey) == 0) {
            stringRedisTemplate.delete(cartKey);
        } else {
            refreshTtl(cartKey);
        }

        return ApiResponse.success(SwitchStoreResponse.builder()
                .removedItems(removedItems)
                .hasRemovedItems(!removedItems.isEmpty())
                .build());
    }

    private String cartKey(Long customerId) {
        return "cart:" + customerId;
    }

    private void refreshTtl(String cartKey) {
        stringRedisTemplate.expire(cartKey, CART_TTL_SECONDS, TimeUnit.SECONDS);
    }

    private String serialize(CartItem item) {
        try {
            return objectMapper.writeValueAsString(item);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize CartItem", e);
        }
    }

    private CartItem deserialize(String json) {
        try {
            return objectMapper.readValue(json, CartItem.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize CartItem", e);
        }
    }

    private <T> ApiResponse<T> fail(CartErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}
