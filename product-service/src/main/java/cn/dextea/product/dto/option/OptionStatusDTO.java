package cn.dextea.product.dto.option;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

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

    public Integer getStatus() {
        if(globalStatus == 0){
            return 0;
        }else if(Objects.isNull(storeStatus)){
            return globalStatus;
        }else{
            return storeStatus;
        }
    }
}
