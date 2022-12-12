package io.aegis.lang.chicago;

import java.util.Objects;

public record Token(TokenType type, String literal) {

    public static final Token ASSIGN = new Token(TokenType.ASSIGN, "=");
    public static final Token PLUS = new Token(TokenType.PLUS, "+");
    public static final Token MINUS = new Token(TokenType.MINUS, "-");
    public static final Token BANG = new Token(TokenType.BANG, "!");
    public static final Token SLASH = new Token(TokenType.SLASH, "/");
    public static final Token ASTERISK = new Token(TokenType.ASTERISK, "*");
    public static final Token LESS_THAN = new Token(TokenType.LESS_THAN, "<");
    public static final Token GREATER_THAN = new Token(TokenType.GREATER_THAN, ">");
    public static final Token LET = new Token(TokenType.LET, "let");
    public static final Token SEMICOLON = new Token(TokenType.SEMICOLON, ";");
    public static final Token COMMA = new Token(TokenType.COMMA, ",");
    public static final Token LEFT_PARENTHESIS = new Token(TokenType.LPAREN, "(");
    public static final Token RIGHT_PARENTHESIS = new Token(TokenType.RPAREN, ")");
    public static final Token LEFT_BRACE = new Token(TokenType.LBRACE, "{");
    public static final Token RIGHT_BRACE = new Token(TokenType.RBRACE, "}");
    public static final Token LEFT_BRACKET = new Token(TokenType.LBRACKET, "[");
    public static final Token RIGHT_BRACKET = new Token(TokenType.RBRACKET, "]");
    public static final Token FUNCTION = new Token(TokenType.FUNCTION, "fn");
    public static final Token IF = new Token(TokenType.IF, "if");
    public static final Token TRUE = new Token(TokenType.TRUE, "true");
    public static final Token FALSE = new Token(TokenType.FALSE, "false");
    public static final Token ELSE = new Token(TokenType.ELSE, "else");
    public static final Token RETURN = new Token(TokenType.RETURN, "return");
    public static final Token EQUAL = new Token(TokenType.EQUAL, "==");
    public static final Token NOT_EQUAL = new Token(TokenType.NOT_EQUAL, "!=");
    public static final Token COLON = new Token(TokenType.COLON, ":");

    public Token(TokenType type, char literal) {
        this(type, String.valueOf(literal));
    }

    public Token {
        Objects.requireNonNull(type, "type can't be null");
        Objects.requireNonNull(literal, "literal can't be null");
    }

    public boolean isLastOne() {
        return TokenType.EOF.equals(type);
    }

    public boolean isNotLastOne() {
        return !isLastOne();
    }

    @Override
    public String toString() {
        return "Token{" + type + ", " + literal + "}";
    }

    public static Token newIdentifier(String literal) {
        return new Token(TokenType.IDENT, literal);
    }

    public static Token newInteger(String literal) {
        return new Token(TokenType.INT, literal);
    }

    public static Token newString(String literal) {
        return new Token(TokenType.STRING, literal);
    }

    public boolean sameAs(Token otherToken) {
        return this.equals(otherToken);
    }

    public boolean notSameAs(Token otherToken) {
        return !sameAs(otherToken);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Token)) {
            return false;
        }
        var otherToken = (Token) obj;
        return type.equals(otherToken.type) && literal.equals(otherToken.literal); 
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, literal);
    }

}
