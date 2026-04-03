package cn.dextea.product.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.ProductConverter;
import cn.dextea.product.converter.StoreProductStatusConverter;
import cn.dextea.product.dto.request.ProductBizPageQueryRequest;
import cn.dextea.product.dto.request.UpdateStoreProductStatusRequest;
import cn.dextea.product.dto.response.ProductBizPageItemResponse;
import cn.dextea.product.dto.response.StoreProductStatusDetailResponse;
import cn.dextea.product.entity.ProductEntity;
import cn.dextea.product.entity.StoreProductStatusEntity;
import cn.dextea.product.enums.ProductErrorCode;
import cn.dextea.product.enums.ProductSaleStatus;
import cn.dextea.product.enums.ProductStatus;
import cn.dextea.product.enums.StoreProductStatusErrorCode;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.mapper.StoreProductStatusMapper;
import cn.dextea.product.service.ProductBizService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductBizServiceImpl implements ProductBizService {

    private final ProductMapper productMapper;
    private final StoreProductStatusMapper storeProductStatusMapper;
    private final ProductConverter productConverter;
    private final StoreProductStatusConverter storeProductStatusConverter;

    @Override
    public ApiResponse<IPage<ProductBizPageItemResponse>> getStoreProductPage(ProductBizPageQueryRequest request) {
        Long storeId = request.getStoreId();

        // 分页查询全局上架商品
        Page<ProductEntity> pageParam = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<ProductEntity> productQuery = new LambdaQueryWrapper<ProductEntity>()
                .eq(ProductEntity::getStatus, ProductStatus.ENABLED.getValue());
        IPage<ProductEntity> productPage = productMapper.selectPage(pageParam, productQuery);

        List<ProductEntity> products = productPage.getRecords();
        if (products.isEmpty()) {
            return ApiResponse.success(productPage.convert(p -> null));
        }

        // 批量查询这批商品在门店的状态
        List<Long> productIds = products.stream().map(ProductEntity::getId).toList();
        LambdaQueryWrapper<StoreProductStatusEntity> statusQuery = new LambdaQueryWrapper<StoreProductStatusEntity>()
                .eq(StoreProductStatusEntity::getStoreId, storeId)
                .in(StoreProductStatusEntity::getProductId, productIds);
        List<StoreProductStatusEntity> storeStatuses = storeProductStatusMapper.selectList(statusQuery);

        Map<Long, Integer> storeStatusMap = storeStatuses.stream()
                .collect(Collectors.toMap(StoreProductStatusEntity::getProductId, StoreProductStatusEntity::getStatus));

        // 转换为响应对象
        IPage<ProductBizPageItemResponse> responsePage = productPage.convert(entity -> {
            Integer storeStatus = storeStatusMap.get(entity.getId());
            ProductSaleStatus saleStatus = ProductSaleStatus.resolve(entity.getStatus(), storeStatus);
            return productConverter.toProductBizPageItemResponse(entity, saleStatus.getValue());
        });

        return ApiResponse.success(responsePage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<StoreProductStatusDetailResponse> updateStoreStatus(Long productId, Long storeId, UpdateStoreProductStatusRequest request) {
        ProductEntity product = productMapper.selectById(productId);
        if (product == null) {
            return fail(ProductErrorCode.PRODUCT_NOT_FOUND);
        }

        LambdaQueryWrapper<StoreProductStatusEntity> query = new LambdaQueryWrapper<StoreProductStatusEntity>()
                .eq(StoreProductStatusEntity::getStoreId, storeId)
                .eq(StoreProductStatusEntity::getProductId, productId);
        StoreProductStatusEntity existing = storeProductStatusMapper.selectOne(query);

        StoreProductStatusEntity entity;
        if (existing != null) {
            LambdaUpdateWrapper<StoreProductStatusEntity> updateWrapper = new LambdaUpdateWrapper<StoreProductStatusEntity>()
                    .eq(StoreProductStatusEntity::getStoreId, storeId)
                    .eq(StoreProductStatusEntity::getProductId, productId)
                    .set(StoreProductStatusEntity::getStatus, request.getStatus());
            if (storeProductStatusMapper.update(null, updateWrapper) != 1) {
                return fail(StoreProductStatusErrorCode.UPDATE_FAILED);
            }
            existing.setStatus(request.getStatus());
            entity = existing;
        } else {
            entity = StoreProductStatusEntity.builder()
                    .storeId(storeId)
                    .productId(productId)
                    .status(request.getStatus())
                    .build();
            if (storeProductStatusMapper.insert(entity) != 1) {
                return fail(StoreProductStatusErrorCode.CREATE_FAILED);
            }
        }

        return ApiResponse.success(storeProductStatusConverter.toDetailResponse(entity));
    }

    private <T> ApiResponse<T> fail(ProductErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }

    private <T> ApiResponse<T> fail(StoreProductStatusErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}
