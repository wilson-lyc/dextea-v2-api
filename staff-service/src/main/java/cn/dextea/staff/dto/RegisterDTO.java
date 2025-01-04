package cn.dextea.staff.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {
    @NotBlank(message = "用户名不能为空")
    String name;
    @NotBlank(message = "手机号不能为空")
    String phone;
    @NotBlank(message = "角色不能为空")
    String role;
}
