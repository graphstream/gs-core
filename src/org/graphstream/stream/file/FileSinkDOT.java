/*
 * Copyright 2006 - 2011 
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
 * 
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