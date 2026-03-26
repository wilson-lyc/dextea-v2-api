package cn.dextea.staff.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 员工登录响应。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffLoginResponse {
    private String tokenName;

    private String tokenValue;

    private StaffDetailResponse staff;
}
