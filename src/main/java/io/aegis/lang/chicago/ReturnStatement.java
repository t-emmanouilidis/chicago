package io.aegis.lang.chicago;

import java.util.Objects;

public record ReturnStatement(Token token, Expression returnValue) implements Statement {

    public ReturnStatement {
        Objects.requireNonNull(token, "token can't be null");
    }

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append(tokenLiteral())
                .append(" ");
        if (returnValue != null) {
            sb.append(returnValue);
        }
        sb.append(";");
        return sb.toString();
    }

}
