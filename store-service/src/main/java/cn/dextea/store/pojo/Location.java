package cn.dextea.store.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private Double longitude;
    private Double latitude;
    private String province;
    private String city;
    private String district;
    private String address;
}
