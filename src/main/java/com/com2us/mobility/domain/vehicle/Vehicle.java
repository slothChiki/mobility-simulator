package com.com2us.mobility.domain.vehicle;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String vehicleNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleGrade vehicleGrade;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ─────────────────────────────────────────
    // 생성자 (정적 팩토리 메서드)
    // ─────────────────────────────────────────
    public static Vehicle create(String vehicleNumber, VehicleGrade vehicleGrade) {
        Vehicle v = new Vehicle();
        v.vehicleNumber = vehicleNumber;
        v.vehicleGrade = vehicleGrade;
        v.createdAt = LocalDateTime.now();
        return v;
    }

    // ─────────────────────────────────────────
    // 비즈니스 메서드
    // ─────────────────────────────────────────

    /** 거절 가능 여부 */
    public boolean isRefusable() {
        return this.vehicleGrade.isRefusable();
    }
}