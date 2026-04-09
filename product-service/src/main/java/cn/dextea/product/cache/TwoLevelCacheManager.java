package cn.dextea.product.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CacheManager that creates {@link TwoLevelCache} instances per cache name.
 * Each cache has its own Caffeine instance (when L1 is enabled) with the TTL and
 * capacity defined by its {@link CacheSpec}.
 */
public class TwoLevelCacheManager implements CacheManager {

    private final Map<String, CacheSpec> specs;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Map<String, TwoLevelCache> cacheMap = new ConcurrentHashMap<>();

    public TwoLevelCacheManager(Map<String, CacheSpec> specs,
                                 RedisTemplate<String, Object> redisTemplate) {
        this.specs = specs;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Cache getCache(String name) {
        return cacheMap.computeIfAbsent(name, this::buildCache);
    }

    @Override
    public Collection<String> getCacheNames() {
        return specs.keySet();
    }

    private TwoLevelCache buildCache(String name) {
        CacheSpec spec = specs.get(name);
        if (spec == null) {
            throw new IllegalArgumentException("No CacheSpec registered for cache name: " + name);
        }
        com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache = null;
        if (spec.l1Enabled()) {
            caffeineCache = Caffeine.newBuilder()
                    .maximumSize(spec.l1MaxSize())
                    .expireAfterWrite(spec.l1Ttl())
                    .build();
        }
        return new TwoLevelCache(name, spec, caffeineCache, redisTemplate);
    }
}
