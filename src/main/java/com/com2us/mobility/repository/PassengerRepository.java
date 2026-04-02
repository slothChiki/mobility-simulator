package com.com2us.mobility.repository;

import com.com2us.mobility.domain.passenger.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    // 기본 CRUD로 충분
}
