package io.aegis.lang.chicago;

public record Error(String message) implements Value {

    @Override
    public String type() {
        return ValueType.ERROR;
    }

    @Override
    public String inspect() {
        return "ERROR: " + message;
    }

    @Override
    public boolean isTruthy() {
        return false;
    }

    @Override
    public boolean isError() {
        return true;
    }
}
