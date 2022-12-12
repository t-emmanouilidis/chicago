package io.aegis.lang.chicago;

import java.util.List;

public record Function(List<Identifier> parameters, BlockStatement body, Environment environment)
      implements Value {

    @Override
    public String type() {
        return ValueType.FUNCTION;
    }

    @Override
    public String inspect() {
        var paramsAsString = parameters.stream().map(Identifier::toString).toList();
        return new StringBuilder("fn(")
              .append(paramsAsString)
              .append(") {")
              .append(System.lineSeparator())
              .append(body.toString())
              .append(System.lineSeparator())
              .append("}")
              .toString();
    }

    @Override
    public boolean isTruthy() {
        return true;
    }
}
