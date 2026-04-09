package cn.dextea.customer.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterCustomerRequest {

    @NotBlank(message = "昵称不能为空")
    @Size(max = 32, message = "昵称长度不能超过32位")
    private String nickname;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 128, message = "邮箱长度不能超过128位")
    private String email;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 64, message = "密码长度须在6到64位之间")
    private String password;

    @NotBlank(message = "验证码不能为空")
    private String code;
}
