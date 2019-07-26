/*  MicroJava Parser Tester
 *  ========================
 *  Place this file in a subdirectory microjava
 *  Compile with
 *    javac microjava\Scanner.java microjava\Parser.java microjava\TestParser.java
 *  Run with
 *    java microjava.TestParser <inputFileName>
 */
package microjava;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Class that tests the parser using a source file.
 */
public class TestParser {

	/**
	 * Main method of the parser tester.
	 */
	public static void main(String args[]) {
		if (args.length == 0) {
			System.out.println("Synopsis: java microjava.TestParser <inputfileName>");
			return;
		}

		String source = args[0];

		try {
			Scanner scanner = new Scanner(new InputStreamReader(new FileInputStream(source)));
			Parser parser = new Parser(scanner);

			parser.parse();
			System.out.println(parser.errors + " errors detected");
		} catch (IOException e) {
			System.err.println("Cannot open input file " + source);
		}
	}

}
