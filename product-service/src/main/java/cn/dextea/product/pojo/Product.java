package cn.dextea.product.pojo;

import cn.dextea.common.code.ProductStatus;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
@AllArgsConstructor
@NoArgsConstructor
@TableName("s_product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;//商品ID
    private String name;//商品名称
    private String description;//简介
    private BigDecimal price;//价格
    private Long categoryId;//分类ID
    private String cover;//封面
    private String detailHeaderImg;//详情页头图
    private Integer globalStatus;// 全局状态
    private String createTime;//创建时间
    private String updateTime;//更新时间
}
