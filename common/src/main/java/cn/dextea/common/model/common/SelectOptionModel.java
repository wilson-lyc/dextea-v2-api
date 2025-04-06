package cn.dextea.common.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectOptionModel {
    private Long value;
    private String label;
}
