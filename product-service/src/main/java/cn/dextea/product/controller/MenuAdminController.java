package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.BindStoreMenuRequest;
import cn.dextea.product.dto.request.CreateMenuRequest;
import cn.dextea.product.dto.request.MenuPageQueryRequest;
import cn.dextea.product.dto.request.UpdateMenuRequest;
import cn.dextea.product.dto.response.CreateMenuResponse;
import cn.dextea.product.dto.response.MenuDetailResponse;
import cn.dextea.product.service.MenuAdminService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "菜单管理（Admin）", description = "管理端菜单 CRUD 及门店绑定接口")
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
    @Operation(summary = "创建菜单")
    @PostMapping
    public ApiResponse<CreateMenuResponse> createMenu(@Valid @RequestBody CreateMenuRequest request) {
        return menuAdminService.createMenu(request);
    }

    /**
     * 分页查询菜单列表
     * @param request 菜单分页查询请求参数
     * @return 菜单分页数据
     */
    @Operation(summary = "分页查询菜单列表")
    @GetMapping
    public ApiResponse<IPage<MenuDetailResponse>> getMenuPage(@Valid MenuPageQueryRequest request) {
        return menuAdminService.getMenuPage(request);
    }

    /**
     * 查询菜单详情
     * @param id 菜单ID
     * @return 菜单详情（含分组及商品ID列表）
     */
    @Operation(summary = "查询菜单详情", description = "返回菜单详情，包含分组结构及各分组下的商品ID列表")
    @GetMapping("/{id}")
    public ApiResponse<MenuDetailResponse> getMenuDetail(
            @Parameter(description = "菜单ID") @PathVariable("id") @Min(value = 1, message = "菜单ID不能为空") Long id) {
        return menuAdminService.getMenuDetail(id);
    }

    /**
     * 更新菜单
     * @param id 菜单ID
     * @param request 更新菜单请求参数
     * @return 更新后的菜单详情
     */
    @Operation(summary = "更新菜单")
    @PutMapping("/{id}")
    public ApiResponse<MenuDetailResponse> updateMenu(
            @Parameter(description = "菜单ID") @PathVariable("id") @Min(value = 1, message = "菜单ID不能为空") Long id,
            @Valid @RequestBody UpdateMenuRequest request) {
        return menuAdminService.updateMenu(id, request);
    }

    /**
     * 删除菜单（软删除，菜单存在门店绑定时不允许删除）
     * @param id 菜单ID
     * @return 操作结果
     */
    @Operation(summary = "删除菜单", description = "软删除，菜单存在门店绑定时不允许删除")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMenu(
            @Parameter(description = "菜单ID") @PathVariable("id") @Min(value = 1, message = "菜单ID不能为空") Long id) {
        return menuAdminService.deleteMenu(id);
    }

    /**
     * 为菜单绑定门店
     * @param id 菜单ID
     * @param request 绑定门店请求参数
     * @return 操作结果
     */
    @Operation(summary = "为菜单绑定门店")
    @PostMapping("/{id}/stores")
    public ApiResponse<Void> bindStore(
            @Parameter(description = "菜单ID") @PathVariable("id") @Min(value = 1, message = "菜单ID不能为空") Long id,
            @Valid @RequestBody BindStoreMenuRequest request) {
        return menuAdminService.bindStore(id, request);
    }

    /**
     * 解绑菜单与门店
     * @param id 菜单ID
     * @param storeId 门店ID
     * @return 操作结果
     */
    @Operation(summary = "解绑菜单与门店")
    @DeleteMapping("/{id}/stores/{storeId}")
    public ApiResponse<Void> unbindStore(
            @Parameter(description = "菜单ID") @PathVariable("id") @Min(value = 1, message = "菜单ID不能为空") Long id,
            @Parameter(description = "门店ID") @PathVariable("storeId") @Min(value = 1, message = "门店ID不能为空") Long storeId) {
        return menuAdminService.unbindStore(id, storeId);
    }
}
