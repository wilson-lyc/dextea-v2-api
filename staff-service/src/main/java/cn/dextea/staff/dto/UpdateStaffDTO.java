package cn.dextea.staff.dto;

import cn.dextea.staff.pojo.Staff;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStaffDTO {
    private String phone;
    private Boolean status;
    private Integer storeId;
    public Staff toStaff(){
        return Staff.builder()
                .phone(phone)
                .status(status)
                .storeId(storeId)
                .build();
    }
}
