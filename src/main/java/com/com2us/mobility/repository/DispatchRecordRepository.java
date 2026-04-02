package com.com2us.mobility.repository;

import com.com2us.mobility.domain.dispatch.DispatchRecord;
import com.com2us.mobility.domain.dispatch.DispatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DispatchRecordRepository extends JpaRepository<DispatchRecord, Long> {

    // 특정 승객의 배차 기록
    List<DispatchRecord> findByPassengerId(Long passengerId);

    // 특정 차량의 배차 기록
    List<DispatchRecord> findByVehicleId(Long vehicleId);

    // 상태별 배차 기록
    List<DispatchRecord> findByStatus(DispatchStatus status);
}