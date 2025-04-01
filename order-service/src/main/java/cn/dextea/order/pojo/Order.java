package cn.dextea.order.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Lai Yongchao
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("s_order")
public class Order {
    private String id;
    private Long customerId;
    private Long storeId;
    private Integer dineMode;
    private String tableNo;
    private String pickUpNo;
    private Integer totalCount;
    private BigDecimal totalPrice;
    private String payNo;
    private Integer status;
    private String createTime;
    private String updateTime;
}
