package cn.dextea.product.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.MenuTypeCreateDTO;
import cn.dextea.product.dto.MenuTypeUpdateDTO;
import cn.dextea.product.service.MenuTypeService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
@RequestMapping("/menu/type")
public class MenuTypeController {
    @Resource
    private MenuTypeService menuTypeService;

    /**
     * 创建菜单分类
     * @param data {name}
     */
    @PostMapping
    public ApiResponse createMenuType(@Valid @RequestBody MenuTypeCreateDTO data){
        return menuTypeService.createMenuType(data);
    }

    /**
     * 获取菜单分类的基础信息
     * @param id 菜单分类id
     */
    @GetMapping("/{id:\\d+}")
    public ApiResponse getMenuTypeBaseById(@PathVariable Long id){
        return menuTypeService.getMenuTypeBaseById(id);
    }

    /**
     * 更新菜单分类的基础信息
     * @param id 菜单分类id
     * @param data {name}
     */
    @PutMapping("/{id:\\d+}")
    public ApiResponse updateMenuTypeBase(@PathVariable Long id, @Valid @RequestBody MenuTypeUpdateDTO data){
        return menuTypeService.updateMenuTypeBaseById(id, data);
    }

    /**
     * 根据MenuId获取分类列表
     * @param menuId 菜单id
     */
    @GetMapping
    public ApiResponse getMenuTypeList(@RequestParam("menuId") Long menuId){
        return menuTypeService.getMenuTypeListByMenuId(menuId);
    }
}
