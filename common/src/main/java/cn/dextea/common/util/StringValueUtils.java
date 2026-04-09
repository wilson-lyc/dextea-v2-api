package cn.dextea.common.util;

public final class StringValueUtils {

    private StringValueUtils() {
    }

    public static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
