package cn.dextea.product.converter;

import cn.dextea.product.dto.response.CreateCustomizationItemResponse;
import cn.dextea.product.dto.response.CreateCustomizationOptionResponse;
import cn.dextea.product.dto.response.CustomizationItemDetailResponse;
import cn.dextea.product.dto.response.CustomizationItemWithStoreStatusResponse;
import cn.dextea.product.dto.response.CustomizationOptionDetailResponse;
import cn.dextea.product.dto.response.CustomizationOptionWithStoreStatusResponse;
import cn.dextea.product.entity.CustomizationItemEntity;
import cn.dextea.product.entity.CustomizationOptionEntity;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public CustomizationItemDetailResponse toItemDetailResponse(CustomizationItemEntity entity,
            List<CustomizationOptionDetailResponse> options) {
        return CustomizationItemDetailResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .options(options)
                .build();
    }

    public CustomizationItemDetailResponse toItemDetailResponse(CustomizationItemEntity entity) {
        return toItemDetailResponse(entity, null);
    }

    public CustomizationItemWithStoreStatusResponse toItemWithStoreStatusResponse(CustomizationItemEntity entity,
            int storeStatus) {
        return CustomizationItemWithStoreStatusResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
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

    public CustomizationOptionDetailResponse toOptionDetailResponse(CustomizationOptionEntity entity) {
        return CustomizationOptionDetailResponse.builder()
                .id(entity.getId())
                .itemId(entity.getItemId())
                .name(entity.getName())
                .price(entity.getPrice())
                .ingredientId(entity.getIngredientId())
                .ingredientQuantity(entity.getIngredientQuantity())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }

    public CustomizationOptionWithStoreStatusResponse toOptionWithStoreStatusResponse(CustomizationOptionEntity entity,
            int storeStatus) {
        return CustomizationOptionWithStoreStatusResponse.builder()
                .id(entity.getId())
                .itemId(entity.getItemId())
                .name(entity.getName())
                .price(entity.getPrice())
                .ingredientId(entity.getIngredientId())
                .ingredientQuantity(entity.getIngredientQuantity())
                .storeStatus(storeStatus)
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}
