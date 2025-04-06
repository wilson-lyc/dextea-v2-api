package cn.dextea.staff.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class StaffUpdatePwdRequest {
    @NotBlank(message = "旧密码不能为空")
    private String oldPwd;
    @NotBlank(message = "新密码不能为空")
    private String newPwd;
}
