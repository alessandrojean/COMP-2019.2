package microjava;

// import java.util.*;
// import MJ.SymTab.*;
// import MJ.CodeGen.*;

import static microjava.TokenType.*;
import java.util.Arrays;
import java.util.List;

public class Parser {

	public static final List<TokenType> STATEMENT_STARTERS = Arrays.asList(
		IDENTIFIER,
		IF,
		WHILE,
		RETURN,
		READ,
		PRINT,
		LEFT_BRACE,
		SEMICOLON
	);

	public static final List<TokenType> EXPR_STARTERS = Arrays.asList(
		MINUS,
		IDENTIFIER,
		NUMBER,
		CHARACTER,
		NEW,
		LEFT_PAREN
	);

	public static final List<TokenType> RELATIONAL_OPERATORS = Arrays.asList(
		EQUAL,
		NOT_EQUAL,
		LESS,
		LESS_EQUAL,
		GREATER,
		GREATER_EQUAL
	);

	public static final List<TokenType> ADDITION_OPERATOR = Arrays.asList(
		PLUS,
		MINUS
	);

	public static final List<TokenType> MULTIPLICATION_OPERATOR =	Arrays.asList(
		TIMES,
		SLASH,
		REM
	);

	
	/**
	 * Scanner returning token to parse.
	 */
	private Scanner scanner;

	/**
	 * Current token.
	 */
	private Token token;

	/**
	 * Look ahead token.
	 */
	private Token nextToken;

	/**
	 * Always contains nextToken.kind.
	 */
	private TokenType sym;

	/**
	 * Errors count.
	 */
	public int errors;

	/**
	 * Number of correctly recognized tokens since last error.
	 */
	private int errDist;

	/**
	 * Creates a new parser, with the provided Scanner for obtaining tokens.
	 * 
	 * @param scanner the scanner
	 */
	public Parser(Scanner scanner) {
		this.scanner = scanner;
	}

	/**
	 * Reads the text token from scanner.
	 */
	private void scan() {
		token = nextToken;
		nextToken = scanner.next();
		sym = nextToken.kind;
		errDist++;
		/*
		System.out.print("line " + la.line + ", col " + la.col + ": " + name[sym]);
		if (sym == ident) System.out.print(" (" + la.string + ")");
		if (sym == number || sym == charCon) System.out.print(" (" + la.val + ")");
		System.out.println();*/
	}

	/**
	 * Checks the presence of an expected token ind.
	 * 
	 * If the token is found, scans the next token.
	 * If the token isn't the one expected, a compilation error is reported.
	 * 
	 * @param expected the expected token kind
	 */
	private void check(TokenType expected) {
		if (sym == expected) {
			scan();
		} else {
		  error(expected + " expected, found " + sym);
		}
	}

	/**
	 * Reports a compiler error.
	 * 
	 * @param msg the error message
	 */
	public void error(String msg) {
		if (errDist >= 3) {
			System.err.println("Line " + nextToken.line + " col " + nextToken.column + ": " + msg);
			errors++;
		}
		errDist = 0;
	}

	/**
	 * Parses a Program
	 *
	 * Program = "program" ident {ConstDecl | ClassDecl | VarDecl}
	 *           '{' {MethodDecl} '}'
	 */
	private void parseProgram() {
		check(PROGRAM);
		check(IDENTIFIER);

		// {ConstDecl | ClassDecl | VarDecl}
		while (true) {
			if (sym == FINAL) {
				parseConstDecl();
			} else if (sym == CLASS) {
				parseClassDecl();
			} else if (sym == IDENTIFIER) {
				parseVarDecl();
			} else {
				break;
			}
		}

		check(LEFT_BRACE);

		// {MethodDecl}
		while (sym == IDENTIFIER || sym == VOID) {
			parseMethodDecl();
		}

		check(RIGHT_BRACE);
	}

	/**
	 * Parses a ConstDecl
	 * 
	 * ConstDecl = "final" Type ident "=" (number | charConst) ";"
	 */
	private void parseConstDecl() {
		check(FINAL);
		parseType();
		check(IDENTIFIER);
		check(ASSIGN);

		if (sym != NUMBER && sym != CHARACTER) {
			error("Expected number or char constant");
		}

		scan();
		check(SEMICOLON);
	}
	
