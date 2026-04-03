package cn.dextea.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomizationStatus {
    DELETED(0, "已删除"),
    ACTIVE(1, "正常");

    private final Integer value;
    private final String desc;
}
