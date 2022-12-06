package io.aegis.lang.chicago;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Objects;

public record CallExpression(Token token, Expression function, List<Expression> arguments)
      implements Expression {

    public CallExpression {
        Objects.requireNonNull(token, "token can't be null");
        Objects.requireNonNull(function, "function can't be null");
        Objects.requireNonNull(arguments, "arguments can't be null");
    }

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return new StringBuilder()
              .append(function.toString())
              .append("(")
              .append(arguments.stream().map(Expression::toString).collect(joining(", ")))
              .append(")")
              .toString();
    }
}
