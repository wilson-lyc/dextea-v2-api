package cn.dextea.menu.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.menu.service.ProductService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
public class ProductController {
    @Resource
    private ProductService productService;
    @PostMapping("/menu/{menuId:\\d+}/group/{groupId}/product/{productId:\\d+}")
    public ApiResponse addProduct(
            @PathVariable Long menuId,
            @PathVariable String groupId,
            @PathVariable Long productId,
            @RequestParam Integer sort){
        return productService.addProduct(menuId,groupId,productId,sort);
    }
    @DeleteMapping("/menu/{menuId:\\d+}/group/{groupId}/product/{productId:\\d+}")
    public ApiResponse deleteProduct(
            @PathVariable Long menuId,
            @PathVariable String groupId,
            @PathVariable Long productId){
        return productService.deleteProduct(menuId,groupId,productId);
    }

    @GetMapping("/menu/{menuId:\\d+}/group/{groupId}/product")
    public ApiResponse getProductList(
            @PathVariable Long menuId,
            @PathVariable String groupId){
        return productService.getProductList(menuId,groupId);
    }

    @GetMapping("/menu/{menuId:\\d+}/group/{groupId}/product/{productId:\\d+}")
    public ApiResponse getProductInfo(
            @PathVariable Long menuId,
            @PathVariable String groupId,
            @PathVariable Long productId){
        return productService.getProductInfo(menuId,groupId,productId);
    }

    @PutMapping("/menu/{menuId:\\d+}/group/{groupId}/product/{productId:\\d+}")
    public ApiResponse updateProductInfo(
            @PathVariable Long menuId,
            @PathVariable String groupId,
            @PathVariable Long productId,
            @RequestParam Integer sort){
        return productService.updateProductInfo(menuId,groupId,productId,sort);
    }
}
