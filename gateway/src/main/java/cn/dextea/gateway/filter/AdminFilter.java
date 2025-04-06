package cn.dextea.gateway.filter;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lai Yongchao
 */
@Configuration
public class AdminFilter {
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截地址
                .addInclude("/**")
                // 白名单
                .addExclude(
                        "/staff/login",
                        "/customer/login",
                        "/store/customer/**",
                        "/product/customer",
                        "/menu/customer/**",
                        "/order/customer/**")
                // 鉴权方法
                .setAuth(obj -> {
                    SaRouter.match("/**", r -> StpUtil.checkLogin());
                })
                // 异常处理
                .setError(e -> {
                    SaResult res=new SaResult();
                    res.setCode(401);
                    res.setMsg("token无效");
                    return res;
                });
    }
}
