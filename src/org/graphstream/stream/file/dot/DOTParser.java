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
package org.graphstream.stream.file.dot;

import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.graphstream.stream.SourceBase.ElementType;
import org.graphstream.stream.file.FileSourceDOT;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.implementations.AbstractElement.AttributeChangeEvent;

import org.graphstream.util.parser.ParseException;
import org.graphstream.util.parser.Parser;
import org.graphstream.util.parser.SimpleCharStream;
import org.graphstream.util.parser.Token;
import org.graphstream.util.parser.TokenMgrError;

/**
 * This class defines a DOT parser.
 * 
 * It respects the specifications of the DOT language that can be found
 * <a href="http://www.graphviz.org/doc/info/lang.html">here</a>.
 * 
 * Subgraph produces no error but has no effect on the graph.
 */
@SuppressWarnings("unused")
public class DOTParser implements Parser, DOTParserConstants {
	/**
	 * The DOT source associated with this parser.
	 */
	private FileSourceDOT dot;

	/**
	 * Id of the parser used in events.
	 */
	private String sourceId;

	/**
	 * Flag telling if the graph is directed.
	 */
	private boolean directed;

	/**
	 * Flag telling if the graph is 'strict'.
	 */
	private boolean strict;

	/**
	 * Global attributes of nodes.
	 */
	private HashMap<String, Object> globalNodesAttributes;

	/**
	 * Global attributes of edges.
	 */
	private HashMap<String, Object> globalEdgesAttributes;

	/**
	 * IDs of added nodes.
	 */
	private HashSet<String> nodeAdded;

	/**
	 * Create a new parser associated with a DOT source from an input stream.
	 */
	public DOTParser(FileSourceDOT dot, InputStream stream) {
		this(stream);
		init(dot);
	}

	/**
	 * Create a new parser associated with a DOT source from a reader.
	 */
	public DOTParser(FileSourceDOT dot, Reader stream) {
		this(stream);
		init(dot);
	}

	/**
	 * Closes the parser, closing the opened stream.
	 */
	public void close() throws IOException {
		jj_input_stream.close();
	}

	private void init(FileSourceDOT dot) {
		this.dot = dot;
		this.sourceId = String.format("<DOT stream %x>", System.nanoTime());

		globalNodesAttributes = new HashMap<String, Object>();
		globalEdgesAttributes = new HashMap<String, Object>();

		nodeAdded = new HashSet<String>();
	}

	private void addNode(String nodeId, String[] port, HashMap<String, Object> attr) {
		if (nodeAdded.contains(nodeId)) {
			if (attr != null) {
				for (String key : attr.keySet())
					dot.sendAttributeChangedEvent(sourceId, nodeId, ElementType.NODE, key, AttributeChangeEvent.ADD,
							null, attr.get(key));
			}
		} else {
			dot.sendNodeAdded(sourceId, nodeId);
			nodeAdded.add(nodeId);

			if (attr == null) {
				for (String key : globalNodesAttributes.keySet())
					dot.sendAttributeChangedEvent(sourceId, nodeId, ElementType.NODE, key, AttributeChangeEvent.ADD,
							null, globalNodesAttributes.get(key));
			} else {
				for (String key : globalNodesAttributes.keySet()) {
					if (!attr.containsKey(key))
						dot.sendAttributeChangedEvent(sourceId, nodeId, ElementType.NODE, key, AttributeChangeEvent.ADD,
								null, globalNodesAttributes.get(key));
				}

				for (String key : attr.keySet())
					dot.sendAttributeChangedEvent(sourceId, nodeId, ElementType.NODE, key, AttributeChangeEvent.ADD,
							null, attr.get(key));
			}
		}
	}

