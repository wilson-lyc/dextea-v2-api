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
public class StaffFilter {
    private Long id;
    private String name;
    private String account;
    private String phone;
    private Integer status;
    private Integer identity;
    private Integer storeId;
}