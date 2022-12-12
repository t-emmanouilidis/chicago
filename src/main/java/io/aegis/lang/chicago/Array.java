package io.aegis.lang.chicago;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record Array(List<Value> elements) implements Value {

    public static final Array EMPTY = new Array(List.of());

    public Array {
        requireNonNull(elements, "elements can't be null");
    }

    @Override
    public String type() {
        return ValueType.ARRAY;
    }

    @Override
    public String inspect() {
        return elements.stream().map(Value::inspect).collect(Collectors.joining(", ", "[", "]"));
    }

    @Override
    public boolean isTruthy() {
        return !elements.isEmpty();
    }

    public int size() {
        return elements.size();
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public Value first() {
        return isEmpty() ? NullValue.get() : elements.get(0);
    }

    public Value last() {
        return isEmpty() ? NullValue.get() : elements.get(size() - 1);
    }

    public Array tail() {
        return isEmpty() || elements.size() == 1 ? EMPTY : new Array(elements.subList(1, size()));
    }

    public Array push(Value value) {
        requireNonNull(value, "value can't be null");

        List<Value> values = new ArrayList<>(elements);
        values.add(value);
        return new Array(values);
    }

    public Value get(IntegerValue index) {
        requireNonNull(index, "index can't be null");

        var idx = index.value();
        if (idx < 0 || idx >= size()) {
            return NullValue.get();
        }
        return elements.get((int) idx);
    }
}
