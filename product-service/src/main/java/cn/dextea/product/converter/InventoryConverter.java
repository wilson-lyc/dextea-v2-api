package cn.dextea.product.converter;

import cn.dextea.product.dto.response.InventoryDetailResponse;
import cn.dextea.product.entity.IngredientEntity;
import cn.dextea.product.entity.StoreIngredientInventoryEntity;
import org.springframework.stereotype.Component;

@Component
public class InventoryConverter {

    public InventoryDetailResponse toInventoryDetailResponse(
            StoreIngredientInventoryEntity inventory, IngredientEntity ingredient) {
        return InventoryDetailResponse.builder()
                .id(inventory.getId())
                .storeId(inventory.getStoreId())
                .ingredientId(inventory.getIngredientId())
                .ingredientName(ingredient.getName())
                .shelfLife(ingredient.getShelfLife())
                .shelfLifeUnit(ingredient.getShelfLifeUnit())
                .storageMethod(ingredient.getStorageMethod())
                .quantity(inventory.getQuantity())
                .unit(inventory.getUnit())
                .warnThreshold(inventory.getWarnThreshold())
                .lowStock(inventory.getQuantity().compareTo(inventory.getWarnThreshold()) <= 0)
                .lastRestockTime(inventory.getLastRestockTime())
                .createTime(inventory.getCreateTime())
                .updateTime(inventory.getUpdateTime())
                .build();
    }
}
