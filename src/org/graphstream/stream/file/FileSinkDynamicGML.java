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
 * @since 2010-07-16
 * 
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file;

/**
 * Transform the input events into a GML graph.
 * 
 * <p>
 * THIS CLASS IS REALLY NOT APPROPRIATE FOR GENERAL USE. Indeed the GML format
 * is not dynamic and it is very difficult to export the correct attributes of
 * nodes if the declaration of the attribute is far from the declaration of the
 * node. The only way would be to store the graph in a buffer and output it at
 * once when the file is closed.
 * </p>
 * 
 * <p>
 * Therefore this class outputs attributes of nodes and edges only if their
 * addition directly follows the corresponding node or edge.
 * </p>
 */
public class FileSinkDynamicGML extends FileSinkGML {
	// Construction

	public FileSinkDynamicGML() {
		// NOP
	}

	// Attribute events

	@Override
	public void graphAttributeAdded(String sourceId, long timeId, String attribute, Object value) {
		ensureToFinish();

		String val = valueToString(value);

		if (val != null) {
			out.printf("\t%s %s%n", attribute, val);
		}
	}

	@Override
	public void graphAttributeChanged(String sourceId, long timeId, String attribute, Object oldValue,
			Object newValue) {
		ensureToFinish();
		graphAttributeAdded(sourceId, timeId, attribute, newValue);
	}

	@Override
	public void graphAttributeRemoved(String sourceId, long timeId, String attribute) {
		ensureToFinish();
		out.printf("\t-%s%n", attribute);
	}

	@Override
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId, String attribute, Object value) {
		nodeAttributeChanged(sourceId, timeId, nodeId, attribute, null, value);
	}

	@Override
	public void nodeAttributeChanged(String sourceId, long timeId, String nodeId, String attribute, Object oldValue,
			Object newValue) {

		if (nodeToFinish == null || (!nodeToFinish.equals(nodeId))) {
			ensureToFinish();
			out.printf("\t+node [%n");
			out.printf("\t\tid \"%s\"%n", nodeId);
			nodeToFinish = nodeId;
		}

		if (newValue != null) {
			String val = valueToString(newValue);

			if (val != null) {
				out.printf("\t\t%s %s%n", attribute, val);
			}
		} else {
			out.printf("\t\t-%s%n", attribute);
		}
	}

	@Override
	public void nodeAttributeRemoved(String sourceId, long timeId, String nodeId, String attribute) {
		nodeAttributeChanged(sourceId, timeId, nodeId, attribute, null, null);
	}

	@Override
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId, String attribute, Object value) {
		edgeAttributeChanged(sourceId, timeId, edgeId, attribute, null, value);
	}

	@Override
	public void edgeAttributeChanged(String sourceId, long timeId, String edgeId, String attribute, Object oldValue,
			Object newValue) {

		if (edgeToFinish == null || (!edgeToFinish.equals(edgeId))) {
			ensureToFinish();
			out.printf("\t+edge [%n");
			out.printf("\t\tid \"%s\"%n", edgeId);
			edgeToFinish = edgeId;
		}

		if (newValue != null) {
			String val = valueToString(newValue);

			if (val != null) {
				out.printf("\t\t%s %s%n", attribute, val);
			}
		} else {
			out.printf("\t\t-%s%n", attribute);
		}
	}

	@Override
	public void edgeAttributeRemoved(String sourceId, long timeId, String edgeId, String attribute) {
		edgeAttributeChanged(sourceId, timeId, edgeId, attribute, null, null);
	}

	// Element events

	@Override
	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		ensureToFinish();
		out.printf("\tnode [%n");
		out.printf("\t\tid \"%s\"%n", nodeId);
		nodeToFinish = nodeId;
	}

	@Override
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		ensureToFinish();
		out.printf("\t-node \"%s\"%n", nodeId);
	}

	@Override
	public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId,
			boolean directed) {
		ensureToFinish();
		out.printf("\tedge [%n");
		out.printf("\t\tid \"%s\"%n", edgeId);
		out.printf("\t\tsource \"%s\"%n", fromNodeId);
		out.printf("\t\ttarget \"%s\"%n", toNodeId);
		out.printf("\t\tdirected %s%n", directed ? "1" : "0");
		edgeToFinish = edgeId;
	}

	@Override
	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		ensureToFinish();
		out.printf("\t-edge \"%s\"%n", edgeId);
	}

	@Override
	public void graphCleared(String sourceId, long timeId) {
		// Ah ah ah !!
	}

	@Override
	public void stepBegins(String sourceId, long timeId, double step) {
		ensureToFinish();
		if ((step - ((int) step)) == 0)
			out.printf("\tstep %d%n", (int) step);
		else
			out.printf("\tstep %f%n", step);
	}
}