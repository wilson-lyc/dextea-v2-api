package cn.dextea.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductListDTO {
    private Integer id;
    private String name;
    private Double price;
    private Integer sort;
    private Integer globalStatus;
    private Integer storeStatus;
    private Integer status;

    public Integer getStatus(){
        if(globalStatus == 0){
            return 0;
        }else if(Objects.isNull(storeStatus)){
            return globalStatus;
        }else{
            return storeStatus;
        }
    }
}
