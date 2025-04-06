package cn.dextea.product.controller;

import cn.dextea.common.model.common.ApiResponse;
import cn.dextea.product.dto.option.OptionCreateDTO;
import cn.dextea.product.dto.option.OptionUpdateDTO;
import cn.dextea.product.service.OptionService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;

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
    public ApiResponse createOption(
            @PathVariable Long itemId,
            @Valid @RequestBody OptionCreateDTO data) {
        return optionService.createOption(itemId,data);
    }

    /**
     * 获取客制化选项列表
     * 携带storeId则额外返回门店状态
     * @param itemId 项目ID
     * @param storeId 门店ID
     */
    @GetMapping("/product/customize/{itemId:\\d+}/option")
    public ApiResponse getOptionList(
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
    public ApiResponse getOptionBase(@PathVariable Long id) throws NotFoundException {
        return optionService.getOptionBase(id);
    }

    /**
     * 获取选项状态
     * 携带storeId则额外返回门店状态
     * @param optionId 选项ID
     * @param storeId 门店ID
     */
    @GetMapping("/product/customize/option/{optionId:\\d+}/status")
    public ApiResponse getOptionGlobalStatus(
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
    public ApiResponse updateOptionBase(
            @PathVariable Long id,
            @Valid @RequestBody OptionUpdateDTO data) throws NotFoundException {
        return optionService.updateOptionBase(id, data);
    }

    @PutMapping("/product/customize/option/{optionId:\\d+}/status")
    public ApiResponse updateOptionStatus(
            @PathVariable Long optionId,
            @RequestParam(required = false) Long storeId,
            @RequestParam Integer status) throws NotFoundException {
        if (Objects.isNull(storeId))
            return optionService.updateOptionStatus(optionId, status);
        else
            return optionService.updateOptionStatus(optionId, storeId, status);
    }
}
