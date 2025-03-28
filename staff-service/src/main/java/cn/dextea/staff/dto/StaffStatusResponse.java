package cn.dextea.staff.dto;

import cn.dextea.common.code.StaffStatus;
import lombok.Data;

/**
 * @author Lai Yongchao
 */
@Data
public class StaffStatusResponse {
    private Integer status;
    private String statusLabel;

    public StaffStatusResponse(Integer status) {
        this.status = status;
    }

    public String getStatusLabel() {
        return StaffStatus.fromValue(status).getLabel();
    }
}
