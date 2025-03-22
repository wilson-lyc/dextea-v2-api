package cn.dextea.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductBaseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private String categoryName;
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
