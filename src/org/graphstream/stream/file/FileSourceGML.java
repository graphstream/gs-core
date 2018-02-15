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
 * @since 2009-02-19
 * 
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file;

import java.io.IOException;
import java.io.Reader;

import org.graphstream.stream.file.gml.GMLParser;

import org.graphstream.util.parser.ParseException;
import org.graphstream.util.parser.Parser;
import org.graphstream.util.parser.ParserFactory;

/**
 * A GML parser.
 * 
 * This parser should understand the whole GML syntax. It transforms any unknown
 * tag into an attribute. Depending on the location of the unknown tag, the
 * attribute is added to the graph, to nodes or to the edges.
 * 
 * The "graphics" attributes are, as far as possible, transformed into
 * "ui.style" attributes that are merged with the style sheet. The understood
 * graphics tags are "x", "y", "z", "w", "h", "d" for position and size, "fill"
 * for the background color (becomes "fill-color"), "outline" (becomes
 * "stroke-color"), "type" (becomes "shape", the known shapes being the ones of
 * the GraphStream CSS, plus the "ellipse" tag wich maps to "circle" and the
 * "rectangle" tag that maps to "box"), "outline_width" (becomes "stroke-width",
 * in pixels).
 * 
 * If edges have no "id" tag, the id is the concatenation of the source and
 * target node identifiers separated by a "_" character and a random number.
 * 
 * You can declare nodes either with the full declaration:
 * 
 * <pre>
 * 		node [ Id "foo" ]
 * </pre>
 * 
 * Which is useful when adding attributes to it. Or you can use a lighter
 * declaration with:
 * 
 * <pre>
 *      node "foo"
 * </pre>
 * 
 * You can also remove nodes and edges by using:
 * 
 * <pre>
 *      -node "foo"
 *      del-node "foo"
 *      -node [ Id "foo" ]
 *      del-node [ Id "foo" ]
 * </pre>
 * 
 * And the same for edges with "-edge" or "del-edge".
 * 
 * All the dynamic events of GraphStream are supported as an extension.
 * 
 * You can add or remove attributes to or from a node or edge using a minus sign
 * in front of the attribute name and following the attribute name by [].
 * 
 * You can remove a node or edge using a minus sign in front of the node and
 * edge tags:
 * 
 * <pre>
 *     -node [ id "foo" ]
 * </pre>
 * 
 * Or
 * 
 * <pre>
 *     -node "foo"
 * </pre>
 * 
 * You can change the attributes of a node or edge using a plus sign in front of
 * the node and edge tags:
 * 
 * <pre>
 *     +node [ id "foo" someAttribute "added" -removedAttribute [] ]
 * </pre>
 * 
 * Be careful, that files exported with the dynamic extensions will not be
 * compatible with most GML readers of other programs.
 * 
 * The standard extension for GML files is ".gml". If your file contains dynamic
 * additions, you can use the ".dgml" (Dynamic GML) extensions. The parser will
 * handle both dynamic and non dynamic files with the extension ".gml".
 */
public class FileSourceGML extends FileSourceParser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSourceParser#nextStep()
	 */
	public boolean nextStep() throws IOException {
		try {
			return ((GMLParser) parser).step();
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSourceParser#getNewParserFactory()
	 */
	public ParserFactory getNewParserFactory() {
		return new ParserFactory() {
			public Parser newParser(Reader reader) {
				return new GMLParser(FileSourceGML.this, reader);
			}
		};
	}
}
