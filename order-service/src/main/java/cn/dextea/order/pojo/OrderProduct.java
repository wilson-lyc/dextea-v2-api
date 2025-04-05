package cn.dextea.order.pojo;

import cn.dextea.common.pojo.OrderProductCustomize;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Lai Yongchao
 * 订单中的商品信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("r_order_product")
public class OrderProduct {
    private Long id;
    private String name;
    private String cover;
    @TableField(value = "customize",typeHandler = Fastjson2TypeHandler.class)
    private List<OrderProductCustomize> customize;
    private Integer count;
    private BigDecimal price;
    private String skuId;
    private String orderId;
    private String createTime;
    private String updateTime;

    public String getSkuId() {
        return Objects.isNull(customize) ? null :
                this.id+"-"+this.customize.stream()
                .sorted((a, b) -> a.getItemId().compareTo(b.getItemId()))
                .map(c -> c.getItemId() + ":" + c.getOptionId())
                // 用"-"连接所有元素
                .collect(Collectors.joining("-"));
    }
}
