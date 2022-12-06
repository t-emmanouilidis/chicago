package io.aegis.lang.chicago;

public record BooleanLiteral(Token token, boolean value) implements Expression {

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return tokenLiteral();
    }
}
