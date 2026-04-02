package com.com2us.mobility.domain.vehicle;

public enum VehicleGrade {
    NORMAL(true),   // 일반 — 거절 가능
    BLUE(false),    // 블루 — 거절 불가
    BLACK(false);   // 블랙 — 거절 불가 + 프리미엄

    private final boolean refusable;

    VehicleGrade(boolean refusable) {
        this.refusable = refusable;
    }

    public boolean isRefusable() {
        return refusable;
    }
}