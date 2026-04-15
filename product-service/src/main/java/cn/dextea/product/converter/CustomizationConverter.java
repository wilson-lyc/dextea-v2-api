package cn.dextea.product.converter;

import cn.dextea.product.dto.response.CreateCustomizationItemResponse;
import cn.dextea.product.dto.response.CreateCustomizationOptionResponse;
import cn.dextea.product.dto.response.CustomizationItemDetailResponse;
import cn.dextea.product.dto.response.OptionDetailResponse;
import cn.dextea.product.entity.CustomizationItemEntity;
import cn.dextea.product.entity.CustomizationOptionEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomizationConverter {

    public CreateCustomizationItemResponse toCreateItemResponse(CustomizationItemEntity entity) {
        return CreateCustomizationItemResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .build();
    }

    public CustomizationItemDetailResponse toItemDetailResponse(CustomizationItemEntity entity) {
        return CustomizationItemDetailResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .globalStatus(entity.getStatus())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }

    public CustomizationItemDetailResponse toItemDetailResponse(CustomizationItemEntity entity, int storeStatus) {
        return CustomizationItemDetailResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .globalStatus(entity.getStatus())
                .storeStatus(storeStatus)
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }

    public CreateCustomizationOptionResponse toCreateOptionResponse(CustomizationOptionEntity entity) {
        return CreateCustomizationOptionResponse.builder()
                .id(entity.getId())
                .itemId(entity.getItemId())
                .name(entity.getName())
                .price(entity.getPrice())
                .ingredientId(entity.getIngredientId())
                .ingredientQuantity(entity.getIngredientQuantity())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .build();
    }

    public OptionDetailResponse toOptionDetailResponse(CustomizationOptionEntity entity) {
        return toOptionDetailResponse(entity, null);
    }

    public OptionDetailResponse toOptionDetailResponse(CustomizationOptionEntity entity, Integer storeStatus) {
        return OptionDetailResponse.builder()
                .id(entity.getId())
                .itemId(entity.getItemId())
                .name(entity.getName())
                .price(entity.getPrice())
                .ingredientId(entity.getIngredientId())
                .ingredientQuantity(entity.getIngredientQuantity())
                .globalStatus(entity.getStatus())
                .storeStatus(storeStatus)
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}
