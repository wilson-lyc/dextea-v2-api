package cn.dextea.menu.dto.group;

import cn.dextea.common.pojo.MenuGroup;
import cn.dextea.common.pojo.MenuProduct;
import cn.hutool.core.util.IdUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupUpdateBaseDTO {
    @NotBlank(message = "分组名不能为空")
    private String name;
    @NotNull(message = "排序不能为空")
    private Integer sort;
}
