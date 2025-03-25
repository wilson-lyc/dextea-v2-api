package cn.dextea.product.dto.product;

import cn.dextea.common.code.ProductStatus;
import lombok.Data;

/**
 * @author Lai Yongchao
 */
@Data
public class ProductStatusDTO {
    private Integer globalStatus;
    private Integer storeStatus;
    private Integer status;

    public ProductStatusDTO(Integer globalStatus) {
        this.globalStatus = globalStatus;
    }

    public ProductStatusDTO(Integer globalStatus, Integer storeStatus) {
        this.globalStatus = globalStatus;
        this.storeStatus = storeStatus;
    }

    public Integer getStatus() {
        return ProductStatus.getStatus(globalStatus,storeStatus);
    }
}
