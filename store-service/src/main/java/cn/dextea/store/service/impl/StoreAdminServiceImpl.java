package cn.dextea.store.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.store.converter.StoreConverter;
import cn.dextea.store.dto.request.CreateStoreRequest;
import cn.dextea.store.dto.request.StorePageQueryRequest;
import cn.dextea.store.dto.request.UpdateStoreRequest;
import cn.dextea.store.dto.response.StoreDetailResponse;
import cn.dextea.store.entity.StoreEntity;
import cn.dextea.store.enums.StoreErrorCode;
import cn.dextea.store.enums.StoreStatus;
import cn.dextea.store.mapper.StoreMapper;
import cn.dextea.store.service.StoreAdminService;
import cn.dextea.store.service.StoreGeoSyncService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TextUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StoreAdminServiceImpl implements StoreAdminService {
    private final StoreMapper storeMapper;
    private final StoreConverter storeConverter;
    private final StoreGeoSyncService storeGeoSyncService;
    private final ObjectMapper objectMapper;

    private static final PoolingHttpClientConnectionManager CONNECTION_MANAGER = new PoolingHttpClientConnectionManager();

    static {
        CONNECTION_MANAGER.setMaxTotal(20);
        CONNECTION_MANAGER.setDefaultMaxPerRoute(10);
    }

    @Value("${amap.key}")
    private String amapKey;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<StoreDetailResponse> createStore(CreateStoreRequest request) {
        // 根据省市区和详细地址调用高德接口解析经纬度。
        BigDecimal[] coordinates = resolveCoordinates(request);
        if (coordinates == null) {
            return fail(StoreErrorCode.GEOCODE_FAILED);
        }

        // 组装门店实体，统一把地址、电话、营业时间等字段规整后写入。
        StoreEntity storeEntity = StoreEntity.builder()
                .name(trim(request.getName()))
                .province(trim(request.getProvince()))
                .city(trim(request.getCity()))
                .district(trim(request.getDistrict()))
                .address(trim(request.getAddress()))
                .status(request.getStatus())
                .longitude(coordinates[0])
                .latitude(coordinates[1])
                .phone(trim(request.getPhone()))
                .openTime(trim(request.getOpenTime()))
                .build();

        // 先写 MySQL 主表，再同步 Redis GEO 索引，保证后续附近门店查询可用。
        if (storeMapper.insert(storeEntity) != 1) {
            return fail(StoreErrorCode.CREATE_FAILED);
        }
        storeGeoSyncService.syncStoreLocation(storeEntity);

        // 重新查询最新门店数据并返回给调用方。
        return ApiResponse.success(storeConverter.toStoreDetailResponse(storeMapper.selectById(storeEntity.getId())));
    }

    @Override
    public ApiResponse<IPage<StoreDetailResponse>> getStorePage(StorePageQueryRequest request) {
        // 按门店名称、状态、区域、电话等条件动态拼装分页查询。
        LambdaQueryWrapper<StoreEntity> queryWrapper = new LambdaQueryWrapper<StoreEntity>()
                .like(hasText(request.getName()), StoreEntity::getName, trim(request.getName()))
                .eq(request.getStatus() != null, StoreEntity::getStatus, request.getStatus())
                .eq(hasText(request.getProvince()), StoreEntity::getProvince, trim(request.getProvince()))
                .eq(hasText(request.getCity()), StoreEntity::getCity, trim(request.getCity()))
                .eq(hasText(request.getDistrict()), StoreEntity::getDistrict, trim(request.getDistrict()))
                .like(hasText(request.getPhone()), StoreEntity::getPhone, trim(request.getPhone()))
                .orderByDesc(StoreEntity::getId);

        // 查询门店分页结果，并转换为对外响应对象。
        IPage<StoreEntity> entityPage = storeMapper.selectPage(
                new Page<>(request.getCurrent(), request.getSize()),
                queryWrapper
        );
        IPage<StoreDetailResponse> responsePage = entityPage.convert(storeConverter::toStoreDetailResponse);
        return ApiResponse.success(responsePage);
    }

    @Override
    public ApiResponse<StoreDetailResponse> getStoreDetail(Long id) {
        // 根据门店主键查询详情，不存在时返回业务错误。
        StoreEntity storeEntity = storeMapper.selectById(id);
        if (storeEntity == null) {
            return fail(StoreErrorCode.STORE_NOT_FOUND);
        }
        return ApiResponse.success(storeConverter.toStoreDetailResponse(storeEntity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<StoreDetailResponse> updateStore(Long id, UpdateStoreRequest request) {
        // 先查询当前门店，避免更新不存在的数据。
        StoreEntity storeEntity = storeMapper.selectById(id);
        if (storeEntity == null) {
            return fail(StoreErrorCode.STORE_NOT_FOUND);
        }

        // 将请求里的最新字段回写到门店实体，包括手动维护的经纬度。
        storeEntity.setName(trim(request.getName()));
        storeEntity.setProvince(trim(request.getProvince()));
        storeEntity.setCity(trim(request.getCity()));
        storeEntity.setDistrict(trim(request.getDistrict()));
        storeEntity.setAddress(trim(request.getAddress()));
        storeEntity.setStatus(request.getStatus());
        storeEntity.setLongitude(request.getLongitude());
        storeEntity.setLatitude(request.getLatitude());
        storeEntity.setPhone(trim(request.getPhone()));
        storeEntity.setOpenTime(trim(request.getOpenTime()));

        // 先更新 MySQL，再同步 Redis GEO，确保两边门店坐标保持一致。
        if (storeMapper.updateById(storeEntity) != 1) {
            return fail(StoreErrorCode.UPDATE_FAILED);
        }
        storeGeoSyncService.syncStoreLocation(storeEntity);

        // 回查最新门店记录作为更新结果返回。
        return ApiResponse.success(storeConverter.toStoreDetailResponse(storeMapper.selectById(id)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> deleteStore(Long id) {
        // 删除门店采用软删除策略：先确认门店存在。
        StoreEntity storeEntity = storeMapper.selectById(id);
        if (storeEntity == null) {
            return fail(StoreErrorCode.STORE_NOT_FOUND);
        }

        // 将门店状态改为关店，并同步从 Redis GEO 索引中移除。
        storeEntity.setStatus(StoreStatus.CLOSED.getValue());
        if (storeMapper.updateById(storeEntity) != 1) {
            return fail(StoreErrorCode.UPDATE_FAILED);
        }
        storeGeoSyncService.removeStoreLocation(id);

        return ApiResponse.success();
    }

    private BigDecimal[] resolveCoordinates(CreateStoreRequest request) {
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(CONNECTION_MANAGER)
                .build()) {
            // 把省市区和详细地址拼成完整地址，供地理编码接口解析。
            String fullAddress = trim(request.getProvince())
                    + trim(request.getCity())
                    + trim(request.getDistrict())
                    + trim(request.getAddress());

            String url = "https://restapi.amap.com/v3/geocode/geo"
                    + "?key=" + URLEncoder.encode(amapKey, StandardCharsets.UTF_8)
                    + "&output=JSON"
                    + "&address=" + URLEncoder.encode(fullAddress, StandardCharsets.UTF_8)
                    + "&city=" + URLEncoder.encode(trim(request.getCity()), StandardCharsets.UTF_8);

            HttpGet httpGet = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                // 校验高德接口返回状态，只在成功时继续解析经纬度。
                Map<String, Object> body = objectMapper.readValue(
                        response.getEntity().getContent(), new TypeReference<Map<String, Object>>() {});
                if (!"1".equals(String.valueOf(body.get("status")))) {
                    return null;
                }

                // 取第一条地理编码结果作为门店坐标。
                List<Map<String, Object>> geocodes = (List<Map<String, Object>>) body.get("geocodes");
                if (geocodes == null || geocodes.isEmpty()) {
                    return null;
                }

                // 高德返回的 location 形如 "经度,纬度"，这里拆分后转换为 BigDecimal。
                String location = String.valueOf(geocodes.get(0).get("location"));
                if (TextUtils.isBlank(location) || !location.contains(",")) {
                    return null;
                }

                String[] parts = location.split(",");
                return new BigDecimal[]{
                        new BigDecimal(parts[0]),
                        new BigDecimal(parts[1])
                };
            }
        } catch (Exception exception) {
            // 地址解析过程中出现任何异常，都按解析失败处理。
            return null;
        }
    }

    private <T> ApiResponse<T> fail(StoreErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
