package org.aquariux.tradingsystem.util;

import org.aquariux.tradingsystem.exception.BusinessException;

public final class ValidationUtil {

    private ValidationUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void requireNonNull(Object obj, String message) {
        if (obj == null) {
            throw new BusinessException(message);
        }
    }

    public static void requirePositive(Number number, String fieldName) {
        if (number == null || number.doubleValue() <= 0) {
            throw new BusinessException(fieldName + " must be positive");
        }
    }

    public static void requireNonEmpty(String str, String fieldName) {
        if (str == null || str.trim().isEmpty()) {
            throw new BusinessException(fieldName + " cannot be empty");
        }
    }

    public static void validateRange(Number value, Number min, Number max, String fieldName) {
        if (value == null) {
            throw new BusinessException(fieldName + " cannot be null");
        }
        double val = value.doubleValue();
        if (val < min.doubleValue() || val > max.doubleValue()) {
            throw new BusinessException(
                    String.format("%s must be between %s and %s", fieldName, min, max));
        }
    }
}
