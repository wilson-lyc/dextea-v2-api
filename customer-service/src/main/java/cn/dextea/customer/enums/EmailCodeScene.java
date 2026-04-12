package cn.dextea.customer.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailCodeScene {
    REGISTER(1, "注册"),
    LOGIN(2, "登录"),
    RESET_PASSWORD(3, "重置密码");

    private final int value;
    private final String label;

    public static boolean isValid(Integer value) {
        if (value == null) {
            return false;
        }
        for (EmailCodeScene scene : values()) {
            if (scene.value == value) {
                return true;
            }
        }
        return false;
    }

    public static EmailCodeScene of(int value) {
        for (EmailCodeScene scene : values()) {
            if (scene.value == value) {
                return scene;
            }
        }
        throw new IllegalArgumentException("未知的验证码场景：" + value);
    }
}