	private void addEdges(LinkedList<String> edges, HashMap<String, Object> attr) {
		HashMap<String, Integer> hash = new HashMap<String, Integer>();
		String[] ids = new String[(edges.size() - 1) / 2];
		boolean[] directed = new boolean[(edges.size() - 1) / 2];
		int count = 0;

		for (int i = 0; i < edges.size() - 1; i += 2) {
			String from = edges.get(i);
			String to = edges.get(i + 2);

			if (!nodeAdded.contains(from))
				addNode(from, null, null);
			if (!nodeAdded.contains(to))
				addNode(to, null, null);

			String edgeId = String.format("(%s;%s)", from, to);
			String rev = String.format("(%s;%s)", to, from);

			if (hash.containsKey(rev)) {
				directed[hash.get(rev)] = false;
			} else {
				hash.put(edgeId, count);
				ids[count] = edgeId;
				directed[count] = edges.get(i + 1).equals("->");

				count++;
			}
		}

		hash.clear();

		if (count == 1 && attr != null && attr.containsKey("id")) {
			ids[0] = attr.get("id").toString();
			attr.remove("id");
		}

		for (int i = 0; i < count; i++) {
			boolean addedEdge = false;
			String IDtoTry = ids[i];
			while (!addedEdge) {
				try {
					dot.sendEdgeAdded(sourceId, ids[i], edges.get(i * 2), edges.get((i + 1) * 2), directed[i]);
					addedEdge = true;
				} catch (IdAlreadyInUseException e) {
					IDtoTry += "'";
				}
			}

			if (attr == null) {
				for (String key : globalEdgesAttributes.keySet())
					dot.sendAttributeChangedEvent(sourceId, ids[i], ElementType.EDGE, key, AttributeChangeEvent.ADD,
							null, globalEdgesAttributes.get(key));
			} else {
				for (String key : globalEdgesAttributes.keySet()) {
					if (!attr.containsKey(key))
						dot.sendAttributeChangedEvent(sourceId, ids[i], ElementType.EDGE, key, AttributeChangeEvent.ADD,
								null, globalEdgesAttributes.get(key));
				}

				for (String key : attr.keySet())
					dot.sendAttributeChangedEvent(sourceId, ids[i], ElementType.EDGE, key, AttributeChangeEvent.ADD,
							null, attr.get(key));
			}
		}
	}

	private void setGlobalAttributes(String who, HashMap<String, Object> attr) {
		if (who.equalsIgnoreCase("graph")) {
			for (String key : attr.keySet())
				dot.sendAttributeChangedEvent(sourceId, sourceId, ElementType.GRAPH, key, AttributeChangeEvent.ADD,
						null, attr.get(key));
		} else if (who.equalsIgnoreCase("node"))
			globalNodesAttributes.putAll(attr);
		else if (who.equalsIgnoreCase("edge"))
			globalEdgesAttributes.putAll(attr);
	}

