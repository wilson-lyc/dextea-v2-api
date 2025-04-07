package cn.dextea.auth.model;

import cn.dextea.auth.pojo.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleUpdateRequest {
    @NotBlank(message = "角色名不能为空")
    private String name;
    private String description;
    private List<Long> permissions;

    public Role toRole(Long id){
        return Role.builder()
                .id(id)
                .name(name)
                .description(description)
                .permissions(Objects.isNull(permissions)?new ArrayList<>():permissions)
                .build();
    }
}
