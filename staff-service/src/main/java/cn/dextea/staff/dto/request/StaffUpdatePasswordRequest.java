package cn.dextea.staff.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 员工修改登录密码请求。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffUpdatePasswordRequest {
    @NotBlank(message = "原密码不能为空")
    @Size(max = 128, message = "原密码长度不能超过128位")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(max = 128, message = "新密码长度不能超过128位")
    private String newPassword;
}
