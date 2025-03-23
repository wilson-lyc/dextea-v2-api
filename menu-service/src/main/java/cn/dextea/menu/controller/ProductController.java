package cn.dextea.menu.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.menu.service.ProductService;
import com.alibaba.fastjson2.function.ObjBoolConsumer;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
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
     * 绑定商品
     * @param groupId 菜单分组ID
     * @param productId 商品ID
     * @param sort 排序
     */
    @PostMapping("/menu/group/{groupId:\\d+}/product/{productId:\\d+}")
    public ApiResponse menuBindProduct(
            @PathVariable Long groupId,
            @PathVariable Long productId,
            @RequestParam Integer sort){
        return productService.menuBindProduct(groupId,productId,sort);
    }

    /**
     * 解绑绑定商品
     * @param groupId 菜单分组ID
     * @param productId 商品ID
     */
    @DeleteMapping("/menu/group/{groupId:\\d+}/product/{productId:\\d+}")
    public ApiResponse menuUnbindProduct(
            @PathVariable Long groupId,
            @PathVariable Long productId){
        return productService.menuUnbindProduct(groupId,productId);
    }

    /**
     * 获取菜单绑定的商品列表
     * @param groupId 菜单分组ID
     * @param storeId 门店ID
     */
    @GetMapping("/menu/group/{groupId:\\d+}/product")
    public ApiResponse getProductList(
            @PathVariable("groupId") Long groupId,
            @RequestParam(required = false) Long storeId){
        if(Objects.isNull(storeId))
            return productService.getProductList(groupId);
        else
            return productService.getProductList(storeId,groupId);
    }

    /**
     * 获取商品绑定的菜单信息
     * @param groupId 菜单分组ID
     * @param productId 商品ID
     */
    @GetMapping("/menu/group/{groupId:\\d+}/product/{productId:\\d+}")
    public ApiResponse getMenuBindProductInfo(
            @PathVariable("groupId") Long groupId,
            @PathVariable("productId") Long productId){
        return productService.getMenuBindProductInfo(groupId,productId);
    }

    /**
     * 更新菜单商品基础信息
     * @param groupId 菜单分组ID
     * @param productId 商品ID
     * @param sort 排序
     */
    @PutMapping("/menu/group/{groupId:\\d+}/product/{productId:\\d+}")
    public ApiResponse updateMenuBindProductInfo(
            @PathVariable("groupId") Long groupId,
            @PathVariable("productId") Long productId,
            @RequestParam Integer sort){
        return productService.updateMenuBindProductInfo(groupId,productId,sort);
    }
}
