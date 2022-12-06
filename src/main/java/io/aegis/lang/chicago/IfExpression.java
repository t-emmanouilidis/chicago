package io.aegis.lang.chicago;

import java.util.Objects;

public record IfExpression(
      Token token, Expression condition, BlockStatement consequence,
      BlockStatement alternative) implements Expression {

    public IfExpression {
        Objects.requireNonNull(token, "token can't be null");
        Objects.requireNonNull(condition, "condition can't be null");
        Objects.requireNonNull(consequence, "consequence can't be null");
    }

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    public boolean hasAlternative() {
        return alternative != null;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder().append("if ")
              .append(condition.toString())
              .append(" ")
              .append(consequence.toString());
        if (alternative != null) {
            sb.append(" else ").append(alternative);
        }
        return sb.toString();
    }
}
