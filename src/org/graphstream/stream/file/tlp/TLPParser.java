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
 * @since 2011-07-21
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file.tlp;

import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

import org.graphstream.stream.SourceBase.ElementType;
import org.graphstream.stream.file.FileSourceTLP;
import org.graphstream.graph.implementations.AbstractElement.AttributeChangeEvent;

import org.graphstream.util.parser.ParseException;
import org.graphstream.util.parser.Parser;
import org.graphstream.util.parser.SimpleCharStream;
import org.graphstream.util.parser.Token;
import org.graphstream.util.parser.TokenMgrError;

/**
 * This class defines a TLP parser.
 */
@SuppressWarnings("unused")
public class TLPParser implements Parser, TLPParserConstants {

	protected static enum PropertyType {
		BOOL, COLOR, DOUBLE, LAYOUT, INT, SIZE, STRING
	}

	protected static class Cluster {
		int index;
		String name;

		LinkedList<String> nodes;
		LinkedList<String> edges;

		Cluster(int index, String name) {
			this.index = index;
			this.name = name;
			this.nodes = new LinkedList<String>();
			this.edges = new LinkedList<String>();
		}
	}

	/**
	 * The DOT source associated with this parser.
	 */
	private FileSourceTLP tlp;

	/**
	 * Id of the parser used in events.
	 */
	private String sourceId;

	private Cluster root;
	private HashMap<Integer, Cluster> clusters;
	private Stack<Cluster> stack;

	/**
	 * Create a new parser associated with a TLP source from an input stream.
	 */
	public TLPParser(FileSourceTLP tlp, InputStream stream) {
		this(stream);
		init(tlp);
	}

	/**
	 * Create a new parser associated with a DOT source from a reader.
	 */
	public TLPParser(FileSourceTLP tlp, Reader stream) {
		this(stream);
		init(tlp);
	}

	/**
	 * Closes the parser, closing the opened stream.
	 */
	public void close() throws IOException {
		jj_input_stream.close();
		clusters.clear();
	}

	private void init(FileSourceTLP tlp) {
		this.tlp = tlp;
		this.sourceId = String.format("<DOT stream %x>", System.nanoTime());

		this.clusters = new HashMap<Integer, Cluster>();
		this.stack = new Stack<Cluster>();

		this.root = new Cluster(0, "<root>");
		this.clusters.put(0, this.root);
		this.stack.push(this.root);
	}

	private void addNode(String id) throws ParseException {
		if (stack.size() > 1 && (!root.nodes.contains(id) || !stack.get(stack.size() - 2).nodes.contains(id)))
			throw new ParseException("parent cluster do not contain the node");

		if (stack.size() == 1)
			tlp.sendNodeAdded(sourceId, id);

		stack.peek().nodes.add(id);
	}

	private void addEdge(String id, String source, String target) throws ParseException {
		if (stack.size() > 1 && (!root.edges.contains(id) || !stack.get(stack.size() - 2).edges.contains(id)))
			throw new ParseException("parent cluster " + stack.get(stack.size() - 2).name + " do not contain the edge");

		if (stack.size() == 1)
			tlp.sendEdgeAdded(sourceId, id, source, target, false);

		stack.peek().edges.add(id);
	}

	private void includeEdge(String id) throws ParseException {
		if (stack.size() > 1 && (!root.edges.contains(id) || !stack.get(stack.size() - 2).edges.contains(id)))
			throw new ParseException("parent cluster " + stack.get(stack.size() - 2).name + " do not contain the edge");

		stack.peek().edges.add(id);
	}

	private void graphAttribute(String key, Object value) {
		tlp.sendAttributeChangedEvent(sourceId, sourceId, ElementType.GRAPH, key, AttributeChangeEvent.ADD, null,
				value);
	}

	private void pushCluster(int i, String name) {
		Cluster c = new Cluster(i, name);
		clusters.put(i, c);
		stack.push(c);
	}

	private void popCluster() {
		if (stack.size() > 1)
			stack.pop();
	}

