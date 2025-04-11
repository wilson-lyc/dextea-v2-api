package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.product.CustomizeOptionModel;
import cn.dextea.product.model.option.OptionCreateRequest;
import cn.dextea.product.model.option.OptionUpdateRequest;
import cn.dextea.product.service.OptionService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@RestController
public class OptionController {
    @Resource
    private OptionService optionService;

    /**
     * 创建客制化选项
     * @param itemId 项目ID
     * @param data 创建数据
     */
    @PostMapping("/product/customize/{itemId:\\d+}/option")
    @SaCheckPermission("product:customize_option:create")
    public DexteaApiResponse<Void> createOption(
            @PathVariable Long itemId,
            @Valid @RequestBody OptionCreateRequest data) {
        return optionService.createOption(itemId,data);
    }

    /**
     * 获取客制化选项列表
     * 携带storeId则额外返回门店状态
     * @param itemId 项目ID
     * @param storeId 门店ID,非空额外返回门店状态
     */
    @GetMapping("/product/customize/{itemId:\\d+}/option")
    @SaCheckPermission("product:customize_option:read")
    public DexteaApiResponse<List<CustomizeOptionModel>> getOptionList(
            @PathVariable Long itemId,
            @RequestParam(required = false) Long storeId) {
        if(Objects.isNull(storeId))
            return optionService.getOptionList(itemId);
        else
            return optionService.getOptionList(itemId,storeId);
    }

    /**
     * 获取选项详情
     * 只返回全局状态
     * @param id 选项ID
     */
    @GetMapping("/product/customize/option/{id:\\d+}/base")
    @SaCheckPermission("product:customize_option:read")
    public DexteaApiResponse<CustomizeOptionModel> getOptionBase(@PathVariable Long id) throws NotFoundException {
        return optionService.getOptionBase(id);
    }

    /**
     * 获取选项状态
     * 携带storeId则额外返回门店状态
     * @param optionId 选项ID
     * @param storeId 门店ID
     */
    @GetMapping("/product/customize/option/{optionId:\\d+}/status")
    @SaCheckPermission("product:customize_option:read")
    public DexteaApiResponse<CustomizeOptionModel> getOptionStatus(
            @PathVariable Long optionId,
            @RequestParam(required = false) Long storeId) throws NotFoundException {
        if (Objects.isNull(storeId))
            return optionService.getOptionStatus(optionId);
        else
            return optionService.getOptionStatus(optionId, storeId);
    }

    /**
     * 更新选项基础信息
     * @param id 选项ID
     * @param data 更新数据
     */
    @PutMapping("/product/customize/option/{id:\\d+}/base")
    @SaCheckPermission("product:customize_option:update:base")
    public DexteaApiResponse<Void> updateOptionBase(
            @PathVariable Long id,
            @Valid @RequestBody OptionUpdateRequest data) throws NotFoundException {
        return optionService.updateOptionBase(id, data);
    }

    @PutMapping("/product/customize/option/{optionId:\\d+}/status")
    @SaCheckPermission("product:customize_option:update:status")
    public DexteaApiResponse<Void> updateOptionStatus(
            @PathVariable Long optionId,
            @RequestParam(required = false) Long storeId,
            @RequestParam Integer status) throws NotFoundException {
        if (Objects.isNull(storeId))
            return optionService.updateOptionStatus(optionId, status);
        else
            return optionService.updateOptionStatus(optionId, storeId, status);
    }
}
