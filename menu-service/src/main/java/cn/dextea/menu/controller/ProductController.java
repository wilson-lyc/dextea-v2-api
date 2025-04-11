package cn.dextea.menu.controller;

import cn.dextea.common.model.common.ApiResponse;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.menu.MenuProductModel;
import cn.dextea.menu.service.ProductService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@RestController
public class ProductController {
    @Resource
    private ProductService productService;
    @PostMapping("/menu/{menuId:\\d+}/group/{groupId}/product/{productId:\\d+}")
    public DexteaApiResponse<Void> addProduct(
            @PathVariable Long menuId,
            @PathVariable String groupId,
            @PathVariable Long productId,
            @RequestParam Integer sort){
        return productService.addProduct(menuId,groupId,productId,sort);
    }
    @DeleteMapping("/menu/{menuId:\\d+}/group/{groupId}/product/{productId:\\d+}")
    public DexteaApiResponse<Void> deleteProduct(
            @PathVariable Long menuId,
            @PathVariable String groupId,
            @PathVariable Long productId){
        return productService.deleteProduct(menuId,groupId,productId);
    }

    @GetMapping("/menu/{menuId:\\d+}/group/{groupId}/product")
    public DexteaApiResponse<List<MenuProductModel>> getProductList(
            @PathVariable Long menuId,
            @PathVariable String groupId){
        return productService.getProductList(menuId,groupId);
    }

    @GetMapping("/menu/{menuId:\\d+}/group/{groupId}/product/{productId:\\d+}")
    public DexteaApiResponse<MenuProductModel> getProductInfo(
            @PathVariable Long menuId,
            @PathVariable String groupId,
            @PathVariable Long productId){
        return productService.getProductInfo(menuId,groupId,productId);
    }

    @PutMapping("/menu/{menuId:\\d+}/group/{groupId}/product/{productId:\\d+}")
    public DexteaApiResponse<Void> updateProductInfo(
            @PathVariable Long menuId,
            @PathVariable String groupId,
            @PathVariable Long productId,
            @RequestParam Integer sort){
        return productService.updateProductInfo(menuId,groupId,productId,sort);
    }
}
