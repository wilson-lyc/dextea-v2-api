package cn.dextea.store.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreUpdateLocationRequest {
    private Double longitude;
    private Double latitude;
}
