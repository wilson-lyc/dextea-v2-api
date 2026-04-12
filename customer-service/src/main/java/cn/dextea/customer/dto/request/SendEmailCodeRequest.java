package cn.dextea.customer.dto.request;

import cn.dextea.common.validation.annotation.EnumValue;
import cn.dextea.customer.enums.EmailCodeScene;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailCodeRequest {

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 128, message = "邮箱长度不能超过128位")
    private String email;

    @NotNull(message = "验证码场景不能为空")
    @EnumValue(enumClass = EmailCodeScene.class, fieldName = "验证码场景")
    private Integer scene;
}
