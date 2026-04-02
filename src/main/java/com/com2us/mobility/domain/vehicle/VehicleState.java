package com.com2us.mobility.domain.vehicle;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_states")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class VehicleState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false, unique = true)
    private Vehicle vehicle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ─────────────────────────────────────────
    // 생성자 (정적 팩토리 메서드)
    // ─────────────────────────────────────────
    public static VehicleState create(Vehicle vehicle) {
        VehicleState vs = new VehicleState();
        vs.vehicle = vehicle;
        vs.status = VehicleStatus.AVAILABLE;
        vs.updatedAt = LocalDateTime.now();
        return vs;
    }

    // ─────────────────────────────────────────
    // 비즈니스 메서드
    // ─────────────────────────────────────────

    /** 배차 */
    public void dispatch() {
        this.status = VehicleStatus.DISPATCHED;
        this.updatedAt = LocalDateTime.now();
    }

    /** 운행 완료 → 다시 배차 가능 */
    public void complete() {
        this.status = VehicleStatus.AVAILABLE;
        this.updatedAt = LocalDateTime.now();
    }

    /** 오프라인 */
    public void offline() {
        this.status = VehicleStatus.OFFLINE;
        this.updatedAt = LocalDateTime.now();
    }

    /** 배차 가능 여부 */
    public boolean isAvailable() {
        return this.status == VehicleStatus.AVAILABLE;
    }
}