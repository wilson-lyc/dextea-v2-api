package cn.dextea.menu.controller;

import cn.dextea.common.model.menu.MenuModel;
import cn.dextea.menu.service.InternalService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lai Yongchao
 */
@RestController
public class InternalController {
    @Resource
    private InternalService internalService;

    /**
     * 获取菜单详情
     * @param id 菜单ID
     * @param mode 模式 - active_only：只返回可售和售罄商品 all：返回所有商品
     * @param storeId 门店ID - 非空时额外展示门店状态
     */
    @GetMapping("/menu/internal/getMenuDetail")
    public MenuModel getMenuDetail(@RequestParam  Long id,
                                   @RequestParam(defaultValue = "active_only") String mode,
                                   @RequestParam(required = false) Long storeId) {
        return internalService.getMenuDetail(id,mode,storeId);
    }

    @GetMapping("/menu/internal/isMenuIdValid")
    public boolean isMenuIdValid(@RequestParam Long id) {
        return internalService.isMenuIdValid(id);
    }
}
