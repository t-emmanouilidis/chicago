package io.aegis.lang.chicago;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

public class Evaluator {

    public Value evaluate(Node node) {
        requireNonNull(node, "node can't be null");

        if (node instanceof Program program) {
            return evaluateProgram(program);
        } else if (node instanceof BlockStatement block) {
            return evaluateBlockStatement(block);
        } else if (node instanceof ExpressionStatement exprStmt) {
            return evaluate(exprStmt.expression());
        } else if (node instanceof ReturnStatement returnStmt) {
            var value = evaluate(returnStmt.returnValue());
            return new ReturnValue(value);
        } else if (node instanceof IntegerLiteral integer) {
            return new IntegerValue(integer.value());
        } else if (node instanceof BooleanLiteral booleanLiteral) {
            return BooleanValue.from(booleanLiteral.value());
        } else if (node instanceof PrefixExpression prefix) {
            var rightValue = evaluate(prefix.right());
            return evaluatePrefixExpression(prefix.operator(), rightValue);
        } else if (node instanceof InfixExpression infix) {
            var left = evaluate(infix.left());
            var right = evaluate(infix.right());
            return evaluateInfixExpression(infix.operator(), left, right);
        } else if (node instanceof IfExpression ifExpression) {
            return evaluateIfExpression(ifExpression);
        }
        return NullValue.get();
    }

    private Value evaluateIfExpression(IfExpression ifExpression) {
        requireNonNull(ifExpression, "ifExpression can't be null");

        var condition = evaluate(ifExpression.condition());
        if (condition.isTruthy()) {
            return evaluate(ifExpression.consequence());
        } else if (ifExpression.hasAlternative()) {
            return evaluate(ifExpression.alternative());
        }
        return NullValue.get();
    }

    private Value evaluateInfixExpression(String operator, Value left, Value right) {
        Objects.requireNonNull(operator, "operator can't be null");
        Objects.requireNonNull(left, "left can't be null");
        Objects.requireNonNull(right, "right can't be null");

        if (left.type() == ValueType.INTEGER && right.type() == ValueType.INTEGER) {
            return evaluateIntegerInfixExpression(operator, left.as(IntegerValue.class), right.as(IntegerValue.class));
        } else if ("==".equalsIgnoreCase(operator)) {
            return BooleanValue.from(left == right);
        } else if ("!=".equalsIgnoreCase(operator)) {
            return BooleanValue.from(left != right);
        } else {
            return NullValue.get();
        }
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
            default -> NullValue.get();
        };
    }

    private Value evaluatePrefixExpression(String operator, Value right) {
        requireNonNull(operator, "operator can't be null");
        requireNonNull(right, "right can't be null");

        return switch (operator) {
            case "!" -> evaluateBangOperator(right);
            case "-" -> evaluateMinusOperator(right);
            default -> NullValue.get();
        };
    }

    private Value evaluateMinusOperator(Value right) {
        requireNonNull(right, "right can't be null");

        if (right.type() != ValueType.INTEGER) {
            return NullValue.get();
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

    private Value evaluateProgram(Program program) {
        requireNonNull(program, "program can't be null");

        Value result = NullValue.get();
        for (Statement statement : program.statements()) {
            result = evaluate(statement);
            if (result.is(ReturnValue.class)) {
                return result.as(ReturnValue.class).value();
            }
        }
        return result;
    }

    private Value evaluateBlockStatement(BlockStatement blockStatement) {
        requireNonNull(blockStatement, "blockStatement can't be null");

        Value result = NullValue.get();
        for (Statement statement : blockStatement.statements()) {
            result = evaluate(statement);
            if (result.is(ReturnValue.class)) {
                return result;
            }
        }
        return result;
    }

}
