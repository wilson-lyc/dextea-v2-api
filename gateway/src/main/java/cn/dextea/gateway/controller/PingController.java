package cn.dextea.gateway.controller;

import cn.dextea.common.web.response.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 网关连通性检测
 */
@RestController
public class PingController {
    @Value("${spring.application.name:gateway}")
    private String applicationName;

    /**
     * 网关Ping检测
     * @return 连通性检测结果
     */
    @GetMapping("/ping")
    public ApiResponse<Map<String, Object>> ping() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("service", applicationName);
        data.put("status", "pong");
        data.put("timestamp", Instant.now().toEpochMilli());
        return ApiResponse.success(data);
    }
}
