package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.product.CustomizeItemModel;
import cn.dextea.product.model.item.ItemUpdateRequest;
import cn.dextea.product.model.item.ItemCreateRequest;
import cn.dextea.product.service.ItemService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @SaCheckPermission("product:customize_item:create")
    public DexteaApiResponse<Void> createItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemCreateRequest data){
        return itemService.createItem(id,data);
    }

    /**
     * 获取客制化项目列表
     * @param productId 商品ID
     */
    @GetMapping("/product/{productId:\\d+}/customize")
    @SaCheckPermission("product:customize_item:read")
    public DexteaApiResponse<List<CustomizeItemModel>> getItemList(@PathVariable Long productId){
        return itemService.getItemList(productId);
    }

    /**
     * 获取项目详情
     * @param id 客制化项目ID
     */
    @GetMapping("/product/customize/{id}")
    @SaCheckPermission("product:customize_item:read")
    public DexteaApiResponse<CustomizeItemModel> getItemDetail(@PathVariable Long id){
        return itemService.getItemDetail(id);
    }

    /**
     * 更新项目
     * @param id 项目ID
     * @param data 更新数据
     */
    @PutMapping("/product/customize/{id}")
    @SaCheckPermission("product:customize_item:update")
    public DexteaApiResponse<Void> updateItemDetail(
            @PathVariable Long id,
            @Valid @RequestBody ItemUpdateRequest data) {
        return itemService.updateItemDetail(id,data);
    }

    @PutMapping("/product/customize/{id}/status")
    @SaCheckPermission("product:customize_item:update")
    public DexteaApiResponse<Void> updateItemStatus(
            @PathVariable Long id,
            @RequestParam Integer status){
        return itemService.updateItemStatus(id,status);
    }
}
