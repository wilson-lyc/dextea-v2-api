package cn.dextea.cart.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dextea.cart.dto.request.AddCartItemRequest;
import cn.dextea.cart.dto.request.SwitchStoreRequest;
import cn.dextea.cart.dto.request.UpdateCartItemQuantityRequest;
import cn.dextea.cart.dto.response.CartDetailResponse;
import cn.dextea.cart.dto.response.SwitchStoreResponse;
import cn.dextea.cart.service.CartBizService;
import cn.dextea.common.web.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/biz/carts")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class CartBizController {

    private final CartBizService cartBizService;

    /**
     * 加购 / 增加数量
     * @param request 商品ID、数量及客制化选项
     * @return 操作结果
     */
    @PostMapping("/items")
    public ApiResponse<Void> addItem(@Valid @RequestBody AddCartItemRequest request) {
        Long customerId = StpUtil.getLoginIdAsLong();
        return cartBizService.addItem(customerId, request);
    }

    /**
     * 查看购物车
     * @return 购物车详情
     */
    @GetMapping
    public ApiResponse<CartDetailResponse> getCart() {
        Long customerId = StpUtil.getLoginIdAsLong();
        return cartBizService.getCart(customerId);
    }

    /**
     * 修改条目数量
     * @param request skuId及新数量（为0时等同删除）
     * @return 操作结果
     */
    @PutMapping("/items")
    public ApiResponse<Void> updateItemQuantity(@Valid @RequestBody UpdateCartItemQuantityRequest request) {
        Long customerId = StpUtil.getLoginIdAsLong();
        return cartBizService.updateItemQuantity(customerId, request);
    }

    /**
     * 删除条目
     * @param skuId 条目SKU标识
     * @return 操作结果
     */
    @DeleteMapping("/items")
    public ApiResponse<Void> deleteItem(
            @RequestParam("skuId") @NotBlank(message = "skuId不能为空") String skuId) {
        Long customerId = StpUtil.getLoginIdAsLong();
        return cartBizService.deleteItem(customerId, skuId);
    }

    /**
     * 清空购物车
     * @return 操作结果
     */
    @DeleteMapping
    public ApiResponse<Void> clearCart() {
        Long customerId = StpUtil.getLoginIdAsLong();
        return cartBizService.clearCart(customerId);
    }

    /**
     * 切换门店（购物车自检）
     * @param request 目标门店ID
     * @return 被移除的条目列表及是否有移除
     */
    @PostMapping("/switch-store")
    public ApiResponse<SwitchStoreResponse> switchStore(@Valid @RequestBody SwitchStoreRequest request) {
        Long customerId = StpUtil.getLoginIdAsLong();
        return cartBizService.switchStore(customerId, request);
    }
}
