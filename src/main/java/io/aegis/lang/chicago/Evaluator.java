package io.aegis.lang.chicago;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class Evaluator {

    private final BuiltinFunctions builtinFunctions = new BuiltinFunctions();

    public Value evaluate(Environment environment, Node node) {
        requireNonNull(environment, "environment can't be null");
        requireNonNull(node, "node can't be null");

        if (node instanceof Program program) {
            return evaluateProgram(environment, program);
        } else if (node instanceof BlockStatement block) {
            return evaluateBlockStatement(environment, block);
        } else if (node instanceof ExpressionStatement exprStmt) {
            return evaluate(environment, exprStmt.expression());
        } else if (node instanceof ReturnStatement returnStmt) {
            var value = evaluate(environment, returnStmt.returnValue());
            return value.isError() ? value : new ReturnValue(value);
        } else if (node instanceof IntegerLiteral integer) {
            return new IntegerValue(integer.value());
        } else if (node instanceof BooleanLiteral booleanLiteral) {
            return BooleanValue.from(booleanLiteral.value());
        } else if (node instanceof PrefixExpression prefix) {
            var rightValue = evaluate(environment, prefix.right());
            return rightValue.isError() ? rightValue : evaluatePrefixExpression(prefix.operator(), rightValue);
        } else if (node instanceof InfixExpression infix) {
            var left = evaluate(environment, infix.left());
            if (left.isError()) {
                return left;
            }
            var right = evaluate(environment, infix.right());
            if (right.isError()) {
                return right;
            }
            return evaluateInfixExpression(infix.operator(), left, right);
        } else if (node instanceof IfExpression ifExpression) {
            return evaluateIfExpression(environment, ifExpression);
        } else if (node instanceof LetStatement letStmt) {
            var value = evaluate(environment, letStmt.value());
            if (value.isError()) {
                return value;
            }
            environment.set(letStmt.name().value(), value);
        } else if (node instanceof Identifier identifier) {
            return evaluateIdentifier(environment, identifier);
        } else if (node instanceof FunctionLiteral fn) {
            return new Function(fn.parameters(), fn.body(), environment);
        } else if (node instanceof CallExpression call) {
            var functionValue = evaluate(environment, call.function());
            if (functionValue.isError()) {
                return functionValue;
            }
            var args = evaluateExpressions(environment, call.arguments());
            if (args.size() == 1 && args.iterator().next().isError()) {
                return args.iterator().next();
            }
            return applyFunction(functionValue, args);
        } else if (node instanceof StringLiteral stringLiteral) {
            return new StringValue(stringLiteral.value());
        } else if (node instanceof ArrayLiteral arrayLiteral) {
            var elements = evaluateExpressions(environment, arrayLiteral.elements());
            if (elements.size() == 1 && elements.iterator().next().isError()) {
                return elements.iterator().next();
            }
            return new Array(elements);
        } else if (node instanceof IndexExpression indexExpr) {
            var left = evaluate(environment, indexExpr.left());
            if (left.isError()) {
                return left;
            }
            var index = evaluate(environment, indexExpr.index());
            if (index.isError()) {
                return index;
            }
            return evaluateIndexExpression(left, index);
        } else if (node instanceof DictionaryLiteral dictionaryLiteral) {
            return evaluateDictionary(environment, dictionaryLiteral);
        }
        return NullValue.get();
    }

    private Value evaluateDictionary(Environment environment, DictionaryLiteral dictionaryLiteral) {
        requireNonNull(environment, "environment can't be null");
        requireNonNull(dictionaryLiteral, "dictionaryLiteral can't be null");

        if (dictionaryLiteral.isEmpty()) {
            return Dictionary.EMPTY;
        }

        Map<Value, Value> map = new HashMap<>();
        for (Entry<Expression, Expression> entry : dictionaryLiteral.pairs().entrySet()) {
            var key = evaluate(environment, entry.getKey());
            if (key.isError()) {
                return key;
            }
            var value = evaluate(environment, entry.getValue());
            if (value.isError()) {
                return value;
            }
            map.put(key, value);
        }
        return new Dictionary(map);
    }

    private Value evaluateIndexExpression(Value left, Value index) {
        requireNonNull(left, "left can't be null");
        requireNonNull(index, "index can't be null");

        if (left.is(Array.class) && index.is(IntegerValue.class)) {
            return evaluateArrayIndexExpression(left, index);
        } else if (left.is(Dictionary.class)) {
            return evaluateDictionaryIndexExpression(left, index);
        } else {
            return newError("Index operator is not supported for: %s", left.type());
        }
    }

    private Value evaluateDictionaryIndexExpression(Value left, Value index) {
        return left.as(Dictionary.class).get(index);
    }

    private Value evaluateArrayIndexExpression(Value left, Value index) {
        var array = left.as(Array.class);
        var idx = index.as(IntegerValue.class);
        return array.get(idx);
    }

    private Value applyFunction(Value functionObject, List<Value> args) {
        requireNonNull(functionObject, "functionObject can't be null");
        requireNonNull(args, "args can't be null");

        if (functionObject.is(Function.class)) {
            var function = functionObject.as(Function.class);
            var fnEnv = new Environment(function.environment());
            var parameters = function.parameters();
            for (int i = 0, n = parameters.size(); i < n; i++) {
                fnEnv.set(parameters.get(i).value(), args.get(i));
            }
            var evaluated = evaluate(fnEnv, function.body());
            if (evaluated.is(ReturnValue.class)) {
                return evaluated.as(ReturnValue.class).value();
            }
            return evaluated;
        } else if (functionObject.is(Builtin.class)) {
            var builtin = functionObject.as(Builtin.class);
            return builtin.function().apply(args.toArray(new Value[0]));
        }
        return newError("Not a function: %s", functionObject.type());
    }

    private List<Value> evaluateExpressions(Environment environment, List<Expression> args) {
        List<Value> evaluated = new ArrayList<>();
        for (Expression arg : args) {
            var value = evaluate(environment, arg);
            if (value.isError()) {
                return List.of(value);
            }
            evaluated.add(value);
        }
        return evaluated;
    }

    private Value evaluateIdentifier(Environment environment, Identifier identifier) {
        requireNonNull(environment, "environment can't be null");
        requireNonNull(identifier, "identifier can't be null");

        Value value = environment.get(identifier.value());
        if (value.isNotNull()) {
            return value;
        }

        Value builtinFunction = builtinFunctions.get(identifier.value());
        if (builtinFunction.isNotNull()) {
            return builtinFunction;
        }
        return newError("Identifier not found: " + identifier.value());
    }

    private Value evaluateIfExpression(Environment environment, IfExpression ifExpression) {
        requireNonNull(environment, "environment can't be null");
        requireNonNull(ifExpression, "ifExpression can't be null");

        var condition = evaluate(environment, ifExpression.condition());
        if (condition.isError()) {
            return condition;
        }
        if (condition.isTruthy()) {
            return evaluate(environment, ifExpression.consequence());
        } else if (ifExpression.hasAlternative()) {
            return evaluate(environment, ifExpression.alternative());
        }
        return NullValue.get();
    }

    private Value evaluateInfixExpression(String operator, Value left, Value right) {
        Objects.requireNonNull(operator, "operator can't be null");
        Objects.requireNonNull(left, "left can't be null");
        Objects.requireNonNull(right, "right can't be null");

        if (ValueType.STRING.equals(left.type()) && ValueType.STRING.equals(right.type())) {
            return evaluateStringInfixExpression(operator, left.as(StringValue.class), right.as(StringValue.class));
        } else if (ValueType.INTEGER.equals(left.type()) && ValueType.INTEGER.equals(right.type())) {
            return evaluateIntegerInfixExpression(operator, left.as(IntegerValue.class), right.as(IntegerValue.class));
        } else if ("==".equalsIgnoreCase(operator)) {
            return BooleanValue.from(left == right);
        } else if ("!=".equalsIgnoreCase(operator)) {
            return BooleanValue.from(left != right);
        } else if (!left.type().equals(right.type())) {
            return newError("Type mismatch: %s %s %s", left.type(), operator, right.type());
        } else {
            return newError("Unknown operator: %s %s %s", left.type(), operator, right.type());
        }
    }

    private Value evaluateStringInfixExpression(String operator, StringValue left, StringValue right) {
        requireNonNull(operator, "operator can't be null");
        requireNonNull(left, "left can't be null");
        requireNonNull(right, "right can't be null");

        if (!operator.equals("+")) {
            return newError("Unknown operator: %s %s %s", left.type(), operator, right.type());
        }
        return new StringValue(left.value() + right.value());
    }

    private Value evaluateIntegerInfixExpression(String operator, IntegerValue left, IntegerValue right) {
        requireNonNull(operator, "operator can't be null");
        requireNonNull(left, "left can't be null");
        requireNonNull(right, "right can't be null");

        return switch (operator) {
            case "+" -> new IntegerValue(left.value() + right.value());
            case "-" -> new IntegerValue(left.value() - right.value());
            case "*" -> new IntegerValue(left.value() * right.value());
            case "/" -> new IntegerValue(left.value() / right.value());
            case "<" -> BooleanValue.from(left.value() < right.value());
            case ">" -> BooleanValue.from(left.value() > right.value());
            case "==" -> BooleanValue.from(left.value() == right.value());
            case "!=" -> BooleanValue.from(left.value() != right.value());
            default -> newError("Unknown operator: %s %s %s", left.type(), operator, right.type());
        };
    }

    private Value evaluatePrefixExpression(String operator, Value right) {
        requireNonNull(operator, "operator can't be null");
        requireNonNull(right, "right can't be null");

        return switch (operator) {
            case "!" -> evaluateBangOperator(right);
            case "-" -> evaluateMinusOperator(right);
            default -> newError("Unknown operator: %s%s", operator, right.type());
        };
    }

    private Value evaluateMinusOperator(Value right) {
        requireNonNull(right, "right can't be null");

        if (!ValueType.INTEGER.equals(right.type())) {
            return newError("Unknown operator: -%s", right.type());
        }
        return new IntegerValue(-right.as(IntegerValue.class).value());
    }

    private Value evaluateBangOperator(Value right) {
        requireNonNull(right, "right can't be null");

        if (BooleanValue.TRUE.equals(right)) {
            return BooleanValue.FALSE;
        } else if (BooleanValue.FALSE.equals(right)) {
            return BooleanValue.TRUE;
        } else if (NullValue.get().equals(right)) {
            return BooleanValue.TRUE;
        } else {
            return BooleanValue.FALSE;
        }
    }

    private Value evaluateProgram(Environment environment, Program program) {
        requireNonNull(environment, "environment can't be null");
        requireNonNull(program, "program can't be null");

        Value result = NullValue.get();
        for (Statement statement : program.statements()) {
            result = evaluate(environment, statement);
            if (result.is(ReturnValue.class)) {
                return result.as(ReturnValue.class).value();
            } else if (result.is(Error.class)) {
                return result;
            }
        }
        return result;
    }

    private Value evaluateBlockStatement(Environment environment, BlockStatement blockStatement) {
        requireNonNull(environment, "environment can't be null");
        requireNonNull(blockStatement, "blockStatement can't be null");

        Value result = NullValue.get();
        for (Statement statement : blockStatement.statements()) {
            result = evaluate(environment, statement);
            if (result.is(ReturnValue.class) || result.is(Error.class)) {
                return result;
            }
        }
        return result;
    }

    private Error newError(String format, Object... args) {
        return new Error(String.format(format, args));
    }

}
