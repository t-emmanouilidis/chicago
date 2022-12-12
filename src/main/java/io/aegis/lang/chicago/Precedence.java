package io.aegis.lang.chicago;

import java.util.Objects;

public class Precedence implements Comparable<Precedence> {

    public static final Precedence LOWEST = new Precedence("LOWEST", 0);
    public static final Precedence EQUAL = new Precedence("EQUAL", 1);
    public static final Precedence LESS_GREATER = new Precedence("LESS_GREATER", 2);
    public static final Precedence SUM = new Precedence("SUM", 3);
    public static final Precedence PRODUCT = new Precedence("PRODUCT", 4);
    public static final Precedence PREFIX = new Precedence("PREFIX", 5);
    public static final Precedence FUNCTION_CALL = new Precedence("FUNCTION_CALL", 6);
    public static final Precedence INDEX = new Precedence("INDEX", 7);

    private final String name;
    private final int value;

    private Precedence(String name, int value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public int compareTo(Precedence other) {
        Objects.requireNonNull(other, "other can't be null");

        return Integer.compare(this.value, other.value);
    }

    public boolean lessThan(Precedence other) {
        Objects.requireNonNull(other, "other can't be null");

        return this.compareTo(other) < 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
