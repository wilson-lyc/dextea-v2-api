package cn.dextea.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 商品最终可售状态（由全局状态和门店状态计算得出）。
 * 0=下架（全局下架），1=售罄（全局上架但门店售罄），2=在售（全局和门店都在售）
 */
@Getter
@RequiredArgsConstructor
public enum ProductSaleStatus {
    DISABLED(0, "下架"),
    SOLD_OUT(1, "售罄"),
    ON_SALE(2, "在售");

    private final int value;
    private final String label;

    /**
     * 根据全局状态和门店状态计算商品最终可售状态。
     *
     * @param globalStatus 全局状态（ProductStatus: 0=下架, 1=上架）
     * @param storeStatus  门店状态（StoreProductSaleStatus: 0=售罄, 1=在售）；传 null 视为售罄
     * @return 最终可售状态
     */
    public static ProductSaleStatus resolve(Integer globalStatus, Integer storeStatus) {
        if (globalStatus == null || globalStatus != ProductStatus.ENABLED.getValue()) {
            return DISABLED;
        }
        if (storeStatus == null || storeStatus != StoreProductSaleStatus.ON_SALE.getValue()) {
            return SOLD_OUT;
        }
        return ON_SALE;
    }
}
