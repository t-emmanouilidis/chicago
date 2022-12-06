package io.aegis.lang.chicago;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.Test;

public class ProgramTest {
    
    @Test
    public void shouldBeAbleToPrintAProgramAsString() {
        // Set
        var program = new Program(List.of(
            new LetStatement(Token.LET, 
            new Identifier(Token.newIdentifier("myVar"), "myVar"),
            new Identifier(Token.newIdentifier("anotherVar"), "anotherVar"))));

        // Act
        var result = program.toString();

        // Assert
        assertThat(result, is(equalTo("let myVar = anotherVar;")));
    }

}
