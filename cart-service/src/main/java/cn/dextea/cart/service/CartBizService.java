package cn.dextea.cart.service;

import cn.dextea.cart.dto.request.AddCartItemRequest;
import cn.dextea.cart.dto.request.SwitchStoreRequest;
import cn.dextea.cart.dto.request.UpdateCartItemQuantityRequest;
import cn.dextea.cart.dto.response.CartDetailResponse;
import cn.dextea.cart.dto.response.SwitchStoreResponse;
import cn.dextea.common.web.response.ApiResponse;

public interface CartBizService {

    ApiResponse<Void> addItem(Long customerId, AddCartItemRequest request);

    ApiResponse<CartDetailResponse> getCart(Long customerId);

    ApiResponse<Void> updateItemQuantity(Long customerId, UpdateCartItemQuantityRequest request);

    ApiResponse<Void> deleteItem(Long customerId, String skuId);

    ApiResponse<Void> clearCart(Long customerId);

    ApiResponse<SwitchStoreResponse> switchStore(Long customerId, SwitchStoreRequest request);
}
