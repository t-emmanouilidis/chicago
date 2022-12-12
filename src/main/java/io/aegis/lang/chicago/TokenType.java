package io.aegis.lang.chicago;

import java.util.Objects;
import java.util.Optional;

public enum TokenType {

    ILLEGAL("ILLEGAL"),
    EOF("EOF"),
    IDENT("IDENT"),
    INT("INT"),
    COMMA(","),
    SEMICOLON(";"),
    LPAREN("("),
    RPAREN(")"),
    LBRACE("{"),
    RBRACE("}"),
    LBRACKET("["),
    RBRACKET("]"),
    FUNCTION("fn"),
    LET("let"),
    TRUE("true"),
    FALSE("false"),
    IF("if"),
    ELSE("else"),
    RETURN("return"),
    ASSIGN("="),
    PLUS("+"),
    BANG("!"),
    MINUS("-"),
    SLASH("/"),
    ASTERISK("*"),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    NOT_EQUAL("!="),
    EQUAL("=="),
    STRING("STRING");

    private final String keyword;

    TokenType(String keyword) {
        this.keyword = keyword;
    }

    public static Optional<TokenType> from(String keyword) {
        Objects.requireNonNull(keyword, "keyword can't be null");

        for (TokenType type : TokenType.values()) {
            if (keyword.equals(type.keyword)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return name();
    }

}
