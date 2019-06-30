package microjava;

/**
 * Represents the possible types of tokens.
 */
enum TokenType {
  /**
   * Error token.
   */
  NONE,

  /**
   * Class tokens.
   */
  IDENTIFIER, NUMBER, CHARACTER,

  /**
   * Operators and special characters tokens.
   */
  PLUS, MINUS, TIMES, SLASH, REM, EQUAL, NOT_EQUAL,
  LESS, LESS_EQUAL, GREATER, GREATER_EQUAL,
  ASSIGN, SEMICOLON, COMMA, PERIOD,
  LEFT_PAREN, RIGHT_PAREN,
  LEFT_BRACKET, RIGHT_BRACKET,
  LEFT_BRACE, RIGHT_BRACE,

  /**
   * Keyword tokens.
   */
  CLASS, ELSE, FINAL, IF, NEW, PRINT, PROGRAM,
  READ, RETURN, VOID, WHILE,

  EOF
}
