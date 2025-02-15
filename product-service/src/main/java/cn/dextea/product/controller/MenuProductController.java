package cn.dextea.product.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.MenuBindProductDTO;
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
@RequestMapping("/menu/product")
public class MenuProductController {
    @Resource
    private MenuProductService menuProductService;

    /**
     * 绑定菜单商品
     * @param data {typeId, productId}
     */
    @PostMapping
    public ApiResponse bindProduct(@Valid @RequestBody MenuProductCreateDTO data){
        return menuProductService.createMenuProduct(data);
    }

    /**
     * 解绑绑定商品
     * @param typeId 菜单分类ID
     * @param productId 商品ID
     */
    @DeleteMapping
    public ApiResponse unbindProduct(@RequestParam("typeId") Long typeId, @RequestParam("productId") Long productId){
        return menuProductService.unbindProduct(typeId,productId);
    }

    /**
     * 根据TypeId获取商品列表
     * @param typeId 菜单分类ID
     */
    @GetMapping
    public ApiResponse getMenuProductListByTypeId(@RequestParam("typeId") Long typeId){
        return menuProductService.getMenuProductListByTypeId(typeId);
    }

    @GetMapping("/base")
    public ApiResponse getMenuProductBase(@RequestParam("typeId") Long typeId, @RequestParam("productId") Long productId){
        return menuProductService.getMenuProductBase(typeId,productId);
    }

    @PutMapping("/base")
    public ApiResponse updateMenuProductBase(
            @RequestParam("typeId") Long typeId,
            @RequestParam("productId") Long productId,
            @Valid @RequestBody MenuProductUpdateDTO data){
        return menuProductService.updateMenuProductBase(typeId,productId,data);
    }
}
