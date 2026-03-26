package cn.dextea.staff.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BindRolePermissionRequest {
    @NotBlank(message = "权限名称不能为空")
    @Size(max = 255, message = "权限名称长度不能超过255位")
    private String permissionName;
}
