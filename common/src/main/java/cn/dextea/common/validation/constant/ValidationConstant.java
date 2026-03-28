package cn.dextea.common.validation.constant;

/**
 * 验证相关常量
 */
public final class ValidationConstant {

    private ValidationConstant() {}

    // 分页参数常量
    public static final long PAGE_CURRENT_MIN = 1L;
    public static final long PAGE_SIZE_MIN = 1L;
    public static final long PAGE_SIZE_MAX = 100L;
    public static final int PAGE_SIZE_DEFAULT = 10;

    // 字符串长度常量
    public static final int NAME_MAX_LENGTH = 64;
    public static final int TITLE_MAX_LENGTH = 128;
    public static final int DESCRIPTION_MAX_LENGTH = 255;
    public static final int ADDRESS_MAX_LENGTH = 255;
    public static final int PHONE_MAX_LENGTH = 11;

    // 地理坐标范围
    public static final double LONGITUDE_MIN = -180.0;
    public static final double LONGITUDE_MAX = 180.0;
    public static final double LATITUDE_MIN = -90.0;
    public static final double LATITUDE_MAX = 90.0;
}
