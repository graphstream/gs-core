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
 * @since 2011-12-06
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.util.cumulative.CumulativeAttributes;
import org.graphstream.util.cumulative.CumulativeSpells;
import org.graphstream.util.cumulative.CumulativeSpells.Spell;
import org.graphstream.util.cumulative.GraphSpells;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class FileSinkGEXF extends FileSinkBase {
	public static enum TimeFormat {
		INTEGER(new DecimalFormat("#", new DecimalFormatSymbols(Locale.ROOT))), DOUBLE(
				new DecimalFormat("#.0###################", new DecimalFormatSymbols(Locale.ROOT))), DATE(
						new SimpleDateFormat("yyyy-MM-dd")), DATETIME(
								new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZ"));
		Format format;

		TimeFormat(Format f) {
			this.format = f;
		}
	}

	XMLStreamWriter stream;
	boolean smart;
	int depth;
	int currentAttributeIndex = 0;
	GraphSpells graphSpells;
	TimeFormat timeFormat;

	public FileSinkGEXF() {
		smart = true;
		depth = 0;
		graphSpells = null;
		timeFormat = TimeFormat.DOUBLE;
	}

	public void setTimeFormat(TimeFormat format) {
		this.timeFormat = format;
	}

	protected void putSpellAttributes(Spell s) throws XMLStreamException {
		if (s.isStarted()) {
			String start = s.isStartOpen() ? "startopen" : "start";
			String date = timeFormat.format.format(s.getStartDate());

			stream.writeAttribute(start, date);
		}

		if (s.isEnded()) {
			String end = s.isEndOpen() ? "endopen" : "end";
			String date = timeFormat.format.format(s.getEndDate());

			stream.writeAttribute(end, date);
		}
	}

	protected void outputEndOfFile() throws IOException {
		try {
			if (graphSpells != null) {
				exportGraphSpells();
				graphSpells = null;
			}

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
			stream = XMLOutputFactory.newFactory().createXMLStreamWriter(output);
			stream.writeStartDocument("UTF-8", "1.0");

			startElement(stream, "gexf");
			stream.writeAttribute("xmlns", "http://www.gexf.net/1.2draft");
			stream.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			stream.writeAttribute("xsi:schemaLocation",
					"http://www.gexf.net/1.2draft http://www.gexf.net/1.2draft/gexf.xsd");
			stream.writeAttribute("version", "1.2");

			startElement(stream, "meta");
			stream.writeAttribute("lastmodifieddate", df.format(date));
			startElement(stream, "creator");
			stream.writeCharacters("GraphStream - " + getClass().getName());
			endElement(stream, true);
			endElement(stream, false);
		} catch (XMLStreamException | FactoryConfigurationError e) {
			throw new IOException(e);
		}
	}

	protected void startElement(XMLStreamWriter stream, String name) throws XMLStreamException {
		if (smart) {
			stream.writeCharacters("\n");

			for (int i = 0; i < depth; i++)
				stream.writeCharacters(" ");
		}

		stream.writeStartElement(name);
		depth++;
	}

	protected void endElement(XMLStreamWriter stream, boolean leaf) throws XMLStreamException {
		depth--;

		if (smart && !leaf) {
			stream.writeCharacters("\n");

			for (int i = 0; i < depth; i++)
				stream.writeCharacters(" ");
		}

		stream.writeEndElement();
	}

	@Override
	protected void exportGraph(Graph g) {
		GEXFAttributeMap nodeAttributes = new GEXFAttributeMap("node", g);
		GEXFAttributeMap edgeAttributes = new GEXFAttributeMap("edge", g);

		final Consumer<Exception> onException = Exception::printStackTrace;

		try {
			startElement(stream, "graph");
			stream.writeAttribute("defaultedgetype", "undirected");

			nodeAttributes.export(stream);
			edgeAttributes.export(stream);

			startElement(stream, "nodes");

			g.nodes().forEach(n -> {
				try {
					startElement(stream, "node");
					stream.writeAttribute("id", n.getId());

					if (n.hasAttribute("label"))
						stream.writeAttribute("label", n.getAttribute("label").toString());

					if (n.getAttributeCount() > 0) {
						startElement(stream, "attvalues");

						n.attributeKeys().forEach(key -> {
							try {
								nodeAttributes.push(stream, n, key);
							} catch (XMLStreamException e) {
								onException.accept(e);
							}
						});

						endElement(stream, false);
					}

					endElement(stream, n.getAttributeCount() == 0);
				} catch (Exception ex) {
					onException.accept(ex);
				}
			});
			endElement(stream, false);

			startElement(stream, "edges");
			g.edges().forEach(e -> {
				try {
					startElement(stream, "edge");

					stream.writeAttribute("id", e.getId());
					stream.writeAttribute("source", e.getSourceNode().getId());
					stream.writeAttribute("target", e.getTargetNode().getId());

					if (e.getAttributeCount() > 0) {
						startElement(stream, "attvalues");

						e.attributeKeys().forEach(key -> {
							try {
								edgeAttributes.push(stream, e, key);
							} catch (XMLStreamException e1) {
								onException.accept(e1);
							}
						});

						endElement(stream, false);
					}

					endElement(stream, e.getAttributeCount() == 0);
				} catch (Exception ex) {
					onException.accept(ex);
				}
			});
			endElement(stream, false);

			endElement(stream, false);
		} catch (XMLStreamException e1) {
			onException.accept(e1);
		}
	}

	protected void exportGraphSpells() {
		GEXFAttributeMap nodeAttributes = new GEXFAttributeMap("node", graphSpells);
		GEXFAttributeMap edgeAttributes = new GEXFAttributeMap("edge", graphSpells);

		try {
			startElement(stream, "graph");
			stream.writeAttribute("mode", "dynamic");
			stream.writeAttribute("defaultedgetype", "undirected");
			stream.writeAttribute("timeformat", timeFormat.name().toLowerCase());

			nodeAttributes.export(stream);
			edgeAttributes.export(stream);

			startElement(stream, "nodes");
			for (String nodeId : graphSpells.getNodes()) {
				startElement(stream, "node");
				stream.writeAttribute("id", nodeId);

				CumulativeAttributes attr = graphSpells.getNodeAttributes(nodeId);
				Object label = attr.getAny("label");

				if (label != null)
					stream.writeAttribute("label", label.toString());

				CumulativeSpells spells = graphSpells.getNodeSpells(nodeId);

				if (!spells.isEternal()) {
					startElement(stream, "spells");
					for (int i = 0; i < spells.getSpellCount(); i++) {
						Spell s = spells.getSpell(i);

						startElement(stream, "spell");
						putSpellAttributes(s);
						endElement(stream, true);
					}
					endElement(stream, false);
				}

				if (attr.getAttributesCount() > 0) {
					startElement(stream, "attvalues");
					nodeAttributes.push(stream, nodeId, graphSpells);
					endElement(stream, false);
				}

				endElement(stream, spells.isEternal() && attr.getAttributesCount() == 0);
			}
			endElement(stream, false);

			startElement(stream, "edges");
			for (String edgeId : graphSpells.getEdges()) {
				startElement(stream, "edge");

				GraphSpells.EdgeData data = graphSpells.getEdgeData(edgeId);

				stream.writeAttribute("id", edgeId);
				stream.writeAttribute("source", data.getSource());
				stream.writeAttribute("target", data.getTarget());

				CumulativeAttributes attr = graphSpells.getEdgeAttributes(edgeId);

				CumulativeSpells spells = graphSpells.getEdgeSpells(edgeId);

				if (!spells.isEternal()) {
					startElement(stream, "spells");
					for (int i = 0; i < spells.getSpellCount(); i++) {
						Spell s = spells.getSpell(i);

						startElement(stream, "spell");
						putSpellAttributes(s);
						endElement(stream, true);
					}
					endElement(stream, false);
				}

				if (attr.getAttributesCount() > 0) {
					startElement(stream, "attvalues");
					edgeAttributes.push(stream, edgeId, graphSpells);
					endElement(stream, false);
				}

				endElement(stream, spells.isEternal() && attr.getAttributesCount() == 0);
			}
			endElement(stream, false);

			endElement(stream, false);
		} catch (XMLStreamException e1) {
			e1.printStackTrace();
		}
	}

	protected void checkGraphSpells() {
		if (graphSpells == null)
			graphSpells = new GraphSpells();
	}

	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId, String attribute, Object value) {
		checkGraphSpells();
		graphSpells.edgeAttributeAdded(sourceId, timeId, edgeId, attribute, value);
	}

	public void edgeAttributeChanged(String sourceId, long timeId, String edgeId, String attribute, Object oldValue,
			Object newValue) {
		checkGraphSpells();
		graphSpells.edgeAttributeChanged(sourceId, timeId, edgeId, attribute, oldValue, newValue);
	}

	public void edgeAttributeRemoved(String sourceId, long timeId, String edgeId, String attribute) {
		checkGraphSpells();
		graphSpells.edgeAttributeRemoved(sourceId, timeId, edgeId, attribute);
	}

	public void graphAttributeAdded(String sourceId, long timeId, String attribute, Object value) {
		checkGraphSpells();
		graphSpells.graphAttributeAdded(sourceId, timeId, attribute, value);
	}

	public void graphAttributeChanged(String sourceId, long timeId, String attribute, Object oldValue,
			Object newValue) {
		checkGraphSpells();
		graphSpells.graphAttributeChanged(sourceId, timeId, attribute, oldValue, newValue);
	}

	public void graphAttributeRemoved(String sourceId, long timeId, String attribute) {
		checkGraphSpells();
		graphSpells.graphAttributeRemoved(sourceId, timeId, attribute);
	}

	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId, String attribute, Object value) {
		checkGraphSpells();
		graphSpells.nodeAttributeAdded(sourceId, timeId, nodeId, attribute, value);
	}

	public void nodeAttributeChanged(String sourceId, long timeId, String nodeId, String attribute, Object oldValue,
			Object newValue) {
		checkGraphSpells();
		graphSpells.nodeAttributeChanged(sourceId, timeId, nodeId, attribute, oldValue, newValue);
	}

	public void nodeAttributeRemoved(String sourceId, long timeId, String nodeId, String attribute) {
		checkGraphSpells();
		graphSpells.nodeAttributeRemoved(sourceId, timeId, nodeId, attribute);
	}

	public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId,
			boolean directed) {
		checkGraphSpells();
		graphSpells.edgeAdded(sourceId, timeId, edgeId, fromNodeId, toNodeId, directed);
	}

	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		checkGraphSpells();
		graphSpells.edgeRemoved(sourceId, timeId, edgeId);
	}

	public void graphCleared(String sourceId, long timeId) {
		checkGraphSpells();
		graphSpells.graphCleared(sourceId, timeId);
	}

	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		checkGraphSpells();
		graphSpells.nodeAdded(sourceId, timeId, nodeId);
	}

	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		checkGraphSpells();
		graphSpells.nodeRemoved(sourceId, timeId, nodeId);
	}

	public void stepBegins(String sourceId, long timeId, double step) {
		checkGraphSpells();
		graphSpells.stepBegins(sourceId, timeId, step);
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

			Stream<? extends Element> stream;

			if (type.equals("node"))
				stream = g.nodes();
			else
				stream = g.edges();

			stream.forEach(e -> {
				e.attributeKeys().forEach(key -> {
					Object value = e.getAttribute(key);
					check(key, value);
				});
			});
		}

		GEXFAttributeMap(String type, GraphSpells spells) {
			this.type = type;

			if (type.equals("node")) {
				for (String nodeId : spells.getNodes()) {
					CumulativeAttributes attr = spells.getNodeAttributes(nodeId);

					for (String key : attr.getAttributes()) {
						for (Spell s : attr.getAttributeSpells(key)) {
							Object value = s.getAttachedData();
							check(key, value);
						}
					}
				}
			} else {
				for (String edgeId : spells.getEdges()) {
					CumulativeAttributes attr = spells.getEdgeAttributes(edgeId);

					for (String key : attr.getAttributes()) {
						for (Spell s : attr.getAttributeSpells(key)) {
							Object value = s.getAttachedData();
							check(key, value);
						}
					}
				}
			}
		}

		void check(String key, Object value) {
			String id = getID(key, value);
			String attType = "string";

			if (containsKey(id))
				return;

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
			else if (value.getClass().isArray() || value instanceof Collection)
				attType = "liststring";

			put(id, new GEXFAttribute(key, attType));
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

		void push(XMLStreamWriter stream, Element e, String key) throws XMLStreamException {
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

		void push(XMLStreamWriter stream, String elementId, GraphSpells spells) throws XMLStreamException {
			CumulativeAttributes attr;

			if (type.equals("node"))
				attr = spells.getNodeAttributes(elementId);
			else
				attr = spells.getEdgeAttributes(elementId);

			for (String key : attr.getAttributes()) {
				for (Spell s : attr.getAttributeSpells(key)) {
					Object value = s.getAttachedData();
					String id = getID(key, value);
					GEXFAttribute a = get(id);

					if (a == null) {
						// TODO
						return;
					}

					startElement(stream, "attvalue");
					stream.writeAttribute("for", Integer.toString(a.index));
					stream.writeAttribute("value", value.toString());
					putSpellAttributes(s);
					endElement(stream, true);
				}
			}
		}
	}
}
