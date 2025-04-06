package cn.dextea.common.model.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Lai Yongchao
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductModel {
    // 绑定信息
    private String orderId;
    // 基础信息
    private Long id;
    private String name;
    private String cover;
    private Integer count;
    private BigDecimal price;
    // 客制化
    List<OrderCustomizeModel> customize;
    private String skuId;
    // 时间
    private String createTime;
    private String updateTime;
}
