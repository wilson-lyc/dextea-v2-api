package cn.dextea.common.model.product;

import cn.dextea.common.code.CustomizeOptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomizeOptionModel {
    // 绑定信息
    private Long itemId;

    // 基础信息
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer sort;

    // 状态
    private Integer globalStatus;
    private Integer storeStatus;
    private Integer status;
    private String statusText;

    // 时间
    private String createTime;
    private String updateTime;

    public Integer getStatus() {
        return CustomizeOptionStatus.getStatusValue(globalStatus, storeStatus);
    }

    public String getStatusText() {
        return Objects.nonNull(getStatus())?
                CustomizeOptionStatus.fromValue(getStatus()).getLabel():null;
    }
}
