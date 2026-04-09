package cn.dextea.staff.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 重置员工密码响应。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetStaffPasswordResponse {
    private Long id;

    private String username;

    private String resetPassword;
}
