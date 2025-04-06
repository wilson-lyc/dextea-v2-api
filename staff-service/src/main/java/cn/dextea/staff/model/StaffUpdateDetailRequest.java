package cn.dextea.staff.model;

import cn.dextea.staff.pojo.Staff;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffUpdateDetailRequest {
    @NotBlank(message = "手机号不能为空")
    private String phone;
    @NotNull(message = "状态不能为空")
    private Integer status;
    public Staff toStaff(Long id){
        return Staff.builder()
                .id(id)
                .phone(phone)
                .status(status)
                .build();
    }
}
