package cn.dextea.product.controller;

import cn.dextea.common.code.ProductStatus;
import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.product.ProductCreateDTO;
import cn.dextea.product.dto.product.ProductQueryDTO;
import cn.dextea.product.dto.product.ProductUpdateBaseDTO;
import cn.dextea.product.service.ProductService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
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
    public ApiResponse createProduct(@Valid @RequestBody ProductCreateDTO data){
        return productService.createProduct(data);
    }

    /**
     * 获取商品列表
     * 携带storeId额外返回门店状态
     * @param current 当前页
     * @param size 每页大小
     * @param filter 过滤条件
     * @param storeId 门店ID
     */
    @GetMapping("/product")
    public ApiResponse getProductList(
            @Min(value = 1,message = "current不能小于1") int current,
            @Min(value = 1,message = "size不能小于1") int size,
            @Valid ProductQueryDTO filter,
            @RequestParam(required = false) Long storeId){
        if(Objects.isNull(storeId))
            return productService.getProductList(current, size, filter);
        else
            return productService.getProductList(storeId,current, size, filter);
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
     */
    @GetMapping("/product/{id:\\d+}/base")
    public ApiResponse getProductBaseById(@PathVariable Long id) throws NotFoundException {
        return productService.getProductBase(id);
    }

    /**
     * 获取商品图册
     * @param id 商品ID
     */
    @GetMapping("/product/{id:\\d+}/img")
    public ApiResponse getProductImgById(@PathVariable Long id) throws NotFoundException {
        return productService.getProductImg(id);
    }

    /**
     * 获取商品状态
     * 携带storeId额外返回门店状态
     * @param productId 商品ID
     * @param storeId 门店ID
     */
    @GetMapping("/product/{productId:\\d+}/status")
    public ApiResponse getProductStatus(
            @PathVariable Long productId,
            @RequestParam(required = false) Long storeId) throws NotFoundException {
        if(Objects.isNull(storeId))
            return productService.getProductStatus(productId);
        else
            return productService.getProductStatus(productId,storeId);
    }

    /**
     * 更新商品基础信息
     * @param id 商品ID
     * @param data 数据
     */
    @PutMapping("/product/{id:\\d+}/base")
    public ApiResponse updateProductBase(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateBaseDTO data) throws NotFoundException {
        return productService.updateProductBase(id, data);
    }

    /**
     * 更新商品状态
     * @param productId 商品ID
     * @param storeId 门店ID
     * @param status 全局状态
     */
    @PutMapping("/product/{productId:\\d+}/status")
    public ApiResponse updateProductStatus(
            @PathVariable Long productId,
            @RequestParam(required = false) Long storeId,
            @RequestParam Integer status) throws NotFoundException {
        if(Objects.isNull(storeId))
            return productService.updateProductStatus(productId, status);
        else
            return productService.updateProductStatus(productId,storeId,status);
    }
}
