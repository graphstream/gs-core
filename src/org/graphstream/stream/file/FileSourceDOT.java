/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * Copyright 2006 - 2010
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.stream.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Graph reader for GraphViz "dot" files.
 * 
 * <p>
 * In this format, edges have no identifier. By default an automatic identifier
 * is added to each edge. You can add an identifier to an edge by adding an "id"
 * attribute to the edge. For example :
 * 
 * <pre>
 *    A -- B [ id=AB ];
 * </pre>
 * 
 * </p>
 */
public class FileSourceDOT extends FileSourceBase {
	// Attribute

	/**
	 * Is the graph directed.
	 */
	protected boolean directed = false;

	/**
	 * Hash map used everywhere to transmit attributes. Avoids to create one for
	 * every node or edge attribute.
	 */
	protected HashMap<String, Object> attributes = new HashMap<String, Object>();

	/**
	 * Hash map of general attributes specified for all nodes.
	 */
	protected HashMap<String, Object> nodesAttributes = new HashMap<String, Object>();

	/**
	 * Hash map of general attributes specified for all edges.
	 */
	protected HashMap<String, Object> edgesAttributes = new HashMap<String, Object>();

	/**
	 * Allocate for edge identifiers.
	 */
	protected int edgeId = 1;

	/**
	 * Set that allow to now which nodes have already been declared and which
	 * node has not yet been declared. This allows to avoid the implicit
	 * declaration of nodes and send the correct set of attributes
	 */
	protected HashSet<String> nodes = new HashSet<String>();

	/**
	 * Name of the graph (set as soon as known, if known).
	 */
	protected String graphName = "DOT_";

	// Construction

	public FileSourceDOT() {
		super(false, '#', '"');
	}

	// Access

	// Commands

	@Override
	protected void continueParsingInInclude() throws IOException {
		// Should not happen, DOT files cannot be nested.
	}

	@Override
	public boolean nextEvents() throws IOException {
		String w;
		boolean remains = true;

		w = getWordOrSymbolOrNumberOrStringOrEolOrEof();

		if (w.equals("node")) {
			// Store general nodes attributes.

			parseAttributeBlock("node", nodesAttributes);
			eatSymbolOrPushback(';');
		} else if (w.equals("edge")) {
			// Store general edges attributes.

			parseAttributeBlock("edge", edgesAttributes);
			eatSymbolOrPushback(';');
		} else if (w.equals("graph")) {
			// Graph attributes.

			attributes.clear();
			parseAttributeBlock("graph", attributes);
			eatSymbolOrPushback(';');

			for (String key : attributes.keySet()) {
				sendGraphAttributeAdded(graphName, key, attributes.get(key));
			}
		} else if (w.equals("}")) {
			// End of the graph.

			remains = false;
		} else if (w.equals("EOF")) {
			parseError("expecting '}' here, got EOF");
		} else if (w.equals("EOL")) {
			parseError("should not get EOL");
		} else {
			// Parse an edge or node specification.

			parseId(w);
			eatSymbolOrPushback(';');
		}

		return remains;
	}

	@Override
	public void begin(String fileName) throws IOException {
		super.begin(fileName);
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
		st.slashSlashComments(true);
		st.slashStarComments(true);

		readHeader();
	}

	@Override
	public void end() throws IOException {
		eatEof();
		super.end();
	}

	public boolean nextStep() throws IOException {
		return nextEvents();
	}

	// Commands

	/**
	 * Read the header of a DOT file.
	 */
	protected void readHeader() throws IOException {
		String w = getWord();

		if (w.equals("strict"))
			w = getWord();

		if (w.equals("graph"))
			directed = false;
		else if (w.equals("digraph"))
			directed = true;
		else
			parseError("waiting 'graph' or 'digraph' here");

		w = getWordOrSymbolOrString();

		if (!w.equals("{")) {
			graphName = w;

			attributes.clear();
			attributes.put("label", graphName);

			sendGraphAttributeAdded(graphName, "label", graphName);

			w = getWordOrSymbol();
		}

		graphName = String.format("%s_%d", graphName,
				System.currentTimeMillis() + ((long) Math.random() * 10));

		if (!w.equals("{"))
			parseError("waiting '{' here");
	}

