package cn.dextea.order.dto;

import cn.dextea.common.code.DineMode;
import cn.dextea.common.code.OrderStatus;
import cn.dextea.order.pojo.OrderProduct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {
    private String id;// 订单ID
    private Long storeId;// 门店ID
    private String storeName;// 门店名称
    private Long customerId;// 顾客ID
    private Integer dineMode;// 用餐方式
    private String dineModeName;// 用餐方式名称
    private String tableNo;// 餐桌号
    private String pickUpNo;// 取餐号
    private Integer totalCount;// 商品数量
    private BigDecimal totalPrice;// 订单总价
    private String tradeNo;// 交易编号
    private Integer status;// 状态
    private String statusName;// 状态名称
    private String createTime;// 创建时间
    private String payTime;// 支付时间
    private String payExpireTime;// 支付过期时间
    private String updateTime;// 更新时间
    private List<OrderProduct> products;
    public String getDineModeName() {
        return DineMode.fromValue(dineMode).getLabel();
    }

    public Integer getStatus() {
        return OrderStatus.fromValue(status,payExpireTime).getValue();
    }

    public String getStatusName() {
        return OrderStatus.fromValue(this.getStatus()).getLabel();
    }
}
