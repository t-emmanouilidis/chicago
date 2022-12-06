package io.aegis.lang.chicago;

public enum BooleanValue implements Value {

    TRUE(true),
    FALSE(false);

    private final boolean value;

    BooleanValue(boolean value) {
        this.value = value;
    }

    public boolean value() {
        return value;
    }

    @Override
    public ValueType type() {
        return ValueType.BOOLEAN;
    }

    @Override
    public String inspect() {
        return String.valueOf(value);
    }

    @Override
    public boolean isTruthy() {
        return value;
    }

    public static BooleanValue from(boolean nativeValue) {
        return nativeValue ? TRUE : FALSE;
    }

}
