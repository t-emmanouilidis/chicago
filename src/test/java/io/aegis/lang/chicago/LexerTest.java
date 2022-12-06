package io.aegis.lang.chicago;

import static io.aegis.lang.chicago.Token.ASSIGN;
import static io.aegis.lang.chicago.Token.BANG;
import static io.aegis.lang.chicago.Token.COMMA;
import static io.aegis.lang.chicago.Token.FUNCTION;
import static io.aegis.lang.chicago.Token.LEFT_BRACE;
import static io.aegis.lang.chicago.Token.LEFT_PARENTHESIS;
import static io.aegis.lang.chicago.Token.LET;
import static io.aegis.lang.chicago.Token.PLUS;
import static io.aegis.lang.chicago.Token.RIGHT_BRACE;
import static io.aegis.lang.chicago.Token.RIGHT_PARENTHESIS;
import static io.aegis.lang.chicago.Token.SEMICOLON;
import static io.aegis.lang.chicago.Token.newIdentifier;
import static io.aegis.lang.chicago.Token.newInteger;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import org.junit.Test;

public class LexerTest {

    @Test
    public void shouldBeAbleToLexSingleCharacters() {
        var input = "=+(){},;";
        var expectedTokens = List.of(
              ASSIGN, PLUS, LEFT_PARENTHESIS, RIGHT_PARENTHESIS, LEFT_BRACE, RIGHT_BRACE,
              COMMA, SEMICOLON, new Token(TokenType.EOF, ""));
        var lexer = new Lexer(input);
        for (Token expected : expectedTokens) {
            var got = lexer.nextToken();
            assertThat(got, is(equalTo(expected)));
        }
    }

    @Test
    public void shouldBeAbleToLexMoreComplexPhrases() {
        var input = """
              let x = 5;
              let add = fn(x, y) { x + y; };
              let r = add(x, x);
              !-/*5;
              5 < 10 > 5;
              if (5 < 10) {
                return true;
              } else {
                return false;
              }
              10 == 10;
              10 != 9;
              """;
        var expectedTokens = List.of(
              LET,
              newIdentifier("x"),
              ASSIGN,
              newInteger("5"),
              SEMICOLON,
              LET,
              newIdentifier("add"),
              ASSIGN,
              FUNCTION,
              LEFT_PARENTHESIS,
              newIdentifier("x"),
              COMMA,
              newIdentifier("y"),
              RIGHT_PARENTHESIS,
              LEFT_BRACE,
              newIdentifier("x"),
              PLUS,
              newIdentifier("y"),
              SEMICOLON,
              RIGHT_BRACE,
              SEMICOLON,
              LET,
              newIdentifier("r"),
              ASSIGN,
              newIdentifier("add"),
              LEFT_PARENTHESIS,
              newIdentifier("x"),
              COMMA,
              newIdentifier("x"),
              RIGHT_PARENTHESIS,
              SEMICOLON,
              BANG,
              Token.MINUS,
              Token.SLASH,
              Token.ASTERISK,
              newInteger("5"),
              SEMICOLON,
              newInteger("5"),
              Token.LESS_THAN,
              newInteger("10"),
              Token.GREATER_THAN,
              newInteger("5"),
              SEMICOLON,
              Token.IF,
              LEFT_PARENTHESIS,
              newInteger("5"),
              Token.LESS_THAN,
              newInteger("10"),
              RIGHT_PARENTHESIS,
              LEFT_BRACE,
              Token.RETURN,
              Token.TRUE,
              SEMICOLON,
              RIGHT_BRACE,
              Token.ELSE,
              LEFT_BRACE,
              Token.RETURN,
              Token.FALSE,
              SEMICOLON,
              RIGHT_BRACE,
              newInteger("10"),
              Token.EQUAL,
              newInteger("10"),
              SEMICOLON,
              newInteger("10"),
              Token.NOT_EQUAL,
              newInteger("9"),
              SEMICOLON
        );
        var lexer = new Lexer(input);
        for (Token expected : expectedTokens) {
            var got = lexer.nextToken();
            assertThat(got, is(equalTo(expected)));
        }
    }
}