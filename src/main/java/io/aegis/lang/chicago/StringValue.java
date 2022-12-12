package io.aegis.lang.chicago;

import java.util.Objects;

public record StringValue(String value) implements Value {

    public StringValue {
        Objects.requireNonNull(value, "value can't be null");
    }

    @Override
    public String type() {
        return ValueType.STRING;
    }

    @Override
    public String inspect() {
        return value;
    }

    @Override
    public boolean isTruthy() {
        return !value.isBlank();
    }

}
