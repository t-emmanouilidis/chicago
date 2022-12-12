package io.aegis.lang.chicago;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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
                    {"if (10 > 1) { if (10 > 1) { return 10; } return 1; }", new IntegerValue(10)},
                    {"5 + true;", new Error("Type mismatch: INTEGER + BOOLEAN")},
                    {"-true;", new Error("Unknown operator: -BOOLEAN")},
                    {"true + true;", new Error("Unknown operator: BOOLEAN + BOOLEAN")},
                    {"5; true + false; 5;", new Error("Unknown operator: BOOLEAN + BOOLEAN")},
                    {"5 + true; 5;", new Error("Type mismatch: INTEGER + BOOLEAN")},
                    {"let a = 5; a;", new IntegerValue(5)},
                    {"let identity = fn(x) { x; }; identity(5);", new IntegerValue(5)},
                    {"fn(x) { return x + 5; }(10 + 5);", new IntegerValue(20)},
                    {"let f=fn(x) { fn(y) { x + y }; }; let addTwo = f(2); addTwo(2);", new IntegerValue(4)},
                    {"\"hello world\";", new StringValue("hello world")},
                    {"\"hello\" + \" \" + \"world\";", new StringValue("hello world")},
                    {"\"hello\" - \"world\"", new Error("Unknown operator: STRING - STRING")},
                    {"len(\"four\");", new IntegerValue(4)},
                    {"len(1);", new Error("Argument to 'len' not supported, got INTEGER")},
                    {"len(\"one\", \"two\");", new Error("Wrong number of arguments. Expected 1, but got 2")},
                    {"[1, 2 + 3];", new Array(List.of(new IntegerValue(1), new IntegerValue(5)))},
                    {"[1, 2][0];", new IntegerValue(1)},
                    {"let arr = [1, 2]; arr[0];", new IntegerValue(1)},
                    {"[1, 2][2];", NullValue.get()},
                    {"len([1, 2, 3]);", new IntegerValue(3)},
                    {"first([1, 2, 3]);", new IntegerValue(1)},
                    {"last([1, 2, \"hello\"]);", new StringValue("hello")},
                    {"tail([1, 2]);", new Array(List.of(new IntegerValue(2)))},
                    {"push([1], 2);", new Array(List.of(IntegerValue.of(1), IntegerValue.of(2)))},
                    {"{\"one\": 1 + 1};", new Dictionary(Map.of(new StringValue("one"), new IntegerValue(2)))},
                    {"{\"one\": 1}[\"one\"]", new IntegerValue(1)},
                    {"let map = {\"one\": 1}; map[\"one\"];", new IntegerValue(1)}
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
        Value evaluated = new Evaluator().evaluate(new Environment(), parser.parseProgram());
        assertThat(evaluated, is(equalTo(expected)));
    }

}
