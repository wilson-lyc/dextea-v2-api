package cn.dextea.common.code;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ProductStatus {
    GLOBAL_FORBIDDEN(0,"全局禁售"),
    AVAILABLE(1,"可售"),
    SELL_OUT(2, "售罄"),
    STORE_FORBIDDEN(3, "门店禁售");

    @EnumValue
    private final int value;
    private final String label;

    public static ProductStatus fromValue(int value) {
        for (ProductStatus item : ProductStatus.values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        throw new IllegalArgumentException("未知的商品状态码");
    }

    public static ProductStatus getStatus(ProductStatus globalStafus, ProductStatus storeStatus) {
        if(globalStafus.equals(ProductStatus.GLOBAL_FORBIDDEN)){
            return ProductStatus.GLOBAL_FORBIDDEN;
        }else{
            return Objects.isNull(storeStatus)?globalStafus:storeStatus;
        }
    }
}
