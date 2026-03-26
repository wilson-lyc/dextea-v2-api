package cn.dextea.staff.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.staff.dto.request.PermissionPageQueryRequest;
import cn.dextea.staff.dto.response.PermissionDetailResponse;
import cn.dextea.staff.service.PermissionAdminService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/permissions")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class PermissionAdminController {
    private final PermissionAdminService permissionAdminService;

    @GetMapping
    public ApiResponse<IPage<PermissionDetailResponse>> getPermissionPage(@Valid PermissionPageQueryRequest request) {
        return permissionAdminService.getPermissionPage(request);
    }

    @GetMapping("/{id}")
    public ApiResponse<PermissionDetailResponse> getPermissionDetail(@PathVariable @Min(value = 1, message = "权限ID不能为空") Long id) {
        return permissionAdminService.getPermissionDetail(id);
    }
}
