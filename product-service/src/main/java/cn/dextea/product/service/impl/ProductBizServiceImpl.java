package cn.dextea.product.service.impl;

import cn.dextea.common.util.StringValueUtils;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.cache.CacheNames;
import cn.dextea.product.converter.CustomizationConverter;
import cn.dextea.product.converter.ProductConverter;
import cn.dextea.product.dto.request.StoreProductPageRequest;
import cn.dextea.product.dto.request.UpdateStoreProductStatusRequest;
import cn.dextea.product.dto.response.CustomizationItemBizDetailResponse;
import cn.dextea.product.dto.response.CustomizationOptionBizDetailResponse;
import cn.dextea.product.dto.response.ProductBizDetailResponse;
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
import cn.dextea.product.enums.StoreCustomizationSaleStatus;
import cn.dextea.product.enums.StoreProductStatus;
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
    public ApiResponse<IPage<ProductDetailResponse>> getProductPage(StoreProductPageRequest request) {
        Long storeId = request.getStoreId();
        Integer status = request.getStatus();

        // 基础查询条件：仅返回全局启用的商品，支持名称模糊搜索
        LambdaQueryWrapper<ProductEntity> productQuery = new LambdaQueryWrapper<ProductEntity>()
                .eq(ProductEntity::getStatus, ProductStatus.ENABLED.getValue())
                .like(StringValueUtils.hasText(request.getName()), ProductEntity::getName, request.getName().trim())
                .orderByDesc(ProductEntity::getId);

        if (status != null) {
            return getProductPageFilteredByStoreStatus(request, storeId, status, productQuery);
        }
        return getProductPageWithStoreStatus(request, storeId, productQuery);
    }

    /**
     * 按门店在售状态筛选分页：
     * ON_SALE  — 先取该门店的在售商品ID，再用 IN 缩小商品范围；结果全部标记为在售
     * SOLD_OUT — 先取该门店的在售商品ID，再用 NOT IN 排除；结果全部标记为售罄
     * 两种情况下结果的门店状态已由筛选条件确定，无需逐条回查
     */
    private ApiResponse<IPage<ProductDetailResponse>> getProductPageFilteredByStoreStatus(
            StoreProductPageRequest request, Long storeId, int status,
            LambdaQueryWrapper<ProductEntity> productQuery) {

        // 查询该门店当前在售的商品ID集合（store_product_rel 存在记录即代表在售）
        Set<Long> onSaleIds = storeProductRelMapper.selectList(
                new LambdaQueryWrapper<StoreProductStatusEntity>()
                        .eq(StoreProductStatusEntity::getStoreId, storeId)
                        .select(StoreProductStatusEntity::getProductId))
                .stream()
                .map(StoreProductStatusEntity::getProductId)
                .collect(Collectors.toSet());

        int fixedStoreStatus;
        if (StoreProductStatus.ENABLED.getValue() == status) {
            // 在售：商品必须在关联表中存在；若集合为空则该门店无在售商品，直接返回空分页
            if (onSaleIds.isEmpty()) {
                IPage<ProductDetailResponse> emptyPage = new Page<>(request.getCurrent(), request.getSize());
                return ApiResponse.success(emptyPage);
            }
            productQuery.in(ProductEntity::getId, onSaleIds);
            fixedStoreStatus = StoreProductStatus.ENABLED.getValue();
        } else {
            // 售罄：全局启用但不在关联表中的商品；若在售集合不为空才需要排除
            if (!onSaleIds.isEmpty()) {
                productQuery.notIn(ProductEntity::getId, onSaleIds);
            }
            fixedStoreStatus = StoreProductStatus.DISABLED.getValue();
        }

        IPage<ProductEntity> productPage = productMapper.selectPage(
                new Page<>(request.getCurrent(), request.getSize()), productQuery);

        // 分页结果的门店状态已由筛选条件确定，直接赋固定值，无需再查关联表
        return ApiResponse.success(productPage.convert(
                entity -> productConverter.toProductDetailResponseWithStoreStatus(entity, fixedStoreStatus)));
    }

    /**
     * 不按门店状态筛选的分页：先分页查商品，再批量查本页商品的门店在售状态并逐条回填
     */
    private ApiResponse<IPage<ProductDetailResponse>> getProductPageWithStoreStatus(
            StoreProductPageRequest request, Long storeId,
            LambdaQueryWrapper<ProductEntity> productQuery) {

        IPage<ProductEntity> productPage = productMapper.selectPage(
                new Page<>(request.getCurrent(), request.getSize()), productQuery);

        List<ProductEntity> products = productPage.getRecords();
        if (products.isEmpty()) {
            return ApiResponse.success(productPage.convert(
                    entity -> productConverter.toProductDetailResponseWithStoreStatus(entity, StoreProductStatus.DISABLED.getValue())));
        }

        // 批量查本页商品在当前门店的在售记录，IN 范围限定在本页，避免全表扫描
        List<Long> productIds = products.stream().map(ProductEntity::getId).toList();
        Set<Long> onSaleIds = storeProductRelMapper.selectList(
                new LambdaQueryWrapper<StoreProductStatusEntity>()
                        .eq(StoreProductStatusEntity::getStoreId, storeId)
                        .in(StoreProductStatusEntity::getProductId, productIds))
                .stream()
                .map(StoreProductStatusEntity::getProductId)
                .collect(Collectors.toSet());

        // 逐条判断每个商品是否在在售集合中，回填门店状态
        return ApiResponse.success(productPage.convert(entity -> {
            int storeStatus = onSaleIds.contains(entity.getId())
                    ? StoreProductStatus.ENABLED.getValue()
                    : StoreProductStatus.DISABLED.getValue();
            return productConverter.toProductDetailResponseWithStoreStatus(entity, storeStatus);
        }));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> updateStatus(Long productId, UpdateStoreProductStatusRequest request) {
        ProductEntity product = productMapper.selectById(productId);
        if (product == null) {
            return fail(ProductErrorCode.PRODUCT_NOT_FOUND);
        }

        Long storeId = request.getStoreId();
        LambdaQueryWrapper<StoreProductStatusEntity> relQuery = new LambdaQueryWrapper<StoreProductStatusEntity>()
                .eq(StoreProductStatusEntity::getStoreId, storeId)
                .eq(StoreProductStatusEntity::getProductId, productId);

        if (StoreProductStatus.ENABLED.getValue() == request.getStatus()) {
            if (!storeProductRelMapper.exists(relQuery)) {
                StoreProductStatusEntity rel = StoreProductStatusEntity.builder()
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

        // 删除缓存
        cacheEvictionService.evictProductBizDetail(productId, storeId);

        return ApiResponse.success();
    }

    @Override
    @Cacheable(
            cacheNames = CacheNames.PRODUCT_BIZ_DETAIL,
            key = "'productId:' + #productId + ':storeId:' + #storeId",
            unless = "#result.code != 0"
    )
    public ApiResponse<ProductBizDetailResponse> getProductDetail(Long productId, Long storeId) {
        // 查询商品基本信息并校验全局状态
        ProductEntity product = productMapper.selectById(productId);
        if (product == null) {
            return fail(ProductErrorCode.PRODUCT_NOT_FOUND);
        }
        if (ProductStatus.DISABLED.getValue() == product.getStatus()) {
            return fail(ProductErrorCode.PRODUCT_DISABLED);
        }

        // 查询商品在当前门店的在售状态（store_product_rel 存在记录即为在售）
        boolean onSale = storeProductRelMapper.exists(new LambdaQueryWrapper<StoreProductStatusEntity>()
                .eq(StoreProductStatusEntity::getStoreId, storeId)
                .eq(StoreProductStatusEntity::getProductId, productId));
        int storeProductStatus = onSale
                ? StoreProductStatus.ENABLED.getValue()
                : StoreProductStatus.DISABLED.getValue();

        // 查询商品绑定的客制化项目，按 sortOrder 升序保证展示顺序
        List<ProductCustomizationItemBindingEntity> bindings = bindingMapper.selectList(
                new LambdaQueryWrapper<ProductCustomizationItemBindingEntity>()
                        .eq(ProductCustomizationItemBindingEntity::getProductId, productId)
                        .orderByAsc(ProductCustomizationItemBindingEntity::getSortOrder));

        // 没有客制化项目，直接返回
        if (bindings.isEmpty()) {
            return ApiResponse.success(productConverter.toProductBizDetailResponse(product, storeProductStatus, List.of()));
        }

        // 过滤出全局状态为启用的客制化项目，排除已停用项
        List<Long> boundItemIds = bindings.stream()
                .map(ProductCustomizationItemBindingEntity::getItemId)
                .toList();
        List<CustomizationItemEntity> activeItems = customizationItemMapper.selectList(
                new LambdaQueryWrapper<CustomizationItemEntity>()
                        .in(CustomizationItemEntity::getId, boundItemIds)
                        .eq(CustomizationItemEntity::getStatus, CustomizationStatus.ACTIVE.getValue()));

        // 没有可用的客制化项目，直接返回
        if (activeItems.isEmpty()) {
            return ApiResponse.success(productConverter.toProductBizDetailResponse(product, storeProductStatus, List.of()));
        }

        List<Long> activeItemIds = activeItems.stream()
                .map(CustomizationItemEntity::getId)
                .toList();

        // 批量查询客制化项目在当前门店的在售状态，IN 范围限定在启用项目集合内
        Set<Long> onSaleItemIds = storeItemRelMapper.selectList(
                new LambdaQueryWrapper<StoreCustomizationItemStatusEntity>()
                        .eq(StoreCustomizationItemStatusEntity::getStoreId, storeId)
                        .in(StoreCustomizationItemStatusEntity::getItemId, activeItemIds))
                .stream()
                .map(StoreCustomizationItemStatusEntity::getItemId)
                .collect(Collectors.toSet());

        // 6. 查询所有启用的客制化选项，范围限定在启用项目集合内
        List<CustomizationOptionEntity> activeOptions = customizationOptionMapper.selectList(
                new LambdaQueryWrapper<CustomizationOptionEntity>()
                        .in(CustomizationOptionEntity::getItemId, activeItemIds)
                        .eq(CustomizationOptionEntity::getStatus, CustomizationStatus.ACTIVE.getValue())
                        .orderByAsc(CustomizationOptionEntity::getId));

        // 7. 批量查询客制化选项在当前门店的在售状态；选项为空时跳过查询
        List<Long> activeOptionIds = activeOptions.stream()
                .map(CustomizationOptionEntity::getId)
                .toList();
        final Set<Long> onSaleOptionIds = activeOptionIds.isEmpty() ? Set.of() :
                storeOptionRelMapper.selectList(
                        new LambdaQueryWrapper<StoreCustomizationOptionStatusEntity>()
                                .eq(StoreCustomizationOptionStatusEntity::getStoreId, storeId)
                                .in(StoreCustomizationOptionStatusEntity::getOptionId, activeOptionIds))
                        .stream()
                        .map(StoreCustomizationOptionStatusEntity::getOptionId)
                        .collect(Collectors.toSet());

        // 8. 将选项按所属项目分组，并填充门店在售状态
        Map<Long, List<CustomizationOptionBizDetailResponse>> optionsByItemId = activeOptions.stream()
                .collect(Collectors.groupingBy(
                        CustomizationOptionEntity::getItemId,
                        Collectors.mapping(option -> {
                            int optionStoreStatus = onSaleOptionIds.contains(option.getId())
                                    ? StoreCustomizationSaleStatus.ENABLED.getValue()
                                    : StoreCustomizationSaleStatus.DISABLED.getValue();
                            return customizationConverter.toOptionBizDetailResponse(option, optionStoreStatus);
                        }, Collectors.toList())));

        // 9. 按绑定排序遍历，组装客制化项目列表并构建最终响应
        Map<Long, CustomizationItemEntity> itemById = activeItems.stream()
                .collect(Collectors.toMap(CustomizationItemEntity::getId, e -> e));
        List<CustomizationItemBizDetailResponse> itemResponses = new ArrayList<>();
        for (ProductCustomizationItemBindingEntity binding : bindings) {
            CustomizationItemEntity item = itemById.get(binding.getItemId());
            if (item == null) {
                // 绑定项目已被停用，跳过
                continue;
            }
            int itemStoreStatus = onSaleItemIds.contains(item.getId())
                    ? StoreCustomizationSaleStatus.ENABLED.getValue()
                    : StoreCustomizationSaleStatus.DISABLED.getValue();
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
