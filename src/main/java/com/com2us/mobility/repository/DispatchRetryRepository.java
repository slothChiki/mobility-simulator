package com.com2us.mobility.repository;

import com.com2us.mobility.domain.dispatch.DispatchRetry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DispatchRetryRepository extends JpaRepository<DispatchRetry, Long> {

    Optional<DispatchRetry> findByDispatchRecordId(Long dispatchRecordId);
}