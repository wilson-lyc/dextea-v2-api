package cn.dextea.staff.dto;

import cn.dextea.common.code.StaffIdentity;
import cn.dextea.common.code.StaffStatus;
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
    private Integer identity;
    private Integer storeId;
}