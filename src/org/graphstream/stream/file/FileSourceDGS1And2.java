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
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

/**
 * Class responsible for parsing files in the DGS format (old versions of the
 * format).
 * 
 * <p>
 * The DGS file format is especially designed for storing dynamic graph
 * definitions into a file. More information about the DGS file format will be
 * found on the GraphStream web site:
 * <a href="http://graphstream-project.org/">http://graphstream-project.org/</a>
 * </p>
 * 
 * @see OldFileSourceDGS
 * @see FileSource
 */
public class FileSourceDGS1And2 extends FileSourceBase {
	// Constants

	/**
	 * Types of attributes.
	 */
	protected enum AttributeType {
		NUMBER, VECTOR, STRING
	};

	/**
	 * Pair <name,type> defining an attribute.
	 */
	protected static class AttributeFormat {
		/**
		 * Name of the attribute.
		 */
		public String name;

		/**
		 * Type of the attribute.
		 */
		public AttributeType type;

		/**
		 * New format descriptor for an attribute.
		 * 
		 * @param name
		 *            The attribute name.
		 * @param type
		 *            The attribute type.
		 */
		public AttributeFormat(String name, AttributeType type) {
			this.name = name;
			this.type = type;
		}

		/**
		 * Attribute name.
		 * 
		 * @return The name.
		 */
		public String getName() {
			return name;
		}

		/**
		 * Attribute format.
		 * 
		 * @return The format.
		 */
		public AttributeType getType() {
			return type;
		}
	}

	// Attributes

	/**
	 * Format version.
	 */
	protected int version;

	/**
	 * Name of the graph.
	 */
	protected String graphName;

	/**
	 * Number of step given in the header.
	 */
	protected int stepCountAnnounced;

	/**
	 * Number of events given in the header.
	 */
	protected int eventCountAnnounced;

	/**
	 * Real number of step at current time.
	 */
	protected int stepCount;

	/**
	 * Real number of events at current time.
	 */
	protected int eventCount;

	/**
	 * Attribute count and type expected for each node add and modify command.
	 */
	protected ArrayList<AttributeFormat> nodesFormat = new ArrayList<AttributeFormat>();

	/**
	 * Attribute count and type expected for each edges add and modify command.
	 */
	protected ArrayList<AttributeFormat> edgesFormat = new ArrayList<AttributeFormat>();

	/**
	 * An attribute set.
	 */
	protected HashMap<String, Object> attributes = new HashMap<String, Object>();

	// Constructors

	/**
	 * New reader for the DGS graph file format versions 1 and 2.
	 */
	public FileSourceDGS1And2() {
		super(true /* EOL is significant */);
	}

	// Access

	// Command

	@Override
	public boolean nextEvents() throws IOException {
		String key = getWordOrSymbolOrStringOrEolOrEof();
		String tag = null;

		if (key.equals("ce")) {
			tag = getStringOrWordOrNumber();

			readAttributes(edgesFormat);

			for (String k : attributes.keySet()) {
				Object value = attributes.get(k);
				sendEdgeAttributeChanged(graphName, tag, k, null, value);
			}

			if (eatEolOrEof() == StreamTokenizer.TT_EOF)
				return false;
		} else if (key.equals("cn")) {
			tag = getStringOrWordOrNumber();

			readAttributes(nodesFormat);

			for (String k : attributes.keySet()) {
				Object value = attributes.get(k);
				sendNodeAttributeChanged(graphName, tag, k, null, value);
			}

			if (eatEolOrEof() == StreamTokenizer.TT_EOF)
				return false;
		} else if (key.equals("ae")) {
			tag = getStringOrWordOrNumber();
			String fromTag = getStringOrWordOrNumber();
			String toTag = getStringOrWordOrNumber();

			readAttributes(edgesFormat);

			sendEdgeAdded(graphName, tag, fromTag, toTag, false);

			if (attributes != null) {
				for (String k : attributes.keySet()) {
					Object value = attributes.get(k);
					sendEdgeAttributeAdded(graphName, tag, k, value);
				}
			}

			if (eatEolOrEof() == StreamTokenizer.TT_EOF)
				return false;
		} else if (key.equals("an")) {
			tag = getStringOrWordOrNumber();

			readAttributes(nodesFormat);
			sendNodeAdded(graphName, tag);

			if (attributes != null) {
				for (String k : attributes.keySet()) {
					Object value = attributes.get(k);
					sendNodeAttributeAdded(graphName, tag, k, value);
				}
			}

			if (eatEolOrEof() == StreamTokenizer.TT_EOF)
				return false;
		} else if (key.equals("de")) {
			tag = getStringOrWordOrNumber();

			sendEdgeRemoved(graphName, tag);

			if (eatEolOrEof() == StreamTokenizer.TT_EOF)
				return false;
		} else if (key.equals("dn")) {
			tag = getStringOrWordOrNumber();

			sendNodeRemoved(graphName, tag);

			if (eatEolOrEof() == StreamTokenizer.TT_EOF)
				return false;
		} else if (key.equals("st")) {
			String w = getWordOrNumber();

			try {
				double time = Double.parseDouble(w);

				sendStepBegins(graphName, time);
			} catch (NumberFormatException e) {
				parseError("expecting a number after `st', got `" + w + "'");
			}

			if (eatEolOrEof() == StreamTokenizer.TT_EOF)
				return false;
		} else if (key == "#") {
			eatAllUntilEol();
		} else if (key == "EOL") {
			return true;
		} else if (key == "EOF") {
			return false;
		} else {
			parseError("found an unknown key in file '" + key + "' (expecting an,ae,cn,ce,dn,de or st)");
		}

		return true;
	}

