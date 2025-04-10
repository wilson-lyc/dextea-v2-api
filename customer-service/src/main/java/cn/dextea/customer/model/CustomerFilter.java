package cn.dextea.customer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFilter {
    private Long id;
    private String openId;
    private Integer status;
}
