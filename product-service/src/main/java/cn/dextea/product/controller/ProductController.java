package cn.dextea.product.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.ProductCreateDTO;
import cn.dextea.product.dto.ProductQueryDTO;
import cn.dextea.product.dto.ProductUpdateBaseDTO;
import cn.dextea.product.dto.ProductUpdateStatusDTO;
import cn.dextea.product.service.ProductService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

/**
 * 商品
 * @author Lai Yongchao
 */
@RestController
public class ProductController {
    @Resource
    private ProductService productService;

    /**
     * 创建商品
     * @param data 商品信息
     */
    @PostMapping("/product")
    public ApiResponse createProduct(@Valid @RequestBody ProductCreateDTO data) {
        return productService.createProduct(data);
    }

    /**
     * 获取商品列表
     * @param current 当前页
     * @param size 每页大小
     * @param filter 过滤条件
     */
    @GetMapping("/product")
    public ApiResponse getProductList(
            @Min(value = 1,message = "current不能小于1") Integer current,
            @Min(value = 1,message = "size不能小于1") Integer size,
            @Valid ProductQueryDTO filter){
        return productService.getProductList(current, size, filter);
    }

    /**
     * 获取商品选项
     * @param status 全局销售状态
     */
    @GetMapping("/product/option")
    public ApiResponse getProductOption(Integer status) {
        return productService.getProductOption(status);
    }

    /**
     * 获取商品基础信息
     * @param id 商品ID
     * @param storeId 门店ID，携带后返回的status是门店的销售状态
     */
    @GetMapping("/product/{id:\\d+}/base")
    public ApiResponse getProductBaseById(
            @PathVariable Long id,
            @RequestParam(required = false) Long storeId) {
        return productService.getProductBase(id);
    }

    /**
     * 获取商品图册
     * @param id 商品ID
     */
    @GetMapping("/product/{id:\\d+}/image")
    public ApiResponse getProductImgById(@PathVariable Long id) {
        return productService.getProductImg(id);
    }

    /**
     *  获取商品的全局状态
     * @param id 商品ID
     */
    @GetMapping("/product/{id:\\d+}/status")
    public ApiResponse getProductGlobalStatus(@PathVariable Long id) {
        return productService.getProductGlobalStatus(id);
    }

    /**
     * 更新商品基础信息
     * @param id 商品ID
     * @param body 更新数据
     */
    @PutMapping("/product/{id:\\d+}/base")
    public ApiResponse updateProductBase(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateBaseDTO body){
        return productService.updateProduct(id, body.toProduct());
    }

    /**
     * 更新商品全局状态
     * @param id 商品ID
     * @param body 更新数据
     */
    @PutMapping("/product/{id:\\d+}/status")
    public ApiResponse updateProductStatus(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateStatusDTO body){
        return productService.updateProduct(id, body.toProduct());
    }
}
