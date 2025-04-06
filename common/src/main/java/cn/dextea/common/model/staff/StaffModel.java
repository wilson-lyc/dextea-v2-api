package cn.dextea.common.model.staff;

import cn.dextea.common.code.StaffIdentity;
import cn.dextea.common.code.StaffStatus;
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
public class StaffModel {
    // 基础信息
    private Long id;
    private String name;
    private String namePinyin;
    private String account;
    //状态
    private Integer status;
    private String statusText;
    // 身份
    private Integer identity;
    private String identityText;
    private Long storeId;
    private String storeName;
    // 登录
    private String token;
    // 时间
    private String createTime;//创建时间
    private String updateTime;//更新时间

    public String getStatusText() {
        return Objects.isNull(status)?null: StaffStatus.fromValue(status).getLabel();
    }

    public String getIdentityText() {
        return Objects.isNull(identity)?null: StaffIdentity.fromValue(identity).getLabel();
    }
}
