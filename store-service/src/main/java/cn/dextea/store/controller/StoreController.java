package cn.dextea.store.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.store.dto.CreateStoreDTO;
import cn.dextea.store.dto.SearchStoreDTO;
import cn.dextea.store.dto.UpdateStoreDTO;
import cn.dextea.store.service.StoreService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
public class StoreController {
    @Autowired
    StoreService storeService;

    /**
     * 创建门店
     * @param data {name,province,city,district,address,linkman,phone,openTime}
     */
    @PostMapping("/store")
    public ApiResponse create(@Valid @RequestBody CreateStoreDTO data) {
        return storeService.create(data);
    }

    /**
     * 获取门店详情
     * @param id 门店id
     */
    @GetMapping("/store/{id:\\d+}")
    public ApiResponse getStoreById(@PathVariable Long id) {
        return storeService.getStoreById(id);
    }

    /**
     * 获取门店列表
     * @param current 页码
     * @param size 分页大小
     */
    @PostMapping("/store/search")
    public ApiResponse getStoreList(
            @Valid @Min(value = 1,message = "current不能小于1") @RequestParam(defaultValue = "1") int current,
            @Valid @Min(value = 1,message = "size不能小于1") @RequestParam(defaultValue = "10") int size,
            @Valid @RequestBody SearchStoreDTO filter) {
        return storeService.getStoreList(current, size,filter);
    }

    /**
     * 更新门店运营状态
     * @param id 门店id
     * @param status 运营状态
     */
    @PutMapping("/store/status")
    public ApiResponse updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        return storeService.updateStatus(id, status);
    }

    @PutMapping("/store/{id:\\d+}")
    public ApiResponse update(@PathVariable Long id, @RequestBody UpdateStoreDTO data) {
        return storeService.update(id, data);
    }
}
