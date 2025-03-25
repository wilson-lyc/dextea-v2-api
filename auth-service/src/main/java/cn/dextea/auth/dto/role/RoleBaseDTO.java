package cn.dextea.auth.dto.role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleBaseDTO {
    private Long id;
    private String name;
    private String description;
}
