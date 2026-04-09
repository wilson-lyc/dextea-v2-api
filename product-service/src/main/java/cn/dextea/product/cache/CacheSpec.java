package cn.dextea.product.cache;

import java.time.Duration;

/**
 * Per-cache configuration: L1 (Caffeine) and L2 (Redis) settings.
 *
 * @param l1Enabled      whether Caffeine local cache is active for this cache
 * @param l1MaxSize      maximum number of entries in Caffeine (ignored when l1Enabled=false)
 * @param l1Ttl          Caffeine expiry duration (ignored when l1Enabled=false)
 * @param l2Enabled      whether Redis distributed cache is active for this cache
 * @param l2Ttl          Redis TTL before jitter (ignored when l2Enabled=false)
 * @param redisKeyPrefix prefix used when building Redis keys, e.g. {@code dextea:product:biz:detail}
 */
public record CacheSpec(
        boolean l1Enabled,
        int l1MaxSize,
        Duration l1Ttl,
        boolean l2Enabled,
        Duration l2Ttl,
        String redisKeyPrefix
) {}
