package cn.dextea.product.cache;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Two-level cache backed by Caffeine (L1) and Redis (L2).
 *
 * <ul>
 *   <li>Read path: L1 → L2 → DB, with back-filling on each miss</li>
 *   <li>Write path: write to L2 first, then L1 (TTL with ±10% jitter on L2)</li>
 *   <li>Evict path: remove from L2 first to minimise stale-read window, then L1</li>
 * </ul>
 */
@Slf4j
public class TwoLevelCache extends AbstractValueAdaptingCache {

    private final String name;
    private final CacheSpec spec;
    @Nullable
    private final Cache<Object, Object> caffeineCache;
    private final RedisTemplate<String, Object> redisTemplate;

    public TwoLevelCache(String name, CacheSpec spec,
                         @Nullable Cache<Object, Object> caffeineCache,
                         RedisTemplate<String, Object> redisTemplate) {
        super(true);
        this.name = name;
        this.spec = spec;
        this.caffeineCache = caffeineCache;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @NonNull
    public String getName() {
        return name;
    }

    @Override
    @NonNull
    public Object getNativeCache() {
        return caffeineCache != null ? caffeineCache : redisTemplate;
    }

    @Override
    @Nullable
    protected Object lookup(@NonNull Object key) {
        if (spec.l1Enabled() && caffeineCache != null) {
            Object l1Value = caffeineCache.getIfPresent(key);
            if (l1Value != null) {
                return l1Value;
            }
        }
        if (spec.l2Enabled()) {
            Object l2Value = redisTemplate.opsForValue().get(toRedisKey(key));
            if (l2Value != null) {
                if (spec.l1Enabled() && caffeineCache != null) {
                    caffeineCache.put(key, l2Value);
                }
                return l2Value;
            }
        }
        return null;
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T get(@NonNull Object key, @NonNull Callable<T> valueLoader) {
        Object cached = lookup(key);
        if (cached != null) {
            return (T) fromStoreValue(cached);
        }
        try {
            T value = valueLoader.call();
            put(key, value);
            return value;
        } catch (Exception e) {
            throw new ValueRetrievalException(key, valueLoader, e);
        }
    }

    @Override
    public void put(@NonNull Object key, @Nullable Object value) {
        Object storeValue = toStoreValue(value);
        if (spec.l2Enabled()) {
            redisTemplate.opsForValue().set(toRedisKey(key), storeValue, withJitter(spec.l2Ttl()));
        }
        if (spec.l1Enabled() && caffeineCache != null) {
            caffeineCache.put(key, storeValue);
        }
    }

    @Override
    public void evict(@NonNull Object key) {
        // Evict L2 first so concurrent readers cannot backfill stale L1 data
        if (spec.l2Enabled()) {
            redisTemplate.delete(toRedisKey(key));
        }
        if (spec.l1Enabled() && caffeineCache != null) {
            caffeineCache.invalidate(key);
        }
    }

    /**
     * Evicts all entries whose Spring Cache key starts with {@code keyPrefix}.
     * Useful for invalidating all store-variants of a resource without knowing exact keys.
     */
    public void evictByKeyPrefix(String keyPrefix) {
        if (spec.l2Enabled()) {
            scanAndDelete(spec.redisKeyPrefix() + ":" + keyPrefix + "*");
        }
        if (spec.l1Enabled() && caffeineCache != null) {
            caffeineCache.asMap().keySet().removeIf(k -> k.toString().startsWith(keyPrefix));
        }
    }

    @Override
    public void clear() {
        if (spec.l2Enabled()) {
            scanAndDelete(spec.redisKeyPrefix() + ":*");
        }
        if (spec.l1Enabled() && caffeineCache != null) {
            caffeineCache.invalidateAll();
        }
    }

    // ---- helpers ----

    private String toRedisKey(Object key) {
        return spec.redisKeyPrefix() + ":" + key;
    }

    private Duration withJitter(Duration base) {
        double jitter = ThreadLocalRandom.current().nextDouble(-0.1, 0.1);
        return Duration.ofMillis((long) (base.toMillis() * (1.0 + jitter)));
    }

    private void scanAndDelete(String pattern) {
        try {
            redisTemplate.execute((RedisConnection connection) -> {
                try (var cursor = connection.scan(
                        ScanOptions.scanOptions().match(pattern).count(200).build())) {
                    cursor.forEachRemaining(key -> connection.del(key));
                } catch (Exception e) {
                    log.warn("Cache evict scan error, pattern={}", pattern, e);
                }
                return null;
            });
        } catch (Exception e) {
            log.warn("Cache evict failed, pattern={}", pattern, e);
        }
    }
}
