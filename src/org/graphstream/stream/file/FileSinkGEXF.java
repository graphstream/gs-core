/*
 * Copyright 2006 - 2012
 *      Stefan Balev       <stefan.balev@graphstream-project.org>
 *      Julien Baudry	<julien.baudry@graphstream-project.org>
 *      Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *      Yoann Pigné	<yoann.pigne@graphstream-project.org>
 *      Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
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

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class FileSinkGEXF extends FileSinkBase {
	XMLStreamWriter stream;

	protected void outputEndOfFile() throws IOException {
		try {
			stream.writeEndElement();
			stream.writeEndDocument();
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}

	protected void outputHeader() throws IOException {
		try {
			stream = XMLOutputFactory.newFactory()
					.createXMLStreamWriter(output);
			stream.writeStartDocument("UTF-8", "1.0");

			stream.writeStartElement("gexf");
			stream.writeAttribute("xmlns", "http://www.gexf.net/1.2draft");
			stream.writeAttribute("xmlns:xsi",
					"http://www.w3.org/2001/XMLSchema-instance");
			stream.writeAttribute("xsi:schemaLocation",
					"http://www.gexf.net/1.2draft http://www.gexf.net/1.2draft/gexf.xsd");
			stream.writeAttribute("version", "1.2");
		} catch (XMLStreamException e) {
			throw new IOException(e);
		} catch (FactoryConfigurationError e) {
			throw new IOException(e);
		}
	}

	@Override
	protected void exportGraph(Graph g) {
		GEXFAttributeMap nodeAttributes = new GEXFAttributeMap("node", g);
		GEXFAttributeMap edgeAttributes = new GEXFAttributeMap("edge", g);

		try {
			stream.writeStartElement("graph");
			stream.writeAttribute("defaultedgetype", "undirected");

			nodeAttributes.export(stream);
			edgeAttributes.export(stream);

			stream.writeStartElement("nodes");
			for (Node n : g.getEachNode()) {
				stream.writeStartElement("node");
				stream.writeAttribute("id", n.getId());

				if (n.hasAttribute("label"))
					stream.writeAttribute("label", n.getAttribute("label")
							.toString());

				stream.writeStartElement("attvalues");
				for (String key : n.getAttributeKeySet())
					nodeAttributes.push(stream, n, key);
				stream.writeEndElement();

				stream.writeEndElement();
			}
			stream.writeEndElement();

			stream.writeStartElement("edges");
			for (Edge e : g.getEachEdge()) {
				stream.writeStartElement("edge");

				stream.writeAttribute("id", e.getId());
				stream.writeAttribute("source", e.getSourceNode().getId());
				stream.writeAttribute("target", e.getTargetNode().getId());

				stream.writeStartElement("attvalues");
				for (String key : e.getAttributeKeySet())
					nodeAttributes.push(stream, e, key);
				stream.writeEndElement();

				stream.writeEndElement();
			}
			stream.writeEndElement();

			stream.writeEndElement();
		} catch (XMLStreamException e1) {
			e1.printStackTrace();
		}
	}

	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId,
			String attribute, Object value) {
		throw new UnsupportedOperationException();
	}

	public void edgeAttributeChanged(String sourceId, long timeId,
			String edgeId, String attribute, Object oldValue, Object newValue) {
		throw new UnsupportedOperationException();
	}

	public void edgeAttributeRemoved(String sourceId, long timeId,
			String edgeId, String attribute) {
		throw new UnsupportedOperationException();
	}

	public void graphAttributeAdded(String sourceId, long timeId,
			String attribute, Object value) {
		throw new UnsupportedOperationException();
	}

	public void graphAttributeChanged(String sourceId, long timeId,
			String attribute, Object oldValue, Object newValue) {
		throw new UnsupportedOperationException();
	}

	public void graphAttributeRemoved(String sourceId, long timeId,
			String attribute) {
		throw new UnsupportedOperationException();
	}

	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId,
			String attribute, Object value) {
		throw new UnsupportedOperationException();
	}

	public void nodeAttributeChanged(String sourceId, long timeId,
			String nodeId, String attribute, Object oldValue, Object newValue) {
		throw new UnsupportedOperationException();
	}

	public void nodeAttributeRemoved(String sourceId, long timeId,
			String nodeId, String attribute) {
		throw new UnsupportedOperationException();
	}

	public void edgeAdded(String sourceId, long timeId, String edgeId,
			String fromNodeId, String toNodeId, boolean directed) {
		throw new UnsupportedOperationException();
	}

	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		throw new UnsupportedOperationException();
	}

	public void graphCleared(String sourceId, long timeId) {
		throw new UnsupportedOperationException();
	}

	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		throw new UnsupportedOperationException();
	}

	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		throw new UnsupportedOperationException();
	}

	public void stepBegins(String sourceId, long timeId, double step) {
		throw new UnsupportedOperationException();
	}

	static class GEXFAttribute {
		static int currentIndex = 0;

		int index;
		String key;
		String type;

		GEXFAttribute(String key, String type) {
			this.index = currentIndex++;
			this.key = key;
			this.type = type;
		}
	}

	static class GEXFAttributeMap extends HashMap<String, GEXFAttribute> {
		private static final long serialVersionUID = 6176508111522815024L;
		protected String type;

		GEXFAttributeMap(String type, Graph g) {
			this.type = type;

			if (type.equals("node")) {
				// TODO
			} else {
				// TODO
			}
		}

		void export(XMLStreamWriter stream) throws XMLStreamException {
			stream.writeStartElement("attributes");
			stream.writeAttribute("class", type);

			for (GEXFAttribute a : values()) {
				stream.writeStartElement("attribute");
				stream.writeAttribute("id", Integer.toString(a.index));
				stream.writeAttribute("title", a.key);
				stream.writeAttribute("type", a.type);
				stream.writeEndDocument();
			}

			stream.writeEndElement();
		}

		void push(XMLStreamWriter stream, Element e, String key)
				throws XMLStreamException {
			// TODO
		}
	}
}
