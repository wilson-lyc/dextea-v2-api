package cn.dextea.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomizationItemDetailResponse {

    private Long id;
    private String name;
    private String description;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<CustomizationOptionDetailResponse> options;
}
