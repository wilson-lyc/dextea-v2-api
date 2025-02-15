package cn.dextea.product.dto;

import cn.dextea.product.pojo.Menu;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuCreateDTO {
    @NotBlank
    private String name;
    private String description;
    public Menu toMenu() {
        return Menu.builder()
                .name(name)
                .description(description)
                .build();
    }
}