	/**
	 * Parse an attribute block for the given element. The element can be
	 * "node", "edge" or "graph". This means that all subsequent nodes, edge or
	 * graphs will have these attributes. The attributes are accumulated in the
	 * {@link #attributes} variable.
	 */
	protected void parseAttributeBlock(String element,
			HashMap<String, Object> attributes) throws IOException {
		if ((!element.equals("node")) && (!element.equals("edge"))
				&& (!element.equals("graph")))
			parseError("attribute list for an unknown element type '" + element
					+ "'");

		boolean loop = true;
		String id, sw;
		Object w;
		char s;

		eatSymbol('[');

		while (loop) {
			id = getWordOrSymbolOrString();

			if (id.equals("]")) {
				// Case of an empty block "[]"...

				loop = false;
			} else {
				// An attribute ID was read, is there a value?

				sw = getWordOrSymbol();
				w = null;

				if (sw.equals("="))
					w = getStringOrWordOrNumberO();// getAllExceptedEof();
				else
					pushBack();

				// System.err.println("What to do with attribute '"+id+"'='"+w+"'??");

				putAttribute(id, w, attributes);

				// Now look what come next, a ',' or a ']'?

				s = getSymbolOrPushback();

				if (s == ']') {
					loop = false;
				} else if (s != ',') {
					parseError("expecting ',' or ']' here");
				}
			}
		}
	}

	/**
	 * Like {@link #parseAttributeBlock(String, HashMap)} but only if a '[' is
	 * found else do nothing.
	 */
	protected void maybeParseAttributeBlock(String element,
			HashMap<String, Object> attributes) throws IOException {
		char s;

		s = getSymbolOrPushback();

		if (s == '[') {
			pushBack();
			parseAttributeBlock(element, attributes);
		} else {
			if (s != 0)
				pushBack();
		}
	}

	/**
	 * Parse a node, edge or single attribute.
	 */
	protected void parseId(String id) throws IOException {
		String w;

		if (id.equals("subgraph")) {
			parseError("subgraphs are not supported yet with the DOT import");
			// SG = parseSubgraph();
			// id = SG.get_id();
		}

		w = getWordOrSymbol();

		if (w.equals("=")) {
			// Read a single attribute.

			// if( SG != null )
			// parseError( "cannot assign to a subgraph" );

			String val = getWordOrString();

			sendGraphAttributeAdded(graphName, id, val);
		} else if (w.startsWith("-")) {
			// Read an edge.

			char symbol = getSymbol();
			boolean directed = false;

			if (symbol == '>')
				directed = true;
			else if (symbol == '-')
				directed = false;
			else
				parseError("expecting '>' or '-', got '" + symbol + "'");

			EdgeRepr e = parseEdge(id);
			this.attributes.clear();
			this.attributes.putAll(edgesAttributes);
			maybeParseAttributeBlock("edge", this.attributes);

			Object edgeId = this.attributes.get("id");

			if (edgeId != null && edgeId instanceof String)
				e.id = edgeId.toString();

			if (!nodes.contains(e.from))
				declareNode(e.from, nodesAttributes);

			if (!nodes.contains(e.to))
				declareNode(e.to, nodesAttributes);

			sendEdgeAdded(graphName, e.id, e.from, e.to, directed);

			if (this.attributes != null) {
				for (String key : this.attributes.keySet()) {
					Object value = this.attributes.get(key);
					sendEdgeAttributeAdded(graphName, e.id, key, value);
				}
			}
		} else {
			// Read a node

			// if( SG != null )
			// {
			// }
			// else
			{
				pushBack();
				this.attributes.clear();
				this.attributes.putAll(nodesAttributes);
				maybeParseAttributeBlock("node", this.attributes);
				declareNode(id, this.attributes);
			}
		}
	}

	protected void declareNode(String id, HashMap<String, Object> attributes)
			throws IOException {
		nodes.add(id);

		sendNodeAdded(graphName, id);

		if (this.attributes != null) {
			for (String key : this.attributes.keySet()) {
				Object value = this.attributes.get(key);
				sendNodeAttributeAdded(graphName, id, key, value);
			}
		}
	}

	protected EdgeRepr parseEdge(String node0Id) throws IOException {
		String node1Id;

		node1Id = getStringOrWordOrNumber();

		if (node1Id.equals("subgraph")) {
			parseError("Subgraphs are not yet handled by this DOT import");
			return null;
			// SG = parse_subgraph();
			// node1_id = SG.get_id();
		} else {
			String w = getWordOrSymbolOrPushback();

			if (w != null) {
				if (w.startsWith("-")) {
					// Chain of edges.

					eatSymbols(">-");
					parseEdge(node1Id);
				} else {
					pushBack();
				}
			}

			return new EdgeRepr(Integer.toString(edgeId++), node0Id, node1Id);
		}
	}

	protected void putAttribute(String id, Object value,
			HashMap<String, Object> attributes) {
		attributes.put(id, value);
	}

	protected class EdgeRepr {
		public String id;
		public String from;
		public String to;

		public EdgeRepr(String id, String from, String to) {
			this.id = id;
			this.from = from;
			this.to = to;
		}
	}
}