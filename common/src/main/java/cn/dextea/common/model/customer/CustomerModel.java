package cn.dextea.common.model.customer;

import cn.dextea.common.code.CustomerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerModel {
    private Long id;
    private String openId;
    private String name;
    private Integer status;
    private String statusText;
    private String createTime;
    private String updateTime;
    private String token;

    public String getStatusText() {
        return Objects.nonNull(status)?CustomerStatus.fromValue(status).getLabel():null;
    }
}
