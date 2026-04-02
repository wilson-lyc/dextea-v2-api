package cn.dextea.staff.dto.request;

import cn.dextea.common.validation.annotation.EnumValue;
import cn.dextea.staff.enums.RoleDataScope;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoleRequest {
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 255, message = "角色名称长度不能超过255位")
    private String name;

    @Size(max = 255, message = "备注长度不能超过255位")
    private String remark;

    @NotNull(message = "数据范围不能为空")
    @EnumValue(enumClass = RoleDataScope.class, fieldName = "数据范围")
    private Integer dataScope;
}
