package cn.dextea.product.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.ProductTypeDTO;
import cn.dextea.product.service.ProductTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商品类型
 * @author Lai Yongchao
 */
@RestController
@RequestMapping("/productType")
public class ProductTypeController {
    @Autowired
    private ProductTypeService productTypeService;

    /**
     * 创建商品类型
     * @param data 商品类型数据
     */
    @PostMapping
    public ApiResponse create(@Valid @RequestBody ProductTypeDTO data) {
        return productTypeService.create(data);
    }

    /**
     * 更新商品类型
     * @param data 商品类型数据
     */
    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable Long id,@Valid @RequestBody ProductTypeDTO data) {
        return productTypeService.update(id,data);
    }

    /**
     * 根据ID获取商品类型
     * @param id 商品类型ID
     */
    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable Long id) {
        return productTypeService.getById(id);
    }

    /**
     * 获取所有商品类型
     */
    @GetMapping
    public ApiResponse getAll() {
        return productTypeService.getAll();
    }
}
