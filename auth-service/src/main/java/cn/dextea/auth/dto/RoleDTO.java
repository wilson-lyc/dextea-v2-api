package cn.dextea.auth.dto;

import cn.dextea.auth.pojo.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
    @NotBlank(message = "label不能为空")
    private String label;
    private String description;
    public Role toRole() {
        return Role.builder()
                .label(label)
                .description(description)
                .build();
    }
}
