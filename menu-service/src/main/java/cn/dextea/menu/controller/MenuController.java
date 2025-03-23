package cn.dextea.menu.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.menu.dto.MenuEditDTO;
import cn.dextea.menu.dto.MenuQueryDTO;
import cn.dextea.menu.service.MenuService;
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
     * @param data 数据
     */
    @PostMapping("/menu")
    public ApiResponse createMenu(@Valid @RequestBody MenuEditDTO data) {
        return menuService.createMenu(data);
    }

    /**
     * 获取菜单列表
     * @param current 当前页码
     * @param size 分页大小
     * @param filter 查询条件
     */
    @GetMapping("/menu")
    public ApiResponse getMenuList(
            @Min(value = 1,message = "current不能小于1") Integer current,
            @Min(value = 1,message = "size不能小于1") Integer size,
            @Valid MenuQueryDTO filter) {
        return menuService.getMenuList(current,size,filter);
    }

    /**
     * 获取菜单详情
     * @param id 菜单ID
     */
    @GetMapping("/menu/{id:\\d+}")
    public ApiResponse getMenuIno(@PathVariable Long id) {
        return menuService.getMenuInfo(id);
    }

    /**
     * 更新菜单详情
     * @param id 菜单id
     * @param data 数据
     */
    @PutMapping("/menu/{id:\\d+}")
    public ApiResponse updateMenuInfo(
            @PathVariable Long id,
            @RequestBody MenuEditDTO data) {
        return menuService.updateMenuInfo(id, data);
    }
}
