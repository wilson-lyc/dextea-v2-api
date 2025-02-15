package cn.dextea.product.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.ProductCreateDTO;
import cn.dextea.product.dto.SearchProductDTO;
import cn.dextea.product.dto.UpdateProductDTO;
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
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    /**
     * 创建商品
     * @param data {name,description,price,typeId}
     */
    @PostMapping
    public ApiResponse create(@Valid @RequestBody ProductCreateDTO data) {
        return productService.create(data);
    }

    /**
     * 根据ID查询商品
     * @param id 商品ID
     */
    @GetMapping("/{id:\\d+}")
    public ApiResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    /**
     * 获取商品列表
     * @param current   页码
     * @param size      页大小
     * @param filter    过滤器
     */
    @PostMapping("/search")
    public ApiResponse getProductList(
            @Valid @Min(value = 1,message = "current不能小于1") @RequestParam(defaultValue = "1") int current,
            @Valid @Min(value = 1,message = "size不能小于1") @RequestParam(defaultValue = "10") int size,
            @Valid @RequestBody SearchProductDTO filter){
        return productService.getProductListByFilter(current,size,filter);
    }

    /**
     * 更新商品
     * @param id 商品ID
     */
    @PutMapping("/{id:\\d+}")
    public ApiResponse updateProduct(@PathVariable Long id,@Valid @RequestBody UpdateProductDTO data) {
        return null;
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
    @GetMapping("/transferOption")
    public ApiResponse getProductTransferOption(Integer status) {
        return productService.getProductTransferOption(status);
    }
}
