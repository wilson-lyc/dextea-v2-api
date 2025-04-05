package cn.dextea.common.model.menu;

import cn.dextea.common.code.ProductStatus;
import cn.dextea.common.model.product.CustomizeItemModel;
import cn.dextea.common.model.product.ProductModel;
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
@AllArgsConstructor
@NoArgsConstructor
public class MenuProductModel{
    // 基础信息
    public Long id;// 商品ID
    public String name;// 商品名称
    public String description;// 简介
    public BigDecimal price;// 价格

    // 图册
    public String cover;// 封面

    // 状态
    private Integer globalStatus;// 全局状态
    private Integer storeStatus;// 门店状态
    private Integer status;// 状态
    private String statusText;

    // 排序
    private Integer sort;

    // 时间
    private String createTime;// 创建时间
    private String updateTime;// 更新时间

    public Integer getStatus() {
        return ProductStatus.getStatus(globalStatus, storeStatus);
    }

    public String getStatusText() {
        return ProductStatus.fromValue(getStatus()).getLabel();
    }
}
