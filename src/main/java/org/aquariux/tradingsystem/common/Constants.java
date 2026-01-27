package org.aquariux.tradingsystem.common;

public final class Constants {

    private Constants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String API_BASE_PATH = "/api/v1";

    public static final class DateTimeFormat {
        public static final String DEFAULT_DATETIME = "yyyy-MM-dd HH:mm:ss";
        public static final String DEFAULT_DATE = "yyyy-MM-dd";
    }

    public static final class ErrorCode {
        public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
        public static final String RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";
        public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
        public static final String BAD_REQUEST = "BAD_REQUEST";
    }
}
