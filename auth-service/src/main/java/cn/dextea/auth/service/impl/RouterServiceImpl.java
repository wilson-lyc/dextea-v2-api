package cn.dextea.auth.service.impl;

import cn.dextea.auth.mapper.RouterMapper;
import cn.dextea.auth.pojo.Router;
import cn.dextea.auth.service.RouterService;
import cn.dextea.common.dto.ApiResponse;
import com.alibaba.fastjson2.JSONObject;
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
}
