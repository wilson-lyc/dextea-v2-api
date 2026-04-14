package cn.dextea.product.service.support;

import cn.dextea.product.entity.CustomizationOptionEntity;
import cn.dextea.product.entity.StoreCustomizationOptionStatusEntity;
import cn.dextea.product.mapper.StoreCustomizationOptionRelMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomizationOptionStoreStatusSyncSupport {

    private final StoreCustomizationOptionRelMapper storeOptionRelMapper;

    public Map<Long, Integer> buildEffectiveStatusMap(Long storeId, List<CustomizationOptionEntity> options) {
        if (options == null || options.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> optionIds = options.stream().map(CustomizationOptionEntity::getId).toList();
        return storeOptionRelMapper.selectList(
                new LambdaQueryWrapper<StoreCustomizationOptionStatusEntity>()
                        .eq(StoreCustomizationOptionStatusEntity::getStoreId, storeId)
                        .in(StoreCustomizationOptionStatusEntity::getOptionId, optionIds))
                .stream()
                .collect(Collectors.toMap(
                        StoreCustomizationOptionStatusEntity::getOptionId,
                        StoreCustomizationOptionStatusEntity::getStatus,
                        (left, right) -> right));
    }
}
