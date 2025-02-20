package cn.dextea.staff.dto;

import cn.dextea.staff.pojo.Staff;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffCreateDTO {
    @NotBlank(message = "name不能为空")
    private String name;
    @NotBlank(message = "phone不能为空")
    private String phone;
    @NotNull(message = "side不能为空")
    private Integer side;
    private Integer storeId;
    public Staff toStaff(){
        return Staff.builder()
                .name(name)
                .phone(phone)
                .side(side)
                .storeId(storeId)
                .build();
    }
}
