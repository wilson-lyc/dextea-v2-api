package cn.dextea.customer.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.customer.dto.request.CustomerPageQueryRequest;
import cn.dextea.customer.dto.response.CustomerDetailResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface CustomerAdminService {

    ApiResponse<IPage<CustomerDetailResponse>> page(CustomerPageQueryRequest request);

    ApiResponse<CustomerDetailResponse> detail(Long id);

    ApiResponse<Void> enable(Long id);

    ApiResponse<Void> disable(Long id);
}
