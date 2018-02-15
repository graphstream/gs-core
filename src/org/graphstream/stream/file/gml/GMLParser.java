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
 * @since 2011-07-04
 * 
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file.gml;

import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;

import org.graphstream.stream.file.FileSourceGML;

import org.graphstream.util.parser.ParseException;
import org.graphstream.util.parser.Parser;
import org.graphstream.util.parser.SimpleCharStream;
import org.graphstream.util.parser.Token;
import org.graphstream.util.parser.TokenMgrError;

@SuppressWarnings("unused")
public class GMLParser implements Parser, GMLParserConstants {
	boolean inGraph = false;
	GMLContext ctx;
	boolean step;

	public GMLParser(FileSourceGML gml, InputStream stream) {
		this(stream);
		this.ctx = new GMLContext(gml);
	}

	public GMLParser(FileSourceGML gml, Reader stream) {
		this(stream);
		this.ctx = new GMLContext(gml);
	}

	public boolean isInGraph() {
		return inGraph;
	}

	public void open() throws IOException, ParseException {
	}

	public boolean next() throws IOException, ParseException {
		KeyValues kv = null;
		kv = nextEvents();
		ctx.handleKeyValues(kv);

		return (kv != null);
	}

	public boolean step() throws IOException, ParseException {
		KeyValues kv = null;
		step = false;

		while ((kv = nextEvents()) != null && !step)
			ctx.handleKeyValues(kv);

		if (kv != null)
			ctx.setNextStep(kv);

		return (kv != null);
	}

	/**
	 * Closes the parser, closing the opened stream.
	 */
	public void close() throws IOException {
		jj_input_stream.close();
	}

	/*****************************************************************/
	/* The parser. */
	/*****************************************************************/

	/** Unused rule, call it to slurp in the whole file. */
	final public void start() throws ParseException {
		list();
	}

