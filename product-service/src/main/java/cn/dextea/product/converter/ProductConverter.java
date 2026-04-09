package cn.dextea.product.converter;

import cn.dextea.product.dto.response.CreateProductResponse;
import cn.dextea.product.dto.response.CustomizationItemBizDetailResponse;
import cn.dextea.product.dto.response.ProductBizDetailResponse;
import cn.dextea.product.dto.response.ProductDetailResponse;
import cn.dextea.product.entity.ProductEntity;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public ProductDetailResponse toProductDetailResponseWithStoreStatus(ProductEntity entity, int storeStatus) {
        return ProductDetailResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .status(storeStatus)
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }

    public ProductBizDetailResponse toProductBizDetailResponse(ProductEntity entity, int storeStatus,
            List<CustomizationItemBizDetailResponse> items) {
        return ProductBizDetailResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .storeStatus(storeStatus)
                .items(items)
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
