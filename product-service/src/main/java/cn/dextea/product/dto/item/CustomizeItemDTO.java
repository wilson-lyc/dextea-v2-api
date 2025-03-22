package cn.dextea.product.dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomizeItemDTO {
    private Long id;
    private String name;// 项目名
    private String description;// 简介
    private Integer sort;// 排序
    private Long productId;// 商品ID
    private Integer globalStatus;// 全局状态
    private Integer storeStatus;// 门店状态
}
