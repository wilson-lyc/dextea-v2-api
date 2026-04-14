package cn.dextea.product.service.support;

import cn.dextea.product.entity.ProductEntity;
import cn.dextea.product.entity.StoreProductStatusEntity;
import cn.dextea.product.enums.StoreProductStatus;
import cn.dextea.product.mapper.StoreProductStatusMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductStoreStatusSyncSupport {

    private final StoreProductStatusMapper storeProductStatusMapper;

    public Map<Long, Integer> buildEffectiveStatusMap(Long storeId, List<ProductEntity> products) {
        if (products == null || products.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> productIds = products.stream().map(ProductEntity::getId).toList();
        return storeProductStatusMapper.selectList(
                new LambdaQueryWrapper<StoreProductStatusEntity>()
                        .eq(StoreProductStatusEntity::getStoreId, storeId)
                        .in(StoreProductStatusEntity::getProductId, productIds))
                .stream()
                .collect(Collectors.toMap(
                        StoreProductStatusEntity::getProductId,
                        StoreProductStatusEntity::getStatus,
                        (left, right) -> right));
    }

    public int resolveEffectiveStatus(Long storeId, ProductEntity product) {
        if (product == null || product.getId() == null) {
            return StoreProductStatus.DISABLED.getValue();
        }
        return buildEffectiveStatusMap(storeId, List.of(product))
                .getOrDefault(product.getId(), StoreProductStatus.DISABLED.getValue());
    }

    public Set<Long> buildEffectiveEnabledProductIds(Long storeId, List<ProductEntity> products) {
        return buildEffectiveStatusMap(storeId, products).entrySet().stream()
                .filter(entry -> Objects.equals(entry.getValue(), StoreProductStatus.ENABLED.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}
