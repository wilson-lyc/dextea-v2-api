package cn.dextea.store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreDetailResponse {
    private Long id;

    private String name;

    private String province;

    private String city;

    private String district;

    private String address;

    private Integer status;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String phone;

    private String openTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
