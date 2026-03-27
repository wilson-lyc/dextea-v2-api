package cn.dextea.store.dto.request;

import jakarta.validation.constraints.Min;
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
    private Long size = 10L;

    private String name;

    private Integer status;

    private String province;

    private String city;

    private String district;

    private String phone;
}
