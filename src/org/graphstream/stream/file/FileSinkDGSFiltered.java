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
 * @since 2014-11-03
 * 
 * @author Thibaut Démare <fdhp_76@hotmail.com>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 */
package org.graphstream.stream.file;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * <p>
 * File output for the DGS (Dynamic Graph Stream) file format. It includes also
 * the possibility to filter dynamic events such as :
 * <ul>
 * <li>Addition or deletion of nodes or edges.</li>
 * <li>Addition/deletion/modification of attributes of nodes, edges or the graph
 * itself.</li>
 * <li>A step event.</li>
 * </ul>
 * </p>
 *
 * <p>
 * For instance :
 * </p>
 * 
 * <pre>
 * Graph graph = new SingleGraph("My_Graph");
 * FileSinkDGSFiltered fileSink = new FileSinkDGSFiltered();
 * graph.addSink(fileSink);
 * 
 * // No need to save the attribute "attr1" of any edges
 * fileSink.addEdgeAttributeFiltered("attr1");
 * // No need to save graph attributes
 * fileSink.setNoFilterGraphAttributeAdded(false);
 * 
 * // Start to listen event
 * fileSink.begin("./my_graph.dgs");
 * 
 * // Make some modifications on the graph and generate events
 * graph.stepBegins(0); // this event will be saved
 * graph.setAttribute("attr2", 2); // this event will not be saved
 * Node a = graph.addNode("A"); // this event will be saved
 * a.setAttribute("attr3", 3); // this event will be saved
 * 
 * // and now, no more need to save modification on nodes attributes
 * fileSink.setNoFilterNodeAttributeChanged(false);
 * 
 * Node b = graph.addNode("B"); // this event will be saved
 * b.setAttribute("attr4", 4); // this event will not be saved
 * 
 * Edge ab = graph.addEdge("AB", a, b); // this event will be saved
 * ab.setAttribute("attr1", 1); // this event will not be saved
 * ab.setAttribute("attr5", 5); // this event will be saved
 * 
 * fileSink.end();
 * </pre>
 */
public class FileSinkDGSFiltered extends FileSinkBaseFiltered {

	// Attribute

	/**
	 * A shortcut to the output.
	 */
	protected PrintWriter out;
	protected String graphName = "";

	// Command

	@Override
	protected void outputHeader() throws IOException {
		out = (PrintWriter) output;
		out.printf("DGS004%n");

		if (graphName.length() <= 0)
			out.printf("null 0 0%n");
		else
			out.printf("\"%s\" 0 0%n", FileSinkDGSUtility.formatStringForQuoting(graphName));
	}

	@Override
	protected void outputEndOfFile() throws IOException {
		// NOP
	}

	public void edgeAttributeAdded(String graphId, long timeId, String edgeId, String attribute, Object value) {
		if (noFilterEdgeAttributeAdded && !edgeAttributesFiltered.contains(attribute))
			edgeAttributeChanged(graphId, timeId, edgeId, attribute, null, value);
	}

	public void edgeAttributeChanged(String graphId, long timeId, String edgeId, String attribute, Object oldValue,
			Object newValue) {
		if (noFilterEdgeAttributeChanged && !edgeAttributesFiltered.contains(attribute))
			out.printf("ce \"%s\" %s%n", FileSinkDGSUtility.formatStringForQuoting(edgeId),
					FileSinkDGSUtility.attributeString(attribute, newValue, false));
	}

	public void edgeAttributeRemoved(String graphId, long timeId, String edgeId, String attribute) {
		if (noFilterEdgeAttributeRemoved && !edgeAttributesFiltered.contains(attribute))
			out.printf("ce \"%s\" %s%n", FileSinkDGSUtility.formatStringForQuoting(edgeId),
					FileSinkDGSUtility.attributeString(attribute, null, true));
	}

	public void graphAttributeAdded(String graphId, long timeId, String attribute, Object value) {
		if (noFilterGraphAttributeAdded && !graphAttributesFiltered.contains(attribute))
			graphAttributeChanged(graphId, timeId, attribute, null, value);
	}

	public void graphAttributeChanged(String graphId, long timeId, String attribute, Object oldValue, Object newValue) {
		if (noFilterGraphAttributeChanged && !graphAttributesFiltered.contains(attribute))
			out.printf("cg %s%n", FileSinkDGSUtility.attributeString(attribute, newValue, false));
	}

	public void graphAttributeRemoved(String graphId, long timeId, String attribute) {
		if (noFilterGraphAttributeRemoved && !graphAttributesFiltered.contains(attribute))
			out.printf("cg %s%n", FileSinkDGSUtility.attributeString(attribute, null, true));
	}

	public void nodeAttributeAdded(String graphId, long timeId, String nodeId, String attribute, Object value) {
		if (noFilterNodeAttributeAdded && !nodeAttributesFiltered.contains(attribute))
			nodeAttributeChanged(graphId, timeId, nodeId, attribute, null, value);
	}

	public void nodeAttributeChanged(String graphId, long timeId, String nodeId, String attribute, Object oldValue,
			Object newValue) {
		if (noFilterNodeAttributeChanged && !nodeAttributesFiltered.contains(attribute))
			out.printf("cn \"%s\" %s%n", FileSinkDGSUtility.formatStringForQuoting(nodeId),
					FileSinkDGSUtility.attributeString(attribute, newValue, false));
	}

	public void nodeAttributeRemoved(String graphId, long timeId, String nodeId, String attribute) {
		if (noFilterNodeAttributeRemoved && !nodeAttributesFiltered.contains(attribute))
			out.printf("cn \"%s\" %s%n", FileSinkDGSUtility.formatStringForQuoting(nodeId),
					FileSinkDGSUtility.attributeString(attribute, null, true));
	}

	public void edgeAdded(String graphId, long timeId, String edgeId, String fromNodeId, String toNodeId,
			boolean directed) {
		if (noFilterEdgeAdded) {
			edgeId = FileSinkDGSUtility.formatStringForQuoting(edgeId);
			fromNodeId = FileSinkDGSUtility.formatStringForQuoting(fromNodeId);
			toNodeId = FileSinkDGSUtility.formatStringForQuoting(toNodeId);
			out.printf("ae \"%s\" \"%s\" %s \"%s\"%n", edgeId, fromNodeId, directed ? ">" : "", toNodeId);
		}
	}

	public void edgeRemoved(String graphId, long timeId, String edgeId) {
		if (noFilterEdgeRemoved)
			out.printf("de \"%s\"%n", FileSinkDGSUtility.formatStringForQuoting(edgeId));
	}

	public void graphCleared(String graphId, long timeId) {
		if (noFilterGraphCleared)
			out.printf("cl%n");
	}

	public void nodeAdded(String graphId, long timeId, String nodeId) {
		if (noFilterNodeAdded)
			out.printf("an \"%s\"%n", FileSinkDGSUtility.formatStringForQuoting(nodeId));
	}

	public void nodeRemoved(String graphId, long timeId, String nodeId) {
		if (noFilterNodeRemoved)
			out.printf("dn \"%s\"%n", FileSinkDGSUtility.formatStringForQuoting(nodeId));
	}

	public void stepBegins(String graphId, long timeId, double step) {
		if (noFilterStepBegins)
			out.printf(Locale.US, "st %f%n", step);
	}
}
