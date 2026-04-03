package com.com2us.mobility.domain.dispatch;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dispatch_retries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class DispatchRetry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_record_id", nullable = false, unique = true)
    private DispatchRecord dispatchRecord;

    @Column(nullable = false)
    private int attemptCount;

    @Enumerated(EnumType.STRING)
    private DispatchRetryReason reason;  // 최종 실패 시에만 업데이트

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // ─────────────────────────────────────────
    // 생성자 (정적 팩토리 메서드)
    // ─────────────────────────────────────────
    public static DispatchRetry create(DispatchRecord dispatchRecord) {
        DispatchRetry retry = new DispatchRetry();
        retry.dispatchRecord = dispatchRecord;
        retry.attemptCount = 1;
        retry.reason = null;
        retry.createdAt = LocalDateTime.now();
        retry.updatedAt = LocalDateTime.now();
        return retry;
    }

    // ─────────────────────────────────────────
    // 비즈니스 메서드
    // ─────────────────────────────────────────

    /** 재시도 횟수 증가 */
    public void increment() {
        this.attemptCount++;
        this.updatedAt = LocalDateTime.now();
    }

    /** 최종 실패 시 reason 업데이트 */
    public void fail(DispatchRetryReason reason) {
        this.reason = reason;
        this.updatedAt = LocalDateTime.now();
    }
}