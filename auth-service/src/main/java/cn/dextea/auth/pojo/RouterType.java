package cn.dextea.auth.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Lai Yongchao
 */
@Getter
@AllArgsConstructor
public enum RouterType {
    SINGLE(0),
    GROUP(1),
    GROUP_ITEM(2);
    private final int code;
}
