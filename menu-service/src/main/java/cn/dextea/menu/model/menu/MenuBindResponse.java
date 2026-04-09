package cn.dextea.menu.model.menu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuBindResponse {
    private List<Long> successIds;
    private List<Long> failIds;
}
