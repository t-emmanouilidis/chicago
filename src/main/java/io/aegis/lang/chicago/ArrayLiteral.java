package io.aegis.lang.chicago;

import java.util.Iterator;
import java.util.List;

public record ArrayLiteral(Token token, List<Expression> elements) implements Expression,
      Iterable<Expression> {

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
