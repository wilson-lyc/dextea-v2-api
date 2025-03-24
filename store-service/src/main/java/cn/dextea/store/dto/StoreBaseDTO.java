package cn.dextea.store.dto;

import cn.dextea.common.code.StaffStatus;
import cn.dextea.common.code.StoreStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreBaseDTO {
    private Long id;
    private String name;
    private StoreStatus status;
    private String province;
    private String city;
    private String district;
    private String address;
    private String linkman;
    private String phone;
    private String openTime;
}
