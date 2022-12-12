package io.aegis.lang.chicago;

public record IndexExpression(Token token, Expression left, Expression index) implements Expression {

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return "(" + left + ")[" + index + "]";
    }
}
