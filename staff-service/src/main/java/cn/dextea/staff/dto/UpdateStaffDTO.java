package cn.dextea.staff.dto;

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
    private String role;
    private Long storeId;
    private Boolean state;
}
