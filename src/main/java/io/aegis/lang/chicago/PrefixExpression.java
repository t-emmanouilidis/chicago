package io.aegis.lang.chicago;

import java.util.Objects;

public record PrefixExpression(Token token, String operator, Expression right) implements Expression {

    public PrefixExpression {
        Objects.requireNonNull(token, "token can't be null");
        Objects.requireNonNull(operator, "operator can't be null");
        Objects.requireNonNull(right, "right can't be null");
    }

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return "(" + operator + right.toString() + ")";
    }
}
