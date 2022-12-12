package io.aegis.lang.chicago;

public enum NullValue implements Value {

    INSTANCE;

    @Override
    public String type() {
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

    @Override
    public boolean isNull() {
        return true;
    }

    public static NullValue get() {
        return INSTANCE;
    }

    @Override
    public String toString() {
        return inspect();
    }
}
