package cn.dextea.customer.dto.request;

import cn.dextea.common.validation.annotation.EnumValue;
import cn.dextea.customer.enums.CustomerStatus;
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
public class CustomerPageQueryRequest {

    @Min(value = 1, message = "当前页码不能小于1")
    @Builder.Default
    private Long current = 1L;

    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 100, message = "每页条数不能大于100")
    @Builder.Default
    private Long size = 10L;

    @Size(max = 32, message = "昵称长度不能超过32位")
    private String nickname;

    @Size(max = 128, message = "邮箱长度不能超过128位")
    private String email;

    @EnumValue(enumClass = CustomerStatus.class, fieldName = "顾客状态")
    private Integer status;
}
