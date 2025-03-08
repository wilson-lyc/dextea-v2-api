package cn.dextea.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Distance;

/**
 * @author Lai Yongchao
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NearbyStoreDTO {
    private Long id;
    private String name;
    private Integer status;
    private String province;
    private String city;
    private String district;
    private String address;
    private Double longitude;
    private Double latitude;
    private Double distance;
    private String distanceUnit;
    private String openTime;
}
