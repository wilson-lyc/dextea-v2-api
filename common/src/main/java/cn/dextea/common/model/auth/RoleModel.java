package cn.dextea.common.model.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleModel {
    private Long id;
    private String name;
    private String description;
    private List<PermissionModel> permissions;
}
