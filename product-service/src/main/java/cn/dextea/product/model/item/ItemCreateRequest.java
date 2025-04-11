package cn.dextea.product.model.item;

import cn.dextea.common.code.CustomizeItemStatus;
import cn.dextea.product.pojo.CustomizeItem;
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
public class ItemCreateRequest {
    @NotBlank(message = "项目名不能为空")
    private String name;
    @NotNull(message = "排序不能为空")
    private Integer sort;

    public CustomizeItem toCustomize() {
        return  CustomizeItem.builder()
                .name(name)
                .sort(sort)
                .build();
    }
}
