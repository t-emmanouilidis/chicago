package io.aegis.lang.chicago;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Parser {

    private final Lexer lexer;
    private final List<String> errors = new ArrayList<>();
    private final Map<TokenType, PrefixParseFunction> prefixParseFunctions;
    private final Map<TokenType, InfixParseFunction> infixParseFunctions;
    private final Map<TokenType, Precedence> precedenceMap;

    private Token current;
    private Token next;

    public Parser(String input) {
        requireNonNull(input, "input can't be null");
        this.lexer = new Lexer(input);

        advanceTwice();

        this.prefixParseFunctions = createPrefixParseFunctions();
        this.infixParseFunctions = createInfixParseFunctions();
        this.precedenceMap = createPrecedenceMap();
    }

    private void advanceTwice() {
        advance();
        advance();
    }

    private void advance() {
        current = next;
        next = lexer.nextToken();
    }

    public Program parseProgram() {
        try {
            List<Statement> statements = new ArrayList<>();
            while (current.isNotLastOne()) {
                parseStatement().ifPresent(statements::add);
                advance();
            }
            return new Program(statements);
        } catch (UnexpectedTokenException ex) {
            errors.add(ex.getMessage());
        }
        return Program.EMPTY;
    }

    private Optional<Statement> parseStatement() {
        return switch (current.type()) {
            case LET -> parseLetStatement();
            case RETURN -> parseReturnStatement();
            default -> parseExpressionStatement();
        };
    }

    private Optional<Statement> parseExpressionStatement() {
        var currentToken = current;
        var expression = parseExpression(Precedence.LOWEST);
        if (nextTokenIsASemicolon()) {
            advance();
        }
        return expression.map(expr -> new ExpressionStatement(currentToken, expr));
    }

    private Optional<Expression> parseExpression(Precedence precedence) {
        requireNonNull(precedence, "precedence can't be null");

        var prefixParseFunction = prefixParseFunctions.get(current.type());
        if (prefixParseFunction == null) {
            errors.add("No prefix parsing function found for " + current.type());
            return Optional.empty();
        }
        var left = prefixParseFunction.get();

        while (nextTokenIsNotASemicolon() && precedence.lessThan(nextTokenPrecedence())) {
            var infixParseFunction = infixParseFunctions.get(next.type());
            if (infixParseFunction != null) {
                advance();
                left = infixParseFunction.apply(left);
            }
        }
        return Optional.of(left);
    }

    private boolean nextTokenIsNotASemicolon() {
        return !nextTokenIsASemicolon();
    }

    private boolean nextTokenIsASemicolon() {
        return nextTokenIsOfType(TokenType.SEMICOLON);
    }

    private Expression parseInfixExpression(Expression left) {
        requireNonNull(left, "left can't be null");

        var currentToken = current;

        var currentPrecedence = currentTokenPrecedence();
        advance();
        return parseExpression(currentPrecedence)
              .map(right -> new InfixExpression(currentToken, left, currentToken.literal(), right))
              .orElse(null);
    }

    private Optional<Statement> parseReturnStatement() {
        var currentToken = current;

        advance();

        var value = parseExpression(Precedence.LOWEST);

        if (nextTokenIsOfType(TokenType.SEMICOLON)) {
            advance();
        }

        return value.map(val -> new ReturnStatement(currentToken, val));
    }

    private Optional<Statement> parseLetStatement() {
        // current token is LET
        var currentToken = current;

        // expect that next token is ident, advance and parse the identifier
        advanceOnlyIfNextTokenIsOfType(TokenType.IDENT);
        var identifier = new Identifier(current, current.literal());

        // next token should be a =, advance to it and then advance again to the value expression
        advanceOnlyIfNextTokenIsOfType(TokenType.ASSIGN);
        advance();

        var value = parseExpression(Precedence.LOWEST);

        if (nextTokenIsOfType(TokenType.SEMICOLON)) {
            advance();
        }

        return value.map(val -> new LetStatement(currentToken, identifier, val));
    }

    private boolean currentTokenTypeIs(TokenType type) {
        requireNonNull(type, "type can't be null");

        return type.equals(current.type());
    }

    private boolean currentTokenTypeIsNot(TokenType type) {
        requireNonNull(type, "type can't be null");

        return !currentTokenTypeIs(type);
    }

    private boolean nextTokenIsNotOfType(TokenType type) {
        requireNonNull(type, "type can't be null");

        return !nextTokenIsOfType(type);
    }

    private boolean nextTokenIsOfType(TokenType type) {
        requireNonNull(type, "type can't be null");

        return type.equals(next.type());
    }

    private void advanceOnlyIfNextTokenIsOfType(TokenType type) {
        requireNonNull(type, "type can't be null");

        assertNextTokenIsOfType(type);
        advance();
    }

    private void assertNextTokenIsOfType(TokenType type) {
        requireNonNull(type, "type can't be null");

        if (nextTokenIsNotOfType(type)) {
            throw new UnexpectedTokenException("Next token should be of type " + type + " but is instead of type " + next.type());
        }
    }

    private Precedence currentTokenPrecedence() {
        return precedenceMap.getOrDefault(current.type(), Precedence.LOWEST);
    }

    private Precedence nextTokenPrecedence() {
        return precedenceMap.getOrDefault(next.type(), Precedence.LOWEST);
    }

    boolean foundErrors() {
        return !errors.isEmpty();
    }

    void printErrors() {
        for (String error : errors) {
            System.out.println("parser error: " + error);
        }
    }

    private Map<TokenType, PrefixParseFunction> createPrefixParseFunctions() {
        Map<TokenType, PrefixParseFunction> map = new EnumMap<>(TokenType.class);
        map.put(TokenType.IDENT, this::parseIdentifier);
        map.put(TokenType.INT, this::parseInteger);
        map.put(TokenType.BANG, this::parsePrefixExpression);
        map.put(TokenType.MINUS, this::parsePrefixExpression);
        map.put(TokenType.TRUE, this::parseBooleanExpression);
        map.put(TokenType.FALSE, this::parseBooleanExpression);
        map.put(TokenType.LPAREN, this::parseGroupedExpression);
        map.put(TokenType.IF, this::parseIfExpression);
        map.put(TokenType.FUNCTION, this::parseFunctionLiteral);
        map.put(TokenType.STRING, this::parseString);
        map.put(TokenType.LBRACKET, this::parseArrayLiteral);
        map.put(TokenType.LBRACE, this::parseDictionary);
        return map;
    }

    private Map<TokenType, InfixParseFunction> createInfixParseFunctions() {
        return Map.of(
              TokenType.PLUS, this::parseInfixExpression,
              TokenType.MINUS, this::parseInfixExpression,
              TokenType.SLASH, this::parseInfixExpression,
              TokenType.ASTERISK, this::parseInfixExpression,
              TokenType.EQUAL, this::parseInfixExpression,
              TokenType.NOT_EQUAL, this::parseInfixExpression,
              TokenType.LESS_THAN, this::parseInfixExpression,
              TokenType.GREATER_THAN, this::parseInfixExpression,
              TokenType.LPAREN, this::parseCallExpression,
              TokenType.LBRACKET, this::parseIndexExpression);
    }

    private Expression parseDictionary() {
        var currentToken = current;
        Map<Expression, Expression> pairs = new HashMap<>();
        while (nextTokenIsNotOfType(TokenType.RBRACE)) {
            advance();
            var key = parseExpression(Precedence.LOWEST);
            advanceOnlyIfNextTokenIsOfType(TokenType.COLON);
            advance();
            var value = parseExpression(Precedence.LOWEST);
            key.ifPresent(k -> value.ifPresent(v -> pairs.put(k, v)));
            if (nextTokenIsNotOfType(TokenType.RBRACE)) {
                advanceOnlyIfNextTokenIsOfType(TokenType.COMMA);
            }
        }
        advanceOnlyIfNextTokenIsOfType(TokenType.RBRACE);
        return new DictionaryLiteral(currentToken, pairs);
    }

    private Expression parseIndexExpression(Expression left) {
        requireNonNull(left, "left can't be null");
        var currentToken = current;
        advance();
        var index = parseExpression(Precedence.LOWEST);
        advanceOnlyIfNextTokenIsOfType(TokenType.RBRACKET);
        return index.map(idx -> new IndexExpression(currentToken, left, idx))
              .orElse(null);
    }

    private Expression parseArrayLiteral() {
        var currentToken = current;
        var elements = parseExpressionList(TokenType.RBRACKET);
        return new ArrayLiteral(currentToken, elements);
    }

    private Expression parseCallExpression(Expression function) {
        requireNonNull(function, "function can't be null");

        var currentToken = current;
        var arguments = parseExpressionList(TokenType.RPAREN);
        return new CallExpression(currentToken, function, arguments);
    }

    private List<Expression> parseExpressionList(TokenType endTokenType) {
        requireNonNull(endTokenType, "endTokenType can't be null");

        if (nextTokenIsOfType(endTokenType)) {
            advance();
            return List.of();
        }
        advance();

        List<Expression> list = new ArrayList<>();
        parseExpression(Precedence.LOWEST).ifPresent(list::add);
        while (nextTokenIsOfType(TokenType.COMMA)) {
            advanceTwice();
            parseExpression(Precedence.LOWEST).ifPresent(list::add);
        }
        advanceOnlyIfNextTokenIsOfType(endTokenType);
        return list;
    }

    private Expression parseFunctionLiteral() {
        var currentToken = current;
        advanceOnlyIfNextTokenIsOfType(TokenType.LPAREN);
        var parameters = parseFunctionParameters();
        advanceOnlyIfNextTokenIsOfType(TokenType.LBRACE);
        var body = parseBlockStatement();
        return new FunctionLiteral(currentToken, parameters, body);
    }

    private List<Identifier> parseFunctionParameters() {
        if (nextTokenIsOfType(TokenType.RBRACE)) {
            advance();
            return List.of();
        }
        advance();
        List<Identifier> parameters = new ArrayList<>();

        var param = new Identifier(current, current.literal());
        parameters.add(param);
        while (nextTokenIsOfType(TokenType.COMMA)) {
            advanceTwice();
            param = new Identifier(current, current.literal());
            parameters.add(param);
        }
        advanceOnlyIfNextTokenIsOfType(TokenType.RPAREN);
        return parameters;
    }

    private Expression parseIfExpression() {
        var currentToken = current;
        advanceOnlyIfNextTokenIsOfType(TokenType.LPAREN);
        advance();
        var condition = parseExpression(Precedence.LOWEST);

        advanceOnlyIfNextTokenIsOfType(TokenType.RPAREN);
        advanceOnlyIfNextTokenIsOfType(TokenType.LBRACE);
        var consequence = parseBlockStatement();

        BlockStatement alternativeBlock = null;
        if (nextTokenIsOfType(TokenType.ELSE)) {
            advance();
            advanceOnlyIfNextTokenIsOfType(TokenType.LBRACE);
            alternativeBlock = parseBlockStatement();
        }
        var alternative = alternativeBlock;

        return condition
              .map(cond -> new IfExpression(currentToken, cond, consequence, alternative))
              .orElse(null);
    }

    private BlockStatement parseBlockStatement() {
        var currentToken = current;
        List<Statement> statements = new ArrayList<>();
        advance();
        while (currentTokenTypeIsNot(TokenType.RBRACE) && current.isNotLastOne()) {
            parseStatement().ifPresent(statements::add);
            advance();
        }
        return new BlockStatement(currentToken, statements);
    }

    private Expression parseGroupedExpression() {
        advance();
        var expr = parseExpression(Precedence.LOWEST);
        advanceOnlyIfNextTokenIsOfType(TokenType.RPAREN);
        return expr.orElse(null);
    }

    private Expression parseBooleanExpression() {
        return new BooleanLiteral(current, TokenType.TRUE.equals(current.type()));
    }

    private Expression parseIdentifier() {
        return new Identifier(current, current.literal());
    }

    private Expression parseInteger() {
        var currentToken = this.current;
        try {
            long value = Long.parseLong(currentToken.literal());
            return new IntegerLiteral(currentToken, value);
        } catch (NumberFormatException ex) {
            errors.add("Could not parse " + currentToken.literal() + " as integer");
            return null;
        }
    }

    private Expression parseString() {
        return new StringLiteral(current, current.literal());
    }

    private Expression parsePrefixExpression() {
        var currentToken = current;
        advance();
        return parseExpression(Precedence.PREFIX)
              .map(right -> new PrefixExpression(currentToken, currentToken.literal(), right))
              .orElse(null);
    }

    private Map<TokenType, Precedence> createPrecedenceMap() {
        return Map.of(
              TokenType.EQUAL, Precedence.EQUAL,
              TokenType.NOT_EQUAL, Precedence.EQUAL,
              TokenType.LESS_THAN, Precedence.LESS_GREATER,
              TokenType.GREATER_THAN, Precedence.LESS_GREATER,
              TokenType.PLUS, Precedence.SUM,
              TokenType.MINUS, Precedence.SUM,
              TokenType.SLASH, Precedence.PRODUCT,
              TokenType.ASTERISK, Precedence.PRODUCT,
              TokenType.LPAREN, Precedence.FUNCTION_CALL,
              TokenType.LBRACKET, Precedence.INDEX);
    }

}
