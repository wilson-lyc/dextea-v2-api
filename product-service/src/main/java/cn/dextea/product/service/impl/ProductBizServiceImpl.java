package cn.dextea.product.service.impl;

import cn.dextea.common.util.StringValueUtils;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.CustomizationConverter;
import cn.dextea.product.converter.ProductConverter;
import cn.dextea.product.dto.request.StoreProductPageRequest;
import cn.dextea.product.dto.request.UpdateStoreProductStatusRequest;
import cn.dextea.product.dto.response.CustomerProductDetailResponse;
import cn.dextea.product.dto.response.CustomizationItemBizDetailResponse;
import cn.dextea.product.dto.response.CustomizationOptionBizDetailResponse;
import cn.dextea.product.dto.response.ProductDetailResponse;
import cn.dextea.product.entity.CustomizationItemEntity;
import cn.dextea.product.entity.CustomizationOptionEntity;
import cn.dextea.product.entity.ProductCustomizationItemBindingEntity;
import cn.dextea.product.entity.ProductEntity;
import cn.dextea.product.entity.StoreCustomizationItemStatusEntity;
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
import cn.dextea.product.mapper.StoreCustomizationItemStatusMapper;
import cn.dextea.product.mapper.StoreCustomizationOptionStatusMapper;
import cn.dextea.product.mapper.StoreProductStatusMapper;
import cn.dextea.product.service.ProductBizService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductBizServiceImpl implements ProductBizService {

    private final ProductMapper productMapper;
    private final StoreProductStatusMapper storeProductStatusMapper;
    private final ProductCustomizationItemBindingMapper bindingMapper;
    private final CustomizationItemMapper customizationItemMapper;
    private final StoreCustomizationItemStatusMapper storeItemStatusMapper;
    private final CustomizationOptionMapper customizationOptionMapper;
    private final StoreCustomizationOptionStatusMapper storeOptionStatusMapper;
    private final ProductConverter productConverter;
    private final CustomizationConverter customizationConverter;

    /**
     * 获取分页数据网关
     */
    @Override
    public ApiResponse<IPage<ProductDetailResponse>> getPage(StoreProductPageRequest request) {
        Long storeId = request.getStoreId();
        Integer storeStatus = request.getStoreStatus();

        // 商品名模糊查询 + 全局状态筛选
        LambdaQueryWrapper<ProductEntity> productQuery = new LambdaQueryWrapper<ProductEntity>()
                .like(StringValueUtils.hasText(request.getName()), ProductEntity::getName, request.getName().trim())
                .eq(request.getGlobalStatus() != null, ProductEntity::getStatus, request.getGlobalStatus())
                .orderByDesc(ProductEntity::getId);

        // 指定门店状态，走门店状态过滤逻辑
        if (storeStatus != null) {
            return getPage(request, storeId, storeStatus, productQuery);
        }
        return getPage(request, storeId, productQuery);
    }

    /**
     * 按指定商品门店状态筛选
     */
    private ApiResponse<IPage<ProductDetailResponse>> getPage(
            StoreProductPageRequest request, Long storeId, Integer requestedStatus,
            LambdaQueryWrapper<ProductEntity> productQuery) {
        if (Objects.equals(StoreProductStatus.DISABLED.getValue(), requestedStatus)) {
            // 排除非售罄商品
            List<Long> excludedId = storeProductStatusMapper.selectList(
                    new LambdaQueryWrapper<StoreProductStatusEntity>()
                            .eq(StoreProductStatusEntity::getStoreId, storeId)
                            .ne(StoreProductStatusEntity::getStatus, StoreProductStatus.DISABLED.getValue()))
                    .stream()
                    .map(StoreProductStatusEntity::getProductId)
                    .toList();
            if (!excludedId.isEmpty()) {
                productQuery.notIn(ProductEntity::getId, excludedId);
            }
        } else {
            // 获取指定门店状态的商品
            List<Long> matchingIds = storeProductStatusMapper.selectList(
                    new LambdaQueryWrapper<StoreProductStatusEntity>()
                            .eq(StoreProductStatusEntity::getStoreId, storeId)
                            .eq(StoreProductStatusEntity::getStatus, requestedStatus))
                    .stream()
                    .map(StoreProductStatusEntity::getProductId)
                    .toList();
            if (matchingIds.isEmpty()) {
                return ApiResponse.success(new Page<>(request.getCurrent(), request.getSize()));
            }
            productQuery.in(ProductEntity::getId, matchingIds);
        }

        IPage<ProductEntity> productPage = productMapper.selectPage(
                new Page<>(request.getCurrent(), request.getSize()), productQuery);
        return ApiResponse.success(fillStoreStatuses(productPage, storeId));
    }

    /**
     * 不按门店状态筛选的分页
     */
    private ApiResponse<IPage<ProductDetailResponse>> getPage(
            StoreProductPageRequest request, Long storeId,
            LambdaQueryWrapper<ProductEntity> productQuery) {

        IPage<ProductEntity> productPage = productMapper.selectPage(
                new Page<>(request.getCurrent(), request.getSize()), productQuery);
        return ApiResponse.success(fillStoreStatuses(productPage, storeId));
    }

    /**
     * 回填门店状态
     * */
    private IPage<ProductDetailResponse> fillStoreStatuses(IPage<ProductEntity> productPage, Long storeId) {
        List<ProductEntity> products = productPage.getRecords();
        if (products.isEmpty()) {
            return productPage.convert(
                    entity -> productConverter.toProductDetailResponse(entity, StoreProductStatus.DISABLED.getValue()));
        }

        List<Long> productIds = products.stream().map(ProductEntity::getId).toList();
        Map<Long, Integer> productStatusMap = storeProductStatusMapper.selectList(
                new LambdaQueryWrapper<StoreProductStatusEntity>()
                        .eq(StoreProductStatusEntity::getStoreId, storeId)
                        .in(StoreProductStatusEntity::getProductId, productIds))
                .stream()
                .collect(Collectors.toMap(
                        StoreProductStatusEntity::getProductId,
                        StoreProductStatusEntity::getStatus,
                        (left, right) -> right));

        // 逐条回填门店状态，未读到状态记录时默认售罄
        return productPage.convert(entity -> {
            int storeStatus = productStatusMap.getOrDefault(entity.getId(), StoreProductStatus.DISABLED.getValue());
            return productConverter.toProductDetailResponse(entity, storeStatus);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> updateStatus(Long productId, UpdateStoreProductStatusRequest request) {
        if (productMapper.selectById(productId) == null) {
            return fail(ProductErrorCode.PRODUCT_NOT_FOUND);
        }

        Long storeId = request.getStoreId();
        StoreProductStatusEntity existingStatus = storeProductStatusMapper.selectOne(
                new LambdaQueryWrapper<StoreProductStatusEntity>()
                        .eq(StoreProductStatusEntity::getStoreId, storeId)
                        .eq(StoreProductStatusEntity::getProductId, productId));
        if (existingStatus == null) {
            StoreProductStatusEntity statusEntity = StoreProductStatusEntity.builder()
                    .storeId(storeId)
                    .productId(productId)
                    .status(request.getStatus())
                    .build();
            if (storeProductStatusMapper.insert(statusEntity) != 1) {
                return fail(ProductErrorCode.STORE_SALE_STATUS_UPDATE_FAILED);
            }
        } else {
            int rows = storeProductStatusMapper.update(null,
                    new LambdaUpdateWrapper<StoreProductStatusEntity>()
                            .eq(StoreProductStatusEntity::getStoreId, storeId)
                            .eq(StoreProductStatusEntity::getProductId, productId)
                            .set(StoreProductStatusEntity::getStatus, request.getStatus()));
            if (rows != 1) {
                return fail(ProductErrorCode.STORE_SALE_STATUS_UPDATE_FAILED);
            }
        }

        return ApiResponse.success();
    }

    @Override
    public ApiResponse<CustomerProductDetailResponse> getCustomerDetail(Long productId, Long storeId) {
        ProductEntity product = productMapper.selectById(productId);
        if (product == null) {
            return fail(ProductErrorCode.PRODUCT_NOT_FOUND);
        }
        if (!Objects.equals(product.getStatus(), ProductStatus.ENABLED.getValue())) {
            return fail(ProductErrorCode.PRODUCT_DISABLED);
        }

        StoreProductStatusEntity productStoreStatus = storeProductStatusMapper.selectOne(
                new LambdaQueryWrapper<StoreProductStatusEntity>()
                        .eq(StoreProductStatusEntity::getStoreId, storeId)
                        .eq(StoreProductStatusEntity::getProductId, productId));
        int productStoreStatusValue = productStoreStatus != null
                ? productStoreStatus.getStatus()
                : StoreProductStatus.DISABLED.getValue();

        List<CustomizationItemBizDetailResponse> customizationItems = buildCustomizationItems(productId, storeId);

        return ApiResponse.success(productConverter.toCustomerProductDetailResponse(
                product, productStoreStatusValue, customizationItems));
    }

    private List<CustomizationItemBizDetailResponse> buildCustomizationItems(Long productId, Long storeId) {
        List<ProductCustomizationItemBindingEntity> bindings = bindingMapper.selectList(
                new LambdaQueryWrapper<ProductCustomizationItemBindingEntity>()
                        .eq(ProductCustomizationItemBindingEntity::getProductId, productId)
                        .orderByAsc(ProductCustomizationItemBindingEntity::getSortOrder));

        if (bindings.isEmpty()) {
            return List.of();
        }

        List<Long> itemIds = bindings.stream()
                .map(ProductCustomizationItemBindingEntity::getItemId)
                .toList();

        List<CustomizationItemEntity> activeItems = customizationItemMapper.selectList(
                new LambdaQueryWrapper<CustomizationItemEntity>()
                        .in(CustomizationItemEntity::getId, itemIds)
                        .eq(CustomizationItemEntity::getStatus, CustomizationStatus.ACTIVE.getValue()));

        if (activeItems.isEmpty()) {
            return List.of();
        }

        List<Long> activeItemIds = activeItems.stream()
                .map(CustomizationItemEntity::getId)
                .toList();

        Map<Long, Integer> itemStoreStatusMap = toStatusMap(
                storeItemStatusMapper.selectList(
                        new LambdaQueryWrapper<StoreCustomizationItemStatusEntity>()
                                .eq(StoreCustomizationItemStatusEntity::getStoreId, storeId)
                                .in(StoreCustomizationItemStatusEntity::getItemId, activeItemIds)),
                StoreCustomizationItemStatusEntity::getItemId,
                StoreCustomizationItemStatusEntity::getStatus);

        List<CustomizationOptionEntity> activeOptions = customizationOptionMapper.selectList(
                new LambdaQueryWrapper<CustomizationOptionEntity>()
                        .in(CustomizationOptionEntity::getItemId, activeItemIds)
                        .eq(CustomizationOptionEntity::getStatus, CustomizationStatus.ACTIVE.getValue())
                        .orderByAsc(CustomizationOptionEntity::getId));

        Map<Long, Integer> optionStoreStatusMap = activeOptions.isEmpty() ? Map.of() : toStatusMap(
                storeOptionStatusMapper.selectList(
                        new LambdaQueryWrapper<StoreCustomizationOptionStatusEntity>()
                                .eq(StoreCustomizationOptionStatusEntity::getStoreId, storeId)
                                .in(StoreCustomizationOptionStatusEntity::getOptionId,
                                        activeOptions.stream().map(CustomizationOptionEntity::getId).toList())),
                StoreCustomizationOptionStatusEntity::getOptionId,
                StoreCustomizationOptionStatusEntity::getStatus);

        Map<Long, List<CustomizationOptionBizDetailResponse>> optionsByItemId = activeOptions.stream()
                .collect(Collectors.groupingBy(
                        CustomizationOptionEntity::getItemId,
                        Collectors.mapping(
                                option -> customizationConverter.toOptionBizDetailResponse(option,
                                        optionStoreStatusMap.getOrDefault(
                                                option.getId(), StoreCustomizationStatus.DISABLED.getValue())),
                                Collectors.toList())));

        Map<Long, Integer> bindingSortMap = bindings.stream()
                .collect(Collectors.toMap(
                        ProductCustomizationItemBindingEntity::getItemId,
                        ProductCustomizationItemBindingEntity::getSortOrder,
                        (left, right) -> left));

        return activeItems.stream()
                .sorted(Comparator.comparingInt(i -> bindingSortMap.getOrDefault(i.getId(), Integer.MAX_VALUE)))
                .map(item -> customizationConverter.toItemBizDetailResponse(item,
                        itemStoreStatusMap.getOrDefault(item.getId(), StoreCustomizationStatus.DISABLED.getValue()),
                        optionsByItemId.getOrDefault(item.getId(), List.of())))
                .toList();
    }

    private <E> Map<Long, Integer> toStatusMap(
            List<E> rows, Function<E, Long> idKey, Function<E, Integer> statusKey) {
        return rows.stream().collect(Collectors.toMap(idKey, statusKey, (left, right) -> right));
    }

    private <T> ApiResponse<T> fail(ProductErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}
