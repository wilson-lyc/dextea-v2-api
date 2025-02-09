package cn.dextea.product.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.CreateProductDTO;
import cn.dextea.product.dto.SearchProductDTO;
import cn.dextea.product.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商品
 * @author Lai Yongchao
 */
@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;
    @PostMapping("")
    public ApiResponse create(@Valid @RequestBody CreateProductDTO data) {
        return productService.create(data);
    }
    @GetMapping("/{id:\\d+}")
    public ApiResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping("/search")
    public ApiResponse getProductList(
            @Valid @Min(value = 1,message = "current不能小于1") @RequestParam(defaultValue = "1") int current,
            @Valid @Min(value = 1,message = "size不能小于1") @RequestParam(defaultValue = "10") int size,
            @Valid @RequestBody SearchProductDTO filter
    ){
        return productService.getProductList(current,size,filter);
    }
}
