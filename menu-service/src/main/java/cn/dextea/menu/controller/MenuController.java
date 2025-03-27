package cn.dextea.menu.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.menu.dto.menu.MenuCreateDTO;
import cn.dextea.menu.dto.menu.MenuQueryDTO;
import cn.dextea.menu.dto.menu.MenuSendDTO;
import cn.dextea.menu.dto.menu.MenuUpdateBaseDTO;
import cn.dextea.menu.service.MenuService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@RestController
public class MenuController {
    @Resource
    private MenuService menuService;

    @PostMapping("/menu")
    public ApiResponse createMenu(@Valid @RequestBody MenuCreateDTO data){
        return menuService.createMenu(data);
    }

    @GetMapping("/menu")
    public ApiResponse getMenuList(
            @Min(message = "current不能小于1",value = 1) @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            MenuQueryDTO filter){
        return menuService.getMenuList(current,size,filter);
    }

    @GetMapping("/menu/{id:\\d+}")
    public ApiResponse getMenuById(
            @PathVariable Long id,
            @RequestParam(required = false) Long storeId) throws NotFoundException {
        return menuService.getMenuById(id,storeId);
    }

    @GetMapping("/menu/{id:\\d+}/base")
    public ApiResponse getMenuBase(@PathVariable Long id) throws NotFoundException {
        return menuService.getMenuBase(id);
    }

    @PutMapping("/menu/{id:\\d+}/base")
    public ApiResponse updateMenuBase(
            @PathVariable Long id,
            @Valid @RequestBody MenuUpdateBaseDTO data) throws NotFoundException {
        return menuService.updateMenuBase(id,data);
    }

    @PostMapping("/menu/{id:\\d+}/send")
    public ApiResponse bindMenu(
            @PathVariable Long id,
            @RequestBody MenuSendDTO data){
        return menuService.storeBindMenu(id,data.getStoreIds());
    }
}
