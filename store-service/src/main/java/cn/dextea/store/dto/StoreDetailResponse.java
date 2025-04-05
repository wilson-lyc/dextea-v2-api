package cn.dextea.store.dto;

import cn.dextea.common.code.StoreStatus;
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
public class StoreDetailResponse {
    private Long id;
    private String name;
    private Integer status;
    private String statusName;
    private String province;
    private String city;
    private String district;
    private String address;
    private Double longitude;
    private Double latitude;
    private Double distance;
    private String distanceUnit;
    private String openTime;
    private String linkman;
    private String phone;

    public String getStatusName() {
        return StoreStatus.fromValue(status).getLabel();
    }
}