	/**
	 * Parses a ClassDecl.
	 * 
	 * ClassDecl = "class" ident "{" {VarDecl} "}"
	 */
	private void parseClassDecl() {
		check(CLASS);
		check(IDENTIFIER);
		check(LEFT_BRACE);

		while (sym == IDENTIFIER) {
			parseVarDecl();
		}

		check(RIGHT_BRACE);
	}

	/**
	 * Parses a VarDecl.
	 * 
	 * VarDecl = Type ident {"," ident} ";"
	 */
	private void parseVarDecl() {
		parseType();
		check(IDENTIFIER);

		while (sym == COMMA) {
			check(COMMA);
			check(IDENTIFIER);
		}

		check(SEMICOLON);
	}

	/**
	 * Parses a MethodDecl.
	 * 
	 * MethodDecl = (Type | "void") ident "(" [FormPars] ") {VarDecl} Block"
	 */
	private void parseMethodDecl() {
		if (sym == IDENTIFIER) {
			parseType();
		} else {
			check(VOID);
		}

		check(IDENTIFIER);
		check(LEFT_PAREN);

		if (sym == IDENTIFIER) {
			parseFormPars();
		}

		check(RIGHT_PAREN);

		while (sym == IDENTIFIER) {
			parseVarDecl();
		}

		parseBlock();
	}

	/**
	 * Parses a Type.
	 * 
	 * Type = ident ["[" "]"]
	 */
	private void parseType() {
		check(IDENTIFIER);

		if (sym == LEFT_BRACKET) {
			check(LEFT_BRACKET);
			check(RIGHT_BRACKET);
		}
	}

	/**
	 * Parses a FormPars.
	 * 
	 * FormPars = Type ident {"," Type ident}
	 */
	private void parseFormPars() {
		parseType();
		check(IDENTIFIER);

		while (sym == COMMA) {
			check(COMMA);
			parseType();
			check(IDENTIFIER);
		}
	}

	/**
	 * Parses a Block.
	 * 
	 * Block = '{' {Statement} '}'
	 */
	private void parseBlock() {
		check(LEFT_BRACE);

		while (sym != RIGHT_BRACE && sym != EOF) {
			parseStatement();
		}

		check(RIGHT_BRACE);
	}

	/**
	 * Parses a Statement.
	 * 
	 * Statement = SimpleStatement
	 *           | IfStatement
	 *           | While Statement
   *           | ReturnStatement
	 *           | ReadStatement
	 *           | PrintStatement
	 *           | Block
	 *           | ";"
	 */
	private void parseStatement() {
		if (!STATEMENT_STARTERS.contains(sym)) {
			error("Invalid start of statement");

			while (sym != SEMICOLON && sym != RIGHT_BRACE) {
				scan();
			}

			if (sym == SEMICOLON) {
				scan();
			}

			errDist = 0;
		}

		if (sym == IDENTIFIER) {
			parseSimpleStatement();
		} else if (sym == IF) {
			parseIfStatement();
		} else if (sym == WHILE) {
			parseWhileStatement();
		} else if (sym == RETURN) {
			parseReturnStatement();
		} else if (sym == READ) {
		  parseReadStatement();
		} else if (sym == PRINT) {
			parsePrintStatement();
		} else if (sym == LEFT_BRACE) {
			parseBlock();
		} else if (sym == SEMICOLON) {
			check(SEMICOLON);
		} else {
			error("Illegal start of statement: " + sym);
		}
	}

	/**
	 * Parses a SimpleStatement.
	 * 
	 * SimpleStatement = Designator ("=" Expr | ActPars) ";"
	 */
	private void parseSimpleStatement() {
		parseDesignator();

		if (sym == ASSIGN) {
			check(ASSIGN);
			parseExpr();
		} else if (sym == LEFT_PAREN) {
			parseActPars();
		} else {
			error("Invalid assignment or call");
		}

		check(SEMICOLON);
	}

	/**
	 * Parses an IfStatement.
	 * 
	 * IfStatement = "if" "(" Condition ")" Statement ["else" Statement]
	 */
	private void parseIfStatement() {
		check(IF);
		check(LEFT_PAREN);
		parseCondition();
		check(RIGHT_PAREN);
		parseStatement();

		if (sym == ELSE) {
			check(ELSE);
			parseStatement();
		}
	}

