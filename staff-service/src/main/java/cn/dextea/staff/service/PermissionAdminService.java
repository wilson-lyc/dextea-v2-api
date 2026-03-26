package cn.dextea.staff.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.staff.dto.request.PermissionPageQueryRequest;
import cn.dextea.staff.dto.response.PermissionDetailResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface PermissionAdminService {
    ApiResponse<IPage<PermissionDetailResponse>> getPermissionPage(PermissionPageQueryRequest request);

    ApiResponse<PermissionDetailResponse> getPermissionDetail(Long id);
}
