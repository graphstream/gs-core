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

import java.io.Writer;
import java.util.LinkedList;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class SmartXMLWriter {
	public final XMLStreamWriter stream;

	boolean smart;
	int depth;
	LinkedList<Integer> childrenCount;

	public SmartXMLWriter(Writer output, boolean smart) throws XMLStreamException, FactoryConfigurationError {
		stream = XMLOutputFactory.newFactory().createXMLStreamWriter(output);
		stream.writeStartDocument("UTF-8", "1.0");

		this.smart = smart;
		this.depth = 0;
		this.childrenCount = new LinkedList<Integer>();
		this.childrenCount.add(0);
	}

	public void startElement(String name) throws XMLStreamException {
		if (smart) {
			stream.writeCharacters("\n");

			for (int i = 0; i < depth; i++)
				stream.writeCharacters(" ");
		}

		childrenCount.set(0, childrenCount.get(0) + 1);
		childrenCount.addFirst(0);

		stream.writeStartElement(name);
		depth++;
	}

	public void endElement() throws XMLStreamException {
		depth--;

		boolean leaf = (childrenCount.pop() == 0);

		if (smart && !leaf) {
			stream.writeCharacters("\n");

			for (int i = 0; i < depth; i++)
				stream.writeCharacters(" ");
		}

		stream.writeEndElement();
	}

	public void leafWithText(String name, String content) throws XMLStreamException {
		startElement(name);
		stream.writeCharacters(content);
		endElement();
	}

	public void flush() {
		try {
			stream.flush();
		} catch (XMLStreamException e) {
			// Ignored
		}
	}

	public void close() throws XMLStreamException {
		stream.writeEndDocument();
		stream.flush();

		stream.close();
	}
}
