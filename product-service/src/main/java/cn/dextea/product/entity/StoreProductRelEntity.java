package cn.dextea.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("store_product_rel")
public class StoreProductRelEntity {

    private Long storeId;

    private Long productId;

    private LocalDateTime createTime;
}
