package cn.dextea.staff.dto.request;

import cn.dextea.common.validation.annotation.EnumValue;
import cn.dextea.staff.enums.StaffType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 后台创建员工请求。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateStaffRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(max = 64, message = "用户名长度不能超过64位")
    private String username;

    @NotBlank(message = "员工姓名不能为空")
    @Size(max = 64, message = "员工姓名长度不能超过64位")
    private String realName;

    @NotNull(message = "员工类型不能为空")
    @EnumValue(enumClass = StaffType.class, fieldName = "员工类型")
    private Integer userType;
}
