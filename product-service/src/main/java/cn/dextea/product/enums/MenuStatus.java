package cn.dextea.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MenuStatus {
    DISABLED(0),
    ENABLED(1);

    private final Integer value;
}