	private void newProperty(Integer cluster, String name, PropertyType type, String nodeDefault, String edgeDefault,
			HashMap<String, String> nodes, HashMap<String, String> edges) {
		Object nodeDefaultValue = convert(type, nodeDefault);
		Object edgeDefaultValue = convert(type, edgeDefault);
		Cluster c = clusters.get(cluster);

		for (String id : c.nodes) {
			Object value = nodeDefaultValue;

			if (nodes.containsKey(id))
				value = convert(type, nodes.get(id));

			tlp.sendAttributeChangedEvent(sourceId, id, ElementType.NODE, name, AttributeChangeEvent.ADD, null, value);
		}

		for (String id : c.edges) {
			Object value = edgeDefaultValue;

			if (edges.containsKey(id))
				value = convert(type, edges.get(id));

			tlp.sendAttributeChangedEvent(sourceId, id, ElementType.EDGE, name, AttributeChangeEvent.ADD, null, value);
		}
	}

	private Object convert(PropertyType type, String value) {
		switch (type) {
		case BOOL:
			return Boolean.valueOf(value);
		case INT:
			return Integer.valueOf(value);
		case DOUBLE:
			return Double.valueOf(value);
		case LAYOUT:
		case COLOR:
		case SIZE:
		case STRING:
			return value;
		}

		return value;
	}

