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
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.regex.Pattern;

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
public class FileSinkGML extends FileSinkBase {
	// Attributes

	/** Alias on the output OutputStream. */
	protected PrintWriter out;

	protected String nodeToFinish = null;

	protected String edgeToFinish = null;

	// Construction

	public FileSinkGML() {
		// NOP
	}

	// File format events

	@Override
	protected void outputHeader() throws IOException {
		out = (PrintWriter) output;

		out.printf("graph [%n");
	}

	@Override
	protected void outputEndOfFile() throws IOException {
		ensureToFinish();
		out.printf("]%n");
	}

	// Attribute events

	public void graphAttributeAdded(String sourceId, long timeId, String attribute, Object value) {
		ensureToFinish();

		String val = valueToString(value);
		attribute = keyToString(attribute);

		if (val != null) {
			out.printf("\t%s %s%n", attribute, val);
		}
	}

	public void graphAttributeChanged(String sourceId, long timeId, String attribute, Object oldValue,
			Object newValue) {
		ensureToFinish();
		// GML is not a dynamic file format ?
	}

	public void graphAttributeRemoved(String sourceId, long timeId, String attribute) {
		ensureToFinish();
		// GML is not a dynamic file format ?
	}

	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId, String attribute, Object value) {
		if (nodeToFinish != null && nodeToFinish.equals(nodeId)) {
			String val = valueToString(value);
			attribute = keyToString(attribute);

			if (val != null) {
				out.printf("\t\t%s %s%n", attribute, val);
			}
		} else {
			ensureToFinish();
		}
	}

	public void nodeAttributeChanged(String sourceId, long timeId, String nodeId, String attribute, Object oldValue,
			Object newValue) {
		if (edgeToFinish != null)
			ensureToFinish();
		// GML is not a dynamic file format ?
	}

	public void nodeAttributeRemoved(String sourceId, long timeId, String nodeId, String attribute) {
		if (edgeToFinish != null)
			ensureToFinish();
		// GML is not a dynamic file format ?
	}

	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId, String attribute, Object value) {
		if (edgeToFinish != null && edgeToFinish.equals(edgeId)) {
			String val = valueToString(value);
			attribute = keyToString(attribute);

			if (val != null) {
				out.printf("\t\t%s %s%n", attribute, val);
			}
		} else {
			ensureToFinish();
		}
	}

	public void edgeAttributeChanged(String sourceId, long timeId, String edgeId, String attribute, Object oldValue,
			Object newValue) {
		if (nodeToFinish != null)
			ensureToFinish();
		// GML is not a dynamic file format ?
	}

	public void edgeAttributeRemoved(String sourceId, long timeId, String edgeId, String attribute) {
		if (nodeToFinish != null)
			ensureToFinish();
		// GML is not a dynamic file format ?
	}

	// Element events

	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		ensureToFinish();
		out.printf("\tnode [%n");
		out.printf("\t\tid \"%s\"%n", nodeId);
		nodeToFinish = nodeId;
	}

	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		ensureToFinish();
	}

	public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId,
			boolean directed) {
		ensureToFinish();
		out.printf("\tedge [%n");
		out.printf("\t\tid \"%s\"%n", edgeId);
		out.printf("\t\tsource \"%s\"%n", fromNodeId);
		out.printf("\t\ttarget \"%s\"%n", toNodeId);
		edgeToFinish = edgeId;
	}

	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		ensureToFinish();
	}

	public void graphCleared(String sourceId, long timeId) {
		// Ah ah ah !!
	}

	public void stepBegins(String sourceId, long timeId, double step) {
		// NOP
	}

	// Commands

	Pattern forbiddenKeyChars = Pattern.compile(".*[^a-zA-Z0-9-_.].*");

	protected String keyToString(String key) {
		if (forbiddenKeyChars.matcher(key).matches())
			return "\"" + key.replace("\"", "\\\"") + "\"";

		return key;
	}

	protected String valueToString(Object value) {
		if (value == null)
			return null;

		if (value instanceof Number) {
			double val = ((Number) value).doubleValue();
			if ((val - ((int) val)) == 0)
				return String.format(Locale.US, "%d", (int) val);
			else
				return String.format(Locale.US, "%f", val);
		}

		return String.format("\"%s\"", value.toString().replaceAll("\n|\r|\"", " "));
	}

	protected void ensureToFinish() {
		assert ((nodeToFinish != null && edgeToFinish == null) || (nodeToFinish == null && edgeToFinish != null)
				|| (nodeToFinish == null && edgeToFinish == null));

		if (nodeToFinish != null || edgeToFinish != null) {
			out.printf("\t]%n");
			nodeToFinish = null;
			edgeToFinish = null;
		}
	}
}