package cn.dextea.product.converter;

import cn.dextea.product.dto.response.InventoryDetailResponse;
import cn.dextea.product.entity.IngredientEntity;
import cn.dextea.product.entity.StoreIngredientInventoryEntity;
import org.springframework.stereotype.Component;

@Component
public class InventoryConverter {

    public InventoryDetailResponse toInventoryDetailResponse(
            StoreIngredientInventoryEntity inventory, IngredientEntity ingredient) {
        InventoryDetailResponse.InventoryDetailResponseBuilder builder = InventoryDetailResponse.builder()
                .storeId(inventory.getStoreId())
                .ingredientId(inventory.getIngredientId())
                .quantity(inventory.getQuantity())
                .unit(inventory.getUnit())
                .warnThreshold(inventory.getWarnThreshold())
                .lowStock(inventory.getWarnThreshold() != null
                        && inventory.getQuantity().compareTo(inventory.getWarnThreshold()) <= 0)
                .lastRestockTime(inventory.getLastRestockTime())
                .createTime(inventory.getCreateTime())
                .updateTime(inventory.getUpdateTime());

        if (ingredient != null) {
            builder.ingredientName(ingredient.getName())
                    .storageDuration(ingredient.getStorageDuration())
                    .storageDurationUnit(ingredient.getStorageDurationUnit())
                    .storageMethod(ingredient.getStorageMethod());
        }

        return builder.build();
    }
}
