package cn.dextea.store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchStoreDTO {
    private Long id;
    private String name;
    private String state;
    private String province;
    private String city;
    private String district;
    private String linkman;
    private String phone;
}
