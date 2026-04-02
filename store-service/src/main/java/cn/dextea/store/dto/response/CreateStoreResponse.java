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
public class CreateStoreResponse {
    private Long id;

    private String name;

    private Integer status;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private LocalDateTime createTime;
}
