package cn.dextea.product.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.MenuProductCreateDTO;
import cn.dextea.product.dto.MenuProductUpdateDTO;
import cn.dextea.product.service.MenuProductService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
public class MenuProductController {
    @Resource
    private MenuProductService menuProductService;

    /**
     * 绑定菜单商品
     * @param data {typeId, productId}
     */
    @PostMapping("/menu/product")
    public ApiResponse menuBindProduct(@Valid @RequestBody MenuProductCreateDTO data){
        return menuProductService.menuBindProduct(data);
    }

    /**
     * 解绑绑定商品
     * @param groupId 菜单分组ID
     * @param productId 商品ID
     */
    @DeleteMapping("/menu/product")
    public ApiResponse menuUnbindProduct(
            @RequestParam("groupId") Long groupId,
            @RequestParam("productId") Long productId){
        return menuProductService.menuUnbindProduct(groupId,productId);
    }

    /**
     * 获取同分组的商品列表
     * @param id 菜单分组ID
     */
    @GetMapping("/menu/group/{id:\\d+}/product")
    public ApiResponse getProductsByGroupId(@PathVariable Long id){
        return menuProductService.getProductsByGroupId(id);
    }

    /**
     * 获取商品绑定的菜单信息
     * @param groupId 菜单分组ID
     * @param productId 商品ID
     */
    @GetMapping("/menu/product")
    public ApiResponse getMenuBindProductInfo(
            @RequestParam("groupId") Long groupId,
            @RequestParam("productId") Long productId){
        return menuProductService.getMenuBindProductInfo(groupId,productId);
    }

    /**
     * 更新菜单商品基础信息
     * @param groupId 菜单分组ID
     * @param productId 商品ID
     * @param data {name, price, image}
     */
    @PutMapping("/menu/product")
    public ApiResponse updateMenuBindProductInfo(
            @RequestParam("groupId") Long groupId,
            @RequestParam("productId") Long productId,
            @Valid @RequestBody MenuProductUpdateDTO data){
        return menuProductService.updateMenuBindProductInfo(groupId,productId,data);
    }
}
