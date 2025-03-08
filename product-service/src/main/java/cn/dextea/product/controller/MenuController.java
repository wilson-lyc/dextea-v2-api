package cn.dextea.product.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.*;
import cn.dextea.product.service.MenuService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
public class MenuController {
    @Resource
    private MenuService menuService;

    /**
     * 创建菜单
     * @param data {name,description}
     */
    @PostMapping("/menu")
    public ApiResponse create(@Valid @RequestBody MenuCreateDTO data) {
        return menuService.create(data);
    }

    /**
     * 获取菜单列表
     * @param current 当前页
     * @param size  页大小
     * @param filter 查询条件
     */
    @GetMapping("/menu")
    public ApiResponse getList(
            @Min(value = 1,message = "current不能小于1") Integer current,
            @Min(value = 1,message = "size不能小于1") Integer size,
            @Valid MenuQueryDTO filter) {
        return menuService.getList(current,size,filter);
    }

    /**
     * 获取菜单基本信息
     * @param id 菜单id
     */
    @GetMapping("/menu/{id:\\d+}/base")
    public ApiResponse getById(@PathVariable Long id) {
        return menuService.getById(id);
    }

    /**
     * 更新菜单基本信息
     * @param id 菜单id
     * @param data {name,description}
     */
    @PutMapping("/menu/{id:\\d+}/base")
    public ApiResponse update(@PathVariable Long id, @RequestBody MenuBaseUpdateDTO data) {
        return menuService.update(id, data);
    }
}
