package cn.dextea.staff.dto;

import cn.dextea.common.code.StaffIdentity;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@RequiredArgsConstructor
public class StaffListDTO {
    private Long id;
    private String name;
    private String account;
    private String phone;
    private Integer status;
    private StaffIdentity identity;
}
