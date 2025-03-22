package cn.dextea.product.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.category.CategoryDTO;
import cn.dextea.product.service.CategoryService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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
    public ApiResponse createCategory(@Valid @RequestBody CategoryDTO data) {
        return categoryService.createCategory(data);
    }

    /**
     * 获取所有商品分类
     */
    @GetMapping("/product-category")
    public ApiResponse getCategoryList() {
        return categoryService.getCategoryList();
    }

    /**
     * 获取商品分类基础信息
     * @param id 商品类型ID
     */
    @GetMapping("/product-category/{id}")
    public ApiResponse getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    /**
     * 获取商品分类选项
     */
    @GetMapping("/product-category/option")
    public ApiResponse getCategoryOption() {
        return categoryService.getCategoryOption();
    }

    /**
     * 更新商品分类
     * @param data 数据
     */
    @PutMapping("/product-category/{id}")
    public ApiResponse updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO data) {
        return categoryService.updateCategory(id, data);
    }
}