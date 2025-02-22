package cn.dextea.product.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.MenuGroupCreateDTO;
import cn.dextea.product.dto.MenuTypeUpdateDTO;
import cn.dextea.product.service.MenuGroupService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
public class MenuGroupController {
    @Resource
    private MenuGroupService menuGroupService;

    /**
     * 创建菜单分组
     * @param data {name}
     */
    @PostMapping("/menu/group")
    public ApiResponse create(@Valid @RequestBody MenuGroupCreateDTO data){
        return menuGroupService.create(data);
    }

    /**
     * 获取菜单分组的基础信息
     * @param id 菜单分组id
     */
    @GetMapping("/menu/group/{id:\\d+}")
    public ApiResponse getById(@PathVariable Long id){
        return menuGroupService.getById(id);
    }

    /**
     * 更新菜单分组的基础信息
     * @param id 菜单分组id
     * @param data {name}
     */
    @PutMapping("/menu/group/{id:\\d+}")
    public ApiResponse update(@PathVariable Long id, @Valid @RequestBody MenuTypeUpdateDTO data){
        return menuGroupService.update(id, data);
    }

    /**
     * 根据MenuId获取分组列表
     * @param id 菜单id
     */
    @GetMapping("/menu/{id:\\d+}/group")
    public ApiResponse getList(@PathVariable Long id){
        return menuGroupService.getList(id);
    }
}
