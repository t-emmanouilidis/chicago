package io.aegis.lang.chicago;

import static java.util.stream.Collectors.joining;

import java.util.Map;
import java.util.Objects;

public record DictionaryLiteral(Token token, Map<Expression, Expression> pairs) implements Expression {

    public DictionaryLiteral {
        Objects.requireNonNull(token, "token can't be null");
        Objects.requireNonNull(pairs, "pairs can't be null");
    }

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    public int size() {
        return pairs.size();
    }

    @Override
    public String toString() {
        return pairs.entrySet()
              .stream()
              .map(e -> e.getKey() + ":" + e.getValue())
              .collect(joining(", ", "{", "}"));
    }

    public boolean isEmpty() {
        return pairs.isEmpty();
    }
}
