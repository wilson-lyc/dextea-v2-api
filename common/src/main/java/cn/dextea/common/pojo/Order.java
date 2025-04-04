package cn.dextea.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

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
    @TableId(type = IdType.INPUT)
    private String id;
    private Long storeId;// 门店ID
    private String storeName;// 门店名称
    private Long customerId;// 顾客ID
    private Integer dineMode;// 用餐方式
    private String tableNo;// 餐桌号
    private String pickUpNo;// 取餐号
    private Integer totalCount;// 商品数量
    private BigDecimal totalPrice;// 订单总价
    private String tradeNo;// 交易编号
    private Integer status;// 状态
    private String createTime;// 创建时间
    private String payTime;// 支付时间
    private String payExpireTime;// 支付过期时间
    private String updateTime;// 更新时间
}
