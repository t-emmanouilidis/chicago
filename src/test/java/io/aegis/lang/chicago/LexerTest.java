package io.aegis.lang.chicago;

import static io.aegis.lang.chicago.Token.ASSIGN;
import static io.aegis.lang.chicago.Token.ASTERISK;
import static io.aegis.lang.chicago.Token.BANG;
import static io.aegis.lang.chicago.Token.COMMA;
import static io.aegis.lang.chicago.Token.ELSE;
import static io.aegis.lang.chicago.Token.EQUAL;
import static io.aegis.lang.chicago.Token.FALSE;
import static io.aegis.lang.chicago.Token.FUNCTION;
import static io.aegis.lang.chicago.Token.GREATER_THAN;
import static io.aegis.lang.chicago.Token.IF;
import static io.aegis.lang.chicago.Token.LEFT_BRACE;
import static io.aegis.lang.chicago.Token.LEFT_BRACKET;
import static io.aegis.lang.chicago.Token.LEFT_PARENTHESIS;
import static io.aegis.lang.chicago.Token.LESS_THAN;
import static io.aegis.lang.chicago.Token.LET;
import static io.aegis.lang.chicago.Token.MINUS;
import static io.aegis.lang.chicago.Token.NOT_EQUAL;
import static io.aegis.lang.chicago.Token.PLUS;
import static io.aegis.lang.chicago.Token.RETURN;
import static io.aegis.lang.chicago.Token.RIGHT_BRACE;
import static io.aegis.lang.chicago.Token.RIGHT_BRACKET;
import static io.aegis.lang.chicago.Token.RIGHT_PARENTHESIS;
import static io.aegis.lang.chicago.Token.SEMICOLON;
import static io.aegis.lang.chicago.Token.SLASH;
import static io.aegis.lang.chicago.Token.TRUE;
import static io.aegis.lang.chicago.Token.newIdentifier;
import static io.aegis.lang.chicago.Token.newInteger;
import static io.aegis.lang.chicago.Token.newString;
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
              "foobar"
              "foo bar"
              [1, 2]
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
              MINUS,
              SLASH,
              ASTERISK,
              newInteger("5"),
              SEMICOLON,
              newInteger("5"),
              LESS_THAN,
              newInteger("10"),
              GREATER_THAN,
              newInteger("5"),
              SEMICOLON,
              IF,
              LEFT_PARENTHESIS,
              newInteger("5"),
              LESS_THAN,
              newInteger("10"),
              RIGHT_PARENTHESIS,
              LEFT_BRACE,
              RETURN,
              TRUE,
              SEMICOLON,
              RIGHT_BRACE,
              ELSE,
              LEFT_BRACE,
              RETURN,
              FALSE,
              SEMICOLON,
              RIGHT_BRACE,
              newInteger("10"),
              EQUAL,
              newInteger("10"),
              SEMICOLON,
              newInteger("10"),
              NOT_EQUAL,
              newInteger("9"),
              SEMICOLON,
              newString("foobar"),
              newString("foo bar"),
              LEFT_BRACKET,
              newInteger("1"),
              COMMA,
              newInteger("2"),
              RIGHT_BRACKET,
              new Token(TokenType.EOF, "")
        );
        var lexer = new Lexer(input);
        for (Token expected : expectedTokens) {
            var got = lexer.nextToken();
            assertThat(got, is(equalTo(expected)));
        }
    }
}