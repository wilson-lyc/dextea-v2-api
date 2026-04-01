package cn.dextea.product.converter;

import cn.dextea.product.dto.response.CreateCustomizationItemResponse;
import cn.dextea.product.dto.response.CreateCustomizationOptionResponse;
import cn.dextea.product.dto.response.CustomizationItemDetailResponse;
import cn.dextea.product.dto.response.CustomizationOptionDetailResponse;
import cn.dextea.product.dto.response.OptionIngredientResponse;
import cn.dextea.product.entity.CustomizationOptionIngredientEntity;
import cn.dextea.product.entity.IngredientEntity;
import cn.dextea.product.entity.ProductCustomizationItemEntity;
import cn.dextea.product.entity.ProductCustomizationOptionEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomizationConverter {

    public CreateCustomizationItemResponse toCreateCustomizationItemResponse(ProductCustomizationItemEntity entity) {
        return CreateCustomizationItemResponse.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .name(entity.getName())
                .sortOrder(entity.getSortOrder())
                .isRequired(entity.getIsRequired())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .build();
    }

    public CustomizationItemDetailResponse toCustomizationItemDetailResponse(
            ProductCustomizationItemEntity entity, List<CustomizationOptionDetailResponse> options) {
        return CustomizationItemDetailResponse.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .name(entity.getName())
                .sortOrder(entity.getSortOrder())
                .isRequired(entity.getIsRequired())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .options(options)
                .build();
    }

    public CreateCustomizationOptionResponse toCreateCustomizationOptionResponse(
            ProductCustomizationOptionEntity entity) {
        return CreateCustomizationOptionResponse.builder()
                .id(entity.getId())
                .itemId(entity.getItemId())
                .name(entity.getName())
                .priceAdjustment(entity.getPriceAdjustment())
                .sortOrder(entity.getSortOrder())
                .isDefault(entity.getIsDefault())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .build();
    }

    public CustomizationOptionDetailResponse toCustomizationOptionDetailResponse(
            ProductCustomizationOptionEntity entity, OptionIngredientResponse ingredient) {
        return CustomizationOptionDetailResponse.builder()
                .id(entity.getId())
                .itemId(entity.getItemId())
                .name(entity.getName())
                .priceAdjustment(entity.getPriceAdjustment())
                .sortOrder(entity.getSortOrder())
                .isDefault(entity.getIsDefault())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .ingredient(ingredient)
                .build();
    }

    public OptionIngredientResponse toOptionIngredientResponse(
            CustomizationOptionIngredientEntity binding, IngredientEntity ingredient) {
        return OptionIngredientResponse.builder()
                .ingredientId(ingredient.getId())
                .ingredientName(ingredient.getName())
                .quantity(binding.getQuantity())
                .unit(binding.getUnit())
                .build();
    }
}
