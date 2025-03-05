package cn.dextea.store.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Lai Yongchao
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("s_store")
public class Store {
    @TableId(type = IdType.AUTO)
    private Long id;// 门店ID
    private String name;// 门店名称
    private Integer status;// 门店状态
    private String province;// 省
    private String city;// 市
    private String district;// 区
    private String address;// 详细地址
    private BigDecimal longitude;// 经度
    private BigDecimal latitude;// 纬度
    private String linkman;// 联系人
    private String phone;// 联系电话
    private String openTime;// 营业时间
    private String businessLicense;// 营业执照
    private String foodBusinessLicense;// 食品经营许可证
    private String createTime;// 创建时间
    private String updateTime;// 更新时间
}
