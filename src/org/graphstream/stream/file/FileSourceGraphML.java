/*
 * Copyright 2006 - 2015
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
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

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.graphstream.stream.SourceBase;

/**
 * GraphML is a comprehensive and easy-to-use file format for graphs. It
 * consists of a language core to describe the structural properties of a graph
 * and a flexible extension mechanism to add application-specific data. Its main
 * features include support of
 * <ul>
 * <li>directed, undirected, and mixed graphs,</li>
 * <li>hypergraphs,</li>
 * <li>hierarchical graphs,</li>
 * <li>graphical representations,</li>
 * <li>references to external data,</li>
 * <li>application-specific attribute data, and</li>
 * <li>light-weight parsers.</li>
 * </ul>
 *
 * Unlike many other file formats for graphs, GraphML does not use a custom
 * syntax. Instead, it is based on XML and hence ideally suited as a common
 * denominator for all kinds of services generating, archiving, or processing
 * graphs.
 *
 * <a href="http://graphml.graphdrawing.org/index.html">Source</a>
 */
public class FileSourceGraphML extends SourceBase implements FileSource,
	XMLStreamConstants {

	protected static enum Balise {
		GRAPHML, GRAPH, NODE, EDGE, HYPEREDGE, DESC, DATA, LOCATOR, PORT, KEY, DEFAULT
	}

	protected static enum GraphAttribute {
		ID, EDGEDEFAULT
	}

	protected static enum LocatorAttribute {
		XMLNS_XLINK, XLINK_HREF, XLINK_TYPE
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

	protected static enum PortAttribute {
		NAME
	}

	protected static enum EndPointAttribute {
		ID, NODE, PORT, TYPE
	}

	protected static enum EndPointType {
		IN, OUT, UNDIR
	}

	protected static enum HyperEdgeAttribute {
		ID
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
			if (value == null) {
				return null;
			}

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
		String id;
		String value;
	}

	protected static class Locator {
		String href;
		String xlink;
		String type;

		Locator() {
			xlink = "http://www.w3.org/TR/2000/PR-xlink-20001220/";
			type = "simple";
			href = null;
		}
	}

	protected static class Port {
		String name;
		String desc;

		LinkedList<Data> datas;
		LinkedList<Port> ports;

		Port() {
			name = null;
			desc = null;

			datas = new LinkedList<Data>();
			ports = new LinkedList<Port>();
		}
	}

	protected static class EndPoint {
		String id;
		String node;
		String port;
		String desc;
		EndPointType type;

		EndPoint() {
			id = null;
			node = null;
			port = null;
			desc = null;
			type = EndPointType.UNDIR;
		}
	}

	protected XMLEventReader reader;
	protected HashMap<String, Key> keys;
	protected LinkedList<Data> datas;
	protected Stack<XMLEvent> events;
	protected Stack<String> graphId;
	protected int graphCounter;

	/**
	 * Build a new source to parse an xml stream in GraphML format.
	 */
	public FileSourceGraphML() {
		events = new Stack<>();
		keys = new HashMap<>();
		datas = new LinkedList<>();
		graphId = new Stack<>();
		graphCounter = 0;
		sourceId = String.format("<GraphML stream %x>", System.nanoTime());
	}

	@Override
	public void readAll(String fileName) throws IOException {
		readAll(new FileReader(fileName));
	}

	@Override
	public void readAll(URL url) throws IOException {
		readAll(url.openStream());
	}

	@Override
	public void readAll(InputStream stream) throws IOException {
		readAll(new InputStreamReader(stream));
	}

	@Override
	public void readAll(Reader reader) throws IOException {
		begin(reader);
		while (nextEvents())
			;
		end();
	}

	@Override
	public void begin(String fileName) throws IOException {
		begin(new FileReader(fileName));
	}

	@Override
	public void begin(URL url) throws IOException {
		begin(url.openStream());
	}

	@Override
	public void begin(InputStream stream) throws IOException {
		begin(new InputStreamReader(stream));
	}

	@Override
	public void begin(Reader reader) throws IOException {
		openStream(reader);
	}

	@Override
	public boolean nextEvents() throws IOException {
		try {
			__graphml();
		} catch (XMLStreamException ex) {
			throw new IOException(ex);
		}

		return false;
	}

	@Override
	public boolean nextStep() throws IOException {
		return nextEvents();
	}

	@Override
	public void end() throws IOException {
		closeStream();
	}

	protected XMLEvent getNextEvent() throws IOException, XMLStreamException {
		skipWhiteSpaces();

		if (events.size() > 0) {
			return events.pop();
		}

		return reader.nextEvent();
	}

	protected void pushback(XMLEvent e) {
		events.push(e);
	}

	private XMLStreamException newParseError(XMLEvent e, String msg,
		Object... args) {
		return new XMLStreamException(String.format(msg, args), e.getLocation());
	}

	private boolean isEvent(XMLEvent e, int type, String name) {
		boolean valid = e.getEventType() == type;

		if (valid) {
			switch (type) {
				case START_ELEMENT:
					valid = e.asStartElement().getName().getLocalPart()
						.equals(name);
					break;
				case END_ELEMENT:
					valid = e.asEndElement().getName().getLocalPart().equals(name);
					break;
				case ATTRIBUTE:
					valid = ((Attribute) e).getName().getLocalPart().equals(name);
					break;
				case CHARACTERS:
				case NAMESPACE:
				case PROCESSING_INSTRUCTION:
				case COMMENT:
				case START_DOCUMENT:
				case END_DOCUMENT:
				case DTD:
			}
		}

		return valid;
	}

	private void checkValid(XMLEvent e, int type, String name)
		throws XMLStreamException {
		boolean valid = isEvent(e, type, name);

		if (!valid) {
			throw newParseError(e, "expecting %s, got %s", gotWhat(type, name),
				gotWhat(e));
		}
	}

	private String gotWhat(XMLEvent e) {
		String v = null;

		switch (e.getEventType()) {
			case START_ELEMENT:
				v = e.asStartElement().getName().getLocalPart();
				break;
			case END_ELEMENT:
				v = e.asEndElement().getName().getLocalPart();
				break;
			case ATTRIBUTE:
				v = ((Attribute) e).getName().getLocalPart();
				break;
		}

		return gotWhat(e.getEventType(), v);
	}

	private String gotWhat(int type, String v) {
		switch (type) {
			case START_ELEMENT:
				return String.format("'<%s>'", v);
			case END_ELEMENT:
				return String.format("'</%s>'", v);
			case ATTRIBUTE:
				return String.format("attribute '%s'", v);
			case NAMESPACE:
				return "namespace";
			case PROCESSING_INSTRUCTION:
				return "processing instruction";
			case COMMENT:
				return "comment";
			case START_DOCUMENT:
				return "document start";
			case END_DOCUMENT:
				return "document end";
			case DTD:
				return "dtd";
			case CHARACTERS:
				return "characters";
			default:
				return "UNKNOWN";
		}
	}

	private Object getValue(Data data) {
		switch (data.key.type) {
			case BOOLEAN:
				return Boolean.parseBoolean(data.value);
			case INT:
				return Integer.parseInt(data.value);
			case LONG:
				return Long.parseLong(data.value);
			case FLOAT:
				return Float.parseFloat(data.value);
			case DOUBLE:
				return Double.parseDouble(data.value);
			case STRING:
				return data.value;
		}

		return data.value;
	}

	private Object getDefaultValue(Key key) {
		switch (key.type) {
			case BOOLEAN:
				return Boolean.TRUE;
			case INT:
				if (key.def != null) {
					return Integer.valueOf(key.def);
				}

				return Integer.valueOf(0);
			case LONG:
				if (key.def != null) {
					return Long.valueOf(key.def);
				}

				return Long.valueOf(0);
			case FLOAT:
				if (key.def != null) {
					return Float.valueOf(key.def);
				}

				return Float.valueOf(0.0f);
			case DOUBLE:
				if (key.def != null) {
					return Double.valueOf(key.def);
				}

				return Double.valueOf(0.0);
			case STRING:
				if (key.def != null) {
					return key.def;
				}

				return "";
		}

		return key.def != null ? key.def : Boolean.TRUE;
	}

	private void skipWhiteSpaces() throws IOException, XMLStreamException {
		XMLEvent e;

		do {
			if (events.size() > 0) {
				e = events.pop();
			} else {
				e = reader.nextEvent();
			}
		} while (isEvent(e, XMLEvent.CHARACTERS, null)
			&& e.asCharacters().getData().matches("^\\s*$"));

		pushback(e);
	}

	protected void openStream(Reader stream) throws IOException {
		if (reader != null) {
			closeStream();
		}

		try {
			XMLEvent e;

			reader = XMLInputFactory.newInstance().createXMLEventReader(stream);

			e = getNextEvent();
			checkValid(e, XMLEvent.START_DOCUMENT, null);

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

	/**
	 * <pre>
	 * <!ELEMENT graphml  ((desc)?,(key)*,((data)|(graph))*)>
	 * </pre>
	 *
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	private void __graphml() throws IOException, XMLStreamException {
		XMLEvent e;

		e = getNextEvent();
		checkValid(e, XMLEvent.START_ELEMENT, "graphml");

		e = getNextEvent();

		if (isEvent(e, XMLEvent.START_ELEMENT, "desc")) {
			pushback(e);
			__desc();

			e = getNextEvent();
		}

		while (isEvent(e, XMLEvent.START_ELEMENT, "key")) {
			pushback(e);
			__key();

			e = getNextEvent();
		}

		while (isEvent(e, XMLEvent.START_ELEMENT, "data")
			|| isEvent(e, XMLEvent.START_ELEMENT, "graph")) {
			pushback(e);

			if (isEvent(e, XMLEvent.START_ELEMENT, "data")) {
				__data();
			} else {
				__graph();
			}

			e = getNextEvent();
		}

		checkValid(e, XMLEvent.END_ELEMENT, "graphml");
	}

	private String __characters() throws IOException, XMLStreamException {
		XMLEvent e;
		StringBuilder buffer = new StringBuilder();

		e = getNextEvent();

		while (e.getEventType() == XMLEvent.CHARACTERS) {
			buffer.append(e.asCharacters());
			e = getNextEvent();
		}

		pushback(e);

		return buffer.toString();
	}

	/**
	 * <pre>
	 * <!ELEMENT desc (#PCDATA)>
	 * </pre>
	 *
	 * @return @throws IOException
	 * @throws XMLStreamException
	 */
	private String __desc() throws IOException, XMLStreamException {
		XMLEvent e;
		String desc;

		e = getNextEvent();
		checkValid(e, XMLEvent.START_ELEMENT, "desc");

		desc = __characters();

		e = getNextEvent();
		checkValid(e, XMLEvent.END_ELEMENT, "desc");

		return desc;
	}

	/**
	 * <pre>
	 * <!ELEMENT locator EMPTY>
	 * <!ATTLIST locator
	 *           xmlns:xlink   CDATA    #FIXED    "http://www.w3.org/TR/2000/PR-xlink-20001220/"
	 *           xlink:href    CDATA    #REQUIRED
	 *           xlink:type    (simple) #FIXED    "simple"
	 * >
	 * </pre>
	 *
	 * @return @throws IOException
	 * @throws XMLStreamException
	 */
	private Locator __locator() throws IOException, XMLStreamException {
		XMLEvent e;

		e = getNextEvent();
		checkValid(e, XMLEvent.START_ELEMENT, "locator");

		@SuppressWarnings("unchecked")
		Iterator<? extends Attribute> attributes = e.asStartElement()
			.getAttributes();

		Locator loc = new Locator();

		while (attributes.hasNext()) {
			Attribute a = attributes.next();

			try {
				LocatorAttribute attribute = LocatorAttribute
					.valueOf(toConstantName(a));

				switch (attribute) {
					case XMLNS_XLINK:
						loc.xlink = a.getValue();
						break;
					case XLINK_HREF:
						loc.href = a.getValue();
						break;
					case XLINK_TYPE:
						loc.type = a.getValue();
						break;
				}
			} catch (IllegalArgumentException ex) {
				throw newParseError(e, "invalid locator attribute '%s'", a
					.getName().getLocalPart());
			}
		}

		e = getNextEvent();
		checkValid(e, XMLEvent.END_ELEMENT, "locator");

		if (loc.href == null) {
			throw newParseError(e, "locator requires an href");
		}

		return loc;
	}

	/**
	 * <pre>
	 * <!ELEMENT key (#PCDATA)>
	 * <!ATTLIST key
	 *           id  ID                                            #REQUIRED
	 *           for (graphml|graph|node|edge|hyperedge|port|endpoint|all) "all"
	 * >
	 * </pre>
	 *
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	private void __key() throws IOException, XMLStreamException {
		XMLEvent e;

		e = getNextEvent();
		checkValid(e, XMLEvent.START_ELEMENT, "key");

		@SuppressWarnings("unchecked")
		Iterator<? extends Attribute> attributes = e.asStartElement()
			.getAttributes();

		String id = null;
		KeyDomain domain = KeyDomain.ALL;
		KeyAttrType type = KeyAttrType.STRING;
		String name = null;
		String def = null;

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
						} catch (IllegalArgumentException ex) {
							throw newParseError(e, "invalid key domain '%s'",
								a.getValue());
						}

						break;
					case ATTR_TYPE:
						try {
							type = KeyAttrType
								.valueOf(toConstantName(a.getValue()));
						} catch (IllegalArgumentException ex) {
							throw newParseError(e, "invalid key type '%s'",
								a.getValue());
						}

						break;
					case ATTR_NAME:
						name = a.getValue();

						break;
				}
			} catch (IllegalArgumentException ex) {
				throw newParseError(e, "invalid key attribute '%s'", a
					.getName().getLocalPart());
			}
		}

		e = getNextEvent();

		if (isEvent(e, XMLEvent.START_ELEMENT, "default")) {
			def = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "default");

			e = getNextEvent();
		}

		checkValid(e, XMLEvent.END_ELEMENT, "key");

		if (id == null) {
			throw newParseError(e, "key requires an id");
		}

		if (name == null) {
			name = id;
		}

		System.out.printf("add key \"%s\"\n", id);

		Key k = new Key();
		k.name = name;
		k.domain = domain;
		k.type = type;
		k.def = def;

		keys.put(id, k);
	}

	/**
	 * <pre>
	 * <!ELEMENT port ((desc)?,((data)|(port))*)>
	 * <!ATTLIST port
	 *           name    NMTOKEN  #REQUIRED
	 * >
	 * </pre>
	 *
	 * @return @throws IOException
	 * @throws XMLStreamException
	 */
	private Port __port() throws IOException, XMLStreamException {
		XMLEvent e;

		e = getNextEvent();
		checkValid(e, XMLEvent.START_ELEMENT, "port");

		Port port = new Port();
		@SuppressWarnings("unchecked")
		Iterator<? extends Attribute> attributes = e.asStartElement()
			.getAttributes();
		while (attributes.hasNext()) {
			Attribute a = attributes.next();

			try {
				PortAttribute attribute = PortAttribute
					.valueOf(toConstantName(a));

				switch (attribute) {
					case NAME:
						port.name = a.getValue();
						break;
				}
			} catch (IllegalArgumentException ex) {
				throw newParseError(e, "invalid attribute '%s' for '<port>'", a
					.getName().getLocalPart());
			}
		}

		if (port.name == null) {
			throw newParseError(e,
				"'<port>' element requires a 'name' attribute");
		}

		e = getNextEvent();
		if (isEvent(e, XMLEvent.START_ELEMENT, "desc")) {
			pushback(e);
			port.desc = __desc();
		} else {
			while (isEvent(e, XMLEvent.START_ELEMENT, "data")
				|| isEvent(e, XMLEvent.START_ELEMENT, "port")) {
				if (isEvent(e, XMLEvent.START_ELEMENT, "data")) {
					Data data;

					pushback(e);
					data = __data();

					port.datas.add(data);
				} else {
					Port portChild;

					pushback(e);
					portChild = __port();

					port.ports.add(portChild);
				}

				e = getNextEvent();
			}
		}

		e = getNextEvent();
		checkValid(e, XMLEvent.END_ELEMENT, "port");

		return port;
	}

	/**
	 * <pre>
	 * <!ELEMENT endpoint ((desc)?)>
	 * <!ATTLIST endpoint
	 *           id    ID             #IMPLIED
	 *           node  IDREF          #REQUIRED
	 *           port  NMTOKEN        #IMPLIED
	 *           type  (in|out|undir) "undir"
	 * >
	 * </pre>
	 *
	 * @return @throws IOException
	 * @throws XMLStreamException
	 */
	private EndPoint __endpoint() throws IOException, XMLStreamException {
		XMLEvent e;

		e = getNextEvent();
		checkValid(e, XMLEvent.START_ELEMENT, "endpoint");

		@SuppressWarnings("unchecked")
		Iterator<? extends Attribute> attributes = e.asStartElement()
			.getAttributes();
		EndPoint ep = new EndPoint();

		while (attributes.hasNext()) {
			Attribute a = attributes.next();

			try {
				EndPointAttribute attribute = EndPointAttribute
					.valueOf(toConstantName(a));

				switch (attribute) {
					case NODE:
						ep.node = a.getValue();
						break;
					case ID:
						ep.id = a.getValue();
						break;
					case PORT:
						ep.port = a.getValue();
						break;
					case TYPE:
						try {
							ep.type = EndPointType.valueOf(toConstantName(a
								.getValue()));
						} catch (IllegalArgumentException ex) {
							throw newParseError(e, "invalid end point type '%s'",
								a.getValue());
						}

						break;
				}
			} catch (IllegalArgumentException ex) {
				throw newParseError(e,
					"invalid attribute '%s' for '<endpoint>'", a.getName()
					.getLocalPart());
			}
		}

		if (ep.node == null) {
			throw newParseError(e,
				"'<endpoint>' element requires a 'node' attribute");
		}

		e = getNextEvent();

		if (isEvent(e, XMLEvent.START_ELEMENT, "desc")) {
			pushback(e);
			ep.desc = __desc();
		}

		e = getNextEvent();
		checkValid(e, XMLEvent.END_ELEMENT, "endpoint");

		return ep;
	}

	/**
	 * <pre>
	 * <!ELEMENT data  (#PCDATA)>
	 * <!ATTLIST data
	 *           key      IDREF        #REQUIRED
	 *           id       ID           #IMPLIED
	 * >
	 * </pre>
	 *
	 * @return @throws IOException
	 * @throws XMLStreamException
	 */
	private Data __data() throws IOException, XMLStreamException {
		XMLEvent e;
		StringBuilder buffer = new StringBuilder();

		e = getNextEvent();
		checkValid(e, XMLEvent.START_ELEMENT, "data");

		@SuppressWarnings("unchecked")
		Iterator<? extends Attribute> attributes = e.asStartElement()
			.getAttributes();
		String key = null, id = null;

		while (attributes.hasNext()) {
			Attribute a = attributes.next();

			try {
				DataAttribute attribute = DataAttribute
					.valueOf(toConstantName(a));

				switch (attribute) {
					case KEY:
						key = a.getValue();
						break;
					case ID:
						id = a.getValue();
						break;
				}
			} catch (IllegalArgumentException ex) {
				throw newParseError(e, "invalid attribute '%s' for '<data>'", a
					.getName().getLocalPart());
			}
		}

		if (key == null) {
			throw newParseError(e,
				"'<data>' element must have a 'key' attribute");
		}

		e = getNextEvent();

		while (e.getEventType() == XMLEvent.CHARACTERS) {
			buffer.append(e.asCharacters());
			e = getNextEvent();
		}

		checkValid(e, XMLEvent.END_ELEMENT, "data");

		if (keys.containsKey(key)) {
			newParseError(e, "unknown key '%s'", key);
		}

		Data d = new Data();

		d.key = keys.get(key);
		d.id = id;
		d.value = buffer.toString();

		return d;
	}

	/**
	 * <pre>
	 * <!ELEMENT graph    ((desc)?,((((data)|(node)|(edge)|(hyperedge))*)|(locator)))>
	 * <!ATTLIST graph
	 *     id          ID                    #IMPLIED
	 *     edgedefault (directed|undirected) #REQUIRED
	 * >
	 * </pre>
	 *
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	private void __graph() throws IOException, XMLStreamException {
		XMLEvent e;

		e = getNextEvent();
		checkValid(e, XMLEvent.START_ELEMENT, "graph");

		@SuppressWarnings("unchecked")
		Iterator<? extends Attribute> attributes = e.asStartElement()
			.getAttributes();

		String id = null;
		String desc = null;
		boolean directed = false;
		boolean directedSet = false;

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
						if (a.getValue().equals("directed")) {
							directed = true;
						} else if (a.getValue().equals("undirected")) {
							directed = false;
						} else {
							throw newParseError(e,
								"invalid 'edgefault' value '%s'", a.getValue());
						}

						directedSet = true;

						break;
				}
			} catch (IllegalArgumentException ex) {
				throw newParseError(e, "invalid node attribute '%s'", a
					.getName().getLocalPart());
			}
		}

		if (!directedSet) {
			throw newParseError(e, "graph requires attribute 'edgedefault'");
		}

		String gid = "";

		if (graphId.size() > 0) {
			gid = graphId.peek() + ":";
		}

		if (id != null) {
			gid += id;
		} else {
			gid += Integer.toString(graphCounter++);
		}

		graphId.push(gid);

		e = getNextEvent();

		if (isEvent(e, XMLEvent.START_ELEMENT, "desc")) {
			pushback(e);
			desc = __desc();

			sendGraphAttributeAdded(sourceId, "desc", desc);

			e = getNextEvent();
		}

		if (isEvent(e, XMLEvent.START_ELEMENT, "locator")) {
			pushback(e);
			__locator();
			// TODO
			e = getNextEvent();
		} else {
			while (isEvent(e, XMLEvent.START_ELEMENT, "data")
				|| isEvent(e, XMLEvent.START_ELEMENT, "node")
				|| isEvent(e, XMLEvent.START_ELEMENT, "edge")
				|| isEvent(e, XMLEvent.START_ELEMENT, "hyperedge")) {
				pushback(e);

				if (isEvent(e, XMLEvent.START_ELEMENT, "data")) {
					datas.add(__data());
				} else if (isEvent(e, XMLEvent.START_ELEMENT, "node")) {
					__node();
				} else if (isEvent(e, XMLEvent.START_ELEMENT, "edge")) {
					__edge(directed);
				} else {
					__hyperedge();
				}

				e = getNextEvent();
			}
		}

		graphId.pop();
		checkValid(e, XMLEvent.END_ELEMENT, "graph");
	}

	/**
	 * <pre>
	 * <!ELEMENT node   (desc?,(((data|port)*,graph?)|locator))>
	 * <!ATTLIST node
	 *     		 id        ID      #REQUIRED
	 * >
	 * </pre>
	 *
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	private void __node() throws IOException, XMLStreamException {
		XMLEvent e;

		e = getNextEvent();
		checkValid(e, XMLEvent.START_ELEMENT, "node");

		@SuppressWarnings("unchecked")
		Iterator<? extends Attribute> attributes = e.asStartElement()
			.getAttributes();

		String id = null;
		HashSet<Key> sentAttributes = new HashSet<>();

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
			} catch (IllegalArgumentException ex) {
				throw newParseError(e, "invalid node attribute '%s'", a
					.getName().getLocalPart());
			}
		}

		if (id == null) {
			throw newParseError(e, "node requires an id");
		}

		sendNodeAdded(sourceId, id);

		e = getNextEvent();

		if (isEvent(e, XMLEvent.START_ELEMENT, "desc")) {
			String desc;

			pushback(e);
			desc = __desc();

			sendNodeAttributeAdded(sourceId, id, "desc", desc);
		} else if (isEvent(e, XMLEvent.START_ELEMENT, "locator")) {
			// TODO
			pushback(e);
			__locator();
		} else {
			while (isEvent(e, XMLEvent.START_ELEMENT, "data")
				|| isEvent(e, XMLEvent.START_ELEMENT, "port")) {
				if (isEvent(e, XMLEvent.START_ELEMENT, "data")) {
					Data data;

					pushback(e);
					data = __data();

					sendNodeAttributeAdded(sourceId, id, data.key.name,
						getValue(data));

					sentAttributes.add(data.key);
				} else {
					pushback(e);
					__port();
				}

				e = getNextEvent();
			}
		}

		for (Key k : keys.values()) {
			if ((k.domain == KeyDomain.NODE || k.domain == KeyDomain.ALL)
				&& !sentAttributes.contains(k)) {
				sendNodeAttributeAdded(sourceId, id, k.name, getDefaultValue(k));
			}
		}

		if (isEvent(e, XMLEvent.START_ELEMENT, "graph")) {
			Location loc = e.getLocation();

			System.err.printf(
				"[WARNING] %d:%d graph inside node is not implemented",
				loc.getLineNumber(), loc.getColumnNumber());

			pushback(e);
			__graph();

			e = getNextEvent();
		}

		checkValid(e, XMLEvent.END_ELEMENT, "node");
	}

	/**
	 * <pre>
	 * <!ELEMENT edge ((desc)?,(data)*,(graph)?)>
	 * <!ATTLIST edge
	 *           id         ID           #IMPLIED
	 *           source     IDREF        #REQUIRED
	 *           sourceport NMTOKEN      #IMPLIED
	 *           target     IDREF        #REQUIRED
	 *           targetport NMTOKEN      #IMPLIED
	 *           directed   (true|false) #IMPLIED
	 * >
	 * </pre>
	 *
	 * @param edgedefault
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	private void __edge(boolean edgedefault) throws IOException,
		XMLStreamException {
		XMLEvent e;

		e = getNextEvent();
		checkValid(e, XMLEvent.START_ELEMENT, "edge");

		@SuppressWarnings("unchecked")
		Iterator<? extends Attribute> attributes = e.asStartElement()
			.getAttributes();

		HashSet<Key> sentAttributes = new HashSet<>();
		String id = null;
		boolean directed = edgedefault;
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
						throw newParseError(e,
							"sourceport and targetport not implemented");
				}
			} catch (IllegalArgumentException ex) {
				throw newParseError(e, "invalid graph attribute '%s'", a
					.getName().getLocalPart());
			}
		}

		if (id == null) {
			throw newParseError(e, "edge must have an id");
		}

		if (source == null || target == null) {
			throw newParseError(e, "edge must have a source and a target");
		}

		sendEdgeAdded(sourceId, id, source, target, directed);

		e = getNextEvent();

		if (isEvent(e, XMLEvent.START_ELEMENT, "desc")) {
			String desc;

			pushback(e);
			desc = __desc();

			sendEdgeAttributeAdded(sourceId, id, "desc", desc);
		} else {
			while (isEvent(e, XMLEvent.START_ELEMENT, "data")) {
				Data data;

				pushback(e);
				data = __data();

				sendEdgeAttributeAdded(sourceId, id, data.key.name,
					getValue(data));

				sentAttributes.add(data.key);

				e = getNextEvent();
			}
		}

		for (Key k : keys.values()) {
			if ((k.domain == KeyDomain.EDGE || k.domain == KeyDomain.ALL)
				&& !sentAttributes.contains(k)) {
				sendEdgeAttributeAdded(sourceId, id, k.name, getDefaultValue(k));
			}
		}

		if (isEvent(e, XMLEvent.START_ELEMENT, "graph")) {
			Location loc = e.getLocation();

			System.err.printf(
				"[WARNING] %d:%d graph inside node is not implemented",
				loc.getLineNumber(), loc.getColumnNumber());

			pushback(e);
			__graph();

			e = getNextEvent();
		}

		checkValid(e, XMLEvent.END_ELEMENT, "edge");
	}

	/**
	 * <pre>
	 * <!ELEMENT hyperedge  ((desc)?,((data)|(endpoint))*,(graph)?)>
	 * <!ATTLIST hyperedge
	 *           id     ID      #IMPLIED
	 * >
	 * </pre>
	 *
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	private void __hyperedge() throws IOException, XMLStreamException {
		XMLEvent e;

		e = getNextEvent();
		checkValid(e, XMLEvent.START_ELEMENT, "hyperedge");

		Location loc = e.getLocation();

		System.err.printf(
			"[WARNING] %d:%d hyperedge feature is not implemented",
			loc.getLineNumber(), loc.getColumnNumber());

		String id = null;

		@SuppressWarnings("unchecked")
		Iterator<? extends Attribute> attributes = e.asStartElement()
			.getAttributes();

		while (attributes.hasNext()) {
			Attribute a = attributes.next();

			try {
				HyperEdgeAttribute attribute = HyperEdgeAttribute
					.valueOf(toConstantName(a));

				switch (attribute) {
					case ID:
						id = a.getValue();
						break;
				}
			} catch (IllegalArgumentException ex) {
				throw newParseError(e,
					"invalid attribute '%s' for '<endpoint>'", a.getName()
					.getLocalPart());
			}
		}

		if (id == null) {
			throw newParseError(e,
				"'<hyperedge>' element requires a 'node' attribute");
		}

		e = getNextEvent();

		if (isEvent(e, XMLEvent.START_ELEMENT, "desc")) {
			pushback(e);
			__desc();
		} else {
			while (isEvent(e, XMLEvent.START_ELEMENT, "data")
				|| isEvent(e, XMLEvent.START_ELEMENT, "endpoint")) {
				if (isEvent(e, XMLEvent.START_ELEMENT, "data")) {
					pushback(e);
					__data();
				} else {
					pushback(e);
					__endpoint();
				}

				e = getNextEvent();
			}
		}

		if (isEvent(e, XMLEvent.START_ELEMENT, "graph")) {
			loc = e.getLocation();

			System.err.printf(
				"[WARNING] %d:%d graph inside node is not implemented",
				loc.getLineNumber(), loc.getColumnNumber());

			pushback(e);
			__graph();

			e = getNextEvent();
		}

		e = getNextEvent();
		checkValid(e, XMLEvent.END_ELEMENT, "hyperedge");
	}
}
