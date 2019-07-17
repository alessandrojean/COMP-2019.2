package microjava;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import static microjava.TokenType.*;

/**
 * Scanner is responsible for creating the tokens from the source file.
 */
public class Scanner {
	/**
	 * End of file character.
	 */
	private static final char EOF_CH = '\u0080';
	/**
	 * End of line character.
	 */
	private static final char EOL = '\n';

  /**
	 * Lookahead character.
	 */
	private static char ch;
  /**
	 * Current column.
	 */
	public  static int col;
  /**
	 * Current line.
	 */
	public  static int line;
  /**
	 * Current position from start of source file.
	 */
	private static int pos;
  /** 
	 * Source file reader.
	 */
	private static Reader in;
  /**
	 * Current lexeme (token string).
	 */
	private static char[] lex;

	/**
	 * Table of keywords and associated type.
	 */
	private static final Map<String, TokenType> KEYWORDS;

	static {
		KEYWORDS = new HashMap<>();
		KEYWORDS.put("class", CLASS);
		KEYWORDS.put("else", ELSE);
		KEYWORDS.put("final", FINAL);
		KEYWORDS.put("if", IF);
		KEYWORDS.put("new", NEW);
		KEYWORDS.put("print", PRINT);
		KEYWORDS.put("program", PROGRAM);
		KEYWORDS.put("read", READ);
		KEYWORDS.put("return", RETURN);
		KEYWORDS.put("void", VOID);
		KEYWORDS.put("while", WHILE);
	}

	/**
	 * Creates a Scanner.
	 * @param reader the input source
	 */
	public Scanner(Reader reader) {
		in = new BufferedReader(reader);
		lex = new char[64];
		line = 1;
		col = 0;
		nextCharacter();
	}

	/**
	 * Try to advance one character in the source file.
	 * If has errors, the next character becomes EOF.
	 */
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

	/**
	 * Creates and returns the next token in the source.
	 * 
	 * @return the next token
	 */
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

				nextCharacter();
				return createToken(NONE);
		}
	}

	/**
	 * Read a name, that can be an indentifier or a keyword.
	 * 
	 * @return the appropriate token
	 */
	private Token readName() {
		// At the beginning ch holds the first letter of the name.
		lex[0] = ch;
		int i = 1;
		nextCharacter();
		// Reads further letters and digits
		while (isDigitOrLetter(ch) && i < 64) {
			lex[i++] = ch;
			nextCharacter();
		}
		// Looks up the name in a keyword table.
		String name = new String(lex, 0, i);
		TokenType type = KEYWORDS.get(name);
		if (type == null) type = IDENTIFIER;

		return createToken(type, name);
	}

	/**
	 * Read a number.
	 * 
	 * @return the token of the number.
	 */
	private Token readNumber() {
		// At the beginning ch holds the first digit of the number.
		lex[0] = ch;
		int i = 1;
		nextCharacter();
		// Reads further digits.
		while (isDigit(ch) && i < 64) {
			lex[i++] = ch;
			nextCharacter();
		}

		String num = new String(lex, 0, i);
		try {
			// Converts them into a number.
			int value = Integer.parseInt(num);
			return createToken(NUMBER, value);
		} catch (NumberFormatException e) {
			// If overflow, report an error.
			System.err.printf("ERROR (%d, %d): Invalid number %s\n", line, col, num);
			return createToken(NONE);
		}
	}

	/**
	 * Read a character constant.
	 * 
	 * @return the token of the character
	 */
	private Token readChar() {
		// At the beginning ch holds a single quote.
		nextCharacter();
		// If the character is empty.
		if (ch == '\'') {
			nextCharacter();
			System.err.printf("ERROR (%d, %d): Empty character\n", line, col);
			return createToken(CHARACTER, "");
		}

		lex[0] = ch;
		int i = 1;
		nextCharacter();
		// Reads further characters.
		while (ch != '\'' && ch != EOL && ch != '\r' && ch != EOF_CH && i < 64) {
			lex[i++] = ch;
			nextCharacter();
		}
		// If the character is unterminated.
		if (ch != '\'') {
			System.err.printf("ERROR (%d, %d): Unterminated char\n", line, col);
			return createToken(CHARACTER, Character.toString(EOF_CH));
		}

		// At the end ch holds the first character after the closing quote.
		nextCharacter();

		String content = new String(lex, 0, i);
		if (content.length() > 1) {
			System.err.printf("ERROR (%d, %d): Invalid character '%s'\n", line, col, content);
			return createToken(CHARACTER, content);
		} else if (content.length() == 1) {
			return createToken(CHARACTER, Character.toString(lex[0]));
		} else if (content.equals("\\n")) {
			return createToken(CHARACTER, "\n");
		} else if (content.equals("\\t")) {
			return createToken(CHARACTER, "\t");
		} else if (content.equals("\\r")) {
			return createToken(CHARACTER, "\r");
		} else if (content.equals("\\''")) {
			return createToken(CHARACTER, "'");
		}

		return createToken(CHARACTER, Character.toString(EOF_CH));
	}

	/**
	 * Checks if the character is a digit.
	 * 
	 * @param c the character to be checked
	 * @return {@code true} if the character is a digit
	 */
	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	/**
	 * Checks if the character is a letter.
	 * 
	 * @param c the character to be checked
	 * @return {@code true} if the character is a digit
	 */
	private boolean isLetter(char c) {
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
	}

	/**
	 * Checks if the character is alphanumeric.
	 * 
	 * @param c the character to be checked
	 * @return {@code true} if the character is a digit
	 */
	private boolean isDigitOrLetter(char c) {
		return isDigit(c) || isLetter(c);
	}

	/**
	 * Create a token of type {@code type}.
	 * 
	 * @param type the type of the token
	 * @return the created token
	 */
	private Token createToken(TokenType type) {
		return createToken(type, null);
	}

	/**
	 * Create a token of a literal type.
	 * 
	 * @param type the type of the token
	 * @param value the literal value
	 * @return the created token
	 */
	private Token createToken(TokenType type, Object value) {
		return new Token(type, line, col, value);
	}
}
