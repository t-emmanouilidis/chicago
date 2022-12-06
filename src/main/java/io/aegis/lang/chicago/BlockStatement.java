package io.aegis.lang.chicago;

import java.util.List;
import java.util.Objects;

public record BlockStatement(Token token, List<Statement> statements)
      implements Statement, Iterable<Statement> {

    public BlockStatement {
        Objects.requireNonNull(token, "token can't be null");
        Objects.requireNonNull(statements, "statements can't be null");
    }

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public StatementIterator iterator() {
        return new StatementIterator(statements);
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        for (Statement statement : statements) {
            sb.append(statement.toString());
        }
        return sb.toString();
    }
}
