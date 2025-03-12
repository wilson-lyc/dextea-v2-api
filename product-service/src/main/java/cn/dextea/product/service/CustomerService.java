package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;

/**
 * @author Lai Yongchao
 */
public interface CustomerService {
    ApiResponse getProductInfo(Long id, Long storeId);
}
