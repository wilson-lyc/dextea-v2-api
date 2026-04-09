package cn.dextea.store.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TreeOptionModel {
    private Long value;
    private String label;
    private Boolean isLeaf=true;
}
