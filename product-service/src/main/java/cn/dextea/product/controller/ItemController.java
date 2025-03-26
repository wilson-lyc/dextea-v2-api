package cn.dextea.product.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.item.ItemUpdateDTO;
import cn.dextea.product.dto.item.ItemCreateDTO;
import cn.dextea.product.service.ItemService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
public class ItemController {

    @Resource
    private ItemService itemService;

    /**
     * 创建客制化项目
     * @param id 商品ID
     * @param data 创建配置
     */
    @PostMapping("/product/{id:\\d+}/customize")
    public ApiResponse createItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemCreateDTO data){
        return itemService.createItem(id,data);
    }

    /**
     * 获取客制化项目列表
     * @param productId 商品ID
     */
    @GetMapping("/product/{productId:\\d+}/customize")
    public ApiResponse getItemList(@PathVariable Long productId){
        return itemService.getItemList(productId);
    }

    /**
     * 获取项目详情
     * @param id 客制化项目ID
     */
    @GetMapping("/product/customize/{id}")
    public ApiResponse getItemInfo(@PathVariable Long id) throws NotFoundException {
        return itemService.getItemInfo(id);
    }

    /**
     * 更新项目
     * @param id 项目ID
     * @param data 更新数据
     */
    @PutMapping("/product/customize/{id}")
    public ApiResponse updateItemInfo(
            @PathVariable Long id,
            @Valid @RequestBody ItemUpdateDTO data) throws NotFoundException {
        return itemService.updateItemInfo(id,data);
    }

    @PutMapping("/product/customize/{id}/status")
    public ApiResponse updateItemStatus(
            @PathVariable Long id,
            @RequestParam Integer status) throws NotFoundException {
        return itemService.updateItemStatus(id,status);
    }
}
