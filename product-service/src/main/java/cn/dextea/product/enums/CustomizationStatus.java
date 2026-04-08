package cn.dextea.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomizationStatus {
    DISABLED(0, "禁用"),
    ACTIVE(1, "激活");

    private final Integer value;
    private final String desc;
}
