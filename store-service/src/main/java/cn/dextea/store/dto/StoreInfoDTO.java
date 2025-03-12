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
public class StoreInfoDTO {
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
    private String linkman;
    private String phone;
}
