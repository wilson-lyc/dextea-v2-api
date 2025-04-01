package cn.dextea.order.pojo;

import cn.dextea.order.model.ProductCustomizeModel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
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
@TableName("r_order_product")
public class OrderProduct {
    private Long id;
    @TableField(value = "customize",typeHandler = Fastjson2TypeHandler.class)
    private List<ProductCustomizeModel> customize;
    private Integer count;
    private BigDecimal price;
    private String skuId;
    private String orderId;
}
