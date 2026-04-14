package cn.dextea.product.service.impl;

import cn.dextea.common.util.StringValueUtils;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.ProductConverter;
import cn.dextea.product.dto.request.CreateProductRequest;
import cn.dextea.product.dto.request.ProductPageRequest;
import cn.dextea.product.dto.request.UpdateProductInfoRequest;
import cn.dextea.product.dto.request.UpdateProductGlobalStatusRequest;
import cn.dextea.product.dto.response.CreateProductResponse;
import cn.dextea.product.dto.response.ProductDetailResponse;
import cn.dextea.product.entity.ProductEntity;
import cn.dextea.product.enums.ProductErrorCode;
import cn.dextea.product.enums.ProductStatus;
import cn.dextea.product.mapper.ProductMapper;
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
    private final ProductConverter productConverter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<CreateProductResponse> create(CreateProductRequest request) {
        String name = request.getName().trim();

        // 名字已存在，不允许创建
        if (existsByName(name, null)) {
            return fail(ProductErrorCode.NAME_ALREADY_EXISTS);
        }

        ProductEntity entity = ProductEntity.builder()
                .name(name)
                .description(request.getDescription())
                .price(request.getPrice())
                .status(ProductStatus.DISABLED.getValue())
                .build();

        if (productMapper.insert(entity) != 1) {
            return fail(ProductErrorCode.CREATE_FAILED);
        }

        return ApiResponse.success(productConverter.toCreateProductResponse(entity));
    }

    @Override
    public ApiResponse<IPage<ProductDetailResponse>> getPage(ProductPageRequest request) {
        LambdaQueryWrapper<ProductEntity> queryWrapper = new LambdaQueryWrapper<ProductEntity>()
                .like(StringValueUtils.hasText(request.getName()), ProductEntity::getName, StringValueUtils.trim(request.getName()))
                .eq(request.getStatus() != null, ProductEntity::getStatus, request.getStatus())
                .orderByDesc(ProductEntity::getId);

        IPage<ProductEntity> entityPage = productMapper.selectPage(
                new Page<>(request.getCurrent(), request.getSize()), queryWrapper);
        return ApiResponse.success(entityPage.convert(productConverter::toProductDetailResponse));
    }

    @Override
    public ApiResponse<ProductDetailResponse> getDetailById(Long id) {
        ProductEntity entity = productMapper.selectById(id);
        if (entity == null) {
            return fail(ProductErrorCode.PRODUCT_NOT_FOUND);
        }
        return ApiResponse.success(productConverter.toProductDetailResponse(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<ProductDetailResponse> updateInfo(Long id, UpdateProductInfoRequest request) {
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

        if (productMapper.updateById(entity) != 1) {
            return fail(ProductErrorCode.UPDATE_FAILED);
        }

        return ApiResponse.success(productConverter.toProductDetailResponse(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> updateStatus(Long id, UpdateProductGlobalStatusRequest request) {
        ProductEntity entity = productMapper.selectById(id);
        if (entity == null) {
            return fail(ProductErrorCode.PRODUCT_NOT_FOUND);
        }

        entity.setStatus(request.getStatus());
        if (productMapper.updateById(entity) != 1) {
            return fail(ProductErrorCode.UPDATE_FAILED);
        }

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
