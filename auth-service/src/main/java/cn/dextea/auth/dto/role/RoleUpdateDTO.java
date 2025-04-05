package cn.dextea.auth.dto.role;

import cn.dextea.auth.pojo.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleUpdateDTO {
    private String name;
    private String description;

    public Role toRole(Long id){
        return Role.builder()
                .id(id)
                .name(name)
                .description(description)
                .routers(new ArrayList<>())
                .permissions(new ArrayList<>())
                .build();
    }
}
