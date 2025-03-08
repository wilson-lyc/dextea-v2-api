package cn.dextea.product.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.ProductCreateDTO;
import cn.dextea.product.dto.ProductQueryDTO;
import cn.dextea.product.dto.ProductUpdateDTO;
import cn.dextea.product.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 商品
 * @author Lai Yongchao
 */
@RestController
public class ProductController {
    @Autowired
    private ProductService productService;

    /**
     * 创建商品
     * @param data {name,description,price,typeId}
     */
    @PostMapping("/product")
    public ApiResponse createProduct(@Valid @RequestBody ProductCreateDTO data) {
        return productService.createProduct(data);
    }

    /**
     * 根据ID查询商品
     * @param id 商品ID
     */
    @GetMapping("/product/{id:\\d+}/base")
    public ApiResponse getProductBaseById(@PathVariable Long id) {
        return productService.getProductBaseById(id);
    }

    /**
     * 获取商品列表
     */
    @GetMapping("/product")
    public ApiResponse getProductList(
            @Min(value = 1,message = "current不能小于1") Integer current,
            @Min(value = 1,message = "size不能小于1") Integer size,
            @Valid ProductQueryDTO filter){
        return productService.getProductList(current, size,filter);
    }

    /**
     * 更新商品
     * @param id 商品ID
     */
    @PutMapping("/product/{id:\\d+}/base")
    public ApiResponse updateProduct(@PathVariable Long id,@Valid @RequestBody ProductUpdateDTO data) {
        return productService.updateProduct(id, data);
    }

    /**
     * 上传商品封面
     * @param id 商品id
     * @param file 封面图
     */
    @PostMapping(value = "/cover", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse> uploadCover(@RequestParam Long id, @RequestPart MultipartFile file) {
        return productService.uploadCover(id, file);
    }

    /**
     * 获取商品穿梭框选项
     */
    @GetMapping("/option")
    public ApiResponse getProductOption(Integer status) {
        return productService.getProductOption(status);
    }

    @GetMapping("/product/{id:\\d+}")
    public ApiResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }
}
