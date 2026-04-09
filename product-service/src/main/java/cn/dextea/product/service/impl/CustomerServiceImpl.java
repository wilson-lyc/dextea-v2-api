package cn.dextea.product.service.impl;

import cn.dextea.common.model.common.ApiResponse;
import cn.dextea.common.feign.ProductFeign;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.product.ProductModel;
import cn.dextea.product.code.ProductErrorCode;
import cn.dextea.product.service.CustomerService;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class CustomerServiceImpl implements CustomerService {
    @Resource
    private ProductFeign productFeign;

    @Override
    public DexteaApiResponse<ProductModel> getProductInfo(Long productId, Long storeId) throws NotFoundException {
        ProductModel product = productFeign.getProductDetail(productId,storeId);
        if (Objects.isNull(product)) {
            return DexteaApiResponse.notFound(ProductErrorCode.PRODUCT_NOT_FOUND.getCode(),
                    ProductErrorCode.PRODUCT_NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success(product);
    }
}
