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
import java.util.HashSet;

/**
 * Reader for the "edge" graph format.
 * 
 * <p>
 * The edge graph format is a very simple and lightweight format where each line
 * describes an edge by giving two node names. The nodes are created implicitly.
 * </p>
 * 
 * <p>
 * This reader also understands the derivative format where a line contains a
 * first node name, followed by several node names separated by spaces. In this
 * case it links the first node with all other node name following on the line.
 * </p>
 * 
 * <p>
 * Also, the format does not specify any direction for edges. By default all
 * edges are undirected. You can choose to make all edges directed by passing
 * "true" as the first arguments to constructors
 * {@link #FileSourceEdge(boolean)} or {@link #FileSourceEdge(boolean, boolean)}
 * . The direction of edges goes from the first node name on each line toward
 * the second (or more) node names on each line.
 * </p>
 * 
 * <p>
 * This format only contains edges. To ensure the "add node" events are sent
 * before an edge referencing two nodes is created via an "add edge" event, this
 * reader has a hash set of already encountered nodes. The hash set allows to
 * issue "add node" events only when a node is encountered for the first time.
 * </p>
 * 
 * </p> This hash set consumes memory, but is the only way to ensure "add node"
 * events are correctly issued. If this input is directly connected to a graph,
 * as graphs can create non-existing nodes automatically, you can disable the
 * hash set of nodes using the constructor
 * {@link #FileSourceEdge(boolean, boolean)}, and giving "false" for the second
 * argument. </p>
 */
public class FileSourceEdge extends FileSourceBase {
	// Attribute

	/**
	 * Allocator for edge identifiers.
	 */
	protected int edgeid = 0;

	/**
	 * By default, consider edges as undirected.
	 */
	protected boolean directed = false;

	/**
	 * Set of existing nodes (if nodes are declared).
	 */
	protected HashSet<String> nodes;

	protected String graphName = "EDGE_";

	// Construction

	/**
	 * New reader for the "edge" format.
	 */
	public FileSourceEdge() {
		this(false);
	}

	/**
	 * New reader for the "edge" format.
	 * 
	 * @param edgesAreDirected
	 *            If true (default=false) edges are considered directed.
	 */
	public FileSourceEdge(boolean edgesAreDirected) {
		this(edgesAreDirected, true);
	}

	/**
	 * New reader for the "edge" format.
	 * 
	 * @param edgesAreDirected
	 *            If true (default=false) edges are considered directed.
	 * @param declareNodes
	 *            If true (default=true) this reader outputs nodeAdded events.
	 */
	public FileSourceEdge(boolean edgesAreDirected, boolean declareNodes) {
		directed = edgesAreDirected;
		nodes = declareNodes ? new HashSet<String>() : null;
	}

	// Commands

	@Override
	protected void continueParsingInInclude() throws IOException {
		// Should not happen, EDGE files cannot be nested.
	}

	@Override
	public boolean nextEvents() throws IOException {
		String id1 = getWordOrNumberOrStringOrEolOrEof();

		if (id1.equals("EOL")) {
			// Empty line.
		} else if (id1.equals("EOF")) {
			return false;
		} else {
			declareNode(id1);

			String id2 = getWordOrNumberOrStringOrEolOrEof();

			while (!id2.equals("EOL")) {
				if (!id1.equals(id2)) {
					String edgeId = Integer.toString(edgeid++);

					declareNode(id2);
					sendEdgeAdded(graphName, edgeId, id1, id2, directed);
				}

				id2 = getWordOrNumberOrStringOrEolOrEof();
			}
		}

		return true;
	}

	protected void declareNode(String id) {
		if (nodes != null) {
			if (!nodes.contains(id)) {
				sendNodeAdded(graphName, id);
				nodes.add(id);
			}
		}
	}

	@Override
	public void begin(String filename) throws IOException {
		super.begin(filename);
		init();
	}

	@Override
	public void begin(URL url) throws IOException {
		super.begin(url);
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

	protected void init() throws IOException {
		st.eolIsSignificant(true);
		st.commentChar('#');

		graphName = String.format("%s_%d", graphName,
				System.currentTimeMillis() + ((long) Math.random() * 10));
	}

	public boolean nextStep() throws IOException {
		return nextEvents();
	}

	@Override
	public void end() throws IOException {
		super.end();
	}
}