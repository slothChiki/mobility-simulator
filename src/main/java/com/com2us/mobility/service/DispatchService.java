package com.com2us.mobility.service;

import com.com2us.mobility.core.exception.ApiException;
import com.com2us.mobility.core.exception.ErrorCode;
import com.com2us.mobility.core.lock.SpinLockService;
import com.com2us.mobility.domain.dispatch.DispatchRecord;
import com.com2us.mobility.domain.dispatch.DispatchRetryReason;
import com.com2us.mobility.domain.vehicle.Vehicle;
import com.com2us.mobility.domain.vehicle.VehicleGrade;
import com.com2us.mobility.domain.vehicle.VehicleState;
import com.com2us.mobility.domain.vehicle.VehicleStatus;
import com.com2us.mobility.repository.VehicleRepository;
import com.com2us.mobility.repository.VehicleStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchService {

    private static final String LOCK_PREFIX = "lock:vehicle:";
    private static final Duration LOCK_TTL = Duration.ofSeconds(3);

    private static final int[][] RETRY_CONFIG = {
            {1, 5},
            {2, 10},
            {3, 15},
    };

    private final LocationCacheService locationCacheService;
    private final DispatchRetryService dispatchRetryService;
    private final VehicleRepository vehicleRepository;
    private final VehicleStateRepository vehicleStateRepository;
    private final SpinLockService spinLockService;
    private final DispatchSseService dispatchSseService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<Long> tryDispatch(DispatchRecord record) {
        int attemptCount = dispatchRetryService.getAttemptCount(record.getId()) + 1;

        if (attemptCount > RETRY_CONFIG.length) {
            String passengerName = record.getPassenger().getName();
            record.fail();
            dispatchRetryService.fail(record, DispatchRetryReason.NO_AVAILABLE_VEHICLE);
            dispatchSseService.sendFailed(record.getId(), passengerName);
            return Optional.empty();
        }

        int[] config = RETRY_CONFIG[attemptCount - 1];
        int radiusKm = config[0];
        int maxCount = config[1];

        // 거리 포함 조회 (Redis 단일 호출)
        List<LocationCacheService.NearbyVehicleLocation> nearbyLocations = locationCacheService.findNearbyWithDistance(
                record.getPickupLatitude(),
                record.getPickupLongitude(),
                radiusKm,
                maxCount
        );

        List<Long> vehicleIds = nearbyLocations.stream()
                .map(LocationCacheService.NearbyVehicleLocation::vehicleId)
                .toList();

        // 주변 차량 목록 (DB 단일 호출 - N+1 방지)
        List<DispatchSseService.NearbyVehicle> nearbyVehicles = buildNearbyVehicles(nearbyLocations);

        // 탐색 중 SSE 푸시
        String passengerName = record.getPassenger().getName();
        if (attemptCount == 1) {
            dispatchSseService.sendSearching(record.getId(), passengerName, radiusKm, nearbyVehicles);
        } else {
            dispatchSseService.sendExpanding(record.getId(), passengerName, radiusKm, nearbyVehicles);
        }

        if (vehicleIds.isEmpty()) {
            dispatchRetryService.increment(record);
            record.workerFailed();
            return Optional.empty();
        }

        Optional<Long> result = selectAndDispatch(record, vehicleIds);

        if (result.isEmpty()) {
            dispatchRetryService.increment(record);
            record.workerFailed();
        }

        return result;
    }

    private List<DispatchSseService.NearbyVehicle> buildNearbyVehicles(
            List<LocationCacheService.NearbyVehicleLocation> nearbyLocations) {

        if (nearbyLocations.isEmpty()) return List.of();

        List<Long> vehicleIds = nearbyLocations.stream()
                .map(LocationCacheService.NearbyVehicleLocation::vehicleId)
                .toList();

        Map<Long, Vehicle> vehicleMap = vehicleRepository.findAllById(vehicleIds)
                .stream()
                .collect(Collectors.toMap(Vehicle::getId, v -> v));

        return nearbyLocations.stream()
                .map(loc -> {
                    Vehicle vehicle = vehicleMap.get(loc.vehicleId());
                    if (vehicle == null) return null;
                    return DispatchSseService.NearbyVehicle.builder()
                            .vehicleId(loc.vehicleId())
                            .vehicleNumber(vehicle.getVehicleNumber())
                            .grade(vehicle.getVehicleGrade())
                            .distanceKm(Math.round(loc.distanceKm() * 10.0) / 10.0)
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private Optional<Long> selectAndDispatch(DispatchRecord record, List<Long> vehicleIds) {
        List<Long> candidates = new ArrayList<>(vehicleIds);

        if (record.getRequestedGrade() == VehicleGrade.NORMAL) {
            Collections.shuffle(candidates);
        }

        for (Long vehicleId : candidates) {
            String lockKey = LOCK_PREFIX + vehicleId;
            boolean locked = spinLockService.lock(lockKey, LOCK_TTL, "dispatch:" + record.getId());
            if (!locked) continue;

            try {
                Optional<VehicleState> stateOpt = vehicleStateRepository.findByVehicleId(vehicleId);
                if (stateOpt.isEmpty() || stateOpt.get().getStatus() != VehicleStatus.AVAILABLE) continue;

                Vehicle vehicle = vehicleRepository.findById(vehicleId)
                        .orElseThrow(() -> new ApiException(ErrorCode.VEHICLE_NOT_FOUND, "[selectAndDispatch] 등록된 차량이 아님"));
                VehicleState state = stateOpt.get();

                state.dispatch();
                record.dispatch(vehicle);

                dispatchSseService.sendDispatched(
                        record.getId(),
                        record.getPassenger().getName(),
                        vehicle.getVehicleNumber(),
                        vehicleId
                );

                log.info("[DispatchService] 배차 완료 dispatchId={} vehicleId={}", record.getId(), vehicleId);
                return Optional.of(vehicleId);

            } finally {
                spinLockService.unlock(lockKey);
            }
        }

        return Optional.empty();
    }
}