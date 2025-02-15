package cn.dextea.product.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.*;
import cn.dextea.product.service.MenuService;
import cn.dextea.product.service.MenuTypeService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
@RequestMapping("/menu")
public class MenuController {
    @Resource
    private MenuService menuService;
    @Resource
    private MenuTypeService menuTypeService;

    /**
     * 创建菜单
     * @param data {name,description}
     */
    @PostMapping
    public ApiResponse createMenu(@Valid @RequestBody MenuCreateDTO data) {
        return menuService.createMenu(data);
    }

    /**
     * 获取菜单列表
     * @param current 当前页
     * @param size  页大小
     * @param filter 查询条件
     */
    @PostMapping("/search")
    public ApiResponse search(
            @RequestParam(value = "current",defaultValue = "1") int current,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestBody MenuFilterDTO filter) {
        return menuService.getMenuListByFilter(current,size,filter);
    }

    /**
     * 获取菜单基本信息
     * @param id 菜单id
     */
    @GetMapping("/{id:\\d+}/base")
    public ApiResponse getMenuBaseById(@PathVariable Long id) {
        return menuService.getMenuBaseById(id);
    }

    /**
     * 更新菜单基本信息
     * @param id 菜单id
     * @param data {name,description}
     */
    @PutMapping("/{id:\\d+}/base")
    public ApiResponse updateMenuBase(@PathVariable Long id, @RequestBody MenuBaseUpdateDTO data) {
        return menuService.updateMenuBase(id, data);
    }
}
