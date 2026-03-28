package cn.dextea.store.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyStoreResponse {

    private Long id;

    private String name;

    private String province;

    private String city;

    private String district;

    private String address;

    private Integer status;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String phone;

    private String openTime;

    /**
     * 距离用户的位置，单位：米
     */
    private Double distance;
}
