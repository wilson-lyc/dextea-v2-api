package cn.dextea.product.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.CustomizeItemEditDTO;
import cn.dextea.product.service.CustomizeItemService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
public class CustomizeItemController {

    @Resource
    private CustomizeItemService customizeItemService;

    /**
     * 创建客制化项目
     * @param id 商品ID
     * @param data 创建配置
     */
    @PostMapping("/product/{id:\\d+}/customize")
    public ApiResponse createCustomizeItem(
            @PathVariable Long id,
            @Valid @RequestBody CustomizeItemEditDTO data){
        return customizeItemService.createItem(id,data);
    }

    /**
     * 查询商品的客制化项目
     * @param id 商品ID
     */
    @GetMapping("/product/{id:\\d+}/customize")
    public ApiResponse getItemList(@PathVariable Long id){
        return customizeItemService.getItemList(id);
    }

    /**
     * 获取客制化项目的基础信息
     * @param id 客制化项目ID
     */
    @GetMapping("/product/customize/{id}/base")
    public ApiResponse getItemBase(@PathVariable Long id){
        return customizeItemService.getItemBase(id);
    }

    /**
     * 获取客制化项目的状态
     * @param id 客制化项目ID
     */
    @GetMapping("/product/customize/{id}/status")
    public ApiResponse getItemGlobalStatus(@PathVariable Long id){
        return customizeItemService.getItemGlobalStatus(id);
    }

    /**
     * 更新客制化项目的基础信息
     * @param id 客制化项目ID
     * @param data 更新数据
     */
    @PutMapping("/product/customize/{id}/base")
    public ApiResponse updateItemBase(
            @PathVariable Long id,
            @Valid @RequestBody CustomizeItemEditDTO data){
        return customizeItemService.updateItemBase(id,data);
    }

    /**
     * 更新客制化项目的全局状态
     * @param id 客制化项目ID
     * @param status 状态
     */
    @PutMapping("/product/customize/{id}/status")
    public ApiResponse updateItemStatus(
            @PathVariable Long id,
            @RequestParam Integer status){
        return customizeItemService.updateItemStatus(id,status);
    }
}
