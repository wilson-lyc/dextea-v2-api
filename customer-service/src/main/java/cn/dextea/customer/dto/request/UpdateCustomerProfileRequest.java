package cn.dextea.customer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerProfileRequest {

    @NotBlank(message = "昵称不能为空")
    @Size(max = 32, message = "昵称长度不能超过32位")
    private String nickname;
}
