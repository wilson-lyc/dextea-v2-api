package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.CreateProductDTO;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.pojo.Product;
import cn.dextea.product.service.ProductService;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Lai Yongchao
 */
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductMapper productMapper;
    @Override
    public ApiResponse create(CreateProductDTO data) {
        Product product = data.toProduct();
        productMapper.insert(product);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse getProductById(Long id) {
        Product product = productMapper.selectById(id);
        if(product == null) {
            String msg = String.format("商品不存在，id=%d", id);
            return ApiResponse.notFound(msg);
        }
        return ApiResponse.success(JSONObject.of("product", product));
    }

}
