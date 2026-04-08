package cn.dextea.product.service.impl;

import cn.dextea.common.util.StringValueUtils;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.ProductConverter;
import cn.dextea.product.dto.request.CreateProductRequest;
import cn.dextea.product.dto.request.ProductPageQueryRequest;
import cn.dextea.product.dto.request.UpdateProductRequest;
import cn.dextea.product.dto.response.CreateProductResponse;
import cn.dextea.product.dto.response.ProductDetailResponse;
import cn.dextea.product.entity.ProductEntity;
import cn.dextea.product.entity.StoreProductRelEntity;
import cn.dextea.product.enums.ProductErrorCode;
import cn.dextea.product.enums.ProductStatus;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.mapper.StoreProductRelMapper;
import cn.dextea.product.service.ProductAdminService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductAdminServiceImpl implements ProductAdminService {

    private final ProductMapper productMapper;
    private final StoreProductRelMapper storeProductRelMapper;
    private final ProductConverter productConverter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<CreateProductResponse> createProduct(CreateProductRequest request) {
        String name = request.getName().trim();

        if (existsByName(name, null)) {
            return fail(ProductErrorCode.NAME_ALREADY_EXISTS);
        }

        ProductEntity entity = ProductEntity.builder()
                .name(name)
                .description(request.getDescription())
                .price(request.getPrice())
                .status(request.getStatus())
                .build();

        if (productMapper.insert(entity) != 1) {
            return fail(ProductErrorCode.CREATE_FAILED);
        }

        return ApiResponse.success(productConverter.toCreateProductResponse(entity));
    }

    @Override
    public ApiResponse<IPage<ProductDetailResponse>> getProductPage(ProductPageQueryRequest request) {
        LambdaQueryWrapper<ProductEntity> queryWrapper = new LambdaQueryWrapper<ProductEntity>()
                .like(StringValueUtils.hasText(request.getName()), ProductEntity::getName, StringValueUtils.trim(request.getName()))
                .eq(request.getStatus() != null, ProductEntity::getStatus, request.getStatus())
                .orderByDesc(ProductEntity::getId);

        IPage<ProductEntity> entityPage = productMapper.selectPage(
                new Page<>(request.getCurrent(), request.getSize()), queryWrapper);
        return ApiResponse.success(entityPage.convert(productConverter::toProductDetailResponse));
    }

    @Override
    public ApiResponse<ProductDetailResponse> getProductDetail(Long id) {
        ProductEntity entity = productMapper.selectById(id);
        if (entity == null) {
            return fail(ProductErrorCode.PRODUCT_NOT_FOUND);
        }
        return ApiResponse.success(productConverter.toProductDetailResponse(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<ProductDetailResponse> updateProduct(Long id, UpdateProductRequest request) {
        ProductEntity entity = productMapper.selectById(id);
        if (entity == null) {
            return fail(ProductErrorCode.PRODUCT_NOT_FOUND);
        }

        String name = request.getName().trim();
        if (existsByName(name, id)) {
            return fail(ProductErrorCode.NAME_ALREADY_EXISTS);
        }

        entity.setName(name);
        entity.setDescription(request.getDescription());
        entity.setPrice(request.getPrice());
        entity.setStatus(request.getStatus());

        if (productMapper.updateById(entity) != 1) {
            return fail(ProductErrorCode.UPDATE_FAILED);
        }

        return ApiResponse.success(productConverter.toProductDetailResponse(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> deleteProduct(Long id) {
        ProductEntity entity = productMapper.selectById(id);
        if (entity == null) {
            return fail(ProductErrorCode.PRODUCT_NOT_FOUND);
        }
        entity.setStatus(ProductStatus.DISABLED.getValue());
        if (productMapper.updateById(entity) != 1) {
            return fail(ProductErrorCode.DELETE_FAILED);
        }

        // 清除该商品在所有门店的在售关联，避免再次上架时门店状态残留
        storeProductRelMapper.delete(new LambdaQueryWrapper<StoreProductRelEntity>()
                .eq(StoreProductRelEntity::getProductId, id));

        return ApiResponse.success();
    }

    private boolean existsByName(String name, Long excludeId) {
        LambdaQueryWrapper<ProductEntity> queryWrapper = new LambdaQueryWrapper<ProductEntity>()
                .eq(ProductEntity::getName, name)
                .ne(excludeId != null, ProductEntity::getId, excludeId);
        return productMapper.exists(queryWrapper);
    }

    private <T> ApiResponse<T> fail(ProductErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}
