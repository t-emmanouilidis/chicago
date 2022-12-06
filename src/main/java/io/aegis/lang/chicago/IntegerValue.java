package io.aegis.lang.chicago;

public record IntegerValue(long value) implements Value {

    @Override
    public ValueType type() {
        return ValueType.INTEGER;
    }

    @Override
    public String inspect() {
        return String.valueOf(value);
    }

    @Override
    public boolean isTruthy() {
        return value != 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof IntegerValue integer && value == integer.value;
    }

    @Override
    public int hashCode() {
        return (int) (value ^ (value >>> 32));
    }
}
