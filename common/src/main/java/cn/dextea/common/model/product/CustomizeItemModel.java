package cn.dextea.common.model.product;

import cn.dextea.common.code.CustomizeItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomizeItemModel {
    // 绑定信息
    private Long productId;

    // 基础信息
    private Long id;
    private String name;
    private Integer sort;
    private Integer status;
    private String statusText;

    // 客制化选项
    List<CustomizeOptionModel> options;

    // 时间
    private String createTime;
    private String updateTime;

    public String getStatusText() {
        return CustomizeItemStatus.fromValue(status).getLabel();
    }
}
