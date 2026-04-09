package cn.dextea.product.service.impl;

import cn.dextea.common.util.StringValueUtils;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.cache.CacheNames;
import cn.dextea.product.converter.CustomizationConverter;
import cn.dextea.product.converter.ProductConverter;
import cn.dextea.product.dto.request.ProductPageQueryWithStoreIdRequest;
import cn.dextea.product.dto.request.UpdateStoreProductSaleRequest;
import cn.dextea.product.dto.response.CustomizationItemBizDetailResponse;
import cn.dextea.product.dto.response.CustomizationOptionBizDetailResponse;
import cn.dextea.product.dto.response.ProductBizDetailResponse;
import cn.dextea.product.dto.response.ProductDetailResponse;
import cn.dextea.product.entity.CustomizationItemEntity;
import cn.dextea.product.entity.CustomizationOptionEntity;
import cn.dextea.product.entity.ProductCustomizationItemBindingEntity;
import cn.dextea.product.entity.ProductEntity;
import cn.dextea.product.entity.StoreCustomizationItemRelEntity;
import cn.dextea.product.entity.StoreCustomizationOptionRelEntity;
import cn.dextea.product.entity.StoreProductRelEntity;
import cn.dextea.product.enums.CustomizationStatus;
import cn.dextea.product.enums.ProductErrorCode;
import cn.dextea.product.enums.ProductStatus;
import cn.dextea.product.enums.StoreCustomizationSaleStatus;
import cn.dextea.product.enums.StoreProductSaleStatus;
import cn.dextea.product.mapper.CustomizationItemMapper;
import cn.dextea.product.mapper.CustomizationOptionMapper;
import cn.dextea.product.mapper.ProductCustomizationItemBindingMapper;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.mapper.StoreCustomizationItemRelMapper;
import cn.dextea.product.mapper.StoreCustomizationOptionRelMapper;
import cn.dextea.product.mapper.StoreProductRelMapper;
import cn.dextea.product.service.ProductBizService;
import cn.dextea.product.service.ProductCacheEvictionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductBizServiceImpl implements ProductBizService {

    private final ProductMapper productMapper;
    private final StoreProductRelMapper storeProductRelMapper;
    private final ProductCustomizationItemBindingMapper bindingMapper;
    private final CustomizationItemMapper customizationItemMapper;
    private final CustomizationOptionMapper customizationOptionMapper;
    private final StoreCustomizationItemRelMapper storeItemRelMapper;
    private final StoreCustomizationOptionRelMapper storeOptionRelMapper;
    private final ProductConverter productConverter;
    private final CustomizationConverter customizationConverter;
    private final ProductCacheEvictionService cacheEvictionService;

    @Override
    @Cacheable(
            cacheNames = CacheNames.PRODUCT_BIZ_LIST,
            key = "'store:' + #request.storeId + ':p:' + #request.current + ':s:' + #request.size + ':n:' + (#request.name ?: '')",
            unless = "#result.code != 0"
    )
    public ApiResponse<IPage<ProductDetailResponse>> getProductPage(ProductPageQueryWithStoreIdRequest request) {
        Long storeId = request.getStoreId();

        LambdaQueryWrapper<ProductEntity> productQuery = new LambdaQueryWrapper<ProductEntity>()
                .eq(ProductEntity::getStatus, ProductStatus.ENABLED.getValue())
                .like(StringValueUtils.hasText(request.getName()), ProductEntity::getName, request.getName().trim())
                .orderByDesc(ProductEntity::getId);
        IPage<ProductEntity> productPage = productMapper.selectPage(
                new Page<>(request.getCurrent(), request.getSize()), productQuery);

        List<ProductEntity> products = productPage.getRecords();
        if (products.isEmpty()) {
            return ApiResponse.success(productPage.convert(
                    p -> productConverter.toProductDetailResponseWithStoreStatus(p, StoreProductSaleStatus.SOLD_OUT.getValue())));
        }

        List<Long> productIds = products.stream().map(ProductEntity::getId).toList();
        LambdaQueryWrapper<StoreProductRelEntity> relQuery = new LambdaQueryWrapper<StoreProductRelEntity>()
                .eq(StoreProductRelEntity::getStoreId, storeId)
                .in(StoreProductRelEntity::getProductId, productIds);
        Set<Long> onSaleProductIds = storeProductRelMapper.selectList(relQuery).stream()
                .map(StoreProductRelEntity::getProductId)
                .collect(Collectors.toSet());

        return ApiResponse.success(productPage.convert(entity -> {
            int storeStatus = onSaleProductIds.contains(entity.getId())
                    ? StoreProductSaleStatus.ON_SALE.getValue()
                    : StoreProductSaleStatus.SOLD_OUT.getValue();
            return productConverter.toProductDetailResponseWithStoreStatus(entity, storeStatus);
        }));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> updateSaleStatus(Long productId, UpdateStoreProductSaleRequest request) {
        ProductEntity product = productMapper.selectById(productId);
        if (product == null) {
            return fail(ProductErrorCode.PRODUCT_NOT_FOUND);
        }

        Long storeId = request.getStoreId();
        LambdaQueryWrapper<StoreProductRelEntity> relQuery = new LambdaQueryWrapper<StoreProductRelEntity>()
                .eq(StoreProductRelEntity::getStoreId, storeId)
                .eq(StoreProductRelEntity::getProductId, productId);

        if (Boolean.TRUE.equals(request.getOnSale())) {
            if (!storeProductRelMapper.exists(relQuery)) {
                StoreProductRelEntity rel = StoreProductRelEntity.builder()
                        .storeId(storeId)
                        .productId(productId)
                        .build();
                if (storeProductRelMapper.insert(rel) != 1) {
                    return fail(ProductErrorCode.STORE_SALE_STATUS_UPDATE_FAILED);
                }
            }
        } else {
            storeProductRelMapper.delete(relQuery);
        }

        cacheEvictionService.evictProductBizDetail(productId, storeId);
        cacheEvictionService.evictProductBizListByStore(storeId);

        return ApiResponse.success();
    }

    @Override
    @Cacheable(
            cacheNames = CacheNames.PRODUCT_BIZ_DETAIL,
            key = "'productId:' + #productId + ':storeId:' + #storeId",
            unless = "#result.code != 0"
    )
    public ApiResponse<ProductBizDetailResponse> getProductDetail(Long productId, Long storeId) {
        ProductEntity product = productMapper.selectById(productId);
        if (product == null) {
            return fail(ProductErrorCode.PRODUCT_NOT_FOUND);
        }
        if (ProductStatus.DISABLED.getValue() == product.getStatus()) {
            return fail(ProductErrorCode.PRODUCT_DISABLED);
        }

        boolean onSale = storeProductRelMapper.exists(new LambdaQueryWrapper<StoreProductRelEntity>()
                .eq(StoreProductRelEntity::getStoreId, storeId)
                .eq(StoreProductRelEntity::getProductId, productId));
        int storeProductStatus = onSale
                ? StoreProductSaleStatus.ON_SALE.getValue()
                : StoreProductSaleStatus.SOLD_OUT.getValue();

        List<ProductCustomizationItemBindingEntity> bindings = bindingMapper.selectList(
                new LambdaQueryWrapper<ProductCustomizationItemBindingEntity>()
                        .eq(ProductCustomizationItemBindingEntity::getProductId, productId)
                        .orderByAsc(ProductCustomizationItemBindingEntity::getSortOrder));

        if (bindings.isEmpty()) {
            return ApiResponse.success(productConverter.toProductBizDetailResponse(product, storeProductStatus, List.of()));
        }

        List<Long> boundItemIds = bindings.stream()
                .map(ProductCustomizationItemBindingEntity::getItemId)
                .collect(Collectors.toList());

        List<CustomizationItemEntity> activeItems = customizationItemMapper.selectList(
                new LambdaQueryWrapper<CustomizationItemEntity>()
                        .in(CustomizationItemEntity::getId, boundItemIds)
                        .eq(CustomizationItemEntity::getStatus, CustomizationStatus.ACTIVE.getValue()));

        if (activeItems.isEmpty()) {
            return ApiResponse.success(productConverter.toProductBizDetailResponse(product, storeProductStatus, List.of()));
        }

        List<Long> activeItemIds = activeItems.stream()
                .map(CustomizationItemEntity::getId)
                .collect(Collectors.toList());

        Set<Long> onSaleItemIds = storeItemRelMapper.selectList(
                new LambdaQueryWrapper<StoreCustomizationItemRelEntity>()
                        .eq(StoreCustomizationItemRelEntity::getStoreId, storeId)
                        .in(StoreCustomizationItemRelEntity::getItemId, activeItemIds))
                .stream()
                .map(StoreCustomizationItemRelEntity::getItemId)
                .collect(Collectors.toSet());

        List<CustomizationOptionEntity> activeOptions = customizationOptionMapper.selectList(
                new LambdaQueryWrapper<CustomizationOptionEntity>()
                        .in(CustomizationOptionEntity::getItemId, activeItemIds)
                        .eq(CustomizationOptionEntity::getStatus, CustomizationStatus.ACTIVE.getValue())
                        .orderByAsc(CustomizationOptionEntity::getId));

        Set<Long> onSaleOptionIds = Set.of();
        if (!activeOptions.isEmpty()) {
            List<Long> activeOptionIds = activeOptions.stream()
                    .map(CustomizationOptionEntity::getId)
                    .collect(Collectors.toList());
            onSaleOptionIds = storeOptionRelMapper.selectList(
                    new LambdaQueryWrapper<StoreCustomizationOptionRelEntity>()
                            .eq(StoreCustomizationOptionRelEntity::getStoreId, storeId)
                            .in(StoreCustomizationOptionRelEntity::getOptionId, activeOptionIds))
                    .stream()
                    .map(StoreCustomizationOptionRelEntity::getOptionId)
                    .collect(Collectors.toSet());
        }

        final Set<Long> finalOnSaleOptionIds = onSaleOptionIds;
        Map<Long, List<CustomizationOptionBizDetailResponse>> optionsByItemId = activeOptions.stream()
                .collect(Collectors.groupingBy(
                        CustomizationOptionEntity::getItemId,
                        Collectors.mapping(option -> {
                            int optionStoreStatus = finalOnSaleOptionIds.contains(option.getId())
                                    ? StoreCustomizationSaleStatus.ON_SALE.getValue()
                                    : StoreCustomizationSaleStatus.SOLD_OUT.getValue();
                            return customizationConverter.toOptionBizDetailResponse(option, optionStoreStatus);
                        }, Collectors.toList())));

        Map<Long, CustomizationItemEntity> itemById = activeItems.stream()
                .collect(Collectors.toMap(CustomizationItemEntity::getId, e -> e));
        List<CustomizationItemBizDetailResponse> itemResponses = new ArrayList<>();
        for (ProductCustomizationItemBindingEntity binding : bindings) {
            CustomizationItemEntity item = itemById.get(binding.getItemId());
            if (item == null) {
                continue;
            }
            int itemStoreStatus = onSaleItemIds.contains(item.getId())
                    ? StoreCustomizationSaleStatus.ON_SALE.getValue()
                    : StoreCustomizationSaleStatus.SOLD_OUT.getValue();
            List<CustomizationOptionBizDetailResponse> options =
                    optionsByItemId.getOrDefault(item.getId(), List.of());
            itemResponses.add(customizationConverter.toItemBizDetailResponse(item, itemStoreStatus, options));
        }

        return ApiResponse.success(productConverter.toProductBizDetailResponse(product, storeProductStatus, itemResponses));
    }

    private <T> ApiResponse<T> fail(ProductErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}
