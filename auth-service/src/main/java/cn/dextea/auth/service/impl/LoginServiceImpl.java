package cn.dextea.auth.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dextea.auth.dto.StaffLoginDTO;
import cn.dextea.auth.dto.CheckPwdResDTO;
import cn.dextea.auth.feign.StaffServiceFeign;
import cn.dextea.auth.service.LoginService;
import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.dto.ResultCode;
import cn.dextea.common.dto.StaffLoginResDTO;
import com.alibaba.fastjson2.JSONObject;
import com.google.protobuf.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Lai Yongchao
 */
@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private StaffServiceFeign staffServiceFeign;
    @Override
    public ApiResponse staffLogin(StaffLoginDTO loginData) {
        ApiResponse res = staffServiceFeign.login(loginData);
        // 登录失败
        if(res.getCode()!= ResultCode.SUCCESS.getCode()){
            return res;
        }
        // 登录成功
        StaffLoginResDTO staff=res.getData().getJSONObject("staff").toJavaObject(StaffLoginResDTO.class);
        StpUtil.login(staff.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return ApiResponse.success("登录成功",JSONObject.of("token",tokenInfo.getTokenValue(),"staff",staff));
    }
}
