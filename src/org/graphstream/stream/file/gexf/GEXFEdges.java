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
 * @since 2013-09-18
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file.gexf;

import java.util.HashMap;

import javax.xml.stream.XMLStreamException;

import org.graphstream.stream.SinkAdapter;

public class GEXFEdges extends SinkAdapter implements GEXFElement {
	GEXF root;
	HashMap<String, GEXFEdge> edges;

	public GEXFEdges(GEXF root) {
		this.root = root;
		this.edges = new HashMap<String, GEXFEdge>();

		root.addSink(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.file.gexf.GEXFElement#export(org.graphstream.stream
	 * .file.gexf.SmartXMLWriter)
	 */
	public void export(SmartXMLWriter stream) throws XMLStreamException {
		stream.startElement("edges");

		for (GEXFEdge edge : edges.values())
			edge.export(stream);

		stream.endElement(); // EDGES
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.SinkAdapter#edgeAdded(java.lang.String, long,
	 * java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId,
			boolean directed) {
		GEXFEdge edge = edges.get(edgeId);

		if (edge == null) {
			edge = new GEXFEdge(root, edgeId, fromNodeId, toNodeId, directed);
			edges.put(edgeId, edge);
		}

		edge.spells.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.SinkAdapter#edgeRemoved(java.lang.String, long,
	 * java.lang.String)
	 */
	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		GEXFEdge edge = edges.get(edgeId);

		if (edge == null) {
			System.err.printf("edge removed but not added\n");
			return;
		}

		edge.spells.end();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.SinkAdapter#graphCleared(java.lang.String, long)
	 */
	public void graphCleared(String sourceId, long timeId) {
		for (GEXFEdge edge : edges.values())
			edge.spells.end();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.SinkAdapter#edgeAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId, String attribute, Object value) {
		GEXFEdge edge = edges.get(edgeId);

		if (("ui.label".equals(attribute) || "label".equals(attribute)) && value != null)
			edge.label = value.toString();

		if ("weight".equals("attribute") && value != null && value instanceof Number)
			edge.weight = ((Number) value).doubleValue();

		edge.attvalues.attributeUpdated(root.getEdgeAttribute(attribute), value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.SinkAdapter#edgeAttributeChanged(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void edgeAttributeChanged(String sourceId, long timeId, String edgeId, String attribute, Object oldValue,
			Object newValue) {
		edgeAttributeAdded(sourceId, timeId, edgeId, attribute, newValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.SinkAdapter#edgeAttributeRemoved(java.lang.String,
	 * long, java.lang.String, java.lang.String)
	 */
	public void edgeAttributeRemoved(String sourceId, long timeId, String edgeId, String attribute) {
		GEXFEdge edge = edges.get(edgeId);
		edge.attvalues.attributeUpdated(root.getNodeAttribute(attribute), null);
	}
}
