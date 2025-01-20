package cn.dextea.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author Lai Yongchao
 */
@Getter
@AllArgsConstructor
public enum Side {
    COMPANY(1, "公司"),
    STORE(2, "门店"),
    CUSTOMER(3, "顾客");
    private final int code;
    private final String msg;
}
