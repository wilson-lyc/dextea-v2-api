package cn.dextea.order.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Getter
@RequiredArgsConstructor
public enum UpdateStatusMode {
    DEFAULT("default", "默认"),
    SILENT("silent", "静默");

    private final String value;
    private final String label;
}
