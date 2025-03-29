package cn.dextea.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private String skuId; // 购物车唯一标识
    private Long id;// 商品ID
    private List<CustomizeSelected> customize;// 客制化
    private Integer count;// 购买数量
}
