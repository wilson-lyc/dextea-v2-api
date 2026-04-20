package cn.dextea.product.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.BindProductCustomizationItemRequest;
import cn.dextea.product.dto.response.ProductCustomizationItemDetailResponse;
import cn.dextea.product.entity.CustomizationItemEntity;
import cn.dextea.product.entity.ProductCustomizationItemBindingEntity;
import cn.dextea.product.entity.ProductEntity;
import cn.dextea.product.enums.CustomizationErrorCode;
import cn.dextea.product.enums.ProductErrorCode;
import cn.dextea.product.mapper.CustomizationItemMapper;
import cn.dextea.product.mapper.ProductCustomizationItemBindingMapper;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.service.ProductCustomizationItemAdminService;
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
public class ProductCustomizationItemAdminServiceImpl implements ProductCustomizationItemAdminService {

    private final ProductMapper productMapper;
    private final CustomizationItemMapper customizationItemMapper;
    private final ProductCustomizationItemBindingMapper bindingMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> bindCustomizationItem(Long productId, BindProductCustomizationItemRequest request) {
        ProductEntity product = productMapper.selectById(productId);
        if (product == null) {
            return fail(ProductErrorCode.PRODUCT_NOT_FOUND);
        }

        CustomizationItemEntity item = customizationItemMapper.selectById(request.getItemId());
        if (item == null) {
            return fail(CustomizationErrorCode.ITEM_NOT_FOUND);
        }

        if (bindingExists(productId, request.getItemId())) {
            return fail(CustomizationErrorCode.ITEM_ALREADY_BOUND);
        }

        ProductCustomizationItemBindingEntity binding = ProductCustomizationItemBindingEntity.builder()
                .productId(productId)
                .itemId(request.getItemId())
                .sortOrder(request.getSortOrder())
                .build();

        if (bindingMapper.insert(binding) != 1) {
            return fail(CustomizationErrorCode.ITEM_BIND_FAILED);
        }
        return ApiResponse.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> unbindCustomizationItem(Long productId, Long itemId) {
        ProductEntity product = productMapper.selectById(productId);
        if (product == null) {
            return fail(ProductErrorCode.PRODUCT_NOT_FOUND);
        }

        LambdaQueryWrapper<ProductCustomizationItemBindingEntity> queryWrapper =
                new LambdaQueryWrapper<ProductCustomizationItemBindingEntity>()
                        .eq(ProductCustomizationItemBindingEntity::getProductId, productId)
                        .eq(ProductCustomizationItemBindingEntity::getItemId, itemId);

        if (!bindingMapper.exists(queryWrapper)) {
            return fail(CustomizationErrorCode.ITEM_NOT_BOUND);
        }

        bindingMapper.delete(queryWrapper);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<List<ProductCustomizationItemDetailResponse>> getProductCustomizationItems(Long productId) {
        ProductEntity product = productMapper.selectById(productId);
        if (product == null) {
            return fail(ProductErrorCode.PRODUCT_NOT_FOUND);
        }

        List<ProductCustomizationItemBindingEntity> bindings = bindingMapper.selectList(
                new LambdaQueryWrapper<ProductCustomizationItemBindingEntity>()
                        .eq(ProductCustomizationItemBindingEntity::getProductId, productId)
                        .orderByAsc(ProductCustomizationItemBindingEntity::getSortOrder)
                        .orderByAsc(ProductCustomizationItemBindingEntity::getCreateTime));

        if (bindings.isEmpty()) {
            return ApiResponse.success(List.of());
        }

        List<Long> itemIds = bindings.stream()
                .map(ProductCustomizationItemBindingEntity::getItemId)
                .collect(Collectors.toList());

        Map<Long, CustomizationItemEntity> itemMap = customizationItemMapper.selectBatchIds(itemIds)
                .stream()
                .collect(Collectors.toMap(CustomizationItemEntity::getId, Function.identity()));

        List<ProductCustomizationItemDetailResponse> result = bindings.stream()
                .filter(b -> itemMap.containsKey(b.getItemId()))
                .map(b -> {
                    CustomizationItemEntity item = itemMap.get(b.getItemId());
                    return ProductCustomizationItemDetailResponse.builder()
                            .bindingId(b.getId())
                            .itemId(item.getId())
                            .itemName(item.getName())
                            .itemDescription(item.getDescription())
                            .itemStatus(item.getStatus())
                            .sortOrder(b.getSortOrder())
                            .createTime(b.getCreateTime())
                            .build();
                })
                .collect(Collectors.toList());

        return ApiResponse.success(result);
    }

    private boolean bindingExists(Long productId, Long itemId) {
        return bindingMapper.exists(
                new LambdaQueryWrapper<ProductCustomizationItemBindingEntity>()
                        .eq(ProductCustomizationItemBindingEntity::getProductId, productId)
                        .eq(ProductCustomizationItemBindingEntity::getItemId, itemId));
    }

    private <T> ApiResponse<T> fail(ProductErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }

    private <T> ApiResponse<T> fail(CustomizationErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}
