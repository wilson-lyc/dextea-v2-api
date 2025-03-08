package cn.dextea.store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreListDTO {
    private Long id;
    private String name;
    private Integer status;
    private String province;
    private String city;
    private String district;
    private String address;
    private String linkman;
    private String phone;
    private String openTime;
}
