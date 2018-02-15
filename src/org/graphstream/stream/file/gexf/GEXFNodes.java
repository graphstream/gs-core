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
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file.gexf;

import java.lang.reflect.Array;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;

import org.graphstream.stream.SinkAdapter;

public class GEXFNodes extends SinkAdapter implements GEXFElement {
	GEXF root;
	HashMap<String, GEXFNode> nodes;

	public GEXFNodes(GEXF root) {
		this.root = root;
		this.nodes = new HashMap<String, GEXFNode>();

		root.addSink(this);
	}

	private float[] convertToXYZ(Object value) {
		if (value == null || !value.getClass().isArray())
			return null;

		float[] xyz = new float[Array.getLength(value)];

		for (int i = 0; i < xyz.length; i++) {
			Object o = Array.get(value, i);

			if (o instanceof Number)
				xyz[i] = ((Number) o).floatValue();
			else
				return null;
		}

		return xyz;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.file.gexf.GEXFElement#export(org.graphstream.stream
	 * .file.gexf.SmartXMLWriter)
	 */
	public void export(SmartXMLWriter stream) throws XMLStreamException {
		stream.startElement("nodes");

		for (GEXFNode node : nodes.values())
			node.export(stream);

		stream.endElement(); // NODES
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.SinkAdapter#nodeAdded(java.lang.String, long,
	 * java.lang.String)
	 */
	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		GEXFNode node = nodes.get(nodeId);

		if (node == null) {
			node = new GEXFNode(root, nodeId);
			nodes.put(nodeId, node);
		}

		node.spells.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.SinkAdapter#nodeRemoved(java.lang.String, long,
	 * java.lang.String)
	 */
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		GEXFNode node = nodes.get(nodeId);

		if (node == null) {
			System.err.printf("node removed but not added\n");
			return;
		}

		node.spells.end();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.SinkAdapter#nodeAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId, String attribute, Object value) {
		GEXFNode node = nodes.get(nodeId);

		if (("ui.label".equals(attribute) || "label".equals(attribute)) && value != null)
			node.label = value.toString();

		if ("xyz".equals(attribute)) {
			float[] xyz = convertToXYZ(value);

			switch (xyz.length) {
			default:
				node.z = xyz[2];
			case 2:
				node.y = xyz[1];
			case 1:
				node.x = xyz[0];
			case 0:
				break;
			}

			node.position = true;
		}

		node.attvalues.attributeUpdated(root.getNodeAttribute(attribute), value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.SinkAdapter#nodeAttributeChanged(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void nodeAttributeChanged(String sourceId, long timeId, String nodeId, String attribute, Object oldValue,
			Object newValue) {
		nodeAttributeAdded(sourceId, timeId, nodeId, attribute, newValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.SinkAdapter#nodeAttributeRemoved(java.lang.String,
	 * long, java.lang.String, java.lang.String)
	 */
	public void nodeAttributeRemoved(String sourceId, long timeId, String nodeId, String attribute) {
		GEXFNode node = nodes.get(nodeId);
		node.attvalues.attributeUpdated(root.getNodeAttribute(attribute), null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.SinkAdapter#graphCleared(java.lang.String, long)
	 */
	public void graphCleared(String sourceId, long timeId) {
		for (GEXFNode node : nodes.values())
			node.spells.end();
	}
}
