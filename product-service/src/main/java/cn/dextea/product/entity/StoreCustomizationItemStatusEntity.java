package cn.dextea.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("store_customization_item_status")
public class StoreCustomizationItemStatusEntity {

    private Long storeId;

    private Long itemId;

    private LocalDateTime createTime;
}
