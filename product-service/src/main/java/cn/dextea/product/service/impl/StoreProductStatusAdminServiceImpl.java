package cn.dextea.product.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.BatchSetStoreProductStatusRequest;
import cn.dextea.product.dto.request.SetStoreProductStatusRequest;
import cn.dextea.product.dto.response.StoreProductStatusDetailResponse;
import cn.dextea.product.entity.ProductEntity;
import cn.dextea.product.entity.StoreProductStatusEntity;
import cn.dextea.product.enums.ProductErrorCode;
import cn.dextea.product.enums.StoreProductSaleStatus;
import cn.dextea.product.enums.StoreProductStatusErrorCode;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.mapper.StoreProductStatusMapper;
import cn.dextea.product.service.StoreProductStatusAdminService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreProductStatusAdminServiceImpl implements StoreProductStatusAdminService {

    private final StoreProductStatusMapper storeProductStatusMapper;
    private final ProductMapper productMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<StoreProductStatusDetailResponse> setStatus(SetStoreProductStatusRequest request) {
        // 校验商品是否存在
        ProductEntity product = productMapper.selectById(request.getProductId());
        if (product == null) {
            return ApiResponse.fail(ProductErrorCode.PRODUCT_NOT_FOUND.getCode(), ProductErrorCode.PRODUCT_NOT_FOUND.getMsg());
        }

        // 查询是否已存在记录
        StoreProductStatusEntity entity = findByStoreAndProduct(request.getStoreId(), request.getProductId());

        if (entity == null) {
            // 新增记录
            entity = StoreProductStatusEntity.builder()
                    .storeId(request.getStoreId())
                    .productId(request.getProductId())
                    .status(request.getStatus())
                    .build();
            if (storeProductStatusMapper.insert(entity) != 1) {
                return ApiResponse.fail(StoreProductStatusErrorCode.CREATE_FAILED.getCode(), StoreProductStatusErrorCode.CREATE_FAILED.getMsg());
            }
        } else {
            // 更新记录
            entity.setStatus(request.getStatus());
            if (storeProductStatusMapper.updateById(entity) != 1) {
                return ApiResponse.fail(StoreProductStatusErrorCode.UPDATE_FAILED.getCode(), StoreProductStatusErrorCode.UPDATE_FAILED.getMsg());
            }
        }

        return ApiResponse.success(toResponse(entity, product.getName()));
    }

    @Override
    public ApiResponse<StoreProductStatusDetailResponse> getStatus(Long storeId, Long productId) {
        StoreProductStatusEntity entity = findByStoreAndProduct(storeId, productId);
        if (entity == null) {
            return ApiResponse.fail(StoreProductStatusErrorCode.RECORD_NOT_FOUND.getCode(), StoreProductStatusErrorCode.RECORD_NOT_FOUND.getMsg());
        }

        ProductEntity product = productMapper.selectById(entity.getProductId());
        String productName = product != null ? product.getName() : null;

        return ApiResponse.success(toResponse(entity, productName));
    }

    @Override
    public ApiResponse<IPage<StoreProductStatusDetailResponse>> getStatusPageByStore(Long storeId, Long productId, Long current, Long size) {
        LambdaQueryWrapper<StoreProductStatusEntity> queryWrapper = new LambdaQueryWrapper<StoreProductStatusEntity>()
                .eq(storeId != null, StoreProductStatusEntity::getStoreId, storeId)
                .eq(productId != null, StoreProductStatusEntity::getProductId, productId)
                .orderByDesc(StoreProductStatusEntity::getUpdateTime);

        IPage<StoreProductStatusEntity> entityPage = storeProductStatusMapper.selectPage(
                new Page<>(current, size), queryWrapper);

        IPage<StoreProductStatusDetailResponse> responsePage = entityPage.convert(entity -> {
            ProductEntity product = productMapper.selectById(entity.getProductId());
            String productName = product != null ? product.getName() : null;
            return toResponse(entity, productName);
        });

        return ApiResponse.success(responsePage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> batchSetStatus(BatchSetStoreProductStatusRequest request) {
        // 校验商品是否存在
        ProductEntity product = productMapper.selectById(request.getProductId());
        if (product == null) {
            return ApiResponse.fail(ProductErrorCode.PRODUCT_NOT_FOUND.getCode(), ProductErrorCode.PRODUCT_NOT_FOUND.getMsg());
        }

        for (BatchSetStoreProductStatusRequest.StoreProductStatusItem item : request.getItems()) {
            StoreProductStatusEntity entity = findByStoreAndProduct(item.getStoreId(), request.getProductId());

            if (entity == null) {
                entity = StoreProductStatusEntity.builder()
                        .storeId(item.getStoreId())
                        .productId(request.getProductId())
                        .status(item.getStatus())
                        .build();
                storeProductStatusMapper.insert(entity);
            } else {
                entity.setStatus(item.getStatus());
                storeProductStatusMapper.updateById(entity);
            }
        }

        return ApiResponse.success();
    }

    private StoreProductStatusEntity findByStoreAndProduct(Long storeId, Long productId) {
        LambdaQueryWrapper<StoreProductStatusEntity> queryWrapper = new LambdaQueryWrapper<StoreProductStatusEntity>()
                .eq(StoreProductStatusEntity::getStoreId, storeId)
                .eq(StoreProductStatusEntity::getProductId, productId);
        return storeProductStatusMapper.selectOne(queryWrapper);
    }

    private StoreProductStatusDetailResponse toResponse(StoreProductStatusEntity entity, String productName) {
        return StoreProductStatusDetailResponse.builder()
                .id(entity.getId())
                .storeId(entity.getStoreId())
                .productId(entity.getProductId())
                .productName(productName)
                .status(entity.getStatus())
                .statusLabel(StoreProductStatusDetailResponse.getStatusLabel(entity.getStatus()))
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}