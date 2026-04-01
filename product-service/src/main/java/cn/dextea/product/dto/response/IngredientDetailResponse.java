package cn.dextea.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientDetailResponse {

    private Long id;
    private String name;
    private String unit;
    private Integer shelfLife;
    private Integer shelfLifeUnit;
    private Integer storageMethod;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
