package cn.dextea.product.controller;

import cn.dextea.common.model.common.ApiResponse;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.common.SelectOptionModel;
import cn.dextea.common.model.product.ProductCategoryModel;
import cn.dextea.product.model.category.EditCategoryResponse;
import cn.dextea.product.service.CategoryService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品类型
 * @author Lai Yongchao
 */
@RestController
public class CategoryController {
    @Resource
    private CategoryService categoryService;

    /**
     * 创建商品分类
     * @param data 创建数据
     */
    @PostMapping("/product-category")
    public DexteaApiResponse<Void> createCategory(@Valid @RequestBody EditCategoryResponse data) {
        return categoryService.createCategory(data);
    }

    /**
     * 获取所有商品分类
     */
    @GetMapping("/product-category")
    public DexteaApiResponse<List<ProductCategoryModel>> getCategoryList() {
        return categoryService.getCategoryList();
    }

    /**
     * 获取商品分类基础信息
     * @param id 商品类型ID
     */
    @GetMapping("/product-category/{id}")
    public DexteaApiResponse<ProductCategoryModel> getCategoryDetail(@PathVariable Long id) {
        return categoryService.getCategoryDetail(id);
    }

    /**
     * 获取商品分类选项
     */
    @GetMapping("/product-category/option")
    public DexteaApiResponse<List<SelectOptionModel>> getCategoryOption() {
        return categoryService.getCategoryOption();
    }

    /**
     * 更新商品分类
     *
     * @param data 数据
     */
    @PutMapping("/product-category/{id}")
    public DexteaApiResponse<Void> updateCategory(@PathVariable Long id, @Valid @RequestBody EditCategoryResponse data) {
        return categoryService.updateCategory(id, data);
    }
}