package cn.dextea.product.converter;

import cn.dextea.product.dto.response.CreateProductResponse;
import cn.dextea.product.dto.response.CustomerProductDetailResponse;
import cn.dextea.product.dto.response.CustomizationItemBizDetailResponse;
import cn.dextea.product.dto.response.ProductDetailResponse;
import cn.dextea.product.entity.ProductEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductConverter {

    public ProductDetailResponse toProductDetailResponse(ProductEntity entity) {
        return toProductDetailResponse(entity,null);
    }

    public ProductDetailResponse toProductDetailResponse(ProductEntity entity, Integer status) {
        return ProductDetailResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .globalStatus(entity.getStatus())
                .storeStatus(status)
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }

    public CustomerProductDetailResponse toCustomerProductDetailResponse(
            ProductEntity entity, Integer storeStatus,
            List<CustomizationItemBizDetailResponse> customizationItems) {
        return CustomerProductDetailResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .globalStatus(entity.getStatus())
                .storeStatus(storeStatus)
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .customizationItems(customizationItems)
                .build();
    }

    public CreateProductResponse toCreateProductResponse(ProductEntity entity) {
        return CreateProductResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .build();
    }
}
