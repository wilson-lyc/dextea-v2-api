package cn.dextea.product.converter;

import cn.dextea.product.dto.response.CreateProductResponse;
import cn.dextea.product.dto.response.ProductDetailResponse;
import cn.dextea.product.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductConverter {

    public ProductDetailResponse toProductDetailResponse(ProductEntity entity) {
        return ProductDetailResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
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
