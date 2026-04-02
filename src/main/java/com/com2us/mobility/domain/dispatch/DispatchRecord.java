package com.com2us.mobility.domain.dispatch;

import com.com2us.mobility.domain.passenger.Passenger;
import com.com2us.mobility.domain.vehicle.Vehicle;
import com.com2us.mobility.domain.vehicle.VehicleGrade;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dispatch_records")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class DispatchRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleGrade requestedGrade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DispatchStatus status;

    @Column(nullable = false)
    private Double pickupLatitude;

    @Column(nullable = false)
    private Double pickupLongitude;

    @Column(nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    private LocalDateTime boardedAt;
    private LocalDateTime refusedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // ─────────────────────────────────────────
    // 생성자 (정적 팩토리 메서드)
    // ─────────────────────────────────────────
    public static DispatchRecord create(Passenger passenger, Vehicle vehicle,
                                        VehicleGrade requestedGrade,
                                        Double pickupLatitude, Double pickupLongitude) {
        DispatchRecord dr = new DispatchRecord();
        dr.passenger = passenger;
        dr.vehicle = vehicle;
        dr.requestedGrade = requestedGrade;
        dr.status = DispatchStatus.REQUESTED;
        dr.pickupLatitude = pickupLatitude;
        dr.pickupLongitude = pickupLongitude;
        dr.requestedAt = LocalDateTime.now();
        dr.createdAt = LocalDateTime.now();
        dr.updatedAt = LocalDateTime.now();
        return dr;
    }

    // ─────────────────────────────────────────
    // 비즈니스 메서드
    // ─────────────────────────────────────────

    /** 배차 완료 */
    public void dispatch(Vehicle vehicle) {
        this.vehicle = vehicle;
        this.status = DispatchStatus.DISPATCHED;
        this.updatedAt = LocalDateTime.now();
    }

    /** 승객 승차 */
    public void board() {
        this.status = DispatchStatus.BOARDED;
        this.boardedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /** 운행 완료 */
    public void complete() {
        this.status = DispatchStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    /** 기사 거절 */
    public void refuse() {
        this.status = DispatchStatus.REFUSED;
        this.refusedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /** 승객 취소 */
    public void cancel() {
        this.status = DispatchStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }
}