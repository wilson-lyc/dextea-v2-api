package cn.dextea.staff.dto;

import cn.dextea.common.code.StaffIdentity;
import cn.dextea.common.code.StaffStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffListResponse {
    private Long id;
    private String name;
    private String account;
    private String phone;
    private Integer status;
    private String statusLabel;
    private Integer identity;
    private String identityLabel;

    public String getStatusLabel() {
        return StaffStatus.fromValue(status).getLabel();
    }

    public String getIdentityLabel() {
        return StaffIdentity.fromValue(identity).getLabel();
    }
}
