package controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.service.MenuService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

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
    public ApiResponse createMenu(@Valid @RequestBody MenuCreateDTO data) {
        return menuService.createMenu(data);
    }

    /**
     * 获取菜单列表
     * @param current 当前页
     * @param size  页大小
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
     * 获取菜单基本信息
     * @param id 菜单id
     */
    @GetMapping("/menu/{id:\\d+}/base")
    public ApiResponse getMenuBaseById(@PathVariable Long id) {
        return menuService.getMenuBaseById(id);
    }

    /**
     * 更新菜单基本信息
     * @param id 菜单id
     * @param data {name,description}
     */
    @PutMapping("/menu/{id:\\d+}/base")
    public ApiResponse updateMenu(@PathVariable Long id, @RequestBody MenuBaseUpdateDTO data) {
        return menuService.updateMenu(id, data);
    }

    /**
     * 获取菜单
     * @param id 菜单id
     */
    @GetMapping("/menu/{id:\\d+}")
    public ApiResponse getMenuById(@PathVariable Long id) {
        return menuService.getMenuById(id);
    }
}