	/**
	 * Parses a WhileStatement.
	 * 
	 * WhileStatement = "while" "(" Condition ")" Statement
	 */
	private void parseWhileStatement() {
		check(WHILE);
		check(LEFT_PAREN);
		parseCondition();
		check(RIGHT_PAREN);
		parseStatement();
	}

	/**
	 * Parses a ReturnStatement.
	 * 
	 * ReturnStatement = "return" [Expr] ";"
	 */
	private void parseReturnStatement() {
		check(RETURN);

		if (EXPR_STARTERS.contains(sym)) {
			parseExpr();
		}

		check(SEMICOLON);
	}

	/**
	 * Parses a ReadStatement.
	 * 
	 * ReadStatement = "read" "(" Designator ")" ";"
	 */
	private void parseReadStatement() {
		check(READ);
		check(LEFT_PAREN);
		parseDesignator();
		check(RIGHT_PAREN);
		check(SEMICOLON);
	}

	/**
	 * Parses a PrintStatement.
	 * 
	 * PrintStatement = "print" "(" Expr ["," number] ")" ";"
	 */
	private void parsePrintStatement() {
		check(PRINT);
		check(LEFT_PAREN);
		parseExpr();

		if (sym == COMMA) {
			check(COMMA);
			check(NUMBER);
		}

		check(RIGHT_PAREN);
		check(SEMICOLON);
	}

	/**
	 * Parses a Condition.
	 * 
	 * Condition = Expr Relop Expr
	 */
	private void parseCondition() {
		parseExpr();

		if (RELATIONAL_OPERATORS.contains(sym)) {
			scan();
		} else {
			error("Relational operator expected");
		}

		parseExpr();
	}

	/**
	 * Parses an Expr.
	 * 
	 * Expr = ["-"] Term {Addop Term}
	 */
	private void parseExpr() {
		if (!EXPR_STARTERS.contains(sym)) {
			error("Invalid expression");
			return;
		}

		if (sym == MINUS) {
			check(MINUS);
		}

		parseTerm();

		if (ADDITION_OPERATOR.contains(sym)) {
			scan();
			parseTerm();
		}
	}

	/**
	 * Parses a Term.
	 * 
	 * Term = Factor {Mulop Factor}
	 */
	private void parseTerm() {
		parseFactor();

		if (MULTIPLICATION_OPERATOR.contains(sym)) {
			scan();
			parseFactor();
		}
	}

	/**
	 * Parses a Factor.
	 * 
	 * Factor = Designator [ActPars]
	 *        | number
   *        | charConst
   *        | "new" ident ["[" Expr "]"]
   *        | "(" Expr ")"
	 */
	private void parseFactor() {
		if (sym == IDENTIFIER) {
			parseDesignator();

			if (sym == LEFT_PAREN) {
				parseActPars();
			}
		} else if (sym == NUMBER) {
			check(NUMBER);
		} else if (sym == CHARACTER) {
			check(CHARACTER);
		} else if (sym == NEW) {
			check(NEW);
			check(IDENTIFIER);

			if (sym == LEFT_BRACKET) {
				check(LEFT_BRACKET);
				parseExpr();
				check(RIGHT_BRACKET);
			}
		} else if (sym == LEFT_PAREN) {
			check(LEFT_PAREN);
			parseExpr();
			check(RIGHT_PAREN);
		}
	}

	/**
	 * Parses a Designator.
	 * 
	 * Designator = ident {"." ident | "[" Expr "]"}
	 */
	private void parseDesignator() {
		check(IDENTIFIER);

		while (true) {
			if (sym == PERIOD) {
				check(PERIOD);
				check(IDENTIFIER);
			} else if (sym == LEFT_BRACKET) {
				check(LEFT_BRACKET);
				parseExpr();
				check(RIGHT_BRACKET);
			} else {
				break;
			}
		}
	}

	/**
	 * Parses an ActPars
	 * 
	 * ActPars = "(" [ Expr {"," Expr} ] ")"
	 */
	private void parseActPars() {
		check(LEFT_PAREN);

		if (EXPR_STARTERS.contains(sym)) {
			parseExpr();
		}

		check(RIGHT_PAREN);
	}

	public void parse() {
		errors = 0; 
		errDist = 3;

		scan();
		parseProgram();

		if (sym != EOF) {
			error("end of file found before end of program");
		}
	}

}
