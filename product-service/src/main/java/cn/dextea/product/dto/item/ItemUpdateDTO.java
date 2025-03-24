package cn.dextea.product.dto.item;

import cn.dextea.common.pojo.CustomizeItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemUpdateDTO {
    @NotBlank(message = "项目名不能为空")
    private String name;
    @NotNull(message = "排序不能为空")
    private Integer sort;
    @NotNull(message = "状态不能为空")
    private Integer status;

    public CustomizeItem toCustomize(){
        return CustomizeItem.builder()
                .name(name)
                .sort(sort)
                .status(status)
                .build();
    }
}
