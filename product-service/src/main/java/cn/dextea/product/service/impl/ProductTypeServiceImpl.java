package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.EditProductTypeDTO;
import cn.dextea.product.dto.ProductTypeOptionDTO;
import cn.dextea.product.mapper.ProductTypeMapper;
import cn.dextea.product.pojo.ProductType;
import cn.dextea.product.service.ProductTypeService;
import com.alibaba.fastjson2.JSONObject;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Service
public class ProductTypeServiceImpl implements ProductTypeService {
    @Autowired
    private ProductTypeMapper productTypeMapper;

    @Override
    public ApiResponse create(EditProductTypeDTO data) {
        ProductType productType = data.toProductType();
        productTypeMapper.insert(productType);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse update(Long id, EditProductTypeDTO data) {
        ProductType productType = data.toProductType();
        productType.setId(id);
        int num=productTypeMapper.updateById(productType);
        if (num==0){
            String msg=String.format("商品类型不存在，ID=%d",id);
            return ApiResponse.notFound(msg);
        }
        return ApiResponse.success();
    }

    @Override
    public ApiResponse getById(Long id) {
        ProductType productType = productTypeMapper.selectById(id);
        if (productType == null) {
            String msg = String.format("商品类型不存在，id=%d", id);
            return ApiResponse.notFound(msg);
        }
        return ApiResponse.success(JSONObject.of("productType", productType));
    }

    @Override
    public ApiResponse getAll() {
        List<ProductType> productTypes = productTypeMapper.selectList(null);
        return ApiResponse.success(JSONObject.of("productTypes", productTypes));
    }

    @Override
    public ApiResponse getSelectOptions() {
        MPJLambdaWrapper<ProductType> wrapper = new MPJLambdaWrapper<ProductType>()
                .selectAs(ProductType::getName, "label")
                .selectAs(ProductType::getId, "value");
        List<ProductTypeOptionDTO> productTypes = productTypeMapper.selectJoinList(ProductTypeOptionDTO.class, wrapper);
        return ApiResponse.success(JSONObject.of("options", productTypes));
    }
}
