package cn.dextea.customer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerLoginResponse {
    private String tokenName;
    private String tokenValue;
    private Long tokenTimeout;
    private Long customerId;
    private String nickname;
    private String email;
}
