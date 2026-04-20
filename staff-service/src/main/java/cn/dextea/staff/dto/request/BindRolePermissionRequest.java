package cn.dextea.staff.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BindRolePermissionRequest {
    @NotNull(message = "权限ID不能为空")
    @Min(value = 1, message = "权限ID不能为空")
    private Long permissionId;
}
