package io.aegis.lang.chicago;

import java.util.HashMap;
import java.util.Map;

public class Environment {

    private final Environment parent;
    private final Map<String, Value> map = new HashMap<>();

    public Environment() {
        this(null);
    }

    public Environment(Environment parent) {
        this.parent = parent;
    }

    public Object set(String name, Value value) {
        map.put(name, value);
        return value;
    }

    public Value get(String name) {
        var value = map.get(name);
        if (value == null) {
            if (parent != null) {
                return parent.get(name);
            }
            return NullValue.get();
        }
        return value;
    }

}
