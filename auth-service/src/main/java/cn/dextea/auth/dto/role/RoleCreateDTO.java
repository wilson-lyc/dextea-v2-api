package cn.dextea.auth.dto.role;

import cn.dextea.common.pojo.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleCreateDTO {
    private String name;
    private String description;

    public Role toRole(){
        return Role.builder()
                .name(name)
                .description(description)
                .routers(new ArrayList<>())
                .permissions(new ArrayList<>())
                .build();
    }
}
