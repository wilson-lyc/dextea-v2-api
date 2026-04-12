package cn.dextea.product.service.impl;

import cn.dextea.common.util.StringValueUtils;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.ProductConverter;
import cn.dextea.product.dto.request.CreateProductRequest;
import cn.dextea.product.dto.request.ProductPageQueryRequest;
import cn.dextea.product.dto.request.UpdateProductRequest;
import cn.dextea.product.dto.response.CreateProductResponse;
import cn.dextea.product.dto.response.ProductDetailResponse;
import cn.dextea.product.entity.CustomizationOptionEntity;
import cn.dextea.product.entity.ProductCustomizationItemBindingEntity;
import cn.dextea.product.entity.ProductEntity;
import cn.dextea.product.entity.ProductIngredientBindingEntity;
import cn.dextea.product.entity.StoreCustomizationItemStatusEntity;
import cn.dextea.product.entity.StoreCustomizationOptionStatusEntity;
import cn.dextea.product.entity.StoreProductStatusEntity;
import cn.dextea.product.enums.ProductErrorCode;
import cn.dextea.product.enums.ProductStatus;
import cn.dextea.product.mapper.CustomizationOptionMapper;
import cn.dextea.product.mapper.ProductCustomizationItemBindingMapper;
import cn.dextea.product.mapper.ProductIngredientMapper;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.mapper.StoreCustomizationItemRelMapper;
import cn.dextea.product.mapper.StoreCustomizationOptionRelMapper;
import cn.dextea.product.mapper.StoreProductRelMapper;
import cn.dextea.product.service.ProductAdminService;
import cn.dextea.product.service.ProductCacheEvictionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductAdminServiceImpl implements ProductAdminService {

    private final ProductMapper productMapper;
    private final StoreProductRelMapper storeProductRelMapper;
    private final ProductCustomizationItemBindingMapper customizationItemBindingMapper;
    private final ProductIngredientMapper productIngredientMapper;
    private final CustomizationOptionMapper customizationOptionMapper;
    private final StoreCustomizationItemRelMapper storeCustomizationItemRelMapper;
    private final StoreCustomizationOptionRelMapper storeCustomizationOptionRelMapper;
    private final ProductConverter productConverter;
    private final ProductCacheEvictionService cacheEvictionService;

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

        // 商品信息更新，删除缓存
        cacheEvictionService.evictProductBizDetailAll(id);
        cacheEvictionService.evictMenuBizAll();

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

        // 删除商品门店在售状态
        storeProductRelMapper.delete(new LambdaQueryWrapper<StoreProductStatusEntity>()
                .eq(StoreProductStatusEntity::getProductId, id));

        // 收集该商品绑定的客制化项目 ID，用于后续孤立数据清理
        List<Long> boundItemIds = customizationItemBindingMapper.selectList(
                new LambdaQueryWrapper<ProductCustomizationItemBindingEntity>()
                        .eq(ProductCustomizationItemBindingEntity::getProductId, id)
                        .select(ProductCustomizationItemBindingEntity::getItemId))
                .stream()
                .map(ProductCustomizationItemBindingEntity::getItemId)
                .collect(Collectors.toList());

        // 删除商品客制化项目绑定
        customizationItemBindingMapper.delete(new LambdaQueryWrapper<ProductCustomizationItemBindingEntity>()
                .eq(ProductCustomizationItemBindingEntity::getProductId, id));

        // 级联清理孤立的门店客制化在售状态：若某 itemId 不再被任何其他商品引用，则清理其门店状态记录
        if (!boundItemIds.isEmpty()) {
            Set<Long> stillReferencedItemIds = customizationItemBindingMapper.selectList(
                    new LambdaQueryWrapper<ProductCustomizationItemBindingEntity>()
                            .in(ProductCustomizationItemBindingEntity::getItemId, boundItemIds))
                    .stream()
                    .map(ProductCustomizationItemBindingEntity::getItemId)
                    .collect(Collectors.toSet());

            List<Long> orphanItemIds = boundItemIds.stream()
                    .filter(itemId -> !stillReferencedItemIds.contains(itemId))
                    .collect(Collectors.toList());

            if (!orphanItemIds.isEmpty()) {
                // 清理孤立 item 的门店在售状态
                storeCustomizationItemRelMapper.delete(new LambdaQueryWrapper<StoreCustomizationItemStatusEntity>()
                        .in(StoreCustomizationItemStatusEntity::getItemId, orphanItemIds));

                // 清理孤立 item 下所有 option 的门店在售状态
                List<Long> orphanOptionIds = customizationOptionMapper.selectList(
                        new LambdaQueryWrapper<CustomizationOptionEntity>()
                                .in(CustomizationOptionEntity::getItemId, orphanItemIds)
                                .select(CustomizationOptionEntity::getId))
                        .stream()
                        .map(CustomizationOptionEntity::getId)
                        .collect(Collectors.toList());

                if (!orphanOptionIds.isEmpty()) {
                    storeCustomizationOptionRelMapper.delete(new LambdaQueryWrapper<StoreCustomizationOptionStatusEntity>()
                            .in(StoreCustomizationOptionStatusEntity::getOptionId, orphanOptionIds));
                }
            }
        }

        // 删除商品原料绑定
        productIngredientMapper.delete(new LambdaQueryWrapper<ProductIngredientBindingEntity>()
                .eq(ProductIngredientBindingEntity::getProductId, id));

        // 删除缓存
        cacheEvictionService.evictProductBizDetailAll(id);
        cacheEvictionService.evictMenuBizAll();

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
