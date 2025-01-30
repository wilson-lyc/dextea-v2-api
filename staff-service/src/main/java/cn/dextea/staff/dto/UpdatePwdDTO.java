package cn.dextea.staff.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePwdDTO {
    @NotBlank
    private String oldPwd;
    @NotBlank
    private String newPwd;
}
