/*  MicroJava Scanner Tester
 *  ========================
 *  Place this file in a subdirectory microjava
 *  Compile with
 *    javac microjava\TestScanner.java
 *  Run with
 *    java microjava.TestScanner <inputFileName>
 */
package microjava;

import java.io.*;

/**
 * Class that tests the output tokens of the scanner using a source file.
 */
public class TestScanner {
	/**
	 * Main method of the scanner tester
	 */
	public static void main(String args[]) {
		if (args.length == 0) {
			System.out.println("-- synopsis: java microjava.TestScanner <inputfileName>");
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
