package io.aegis.lang.chicago;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public record ArrayLiteral(Token token, List<Expression> elements) implements Expression,
      Iterable<Expression> {

    public ArrayLiteral {
        Objects.requireNonNull(token, "token can't be null");
        Objects.requireNonNull(elements, "elements can't be null");
    }

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return elements.toString();
    }

    @Override
    public Iterator<Expression> iterator() {
        return elements.iterator();
    }
}
