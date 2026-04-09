package cn.dextea.customer.controller;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.customer.dto.request.CustomerPageQueryRequest;
import cn.dextea.customer.dto.response.CustomerDetailResponse;
import cn.dextea.customer.service.CustomerAdminService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 顾客管理
 */
@RestController
@RequestMapping("/v1/admin/customers")
@RequiredArgsConstructor
@Validated
public class CustomerAdminController {

    private final CustomerAdminService customerAdminService;

    /**
     * 分页查询顾客列表
     * @param request 顾客分页查询请求参数
     * @return 顾客分页数据
     */
    @GetMapping
    public ApiResponse<IPage<CustomerDetailResponse>> page(@Valid CustomerPageQueryRequest request) {
        return customerAdminService.page(request);
    }

    /**
     * 查询顾客详情
     * @param id 顾客ID
     * @return 顾客详情
     */
    @GetMapping("/{id}")
    public ApiResponse<CustomerDetailResponse> detail(
            @PathVariable("id") @Min(value = 1, message = "顾客ID不能为空") Long id) {
        return customerAdminService.detail(id);
    }

    /**
     * 启用顾客账号
     * @param id 顾客ID
     * @return 操作结果
     */
    @PutMapping("/{id}/enable")
    public ApiResponse<Void> enable(
            @PathVariable("id") @Min(value = 1, message = "顾客ID不能为空") Long id) {
        return customerAdminService.enable(id);
    }

    /**
     * 禁用顾客账号
     * @param id 顾客ID
     * @return 操作结果
     */
    @PutMapping("/{id}/disable")
    public ApiResponse<Void> disable(
            @PathVariable("id") @Min(value = 1, message = "顾客ID不能为空") Long id) {
        return customerAdminService.disable(id);
    }
}
