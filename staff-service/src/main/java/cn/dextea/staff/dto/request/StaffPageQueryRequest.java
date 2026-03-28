package cn.dextea.staff.dto.request;

import cn.dextea.common.validation.annotation.EnumValue;
import cn.dextea.staff.enums.StaffStatus;
import cn.dextea.staff.enums.StaffType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 员工分页查询请求。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffPageQueryRequest {
    @Min(value = 1, message = "当前页码不能小于1")
    @Builder.Default
    private Long current = 1L;

    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 100, message = "每页条数不能大于100")
    @Builder.Default
    private Long size = 10L;

    @Size(max = 64, message = "用户名长度不能超过64位")
    private String username;

    @Size(max = 64, message = "员工姓名长度不能超过64位")
    private String realName;

    @NotNull(message = "门店ID是必填的")
    @Min(value = 1, message = "门店ID不能小于1")
    private Long storeId;

    @EnumValue(enumClass = StaffType.class, fieldName = "用户类型")
    private Integer userType;

    @EnumValue(enumClass = StaffStatus.class, fieldName = "员工状态")
    private Integer status;
}
