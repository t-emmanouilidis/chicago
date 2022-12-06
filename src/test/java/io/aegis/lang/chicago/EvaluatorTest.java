package io.aegis.lang.chicago;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class EvaluatorTest {

    @Parameters
    public static Collection<Object[]> data() {
        return List.of(
              new Object[][]{
                    {"5", new IntegerValue(5)},
                    {"true", BooleanValue.TRUE},
                    {"!!1", BooleanValue.TRUE},
                    {"-5", new IntegerValue(-5)},
                    {"(5 + 10 * 2 + 15 / 3) * 2 + -10", new IntegerValue(50)},
                    {"(1 < 2) == (2 > 1)", BooleanValue.TRUE},
                    {"if (true) { 10 }", new IntegerValue(10)},
                    {"if (false) { 10 }", NullValue.get()},
                    {"if (1 > 2) { 10 } else { 20 }", new IntegerValue(20)},
                    {"if (2 > 1) { 10 } else { 20 }", new IntegerValue(10)},
                    {"if (1) { 10 }", new IntegerValue(10)},
                    {"9; return 1 * 5; return 9;", new IntegerValue(5)},
                    {"if (10 > 1) { if (10 > 1) { return 10; } return 1; }", new IntegerValue(10)}
              }
        );
    }

    private final String input;
    private final Value expected;

    public EvaluatorTest(String input, Value expected) {
        this.input = input;
        this.expected = expected;
    }

    @Test
    public void shouldEvaluateToExpectedValue() {
        var parser = new Parser(input);
        Value evaluated = new Evaluator().evaluate(parser.parseProgram());
        assertThat(evaluated, is(equalTo(expected)));
    }

}
