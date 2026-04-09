package cn.dextea.product.dto.request;

import cn.dextea.common.validation.annotation.EnumValue;
import cn.dextea.product.enums.MenuStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMenuRequest {

    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 64, message = "菜单名称长度不能超过64位")
    private String name;

    @Size(max = 255, message = "菜单描述长度不能超过255位")
    private String description;

    @NotNull(message = "菜单状态不能为空")
    @EnumValue(enumClass = MenuStatus.class, fieldName = "菜单状态")
    private Integer status;

    @Valid
    private List<MenuGroupRequest> groups;
}
