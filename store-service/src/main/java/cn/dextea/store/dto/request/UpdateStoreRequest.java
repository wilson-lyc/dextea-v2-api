package cn.dextea.store.dto.request;

import cn.dextea.common.validation.annotation.EnumValue;
import cn.dextea.common.validation.annotation.PhoneNumber;
import cn.dextea.store.enums.StoreStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(max = 64, message = "店名长度不能超过64位")
    private String name;

    @NotBlank(message = "省不能为空")
    @Size(max = 32, message = "省份长度不能超过32位")
    private String province;

    @NotBlank(message = "市不能为空")
    @Size(max = 32, message = "城市长度不能超过32位")
    private String city;

    @NotBlank(message = "区不能为空")
    @Size(max = 32, message = "区县长度不能超过32位")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    @Size(max = 255, message = "详细地址长度不能超过255位")
    private String address;

    @NotNull(message = "门店状态不能为空")
    @EnumValue(enumClass = StoreStatus.class, fieldName = "门店状态")
    private Integer status;

    @NotNull(message = "经度是必填的")
    @DecimalMin(value = "-180", message = "经度不能小于-180")
    @DecimalMax(value = "180", message = "经度不能大于180")
    private BigDecimal longitude;

    @NotNull(message = "纬度是必填的")
    @DecimalMin(value = "-90", message = "纬度不能小于-90")
    @DecimalMax(value = "90", message = "纬度不能大于90")
    private BigDecimal latitude;

    @NotBlank(message = "联系电话不能为空")
    @PhoneNumber(fieldName = "联系电话")
    private String phone;

    @NotBlank(message = "营业时间不能为空")
    @Size(max = 64, message = "营业时间长度不能超过64位")
    private String openTime;
}
