package cn.dextea.common.pojo;

import cn.dextea.common.code.ProductStatus;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("r_product_store_status")
public class ProductStoreStatus {
    private Long storeId;
    private Long productId;
    private ProductStatus status;
    private String createTime;
}
