package com.com2us.mobility.service;

import com.com2us.mobility.core.redis.GeoRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationCacheService {

    private static final String GEO_KEY = "vehicles:locations";

    private final GeoRedisRepository geoRedisRepository;

    /** 근처 차량 ID 조회 */
    public List<Long> findNearbyVehicleIds(Double lat, Double lng, int radiusKm, int count) {
        return geoRedisRepository.search(GEO_KEY, lat, lng, radiusKm, count)
                .stream()
                .map(member -> Long.parseLong(member.replace("vehicle:", "")))
                .toList();
    }

    /** 근처 차량 ID + 거리 조회 */
    public List<NearbyVehicleLocation> findNearbyWithDistance(Double lat, Double lng, int radiusKm, int count) {
        return geoRedisRepository.searchWithDistance(GEO_KEY, lat, lng, radiusKm, count)
                .stream()
                .map(r -> new NearbyVehicleLocation(
                        Long.parseLong(r.member().replace("vehicle:", "")),
                        r.distanceKm()
                ))
                .toList();
    }

    /** GEO에서 차량 제거 */
    public void removeFromGeo(Long vehicleId) {
        geoRedisRepository.remove(GEO_KEY, "vehicle:" + vehicleId);
    }

    public record NearbyVehicleLocation(Long vehicleId, double distanceKm) {}
}