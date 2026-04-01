package cn.dextea.product.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.IngredientConverter;
import cn.dextea.product.dto.request.BindProductIngredientRequest;
import cn.dextea.product.dto.response.ProductIngredientDetailResponse;
import cn.dextea.product.entity.IngredientEntity;
import cn.dextea.product.entity.ProductEntity;
import cn.dextea.product.entity.ProductIngredientEntity;
import cn.dextea.product.enums.IngredientErrorCode;
import cn.dextea.product.enums.IngredientStatus;
import cn.dextea.product.enums.ProductErrorCode;
import cn.dextea.product.mapper.IngredientMapper;
import cn.dextea.product.mapper.ProductIngredientMapper;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.service.ProductIngredientAdminService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductIngredientAdminServiceImpl implements ProductIngredientAdminService {

    private final ProductMapper productMapper;
    private final IngredientMapper ingredientMapper;
    private final ProductIngredientMapper productIngredientMapper;
    private final IngredientConverter ingredientConverter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> bindIngredient(Long productId, BindProductIngredientRequest request) {
        ProductEntity product = productMapper.selectById(productId);
        if (product == null) {
            return ApiResponse.fail(ProductErrorCode.PRODUCT_NOT_FOUND.getCode(),
                    ProductErrorCode.PRODUCT_NOT_FOUND.getMsg());
        }

        IngredientEntity ingredient = getActiveIngredientById(request.getIngredientId());
        if (ingredient == null) {
            return fail(IngredientErrorCode.INGREDIENT_NOT_FOUND);
        }

        if (bindingExists(productId, request.getIngredientId())) {
            return fail(IngredientErrorCode.INGREDIENT_ALREADY_BOUND);
        }

        ProductIngredientEntity binding = ProductIngredientEntity.builder()
                .productId(productId)
                .ingredientId(request.getIngredientId())
                .quantity(request.getQuantity())
                .unit(request.getUnit().trim())
                .build();

        productIngredientMapper.insert(binding);
        return ApiResponse.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> unbindIngredient(Long productId, Long ingredientId) {
        ProductEntity product = productMapper.selectById(productId);
        if (product == null) {
            return ApiResponse.fail(ProductErrorCode.PRODUCT_NOT_FOUND.getCode(),
                    ProductErrorCode.PRODUCT_NOT_FOUND.getMsg());
        }

        LambdaQueryWrapper<ProductIngredientEntity> queryWrapper = new LambdaQueryWrapper<ProductIngredientEntity>()
                .eq(ProductIngredientEntity::getProductId, productId)
                .eq(ProductIngredientEntity::getIngredientId, ingredientId);

        if (!productIngredientMapper.exists(queryWrapper)) {
            return fail(IngredientErrorCode.INGREDIENT_NOT_BOUND);
        }

        productIngredientMapper.delete(queryWrapper);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<List<ProductIngredientDetailResponse>> getProductIngredients(Long productId) {
        ProductEntity product = productMapper.selectById(productId);
        if (product == null) {
            return ApiResponse.fail(ProductErrorCode.PRODUCT_NOT_FOUND.getCode(),
                    ProductErrorCode.PRODUCT_NOT_FOUND.getMsg());
        }

        List<ProductIngredientEntity> bindings = productIngredientMapper.selectList(
                new LambdaQueryWrapper<ProductIngredientEntity>()
                        .eq(ProductIngredientEntity::getProductId, productId)
                        .orderByAsc(ProductIngredientEntity::getId));

        if (bindings.isEmpty()) {
            return ApiResponse.success(List.of());
        }

        List<Long> ingredientIds = bindings.stream()
                .map(ProductIngredientEntity::getIngredientId)
                .collect(Collectors.toList());

        Map<Long, IngredientEntity> ingredientMap = ingredientMapper.selectBatchIds(ingredientIds)
                .stream()
                .collect(Collectors.toMap(IngredientEntity::getId, Function.identity()));

        List<ProductIngredientDetailResponse> result = bindings.stream()
                .filter(b -> ingredientMap.containsKey(b.getIngredientId()))
                .map(b -> ingredientConverter.toProductIngredientDetailResponse(
                        b, ingredientMap.get(b.getIngredientId())))
                .collect(Collectors.toList());

        return ApiResponse.success(result);
    }

    // ---- Helpers ----

    private IngredientEntity getActiveIngredientById(Long id) {
        LambdaQueryWrapper<IngredientEntity> queryWrapper = new LambdaQueryWrapper<IngredientEntity>()
                .eq(IngredientEntity::getId, id)
                .eq(IngredientEntity::getStatus, IngredientStatus.ACTIVE.getValue());
        return ingredientMapper.selectOne(queryWrapper);
    }

    private boolean bindingExists(Long productId, Long ingredientId) {
        LambdaQueryWrapper<ProductIngredientEntity> queryWrapper = new LambdaQueryWrapper<ProductIngredientEntity>()
                .eq(ProductIngredientEntity::getProductId, productId)
                .eq(ProductIngredientEntity::getIngredientId, ingredientId);
        return productIngredientMapper.exists(queryWrapper);
    }

    private <T> ApiResponse<T> fail(IngredientErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}
