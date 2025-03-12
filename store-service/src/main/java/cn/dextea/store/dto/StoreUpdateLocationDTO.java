package cn.dextea.store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreUpdateLocationDTO {
    private Double longitude;
    private Double latitude;
}
