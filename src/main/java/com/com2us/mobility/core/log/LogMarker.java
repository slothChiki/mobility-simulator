package com.com2us.mobility.core.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class LogMarker {

    private static final Logger log = LoggerFactory.getLogger(LogMarker.class);

    // 마커 정의
    public static final Marker DEBUG    = MarkerFactory.getMarker("DEBUG");

    private LogMarker() {}

    // ── VIEW ──────────────────────────────────────────
    public static void debug(String msg, Object... args) {
        log.debug(DEBUG, msg, args);
    }
}