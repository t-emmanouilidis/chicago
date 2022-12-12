package io.aegis.lang.chicago;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public final class Repl {

    private static final String PROMPT = ">> ";

    public static void start() throws IOException {
        Environment environment = new Environment();
        try (Reader is = new InputStreamReader(System.in);
                BufferedReader bufferedReader = new BufferedReader(is)) {
            while (true) {
                System.out.print(PROMPT);
                String line = bufferedReader.readLine();
                if (line != null && !line.isBlank()) {
                    var parser = new Parser(line);
                    var program = parser.parseProgram();
                    if (parser.foundErrors()) {
                        parser.printErrors();
                        continue;
                    }
                    var evaluated = new Evaluator().evaluate(environment, program);
                    System.out.println(evaluated.inspect());
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String user = System.getProperty("USER");
        if (user != null) {
            System.out.println("Hello " + user + "! This is the Chicago programming language!");
        } else {
            System.out.println("Hello! This is the Chicago programming language!");
        }
        System.out.println("Feel free to type in commands!");
        start();
    }
}