	final public void all() throws ParseException {
		graph();
		label_1: while (true) {
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case GRAPH:
			case SUBGRAPH:
			case NODE:
			case EDGE:
			case REAL:
			case STRING:
			case WORD:
				;
				break;
			default:
				jj_la1[0] = jj_gen;
				break label_1;
			}
			statement();
		}
		jj_consume_token(RBRACE);
	}

	final public boolean next() throws ParseException {
		boolean hasMore = false;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case GRAPH:
		case SUBGRAPH:
		case NODE:
		case EDGE:
		case REAL:
		case STRING:
		case WORD:
			statement();
			hasMore = true;
			break;
		case RBRACE:
			jj_consume_token(RBRACE);
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
		graph();
	}

	final private void graph() throws ParseException {
		directed = false;
		strict = false;

		globalNodesAttributes.clear();
		globalEdgesAttributes.clear();
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case STRICT:
			jj_consume_token(STRICT);
			strict = true;
			break;
		default:
			jj_la1[2] = jj_gen;
			;
		}
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case GRAPH:
			jj_consume_token(GRAPH);
			break;
		case DIGRAPH:
			jj_consume_token(DIGRAPH);
			directed = true;
			break;
		default:
			jj_la1[3] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case REAL:
		case STRING:
		case WORD:
			this.sourceId = id();
			break;
		default:
			jj_la1[4] = jj_gen;
			;
		}
		jj_consume_token(LBRACE);
	}

	final private void subgraph() throws ParseException {
		jj_consume_token(SUBGRAPH);
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case REAL:
		case STRING:
		case WORD:
			id();
			break;
		default:
			jj_la1[5] = jj_gen;
			;
		}
		jj_consume_token(LBRACE);
		label_2: while (true) {
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case GRAPH:
			case SUBGRAPH:
			case NODE:
			case EDGE:
			case REAL:
			case STRING:
			case WORD:
				;
				break;
			default:
				jj_la1[6] = jj_gen;
				break label_2;
			}
			statement();
		}
		jj_consume_token(RBRACE);
	}

	final private String id() throws ParseException {
		Token t;
		String id;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case STRING:
			t = jj_consume_token(STRING);
			id = t.image.substring(1, t.image.length() - 1);
			break;
		case REAL:
			t = jj_consume_token(REAL);
			id = t.image;
			break;
		case WORD:
			t = jj_consume_token(WORD);
			id = t.image;
			break;
		default:
			jj_la1[7] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}

		return id;
	}

	final private void statement() throws ParseException {
		if (jj_2_1(3)) {
			edgeStatement();
		} else {
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case REAL:
			case STRING:
			case WORD:
				nodeStatement();
				break;
			case GRAPH:
			case NODE:
			case EDGE:
				attributeStatement();
				break;
			case SUBGRAPH:
				subgraph();
				break;
			default:
				jj_la1[8] = jj_gen;
				jj_consume_token(-1);
				throw new ParseException();
			}
		}
		jj_consume_token(27);
	}

	final private void nodeStatement() throws ParseException {
		String nodeId;
		String[] port;
		HashMap<String, Object> attr = null;

		port = null;
		nodeId = id();
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case COLON:
			port = port();
			break;
		default:
			jj_la1[9] = jj_gen;
			;
		}
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case LSQBR:
			attr = attributesList();
			break;
		default:
			jj_la1[10] = jj_gen;
			;
		}
		addNode(nodeId, port, attr);
	}

	final private String compassPoint() throws ParseException {
		Token pt = null;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case 28:
			pt = jj_consume_token(28);
			break;
		case 29:
			pt = jj_consume_token(29);
			break;
		case 30:
			pt = jj_consume_token(30);
			break;
		case 31:
			pt = jj_consume_token(31);
			break;
		case 32:
			pt = jj_consume_token(32);
			break;
		case 33:
			pt = jj_consume_token(33);
			break;
		case 34:
			pt = jj_consume_token(34);
			break;
		case 35:
			pt = jj_consume_token(35);
			break;
		case 36:
			pt = jj_consume_token(36);
			break;
		case 37:
			pt = jj_consume_token(37);
			break;
		default:
			jj_la1[11] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}

		return pt.image;
	}

	final private String[] port() throws ParseException {
		String[] p = { null, null };
		jj_consume_token(COLON);
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case REAL:
		case STRING:
		case WORD:
			p[0] = id();
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case COLON:
				jj_consume_token(COLON);
				p[1] = compassPoint();
				break;
			default:
				jj_la1[12] = jj_gen;
				;
			}
			break;
		case 28:
		case 29:
		case 30:
		case 31:
		case 32:
		case 33:
		case 34:
		case 35:
		case 36:
		case 37:
			p[1] = compassPoint();
			break;
		default:
			jj_la1[13] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}

		return p;
	}

	final private void edgeStatement() throws ParseException {
		String id;
		LinkedList<String> edges = new LinkedList<String>();
		HashMap<String, Object> attr = null;
		id = id();
		edges.add(id);
		edgeRHS(edges);
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case LSQBR:
			attr = attributesList();
			break;
		default:
			jj_la1[14] = jj_gen;
			;
		}
		addEdges(edges, attr);
	}

	final private void edgeRHS(LinkedList<String> edges) throws ParseException {
		Token t;
		String i;
		t = jj_consume_token(EDGE_OP);
		edges.add(t.image);
		i = id();
		edges.add(i);
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case EDGE_OP:
			edgeRHS(edges);
			break;
		default:
			jj_la1[15] = jj_gen;
			;
		}
	}

	final private void attributeStatement() throws ParseException {
		Token t;
		HashMap<String, Object> attr;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case GRAPH:
			t = jj_consume_token(GRAPH);
			break;
		case NODE:
			t = jj_consume_token(NODE);
			break;
		case EDGE:
			t = jj_consume_token(EDGE);
			break;
		default:
			jj_la1[16] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		attr = attributesList();
		setGlobalAttributes(t.image, attr);
	}

	final private HashMap<String, Object> attributesList() throws ParseException {
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		label_3: while (true) {
			jj_consume_token(LSQBR);
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case REAL:
			case STRING:
			case WORD:
				attributeList(attributes);
				label_4: while (true) {
					switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
					case COMMA:
						;
						break;
					default:
						jj_la1[17] = jj_gen;
						break label_4;
					}
					jj_consume_token(COMMA);
					attributeList(attributes);
				}
				break;
			default:
				jj_la1[18] = jj_gen;
				;
			}
			jj_consume_token(RSQBR);
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case LSQBR:
				;
				break;
			default:
				jj_la1[19] = jj_gen;
				break label_3;
			}
		}

		return attributes;
	}

	final private void attributeList(HashMap<String, Object> attributes) throws ParseException {
		String key;
		Object val;

		Token t;
		key = id();
		val = Boolean.TRUE;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case EQUALS:
			jj_consume_token(EQUALS);
			if (jj_2_2(2)) {
				t = jj_consume_token(REAL);
				val = Double.parseDouble(t.image);
			} else {
				switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
				case REAL:
				case STRING:
				case WORD:
					val = id();
					break;
				default:
					jj_la1[20] = jj_gen;
					jj_consume_token(-1);
					throw new ParseException();
				}
			}
			break;
		default:
			jj_la1[21] = jj_gen;
			;
		}
		attributes.put(key, val);
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

	private boolean jj_3R_6() {
		Token xsp;
		xsp = jj_scanpos;
		if (jj_3R_8()) {
			jj_scanpos = xsp;
			if (jj_3R_9()) {
				jj_scanpos = xsp;
				if (jj_3R_10())
					return true;
			}
		}
		return false;
	}

	private boolean jj_3_2() {
		if (jj_scan_token(REAL))
			return true;
		return false;
	}

	private boolean jj_3R_8() {
		if (jj_scan_token(STRING))
			return true;
		return false;
	}

	private boolean jj_3R_10() {
		if (jj_scan_token(WORD))
			return true;
		return false;
	}

	private boolean jj_3R_7() {
		if (jj_scan_token(EDGE_OP))
			return true;
		if (jj_3R_6())
			return true;
		return false;
	}

	private boolean jj_3R_9() {
		if (jj_scan_token(REAL))
			return true;
		return false;
	}

	private boolean jj_3R_5() {
		if (jj_3R_6())
			return true;
		if (jj_3R_7())
			return true;
		return false;
	}

	private boolean jj_3_1() {
		if (jj_3R_5())
			return true;
		return false;
	}

	/** Generated Token Manager. */
	public DOTParserTokenManager token_source;
	SimpleCharStream jj_input_stream;
	/** Current token. */
	public Token token;
	/** Next token. */
	public Token jj_nt;
	private int jj_ntk;
	private Token jj_scanpos, jj_lastpos;
	private int jj_la;
	private int jj_gen;
	final private int[] jj_la1 = new int[22];
	static private int[] jj_la1_0;
	static private int[] jj_la1_1;
	static {
		jj_la1_init_0();
		jj_la1_init_1();
	}

	private static void jj_la1_init_0() {
		jj_la1_0 = new int[] { 0x73a0000, 0x73a2001, 0x400000, 0x60000, 0x7000000, 0x7000000, 0x73a0000, 0x7000000,
				0x73a0000, 0x4000, 0x400, 0xf0000000, 0x4000, 0xf7000000, 0x400, 0x800000, 0x320000, 0x8000, 0x7000000,
				0x400, 0x7000000, 0x10000, };
	}

	private static void jj_la1_init_1() {
		jj_la1_1 = new int[] { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x3f, 0x0, 0x3f, 0x0, 0x0, 0x0,
				0x0, 0x0, 0x0, 0x0, 0x0, };
	}

	final private JJCalls[] jj_2_rtns = new JJCalls[2];
	private boolean jj_rescan = false;
	private int jj_gc = 0;

	/** Constructor with InputStream. */
	public DOTParser(java.io.InputStream stream) {
		this(stream, null);
	}

	/** Constructor with InputStream and supplied encoding */
	public DOTParser(java.io.InputStream stream, String encoding) {
		try {
			jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
		} catch (java.io.UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		token_source = new DOTParserTokenManager(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 22; i++)
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
		for (int i = 0; i < 22; i++)
			jj_la1[i] = -1;
		for (int i = 0; i < jj_2_rtns.length; i++)
			jj_2_rtns[i] = new JJCalls();
	}

	/** Constructor. */
	public DOTParser(java.io.Reader stream) {
		jj_input_stream = new SimpleCharStream(stream, 1, 1);
		token_source = new DOTParserTokenManager(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 22; i++)
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
		for (int i = 0; i < 22; i++)
			jj_la1[i] = -1;
		for (int i = 0; i < jj_2_rtns.length; i++)
			jj_2_rtns[i] = new JJCalls();
	}

	/** Constructor with generated Token Manager. */
	public DOTParser(DOTParserTokenManager tm) {
		token_source = tm;
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 22; i++)
			jj_la1[i] = -1;
		for (int i = 0; i < jj_2_rtns.length; i++)
			jj_2_rtns[i] = new JJCalls();
	}

	/** Reinitialise. */
	public void ReInit(DOTParserTokenManager tm) {
		token_source = tm;
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 22; i++)
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

	@SuppressWarnings("serial")
	static private final class LookaheadSuccess extends java.lang.Error {
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
		boolean[] la1tokens = new boolean[38];
		if (jj_kind >= 0) {
			la1tokens[jj_kind] = true;
			jj_kind = -1;
		}
		for (int i = 0; i < 22; i++) {
			if (jj_la1[i] == jj_gen) {
				for (int j = 0; j < 32; j++) {
					if ((jj_la1_0[i] & (1 << j)) != 0) {
						la1tokens[j] = true;
					}
					if ((jj_la1_1[i] & (1 << j)) != 0) {
						la1tokens[32 + j] = true;
					}
				}
			}
		}
		for (int i = 0; i < 38; i++) {
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
		for (int i = 0; i < 2; i++) {
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
