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
import java.util.Map;

/**
 * Graph writer for the GraphViz DOT format.
 */
public class FileSinkDOT extends FileSinkBase {
	// Attribute

	/**
	 * The output.
	 */
	protected PrintStream out;

	/**
	 * The graph name (set as soon as known).
	 */
	protected String graphName = "";

	/**
	 * What element ?.
	 */
	protected enum What {
		NODE, EDGE, OTHER
	};

	// Command

	@Override
	protected void outputHeader() throws IOException {
		out = (PrintStream) output;
		out.printf("graph {%n");

		if (graphName.length() > 0)
			out.printf("\tgraph [label=%s];%n", graphName);
	}

	@Override
	protected void outputEndOfFile() throws IOException {
		out.printf("}%n");
	}

	public void edgeAttributeAdded(String graphId, long timeId, String edgeId,
			String attribute, Object value) {
		// NOP
	}

	public void edgeAttributeChanged(String graphId, long timeId,
			String edgeId, String attribute, Object oldValue, Object newValue) {
		// NOP
	}

	public void edgeAttributeRemoved(String graphId, long timeId,
			String edgeId, String attribute) {
		// NOP
	}

	public void graphAttributeAdded(String graphId, long timeId,
			String attribute, Object value) {
		// NOP
	}

	public void graphAttributeChanged(String graphId, long timeId,
			String attribute, Object oldValue, Object newValue) {
		// NOP
	}

	public void graphAttributeRemoved(String graphId, long timeId,
			String attribute) {
		// NOP
	}

	public void nodeAttributeAdded(String graphId, long timeId, String nodeId,
			String attribute, Object value) {
		out.printf("\t%s [ %s ];%n", nodeId,
				outputAttribute(attribute, value, true));
	}

	public void nodeAttributeChanged(String graphId, long timeId,
			String nodeId, String attribute, Object oldValue, Object newValue) {
		out.printf("\t%s [ %s ];%n", nodeId,
				outputAttribute(attribute, newValue, true));
	}

	public void nodeAttributeRemoved(String graphId, long timeId,
			String nodeId, String attribute) {
		// NOP
	}

	public void edgeAdded(String graphId, long timeId, String edgeId,
			String fromNodeId, String toNodeId, boolean directed) {
		if (directed)
			out.printf("\t%s -> %s;%n", fromNodeId, toNodeId);
		else
			out.printf("\t%s -- %s;%n", fromNodeId, toNodeId);
	}

	public void edgeRemoved(String graphId, long timeId, String edgeId) {
		// NOP
	}

	public void graphCleared(String graphId, long timeId) {
		// NOP
	}

	public void nodeAdded(String graphId, long timeId, String nodeId) {
		out.printf("\t%s;%n", nodeId);
	}

	public void nodeRemoved(String graphId, long timeId, String nodeId) {
		// NOP
	}

	public void stepBegins(String graphId, long timeId, double step) {
		// NOP
	}

	// Utility

	protected void outputAttributes(Map<String, Object> attributes, What what)
			throws IOException {
		out.printf(" [");

		boolean first = true;

		for (String key : attributes.keySet()) {
			Object value = attributes.get(key);

			if (what == What.NODE) {
				// if( ! nodeForbiddenAttrs.contains( key ) )
				{
					first = outputAttribute(key, value, first);
				}
			} else if (what == What.EDGE) {
				// if( ! edgeForbiddenAttrs.contains( key ) )
				{
					first = outputAttribute(key, value, first);
				}
			} else {
				first = outputAttribute(key, value, first);
			}

		}

		out.printf("]");
	}

	protected boolean outputAttribute(String key, Object value, boolean first) {
		if (first)
			out.printf("\"%s\"=\"%s\"", key, value);
		else
			out.printf(",\"%s\"=\"%s\"", key, value);

		return false;
	}
}