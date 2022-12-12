package io.aegis.lang.chicago;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

public class Lexer {

    private final String input;
    private int currentPosition;
    private int nextPosition;
    private char current;

    public Lexer(String input) {
        requireNonNull(input, "input can't be null");
        if (input.isBlank()) {
            throw new IllegalArgumentException("Cannot lex empty input");
        }
        this.input = input;
        readNextChar();
    }

    public Token nextToken() {
        skipWhitespace();

        Token token;
        switch (current) {
            case '=':
                if (peekNextChar() == '=') {
                    readNextChar();
                    token = Token.EQUAL;
                } else {
                    token = Token.ASSIGN;
                }
                break;
            case ';':
                token = Token.SEMICOLON;
                break;
            case ':':
                token = Token.COLON;
                break;
            case '(':
                token = Token.LEFT_PARENTHESIS;
                break;
            case ')':
                token = Token.RIGHT_PARENTHESIS;
                break;
            case ',':
                token = Token.COMMA;
                break;
            case '+':
                token = Token.PLUS;
                break;
            case '{':
                token = Token.LEFT_BRACE;
                break;
            case '}':
                token = Token.RIGHT_BRACE;
                break;
            case '[':
                token = Token.LEFT_BRACKET;
                break;
            case ']':
                token = Token.RIGHT_BRACKET;
                break;
            case '<':
                token = Token.LESS_THAN;
                break;
            case '>':
                token = Token.GREATER_THAN;
                break;
            case '!':
                if (peekNextChar() == '=') {
                    readNextChar();
                    token = Token.NOT_EQUAL;
                } else {
                    token = Token.BANG;
                }
                break;
            case '-':
                token = Token.MINUS;
                break;
            case '/':
                token = Token.SLASH;
                break;
            case '*':
                token = Token.ASTERISK;
                break;
            case '"':
                token = Token.newString(readString());
                break;
            case 0:
                token = new Token(TokenType.EOF, "");
                break;
            default:
                if (isLetter(current)) {
                    String identifier = readIdentifier();
                    return new Token(lookupIdentifierType(identifier), identifier);
                } else if (isDigit(current)) {
                    return Token.newInteger(readNumber());
                } else {
                    token = new Token(TokenType.ILLEGAL, current);
                }
                break;
        }
        readNextChar();
        return token;
    }

    private String readString() {
        readNextChar();
        int start = currentPosition;

        while (current != '"' && current != 0) {
            readNextChar();
        }
        int end = currentPosition;
        return input.substring(start, end);
    }

    private void skipWhitespace() {
        while (current == ' ' || current == '\t' || current == '\n' || current == '\r') {
            readNextChar();
        }
    }

    private TokenType lookupIdentifierType(String literal) {
        Objects.requireNonNull(literal, "literal can't be null");

        return TokenType.from(literal).orElse(TokenType.IDENT);
    }

    private String readIdentifier() {
        var start = currentPosition;
        while (isLetter(current)) {
            readNextChar();
        }
        return input.substring(start, currentPosition);
    }

    private String readNumber() {
        var start = currentPosition;
        while (isDigit(current)) {
            readNextChar();
        }
        return input.substring(start, currentPosition);
    }

    private void readNextChar() {
        if (nextPosition >= input.length()) {
            current = 0;
        } else {
            current = input.charAt(nextPosition);
        }
        currentPosition = nextPosition;
        nextPosition++;
    }

    private char peekNextChar() {
        if (nextPosition >= input.length()) {
            return 0;
        } else {
            return input.charAt(nextPosition);
        }
    }

    private boolean isLetter(char character) {
        return ('a' <= character && character <= 'z') ||
              ('A' <= character && character <= 'Z') ||
              character == '_';
    }

    private boolean isDigit(char character) {
        return '0' <= character && character <= '9';
    }

}
