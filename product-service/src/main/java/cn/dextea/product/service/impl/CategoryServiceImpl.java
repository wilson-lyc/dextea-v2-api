package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.dto.OptionDTO;
import cn.dextea.product.dto.category.CategoryDTO;
import cn.dextea.product.mapper.CategoryMapper;
import cn.dextea.common.pojo.ProductCategory;
import cn.dextea.product.service.CategoryService;
import com.alibaba.fastjson2.JSONObject;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public ApiResponse createCategory(CategoryDTO data) {
        ProductCategory productCategory = data.toCategory();
        categoryMapper.insert(productCategory);
        return ApiResponse.success("分类创建成功");
    }

    @Override
    public ApiResponse getCategoryList() {
        List<ProductCategory> categories = categoryMapper.selectList(null);
        return ApiResponse.success(JSONObject.of("categories", categories));
    }

    @Override
    public ApiResponse getCategoryById(Long id) {
        ProductCategory productCategory = categoryMapper.selectById(id);
        if (productCategory == null) {
            return ApiResponse.notFound(String.format("不存在ID=%d的分类",id));
        }
        return ApiResponse.success(JSONObject.of("productCategory", productCategory));
    }

    @Override
    public ApiResponse getCategoryOption() {
        MPJLambdaWrapper<ProductCategory> wrapper = new MPJLambdaWrapper<ProductCategory>()
                .selectAs(ProductCategory::getName, OptionDTO::getLabel)
                .selectAs(ProductCategory::getId, OptionDTO::getValue);
        List<OptionDTO> options = categoryMapper.selectJoinList(OptionDTO.class, wrapper);
        return ApiResponse.success(JSONObject.of("options", options));
    }

    @Override
    public ApiResponse updateCategory(Long id, CategoryDTO data) {
        ProductCategory productCategory = data.toCategory();
        productCategory.setId(id);
        int num= categoryMapper.updateById(productCategory);
        if (num==0){
            return ApiResponse.notFound(String.format("不存在ID=%d的分类",id));
        }
        return ApiResponse.success("更新成功");
    }
}
