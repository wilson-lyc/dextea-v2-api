package cn.dextea.order.util;

import cn.hutool.core.date.DateUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * @author Lai Yongchao
 */
@Slf4j
@Component
public class PickUpNoUtil {
    @Resource
    private RedisTemplate<String, Long> redisTemplate; // 修改为Long类型模板

    private String getKey(Long id) {
        String date = DateUtil.format(DateUtil.date(), "yyyyMMdd");
        return String.format("order:pick_up:%s:%d", date, id);
    }

    public String getPickUpNo(Long id) {
        String key = getKey(id);

        // 使用increment方法，如果key不存在会自动初始化为0然后加1
        Long value = redisTemplate.opsForValue().increment(key, 1L);

        // 设置过期时间（只有在新key时才设置，避免每次操作都重置过期时间）
        if (value != null && value == 1L) {
            redisTemplate.expire(key, Duration.ofHours(24));
            log.info("Initialized key: {} with value: {}", key, value);
        } else {
            log.info("Incremented key: {} to value: {}", key, value);
        }

        // 计算取货号
        Long pickUpNo = 8000 + (value % 1000);
        String formattedPickUpNo = String.format("%04d", pickUpNo);
        log.info("Generated pick-up number: {}", formattedPickUpNo);

        return formattedPickUpNo;
    }
}