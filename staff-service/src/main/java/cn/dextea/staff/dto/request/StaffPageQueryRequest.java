package cn.dextea.staff.dto.request;

import jakarta.validation.constraints.Min;
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
    @Builder.Default
    private Long size = 10L;

    @Size(max = 64, message = "用户名长度不能超过64位")
    private String username;

    @Size(max = 64, message = "员工姓名长度不能超过64位")
    private String realName;

    private Integer userType;

    private Integer status;
}
