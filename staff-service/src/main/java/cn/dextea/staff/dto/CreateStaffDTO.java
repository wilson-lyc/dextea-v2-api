package cn.dextea.staff.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateStaffDTO {
    @NotBlank(message = "用户名不能为空")
    private String name;
    @NotBlank(message = "手机号不能为空")
    private String phone;
}
