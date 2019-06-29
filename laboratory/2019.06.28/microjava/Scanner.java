package microjava;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static microjava.TokenType.*;

public class Scanner {
	private static final char EOF_CH = '\u0080';
	private static final char EOL = '\n';

  // Lookahead character.
	private static char ch;
  // Current column.
	public  static int col;
  // Current line.
	public  static int line;
  // Current position from start of source file.
	private static int pos;
  // Source file reader.
	private static Reader in;
  // Current lexeme (token string).
	private static char[] lex;

	private static final Map<String, TokenType> keywords;

	static {
		keywords = new HashMap<>();
		keywords.put("class", CLASS);
		keywords.put("else", ELSE);
		keywords.put("final", FINAL);
		keywords.put("if", IF);
		keywords.put("new", NEW);
		keywords.put("print", PRINT);
		keywords.put("program", PROGRAM);
		keywords.put("read", READ);
		keywords.put("return", RETURN);
		keywords.put("void", VOID);
		keywords.put("while", WHILE);
	}

	public Scanner(Reader reader) {
		in = new BufferedReader(reader);
		lex = new char[64];
		line = 1;
		col = 0;
		nextCharacter();
	}

	private void nextCharacter() {
		try {
			ch = (char) in.read(); 
			col++; 
			pos++;

			if (ch == EOL) {
				line++; 
				col = 0;
			} else if (ch == '\uffff') {
				ch = EOF_CH;
			}
		} catch (IOException e) {
			ch = EOF_CH;
		}
	}

	public Token next() {
		while (ch <= ' ') nextCharacter();

		switch (ch) {
			case ';':
				nextCharacter();
				return createToken(SEMICOLON);
			case '.':
				nextCharacter();
				return createToken(PERIOD);
			case EOF_CH:
				return createToken(EOF);
			case '+':
				nextCharacter();
				return createToken(PLUS);
			case '-':
			  nextCharacter();
				return createToken(MINUS);
			case '*':
				nextCharacter();
				return createToken(TIMES);
			case '%':
				nextCharacter();
				return createToken(REM);
			case ',':
				nextCharacter();
				return createToken(COMMA);
			case '(':
				nextCharacter();
				return createToken(LEFT_PAREN);
			case ')':
				nextCharacter();
				return createToken(RIGHT_PAREN);
			case '[':
				nextCharacter();
				return createToken(LEFT_BRACKET);
			case ']':
				nextCharacter();
				return createToken(RIGHT_BRACKET);
			case '{':
				nextCharacter();
				return createToken(LEFT_BRACE);
			case '}':
				nextCharacter();
				return createToken(RIGHT_BRACE);
			case '=':
				nextCharacter();
				if (ch == '=') {
					nextCharacter();
					return createToken(EQUAL);
				} else {
					return createToken(ASSIGN);
				}
			case '!':
				nextCharacter();
				if (ch == '=') {
					nextCharacter();
					return createToken(NOT_EQUAL);
				} else {
					return createToken(NONE);
				}
			case '<':
				nextCharacter();
				if (ch == '=') {
					nextCharacter();
					return createToken(LESS_EQUAL);
				} else {
					return createToken(LESS);
				}
			case '>':
				nextCharacter();
				if (ch == '=') {
					nextCharacter();
					return createToken(GREATER_EQUAL);
				} else {
					return createToken(GREATER);
				}
			case '/':
				nextCharacter();
				if (ch == '/') {
					do {
						nextCharacter();
					} while (ch != '\n' && ch != EOF_CH);
					return next();
				} else {
					return createToken(SLASH);
				}
			case '\'':
				return readChar();
			default:
				if (isDigit(ch)) {
					return readNumber();
				} else if (isLetter(ch)) {
					return readName();
				}

				return createToken(NONE);
		}
	}

	private Token readName() {
		lex[0] = ch;
		int i = 1;
		nextCharacter();

		while (isDigitOrLetter(ch) && i < 64) {
			lex[i++] = ch;
			nextCharacter();
		}

		String name = new String(lex, 0, i);
		TokenType type = keywords.get(name);
		if (type == null) type = IDENTIFIER;

		return createToken(type, name);
	}

	private Token readNumber() {
		lex[0] = ch;
		int i = 1;
		nextCharacter();

		while (isDigit(ch) && i < 64) {
			lex[i++] = ch;
			nextCharacter();
		}

		String num = new String(lex, 0, i);
		return createToken(NUMBER, Integer.parseInt(num));
	}

	private Token readChar() {
		nextCharacter();
		lex[0] = ch;
		int i = 1;
		nextCharacter();

		while (ch != '\'' && i < 64) {
			lex[i++] = ch;
			nextCharacter();
		}
		// Consume the closing '.
		nextCharacter();

		if (i == 1 && lex[0] != '\\') {
			return createToken(CHARACTER, Character.toString(lex[0]));
		}

		switch (lex[1]) {
			case 'r':
				return createToken(CHARACTER, "\r");
			case 't':
				return createToken(CHARACTER, "\t");
			case 'n':
				return createToken(CHARACTER, "\n");				
		}

		return createToken(CHARACTER, Character.toString(EOF_CH));
	}

	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	private boolean isLetter(char c) {
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
	}

	private boolean isDigitOrLetter(char c) {
		return isDigit(c) || isLetter(c);
	}

	private Token createToken(TokenType type) {
		return createToken(type, null);
	}

	private Token createToken(TokenType type, Object value) {
		return new Token(type, line, col, value);
	}
}
