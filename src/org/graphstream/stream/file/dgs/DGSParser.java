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
 * @since 2011-10-04
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file.dgs;

import java.awt.Color;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;

import org.graphstream.graph.implementations.AbstractElement.AttributeChangeEvent;
import org.graphstream.stream.SourceBase.ElementType;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.util.parser.ParseException;
import org.graphstream.util.parser.Parser;

// import org.graphstream.util.time.ISODateIO;

public class DGSParser implements Parser {
	static enum Token {
		AN, CN, DN, AE, CE, DE, CG, ST, CL, TF, EOF
	}

	protected static final int BUFFER_SIZE = 4096;

	public static final int ARRAY_OPEN = '{';
	public static final int ARRAY_CLOSE = '}';

	public static final int MAP_OPEN = '[';
	public static final int MAP_CLOSE = ']';

	Reader reader;
	int line, column;
	int bufferCapacity, bufferPosition;
	char[] buffer;
	int[] pushback;
	int pushbackOffset;
	FileSourceDGS dgs;
	String sourceId;
	Token lastDirective;

	// ISODateIO dateIO;

	public DGSParser(FileSourceDGS dgs, Reader reader) {
		this.dgs = dgs;
		this.reader = reader;
		bufferCapacity = 0;
		buffer = new char[BUFFER_SIZE];
		pushback = new int[10];
		pushbackOffset = -1;
		this.sourceId = String.format("<DGS stream %x>", System.nanoTime());

		// try {
		// dateIO = new ISODateIO();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.util.parser.Parser#close()
	 */
	public void close() throws IOException {
		reader.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.util.parser.Parser#open()
	 */
	public void open() throws IOException, ParseException {
		header();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.util.parser.Parser#all()
	 */
	public void all() throws IOException, ParseException {
		header();

		while (next())
			;
	}

	protected int nextChar() throws IOException {
		int c;

		if (pushbackOffset >= 0)
			return pushback[pushbackOffset--];

		if (bufferCapacity == 0 || bufferPosition >= bufferCapacity) {
			bufferCapacity = reader.read(buffer, 0, BUFFER_SIZE);
			bufferPosition = 0;
		}

		if (bufferCapacity <= 0)
			return -1;

		c = buffer[bufferPosition++];

		//
		// Handle special EOL
		// - LF
		// - CR
		// - CR+LF
		//
		if (c == '\r') {
			if (bufferPosition < bufferCapacity) {
				if (buffer[bufferPosition] == '\n')
					bufferPosition++;
			} else {
				c = nextChar();

				if (c != '\n')
					pushback(c);
			}

			c = '\n';
		}

		if (c == '\n') {
			line++;
			column = 0;
		} else
			column++;

		return c;
	}

	protected void pushback(int c) throws IOException {
		if (c < 0)
			return;

		if (pushbackOffset + 1 >= pushback.length)
			throw new IOException("pushback buffer overflow");

		pushback[++pushbackOffset] = c;
	}

	protected void skipLine() throws IOException {
		int c;

		while ((c = nextChar()) != '\n' && c >= 0)
			;
	}

	protected void skipWhitespaces() throws IOException {
		int c;

		while ((c = nextChar()) == ' ' || c == '\t')
			;

		pushback(c);
	}

	protected void header() throws IOException, ParseException {
		int[] dgs = new int[6];

		for (int i = 0; i < 6; i++)
			dgs[i] = nextChar();

		if (dgs[0] != 'D' || dgs[1] != 'G' || dgs[2] != 'S')
			throw parseException(
					String.format("bad magic header, 'DGS' expected, got '%c%c%c'", dgs[0], dgs[1], dgs[2]));

		if (dgs[3] != '0' || dgs[4] != '0' || dgs[5] < '0' || dgs[5] > '5')
			throw parseException(String.format("bad version \"%c%c%c\"", dgs[0], dgs[1], dgs[2]));

		if (nextChar() != '\n')
			throw parseException("end-of-line is missing");

		skipLine();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.util.parser.Parser#next()
	 */
	public boolean next() throws IOException, ParseException {
		int c;
		String nodeId;
		String edgeId, source, target;

		lastDirective = directive();

		switch (lastDirective) {
		case AN:
			nodeId = id();
			dgs.sendNodeAdded(sourceId, nodeId);

			attributes(ElementType.NODE, nodeId);
			break;
		case CN:
			nodeId = id();
			attributes(ElementType.NODE, nodeId);
			break;
		case DN:
			nodeId = id();
			dgs.sendNodeRemoved(sourceId, nodeId);
			break;
		case AE:
			edgeId = id();
			source = id();

			skipWhitespaces();
			c = nextChar();

			if (c != '<' && c != '>')
				pushback(c);

			target = id();

			switch (c) {
			case '>':
				dgs.sendEdgeAdded(sourceId, edgeId, source, target, true);
				break;
			case '<':
				dgs.sendEdgeAdded(sourceId, edgeId, target, source, true);
				break;
			default:
				dgs.sendEdgeAdded(sourceId, edgeId, source, target, false);
				break;
			}

			attributes(ElementType.EDGE, edgeId);
			break;
		case CE:
			edgeId = id();
			attributes(ElementType.EDGE, edgeId);
			break;
		case DE:
			edgeId = id();
			dgs.sendEdgeRemoved(sourceId, edgeId);
			break;
		case CG:
			attributes(ElementType.GRAPH, null);
			break;
		case ST:
			// TODO release 1.2 : read timestamp
			// Version for 1.2 :
			// --------------------------------
			// long step;
			// step = timestamp();
			// sendStepBegins(sourceId, ste);

			double step;

			step = Double.valueOf(id());
			dgs.sendStepBegins(sourceId, step);
			break;
		case CL:
			dgs.sendGraphCleared(sourceId);
			break;
		case TF:
			// TODO for release 1.2
			// String tf;
			// tf = string();

			// try {
			// dateIO.setFormat(tf);
			// } catch (Exception e) {
			// throw parseException("invalid time format \"%s\"", tf);
			// }

			break;
		case EOF:
			return false;
		}

		skipWhitespaces();
		c = nextChar();

		if (c == '#') {
			skipLine();
			return true;
		}

		if (c < 0)
			return false;

		if (c != '\n')
			throw parseException("eol expected, got '%c'", c);

		return true;
	}

	public boolean nextStep() throws IOException, ParseException {
		boolean r;
		Token next;

		do {
			r = next();
			next = directive();

			if (next != Token.EOF) {
				pushback(next.name().charAt(1));
				pushback(next.name().charAt(0));
			}
		} while (next != Token.ST && next != Token.EOF);

		return r;
	}

	protected void attributes(ElementType type, String id) throws IOException, ParseException {
		int c;

		skipWhitespaces();

		while ((c = nextChar()) != '\n' && c != '#' && c >= 0) {
			pushback(c);
			attribute(type, id);
			skipWhitespaces();
		}

		pushback(c);
	}

	protected void attribute(ElementType type, String elementId) throws IOException, ParseException {
		String key;
		Object value = null;
		int c;
		AttributeChangeEvent ch = AttributeChangeEvent.CHANGE;

		skipWhitespaces();
		c = nextChar();

		if (c == '+')
			ch = AttributeChangeEvent.ADD;
		else if (c == '-')
			ch = AttributeChangeEvent.REMOVE;
		else
			pushback(c);

		key = id();

		if (key == null)
			throw parseException("attribute key expected");

		if (ch != AttributeChangeEvent.REMOVE) {

			skipWhitespaces();
			c = nextChar();

			if (c == '=' || c == ':') {
				skipWhitespaces();
				value = value(true);
			} else {
				value = Boolean.TRUE;
				pushback(c);
			}
		}

		dgs.sendAttributeChangedEvent(sourceId, elementId, type, key, ch, null, value);
	}

	protected Object value(boolean array) throws IOException, ParseException {
		int c;
		LinkedList<Object> l = null;
		Object o;

		do {
			skipWhitespaces();
			c = nextChar();
			pushback(c);

			switch (c) {
			case '\'':
			case '\"':
				o = string();
				break;
			case '#':
				o = color();
				break;
			case ARRAY_OPEN:
				//
				// Skip ARRAY_OPEN
				nextChar();
				//

				skipWhitespaces();
				o = value(true);
				skipWhitespaces();

				//
				// Check if next char is ARRAY_CLOSE
				if (nextChar() != ARRAY_CLOSE)
					throw parseException("'%c' expected", ARRAY_CLOSE);
				//

				if (!o.getClass().isArray())
					o = new Object[] { o };

				break;
			case MAP_OPEN:
				o = map();
				break;
			default: {
				String word = id();

				if (word == null)
					throw parseException("missing value");

				if ((c >= '0' && c <= '9') || c == '-') {
					try {
						if (word.indexOf('.') > 0)
							o = Double.valueOf(word);
						else {
							try {
								o = Integer.valueOf(word);
							} catch (NumberFormatException e) {
								o = Long.valueOf(word);
							}
						}
					} catch (NumberFormatException e) {
						throw parseException("invalid number format '%s'", word);
					}
				} else {
					if (word.equalsIgnoreCase("true"))
						o = Boolean.TRUE;
					else if (word.equalsIgnoreCase("false"))
						o = Boolean.FALSE;
					else
						o = word;
				}

				break;
			}
			}

			c = nextChar();

			if (l == null && array && c == ',') {
				l = new LinkedList<Object>();
				l.add(o);
			} else if (l != null)
				l.add(o);
		} while (array && c == ',');

		pushback(c);

		if (l == null)
			return o;

		return l.toArray();
	}

	protected Color color() throws IOException, ParseException {
		int c;
		int r, g, b, a;
		StringBuilder hexa = new StringBuilder();

		c = nextChar();

		if (c != '#')
			throw parseException("'#' expected");

		for (int i = 0; i < 6; i++) {
			c = nextChar();

			if ((c >= 0 && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))
				hexa.appendCodePoint(c);
			else
				throw parseException("hexadecimal value expected");
		}

		r = Integer.parseInt(hexa.substring(0, 2), 16);
		g = Integer.parseInt(hexa.substring(2, 4), 16);
		b = Integer.parseInt(hexa.substring(4, 6), 16);

		c = nextChar();

		if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')) {
			hexa.appendCodePoint(c);

			c = nextChar();

			if ((c >= 0 && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))
				hexa.appendCodePoint(c);
			else
				throw parseException("hexadecimal value expected");

			a = Integer.parseInt(hexa.substring(6, 8), 16);
		} else {
			a = 255;
			pushback(c);
		}

		return new Color(r, g, b, a);
	}

	protected Object array() throws IOException, ParseException {
		int c;
		LinkedList<Object> array = new LinkedList<Object>();

		c = nextChar();

		if (c != ARRAY_OPEN)
			throw parseException("'%c' expected", ARRAY_OPEN);

		skipWhitespaces();
		c = nextChar();

		while (c != ARRAY_CLOSE) {
			pushback(c);
			array.add(value(false));

			skipWhitespaces();
			c = nextChar();

			if (c != ARRAY_CLOSE && c != ',')
				throw parseException("'%c' or ',' expected, got '%c'", ARRAY_CLOSE, c);

			if (c == ',') {
				skipWhitespaces();
				c = nextChar();
			}
		}

		if (c != ARRAY_CLOSE)
			throw parseException("'%c' expected", ARRAY_CLOSE);

		return array.toArray();
	}

	protected Object map() throws IOException, ParseException {
		int c;
		HashMap<String, Object> map = new HashMap<String, Object>();
		String key;
		Object value;

		c = nextChar();

		if (c != MAP_OPEN)
			throw parseException("'%c' expected", MAP_OPEN);

		c = nextChar();

		while (c != MAP_CLOSE) {
			pushback(c);
			key = id();

			if (key == null)
				throw parseException("id expected here, '%c'", c);

			skipWhitespaces();
			c = nextChar();

			if (c == '=' || c == ':') {
				skipWhitespaces();
				value = value(false);
			} else {
				value = Boolean.TRUE;
				pushback(c);
			}

			map.put(key, value);

			skipWhitespaces();
			c = nextChar();

			if (c != MAP_CLOSE && c != ',')
				throw parseException("'%c' or ',' expected, got '%c'", MAP_CLOSE, c);

			if (c == ',') {
				skipWhitespaces();
				c = nextChar();
			}
		}

		if (c != MAP_CLOSE)
			throw parseException("'%c' expected", MAP_CLOSE);

		return map;
	}

	protected Token directive() throws IOException, ParseException {
		int c1, c2;

		//
		// Skip comment and empty lines
		//
		do {
			c1 = nextChar();

			if (c1 == '#')
				skipLine();

			if (c1 < 0)
				return Token.EOF;
		} while (c1 == '#' || c1 == '\n');

		c2 = nextChar();

		if (c1 >= 'A' && c1 <= 'Z')
			c1 -= 'A' - 'a';

		if (c2 >= 'A' && c2 <= 'Z')
			c2 -= 'A' - 'a';

		switch (c1) {
		case 'a':
			if (c2 == 'n')
				return Token.AN;
			else if (c2 == 'e')
				return Token.AE;

			break;
		case 'c':
			switch (c2) {
			case 'n':
				return Token.CN;
			case 'e':
				return Token.CE;
			case 'g':
				return Token.CG;
			case 'l':
				return Token.CL;
			}

			break;
		case 'd':
			if (c2 == 'n')
				return Token.DN;
			else if (c2 == 'e')
				return Token.DE;

			break;
		case 's':
			if (c2 == 't')
				return Token.ST;

			break;
		case 't':
			if (c1 == 'f')
				return Token.TF;

			break;
		}

		throw parseException("unknown directive '%c%c'", c1, c2);
	}

	protected String string() throws IOException, ParseException {
		int c, s;
		StringBuilder builder;
		boolean slash;

		slash = false;
		builder = new StringBuilder();
		c = nextChar();

		if (c != '\"' && c != '\'')
			throw parseException("string expected");

		s = c;

		while ((c = nextChar()) != s || slash) {
			if (slash && c != s)
				builder.append("\\");

			slash = c == '\\';

			if (!slash) {
				if (!Character.isValidCodePoint(c))
					throw parseException("invalid code-point 0x%X", c);

				builder.appendCodePoint(c);
			}
		}

		return builder.toString();
	}

	protected String id() throws IOException, ParseException {
		int c;
		StringBuilder builder = new StringBuilder();

		skipWhitespaces();
		c = nextChar();
		pushback(c);

		if (c == '\"' || c == '\'') {
			return string();
		} else {
			boolean stop = false;

			while (!stop) {
				c = nextChar();

				switch (Character.getType(c)) {
				case Character.LOWERCASE_LETTER:
				case Character.UPPERCASE_LETTER:
				case Character.DECIMAL_DIGIT_NUMBER:
					break;
				case Character.DASH_PUNCTUATION:
					if (c != '-')
						stop = true;

					break;
				case Character.MATH_SYMBOL:
					if (c != '+')
						stop = true;

					break;
				case Character.CONNECTOR_PUNCTUATION:
					if (c != '_')
						stop = true;

					break;
				case Character.OTHER_PUNCTUATION:
					if (c != '.')
						stop = true;

					break;
				default:
					stop = true;
					break;
				}

				if (!stop)
					builder.appendCodePoint(c);
			}

			pushback(c);
		}

		if (builder.length() == 0)
			return null;

		return builder.toString();
	}

	/*
	 * protected long timestamp() throws IOException, ParseException { int c; String
	 * time;
	 * 
	 * c = nextChar(); pushback(c);
	 * 
	 * switch (c) { case '"': case '\'': time = string(); break; default:
	 * StringBuilder builder = new StringBuilder();
	 * 
	 * while ((c = nextChar()) != '\n' && c != '"') builder.appendCodePoint(c);
	 * 
	 * pushback(c); time = builder.toString(); break; }
	 * 
	 * pushback(c); return dateIO.parse(time).getTimeInMillis(); }
	 */

	protected ParseException parseException(String message, Object... args) {
		return new ParseException(
				String.format(String.format("parse error at (%d;%d) : %s", line, column, message), args));
	}
}
