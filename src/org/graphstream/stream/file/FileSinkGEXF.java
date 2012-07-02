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
import org.graphstream.graph.implementations.AdjacencyListGraph;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class FileSinkGEXF extends FileSinkBase {
	XMLStreamWriter stream;
	boolean smart;
	int depth;
	int currentAttributeIndex = 0;

	public FileSinkGEXF() {
		smart = true;
		depth = 0;
	}

	protected void outputEndOfFile() throws IOException {
		try {
			endElement(stream, false);
			stream.writeEndDocument();
			stream.flush();
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}

	protected void outputHeader() throws IOException {
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		
		try {
			stream = XMLOutputFactory.newFactory()
					.createXMLStreamWriter(output);
			stream.writeStartDocument("UTF-8", "1.0");

			startElement(stream, "gexf");
			stream.writeAttribute("xmlns", "http://www.gexf.net/1.2draft");
			stream.writeAttribute("xmlns:xsi",
					"http://www.w3.org/2001/XMLSchema-instance");
			stream.writeAttribute("xsi:schemaLocation",
					"http://www.gexf.net/1.2draft http://www.gexf.net/1.2draft/gexf.xsd");
			stream.writeAttribute("version", "1.2");
			
			startElement(stream, "meta");
			stream.writeAttribute("lastmodifieddate", df.format(date));
			startElement(stream, "creator");
			stream.writeCharacters("GraphStream - " + getClass().getName());
			endElement(stream, true);
			endElement(stream, false);
		} catch (XMLStreamException e) {
			throw new IOException(e);
		} catch (FactoryConfigurationError e) {
			throw new IOException(e);
		}
	}

	protected void startElement(XMLStreamWriter stream, String name)
			throws XMLStreamException {
		if (smart) {
			stream.writeCharacters("\n");

			for (int i = 0; i < depth; i++)
				stream.writeCharacters("\t");
		}

		stream.writeStartElement(name);
		depth++;
	}

	protected void endElement(XMLStreamWriter stream, boolean leaf)
			throws XMLStreamException {
		depth--;

		if (smart && !leaf) {
			stream.writeCharacters("\n");

			for (int i = 0; i < depth; i++)
				stream.writeCharacters("\t");
		}

		stream.writeEndElement();
	}

	@Override
	protected void exportGraph(Graph g) {
		GEXFAttributeMap nodeAttributes = new GEXFAttributeMap("node", g);
		GEXFAttributeMap edgeAttributes = new GEXFAttributeMap("edge", g);

		try {
			startElement(stream, "graph");
			stream.writeAttribute("defaultedgetype", "undirected");

			nodeAttributes.export(stream);
			edgeAttributes.export(stream);

			startElement(stream, "nodes");
			for (Node n : g.getEachNode()) {
				startElement(stream, "node");
				stream.writeAttribute("id", n.getId());

				if (n.hasAttribute("label"))
					stream.writeAttribute("label", n.getAttribute("label")
							.toString());

				if (n.getAttributeCount() > 0) {
					startElement(stream, "attvalues");
					for (String key : n.getAttributeKeySet())
						nodeAttributes.push(stream, n, key);
					endElement(stream, false);
				}

				endElement(stream, n.getAttributeCount() == 0);
			}
			endElement(stream, false);

			startElement(stream, "edges");
			for (Edge e : g.getEachEdge()) {
				startElement(stream, "edge");

				stream.writeAttribute("id", e.getId());
				stream.writeAttribute("source", e.getSourceNode().getId());
				stream.writeAttribute("target", e.getTargetNode().getId());

				if (e.getAttributeCount() > 0) {
					startElement(stream, "attvalues");
					for (String key : e.getAttributeKeySet())
						nodeAttributes.push(stream, e, key);
					endElement(stream, false);
				}

				endElement(stream, e.getAttributeCount() == 0);
			}
			endElement(stream, false);

			endElement(stream, false);
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

	class GEXFAttribute {
		int index;
		String key;
		String type;

		GEXFAttribute(String key, String type) {
			this.index = currentAttributeIndex++;
			this.key = key;
			this.type = type;
		}
	}

	class GEXFAttributeMap extends HashMap<String, GEXFAttribute> {
		private static final long serialVersionUID = 6176508111522815024L;
		protected String type;

		GEXFAttributeMap(String type, Graph g) {
			this.type = type;

			Iterable<? extends Element> iterable;

			if (type.equals("node"))
				iterable = (Iterable<? extends Element>) g.getNodeSet();
			else
				iterable = (Iterable<? extends Element>) g.getEdgeSet();

			for (Element e : iterable) {
				for (String key : e.getAttributeKeySet()) {
					Object value = e.getAttribute(key);
					String id = getID(key, value);
					String attType = "string";

					if (containsKey(id))
						continue;

					if (value instanceof Integer || value instanceof Short)
						attType = "integer";
					else if (value instanceof Long)
						attType = "long";
					else if (value instanceof Float)
						attType = "float";
					else if (value instanceof Double)
						attType = "double";
					else if (value instanceof Boolean)
						attType = "boolean";
					else if (value instanceof URL || value instanceof URI)
						attType = "anyURI";
					else if (value.getClass().isArray()
							|| value instanceof Collection)
						attType = "liststring";

					put(id, new GEXFAttribute(key, attType));
				}
			}
		}

		String getID(String key, Object value) {
			return String.format("%s@%s", key, value.getClass().getName());
		}

		void export(XMLStreamWriter stream) throws XMLStreamException {
			if (size() == 0)
				return;

			startElement(stream, "attributes");
			stream.writeAttribute("class", type);

			for (GEXFAttribute a : values()) {
				startElement(stream, "attribute");
				stream.writeAttribute("id", Integer.toString(a.index));
				stream.writeAttribute("title", a.key);
				stream.writeAttribute("type", a.type);
				endElement(stream, true);
			}

			endElement(stream, size() == 0);
		}

		void push(XMLStreamWriter stream, Element e, String key)
				throws XMLStreamException {
			String id = getID(key, e.getAttribute(key));
			GEXFAttribute a = get(id);

			if (a == null) {
				// TODO
				return;
			}

			startElement(stream, "attvalue");
			stream.writeAttribute("for", Integer.toString(a.index));
			stream.writeAttribute("value", e.getAttribute(key).toString());
			endElement(stream, true);
		}
	}

	public static void main(String... args) throws Exception {
		Graph g = new AdjacencyListGraph("g");
		g.addNode("A").addAttribute("label", "Node A");
		g.addNode("B").addAttribute("test", 1.0);
		g.addNode("C").addAttribute("test", "Test");
		g.addNode("D").addAttribute("other", true);

		g.addEdge("AB", "A", "B");

		FileSinkGEXF out = new FileSinkGEXF();
		out.writeAll(g, System.out);
	}
}
