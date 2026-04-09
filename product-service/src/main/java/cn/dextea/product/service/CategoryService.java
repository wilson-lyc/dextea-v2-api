package cn.dextea.product.service;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.common.SelectOptionModel;
import cn.dextea.common.model.product.ProductCategoryModel;
import cn.dextea.product.model.category.EditCategoryResponse;

import java.util.List;

/**
 * @author Lai Yongchao
 */
public interface CategoryService {
    DexteaApiResponse<Void> createCategory(EditCategoryResponse data);
    DexteaApiResponse<List<ProductCategoryModel>> getCategoryList();
    DexteaApiResponse<ProductCategoryModel> getCategoryDetail(Long id);
    DexteaApiResponse<List<SelectOptionModel>> getCategoryOption();
    DexteaApiResponse<Void> updateCategory(Long id, EditCategoryResponse data);

}
