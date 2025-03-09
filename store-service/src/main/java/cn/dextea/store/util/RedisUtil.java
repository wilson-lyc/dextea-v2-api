package cn.dextea.store.util;

import cn.dextea.store.dto.StoreNearbyDTO;
import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lai Yongchao
 */
@Slf4j
@Component
public class RedisUtil {
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private static final String LOCATION_KEY = "location:store";

    /**
     * 添加定位到redis
     * @param storeId 门店ID
     * @param longitude 经度
     * @param latitude 纬度
     */
    public void setStoreLocation(long storeId, double longitude, double latitude) {
        Point point = new Point(longitude, latitude);
        redisTemplate.opsForGeo().add(LOCATION_KEY, point, String.valueOf(storeId));
    }

    /**
     * 获取附近门店
     * @param longitude 经度
     * @param latitude 纬度
     * @param radius 半径
     * @param limit 数量
     */
    public List<StoreNearbyDTO> getNearbyStores(double longitude, double latitude, double radius, int limit) {
        Point center = new Point(longitude, latitude);
        Distance distance = new Distance(radius, Metrics.KILOMETERS);
        Circle circle = new Circle(center, distance);

        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending()
                .limit(limit);
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo().radius(LOCATION_KEY, circle, args);
        if (results.getContent().isEmpty()) {
            // 附近没有门店, 返回最近的门店
            args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                    .includeDistance()
                    .includeCoordinates()
                    .sortAscending()
                    .limit(1);
            results = redisTemplate.opsForGeo().radius(LOCATION_KEY, new Circle(center,new Distance(Double.MAX_VALUE, Metrics.KILOMETERS)), args);
        }
        return results.getContent().stream()
                .map(result -> {
                    String storeIdStr = result.getContent().getName();
                    Long storeId = Long.parseLong(storeIdStr);
                    Distance storeDistance = result.getDistance();
                    Double storeDistanceValue;
                    String distanceUnit;
                    if (storeDistance.getValue() < 1) {
                        storeDistanceValue = storeDistance.getValue() * 1000;
                        DecimalFormat df = new DecimalFormat("#");
                        storeDistanceValue = Double.parseDouble(df.format(storeDistanceValue));
                        distanceUnit = "m";
                    } else {
                        storeDistanceValue = storeDistance.getValue();
                        DecimalFormat df = new DecimalFormat("#.0");
                        storeDistanceValue = Double.parseDouble(df.format(storeDistanceValue));
                        distanceUnit = "km";
                    }
                    return StoreNearbyDTO.builder()
                            .id(storeId)
                            .distance(storeDistanceValue)
                            .distanceUnit(distanceUnit)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取门店距离
     * @param id 门店ID
     * @param longitude 经度
     * @param latitude 纬度
     * @return 距离
     */
    public double getDistanceToStore(Long id, Double longitude, Double latitude) {
        Point point = new Point(longitude, latitude);
        String userKey = IdUtil.fastSimpleUUID();
        redisTemplate.opsForGeo().add(LOCATION_KEY, point, userKey);// 添加用户位置
        Distance distance = redisTemplate.opsForGeo().distance(
                LOCATION_KEY,
                String.valueOf(id),
                userKey,
                Metrics.KILOMETERS);
        redisTemplate.opsForGeo().remove(LOCATION_KEY, userKey);
        return distance.getValue();
    }
}
