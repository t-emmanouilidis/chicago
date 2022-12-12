package io.aegis.lang.chicago;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ParserTest {

    @Test
    public void shouldBeAbleToParseALetStatement() {
        // Set
        var input = "let x = 5;";
        var parser = new Parser(input);

        // Act
        var program = parser.parseProgram();

        // Assert
        assertFalse(parser.foundErrors());
        assertThat(program.statementCount(), is(equalTo(1)));
        var letStmt = program.iterator().nextStatementAs(LetStatement.class);
        assertThat(letStmt.tokenLiteral(), is(equalTo("let")));
        assertThat(letStmt.name().value(), is(equalTo("x")));
        assertThat(letStmt.name().tokenLiteral(), is(equalTo("x")));
        assertThat(letStmt.value().toString(), is(equalTo("5")));
    }

    @Test
    public void shouldBeAbleToParseAReturnStatement() {
        // Set
        var input = "return 5;";
        var parser = new Parser(input);

        // Act
        var program = parser.parseProgram();

        // Assert
        assertFalse(parser.foundErrors());
        assertThat(program.statementCount(), is(equalTo(1)));
        var returnStmt = program.iterator().nextStatementAs(ReturnStatement.class);
        assertThat(returnStmt.tokenLiteral(), is(equalTo("return")));
        assertThat(returnStmt.returnValue().toString(), is(equalTo("5")));
    }

    @Test
    public void shouldBeAbleToParseAnIdentifierExpression() {
        // Set
        var input = "foobar;";
        var parser = new Parser(input);

        // Act
        var program = parser.parseProgram();

        // Assert
        assertFalse(parser.foundErrors());
        assertThat(program.statementCount(), is(equalTo(1)));
        var identifier = program.iterator().nextStatementAs(ExpressionStatement.class).expressionAs(Identifier.class);
        assertThat(identifier.tokenLiteral(), is(equalTo("foobar")));
        assertThat(identifier.value(), is(equalTo("foobar")));
    }

    @Test
    public void shouldBeAbleToParseAnIntegerExpression() {
        // Set
        var input = "5;";
        var parser = new Parser(input);

        // Act
        var program = parser.parseProgram();

        // Assert
        assertFalse(parser.foundErrors());
        assertThat(program.statementCount(), is(equalTo(1)));
        var number = program.iterator().nextStatementAs(ExpressionStatement.class).expressionAs(IntegerLiteral.class);
        assertThat(number.token().type(), is(equalTo(TokenType.INT)));
        assertThat(number.tokenLiteral(), is(equalTo("5")));
        assertThat(number.value(), is(equalTo(5L)));
    }

    @Test
    public void shouldBeAbleToParseAPrefixExpression() {
        // Set
        var input = """
              !5;
              -15;
              """;
        var parser = new Parser(input);

        // Act
        var program = parser.parseProgram();

        // Assert
        parser.printErrors();
        assertFalse(parser.foundErrors());
        assertThat(program.statementCount(), is(equalTo(2)));
        var statementIterator = program.iterator();
        var expr = statementIterator.nextStatementAs(ExpressionStatement.class).expressionAs(PrefixExpression.class);
        assertThat(expr.operator(), is(equalTo("!")));
        assertThat(expr.right().toString(), is(equalTo("5")));
        expr = statementIterator.nextStatementAs(ExpressionStatement.class).expressionAs(PrefixExpression.class);
        assertThat(expr.operator(), is(equalTo("-")));
        assertThat(expr.right().toString(), is(equalTo("15")));
    }

    @Test
    public void shouldBeAbleToParseAnInfixExpression() {
        // Set
        var input = "5 + 5; 5 < 5; 5 != 5;";
        var parser = new Parser(input);

        // Act
        var program = parser.parseProgram();

        // Assert
        parser.printErrors();
        assertFalse(parser.foundErrors());
        assertThat(program.statementCount(), is(equalTo(3)));
        var iterator = program.iterator();
        assertNextInfixExpressionStatement(iterator, "+", "5", "5");
        assertNextInfixExpressionStatement(iterator, "<", "5", "5");
        assertNextInfixExpressionStatement(iterator, "!=", "5", "5");
    }

    private void assertNextInfixExpressionStatement(
          StatementIterator iterator, String expectedOperator, String expectedLeft, String expectedRight) {
        var expr = iterator.nextStatementAs(ExpressionStatement.class).expressionAs(InfixExpression.class);
        assertThat(expr.operator(), is(equalTo(expectedOperator)));
        assertThat(expr.left().toString(), is(equalTo(expectedLeft)));
        assertThat(expr.right().toString(), is(equalTo(expectedRight)));
    }

    @Test
    public void shouldBeAbleToParseAnInfixExpressionWithAPrefixLeft() {
        // Set
        var input = "-5 * 6;";
        var parser = new Parser(input);

        // Act
        var program = parser.parseProgram();

        // Assert
        parser.printErrors();
        assertFalse(parser.foundErrors());
        assertThat(program.statementCount(), is(equalTo(1)));
        var infix = program.iterator().nextStatementAs(ExpressionStatement.class).expressionAs(InfixExpression.class);
        assertThat(infix.operator(), is(equalTo("*")));
        assertThat(infix.right().toString(), is(equalTo("6")));
        var prefix = (PrefixExpression) infix.left();
        assertThat(prefix.operator(), is(equalTo("-")));
        assertThat(prefix.right().toString(), is(equalTo("5")));
    }

    @Test
    public void shouldBeAbleToParseABooleanExpression() {
        // Set
        var input = "true; false;";
        var parser = new Parser(input);

        // Act
        var program = parser.parseProgram();

        // Assert
        parser.printErrors();
        assertFalse(parser.foundErrors());
        assertThat(program.statementCount(), is(equalTo(2)));
        var iterator = program.iterator();
        var bool = iterator.nextStatementAs(ExpressionStatement.class).expressionAs(BooleanLiteral.class);
        assertTrue(bool.value());
        bool = iterator.nextStatementAs(ExpressionStatement.class).expressionAs(BooleanLiteral.class);
        assertFalse(bool.value());
    }

    @Test
    public void shouldBeAbleToParseAGroupedExpression() {
        var input = "(5 + 5) * 2;";
        var parser = new Parser(input);
        var program = parser.parseProgram();

        assertThat(program.toString(), is(equalTo("((5 + 5) * 2)")));
    }

    @Test
    public void shouldBeAbleToParseAnIfExpression() {
        var input = "if (x < y) { x }";
        var parser = new Parser(input);
        var program = parser.parseProgram();
        var expr = program.iterator().nextStatementAs(ExpressionStatement.class).expressionAs(IfExpression.class);
        assertThat(expr.condition().toString(), is(equalTo("(x < y)")));
        assertThat(expr.consequence().toString(), is(equalTo("x")));
        assertThat(expr.alternative(), is(nullValue()));
    }

    @Test
    public void shouldBeAbleToParseAnIfElseExpression() {
        var input = "if (x < y) { x } else { y }";
        var parser = new Parser(input);
        var program = parser.parseProgram();
        var expr = program.iterator().nextStatementAs(ExpressionStatement.class).expressionAs(IfExpression.class);
        assertThat(expr.condition().toString(), is(equalTo("(x < y)")));
        assertThat(expr.consequence().toString(), is(equalTo("x")));
        assertThat(expr.alternative().toString(), is(equalTo("y")));
    }

    @Test
    public void shouldBeAbleToParseAFunctionLiteral() {
        var input = "fn(x, y) { x + y; }";
        var parser = new Parser(input);
        var program = parser.parseProgram();
        var expr = program.iterator().nextStatementAs(ExpressionStatement.class).expressionAs(FunctionLiteral.class);
        assertThat(expr.parameters(), hasSize(2));
        assertThat(expr.body().iterator()
                    .nextStatementAs(ExpressionStatement.class).expressionAs(InfixExpression.class).toString(),
              is(equalTo("(x + y)")));
    }

    @Test
    public void shouldBeAbleToParseACallExpression() {
        var input = "add(1 + 1, 1);";
        var parser = new Parser(input);
        var program = parser.parseProgram();
        parser.printErrors();
        var expr = program.iterator().nextStatementAs(ExpressionStatement.class).expressionAs(CallExpression.class);
        assertThat(expr.function().toString(), is(equalTo("add")));
        assertThat(expr.arguments(), hasSize(2));
        var iterator = expr.arguments().iterator();
        assertThat(iterator.next().toString(), is(equalTo("(1 + 1)")));
        assertThat(iterator.next().toString(), is(equalTo("1")));
    }

    @Test
    public void shouldBeAbleToParseAStringLiteral() {
        var input = "\"hello world\"";
        var parser = new Parser(input);
        var program = parser.parseProgram();
        var expr = program.iterator().nextStatementAs(ExpressionStatement.class).expressionAs(StringLiteral.class);
        assertThat(expr.token().type(), is(equalTo(TokenType.STRING)));
        assertThat(expr.value(), is(equalTo("hello world")));
    }

    @Test
    public void shouldBeAbleToParseAnArray() {
        var input = "[1, 2 * 3]";
        var parser = new Parser(input);
        var program = parser.parseProgram();
        var arrayLiteral = program.iterator()
              .nextStatementAs(ExpressionStatement.class)
              .expressionAs(ArrayLiteral.class);
        var elementIterator = arrayLiteral.iterator();
        assertThat(elementIterator.next().toString(), is(equalTo("1")));
        assertThat(elementIterator.next().toString(), is(equalTo("(2 * 3)")));
    }

    @Test
    public void shouldBeAbleToParseAnIndexExpression() {
        var input = "myArray[1 + 1]";
        var parser = new Parser(input);
        var program = parser.parseProgram();
        var indexExpr = program.iterator().nextStatementAs(ExpressionStatement.class).expressionAs(IndexExpression.class);
        assertThat(indexExpr.left().getClass(), is(equalTo(Identifier.class)));
        assertThat(indexExpr.index().getClass(), is(equalTo(InfixExpression.class)));
    }

}
