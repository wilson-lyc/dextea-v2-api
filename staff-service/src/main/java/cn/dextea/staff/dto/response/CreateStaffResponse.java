package cn.dextea.staff.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 后台创建员工响应。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateStaffResponse {
    private Long id;

    private String username;

    private String realName;

    private Integer userType;

    private Integer status;

    private String initialPassword;

    private LocalDateTime createTime;
}
