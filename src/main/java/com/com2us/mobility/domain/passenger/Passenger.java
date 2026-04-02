package com.com2us.mobility.domain.passenger;

import com.com2us.mobility.domain.vehicle.VehicleGrade;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "passengers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ─────────────────────────────────────────
    // 생성자 (정적 팩토리 메서드)
    // ─────────────────────────────────────────
    public static Passenger create(String name, Double latitude, Double longitude,
                                   VehicleGrade requestedGrade) {
        Passenger p = new Passenger();
        p.name = name;
        p.createdAt = LocalDateTime.now();
        return p;
    }
}