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
 * @since 2011-09-21
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.logging.Logger;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.graphstream.stream.SourceBase;

/**
 * Base for XML-based file format. It uses an xml events stream (
 * {@link javax.xml.stream}). One who want to define a new xml-based fiel source
 * has to define actions after the document start and before the document end.
 * The {@link #nextEvents()}, called between start and end, has to be defined
 * too.
 *
 * @author Guilhelm Savin
 */
public abstract class FileSourceXML extends SourceBase implements FileSource, XMLStreamConstants {
	private static final Logger LOGGER = Logger.getLogger(FileSourceXML.class.getName());

	/**
	 * XML events stream. Should not be used directly but with
	 * {@link #getNextEvent()}.
	 */
	protected XMLEventReader reader;
	/*
	 * Used to allow 'pushback' of events.
	 */
	private Stack<XMLEvent> events;

	protected boolean strictMode = true;

	protected FileSourceXML() {
		events = new Stack<>();
	}

	/**
	 * If strict mode is enabled, will produce errors while encountering unexpected
	 * attributes or elements. This is enabled by default.
	 *
	 * @return true if strict mode is enabled
	 */
	public boolean isStrictMode() {
		return strictMode;
	}

	public void setStrictMode(boolean strictMode) {
		this.strictMode = strictMode;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.file.FileSource#readAll(java.lang.String)
	 */
	public void readAll(String fileName) throws IOException {
		readAll(new FileReader(fileName));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.file.FileSource#readAll(java.net.URL)
	 */
	public void readAll(URL url) throws IOException {
		readAll(url.openStream());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.file.FileSource#readAll(java.io.InputStream)
	 */
	public void readAll(InputStream stream) throws IOException {
		readAll(new InputStreamReader(stream));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.file.FileSource#readAll(java.io.Reader)
	 */
	public void readAll(Reader reader) throws IOException {
		begin(reader);
		while (nextEvents())
			;
		end();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.file.FileSource#begin(java.lang.String)
	 */
	public void begin(String fileName) throws IOException {
		begin(new FileReader(fileName));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.file.FileSource#begin(java.net.URL)
	 */
	public void begin(URL url) throws IOException {
		begin(url.openStream());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.file.FileSource#begin(java.io.InputStream)
	 */
	public void begin(InputStream stream) throws IOException {
		begin(new InputStreamReader(stream));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.file.FileSource#begin(java.io.Reader)
	 */
	public void begin(Reader reader) throws IOException {
		openStream(reader);
	}

	/**
	 * Called after the event
	 * {@link javax.xml.stream.events.XMLEvent#START_DOCUMENT} has been received.
	 *
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	protected abstract void afterStartDocument() throws IOException, XMLStreamException;

	/**
	 * Called before trying to receive the events
	 * {@link javax.xml.stream.events.XMLEvent#END_DOCUMENT}.
	 *
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	protected abstract void beforeEndDocument() throws IOException, XMLStreamException;

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.file.FileSource#nextEvents()
	 */
	public abstract boolean nextEvents() throws IOException;

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.file.FileSource#nextStep()
	 */
	public boolean nextStep() throws IOException {
		return nextEvents();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.file.FileSource#end()
	 */
	public void end() throws IOException {
		closeStream();
	}

	/**
	 * Get a new event from the stream. This method has to be used to allow the
	 * {@link #pushback(XMLEvent)} method to work.
	 *
	 * @return the next event in the stream
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	protected XMLEvent getNextEvent() throws IOException, XMLStreamException {
		skipWhiteSpaces();

		if (events.size() > 0)
			return events.pop();

		return reader.nextEvent();
	}

	/**
	 * Pushback an event in the stream.
	 *
	 * @param e
	 *            the event
	 */
	protected void pushback(XMLEvent e) {
		events.push(e);
	}

	/**
	 * Generate a new parse exception.
	 *
	 * @param e
	 *            event producing an error
	 * @param critical
	 *            if true, will always produce an exception, else if strict mode is
	 *            disable, will only produce a warning
	 * @param msg
	 *            message to put in the exception
	 * @param args
	 *            arguments of the message
	 */
	protected void newParseError(XMLEvent e, boolean critical, String msg, Object... args) throws XMLStreamException {
		if (!critical && !strictMode) {
			LOGGER.warning(String.format(msg, args));
		} else {
			throw new XMLStreamException(String.format(msg, args), e.getLocation());
		}
	}

	/**
	 * Check is an event has an expected type and name.
	 *
	 * @param e
	 *            event to check
	 * @param type
	 *            expected type
	 * @param name
	 *            expected name
	 * @return true is type and name are valid
	 */
	protected boolean isEvent(XMLEvent e, int type, String name) {
		boolean valid = e.getEventType() == type;

		if (valid) {
			switch (type) {
			case START_ELEMENT:
				valid = e.asStartElement().getName().getLocalPart().equals(name);
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

	/**
	 * Check is the event has valid type and name. If not, a new exception is
	 * thrown.
	 *
	 * @param e
	 *            event to check
	 * @param type
	 *            expected type
	 * @param name
	 *            expected name
	 * @throws XMLStreamException
	 *             if event has invalid type or name
	 */
	protected void checkValid(XMLEvent e, int type, String name) throws XMLStreamException {
		boolean valid = isEvent(e, type, name);

		if (!valid)
			newParseError(e, true, "expecting %s, got %s", gotWhat(type, name), gotWhat(e));
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

	private void skipWhiteSpaces() throws IOException, XMLStreamException {
		XMLEvent e;

		do {
			if (events.size() > 0)
				e = events.pop();
			else
				e = reader.nextEvent();
		} while (isEvent(e, XMLEvent.CHARACTERS, null) && e.asCharacters().getData().matches("^\\s*$"));

		pushback(e);
	}

	/**
	 * Open a new xml events stream.
	 *
	 * @param stream
	 * @throws IOException
	 */
	protected void openStream(Reader stream) throws IOException {
		if (reader != null)
			closeStream();

		try {
			XMLEvent e;

			reader = XMLInputFactory.newInstance().createXMLEventReader(stream);

			e = getNextEvent();
			checkValid(e, XMLEvent.START_DOCUMENT, null);

			afterStartDocument();
		} catch (XMLStreamException e) {
			throw new IOException(e);
		} catch (FactoryConfigurationError e) {
			throw new IOException(e);
		}
	}

	/**
	 * Close the current opened stream.
	 *
	 * @throws IOException
	 */
	protected void closeStream() throws IOException {
		try {
			beforeEndDocument();
			reader.close();
		} catch (XMLStreamException e) {
			throw new IOException(e);
		} finally {
			reader = null;
		}
	}

	/**
	 * Convert an attribute to a valid constant name.
	 *
	 * @param a
	 * @return
	 * @see #toConstantName(String)
	 */
	protected String toConstantName(Attribute a) {
		return toConstantName(a.getName().getLocalPart());
	}

	/**
	 * Convert a string to a valid constant name. String is put to upper case and
	 * all non-word characters are replaced by '_'.
	 *
	 * @param value
	 * @return
	 */
	protected String toConstantName(String value) {
		return value.toUpperCase().replaceAll("\\W", "_");
	}

	/**
	 * Base for parsers, providing some usefull features.
	 */
	protected class Parser {
		/**
		 * Read a sequence of characters and return these characters as a string.
		 * Characters are read until a non-character event is reached.
		 *
		 * @return a sequence of characters
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		protected String __characters() throws IOException, XMLStreamException {
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
		 * Get attributes of a start element in a map. Attributes should be described in
		 * an enumeration such that {@link FileSourceXML#toConstantName(Attribute)}
		 * correspond to names of enumeration constants.
		 *
		 * @param <T>
		 *            type of the enumeration describing attributes
		 * @param cls
		 *            class of the enumeration T
		 * @param e
		 *            start event from which attributes have to be extracted
		 * @return a mapping between enum constants and attribute values.
		 */
		protected <T extends Enum<T>> EnumMap<T, String> getAttributes(Class<T> cls, StartElement e) {
			EnumMap<T, String> values = new EnumMap<T, String>(cls);

			@SuppressWarnings("unchecked")
			Iterator<? extends Attribute> attributes = e.asStartElement().getAttributes();

			while (attributes.hasNext()) {
				Attribute a = attributes.next();

				for (int i = 0; i < cls.getEnumConstants().length; i++) {
					if (cls.getEnumConstants()[i].name().equals(toConstantName(a))) {
						values.put(cls.getEnumConstants()[i], a.getValue());
						break;
					}
				}
			}

			return values;
		}

		/**
		 * Check if all required attributes are present.
		 *
		 * @param <T>
		 *            type of the enumeration describing attributes
		 * @param e
		 *            the event
		 * @param attributes
		 *            extracted attributes
		 * @param required
		 *            array of required attributes
		 * @throws XMLStreamException
		 *             if at least one required attribute is not found
		 */
		protected <T extends Enum<T>> void checkRequiredAttributes(XMLEvent e, EnumMap<T, String> attributes,
				T... required) throws XMLStreamException {
			if (required != null) {
				for (int i = 0; i < required.length; i++) {
					if (!attributes.containsKey(required[i]))
						newParseError(e, true, "'%s' attribute is required for <%s> element",
								required[i].name().toLowerCase(), e.asStartElement().getName().getLocalPart());
				}
			}
		}
	}
}
