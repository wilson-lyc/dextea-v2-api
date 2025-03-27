package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import org.apache.ibatis.javassist.NotFoundException;

/**
 * @author Lai Yongchao
 */
public interface CustomerService {
    ApiResponse getProductInfo(Long id, Long storeId) throws NotFoundException;
}
