package io.aegis.lang.chicago;

import java.util.Objects;

public record Identifier(Token token, String value) implements Expression {

    public Identifier {
        Objects.requireNonNull(token, "token can't be null");
        Objects.requireNonNull(value, "value can't be null");
    }

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return value;
    }

}
