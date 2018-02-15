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
 */
package org.graphstream.stream.file;

import java.io.Reader;

import org.graphstream.stream.file.dot.DOTParser;
import org.graphstream.util.parser.Parser;
import org.graphstream.util.parser.ParserFactory;

/**
 * Graph reader for GraphViz "dot" files.
 * 
 * In this format, edges have no identifier. By default an automatic identifier
 * is added to each edge. You can add an identifier to an edge by adding an "id"
 * attribute to the edge. For example :
 * 
 * <pre>
 * A -- B [ id=AB ];
 * </pre>
 */
public class FileSourceDOT extends FileSourceParser {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSourceParser#getNewFactory()
	 */
	public ParserFactory getNewParserFactory() {
		return new ParserFactory() {
			public Parser newParser(Reader reader) {
				return new DOTParser(FileSourceDOT.this, reader);
			}
		};
	}
}
