package cn.dextea.staff.dto;

import cn.dextea.staff.pojo.Staff;
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
    private Long id;
    private String name;
    private String account;
    private String phone;
    private Boolean status;
    private Integer side;
    private Integer storeId;
}