	/**
	 * tries to read all the events between 2 steps
	 */
	public boolean nextStep() throws IOException {
		String key = "";
		String tag = null;

		while (!key.equals("st") && !key.equals("EOF")) {
			key = getWordOrSymbolOrStringOrEolOrEof();

			if (key.equals("ce")) {
				tag = getStringOrWordOrNumber();

				readAttributes(edgesFormat);

				for (String k : attributes.keySet()) {
					Object value = attributes.get(k);
					sendEdgeAttributeChanged(graphName, tag, k, null, value);
				}

				if (eatEolOrEof() == StreamTokenizer.TT_EOF)
					return false;
			} else if (key.equals("cn")) {
				tag = getStringOrWordOrNumber();

				readAttributes(nodesFormat);

				for (String k : attributes.keySet()) {
					Object value = attributes.get(k);
					sendNodeAttributeChanged(graphName, tag, k, null, value);
				}

				if (eatEolOrEof() == StreamTokenizer.TT_EOF)
					return false;
			} else if (key.equals("ae")) {
				tag = getStringOrWordOrNumber();
				String fromTag = getStringOrWordOrNumber();
				String toTag = getStringOrWordOrNumber();

				readAttributes(edgesFormat);
				sendEdgeAdded(graphName, tag, fromTag, toTag, false);

				if (attributes != null) {
					for (String k : attributes.keySet()) {
						Object value = attributes.get(k);
						sendNodeAttributeAdded(graphName, tag, k, value);
					}
				}

				if (eatEolOrEof() == StreamTokenizer.TT_EOF)
					return false;
			} else if (key.equals("an")) {
				tag = getStringOrWordOrNumber();

				readAttributes(nodesFormat);
				sendNodeAdded(graphName, tag);

				if (attributes != null) {
					for (String k : attributes.keySet()) {
						Object value = attributes.get(k);
						sendNodeAttributeAdded(graphName, tag, k, value);
					}
				}

				if (eatEolOrEof() == StreamTokenizer.TT_EOF)
					return false;
			} else if (key.equals("de")) {
				tag = getStringOrWordOrNumber();

				sendEdgeRemoved(graphName, tag);

				if (eatEolOrEof() == StreamTokenizer.TT_EOF)
					return false;
			} else if (key.equals("dn")) {
				tag = getStringOrWordOrNumber();

				sendNodeRemoved(graphName, tag);

				if (eatEolOrEof() == StreamTokenizer.TT_EOF)
					return false;
			} else if (key.equals("st")) {
				String w = getWordOrNumber();

				try {
					double time = Double.parseDouble(w);
					sendStepBegins(graphName, time);
				} catch (NumberFormatException e) {
					parseError("expecting a number after `st', got `" + w + "'");
				}

				if (eatEolOrEof() == StreamTokenizer.TT_EOF)
					return false;
			} else if (key == "#") {
				eatAllUntilEol();
			} else if (key == "EOL") {
				// NOP
			} else if (key == "EOF") {
				return false;
			} else {
				parseError("found an unknown key in file '" + key + "' (expecting an,ae,cn,ce,dn,de or st)");
			}
		}

		return true;
	}

	protected void readAttributes(ArrayList<AttributeFormat> formats) throws IOException {
		attributes.clear();

		if (formats.size() > 0) {
			for (AttributeFormat format : formats) {
				if (format.type == AttributeType.NUMBER) {
					readNumberAttribute(format.name);
				} else if (format.type == AttributeType.VECTOR) {
					readVectorAttribute(format.name);
				} else if (format.type == AttributeType.STRING) {
					readStringAttribute(format.name);
				}
			}
		}
	}

	protected void readNumberAttribute(String name) throws IOException {
		int tok = st.nextToken();

		if (isNull(tok)) {
			attributes.put(name, new Double(0));
		} else {
			st.pushBack();

			double n = getNumber();

			attributes.put(name, new Double(n));
		}
	}

	protected void readVectorAttribute(String name) throws IOException {
		int tok = st.nextToken();

		if (isNull(tok)) {
			attributes.put(name, new ArrayList<Double>());
		} else {

			boolean loop = true;

			ArrayList<Double> vector = new ArrayList<Double>();

			while (loop) {
				if (tok != StreamTokenizer.TT_NUMBER)
					parseError("expecting a number, " + gotWhat(tok));

				vector.add(st.nval);

				tok = st.nextToken();

				if (tok != ',') {
					loop = false;
					st.pushBack();
				} else {
					tok = st.nextToken();
				}
			}

			attributes.put(name, vector);
		}
	}

