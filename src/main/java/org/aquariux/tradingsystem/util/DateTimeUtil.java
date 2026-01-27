package org.aquariux.tradingsystem.util;

import org.aquariux.tradingsystem.common.Constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtil {

    private DateTimeUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static final DateTimeFormatter DEFAULT_DATETIME_FORMATTER = 
            DateTimeFormatter.ofPattern(Constants.DateTimeFormat.DEFAULT_DATETIME);

    private static final DateTimeFormatter DEFAULT_DATE_FORMATTER = 
            DateTimeFormatter.ofPattern(Constants.DateTimeFormat.DEFAULT_DATE);

    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DEFAULT_DATETIME_FORMATTER);
    }

    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DEFAULT_DATETIME_FORMATTER);
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}
