package cn.dextea.product.service.impl;

import cn.dextea.common.model.common.ApiResponse;
import cn.dextea.common.feign.ProductFeign;
import cn.dextea.common.model.product.ProductModel;
import cn.dextea.product.mapper.ItemMapper;
import cn.dextea.product.mapper.OptionMapper;
import cn.dextea.product.mapper.ProductMapper;
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
    private ItemMapper itemMapper;
    @Resource
    private OptionMapper optionMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private ProductFeign productFeign;

    @Override
    public ApiResponse getProductInfo(Long productId, Long storeId) throws NotFoundException {
        ProductModel product = productFeign.getProductDetail(productId,storeId);
        if (Objects.isNull(product))
            throw new NotFoundException("商品不存在");
        return ApiResponse.success(JSONObject.of("product",product));
    }
}
