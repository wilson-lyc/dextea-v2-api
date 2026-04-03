package cn.dextea.product.converter;

import cn.dextea.product.dto.response.StoreProductStatusDetailResponse;
import cn.dextea.product.entity.StoreProductStatusEntity;
import org.springframework.stereotype.Component;

@Component
public class StoreProductStatusConverter {

    public StoreProductStatusDetailResponse toDetailResponse(StoreProductStatusEntity entity) {
        return StoreProductStatusDetailResponse.builder()
                .storeId(entity.getStoreId())
                .productId(entity.getProductId())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}
