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
public class StoreLocationDTO {
    private Long id;
    private Double longitude;
    private Double latitude;
}
