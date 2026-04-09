package cn.dextea.store.cache;

import cn.dextea.store.entity.StoreEntity;
import cn.dextea.store.enums.StoreStatus;
import cn.dextea.store.mapper.StoreMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StoreCacheWarmupRunner implements ApplicationRunner {

    private static final String DETAIL_KEY_PREFIX = "store:detail:";
    private static final long DETAIL_BASE_TTL_SECONDS = 30 * 60L;

    private final StoreMapper storeMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) {
        List<StoreEntity> stores = storeMapper.selectList(
                new LambdaQueryWrapper<StoreEntity>()
                        .ne(StoreEntity::getStatus, StoreStatus.CLOSED.getValue()));

        if (stores.isEmpty()) {
            log.info("Store cache warmup skipped: no active stores found");
            return;
        }

        // 使用 Pipeline 批量写入 Redis L2，减少 RTT
        stringRedisTemplate.executePipelined((org.springframework.data.redis.core.RedisCallback<Object>) connection -> {
            for (StoreEntity store : stores) {
                try {
                    String key = DETAIL_KEY_PREFIX + store.getId();
                    String json = objectMapper.writeValueAsString(store);
                    // TTL = 30min + 随机抖动最多 6min，防止同时过期
                    long ttl = DETAIL_BASE_TTL_SECONDS + (long) (Math.random() * DETAIL_BASE_TTL_SECONDS * 0.2);
                    connection.stringCommands().setEx(key.getBytes(), ttl, json.getBytes());
                } catch (Exception e) {
                    log.warn("Failed to warmup cache for store {}", store.getId(), e);
                }
            }
            return null;
        });

        log.info("Store cache warmup completed: {} stores written to Redis L2", stores.size());
    }
}
