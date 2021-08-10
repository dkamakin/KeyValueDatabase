package com.infotecs.intern.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeStampGenerator {

    private static final String TIME_STAMP = "yyyy-MM-dd.HH:mm:ss";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(TIME_STAMP);

    private TimeStampGenerator() {
    }

    public static String getCurrentTimeStamp(Integer ttl) {
        return LocalDateTime.now().plusSeconds(ttl).format(dateTimeFormatter);
    }
}
