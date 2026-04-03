package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CreateMenuRequest;
import cn.dextea.product.dto.request.MenuPageQueryRequest;
import cn.dextea.product.dto.request.UpdateMenuRequest;
import cn.dextea.product.dto.response.CreateMenuResponse;
import cn.dextea.product.dto.response.MenuDetailResponse;
import cn.dextea.product.service.MenuAdminService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/admin/menus")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class MenuAdminController {

    private final MenuAdminService menuAdminService;

    /**
     * 创建菜单
     * @param request 创建菜单请求参数
     * @return 创建成功的菜单信息
     */
    @PostMapping
    public ApiResponse<CreateMenuResponse> createMenu(@Valid @RequestBody CreateMenuRequest request) {
        return menuAdminService.createMenu(request);
    }

    /**
     * 分页查询菜单列表
     * @param request 菜单分页查询请求参数
     * @return 菜单分页数据
     */
    @GetMapping
    public ApiResponse<IPage<MenuDetailResponse>> getMenuPage(@Valid MenuPageQueryRequest request) {
        return menuAdminService.getMenuPage(request);
    }

    /**
     * 查询菜单详情
     * @param id 菜单ID
     * @return 菜单详情（含分组及商品ID列表）
     */
    @GetMapping("/{id}")
    public ApiResponse<MenuDetailResponse> getMenuDetail(
            @PathVariable("id") @Min(value = 1, message = "菜单ID不能为空") Long id) {
        return menuAdminService.getMenuDetail(id);
    }

    /**
     * 更新菜单
     * @param id 菜单ID
     * @param request 更新菜单请求参数
     * @return 更新后的菜单详情
     */
    @PutMapping("/{id}")
    public ApiResponse<MenuDetailResponse> updateMenu(
            @PathVariable("id") @Min(value = 1, message = "菜单ID不能为空") Long id,
            @Valid @RequestBody UpdateMenuRequest request) {
        return menuAdminService.updateMenu(id, request);
    }

    /**
     * 删除菜单
     * @param id 菜单ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMenu(
            @PathVariable("id") @Min(value = 1, message = "菜单ID不能为空") Long id) {
        return menuAdminService.deleteMenu(id);
    }
}
