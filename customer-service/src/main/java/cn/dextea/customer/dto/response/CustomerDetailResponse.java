package cn.dextea.customer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetailResponse {
    private Long id;
    private String nickname;
    private String email;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
