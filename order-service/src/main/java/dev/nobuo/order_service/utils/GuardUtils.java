package dev.nobuo.order_service.utils;

public final class GuardUtils {
    private GuardUtils() {
        throw new UnsupportedOperationException("Cannot instantiate a utility class");
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || isEmpty(value);
    }

    public static boolean isEmpty(String value) {
        return value.trim().isEmpty();
    }
}
