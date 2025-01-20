package cn.dextea.auth.dto;

import cn.dextea.auth.pojo.Role;
import com.alibaba.fastjson2.JSONArray;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
    @NotBlank
    private String key;
    private String description;
    @NotNull
    private List<Integer> routers;
    public Role toRole() {
        return Role.builder()
                .key(key)
                .description(description)
                .routers(routers)
                .build();
    }
}
