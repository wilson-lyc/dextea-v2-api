package cn.dextea.auth.service.impl;

import cn.dextea.auth.mapper.RouterMapper;
import cn.dextea.auth.pojo.*;
import cn.dextea.auth.service.RouterService;
import cn.dextea.common.dto.ApiResponse;
import com.alibaba.fastjson2.JSONObject;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Service
public class RouterServiceImpl implements RouterService {
    @Autowired
    RouterMapper routerMapper;

    @Override
    public ApiResponse getRouterList() {
        List<Router> routers = routerMapper.selectList(null);
        return ApiResponse.success(JSONObject.of("routers",routers));
    }

    @Override
    public ApiResponse getStaffRouter(Long uid) {
        MPJLambdaWrapper<Router> wrapper = new MPJLambdaWrapper<Router>()
                .selectAll(Router.class)
                .innerJoin(RoleRouter.class, RoleRouter::getRouterId, Router::getId)
                .innerJoin(StaffRole.class, StaffRole::getRoleId, RoleRouter::getRoleId)
                .eq(StaffRole::getStaffId, uid)
                .unionAll(Router.class, union -> union
                        .selectAll(Router.class)
                        .eq(Router::getType, 2));
        List<Router> routers = routerMapper.selectList(wrapper);
        return ApiResponse.success(JSONObject.of("routers",routers));
    }
}