	protected void readStringAttribute(String name) throws IOException {
		String s = getStringOrWordOrNumber();

		attributes.put(name, s);
	}

	protected boolean isNull(int tok) {
		if (tok == StreamTokenizer.TT_WORD)
			return (st.sval.equals("null"));

		return false;
	}

	@Override
	public void begin(String filename) throws IOException {
		super.begin(filename);
		init();
	}

	@Override
	public void begin(InputStream stream) throws IOException {
		super.begin(stream);
		init();
	}

	@Override
	public void begin(Reader reader) throws IOException {
		super.begin(reader);
		init();
	}

	@Override
	public void begin(URL url) throws IOException {
		super.begin(url);
		init();
	}

	protected void init() throws IOException {
		st.parseNumbers();

		String magic = eatOneOfTwoWords("DGS001", "DGS002");

		if (magic.equals("DGS001"))
			version = 1;
		else
			version = 2;

		eatEol();
		graphName = getWord();
		stepCountAnnounced = (int) getNumber();// Integer.parseInt( getWord() );
		eventCountAnnounced = (int) getNumber();// Integer.parseInt( getWord()
												// );
		eatEol();

		if (graphName != null) {
			attributes.clear();
			attributes.put("label", graphName);
			sendGraphAttributeAdded(graphName, "label", graphName);
		} else {
			graphName = "DGS_";
		}

		graphName = String.format("%s_%d", graphName, System.currentTimeMillis() + ((long) Math.random() * 10));

		readAttributeFormat();
	}

	protected void readAttributeFormat() throws IOException {
		int tok = st.nextToken();

		if (tok == StreamTokenizer.TT_WORD && st.sval.equals("nodes")) {
			parseAttributeFormat(nodesFormat);
			tok = st.nextToken();
		}

		if (tok == StreamTokenizer.TT_WORD && st.sval.equals("edges")) {
			parseAttributeFormat(edgesFormat);
		} else {
			st.pushBack();
		}
	}

	protected void parseAttributeFormat(ArrayList<AttributeFormat> format) throws IOException {
		int tok = st.nextToken();

		while (tok != StreamTokenizer.TT_EOL) {
			if (tok == StreamTokenizer.TT_WORD) {
				String name = st.sval;

				eatSymbol(':');

				tok = st.nextToken();

				if (tok == StreamTokenizer.TT_WORD) {
					String type = st.sval.toLowerCase();

					if (type.equals("number") || type.equals("n")) {
						format.add(new AttributeFormat(name, AttributeType.NUMBER));
					} else if (type.equals("string") || type.equals("s")) {
						format.add(new AttributeFormat(name, AttributeType.STRING));
					} else if (type.equals("vector") || type.equals("v")) {
						format.add(new AttributeFormat(name, AttributeType.VECTOR));
					} else {
						parseError("unknown attribute type `" + type
								+ "' (only `number', `vector' and `string' are accepted)");
					}
				} else {
					parseError("expecting an attribute type, got `" + gotWhat(tok) + "'");
				}
			} else {
				parseError("expecting an attribute name, got `" + gotWhat(tok) + "'");
			}

			tok = st.nextToken();
		}
	}

	@Override
	protected void continueParsingInInclude() throws IOException {
	}

	@Override
	protected Reader createReaderFrom(String file) throws FileNotFoundException {
		InputStream is = null;

		try {
			is = new GZIPInputStream(new FileInputStream(file));
		} catch (IOException e) {
			is = new FileInputStream(file);
		}

		return new BufferedReader(new InputStreamReader(is));
	}

	@Override
	protected Reader createReaderFrom(InputStream stream) {

		return new BufferedReader(new InputStreamReader(stream));
	}

	@Override
	protected void configureTokenizer(StreamTokenizer tok) throws IOException {
		if (COMMENT_CHAR > 0)
			tok.commentChar(COMMENT_CHAR);
		// tok.quoteChar( QUOTE_CHAR );
		tok.eolIsSignificant(eol_is_significant);
		tok.wordChars('_', '_');
		tok.ordinaryChar('1');
		tok.ordinaryChar('2');
		tok.ordinaryChar('3');
		tok.ordinaryChar('4');
		tok.ordinaryChar('5');
		tok.ordinaryChar('6');
		tok.ordinaryChar('7');
		tok.ordinaryChar('8');
		tok.ordinaryChar('9');
		tok.ordinaryChar('0');
		tok.ordinaryChar('.');
		tok.ordinaryChar('-');
		tok.wordChars('1', '1');
		tok.wordChars('2', '2');
		tok.wordChars('3', '3');
		tok.wordChars('4', '4');
		tok.wordChars('5', '5');
		tok.wordChars('6', '6');
		tok.wordChars('7', '7');
		tok.wordChars('8', '8');
		tok.wordChars('9', '9');
		tok.wordChars('0', '0');
		tok.wordChars('.', '.');
		tok.wordChars('-', '-');
		// tok.parseNumbers();
	}
}