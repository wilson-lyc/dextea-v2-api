package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.StoreMenuQueryRequest;
import cn.dextea.product.dto.response.StoreMenuResponse;
import cn.dextea.product.service.MenuBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "菜单（Biz）", description = "顾客端门店菜单查询接口")
@RestController
@RequestMapping("/v1/biz/menus")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class MenuBizController {

    private final MenuBizService menuBizService;

    /**
     * 查询门店菜单（含分组及商品信息）
     * @param request 门店ID
     * @return 菜单详情，按分组包含商品名称、简介、价格、状态
     */
    @Operation(summary = "查询门店菜单", description = "按分组结构返回菜单，每条商品包含名称、简介、价格及在售状态")
    @GetMapping
    public ApiResponse<StoreMenuResponse> getStoreMenu(@Valid StoreMenuQueryRequest request) {
        return menuBizService.getStoreMenu(request);
    }
}
