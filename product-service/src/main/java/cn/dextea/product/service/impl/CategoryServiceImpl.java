package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.dto.OptionDTO;
import cn.dextea.product.dto.category.CategoryDTO;
import cn.dextea.product.mapper.CategoryMapper;
import cn.dextea.product.pojo.Category;
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
        Category category = data.toCategory();
        categoryMapper.insert(category);
        return ApiResponse.success("分类创建成功");
    }

    @Override
    public ApiResponse getCategoryList() {
        List<Category> categories = categoryMapper.selectList(null);
        return ApiResponse.success(JSONObject.of("categories", categories));
    }

    @Override
    public ApiResponse getCategoryById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            return ApiResponse.notFound(String.format("不存在ID=%d的分类",id));
        }
        return ApiResponse.success(JSONObject.of("category", category));
    }

    @Override
    public ApiResponse getCategoryOption() {
        MPJLambdaWrapper<Category> wrapper = new MPJLambdaWrapper<Category>()
                .selectAs(Category::getName, OptionDTO::getLabel)
                .selectAs(Category::getId, OptionDTO::getValue);
        List<OptionDTO> options = categoryMapper.selectJoinList(OptionDTO.class, wrapper);
        return ApiResponse.success(JSONObject.of("options", options));
    }

    @Override
    public ApiResponse updateCategory(Long id, CategoryDTO data) {
        Category category = data.toCategory();
        category.setId(id);
        int num= categoryMapper.updateById(category);
        if (num==0){
            return ApiResponse.notFound(String.format("不存在ID=%d的分类",id));
        }
        return ApiResponse.success("更新成功");
    }
}