	final public void all() throws ParseException, IOException {
		KeyValues values = new KeyValues();
		String key;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case GRAPH:
			graphStart();
			ctx.setIsInGraph(true);
			ctx.setDirected(false);
			break;
		case DIGRAPH:
			diGraphStart();
			ctx.setIsInGraph(true);
			ctx.setDirected(true);
			break;
		default:
			jj_la1[0] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		label_1: while (true) {
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case STRING:
			case KEY:
			case COMMENT:
				;
				break;
			default:
				jj_la1[1] = jj_gen;
				break label_1;
			}
			key = keyValue(values);
			values.key = key;
			ctx.handleKeyValues(values);
			values.clear();
		}
		graphEnd();
		values.key = null;
		inGraph = false;
		jj_consume_token(0);
	}

	final public void graphStart() throws ParseException {
		jj_consume_token(GRAPH);
		jj_consume_token(LSQBR);
	}

	final public void diGraphStart() throws ParseException {
		jj_consume_token(DIGRAPH);
		jj_consume_token(LSQBR);
	}

	final public void graphEnd() throws ParseException {
		jj_consume_token(RSQBR);
	}

	/**
	 * The top-level method to be called by the file source. Returns a set of
	 * top-level key values or null if the end of the file was reached.
	 * 
	 * Top-level key values are nodes and edges as well as all key-values defined
	 * before and after the graph.
	 */
	final public KeyValues nextEvents() throws ParseException {
		KeyValues values = new KeyValues();
		String key;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case GRAPH:
			graphStart();
			values.key = null;
			ctx.setIsInGraph(true);
			ctx.setDirected(false);
			break;
		case DIGRAPH:
			diGraphStart();
			values.key = null;
			ctx.setIsInGraph(true);
			ctx.setDirected(true);
			break;
		case RSQBR:
			graphEnd();
			values.key = null;
			ctx.setIsInGraph(false);
			break;
		case STRING:
		case KEY:
		case COMMENT:
			key = keyValue(values);
			values.key = key;
			break;
		case 0:
			jj_consume_token(0);
			values = null;
			break;
		default:
			jj_la1[2] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		{
			if (true)
				return values;
		}
		throw new Error("Missing return statement in function");
	}

	/**
	 * A list of key values, all values are stored in a KeyValues object.
	 */
	final public KeyValues list() throws ParseException {
		KeyValues values = new KeyValues();
		label_2: while (true) {
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case STRING:
			case KEY:
			case COMMENT:
				;
				break;
			default:
				jj_la1[3] = jj_gen;
				break label_2;
			}
			keyValue(values);
		}
		{
			if (true)
				return values;
		}
		throw new Error("Missing return statement in function");
	}

	/**
	 * A set of key and value, the value can recursively be a list of key-values.
	 * Only the key-value list "graph [ ... ]" is not parsed by this rule, and
	 * parsed by another rules, so that the nextEvent() rule can be called
	 * repeatedly.
	 */
	final public String keyValue(KeyValues values) throws ParseException {
		Token k;
		String key;
		Object v;
		boolean isGraph = false;
		label_3: while (true) {
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case COMMENT:
				;
				break;
			default:
				jj_la1[4] = jj_gen;
				break label_3;
			}
			jj_consume_token(COMMENT);
		}
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case KEY:
			k = jj_consume_token(KEY);
			key = k.image;
			if (key.equalsIgnoreCase("step"))
				step = true;
			break;
		case STRING:
			k = jj_consume_token(STRING);
			key = k.image.substring(1, k.image.length() - 2);
			break;
		default:
			jj_la1[5] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		v = value(key);
		values.put(key, v);
		values.line = k.beginLine;
		values.column = k.beginColumn;
		{
			if (true)
				return key;
		}
		throw new Error("Missing return statement in function");
	}

	/**
	 * A value for a key, either a number, a string or a recursive list of
	 * key-values.
	 */
	final public Object value(String key) throws ParseException {
		Token t;
		Object val;
		KeyValues kv;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case REAL:
			t = jj_consume_token(REAL);
			if (t.image.indexOf('.') < 0)
				val = Integer.valueOf(t.image);
			else
				val = Double.valueOf(t.image);
			break;
		case STRING:
			t = jj_consume_token(STRING);
			val = t.image.substring(1, t.image.length() - 1);
			break;
		case KEY:
			t = jj_consume_token(KEY);
			val = t.image;
			break;
		case LSQBR:
			jj_consume_token(LSQBR);
			kv = list();
			val = kv;
			jj_consume_token(RSQBR);
			break;
		default:
			jj_la1[6] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		{
			if (true)
				return val;
		}
		throw new Error("Missing return statement in function");
	}

	/** Generated Token Manager. */
	public GMLParserTokenManager token_source;
	SimpleCharStream jj_input_stream;
	/** Current token. */
	public Token token;
	/** Next token. */
	public Token jj_nt;
	private int jj_ntk;
	private int jj_gen;
	final private int[] jj_la1 = new int[7];
	static private int[] jj_la1_0;
	static {
		jj_la1_init_0();
	}

	private static void jj_la1_init_0() {
		jj_la1_0 = new int[] { 0x3000, 0xc800, 0xfa01, 0xc800, 0x8000, 0x4800, 0x4d00, };
	}

	/** Constructor with InputStream. */
	public GMLParser(java.io.InputStream stream) {
		this(stream, null);
	}

	/** Constructor with InputStream and supplied encoding */
	public GMLParser(java.io.InputStream stream, String encoding) {
		try {
			jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
		} catch (java.io.UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		token_source = new GMLParserTokenManager(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 7; i++)
			jj_la1[i] = -1;
	}

	/** Reinitialise. */
	public void ReInit(java.io.InputStream stream) {
		ReInit(stream, null);
	}

	/** Reinitialise. */
	public void ReInit(java.io.InputStream stream, String encoding) {
		try {
			jj_input_stream.ReInit(stream, encoding, 1, 1);
		} catch (java.io.UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		token_source.ReInit(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 7; i++)
			jj_la1[i] = -1;
	}

	/** Constructor. */
	public GMLParser(java.io.Reader stream) {
		jj_input_stream = new SimpleCharStream(stream, 1, 1);
		token_source = new GMLParserTokenManager(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 7; i++)
			jj_la1[i] = -1;
	}

	/** Reinitialise. */
	public void ReInit(java.io.Reader stream) {
		jj_input_stream.ReInit(stream, 1, 1);
		token_source.ReInit(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 7; i++)
			jj_la1[i] = -1;
	}

	/** Constructor with generated Token Manager. */
	public GMLParser(GMLParserTokenManager tm) {
		token_source = tm;
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 7; i++)
			jj_la1[i] = -1;
	}

	/** Reinitialise. */
	public void ReInit(GMLParserTokenManager tm) {
		token_source = tm;
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 7; i++)
			jj_la1[i] = -1;
	}

	private Token jj_consume_token(int kind) throws ParseException {
		Token oldToken;
		if ((oldToken = token).next != null)
			token = token.next;
		else
			token = token.next = token_source.getNextToken();
		jj_ntk = -1;
		if (token.kind == kind) {
			jj_gen++;
			return token;
		}
		token = oldToken;
		jj_kind = kind;
		throw generateParseException();
	}

	/** Get the next Token. */
	final public Token getNextToken() {
		if (token.next != null)
			token = token.next;
		else
			token = token.next = token_source.getNextToken();
		jj_ntk = -1;
		jj_gen++;
		return token;
	}

	/** Get the specific Token. */
	final public Token getToken(int index) {
		Token t = token;
		for (int i = 0; i < index; i++) {
			if (t.next != null)
				t = t.next;
			else
				t = t.next = token_source.getNextToken();
		}
		return t;
	}

	private int jj_ntk() {
		if ((jj_nt = token.next) == null)
			return (jj_ntk = (token.next = token_source.getNextToken()).kind);
		else
			return (jj_ntk = jj_nt.kind);
	}

	private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
	private int[] jj_expentry;
	private int jj_kind = -1;

	/** Generate ParseException. */
	public ParseException generateParseException() {
		jj_expentries.clear();
		boolean[] la1tokens = new boolean[16];
		if (jj_kind >= 0) {
			la1tokens[jj_kind] = true;
			jj_kind = -1;
		}
		for (int i = 0; i < 7; i++) {
			if (jj_la1[i] == jj_gen) {
				for (int j = 0; j < 32; j++) {
					if ((jj_la1_0[i] & (1 << j)) != 0) {
						la1tokens[j] = true;
					}
				}
			}
		}
		for (int i = 0; i < 16; i++) {
			if (la1tokens[i]) {
				jj_expentry = new int[1];
				jj_expentry[0] = i;
				jj_expentries.add(jj_expentry);
			}
		}
		int[][] exptokseq = new int[jj_expentries.size()][];
		for (int i = 0; i < jj_expentries.size(); i++) {
			exptokseq[i] = jj_expentries.get(i);
		}
		return new ParseException(token, exptokseq, tokenImage);
	}

	/** Enable tracing. */
	final public void enable_tracing() {
	}

	/** Disable tracing. */
	final public void disable_tracing() {
	}

}
