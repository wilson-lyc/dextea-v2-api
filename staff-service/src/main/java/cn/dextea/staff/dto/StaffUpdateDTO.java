package cn.dextea.staff.dto;

import cn.dextea.common.code.StaffStatus;
import cn.dextea.common.pojo.Staff;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffUpdateDTO {
    @NotBlank(message = "手机号不能为空")
    private String phone;
    @NotNull(message = "状态不能为空")
    private StaffStatus status;
    public Staff toStaff(){
        return Staff.builder()
                .phone(phone)
                .status(status)
                .build();
    }
}
