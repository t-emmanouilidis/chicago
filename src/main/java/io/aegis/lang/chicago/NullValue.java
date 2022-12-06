package io.aegis.lang.chicago;

public enum NullValue implements Value {

    INSTANCE;

    @Override
    public ValueType type() {
        return ValueType.NULL;
    }

    @Override
    public String inspect() {
        return "null";
    }

    @Override
    public boolean isTruthy() {
        return false;
    }

    public static NullValue get() {
        return INSTANCE;
    }

}
