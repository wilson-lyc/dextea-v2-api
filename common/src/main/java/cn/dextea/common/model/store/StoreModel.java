package cn.dextea.common.model.store;

import cn.dextea.common.code.StoreStatus;
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
public class StoreModel {
    // 基础信息
    private Long id;
    private String name;
    private String openTime;

    // 地理位置
    private String province;
    private String city;
    private String district;
    private String address;
    private Double longitude;
    private Double latitude;

    // 状态
    private Integer status;
    private String statusText;

    // 联系方式
    private String linkman;
    private String phone;

    // 许可证
    private String businessLicense;
    private String foodLicense;

    // 菜单
    private Long menuId;

    // 距离
    private Double distance;
    private String distanceUnit;

    // 时间
    private String createTime;
    private String updateTime;

    public String getStatusText() {
        return Objects.isNull(status)?null:StoreStatus.fromValue(status).getLabel();
    }
}
