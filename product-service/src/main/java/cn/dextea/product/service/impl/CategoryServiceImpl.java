package cn.dextea.product.service.impl;

import cn.dextea.common.model.common.ApiResponse;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.common.SelectOptionModel;
import cn.dextea.common.model.product.ProductCategoryModel;
import cn.dextea.product.code.ProductErrorCode;
import cn.dextea.product.model.category.EditCategoryResponse;
import cn.dextea.product.mapper.CategoryMapper;
import cn.dextea.product.pojo.ProductCategory;
import cn.dextea.product.service.CategoryService;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public DexteaApiResponse<Void> createCategory(EditCategoryResponse data) {
        ProductCategory productCategory = data.toCategory();
        categoryMapper.insert(productCategory);
        return DexteaApiResponse.success();
    }

    @Override
    public DexteaApiResponse<List<ProductCategoryModel>> getCategoryList() {
        MPJLambdaWrapper<ProductCategory> wrapper=new MPJLambdaWrapper<ProductCategory>()
                .selectAsClass(ProductCategory.class,ProductCategoryModel.class);
        List<ProductCategoryModel> list = categoryMapper.selectJoinList(ProductCategoryModel.class,wrapper);
        return DexteaApiResponse.success(list);
    }

    @Override
    public DexteaApiResponse<ProductCategoryModel> getCategoryDetail(Long id) {
        MPJLambdaWrapper<ProductCategory> wrapper=new MPJLambdaWrapper<ProductCategory>()
                .selectAsClass(ProductCategory.class,ProductCategoryModel.class)
                .eq(ProductCategory::getId,id);
        ProductCategoryModel category = categoryMapper.selectJoinOne(ProductCategoryModel.class,wrapper);
        if (Objects.isNull(category)) {
            return DexteaApiResponse.notFound(ProductErrorCode.CATEGORY_NOT_FOUND.getCode(),
                    ProductErrorCode.CATEGORY_NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success(category);
    }

    @Override
    public DexteaApiResponse<List<SelectOptionModel>> getCategoryOption() {
        MPJLambdaWrapper<ProductCategory> wrapper = new MPJLambdaWrapper<ProductCategory>()
                .selectAs(ProductCategory::getName, SelectOptionModel::getLabel)
                .selectAs(ProductCategory::getId, SelectOptionModel::getValue);
        List<SelectOptionModel> options = categoryMapper.selectJoinList(SelectOptionModel.class, wrapper);
        return DexteaApiResponse.success(options);
    }

    @Override
    public DexteaApiResponse<Void> updateCategory(Long id, EditCategoryResponse data) {
        ProductCategory productCategory = data.toCategory();
        productCategory.setId(id);
        if (categoryMapper.updateById(productCategory)==0){
            return DexteaApiResponse.notFound(ProductErrorCode.CATEGORY_NOT_FOUND.getCode(),
                    ProductErrorCode.CATEGORY_NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success();
    }
}
