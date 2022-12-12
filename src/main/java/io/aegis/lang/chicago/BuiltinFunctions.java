package io.aegis.lang.chicago;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class BuiltinFunctions {

    private final Map<String, Builtin> functionMap = new HashMap<>();

    BuiltinFunctions() {
        functionMap.put("len", len());
        functionMap.put("first", first());
        functionMap.put("last", last());
        functionMap.put("tail", tail());
        functionMap.put("push", push());
    }

    Value get(String name) {
        Objects.requireNonNull(name, "name can't be null");

        Value function = functionMap.get(name);
        if (function == null) {
            return NullValue.get();
        }
        return function;
    }

    private static Builtin len() {
        return new Builtin(args -> {
            if (args == null) {
                return new Error("Wrong number of arguments. Expected 1, but got nothing");
            }
            if (args.length != 1) {
                return new Error("Wrong number of arguments. Expected 1, but got " + args.length);
            }
            var arg = args[0];
            if (arg.is(StringValue.class)) {
                return new IntegerValue(arg.as(StringValue.class).value().length());
            } else if (arg.is(Array.class)) {
                return new IntegerValue(arg.as(Array.class).size());
            }
            return new Error("Argument to 'len' not supported, got " + arg.type());
        });
    }

    private static Builtin first() {
        return new Builtin(args -> {
            if (args == null) {
                return new Error("Wrong number of arguments. Expected 1, but got nothing");
            }
            if (args.length != 1) {
                return new Error("Wrong number of arguments. Expected 1, but got " + args.length);
            }
            var arg = args[0];
            if (arg.isNot(Array.class)) {
                return new Error("Argument to 'first' must be ARRAY, but got " + arg.type());
            }
            return arg.as(Array.class).first();
        });
    }

    private static Builtin last() {
        return new Builtin(args -> {
            if (args == null) {
                return new Error("Wrong number of arguments. Expected 1, but got nothing");
            }
            if (args.length != 1) {
                return new Error("Wrong number of arguments. Expected 1, but got " + args.length);
            }
            var arg = args[0];
            if (arg.isNot(Array.class)) {
                return new Error("Argument to 'last' must be ARRAY, but got " + arg.type());
            }
            return arg.as(Array.class).last();
        });
    }

    private static Builtin tail() {
        return new Builtin(args -> {
            if (args == null) {
                return new Error("Wrong number of arguments. Expected 1, but got nothing");
            }
            if (args.length != 1) {
                return new Error("Wrong number of arguments. Expected 1, but got " + args.length);
            }
            var arg = args[0];
            if (arg.isNot(Array.class)) {
                return new Error("Argument to 'rest' must be ARRAY, but got " + arg.type());
            }
            return arg.as(Array.class).tail();
        });
    }

    private static Builtin push() {
        return new Builtin(args -> {
            if (args == null) {
                return new Error("Wrong number of arguments. Expected 2, but got nothing");
            }
            if (args.length != 2) {
                return new Error("Wrong number of arguments. Expected 2, but got " + args.length);
            }
            var arrayArg = args[0];
            if (arrayArg.isNot(Array.class)) {
                return new Error("First argument to 'push' must be ARRAY, but got " + arrayArg.type());
            }
            var elemArg = args[1];
            return arrayArg.as(Array.class).push(elemArg);
        });
    }

}
