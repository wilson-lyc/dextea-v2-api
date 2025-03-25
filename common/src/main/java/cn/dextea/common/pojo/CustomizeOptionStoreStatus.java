package cn.dextea.common.pojo;

import cn.dextea.common.code.CustomizeOptionStatus;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("r_customize_option_store_status")
public class CustomizeOptionStoreStatus {
    private Long optionId;
    private Long storeId;
    private CustomizeOptionStatus status;
    private String createTime;
    private String updateTime;
}
