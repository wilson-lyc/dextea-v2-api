package cn.dextea.product.config;

import cn.dextea.product.cache.CacheNames;
import cn.dextea.product.cache.CacheSpec;
import cn.dextea.product.cache.TwoLevelCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Registers the two-level CacheManager (Caffeine L1 + Redis L2) for the product service.
 *
 * TTL values match the cache-design.md specification:
 * <pre>
 *   productBizDetail        L1=60s   L2=10min
 *   productBizList          L1=off   L2=5min
 *   menuBiz                 L1=2min  L2=30min
 *   customizationItemBiz    L1=2min  L2=15min
 *   customizationOptionsBiz L1=2min  L2=15min
 * </pre>
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

    @Bean
    @Primary
    public CacheManager cacheManager(RedisTemplate<String, Object> redisTemplate) {
        return new TwoLevelCacheManager(buildSpecs(), redisTemplate);
    }

    private Map<String, CacheSpec> buildSpecs() {
        Map<String, CacheSpec> specs = new HashMap<>();

        specs.put(CacheNames.PRODUCT_BIZ_DETAIL, new CacheSpec(
                true, 500, Duration.ofSeconds(60),
                true, Duration.ofMinutes(10),
                CacheNames.REDIS_PREFIX_PRODUCT_BIZ_DETAIL));

        // L1 disabled: parameter combinations are large, L1 hit rate would be too low
        specs.put(CacheNames.PRODUCT_BIZ_LIST, new CacheSpec(
                false, 0, null,
                true, Duration.ofMinutes(5),
                CacheNames.REDIS_PREFIX_PRODUCT_BIZ_LIST));

        specs.put(CacheNames.MENU_BIZ, new CacheSpec(
                true, 100, Duration.ofMinutes(2),
                true, Duration.ofMinutes(30),
                CacheNames.REDIS_PREFIX_MENU_BIZ));

        specs.put(CacheNames.CUSTOMIZATION_ITEM_BIZ, new CacheSpec(
                true, 300, Duration.ofMinutes(2),
                true, Duration.ofMinutes(15),
                CacheNames.REDIS_PREFIX_CUSTOMIZATION_ITEM_BIZ));

        specs.put(CacheNames.CUSTOMIZATION_OPTIONS_BIZ, new CacheSpec(
                true, 300, Duration.ofMinutes(2),
                true, Duration.ofMinutes(15),
                CacheNames.REDIS_PREFIX_CUSTOMIZATION_OPTIONS_BIZ));

        return specs;
    }
}
