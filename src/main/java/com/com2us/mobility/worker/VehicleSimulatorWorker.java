package com.com2us.mobility.worker;

import com.com2us.mobility.core.lock.SpinLockService;
import com.com2us.mobility.domain.vehicle.Vehicle;
import com.com2us.mobility.kafka.event.LocationEvent;
import com.com2us.mobility.kafka.producer.LocationProducer;
import com.com2us.mobility.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class VehicleSimulatorWorker {

    private static final String LOCK_KEY = "lock:vehicle-simulator";
    private static final Duration LOCK_TTL = Duration.ofSeconds(1);

//    // 서울 좌표 범위
//    private static final double LAT_MIN = 37.4;
//    private static final double LAT_MAX = 37.7;
//    private static final double LNG_MIN = 126.8;
//    private static final double LNG_MAX = 127.2;
    // 금천구 좌표 범위
    private static final double LAT_MIN = 37.430;
    private static final double LAT_MAX = 37.490;
    private static final double LNG_MIN = 126.870;
    private static final double LNG_MAX = 126.930;

    // 차량별 현재 위치 (메모리)
    private final Map<Long, double[]> locationMap = new ConcurrentHashMap<>();

    private boolean working = false;

    private final VehicleRepository vehicleRepository;
    private final LocationProducer locationProducer;
    private final SpinLockService spinLockService;

    @Scheduled(fixedDelay = 500) // 0.5초
    public void process() {
        if (working) return;
        working = true;

        boolean locked = spinLockService.lock(LOCK_KEY, LOCK_TTL, "simulator");
        if (!locked) {
            working = false;
            return;
        }

        try {
            simulate();
        } catch (Exception e) {
            log.error("[VehicleSimulatorWorker] 에러 발생 error={}", e.getMessage());
        } finally {
            spinLockService.unlock(LOCK_KEY);
            working = false;
        }
    }

    // ─────────────────────────────────────────
    // 차량 위치 시뮬레이션
    // ─────────────────────────────────────────
    private void simulate() {
        List<Vehicle> vehicles = vehicleRepository.findAll();

        for (Vehicle vehicle : vehicles) {
            double[] location = locationMap.computeIfAbsent(
                    vehicle.getId(),
                    id -> new double[]{randomLat(), randomLng()}  // 최초 위치 랜덤 생성
            );

            // 조금씩 이동
            location[0] = clamp(location[0] + move(), LAT_MIN, LAT_MAX);
            location[1] = clamp(location[1] + move(), LNG_MIN, LNG_MAX);

            locationProducer.send(LocationEvent.builder()
                    .vehicleId(vehicle.getId())
                    .latitude(location[0])
                    .longitude(location[1])
                    .occurredAt(LocalDateTime.now())
                    .build());
        }

        log.debug("[VehicleSimulatorWorker] 차량 {}대 위치 발행 완료", vehicles.size());
    }

    // ─────────────────────────────────────────
    // 유틸
    // ─────────────────────────────────────────

    /** 0.001 범위 안에서 랜덤 이동 */
    private double move() {
        return (Math.random() - 0.5) * 0.001;
    }

    private double randomLat() {
        return LAT_MIN + Math.random() * (LAT_MAX - LAT_MIN);
    }

    private double randomLng() {
        return LNG_MIN + Math.random() * (LNG_MAX - LNG_MIN);
    }

    /** 좌표가 범위를 벗어나지 않도록 */
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}