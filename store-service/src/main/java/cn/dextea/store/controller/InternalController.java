package cn.dextea.store.controller;

import cn.dextea.store.service.InternalService;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.annotation.Resource;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
public class InternalController {
    @Resource
    private InternalService internalService;
    @GetMapping("/store/internal/isStoreIdValid")
    public boolean isStoreIdValid(@RequestParam Long id) {
        return internalService.isStoreIdValid(id);
    }
    @GetMapping("/store/internal/getStoreName")
    public String getStoreName(@RequestParam Long id) throws IllegalArgumentException {
        return internalService.getStoreName(id);
    }

    @PutMapping("/store/internal/storeBindMenu")
    public boolean storeBindMenu(
            @RequestParam Long storeId,
            @RequestParam Long menuId) throws NotFoundException {
        return internalService.storeBindMenu(storeId, menuId);
    }

    @GetMapping("/store/internal/getStoreMenuId")
    public Long getStoreMenuId(@RequestParam Long id){
        return internalService.getStoreMenuId(id);
    }

    @GetMapping("/store/internal/getStoreDistance")
    public Double getStoreDistance(@RequestParam Long storeId,@RequestParam Double longitude, @RequestParam Double latitude){
        return internalService.getStoreDistance(storeId,longitude, latitude);
    }
}
