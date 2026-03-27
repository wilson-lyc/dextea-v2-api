package cn.dextea.store.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStoreRequest {
    @NotBlank(message = "店名不能为空")
    private String name;

    @NotBlank(message = "省不能为空")
    private String province;

    @NotBlank(message = "市不能为空")
    private String city;

    @NotBlank(message = "区不能为空")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    private String address;

    @NotNull(message = "状态不能为空")
    private Integer status;

    @NotNull(message = "经度不能为空")
    private BigDecimal longitude;

    @NotNull(message = "纬度不能为空")
    private BigDecimal latitude;

    @NotBlank(message = "联系电话不能为空")
    private String phone;

    @NotBlank(message = "营业时间不能为空")
    private String openTime;
}
