package io.aegis.lang.chicago;

import java.util.Objects;

public record StringLiteral(Token token, String value) implements Expression {

    public StringLiteral {
        Objects.requireNonNull(token, "token can't be null");
        Objects.requireNonNull(value, "value can't be null");
    }

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return tokenLiteral();
    }

}
