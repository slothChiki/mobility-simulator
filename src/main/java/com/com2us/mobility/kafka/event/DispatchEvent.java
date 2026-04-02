package com.com2us.mobility.kafka.event;

import com.com2us.mobility.domain.dispatch.DispatchStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DispatchEvent {

    private Long dispatchId;        // 파티션 Key + DB 조회용
    private DispatchStatus status;  // REQUESTED / DISPATCHED / BOARDED / REFUSED / CANCELLED / COMPLETED
    private LocalDateTime occurredAt;
}