package microjava;

public class Token {
	// Token kind.
	final TokenType kind;
	// Token line.
	final int line;		
	// Token column.
	final int column;		
	// Token value.	
	final Object value;

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