package cn.dextea.product.controller;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.common.ImageModel;
import cn.dextea.common.model.common.SelectOptionModel;
import cn.dextea.common.model.product.ProductModel;
import cn.dextea.product.model.product.ProductCreateRequest;
import cn.dextea.product.model.product.ProductFilter;
import cn.dextea.product.model.product.ProductUpdateBaseRequest;
import cn.dextea.product.service.ProductService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.bouncycastle.dvcs.VPKCRequestBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public DexteaApiResponse<Void> createProduct(@Valid @RequestBody ProductCreateRequest data){
        return productService.createProduct(data);
    }

    /**
     * 获取商品列表
     * 携带storeId额外返回门店状态
     * @param current 当前页
     * @param size 每页大小
     * @param filter 过滤条件
     * @param storeId 门店ID，非空时额外返回门店状态
     */
    @GetMapping("/product")
    public DexteaApiResponse<IPage<ProductModel>> getProductList(
            @Min(value = 1,message = "current不能小于1") int current,
            @Min(value = 1,message = "size不能小于1") int size,
            ProductFilter filter,
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
    public DexteaApiResponse<List<SelectOptionModel>> getProductOption(Integer status) {
        return productService.getProductOption(status);
    }

    /**
     * 获取商品基础信息
     * @param id 商品ID
     */
    @GetMapping("/product/{id:\\d+}/base")
    public DexteaApiResponse<ProductModel> getProductBaseById(@PathVariable Long id){
        return productService.getProductBase(id);
    }

    /**
     * 获取商品图册
     * @param id 商品ID
     */
    @GetMapping("/product/{id:\\d+}/img")
    public DexteaApiResponse<List<ImageModel>> getProductImgById(@PathVariable Long id){
        return productService.getProductImg(id);
    }

    /**
     * 获取商品状态
     * 携带storeId额外返回门店状态
     * @param productId 商品ID
     * @param storeId 门店ID，非空额外返回门店状态
     */
    @GetMapping("/product/{productId:\\d+}/status")
    public DexteaApiResponse<ProductModel> getProductStatus(
            @PathVariable Long productId,
            @RequestParam(required = false) Long storeId){
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
    public DexteaApiResponse<Void> updateProductBase(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateBaseRequest data){
        return productService.updateProductBase(id, data);
    }

    /**
     * 更新商品状态
     * @param productId 商品ID
     * @param storeId 门店ID,非空时更新门店状态
     * @param status 状态
     */
    @PutMapping("/product/{productId:\\d+}/status")
    public DexteaApiResponse<VPKCRequestBuilder> updateProductStatus(
            @PathVariable Long productId,
            @RequestParam(required = false) Long storeId,
            @RequestParam Integer status){
        if(Objects.isNull(storeId))
            return productService.updateProductStatus(productId, status);
        else
            return productService.updateProductStatus(productId,storeId,status);
    }
}
