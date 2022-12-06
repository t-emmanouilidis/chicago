package io.aegis.lang.chicago;

import java.util.Objects;

public record ExpressionStatement(Token token, Expression expression) implements Statement {

    public ExpressionStatement {
        Objects.requireNonNull(token, "token can't be null");
    }

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    public <T extends Expression> T expressionAs(Class<T> type) {
        Objects.requireNonNull(type, "type can't be null");

        return type.cast(expression());
    }

    @Override
    public String toString() {
        if (expression != null) {
            return expression.toString();
        }
        return "";
    }

}
