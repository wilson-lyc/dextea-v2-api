package cn.dextea.product.cache;

public final class CacheNames {

    private CacheNames() {}

    // Spring Cache logical names
    public static final String PRODUCT_BIZ_DETAIL         = "productBizDetail";
    public static final String PRODUCT_BIZ_LIST           = "productBizList";
    public static final String MENU_BIZ                   = "menuBiz";
    public static final String CUSTOMIZATION_ITEM_BIZ     = "customizationItemBiz";
    public static final String CUSTOMIZATION_OPTIONS_BIZ  = "customizationOptionsBiz";

    // Redis key prefixes (mirrors design doc naming convention)
    public static final String REDIS_PREFIX_PRODUCT_BIZ_DETAIL        = "dextea:product:biz:detail";
    public static final String REDIS_PREFIX_PRODUCT_BIZ_LIST          = "dextea:product:biz:list";
    public static final String REDIS_PREFIX_MENU_BIZ                  = "dextea:menu:biz";
    public static final String REDIS_PREFIX_CUSTOMIZATION_ITEM_BIZ    = "dextea:customization:biz:item";
    public static final String REDIS_PREFIX_CUSTOMIZATION_OPTIONS_BIZ = "dextea:customization:biz:options";
}
