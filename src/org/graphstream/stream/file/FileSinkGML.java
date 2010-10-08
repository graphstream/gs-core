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
import java.io.PrintStream;

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
	protected PrintStream out;

	protected String nodeToFinish = null;

	protected String edgeToFinish = null;

	// Construction

	public FileSinkGML() {
		// NOP
	}

	// File format events

	@Override
	protected void outputHeader() throws IOException {
		out = (PrintStream) output;

		out.printf("graph [%n");
	}

	@Override
	protected void outputEndOfFile() throws IOException {
		ensureToFinish();
		out.printf("]%n");
	}

	// Attribute events

	public void graphAttributeAdded(String sourceId, long timeId,
			String attribute, Object value) {
		ensureToFinish();

		String val = valueToString(value);

		if (val != null) {
			out.printf("\t%s %s%n", attribute, val);
		}
	}

	public void graphAttributeChanged(String sourceId, long timeId,
			String attribute, Object oldValue, Object newValue) {
		ensureToFinish();
		// GML is not a dynamic file format ?
	}

	public void graphAttributeRemoved(String sourceId, long timeId,
			String attribute) {
		ensureToFinish();
		// GML is not a dynamic file format ?
	}

	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId,
			String attribute, Object value) {
		if (nodeToFinish != null && nodeToFinish.equals(nodeId)) {
			String val = valueToString(value);

			if (val != null) {
				out.printf("\t\t%s %s%n", attribute, val);
			}
		} else {
			ensureToFinish();
		}
	}

	public void nodeAttributeChanged(String sourceId, long timeId,
			String nodeId, String attribute, Object oldValue, Object newValue) {
		if (edgeToFinish != null)
			ensureToFinish();
		// GML is not a dynamic file format ?
	}

	public void nodeAttributeRemoved(String sourceId, long timeId,
			String nodeId, String attribute) {
		if (edgeToFinish != null)
			ensureToFinish();
		// GML is not a dynamic file format ?
	}

	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId,
			String attribute, Object value) {
		if (edgeToFinish != null && edgeToFinish.equals(edgeId)) {
			String val = valueToString(value);

			if (val != null) {
				out.printf("\t\t%s %s%n", attribute, val);
			}
		} else {
			ensureToFinish();
		}
	}

	public void edgeAttributeChanged(String sourceId, long timeId,
			String edgeId, String attribute, Object oldValue, Object newValue) {
		if (nodeToFinish != null)
			ensureToFinish();
		// GML is not a dynamic file format ?
	}

	public void edgeAttributeRemoved(String sourceId, long timeId,
			String edgeId, String attribute) {
		if (nodeToFinish != null)
			ensureToFinish();
		// GML is not a dynamic file format ?
	}

	// Element events

	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		ensureToFinish();
		out.printf("\tnode [%n");
		out.printf("\t\tid %s%n", nodeId);
		nodeToFinish = nodeId;
	}

	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		ensureToFinish();
	}

	public void edgeAdded(String sourceId, long timeId, String edgeId,
			String fromNodeId, String toNodeId, boolean directed) {
		ensureToFinish();
		out.printf("\tedge [%n");
		out.printf("\t\tid %s%n", edgeId);
		out.printf("\t\tsource %s%n", fromNodeId);
		out.printf("\t\ttarget %s%n", toNodeId);
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

	protected String valueToString(Object value) {
		if (value instanceof String) {
			return (String) value;
		} else if (value instanceof Number) {
			return ((Number) value).toString();
		} else if (value instanceof CharSequence) {
			((CharSequence) value).toString();
		}

		// TODO ...

		return null;
	}

	protected void ensureToFinish() {
		assert ((nodeToFinish != null && edgeToFinish == null)
				|| (nodeToFinish == null && edgeToFinish != null) || (nodeToFinish == null && edgeToFinish == null));

		if (nodeToFinish != null || edgeToFinish != null) {
			out.printf("\t]%n");
			nodeToFinish = null;
			edgeToFinish = null;
		}
	}
}