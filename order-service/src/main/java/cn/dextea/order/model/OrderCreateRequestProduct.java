package cn.dextea.order.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateRequestProduct {
    @NotNull(message = "缺少商品ID")
    private Long id;
    @NotNull(message = "缺少商品名称")
    private String name;
    @NotBlank(message = "缺少封面图")
    private String cover;
    @Valid @NotNull(message = "缺少客制化信息")
    private List<OrderCreateRequestCustomize> customize;
    @NotNull(message = "缺少商品购买数量")
    private Integer count;
}
