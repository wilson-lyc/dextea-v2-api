package cn.dextea.store.dto.request;

import cn.dextea.common.validation.annotation.EnumValue;
import cn.dextea.store.enums.StoreStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新门店状态请求。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStoreStatusRequest {
    @NotNull(message = "门店状态不能为空")
    @EnumValue(enumClass = StoreStatus.class, fieldName = "门店状态")
    private Integer status;
}
