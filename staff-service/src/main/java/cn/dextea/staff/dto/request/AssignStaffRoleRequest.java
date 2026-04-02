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
public class AssignStaffRoleRequest {
    @NotNull(message = "角色ID不能为空")
    @Min(value = 1, message = "角色ID不能小于1")
    private Long roleId;
}
