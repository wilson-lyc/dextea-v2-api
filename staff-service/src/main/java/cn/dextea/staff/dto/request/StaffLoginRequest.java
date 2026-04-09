package cn.dextea.staff.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 员工登录请求。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffLoginRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(max = 64, message = "用户名长度不能超过64位")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(max = 128, message = "密码长度不能超过128位")
    private String password;
}
