package cn.dextea.store.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("store")
public class StoreEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String province;

    private String city;

    private String district;

    private String address;

    private Integer status;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String phone;

    @TableField("open_time")
    private String openTime;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
