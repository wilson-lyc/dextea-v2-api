package cn.dextea.product.model.option;

import cn.dextea.common.code.CustomizeOptionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionStatusDTO {
    private Integer globalStatus;
    private Integer storeStatus;
    private Integer status;

    public OptionStatusDTO(Integer globalStatus) {
        this.globalStatus = globalStatus;
    }

    public OptionStatusDTO(Integer globalStatus, Integer storeStatus) {
        this.globalStatus = globalStatus;
        this.storeStatus = storeStatus;
    }

    public Integer getStatus() {
        return CustomizeOptionStatus.getStatusValue(globalStatus,storeStatus);
    }
}
