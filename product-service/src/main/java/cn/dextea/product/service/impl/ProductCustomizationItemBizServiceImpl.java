package cn.dextea.product.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.response.StoreProductCustomizationItemDetailResponse;
import cn.dextea.product.entity.CustomizationItemEntity;
import cn.dextea.product.entity.ProductCustomizationItemBindingEntity;
import cn.dextea.product.entity.ProductEntity;
import cn.dextea.product.entity.StoreCustomizationItemStatusEntity;
import cn.dextea.product.enums.ProductErrorCode;
import cn.dextea.product.mapper.CustomizationItemMapper;
import cn.dextea.product.mapper.ProductCustomizationItemBindingMapper;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.mapper.StoreCustomizationItemStatusMapper;
import cn.dextea.product.service.ProductCustomizationItemBizService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductCustomizationItemBizServiceImpl implements ProductCustomizationItemBizService {

    private final ProductMapper productMapper;
    private final CustomizationItemMapper customizationItemMapper;
    private final ProductCustomizationItemBindingMapper bindingMapper;
    private final StoreCustomizationItemStatusMapper storeCustomizationItemStatusMapper;

    @Override
    public ApiResponse<List<StoreProductCustomizationItemDetailResponse>> getProductCustomizationItems(Long productId, Long storeId) {
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

        Map<Long, Integer> storeStatusMap = storeCustomizationItemStatusMapper.selectList(
                new LambdaQueryWrapper<StoreCustomizationItemStatusEntity>()
                        .eq(StoreCustomizationItemStatusEntity::getStoreId, storeId)
                        .in(StoreCustomizationItemStatusEntity::getItemId, itemIds))
                .stream()
                .collect(Collectors.toMap(
                        StoreCustomizationItemStatusEntity::getItemId,
                        StoreCustomizationItemStatusEntity::getStatus));

        List<StoreProductCustomizationItemDetailResponse> result = bindings.stream()
                .filter(b -> itemMap.containsKey(b.getItemId()))
                .map(b -> {
                    CustomizationItemEntity item = itemMap.get(b.getItemId());
                    return StoreProductCustomizationItemDetailResponse.builder()
                            .bindingId(b.getId())
                            .itemId(item.getId())
                            .itemName(item.getName())
                            .itemDescription(item.getDescription())
                            .itemStatus(item.getStatus())
                            .storeStatus(storeStatusMap.get(item.getId()))
                            .sortOrder(b.getSortOrder())
                            .createTime(b.getCreateTime())
                            .build();
                })
                .collect(Collectors.toList());

        return ApiResponse.success(result);
    }

    private <T> ApiResponse<T> fail(ProductErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}
