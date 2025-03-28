package cn.dextea.staff.dto;

import cn.dextea.common.code.StaffIdentity;
import cn.dextea.common.code.StaffStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffInfoResponse {
    Long id;
    String name;
    String namePinyin;
    String account;
    String phone;
    Integer status;
    String statusLabel;
    Integer identity;
    String identityLabel;
    Long storeId;
    String storeName;
    String createTime;
    String updateTime;

    public String getStatusLabel() {
        return StaffStatus.fromValue(status).getLabel();
    }

    public String getIdentityLabel() {
        return StaffIdentity.fromValue(identity).getLabel();
    }
}
