package cn.dextea.staff.dto;

import cn.dextea.common.code.StaffIdentity;
import cn.dextea.common.code.StaffStatus;
import cn.dextea.common.pojo.Staff;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class StaffCreateDTO {
    @NotBlank(message = "姓名不能为空")
    private String name;
    @NotBlank(message = "手机号不能为空")
    private String phone;
    @NotNull(message = "身份不能为空")
    private Integer identity;
    private Long storeId;
    public Staff toStaff(){
        return Staff.builder()
                .name(name)
                .phone(phone)
                .identity(identity)
                .storeId(storeId)
                .status(StaffStatus.FORBIDDEN.getValue())
                .build();
    }
}
