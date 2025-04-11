package cn.dextea.menu.controller;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.menu.MenuModel;
import cn.dextea.menu.model.menu.*;
import cn.dextea.menu.service.MenuService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
public class MenuController {
    @Resource
    private MenuService menuService;

    @PostMapping("/menu")
    public DexteaApiResponse<Void> createMenu(@Valid @RequestBody MenuCreateRequest data){
        return menuService.createMenu(data);
    }

    @GetMapping("/menu")
    public DexteaApiResponse<IPage<MenuModel>> getMenuList(
            @Min(message = "current不能小于1",value = 1) @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            MenuFilter filter){
        return menuService.getMenuList(current,size,filter);
    }

    @GetMapping("/menu/{id:\\d+}")
    public DexteaApiResponse<MenuModel> getMenuByDetail(
            @PathVariable Long id,
            @RequestParam(required = false) Long storeId){
        return menuService.getMenuDetail(id,storeId);
    }

    @GetMapping("/menu/{id:\\d+}/base")
    public DexteaApiResponse<MenuModel> getMenuBase(@PathVariable Long id){
        return menuService.getMenuBase(id);
    }

    @PutMapping("/menu/{id:\\d+}/base")
    public DexteaApiResponse<Void> updateMenuBase(
            @PathVariable Long id,
            @Valid @RequestBody MenuUpdateBaseRequest data){
        return menuService.updateMenuBase(id,data);
    }

    @PostMapping("/menu/{id:\\d+}/send")
    public DexteaApiResponse<MenuBindResponse> bindMenu(
            @PathVariable Long id,
            @RequestBody MenuBindRequest data){
        return menuService.storeBindMenu(id,data.getStoreIds());
    }
}
