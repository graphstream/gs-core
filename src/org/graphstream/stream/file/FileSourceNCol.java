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
 * @since 2009-05-07
 * 
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.HashSet;

/**
 * Reader for the "ncol" graph format.
 * 
 * <p>
 * The ncol graph format is a simple format where each line describes an edge by
 * giving two node names and an optional third parameters giving the edge
 * weight. The nodes are created implicitly.
 * </p>
 * 
 * <p>
 * Also, the format does not specify any direction for edges. By default all
 * edges are undirected. It is specified in the format that you will never have
 * directed edges and that the lines:
 * 
 * <pre>
 *     node1Name node2Name
 * </pre>
 * 
 * and
 * 
 * <pre>
 *     node2Name node1Name
 * </pre>
 * 
 * Cannot both appear at the same time in a file.
 * </p>
 * 
 * <p>
 * This format only contains edges. To ensure the "add node" events are sent
 * before an edge referencing two nodes is created via an "add edge" event, this
 * reader has a hash set of already encountered nodes. The hash set allows to
 * issue "add node" events only when a node is encountered for the first time.
 * </p>
 * 
 * </p>
 * This hash set consumes memory, but is the only way to ensure "add node"
 * events are correctly issued. If this input is directly connected to a graph,
 * as graphs can create non-existing nodes automatically, you can disable the
 * hash set of nodes using the constructor {@link #FileSourceNCol(boolean)}, and
 * giving "false" for the first argument.
 * </p>
 * 
 * The usual file name extension for this format is ".ncol".
 */
public class FileSourceNCol extends FileSourceBase {
	// Attribute

	/**
	 * Allocator for edge identifiers.
	 */
	protected int edgeid = 0;

	/**
	 * Set of existing nodes (if nodes are declared).
	 */
	protected HashSet<String> nodes;

	protected String graphName = "NCOL_";

	// Construction

	/**
	 * New reader for the "ncol" format.
	 */
	public FileSourceNCol() {
		this(false);
	}

	/**
	 * New reader for the "ncol" format.
	 * 
	 * @param declareNodes
	 *            If true (default=true) this reader outputs nodeAdded events.
	 */
	public FileSourceNCol(boolean declareNodes) {
		nodes = declareNodes ? new HashSet<String>() : null;
	}

	// Commands

	@Override
	protected void continueParsingInInclude() throws IOException {
		// Should not happen, NCol files cannot be nested.
	}

	@Override
	public boolean nextEvents() throws IOException {
		String id1 = getWordOrNumberOrStringOrEolOrEof();

		if (id1.equals("EOL")) {
			// Empty line. Skip it.
		} else if (id1.equals("EOF")) {
			return false;
		} else {
			declareNode(id1);

			String id2 = getWordOrNumberOrStringOrEolOrEof();

			if (!id2.equals("EOL") && !id2.equals("EOF")) {
				// Loops are not accepted by the format.
				if (!id1.equals(id2)) {
					// There may be a weight.
					String weight = getWordOrNumberOrStringOrEolOrEof();
					double w = 0.0;

					if (weight.equals("EOL") || weight.equals("EOF")) {
						weight = null;
						pushBack();
					} else {
						try {
							w = Double.parseDouble(weight);
						} catch (Exception e) {
							throw new IOException(String.format("cannot transform weight %s into a number", weight));
						}
					}

					String edgeId = Integer.toString(edgeid++);

					declareNode(id2);
					sendEdgeAdded(graphName, edgeId, id1, id2, false);

					if (weight != null)
						sendEdgeAttributeAdded(graphName, edgeId, "weight", (Double) w);
				}
			} else {
				throw new IOException("unexpected EOL or EOF");
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

		graphName = String.format("%s_%d", graphName, System.currentTimeMillis() + ((long) Math.random() * 10));
	}

	public boolean nextStep() throws IOException {
		return nextEvents();
	}

	@Override
	public void end() throws IOException {
		super.end();
	}
}