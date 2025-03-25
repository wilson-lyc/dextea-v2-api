package cn.dextea.store.controller;

import cn.dextea.common.code.StoreStatus;
import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.pojo.Store;
import cn.dextea.store.dto.*;
import cn.dextea.store.service.StoreService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
public class StoreController {
    @Resource
    StoreService storeService;

    /**
     * 创建门店
     * @param data 门店信息
     */
    @PostMapping("/store")
    public ApiResponse createStore(@Valid @RequestBody StoreCreateDTO data) {
        return storeService.createStore(data);
    }

    /**
     * 获取门店列表
     * @param current 页码
     * @param size 分页大小
     * @param filter 过滤条件
     */
    @GetMapping("/store")
    public ApiResponse getStoreList(
            @Valid @Min(value = 1,message = "current不能小于1") @RequestParam(defaultValue = "1") int current,
            @Valid @Min(value = 1,message = "size不能小于1") @RequestParam(defaultValue = "10") int size,
            @Valid StoreFilter filter) {
        return storeService.getStoreList(current,size,filter);
    }

    /**
     * 获取门店选项
     * @param status 状态
     */
    @GetMapping("/store/option")
    public ApiResponse getStoreOption(
            @RequestParam(required = false) Integer status) {
        return storeService.getStoreOption(status);
    }

    /**
     * 获取门店基础信息
     * @param id 门店ID
     */
    @GetMapping("/store/{id:\\d+}/base")
    public ApiResponse getStoreBase(@PathVariable Long id) throws NotFoundException {
        return storeService.getStoreBase(id);
    }

    /**
     * 获取门店许可证
     * @param id 门店ID
     */
    @GetMapping("/store/{id:\\d+}/license")
    public ApiResponse getStoreLicense(@PathVariable Long id) throws NotFoundException {
        return storeService.getStoreLicense(id);
    }

    /**
     * 获取门店状态
     * @param id 门店id
     */
    @GetMapping("/store/{id:\\d+}/status")
    public ApiResponse getStoreStatus(@PathVariable Long id) throws NotFoundException {
        return storeService.getStoreStatus(id);
    }

    /**
     * 获取门店位置
     * @param id 门店id
     */
    @GetMapping("/store/{id:\\d+}/location")
    public ApiResponse getStoreLocation(@PathVariable Long id) throws NotFoundException {
        return storeService.getStoreLocation(id);
    }

    /**
     * 更新门店基本信息
     * @param id 门店id
     * @param data 门店基础信息
     */
    @PutMapping("/store/{id:\\d+}/base")
    public ApiResponse updateStoreBase(@PathVariable Long id, @RequestBody StoreUpdateBaseDTO data) throws NotFoundException {
        return storeService.updateStoreBase(id, data);
    }

    /**
     * 更新门店位置
     * @param id 门店id
     * @param body 经度,纬度
     * @param latitude 纬度
     */
    @PutMapping("/store/{id:\\d+}/location")
    public ApiResponse updateStoreLocation(
            @PathVariable Long id,
            @RequestBody StoreUpdateLocationDTO body,
            @RequestParam Double latitude) throws NotFoundException {
        return storeService.updateStoreLocation(id, body);
    }

    /**
     * 更新门店状态
     * @param id 门店id
     * @param status 状态
     */
    @PutMapping("/store/{id:\\d+}/status")
    public ApiResponse updateStoreStatus(
            @PathVariable Long id,
            @RequestParam Integer status) throws NotFoundException {
        return storeService.updateStoreStatus(id, status);
    }
}
