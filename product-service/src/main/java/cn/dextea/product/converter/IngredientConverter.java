package cn.dextea.product.converter;

import cn.dextea.product.dto.response.CreateIngredientResponse;
import cn.dextea.product.dto.response.IngredientDetailResponse;
import cn.dextea.product.dto.response.ProductIngredientDetailResponse;
import cn.dextea.product.entity.IngredientEntity;
import cn.dextea.product.entity.ProductIngredientBindingEntity;
import org.springframework.stereotype.Component;

@Component
public class IngredientConverter {

    public CreateIngredientResponse toCreateIngredientResponse(IngredientEntity entity) {
        return CreateIngredientResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .unit(entity.getUnit())
                .storageDuration(entity.getStorageDuration())
                .storageDurationUnit(entity.getStorageDurationUnit())
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
                .storageDuration(entity.getStorageDuration())
                .storageDurationUnit(entity.getStorageDurationUnit())
                .storageMethod(entity.getStorageMethod())
                .preparedExpiry(entity.getPreparedExpiry())
                .preparedExpiryUnit(entity.getPreparedExpiryUnit())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }

    public ProductIngredientDetailResponse toProductIngredientDetailResponse(
            ProductIngredientBindingEntity binding, IngredientEntity ingredient) {
        return ProductIngredientDetailResponse.builder()
                .ingredientId(ingredient.getId())
                .ingredientName(ingredient.getName())
                .unit(ingredient.getUnit())
                .storageDuration(ingredient.getStorageDuration())
                .storageDurationUnit(ingredient.getStorageDurationUnit())
                .storageMethod(ingredient.getStorageMethod())
                .quantity(binding.getQuantity())
                .createTime(binding.getCreateTime())
                .updateTime(binding.getUpdateTime())
                .build();
    }
}
