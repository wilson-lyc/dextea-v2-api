package cn.dextea.gateway.filter;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lai Yongchao
 */
@Slf4j
@Configuration
public class AdminFilter {
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截地址
                .addInclude("/**")
                // 白名单
                .addExclude(
                        // 登录接口
                        "/v1/staff/login",
                        "/ping",
                        "/customer/login",
                        // 顾客端接口
                        "/store/customer/**",
                        "/product/customer/**",
                        "/menu/customer/**",
                        "/order/customer/**")
                // 鉴权方法
                .setAuth(obj -> {
                    SaRouter.match("/**", r -> StpUtil.checkLogin());
                })
                // 异常处理
                .setError(e -> {
                    log.error("鉴权失败：{}", e.getMessage());
                    SaResult res=new SaResult();
                    res.setCode(401);
                    res.setMsg("token无效");
                    return res;
                })
                // 前置操作
                .setBeforeAuth(obj -> {
                    SaHolder.getResponse()
                            .setHeader("Access-Control-Allow-Origin", "*")
                            .setHeader("Access-Control-Allow-Methods", "*")
                            .setHeader("Access-Control-Allow-Headers", "*")
                            .setHeader("Access-Control-Max-Age", "3600");
                    SaRouter.match(SaHttpMethod.OPTIONS)
                            .free(r -> log.info("OPTIONS预检请求，不做处理"))
                            .back();
                });
    }
}
