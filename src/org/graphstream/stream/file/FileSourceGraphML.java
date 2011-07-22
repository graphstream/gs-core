/*
 * Copyright 2006 - 2011 
 * 	   Stefan Balev     <stefan.balev@graphstream-project.org>
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

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.graphstream.stream.SourceBase;

public class FileSourceGraphML extends SourceBase implements FileSource,
		XMLStreamConstants {

	protected static enum Balise {
		GRAPHML, GRAPH, NODE, EDGE, HYPEREDGE, DESC, DATA, LOCATOR, PORT, KEY, DEFAULT
	}

	protected static enum GraphAttribute {
		ID, EDGEDEFAULT
	}

	protected static enum NodeAttribute {
		ID
	}

	protected static enum EdgeAttribute {
		ID, SOURCE, SOURCEPORT, TARGET, TARGETPORT, DIRECTED
	}

	protected static enum DataAttribute {
		KEY, ID
	}

	protected static enum KeyAttribute {
		ID, FOR, ATTR_NAME, ATTR_TYPE
	}

	protected static enum KeyDomain {
		GRAPHML, GRAPH, NODE, EDGE, HYPEREDGE, PORT, ENDPOINT, ALL
	}

	protected static enum KeyAttrType {
		BOOLEAN, INT, LONG, FLOAT, DOUBLE, STRING
	}

	protected static class Element {
		final Balise name;
		String id;
		StringBuilder characters;
		HashMap<String, String> extra = null;

		Element(Balise name) {
			this.name = name;
		}

		void addCharacters(String data) {
			if (characters == null)
				characters = new StringBuilder();

			characters.append(data);
		}

		String characters() {
			if (characters == null)
				return "";

			return characters.toString();
		}

		void setExtra(String key, String value) {
			if (extra == null)
				extra = new HashMap<String, String>();

			extra.put(key, value);
		}

		String getExtra(String key) {
			if (extra == null)
				return null;

			return extra.get(key);
		}
	}

	protected static class Key {
		KeyDomain domain;
		String name;
		KeyAttrType type;
		String def = null;

		Key() {
			domain = KeyDomain.ALL;
			name = null;
			type = KeyAttrType.STRING;
		}

		Object getKeyValue(String value) {
			if (value == null)
				return null;

			switch (type) {
			case STRING:
				return value;
			case INT:
				return Integer.valueOf(value);
			case LONG:
				return Long.valueOf(value);
			case FLOAT:
				return Float.valueOf(value);
			case DOUBLE:
				return Double.valueOf(value);
			case BOOLEAN:
				return Boolean.valueOf(value);
			}

			return value;
		}

		Object getDefaultValue() {
			return getKeyValue(def);
		}
	}

	protected static class Data {
		Key key;
		String value;
	}

	XMLEventReader reader;
	Stack<Element> balises;
	String graphId;
	boolean directed;
	HashMap<String, Key> keys;
	LinkedList<Data> datas;

	public FileSourceGraphML() {
		balises = new Stack<Element>();
		keys = new HashMap<String, Key>();
		datas = new LinkedList<Data>();
	}

	public void readAll(String fileName) throws IOException {
		// TODO Auto-generated method stub

	}

	public void readAll(URL url) throws IOException {
		// TODO Auto-generated method stub

	}

	public void readAll(InputStream stream) throws IOException {
		// TODO Auto-generated method stub

	}

	public void readAll(Reader reader) throws IOException {
		// TODO Auto-generated method stub

	}

	public void begin(String fileName) throws IOException {
		begin(new FileReader(fileName));
	}

	public void begin(URL url) throws IOException {
		begin(url.openStream());
	}

	public void begin(InputStream stream) throws IOException {
		begin(new InputStreamReader(stream));
	}

	public void begin(Reader reader) throws IOException {
		openStream(reader);
	}

	public boolean nextEvents() throws IOException {
		try {
			XMLEvent e = reader.nextEvent();

			switch (e.getEventType()) {
			case START_ELEMENT:
				StartElement start = e.asStartElement();
				String startName = start.getName().getLocalPart();

				try {
					Balise b = Balise.valueOf(toConstantName(startName));
					checkAncestor(b);
					balises.push(new Element(b));

					switch (b) {
					case GRAPH:
						nextGraph(start);
						break;
					case NODE:
						nextNode(start);
						break;
					case EDGE:
						nextEdge(start);
						break;
					case HYPEREDGE:
						throw new IOException(
								"hyperedge feature is not implemented");
					case KEY:
						nextKey(start);
						break;
					case DATA:
						nextData(start);
						break;
					}
				} catch (IllegalArgumentException ex) {
					throw new IOException("Invalid start tag \"" + startName
							+ "\"");
				}

				break;
			case END_ELEMENT:
				EndElement end = e.asEndElement();
				String endName = end.getName().getLocalPart();

				try {
					Balise b = Balise.valueOf(toConstantName(endName));

					if (b != balises.peek().name)
						throw new IOException("end a non-started element");

					switch (b) {
					case GRAPH:
						flushAttributes();
						break;
					case NODE:
						flushAttributes();
						break;
					case EDGE:
						flushAttributes();
						break;
					case DATA:
						String key = balises.peek().getExtra("key");
						String value = balises.peek().characters();

						if (!keys.containsKey(key))
							throw new IOException("Invalid key \"" + key
									+ "\"for data");

						Data d = new Data();
						d.key = keys.get(key);
						d.value = value;

						datas.add(d);

						System.out.printf("set ");
						switch (balises.elementAt(balises.size() - 2).name) {
						case GRAPH:
							System.out.printf("graph ");
							break;
						case NODE:
							System.out.printf("node ");
							break;
						case EDGE:
							System.out.printf("edge ");
							break;
						}

						System.out.printf("attribute %s=\"%s\"\n", key, value);

						break;
					case DEFAULT:
						System.out.printf("set key default %s : %s\n",
								balises.elementAt(balises.size() - 2).id,
								balises.peek().characters());
						keys.get(balises.elementAt(balises.size() - 2).id).def = balises
								.peek().characters();
						break;
					}

					balises.pop();
				} catch (IllegalArgumentException ex) {
					throw new IOException("Invalid end tag \"" + endName + "\"");
				}

				break;
			case CHARACTERS:
				Characters characters = e.asCharacters();
				balises.peek().addCharacters(characters.getData());

				break;
			}
		} catch (XMLStreamException ex) {
			throw new IOException(ex);
		}

		return reader.hasNext();
	}

	public boolean nextStep() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	public void end() throws IOException {
		closeStream();
	}

	protected void flushAttributes() throws IOException {
		HashSet<String> flushed = new HashSet<String>();
		Balise element = balises.peek().name;

		while (datas.size() > 0) {
			Data d = datas.poll();

			String key = d.key.name;
			Object val = d.key.getKeyValue(d.value);

			switch (element) {
			case GRAPH:
				sendGraphAttributeAdded(sourceId, key, val);
				break;
			case NODE:
				sendNodeAttributeAdded(sourceId, balises.peek().id, key, val);
				break;
			case EDGE:
				sendEdgeAttributeAdded(sourceId, balises.peek().id, key, val);
				break;
			default:
				throw new IOException("Invalid element for attributes");
			}

			flushed.add(d.key.name);
		}
	}

	protected void nextGraph(StartElement graph) throws IOException {
		@SuppressWarnings("unchecked")
		Iterator<? extends Attribute> attributes = graph.getAttributes();

		String id = null;
		boolean directed = false;

		while (attributes.hasNext()) {
			Attribute a = attributes.next();

			try {
				GraphAttribute attribute = GraphAttribute
						.valueOf(toConstantName(a));

				switch (attribute) {
				case ID:
					id = a.getValue();
					break;
				case EDGEDEFAULT:
					if (a.getValue().equals("directed"))
						directed = true;

					break;
				}
			} catch (IllegalArgumentException e) {
				throw new IOException("invalid graph attribute \""
						+ a.getName().getLocalPart() + "\"");
			}
		}

		if (id == null)
			throw new IOException("graph must have an id");

		balises.peek().id = id;
		this.graphId = id;
		this.directed = directed;
	}

	protected void nextNode(StartElement node) throws IOException {
		@SuppressWarnings("unchecked")
		Iterator<? extends Attribute> attributes = node.getAttributes();

		String id = null;

		while (attributes.hasNext()) {
			Attribute a = attributes.next();

			try {
				NodeAttribute attribute = NodeAttribute
						.valueOf(toConstantName(a));

				switch (attribute) {
				case ID:
					id = a.getValue();
					break;
				}
			} catch (IllegalArgumentException e) {
				throw new IOException("invalid graph attribute \""
						+ a.getName().getLocalPart() + "\"");
			}
		}

		if (id == null)
			throw new IOException("node must have an id");

		balises.peek().id = id;
		sendNodeAdded(sourceId, id);

		System.out.printf("add node \"%s\"\n", id);
	}

	protected void nextEdge(StartElement edge) throws IOException {
		@SuppressWarnings("unchecked")
		Iterator<? extends Attribute> attributes = edge.getAttributes();

		String id = null;
		boolean directed = this.directed;
		String source = null;
		String target = null;

		while (attributes.hasNext()) {
			Attribute a = attributes.next();

			try {
				EdgeAttribute attribute = EdgeAttribute
						.valueOf(toConstantName(a));

				switch (attribute) {
				case ID:
					id = a.getValue();
					break;
				case DIRECTED:
					directed = Boolean.parseBoolean(a.getValue());
					break;
				case SOURCE:
					source = a.getValue();
					break;
				case TARGET:
					target = a.getValue();
					break;
				case SOURCEPORT:
				case TARGETPORT:
					throw new IOException(
							"sourceport and targetport not implemented");
				}
			} catch (IllegalArgumentException e) {
				throw new IOException("invalid graph attribute \""
						+ a.getName().getLocalPart() + "\"");
			}
		}

		if (id == null)
			throw new IOException("edge must have an id");

		if (source == null || target == null)
			throw new IOException("edge must have a source and a target");

		balises.peek().id = id;
		sendEdgeAdded(sourceId, id, source, target, directed);

		System.out.printf("add edge \"%s\" : \"%s\" %s\"%s\"\n", id, source,
				directed ? "-> " : "", target);
	}

	protected void nextKey(StartElement key) throws IOException {
		@SuppressWarnings("unchecked")
		Iterator<? extends Attribute> attributes = key.getAttributes();

		String id = null;
		KeyDomain domain = KeyDomain.ALL;
		KeyAttrType type = KeyAttrType.STRING;
		String name = null;

		while (attributes.hasNext()) {
			Attribute a = attributes.next();

			try {
				KeyAttribute attribute = KeyAttribute
						.valueOf(toConstantName(a));

				switch (attribute) {
				case ID:
					id = a.getValue();

					break;
				case FOR:
					try {
						domain = KeyDomain
								.valueOf(toConstantName(a.getValue()));
					} catch (IllegalArgumentException e) {
						throw new IOException("invalid key domain: \""
								+ a.getValue() + "\"");
					}

					break;
				case ATTR_TYPE:
					try {
						type = KeyAttrType
								.valueOf(toConstantName(a.getValue()));
					} catch (IllegalArgumentException e) {
						throw new IOException("invalid key type: \""
								+ a.getValue() + "\"");
					}

					break;
				case ATTR_NAME:
					name = a.getValue();

					break;
				}
			} catch (IllegalArgumentException e) {
				throw new IOException("invalid key attribute \""
						+ a.getName().getLocalPart() + "\"");
			}
		}

		if (id == null)
			throw new IOException("key must have an id");

		if (name == null)
			name = id;

		System.out.printf("add key \"%s\"\n", id);

		balises.peek().id = id;

		Key k = new Key();
		k.name = name;
		k.domain = domain;
		k.type = type;

		keys.put(id, k);
	}

	protected void nextData(StartElement data) throws IOException {
		@SuppressWarnings("unchecked")
		Iterator<? extends Attribute> attributes = data.getAttributes();

		String key = null;

		while (attributes.hasNext()) {
			Attribute a = attributes.next();

			try {
				DataAttribute attribute = DataAttribute
						.valueOf(toConstantName(a));

				switch (attribute) {
				case KEY:
					key = a.getValue();
					break;
				}
			} catch (IllegalArgumentException e) {
				throw new IOException("invalid data attribute \""
						+ a.getName().getLocalPart() + "\"");
			}
		}

		if (key == null)
			throw new IOException("data must have a key attribute");

		balises.peek().setExtra("key", key);
	}

	protected String getElementID(StartElement element) throws IOException {
		return getElementAttribute(element, "id", true);
	}

	protected String getElementAttribute(StartElement element,
			String localName, boolean required) throws IOException {
		Attribute attribute = element.getAttributeByName(QName
				.valueOf(localName));

		if (attribute == null) {
			if (required)
				throw new IOException(String.format(
						"attribute \"%s\" required for %s", localName, element
								.getName().getLocalPart()));

			return null;
		}

		return attribute.getValue();
	}

	protected void checkAncestor(Balise child) throws IOException {
		if (balises.isEmpty()) {
			if (child != Balise.GRAPHML)
				throw new IOException(
						"root of a GraphML document should be \"graphml\"");
		} else if (child == Balise.GRAPHML) {
			throw new IOException("only document root can be \"graphml\"");
		} else {
			boolean valid = true;
			String message = null;

			switch (balises.peek().name) {
			case GRAPHML: {
				switch (child) {
				case NODE:
				case EDGE:
				case HYPEREDGE:
					valid = false;
					message = "node and edge have to be include in a graph element";

					break;
				}
				break;
			}
			case GRAPH: {
				switch (child) {
				case GRAPH:
					valid = false;
					message = "graph in graph is not allowed";
					break;
				}

				break;
			}
			case NODE: {
				switch (child) {
				case DATA:
					break;
				case GRAPH:
					valid = false;
					message = "neested graph feature is not available";

					break;
				default:
					valid = false;
					message = String.format("invalid child \"%s\" for node",
							child);

					break;
				}

				break;
			}
			case EDGE: {
				switch (child) {
				case DATA:
					break;
				default:
					valid = false;
					message = String.format("invalid child \"%s\" for edge",
							child);

					break;
				}

				break;
			}
			case KEY: {
				switch (child) {
				case DEFAULT:
					break;
				default:
					valid = false;
					message = String.format("invalid child \"%s\" for key",
							child);

					break;
				}

				break;
			}
			default:
				valid = false;
				message = "unknown ancestor " + balises.peek().name;

				break;
			}

			if (!valid)
				throw new IOException(message);
		}
	}

	protected void openStream(Reader stream) throws IOException {
		if (reader != null)
			closeStream();

		try {
			reader = XMLInputFactory.newInstance().createXMLEventReader(stream);

			balises.clear();
			directed = false;
		} catch (XMLStreamException e) {
			throw new IOException(e);
		} catch (FactoryConfigurationError e) {
			throw new IOException(e);
		}
	}

	protected void closeStream() throws IOException {
		try {
			reader.close();
		} catch (XMLStreamException e) {
			throw new IOException(e);
		} finally {
			reader = null;
		}
	}

	protected String toConstantName(Attribute a) {
		return toConstantName(a.getName().getLocalPart());
	}

	protected String toConstantName(String value) {
		return value.toUpperCase().replaceAll("\\W", "_");
	}

	public static void main(String... args) throws IOException {
		FileSourceGraphML graphml = new FileSourceGraphML();
		graphml.begin("/home/raziel/workspace/gs-labs/graphml.xml");
		while (graphml.nextEvents())
			;
		graphml.end();
	}
}
