package io.aegis.lang.chicago;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Objects;

public record Program(List<Statement> statements) implements Node, Iterable<Statement> {

    static final Program EMPTY = new Program(List.of());

    public Program {
        Objects.requireNonNull(statements, "statements can't be null");
    }

    @Override
    public String tokenLiteral() {
        if (statements == null || statements.isEmpty()) {
            return "";
        } else {
            return statements.iterator().next().tokenLiteral();
        }
    }

    public int statementCount() {
        return statements.size();
    }

    @Override
    public StatementIterator iterator() {
        return new StatementIterator(statements);
    }

    @Override
    public String toString() {
        return statements.stream().map(Object::toString).collect(joining(System.lineSeparator()));
    }

}
