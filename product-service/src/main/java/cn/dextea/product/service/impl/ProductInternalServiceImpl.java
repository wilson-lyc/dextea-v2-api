package cn.dextea.product.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.BatchStoreAvailabilityRequest;
import cn.dextea.product.dto.request.BatchStoreAvailabilityRequest.ProductAvailabilityItem;
import cn.dextea.product.dto.request.CartSnapshotRequest;
import cn.dextea.product.dto.response.CartOptionSnapshotResponse;
import cn.dextea.product.dto.response.CartSnapshotResponse;
import cn.dextea.product.dto.response.ProductStoreAvailabilityResponse;
import cn.dextea.product.entity.CustomizationItemEntity;
import cn.dextea.product.entity.CustomizationOptionEntity;
import cn.dextea.product.entity.ProductCustomizationItemBindingEntity;
import cn.dextea.product.entity.ProductEntity;
import cn.dextea.product.entity.StoreCustomizationOptionStatusEntity;
import cn.dextea.product.entity.StoreProductStatusEntity;
import cn.dextea.product.enums.CustomizationStatus;
import cn.dextea.product.enums.ProductErrorCode;
import cn.dextea.product.enums.ProductStatus;
import cn.dextea.product.enums.StoreCustomizationStatus;
import cn.dextea.product.enums.StoreProductStatus;
import cn.dextea.product.mapper.CustomizationItemMapper;
import cn.dextea.product.mapper.CustomizationOptionMapper;
import cn.dextea.product.mapper.ProductCustomizationItemBindingMapper;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.mapper.StoreCustomizationOptionStatusMapper;
import cn.dextea.product.mapper.StoreProductStatusMapper;
import cn.dextea.product.service.ProductInternalService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductInternalServiceImpl implements ProductInternalService {

    private final ProductMapper productMapper;
    private final CustomizationItemMapper customizationItemMapper;
    private final CustomizationOptionMapper customizationOptionMapper;
    private final ProductCustomizationItemBindingMapper bindingMapper;
    private final StoreCustomizationOptionStatusMapper storeOptionRelMapper;
    private final StoreProductStatusMapper storeProductStatusMapper;

    @Override
    public ApiResponse<CartSnapshotResponse> getCartSnapshot(CartSnapshotRequest request) {
        Long productId = request.getProductId();
        List<Long> optionIds = request.getOptionIds();

        ProductEntity product = productMapper.selectById(productId);
        if (product == null || Integer.valueOf(ProductStatus.DISABLED.getValue()).equals(product.getStatus())) {
            return fail(ProductErrorCode.PRODUCT_NOT_FOUND);
        }

        if (optionIds == null || optionIds.isEmpty()) {
            CartSnapshotResponse response = CartSnapshotResponse.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .basePrice(product.getPrice())
                    .options(List.of())
                    .build();
            return ApiResponse.success(response);
        }

        // 对传入的 optionIds 去重，避免重复 ID 导致误报 CUSTOMIZATION_OPTION_NOT_FOUND
        Set<Long> uniqueOptionIds = new HashSet<>(optionIds);

        // Fetch options and validate they belong to items bound to this product
        List<CustomizationOptionEntity> options = customizationOptionMapper.selectList(
                new LambdaQueryWrapper<CustomizationOptionEntity>()
                        .in(CustomizationOptionEntity::getId, uniqueOptionIds)
                        .eq(CustomizationOptionEntity::getStatus, CustomizationStatus.ACTIVE.getValue()));

        if (options.size() != uniqueOptionIds.size()) {
            return fail(ProductErrorCode.CUSTOMIZATION_OPTION_NOT_FOUND);
        }

        // Verify all option items are bound to this product
        List<Long> itemIds = options.stream().map(CustomizationOptionEntity::getItemId).distinct().toList();
        Set<Long> boundItemIds = bindingMapper.selectList(
                new LambdaQueryWrapper<ProductCustomizationItemBindingEntity>()
                        .eq(ProductCustomizationItemBindingEntity::getProductId, productId)
                        .in(ProductCustomizationItemBindingEntity::getItemId, itemIds))
                .stream()
                .map(ProductCustomizationItemBindingEntity::getItemId)
                .collect(Collectors.toSet());

        boolean allBound = itemIds.stream().allMatch(boundItemIds::contains);
        if (!allBound) {
            return fail(ProductErrorCode.CUSTOMIZATION_OPTION_NOT_FOUND);
        }

        // Fetch item names
        Map<Long, String> itemNameById = customizationItemMapper.selectList(
                new LambdaQueryWrapper<CustomizationItemEntity>()
                        .in(CustomizationItemEntity::getId, itemIds)
                        .eq(CustomizationItemEntity::getStatus, CustomizationStatus.ACTIVE.getValue()))
                .stream()
                .collect(Collectors.toMap(CustomizationItemEntity::getId, CustomizationItemEntity::getName));

        List<CartOptionSnapshotResponse> optionSnapshots = options.stream()
                .map(opt -> CartOptionSnapshotResponse.builder()
                        .optionId(opt.getId())
                        .itemId(opt.getItemId())
                        .itemName(itemNameById.get(opt.getItemId()))
                        .optionName(opt.getName())
                        .optionPrice(opt.getPrice())
                        .build())
                .collect(Collectors.toList());

        CartSnapshotResponse response = CartSnapshotResponse.builder()
                .productId(product.getId())
                .productName(product.getName())
                .basePrice(product.getPrice())
                .options(optionSnapshots)
                .build();

        return ApiResponse.success(response);
    }

    @Override
    public ApiResponse<List<ProductStoreAvailabilityResponse>> checkStoreAvailability(BatchStoreAvailabilityRequest request) {
        Long storeId = request.getStoreId();
        List<ProductAvailabilityItem> items = request.getItems();

        // Batch fetch all product statuses
        List<Long> productIds = items.stream().map(ProductAvailabilityItem::getProductId).toList();
        Map<Long, ProductEntity> productById = productMapper.selectList(
                new LambdaQueryWrapper<ProductEntity>()
                        .in(ProductEntity::getId, productIds))
                .stream()
                .collect(Collectors.toMap(ProductEntity::getId, p -> p));

        List<Long> productIds = items.stream().map(ProductAvailabilityItem::getProductId).toList();
        Set<Long> storeOnSaleProductIds = storeProductStatusMapper.selectList(
                new LambdaQueryWrapper<StoreProductStatusEntity>()
                        .eq(StoreProductStatusEntity::getStoreId, storeId)
                        .in(StoreProductStatusEntity::getProductId, productIds))
                .stream()
                .filter(e -> Objects.equals(e.getStatus(), StoreProductStatus.ENABLED.getValue()))
                .map(StoreProductStatusEntity::getProductId)
                .collect(Collectors.toSet());

        // Batch fetch all option statuses for all items in the request
        List<Long> allOptionIds = items.stream()
                .flatMap(item -> item.getOptionIds().stream())
                .distinct()
                .toList();

        Set<Long> globallyActiveOptionIds = Set.of();
        Set<Long> storeOnSaleOptionIds = Set.of();

        if (!allOptionIds.isEmpty()) {
            globallyActiveOptionIds = customizationOptionMapper.selectList(
                    new LambdaQueryWrapper<CustomizationOptionEntity>()
                            .in(CustomizationOptionEntity::getId, allOptionIds)
                            .eq(CustomizationOptionEntity::getStatus, CustomizationStatus.ACTIVE.getValue()))
                    .stream()
                    .map(CustomizationOptionEntity::getId)
                    .collect(Collectors.toSet());

            storeOnSaleOptionIds = storeOptionRelMapper.selectList(
                    new LambdaQueryWrapper<StoreCustomizationOptionStatusEntity>()
                            .eq(StoreCustomizationOptionStatusEntity::getStoreId, storeId)
                            .in(StoreCustomizationOptionStatusEntity::getOptionId, allOptionIds)
                            .eq(StoreCustomizationOptionStatusEntity::getStatus, StoreCustomizationStatus.ENABLED.getValue()))
                    .stream()
                    .map(StoreCustomizationOptionStatusEntity::getOptionId)
                    .collect(Collectors.toSet());
        }

        final Set<Long> finalGloballyActiveOptionIds = globallyActiveOptionIds;
        final Set<Long> finalStoreOnSaleOptionIds = storeOnSaleOptionIds;

        List<ProductStoreAvailabilityResponse> result = new ArrayList<>();
        for (ProductAvailabilityItem item : items) {
            Long productId = item.getProductId();
            ProductEntity product = productById.get(productId);

            boolean productAvailable = product != null
                    && Integer.valueOf(ProductStatus.ENABLED.getValue()).equals(product.getStatus())
                    && storeOnSaleProductIds.contains(productId);

            List<Long> unavailableOptionIds = List.of();
            if (productAvailable && !item.getOptionIds().isEmpty()) {
                unavailableOptionIds = item.getOptionIds().stream()
                        .filter(optId -> !finalGloballyActiveOptionIds.contains(optId)
                                || !finalStoreOnSaleOptionIds.contains(optId))
                        .toList();
            }

            result.add(ProductStoreAvailabilityResponse.builder()
                    .productId(productId)
                    .productAvailable(productAvailable)
                    .unavailableOptionIds(unavailableOptionIds)
                    .build());
        }

        return ApiResponse.success(result);
    }

    private <T> ApiResponse<T> fail(ProductErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}
