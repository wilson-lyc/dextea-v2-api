package cn.dextea.staff.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffQueryDTO {
    private Long id;
    private String name;
    private String account;
    private String phone;
    private Integer status;
    private Integer side;
    private Integer storeId;
}