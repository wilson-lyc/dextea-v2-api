package cn.dextea.product.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.ProductCategoryDTO;
import cn.dextea.product.service.ProductCategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商品类型
 * @author Lai Yongchao
 */
@RestController
@RequestMapping("/product/category")
public class ProductCategoryController {
    @Autowired
    private ProductCategoryService productCategoryService;

    /**
     * 创建商品类型
     * @param data 商品类型数据
     */
    @PostMapping
    public ApiResponse create(@Valid @RequestBody ProductCategoryDTO data) {
        return productCategoryService.create(data);
    }

    /**
     * 更新商品类型
     * @param data 商品类型数据
     */
    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable Long id,@Valid @RequestBody ProductCategoryDTO data) {
        return productCategoryService.update(id,data);
    }

    /**
     * 根据ID获取商品类型
     * @param id 商品类型ID
     */
    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable Long id) {
        return productCategoryService.getById(id);
    }

    /**
     * 获取所有商品类型
     */
    @GetMapping
    public ApiResponse getAll() {
        return productCategoryService.getAll();
    }

    @GetMapping("/option")
    public ApiResponse getOptions() {
        return productCategoryService.getOptions();
    }
}
