package cn.dextea.product.service;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.product.ProductModel;
import org.apache.ibatis.javassist.NotFoundException;

/**
 * @author Lai Yongchao
 */
public interface CustomerService {
    DexteaApiResponse<ProductModel> getProductInfo(Long id, Long storeId) throws NotFoundException;
}
