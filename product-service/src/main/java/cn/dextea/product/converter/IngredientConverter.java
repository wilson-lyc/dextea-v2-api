package cn.dextea.product.converter;

import cn.dextea.product.dto.response.CreateIngredientResponse;
import cn.dextea.product.dto.response.IngredientDetailResponse;
import cn.dextea.product.dto.response.ProductIngredientDetailResponse;
import cn.dextea.product.entity.IngredientEntity;
import cn.dextea.product.entity.ProductIngredientEntity;
import org.springframework.stereotype.Component;

@Component
public class IngredientConverter {

    public CreateIngredientResponse toCreateIngredientResponse(IngredientEntity entity) {
        return CreateIngredientResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .unit(entity.getUnit())
                .shelfLife(entity.getShelfLife())
                .shelfLifeUnit(entity.getShelfLifeUnit())
                .storageMethod(entity.getStorageMethod())
                .preparedExpiry(entity.getPreparedExpiry())
                .preparedExpiryUnit(entity.getPreparedExpiryUnit())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .build();
    }

    public IngredientDetailResponse toIngredientDetailResponse(IngredientEntity entity) {
        return IngredientDetailResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .unit(entity.getUnit())
                .shelfLife(entity.getShelfLife())
                .shelfLifeUnit(entity.getShelfLifeUnit())
                .storageMethod(entity.getStorageMethod())
                .preparedExpiry(entity.getPreparedExpiry())
                .preparedExpiryUnit(entity.getPreparedExpiryUnit())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }

    public ProductIngredientDetailResponse toProductIngredientDetailResponse(
            ProductIngredientEntity binding, IngredientEntity ingredient) {
        return ProductIngredientDetailResponse.builder()
                .ingredientId(ingredient.getId())
                .ingredientName(ingredient.getName())
                .unit(ingredient.getUnit())
                .shelfLife(ingredient.getShelfLife())
                .shelfLifeUnit(ingredient.getShelfLifeUnit())
                .storageMethod(ingredient.getStorageMethod())
                .quantity(binding.getQuantity())
                .createTime(binding.getCreateTime())
                .updateTime(binding.getUpdateTime())
                .build();
    }
}
