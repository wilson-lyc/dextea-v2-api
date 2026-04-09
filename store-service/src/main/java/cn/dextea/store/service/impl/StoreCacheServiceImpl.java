package cn.dextea.store.service.impl;

import cn.dextea.store.entity.StoreEntity;
import cn.dextea.store.enums.StoreStatus;
import cn.dextea.store.mapper.StoreMapper;
import cn.dextea.store.service.StoreCacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreCacheServiceImpl implements StoreCacheService {

    private final StoreMapper storeMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final CacheManager cacheManager;
    private final ObjectMapper objectMapper;

    private static final String DETAIL_KEY_PREFIX = "store:detail:";
    private static final String VALIDITY_KEY_PREFIX = "store:valid:";
    private static final String LOCK_KEY_PREFIX = "lock:store:detail:";

    private static final long DETAIL_BASE_TTL_SECONDS = 30 * 60L;
    private static final long VALIDITY_BASE_TTL_SECONDS = 10 * 60L;
    private static final long NULL_VALIDITY_TTL_SECONDS = 60L;
    private static final long LOCK_TTL_SECONDS = 3L;

    @Override
    public StoreEntity getStoreDetail(Long id) {
        Cache caffeineCache = cacheManager.getCache("storeDetail");

        // L1 Caffeine
        if (caffeineCache != null) {
            Cache.ValueWrapper wrapper = caffeineCache.get(id);
            if (wrapper != null) {
                return (StoreEntity) wrapper.get();
            }
        }

        // L2 Redis
        String redisKey = DETAIL_KEY_PREFIX + id;
        String json = stringRedisTemplate.opsForValue().get(redisKey);
        if (json != null) {
            StoreEntity entity = deserialize(json);
            if (caffeineCache != null && entity != null) {
                caffeineCache.put(id, entity);
            }
            return entity;
        }

        // 分布式互斥锁防击穿：同一时刻只允许一个实例回源 DB
        String lockKey = LOCK_KEY_PREFIX + id;
        boolean locked = Boolean.TRUE.equals(
                stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "1", Duration.ofSeconds(LOCK_TTL_SECONDS)));

        try {
            if (!locked) {
                // 未抢到锁，等待后再次尝试 Redis
                Thread.sleep(50);
                json = stringRedisTemplate.opsForValue().get(redisKey);
                if (json != null) {
                    StoreEntity entity = deserialize(json);
                    if (caffeineCache != null && entity != null) {
                        caffeineCache.put(id, entity);
                    }
                    return entity;
                }
            }

            // MySQL
            StoreEntity entity = storeMapper.selectById(id);
            if (entity != null) {
                long ttl = jitter(DETAIL_BASE_TTL_SECONDS);
                stringRedisTemplate.opsForValue().set(redisKey, serialize(entity), Duration.ofSeconds(ttl));
                if (caffeineCache != null) {
                    caffeineCache.put(id, entity);
                }
            }
            return entity;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Interrupted while waiting for store detail lock, id={}", id);
            return null;
        } catch (Exception e) {
            log.error("Failed to load store detail, id={}", id, e);
            return null;
        } finally {
            if (locked) {
                stringRedisTemplate.delete(lockKey);
            }
        }
    }

    @Override
    public boolean checkValidity(Long id) {
        Cache caffeineCache = cacheManager.getCache("storeValidity");

        // L1 Caffeine
        if (caffeineCache != null) {
            Cache.ValueWrapper wrapper = caffeineCache.get(id);
            if (wrapper != null) {
                return Boolean.TRUE.equals(wrapper.get());
            }
        }

        // L2 Redis
        String redisKey = VALIDITY_KEY_PREFIX + id;
        String value = stringRedisTemplate.opsForValue().get(redisKey);
        if (value != null) {
            boolean valid = Boolean.parseBoolean(value);
            if (caffeineCache != null) {
                caffeineCache.put(id, valid);
            }
            return valid;
        }

        // MySQL
        StoreEntity entity = storeMapper.selectById(id);
        boolean valid = entity != null && entity.getStatus() != StoreStatus.CLOSED.getValue();

        // 门店不存在时缓存 false，使用短 TTL 防穿透
        long ttl = entity == null ? NULL_VALIDITY_TTL_SECONDS : jitter(VALIDITY_BASE_TTL_SECONDS);
        stringRedisTemplate.opsForValue().set(redisKey, String.valueOf(valid), Duration.ofSeconds(ttl));
        if (caffeineCache != null) {
            caffeineCache.put(id, valid);
        }
        return valid;
    }

    @Override
    public void evictStore(Long id) {
        // 同步删除 L1
        Cache detailCache = cacheManager.getCache("storeDetail");
        if (detailCache != null) {
            detailCache.evict(id);
        }
        Cache validityCache = cacheManager.getCache("storeValidity");
        if (validityCache != null) {
            validityCache.evict(id);
        }

        // 同步删除 L2
        stringRedisTemplate.delete(DETAIL_KEY_PREFIX + id);
        stringRedisTemplate.delete(VALIDITY_KEY_PREFIX + id);
    }

    private long jitter(long baseTtl) {
        return baseTtl + (long) (Math.random() * baseTtl * 0.2);
    }

    private String serialize(StoreEntity entity) {
        try {
            return objectMapper.writeValueAsString(entity);
        } catch (Exception e) {
            log.error("Failed to serialize StoreEntity, id={}", entity.getId(), e);
            throw new IllegalStateException("StoreEntity serialization failed", e);
        }
    }

    private StoreEntity deserialize(String json) {
        try {
            return objectMapper.readValue(json, StoreEntity.class);
        } catch (Exception e) {
            log.error("Failed to deserialize StoreEntity from Redis", e);
            return null;
        }
    }
}
