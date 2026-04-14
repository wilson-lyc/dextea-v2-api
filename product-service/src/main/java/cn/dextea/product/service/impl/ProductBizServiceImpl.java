package cn.dextea.product.service.impl;

import cn.dextea.common.util.StringValueUtils;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.ProductConverter;
import cn.dextea.product.dto.request.StoreProductPageRequest;
import cn.dextea.product.dto.request.UpdateStoreProductStatusRequest;
import cn.dextea.product.dto.response.ProductDetailResponse;
import cn.dextea.product.entity.ProductEntity;
import cn.dextea.product.entity.StoreProductStatusEntity;
import cn.dextea.product.enums.ProductErrorCode;
import cn.dextea.product.enums.StoreProductStatus;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.mapper.StoreProductStatusMapper;
import cn.dextea.product.service.ProductBizService;
import cn.dextea.product.service.support.ProductStoreStatusSyncSupport;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductBizServiceImpl implements ProductBizService {

    private final ProductMapper productMapper;
    private final StoreProductStatusMapper storeProductStatusMapper;
    private final ProductConverter productConverter;
    private final ProductStoreStatusSyncSupport productStoreStatusSyncSupport;

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
            return getPageFilteredByStoreStatus(request, storeId, storeStatus, productQuery);
        }
        return getPageDirectly(request, storeId, productQuery);
    }

    /**
     * 按指定商品门店状态筛选
     */
    private ApiResponse<IPage<ProductDetailResponse>> getPageFilteredByStoreStatus(
            StoreProductPageRequest request, Long storeId, Integer requestedStatus,
            LambdaQueryWrapper<ProductEntity> productQuery) {
        if (Objects.equals(StoreProductStatus.DISABLED.getValue(), requestedStatus)) {
            // 售罄是兜底状态（无记录即售罄），需排除有明确非售罄记录的商品
            List<Long> nonDefaultProductIds = storeProductStatusMapper.selectList(
                    new LambdaQueryWrapper<StoreProductStatusEntity>()
                            .eq(StoreProductStatusEntity::getStoreId, storeId)
                            .ne(StoreProductStatusEntity::getStatus, StoreProductStatus.DISABLED.getValue()))
                    .stream()
                    .map(StoreProductStatusEntity::getProductId)
                    .toList();
            if (!nonDefaultProductIds.isEmpty()) {
                productQuery.notIn(ProductEntity::getId, nonDefaultProductIds);
            }
        } else {
            // 非兜底状态必须有明确的状态记录
            List<Long> matchingProductIds = storeProductStatusMapper.selectList(
                    new LambdaQueryWrapper<StoreProductStatusEntity>()
                            .eq(StoreProductStatusEntity::getStoreId, storeId)
                            .eq(StoreProductStatusEntity::getStatus, requestedStatus))
                    .stream()
                    .map(StoreProductStatusEntity::getProductId)
                    .toList();
            if (matchingProductIds.isEmpty()) {
                return ApiResponse.success(new Page<>(request.getCurrent(), request.getSize()));
            }
            productQuery.in(ProductEntity::getId, matchingProductIds);
        }

        IPage<ProductEntity> productPage = productMapper.selectPage(
                new Page<>(request.getCurrent(), request.getSize()), productQuery);
        return ApiResponse.success(fillStoreStatuses(productPage, storeId));
    }

    /**
     * 不按门店状态筛选的分页
     */
    private ApiResponse<IPage<ProductDetailResponse>> getPageDirectly(
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

        Map<Long, Integer> productStatusMap = productStoreStatusSyncSupport.buildEffectiveStatusMap(storeId, products);

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

    private <T> ApiResponse<T> fail(ProductErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}
