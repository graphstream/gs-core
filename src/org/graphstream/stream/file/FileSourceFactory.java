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
 * @since 2009-05-09
 * 
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * File source factory.
 * 
 * <p>
 * A graph reader factory allow to create readers according to a given file. It
 * both tries to read the start of the file to infer its type (works well for
 * file formats with a magic cookie or header), and if it fails it tries to look
 * at the file name extension.
 * </p>
 */
public class FileSourceFactory {
	/**
	 * Create a file input for the given file name.
	 * 
	 * <p>
	 * This method first tests if the file is a regular file and is readable. If so,
	 * it opens it and reads the magic cookie to test the known file formats that
	 * can be inferred from their header. If it works, it returns a file input for
	 * the format. Else it looks at the file name extension, and returns a file
	 * input for the extension. Finally if all fail, it throws a NotFoundException.
	 * </p>
	 * 
	 * <p>
	 * Notice that this method only creates the file input and does not connect it
	 * to a graph.
	 * </p>
	 * 
	 * @param fileName
	 *            Name of the graph file.
	 * @return A graph reader suitable for the fileName graph format.
	 * @throws IOException
	 *             If the file is not readable or accessible.
	 */
	public static FileSource sourceFor(String fileName) throws IOException {
		File file = new File(fileName);

		if (!file.isFile())
			throw new IOException("not a regular file '" + fileName + "'");

		if (!file.canRead())
			throw new IOException("not a readable file '" + fileName + "'");

		// Try to read the beginning of the file.

		RandomAccessFile in = new RandomAccessFile(fileName, "r");

		byte b[] = new byte[10];
		int n = in.read(b, 0, 10);

		// System.err.printf( "[" );
		// for( int i=0; i<n; ++i )
		// {
		// System.err.printf( "%c", (char)b[i] );
		// }
		// System.err.printf( "]%n" );

		in.close();

		// Surely match a DGS file, as DGS files are well done and have a
		// signature.

		if (n >= 3 && b[0] == 'D' && b[1] == 'G' && b[2] == 'S') {
			if (n >= 6 && b[3] == '0' && b[4] == '0') {
				if (b[5] == '1' || b[5] == '2') {
					return new FileSourceDGS1And2();
				} else if (b[5] == '3' || b[5] == '4') {
					return new FileSourceDGS();
				}
			}
		}

		// Maybe match a GML file as most GML files begin by the line "graph [",
		// but not sure, you may create a GML file that starts by a comment, an
		// empty line, with any kind of spaces, etc.

		if (n >= 7 && b[0] == 'g' && b[1] == 'r' && b[2] == 'a' && b[3] == 'p' && b[4] == 'h' && b[5] == ' '
				&& b[6] == '[') {
			return new org.graphstream.stream.file.FileSourceGML();
		}

		if (n >= 4 && b[0] == '(' && b[1] == 't' && b[2] == 'l' && b[3] == 'p')
			return new FileSourceTLP();

		// The web reader.

		String flc = fileName.toLowerCase();

		// If we did not found anything, we try with the filename extension ...

		if (flc.endsWith(".dgs")) {
			return new FileSourceDGS();
		}

		if (flc.endsWith(".gml") || flc.endsWith(".dgml")) {
			return new org.graphstream.stream.file.FileSourceGML();
		}

		if (flc.endsWith(".net")) {
			return new FileSourcePajek();
		}

		if (flc.endsWith(".chaco") || flc.endsWith(".graph")) {
			// return new GraphReaderChaco();
		}

		if (flc.endsWith(".dot")) {
			return new org.graphstream.stream.file.FileSourceDOT();
		}

		if (flc.endsWith(".edge")) {
			return new FileSourceEdge();
		}

		if (flc.endsWith(".lgl")) {
			return new FileSourceLGL();
		}

		if (flc.endsWith(".ncol")) {
			return new FileSourceNCol();
		}

		if (flc.endsWith(".tlp")) {
			return new FileSourceTLP();
		}

		if (flc.endsWith(".xml")) {
			String root = getXMLRootElement(fileName);

			if (root.equalsIgnoreCase("gexf"))
				return new FileSourceGEXF();

			return new FileSourceGraphML();
		}

		if (flc.endsWith(".gexf")) {
			return new FileSourceGEXF();
		}

		return null;
	}

	public static String getXMLRootElement(String fileName) throws IOException {
		FileReader stream = new FileReader(fileName);
		XMLEventReader reader;
		XMLEvent e;
		String root;

		try {
			reader = XMLInputFactory.newInstance().createXMLEventReader(stream);

			do {
				e = reader.nextEvent();
			} while (!e.isStartElement() && !e.isEndDocument());

			if (e.isEndDocument())
				throw new IOException("document ended before catching root element");

			root = e.asStartElement().getName().getLocalPart();
			reader.close();
			stream.close();

			return root;
		} catch (XMLStreamException ex) {
			throw new IOException(ex);
		} catch (FactoryConfigurationError ex) {
			throw new IOException(ex);
		}
	}
}