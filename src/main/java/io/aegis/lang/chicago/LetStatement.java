package io.aegis.lang.chicago;

import java.util.Objects;

public record LetStatement(Token token, Identifier name, Expression value) implements Statement {

    public LetStatement {
        Objects.requireNonNull(token, "token can't be null");
        Objects.requireNonNull(name, "name can't be null");
    }

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(tokenLiteral()).append(" ").append(name.toString())
                .append(" = ");
        if (value != null) {
            sb.append(value);
        }
        sb.append(";");
        return sb.toString();
    }

}
