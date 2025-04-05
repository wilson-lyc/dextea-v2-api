package cn.dextea.common.model.product;

import cn.dextea.common.code.ProductStatus;
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
public class ProductModel {
    // 基础信息
    private Long id;// 商品ID
    private String name;// 商品名称
    private String description;// 简介
    private BigDecimal price;// 价格
    private Long categoryId;// 分类ID
    private String categoryText;// 分类名称

    // 图册
    private String cover;// 封面
    private String detailHeaderImg;// 详情页头图

    // 状态
    private Integer globalStatus;// 全局状态
    private Integer storeStatus;// 门店状态
    private Integer status;// 状态
    private String statusText;

    // 客制化项目
    List<CustomizeItemModel> customize;

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
