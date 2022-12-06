package io.aegis.lang.chicago;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Objects;

public record FunctionLiteral(Token token, List<Identifier> parameters, BlockStatement body)
      implements Expression {

    public FunctionLiteral {
        Objects.requireNonNull(token, "token can't be null");
        Objects.requireNonNull(parameters, "parameters can't be null");
        Objects.requireNonNull(body, "body can't be null");
    }

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return new StringBuilder()
              .append(tokenLiteral())
              .append("(")
              .append(parameters.stream().map(Identifier::toString).collect(joining(", ")))
              .append(") ")
              .append(body.toString())
              .toString();
    }
}
