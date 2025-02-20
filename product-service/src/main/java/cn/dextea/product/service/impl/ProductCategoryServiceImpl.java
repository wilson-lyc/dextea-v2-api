package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.ProductCategoryDTO;
import cn.dextea.product.dto.ProductCategoryOptionDTO;
import cn.dextea.product.mapper.ProductTypeMapper;
import cn.dextea.product.pojo.ProductCategory;
import cn.dextea.product.service.ProductCategoryService;
import com.alibaba.fastjson2.JSONObject;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {
    @Autowired
    private ProductTypeMapper productTypeMapper;

    @Override
    public ApiResponse create(ProductCategoryDTO data) {
        ProductCategory productCategory = data.toProductType();
        productTypeMapper.insert(productCategory);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse update(Long id, ProductCategoryDTO data) {
        ProductCategory productCategory = data.toProductType();
        productCategory.setId(id);
        int num=productTypeMapper.updateById(productCategory);
        if (num==0){
            String msg=String.format("不存在ID=%d的分类",id);
            return ApiResponse.notFound(msg);
        }
        return ApiResponse.success();
    }

    @Override
    public ApiResponse getById(Long id) {
        ProductCategory productCategory = productTypeMapper.selectById(id);
        if (productCategory == null) {
            String msg=String.format("不存在ID=%d的分类",id);
            return ApiResponse.notFound(msg);
        }
        return ApiResponse.success(JSONObject.of("category", productCategory));
    }

    @Override
    public ApiResponse getAll() {
        List<ProductCategory> productCategories = productTypeMapper.selectList(null);
        return ApiResponse.success(JSONObject.of("categories", productCategories));
    }

    @Override
    public ApiResponse getOptions() {
        MPJLambdaWrapper<ProductCategory> wrapper = new MPJLambdaWrapper<ProductCategory>()
                .selectAs(ProductCategory::getName, "label")
                .selectAs(ProductCategory::getId, "value");
        List<ProductCategoryOptionDTO> options = productTypeMapper.selectJoinList(ProductCategoryOptionDTO.class, wrapper);
        return ApiResponse.success(JSONObject.of("options", options));
    }
}
