package com.com2us.mobility.core.redis;

import com.com2us.mobility.core.exception.CoreException;
import com.com2us.mobility.core.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
@Slf4j
@Repository
@RequiredArgsConstructor
public class GeoRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    /** 위치 추가 */
    public void add(String key, Double lat, Double lng, String member) {
        try {
            redisTemplate.opsForGeo().add(key, new Point(lng, lat), member);
        } catch (Exception e) {
            throw new CoreException(ErrorCode.REDIS_OPERATION_FAILED, e);
        }
    }

    /** 위치 제거 */
    public void remove(String key, String member) {
        try {
            redisTemplate.opsForZSet().remove(key, member);
        } catch (Exception e) {
            throw new CoreException(ErrorCode.REDIS_OPERATION_FAILED, e);
        }
    }

    /** 반경 내 멤버 조회 (거리순) - member 이름만 반환 */
    public List<String> search(String key, Double lat, Double lng, int radiusKm, int count) {
        return searchWithDistance(key, lat, lng, radiusKm, count)
                .stream()
                .map(GeoResult::member)
                .toList();
    }

    /** 반경 내 멤버 조회 (거리 포함) */
    public List<GeoResult> searchWithDistance(String key, Double lat, Double lng, int radiusKm, int count) {
        try {
            GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo().radius(
                    key,
                    new Circle(
                            new Point(lng, lat),
                            new Distance(radiusKm, Metrics.KILOMETERS)
                    ),
                    RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                            .includeDistance()
                            .sortAscending()
                            .limit(count)
            );

            if (results == null) return List.of();

            return results.getContent().stream()
                    .map(r -> new GeoResult(
                            r.getContent().getName(),
                            r.getDistance().getValue()
                    ))
                    .toList();

        } catch (CoreException e) {
            throw e;
        } catch (Exception e) {
            throw new CoreException(ErrorCode.REDIS_OPERATION_FAILED, e);
        }
    }

    // ─────────────────────────────────────────
    // 반환 타입
    // ─────────────────────────────────────────
    public record GeoResult(String member, double distanceKm) {}
}