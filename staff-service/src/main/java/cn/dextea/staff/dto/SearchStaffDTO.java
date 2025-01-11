package cn.dextea.staff.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchStaffDTO {
    @Min(value = 1, message = "页码不能小于1")
    int current=1;
    @Min(value = 1, message = "页大小不能小于1")
    int size = 10;
    Long id;
    String name;
    String account;
    String role;
    Long storeId;
    String phone;
    int state;
}