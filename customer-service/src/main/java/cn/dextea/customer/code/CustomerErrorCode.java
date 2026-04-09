package cn.dextea.customer.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Getter
@RequiredArgsConstructor
public enum CustomerErrorCode {
    CUSTOMER_ALIPAY_API_ERROR("customer.alipay.api.error", "支付宝接口异常"),
    CUSTOMER_NOT_FOUND("customer.not.found", "顾客不存在"),
    CUSTOMER_FORBIDDEN("customer.forbidden", "此账号被禁用");
    private final String code;
    private final String msg;
}
