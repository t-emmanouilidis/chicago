package io.aegis.lang.chicago;

public interface Value {

    ValueType type();

    String inspect();

    default <T extends Value> T as(Class<T> type) {
        return type.cast(this);
    }

    default boolean is(Class<?> type) {
        return type.isAssignableFrom(getClass());
    }

    boolean isTruthy();

}
