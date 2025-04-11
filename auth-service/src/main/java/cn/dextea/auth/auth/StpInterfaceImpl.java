package cn.dextea.auth.auth;

import cn.dev33.satoken.stp.StpInterface;
import cn.dextea.common.feign.AuthFeign;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Slf4j
@Component
public class StpInterfaceImpl implements StpInterface {
    @Resource
    private AuthFeign authFeign;
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return authFeign.getStaffPermissionKeys(Long.valueOf(loginId.toString()));
    }

    @Override
    public List<String> getRoleList(Object loginId, String s) {
        return authFeign.getStaffRoleKeys((Long) loginId);
    }
}
