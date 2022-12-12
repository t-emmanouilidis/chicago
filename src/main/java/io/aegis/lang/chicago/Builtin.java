package io.aegis.lang.chicago;

import java.util.Objects;

public record Builtin(BuiltinFunction function) implements Value {

    public Builtin {
        Objects.requireNonNull(function, "function can't be null");
    }

    @Override
    public String type() {
        return ValueType.BUILTIN;
    }

    @Override
    public String inspect() {
        return "builtin function";
    }

    @Override
    public boolean isTruthy() {
        return true;
    }
    
}
