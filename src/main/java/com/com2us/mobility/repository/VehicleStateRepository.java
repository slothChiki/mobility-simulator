package com.com2us.mobility.repository;

import com.com2us.mobility.domain.vehicle.VehicleGrade;
import com.com2us.mobility.domain.vehicle.VehicleState;
import com.com2us.mobility.domain.vehicle.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleStateRepository extends JpaRepository<VehicleState, Long> {

    // AVAILABLE 상태인 전체 차량
    List<VehicleState> findByStatus(VehicleStatus status);

    // 특정 등급 + AVAILABLE 차량 (BLUE/BLACK 콜 처리 시)
    List<VehicleState> findByStatusAndVehicle_VehicleGrade(VehicleStatus status, VehicleGrade grade);

    // 특정 차량의 상태 조회
    Optional<VehicleState> findByVehicleId(Long vehicleId);
}