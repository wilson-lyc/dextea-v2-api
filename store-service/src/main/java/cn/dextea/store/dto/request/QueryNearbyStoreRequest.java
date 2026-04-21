package cn.dextea.store.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询附近门店请求。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryNearbyStoreRequest {

    /**
     * 用户经度
     */
    @NotNull(message = "经度不能为空")
    @DecimalMin(value = "73.0", message = "经度范围无效")
    @DecimalMax(value = "135.0", message = "经度范围无效")
    private Double longitude;

    /**
     * 用户纬度
     */
    @NotNull(message = "纬度不能为空")
    @DecimalMin(value = "3.0", message = "纬度范围无效")
    @DecimalMax(value = "54.0", message = "纬度范围无效")
    private Double latitude;

    /**
     * 搜索半径，单位：米，默认 3000 米
     */
    @NotNull(message = "搜索半径不能为空")
    @Min(value = 500, message = "搜索半径不能小于500米")
    @Max(value = 10000, message = "搜索半径不能大于10000米")
    @Builder.Default
    private Integer radius = 3000;

    /**
     * 推荐数量上限，默认 10 家
     */
    @NotNull(message = "推荐数量不能为空")
    @Builder.Default
    @Min(value = 1, message = "推荐数量至少为1")
    @Max(value = 50, message = "推荐数量最多为50")
    private Integer limit = 10;
}
