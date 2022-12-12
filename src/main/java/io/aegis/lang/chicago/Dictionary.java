package io.aegis.lang.chicago;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public record Dictionary(Map<Value, Value> pairs) implements Value {

    public static final Dictionary EMPTY = new Dictionary(Map.of());

    @Override
    public String type() {
        return ValueType.DICTIONARY;
    }

    public Value get(Value key) {
        Objects.requireNonNull(key, "key can't be null");

        return pairs.getOrDefault(key, NullValue.get());
    }

    @Override
    public String inspect() {
        return pairs.entrySet()
              .stream()
              .map(e -> e.getKey().inspect() + ": " + e.getValue().inspect())
              .collect(Collectors.joining(", ", "{", "}"));
    }

    @Override
    public boolean isTruthy() {
        return !pairs.isEmpty();
    }
}
