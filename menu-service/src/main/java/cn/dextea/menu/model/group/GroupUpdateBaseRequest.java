package cn.dextea.menu.model.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupUpdateBaseRequest {
    @NotBlank(message = "分组名不能为空")
    private String name;
    @NotNull(message = "排序不能为空")
    private Integer sort;
}
