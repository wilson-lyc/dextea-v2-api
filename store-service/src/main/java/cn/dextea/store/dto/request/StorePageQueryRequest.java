package cn.dextea.store.dto.request;

import cn.dextea.common.validation.annotation.EnumValue;
import cn.dextea.store.enums.StoreStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorePageQueryRequest {
    @Builder.Default
    @Min(value = 1, message = "页码不能小于1")
    private Long current = 1L;

    @Builder.Default
    @Min(value = 1, message = "分页大小不能小于1")
    @Max(value = 100, message = "每页条数不能大于100")
    private Long size = 10L;

    @Size(max = 64, message = "门店名称长度不能超过64位")
    private String name;

    @EnumValue(enumClass = StoreStatus.class, fieldName = "门店状态")
    private Integer status;

    @Size(max = 32, message = "省份长度不能超过32位")
    private String province;

    @Size(max = 32, message = "城市长度不能超过32位")
    private String city;

    @Size(max = 32, message = "区县长度不能超过32位")
    private String district;

    private String phone;
}