	final public void all() throws ParseException {
		tlp();
		label_1: while (true) {
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case OBRACKET:
				;
				break;
			default:
				jj_la1[0] = jj_gen;
				break label_1;
			}
			statement();
		}
		jj_consume_token(CBRACKET);
		jj_consume_token(0);
	}

	final public boolean next() throws ParseException {
		boolean hasMore = false;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case OBRACKET:
			statement();
			hasMore = true;
			break;
		case 0:
			jj_consume_token(0);
			break;
		default:
			jj_la1[1] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}

		return hasMore;
	}

	final public void open() throws ParseException {
		tlp();
	}

	final private void tlp() throws ParseException {
		jj_consume_token(OBRACKET);
		jj_consume_token(TLP);
		jj_consume_token(STRING);
		label_2: while (true) {
			if (jj_2_1(2)) {
				;
			} else {
				break label_2;
			}
			headers();
		}
	}

	final private void headers() throws ParseException {
		String s;
		jj_consume_token(OBRACKET);
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case DATE:
			jj_consume_token(DATE);
			s = string();
			graphAttribute("date", s);
			break;
		case AUTHOR:
			jj_consume_token(AUTHOR);
			s = string();
			graphAttribute("author", s);
			break;
		case COMMENTS:
			jj_consume_token(COMMENTS);
			s = string();
			graphAttribute("comments", s);
			break;
		default:
			jj_la1[2] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		jj_consume_token(CBRACKET);
	}

	final private void statement() throws ParseException {
		if (jj_2_2(2)) {
			nodes();
		} else if (jj_2_3(2)) {
			edge();
		} else if (jj_2_4(2)) {
			cluster();
		} else if (jj_2_5(2)) {
			property();
		} else {
			jj_consume_token(-1);
			throw new ParseException();
		}
	}

	final private void nodes() throws ParseException {
		Token i;
		jj_consume_token(OBRACKET);
		jj_consume_token(NODES);
		label_3: while (true) {
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case INTEGER:
				;
				break;
			default:
				jj_la1[3] = jj_gen;
				break label_3;
			}
			i = jj_consume_token(INTEGER);
			addNode(i.image);
		}
		jj_consume_token(CBRACKET);
	}

	final private void edge() throws ParseException {
		Token i, s, t;
		jj_consume_token(OBRACKET);
		jj_consume_token(EDGE);
		i = jj_consume_token(INTEGER);
		s = jj_consume_token(INTEGER);
		t = jj_consume_token(INTEGER);
		jj_consume_token(CBRACKET);
		addEdge(i.image, s.image, t.image);
	}

	final private void edges() throws ParseException {
		Token i;
		jj_consume_token(OBRACKET);
		jj_consume_token(EDGES);
		label_4: while (true) {
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case INTEGER:
				;
				break;
			default:
				jj_la1[4] = jj_gen;
				break label_4;
			}
			i = jj_consume_token(INTEGER);
			includeEdge(i.image);
		}
		jj_consume_token(CBRACKET);
	}

	final private void cluster() throws ParseException {
		Token index;
		String name;
		jj_consume_token(OBRACKET);
		jj_consume_token(CLUSTER);
		index = jj_consume_token(INTEGER);
		name = string();
		pushCluster(Integer.valueOf(index.image), name);
		nodes();
		edges();
		label_5: while (true) {
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case OBRACKET:
				;
				break;
			default:
				jj_la1[5] = jj_gen;
				break label_5;
			}
			cluster();
		}
		jj_consume_token(CBRACKET);
		popCluster();
	}

	final private void property() throws ParseException {
		PropertyType type;
		Integer cluster;
		String name;
		String nodeDefault, edgeDefault;
		String value;
		Token t;

		HashMap<String, String> nodes = new HashMap<String, String>();
		HashMap<String, String> edges = new HashMap<String, String>();
		jj_consume_token(OBRACKET);
		jj_consume_token(PROPERTY);
		cluster = integer();
		type = type();
		name = string();
		jj_consume_token(OBRACKET);
		jj_consume_token(DEF);
		nodeDefault = string();
		edgeDefault = string();
		jj_consume_token(CBRACKET);
		label_6: while (true) {
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case OBRACKET:
				;
				break;
			default:
				jj_la1[6] = jj_gen;
				break label_6;
			}
			jj_consume_token(OBRACKET);
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case NODE:
				jj_consume_token(NODE);
				t = jj_consume_token(INTEGER);
				value = string();
				nodes.put(t.image, value);
				break;
			case EDGE:
				jj_consume_token(EDGE);
				t = jj_consume_token(INTEGER);
				value = string();
				edges.put(t.image, value);
				break;
			default:
				jj_la1[7] = jj_gen;
				jj_consume_token(-1);
				throw new ParseException();
			}
			jj_consume_token(CBRACKET);
		}
		jj_consume_token(CBRACKET);
		newProperty(cluster, name, type, nodeDefault, edgeDefault, nodes, edges);
	}

	final private PropertyType type() throws ParseException {
		Token t;
		t = jj_consume_token(PTYPE);

		return PropertyType.valueOf(t.image.toUpperCase());
	}

	final private String string() throws ParseException {
		Token t;
		t = jj_consume_token(STRING);

		return t.image.substring(1, t.image.length() - 1);
	}

	final private Integer integer() throws ParseException {
		Token t;
		t = jj_consume_token(INTEGER);

		return Integer.valueOf(t.image);
	}

	private boolean jj_2_1(int xla) {
		jj_la = xla;
		jj_lastpos = jj_scanpos = token;
		try {
			return !jj_3_1();
		} catch (LookaheadSuccess ls) {
			return true;
		} finally {
			jj_save(0, xla);
		}
	}

	private boolean jj_2_2(int xla) {
		jj_la = xla;
		jj_lastpos = jj_scanpos = token;
		try {
			return !jj_3_2();
		} catch (LookaheadSuccess ls) {
			return true;
		} finally {
			jj_save(1, xla);
		}
	}

	private boolean jj_2_3(int xla) {
		jj_la = xla;
		jj_lastpos = jj_scanpos = token;
		try {
			return !jj_3_3();
		} catch (LookaheadSuccess ls) {
			return true;
		} finally {
			jj_save(2, xla);
		}
	}

	private boolean jj_2_4(int xla) {
		jj_la = xla;
		jj_lastpos = jj_scanpos = token;
		try {
			return !jj_3_4();
		} catch (LookaheadSuccess ls) {
			return true;
		} finally {
			jj_save(3, xla);
		}
	}

	private boolean jj_2_5(int xla) {
		jj_la = xla;
		jj_lastpos = jj_scanpos = token;
		try {
			return !jj_3_5();
		} catch (LookaheadSuccess ls) {
			return true;
		} finally {
			jj_save(4, xla);
		}
	}

	private boolean jj_3_1() {
		if (jj_3R_7())
			return true;
		return false;
	}

	private boolean jj_3_5() {
		if (jj_3R_11())
			return true;
		return false;
	}

	private boolean jj_3R_9() {
		if (jj_scan_token(OBRACKET))
			return true;
		if (jj_scan_token(EDGE))
			return true;
		return false;
	}

	private boolean jj_3_4() {
		if (jj_3R_10())
			return true;
		return false;
	}

	private boolean jj_3_3() {
		if (jj_3R_9())
			return true;
		return false;
	}

	private boolean jj_3R_14() {
		if (jj_scan_token(COMMENTS))
			return true;
		return false;
	}

	private boolean jj_3R_13() {
		if (jj_scan_token(AUTHOR))
			return true;
		return false;
	}

	private boolean jj_3_2() {
		if (jj_3R_8())
			return true;
		return false;
	}

	private boolean jj_3R_12() {
		if (jj_scan_token(DATE))
			return true;
		return false;
	}

	private boolean jj_3R_11() {
		if (jj_scan_token(OBRACKET))
			return true;
		if (jj_scan_token(PROPERTY))
			return true;
		return false;
	}

	private boolean jj_3R_10() {
		if (jj_scan_token(OBRACKET))
			return true;
		if (jj_scan_token(CLUSTER))
			return true;
		return false;
	}

	private boolean jj_3R_7() {
		if (jj_scan_token(OBRACKET))
			return true;
		Token xsp;
		xsp = jj_scanpos;
		if (jj_3R_12()) {
			jj_scanpos = xsp;
			if (jj_3R_13()) {
				jj_scanpos = xsp;
				if (jj_3R_14())
					return true;
			}
		}
		return false;
	}

	private boolean jj_3R_8() {
		if (jj_scan_token(OBRACKET))
			return true;
		if (jj_scan_token(NODES))
			return true;
		return false;
	}

	/** Generated Token Manager. */
	public TLPParserTokenManager token_source;
	SimpleCharStream jj_input_stream;
	/** Current token. */
	public Token token;
	/** Next token. */
	public Token jj_nt;
	private int jj_ntk;
	private Token jj_scanpos, jj_lastpos;
	private int jj_la;
	private int jj_gen;
	final private int[] jj_la1 = new int[8];
	static private int[] jj_la1_0;
	static {
		jj_la1_init_0();
	}

	private static void jj_la1_init_0() {
		jj_la1_0 = new int[] { 0x400, 0x401, 0x380000, 0x1000000, 0x1000000, 0x400, 0x400, 0x14000, };
	}

	final private JJCalls[] jj_2_rtns = new JJCalls[5];
	private boolean jj_rescan = false;
	private int jj_gc = 0;

	/** Constructor with InputStream. */
	public TLPParser(java.io.InputStream stream) {
		this(stream, null);
	}

	/** Constructor with InputStream and supplied encoding */
	public TLPParser(java.io.InputStream stream, String encoding) {
		try {
			jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
		} catch (java.io.UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		token_source = new TLPParserTokenManager(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 8; i++)
			jj_la1[i] = -1;
		for (int i = 0; i < jj_2_rtns.length; i++)
			jj_2_rtns[i] = new JJCalls();
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
		for (int i = 0; i < 8; i++)
			jj_la1[i] = -1;
		for (int i = 0; i < jj_2_rtns.length; i++)
			jj_2_rtns[i] = new JJCalls();
	}

	/** Constructor. */
	public TLPParser(java.io.Reader stream) {
		jj_input_stream = new SimpleCharStream(stream, 1, 1);
		token_source = new TLPParserTokenManager(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 8; i++)
			jj_la1[i] = -1;
		for (int i = 0; i < jj_2_rtns.length; i++)
			jj_2_rtns[i] = new JJCalls();
	}

	/** Reinitialise. */
	public void ReInit(java.io.Reader stream) {
		jj_input_stream.ReInit(stream, 1, 1);
		token_source.ReInit(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 8; i++)
			jj_la1[i] = -1;
		for (int i = 0; i < jj_2_rtns.length; i++)
			jj_2_rtns[i] = new JJCalls();
	}

	/** Constructor with generated Token Manager. */
	public TLPParser(TLPParserTokenManager tm) {
		token_source = tm;
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 8; i++)
			jj_la1[i] = -1;
		for (int i = 0; i < jj_2_rtns.length; i++)
			jj_2_rtns[i] = new JJCalls();
	}

	/** Reinitialise. */
	public void ReInit(TLPParserTokenManager tm) {
		token_source = tm;
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 8; i++)
			jj_la1[i] = -1;
		for (int i = 0; i < jj_2_rtns.length; i++)
			jj_2_rtns[i] = new JJCalls();
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
			if (++jj_gc > 100) {
				jj_gc = 0;
				for (int i = 0; i < jj_2_rtns.length; i++) {
					JJCalls c = jj_2_rtns[i];
					while (c != null) {
						if (c.gen < jj_gen)
							c.first = null;
						c = c.next;
					}
				}
			}
			return token;
		}
		token = oldToken;
		jj_kind = kind;
		throw generateParseException();
	}

	static private final class LookaheadSuccess extends java.lang.Error {
		private static final long serialVersionUID = -7986896058452164869L;
	}

	final private LookaheadSuccess jj_ls = new LookaheadSuccess();

	private boolean jj_scan_token(int kind) {
		if (jj_scanpos == jj_lastpos) {
			jj_la--;
			if (jj_scanpos.next == null) {
				jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
			} else {
				jj_lastpos = jj_scanpos = jj_scanpos.next;
			}
		} else {
			jj_scanpos = jj_scanpos.next;
		}
		if (jj_rescan) {
			int i = 0;
			Token tok = token;
			while (tok != null && tok != jj_scanpos) {
				i++;
				tok = tok.next;
			}
			if (tok != null)
				jj_add_error_token(kind, i);
		}
		if (jj_scanpos.kind != kind)
			return true;
		if (jj_la == 0 && jj_scanpos == jj_lastpos)
			throw jj_ls;
		return false;
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
	private int[] jj_lasttokens = new int[100];
	private int jj_endpos;

	private void jj_add_error_token(int kind, int pos) {
		if (pos >= 100)
			return;
		if (pos == jj_endpos + 1) {
			jj_lasttokens[jj_endpos++] = kind;
		} else if (jj_endpos != 0) {
			jj_expentry = new int[jj_endpos];
			for (int i = 0; i < jj_endpos; i++) {
				jj_expentry[i] = jj_lasttokens[i];
			}
			jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
				int[] oldentry = (int[]) (it.next());
				if (oldentry.length == jj_expentry.length) {
					for (int i = 0; i < jj_expentry.length; i++) {
						if (oldentry[i] != jj_expentry[i]) {
							continue jj_entries_loop;
						}
					}
					jj_expentries.add(jj_expentry);
					break jj_entries_loop;
				}
			}
			if (pos != 0)
				jj_lasttokens[(jj_endpos = pos) - 1] = kind;
		}
	}

	/** Generate ParseException. */
	public ParseException generateParseException() {
		jj_expentries.clear();
		boolean[] la1tokens = new boolean[28];
		if (jj_kind >= 0) {
			la1tokens[jj_kind] = true;
			jj_kind = -1;
		}
		for (int i = 0; i < 8; i++) {
			if (jj_la1[i] == jj_gen) {
				for (int j = 0; j < 32; j++) {
					if ((jj_la1_0[i] & (1 << j)) != 0) {
						la1tokens[j] = true;
					}
				}
			}
		}
		for (int i = 0; i < 28; i++) {
			if (la1tokens[i]) {
				jj_expentry = new int[1];
				jj_expentry[0] = i;
				jj_expentries.add(jj_expentry);
			}
		}
		jj_endpos = 0;
		jj_rescan_token();
		jj_add_error_token(0, 0);
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

	private void jj_rescan_token() {
		jj_rescan = true;
		for (int i = 0; i < 5; i++) {
			try {
				JJCalls p = jj_2_rtns[i];
				do {
					if (p.gen > jj_gen) {
						jj_la = p.arg;
						jj_lastpos = jj_scanpos = p.first;
						switch (i) {
						case 0:
							jj_3_1();
							break;
						case 1:
							jj_3_2();
							break;
						case 2:
							jj_3_3();
							break;
						case 3:
							jj_3_4();
							break;
						case 4:
							jj_3_5();
							break;
						}
					}
					p = p.next;
				} while (p != null);
			} catch (LookaheadSuccess ls) {
			}
		}
		jj_rescan = false;
	}

	private void jj_save(int index, int xla) {
		JJCalls p = jj_2_rtns[index];
		while (p.gen > jj_gen) {
			if (p.next == null) {
				p = p.next = new JJCalls();
				break;
			}
			p = p.next;
		}
		p.gen = jj_gen + xla - jj_la;
		p.first = token;
		p.arg = xla;
	}

	static final class JJCalls {
		int gen;
		Token first;
		int arg;
		JJCalls next;
	}

}
