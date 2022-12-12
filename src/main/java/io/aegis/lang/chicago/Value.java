package io.aegis.lang.chicago;

public interface Value {

    String type();

    String inspect();

    default <T extends Value> T as(Class<T> type) {
        return type.cast(this);
    }

    default boolean is(Class<?> type) {
        return type.isAssignableFrom(getClass());
    }

    default boolean isNot(Class<?> type) {
        return !is(type);
    }

    boolean isTruthy();

    default boolean isError() {
        return false;
    }

    default boolean isNull() {
        return false;
    }

    default boolean isNotNull() {
        return !isNull();
    }

}
