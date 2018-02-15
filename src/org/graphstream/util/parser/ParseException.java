/*
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */

/**
 * @since 2009-02-19
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.util.parser;

/**
 * This exception is thrown when parse errors are encountered. You can
 * explicitly create objects of this exception type by calling the method
 * generateParseException in the generated parser.
 * 
 * You can modify this class to customize your error reporting mechanisms so
 * long as you retain the public fields.
 */
public class ParseException extends Exception {

	/**
	 * The version identifier for this Serializable class. Increment only if the
	 * <i>serialized</i> form of the class changes.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This constructor is used by the method "generateParseException" in the
	 * generated parser. Calling this constructor generates a new object of this
	 * type with the fields "currentToken", "expectedTokenSequences", and
	 * "tokenImage" set.
	 */
	public ParseException(Token currentTokenVal, int[][] expectedTokenSequencesVal, String[] tokenImageVal) {
		super(initialise(currentTokenVal, expectedTokenSequencesVal, tokenImageVal));
		currentToken = currentTokenVal;
		expectedTokenSequences = expectedTokenSequencesVal;
		tokenImage = tokenImageVal;
	}

	/**
	 * The following constructors are for use by you for whatever purpose you can
	 * think of. Constructing the exception in this manner makes the exception
	 * behave in the normal way - i.e., as documented in the class "Throwable". The
	 * fields "errorToken", "expectedTokenSequences", and "tokenImage" do not
	 * contain relevant information. The JavaCC generated code does not use these
	 * constructors.
	 */

	public ParseException() {
		super();
	}

	/** Constructor with message. */
	public ParseException(String message) {
		super(message);
	}

	/**
	 * This is the last token that has been consumed successfully. If this object
	 * has been created due to a parse error, the token followng this token will
	 * (therefore) be the first error token.
	 */
	public Token currentToken;

	/**
	 * Each entry in this array is an array of integers. Each array of integers
	 * represents a sequence of tokens (by their ordinal values) that is expected at
	 * this point of the parse.
	 */
	public int[][] expectedTokenSequences;

	/**
	 * This is a reference to the "tokenImage" array of the generated parser within
	 * which the parse error occurred. This array is defined in the generated
	 * ...Constants interface.
	 */
	public String[] tokenImage;

	/**
	 * It uses "currentToken" and "expectedTokenSequences" to generate a parse error
	 * message and returns it. If this object has been created due to a parse error,
	 * and you do not catch it (it gets thrown from the parser) the correct error
	 * message gets displayed.
	 */
	private static String initialise(Token currentToken, int[][] expectedTokenSequences, String[] tokenImage) {
		String eol = System.getProperty("line.separator", "\n");
		StringBuffer expected = new StringBuffer();
		int maxSize = 0;
		for (int i = 0; i < expectedTokenSequences.length; i++) {
			if (maxSize < expectedTokenSequences[i].length) {
				maxSize = expectedTokenSequences[i].length;
			}
			for (int j = 0; j < expectedTokenSequences[i].length; j++) {
				expected.append(tokenImage[expectedTokenSequences[i][j]]).append(' ');
			}
			if (expectedTokenSequences[i][expectedTokenSequences[i].length - 1] != 0) {
				expected.append("...");
			}
			expected.append(eol).append("    ");
		}
		String retval = "Encountered \"";
		Token tok = currentToken.next;
		for (int i = 0; i < maxSize; i++) {
			if (i != 0)
				retval += " ";
			if (tok.kind == 0) {
				retval += tokenImage[0];
				break;
			}
			retval += " " + tokenImage[tok.kind];
			retval += " \"";
			retval += add_escapes(tok.image);
			retval += " \"";
			tok = tok.next;
		}
		retval += "\" at line " + currentToken.next.beginLine + ", column " + currentToken.next.beginColumn;
		retval += "." + eol;
		if (expectedTokenSequences.length == 1) {
			retval += "Was expecting:" + eol + "    ";
		} else {
			retval += "Was expecting one of:" + eol + "    ";
		}
		retval += expected.toString();
		return retval;
	}

	/**
	 * The end of line string for this machine.
	 */
	protected String eol = System.getProperty("line.separator", "\n");

	/**
	 * Used to convert raw characters to their escaped version when these raw
	 * version cannot be used as part of an ASCII string literal.
	 */
	static String add_escapes(String str) {
		StringBuffer retval = new StringBuffer();
		char ch;
		for (int i = 0; i < str.length(); i++) {
			switch (str.charAt(i)) {
			case 0:
				continue;
			case '\b':
				retval.append("\\b");
				continue;
			case '\t':
				retval.append("\\t");
				continue;
			case '\n':
				retval.append("\\n");
				continue;
			case '\f':
				retval.append("\\f");
				continue;
			case '\r':
				retval.append("\\r");
				continue;
			case '\"':
				retval.append("\\\"");
				continue;
			case '\'':
				retval.append("\\\'");
				continue;
			case '\\':
				retval.append("\\\\");
				continue;
			default:
				if ((ch = str.charAt(i)) < 0x20 || ch > 0x7e) {
					String s = "0000" + Integer.toString(ch, 16);
					retval.append("\\u" + s.substring(s.length() - 4, s.length()));
				} else {
					retval.append(ch);
				}
				continue;
			}
		}
		return retval.toString();
	}

}
