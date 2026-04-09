package cn.dextea.staff.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 员工详情响应。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffDetailResponse {
    private Long id;

    private String username;

    private String realName;

    private Integer userType;

    private Integer status;

    private LocalDateTime lastLoginTime;

    private String lastLoginIp;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
