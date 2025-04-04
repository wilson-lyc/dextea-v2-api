package cn.dextea.store.dto;

import cn.dextea.common.code.StoreStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetStoreDetailResponse {
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
    private String linkman;
    private String phone;
    private String openTime;
    private String businessLicense;
    private String foodLicense;
    private Double distance;
    private String distanceUnit;

    public String getStatusName() {
        return StoreStatus.fromValue(status).getLabel();
    }
}
