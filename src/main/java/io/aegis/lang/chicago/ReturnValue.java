package io.aegis.lang.chicago;

public record ReturnValue(Value value) implements Value {

    @Override
    public ValueType type() {
        return ValueType.RETURN;
    }

    @Override
    public String inspect() {
        return value.inspect();
    }

    @Override
    public boolean isTruthy() {
        return value.isTruthy();
    }
}
