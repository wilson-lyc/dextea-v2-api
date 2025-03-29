package cn.dextea.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nls;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerListResponse {
    private Long id;
    private String openId;
    private String name;
    private String createTime;
    private String updateTime;
}
