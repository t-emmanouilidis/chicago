package io.aegis.lang.chicago;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Iterator;

public class StatementIterator implements Iterator<Statement> {

    private final Iterator<Statement> iterator;

    public StatementIterator(Collection<Statement> statements) {
        requireNonNull(statements, "statements can't be null");
        this.iterator = statements.iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Statement next() {
        return iterator.next();
    }

    public <T extends Statement> T nextStatementAs(Class<T> type) {
        requireNonNull(type, "type can't be null");

        return type.cast(next());
    }

}
