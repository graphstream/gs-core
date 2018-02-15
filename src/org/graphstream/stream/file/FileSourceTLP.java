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
 * @since 2011-07-21
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file;

import java.io.Reader;

import org.graphstream.stream.file.tlp.TLPParser;
import org.graphstream.util.parser.Parser;
import org.graphstream.util.parser.ParserFactory;

/**
 * Source for the Tulip file format (TLP).
 * 
 * TLP files begins with :
 * 
 * <pre>
 * (tlp "2.0"
 * ; file content
 * )
 * </pre>
 * 
 * Some meta informations can be done :
 * 
 * <pre>
 * (tlp "2.0"
 * 	(author "author name")
 *  (date "...")
 *  (comments "...")
 *  ...
 * )
 * </pre>
 * 
 * Node indexes are integer. They can be declared in a "nodes" tag :
 * 
 * <pre>
 * 	(tlp "2.0"
 * 		(nodes 1 2 3)
 *  	(edge 1 1 2)
 *  	(edge 2 1 3)
 *  	(edge 3 2 3)
 * 	)
 * </pre>
 * 
 * Then edge can be defined with an int index followed by the index of the
 * source node and the target nodes.
 * 
 * Clusters can be created with an index and a name:
 * 
 * <pre>
 * 	(tlp "2.0"
 * 		(nodes 1 2 3)
 *  	(edge 1 1 2)
 *  	(edge 2 1 3)
 *  	(edge 3 2 3)
 *  	(cluster 1 "cluster name"
 *  		(nodes 1 3)
 *  		(edges 2)
 *  	)
 * 	)
 * </pre>
 * 
 * Cluster 0 is the root graph.
 * 
 * Properties can be applied to cluster:
 * 
 * <pre>
 * 	(tlp "2.0"
 * 		(nodes 1 2 3)
 *  	(edge 1 1 2)
 *  	(edge 2 1 3)
 *  	(edge 3 2 3)
 *  	(property cluster_id type "name"
 *  		(default "node_default" "edge_default")
 *  		(node node_id "value")
 *  		(edge edge_id "value")
 *  	)
 * 	)
 * </pre>
 * 
 * Type of properties can be one of :
 * <ul>
 * <li>bool</li>
 * <li>double</li>
 * <li>int</li>
 * <li>string</li>
 * <li>color</li>
 * <li>layout</li>
 * <li>size</li>
 * </ul>
 */
public class FileSourceTLP extends FileSourceParser {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSourceParser#getNewFactory()
	 */
	public ParserFactory getNewParserFactory() {
		return new ParserFactory() {
			public Parser newParser(Reader reader) {
				return new TLPParser(FileSourceTLP.this, reader);
			}
		};
	}
}
