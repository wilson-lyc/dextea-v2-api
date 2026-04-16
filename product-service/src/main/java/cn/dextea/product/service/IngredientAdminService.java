package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CreateIngredientRequest;
import cn.dextea.product.dto.request.IngredientPageQueryRequest;
import cn.dextea.product.dto.request.UpdateIngredientRequest;
import cn.dextea.product.dto.request.UpdateIngredientStatusRequest;
import cn.dextea.product.dto.response.CreateIngredientResponse;
import cn.dextea.product.dto.response.IngredientDetailResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface IngredientAdminService {

    ApiResponse<CreateIngredientResponse> create(CreateIngredientRequest request);

    ApiResponse<IPage<IngredientDetailResponse>> getPage(IngredientPageQueryRequest request);

    ApiResponse<IngredientDetailResponse> getDetail(Long id);

    ApiResponse<IngredientDetailResponse> updateInfo(Long id, UpdateIngredientRequest request);

    ApiResponse<Void> updateStatus(Long id, UpdateIngredientStatusRequest request);
}
