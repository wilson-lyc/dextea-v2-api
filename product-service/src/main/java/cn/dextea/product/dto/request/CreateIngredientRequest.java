package cn.dextea.product.dto.request;

import cn.dextea.common.validation.annotation.EnumValue;
import cn.dextea.product.enums.ShelfLifeUnit;
import cn.dextea.product.enums.StorageMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateIngredientRequest {

    @NotBlank(message = "原料名称不能为空")
    @Size(max = 64, message = "原料名称长度不能超过64位")
    private String name;

    @NotNull(message = "保质时长不能为空")
    @Min(value = 1, message = "保质时长不能小于1")
    private Integer shelfLife;

    @NotNull(message = "保质时长单位不能为空")
    @EnumValue(enumClass = ShelfLifeUnit.class, fieldName = "保质时长单位")
    private Integer shelfLifeUnit;

    @NotNull(message = "存储方式不能为空")
    @EnumValue(enumClass = StorageMethod.class, fieldName = "存储方式")
    private Integer storageMethod;
}
