package cn.dextea.product.model.option;

import cn.dextea.common.code.CustomizeOptionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionListDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer sort;
    private Integer globalStatus;
    private Integer storeStatus;
    private Integer status;

    public Integer getStatus() {
        return CustomizeOptionStatus.getStatusValue(globalStatus,storeStatus);
    }
}
