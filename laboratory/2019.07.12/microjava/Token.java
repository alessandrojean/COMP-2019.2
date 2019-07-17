package microjava;

/**
 * A program token.
 */
public class Token {
	/**
	 * Token kind.
	 */
	final TokenType kind;
	/**
   * Token line.
   */
	final int line;		
	/**
   * Token column.
   */
	final int column;		
	/**
   * Token value.	
   */
	final Object value;

	/**
	 * Creates a token.
	 * 
	 * @param kind the type of the token
	 * @param line the line of the token
	 * @param column the column of the token
	 * @param value the value of the token if is literal
	 */
	public Token(TokenType kind, int line, int column, Object value) {
		this.kind = kind;
		this.line = line;
		this.column = column;
		this.value = value;
	}

	@Override
	public String toString() {
		return String.format(
			"Token { kind = %s; line = %d; col = %d%s }",
			kind,
			line,
			column,
			(value != null) ? "; value = " + value.toString() : ""
		);
	}
}