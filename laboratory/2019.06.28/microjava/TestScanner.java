/* MicroJava Scanner Tester
   ========================
   Place this file in a subdirectory MJ
   Compile with
     javac MJ\TestScanner.java
   Run with
     java MJ.TestScanner <inputFileName>
*/
package microjava;

import java.io.*;

public class TestScanner {
	private static final int  // token codes
		none      = 0,
		ident     = 1,
		number    = 2,
		charCon   = 3,
		plus      = 4,
		minus     = 5,
		times     = 6,
		slash     = 7,
		rem       = 8,
		eql       = 9,
		neq       = 10,
		lss       = 11,
		leq       = 12,
		gtr       = 13,
		geq       = 14,
		assign    = 15,
		semicolon = 16,
		comma     = 17,
		period    = 18,
		lpar      = 19,
		rpar      = 20,
		lbrack    = 21,
		rbrack    = 22,
		lbrace    = 23,
		rbrace    = 24,
		class_    = 25,
		else_     = 26,
		final_    = 27,
		if_       = 28,
		new_      = 29,
		print_    = 30,
		program_  = 31,
		read_     = 32,
		return_   = 33,
		void_     = 34,
		while_    = 35,
		eof       = 36;

	private static String[] tokenName = {
		"none",
		"ident  ",
		"number ",
		"char   ",
		"+",
		"-",
		"*",
		"/",
		"%",
		"==",
		"!=",
		"<",
		"<=",
		">",
		">=",
		"=",
		";",
		",",
		".",
		"(",
		")",
		"[",
		"]",
		"{",
		"}",
		"class",
		"else",
		"final",
		"if",
		"new",
		"print",
		"program",
		"read",
		"return",
		"void",
		"while",
		"eof"
	};

	// Main method of the scanner tester
	public static void main(String args[]) {
		if (args.length == 0) {
			System.out.println("-- synopsis: java MJ.TestScanner <inputfileName>");
			return;
		}

		Token t = null;
		String source = args[0];

		try {
			Scanner scan = new Scanner(new InputStreamReader(new FileInputStream(source)));
			do {
				t = scan.next();
				System.out.println(t);
			} while (t.kind != TokenType.EOF);
		} catch (IOException e) {
			System.err.println("-- cannot open input file " + source);
		}
	}

}