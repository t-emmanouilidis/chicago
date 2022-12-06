package io.aegis.lang.chicago;

import java.util.Objects;

public record IntegerLiteral(Token token, long value) implements Expression {

    public IntegerLiteral {
        Objects.requireNonNull(token, "token can't be null");
    }

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return token.literal();
    }

}
