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

import javax.xml.stream.XMLStreamException;

public class GEXFGraph implements GEXFElement {
	GEXF root;

	DefaultEdgeType defaultEdgeType;
	IDType idType;
	Mode mode;

	GEXFAttributes nodesAttributes;
	GEXFAttributes edgesAttributes;
	GEXFElement nodes;
	GEXFElement edges;

	public GEXFGraph(GEXF root) {
		this(root, Mode.DYNAMIC);
	}

	public GEXFGraph(GEXF root, Mode mode) {
		this.root = root;

		this.defaultEdgeType = DefaultEdgeType.UNDIRECTED;
		this.idType = IDType.STRING;
		this.mode = mode;

		nodesAttributes = new GEXFAttributes(root, ClassType.NODE);
		edgesAttributes = new GEXFAttributes(root, ClassType.EDGE);
		nodes = new GEXFNodes(root);
		edges = new GEXFEdges(root);
	}

	public void export(SmartXMLWriter stream) throws XMLStreamException {
		Mode realMode = mode;

		if (!root.isExtensionEnable(Extension.DYNAMICS))
			realMode = Mode.STATIC;

		stream.startElement("graph");

		stream.stream.writeAttribute("idtype", idType.qname);
		stream.stream.writeAttribute("mode", realMode.qname);
		stream.stream.writeAttribute("defaultedgetype", defaultEdgeType.qname);

		if (root.isExtensionEnable(Extension.DYNAMICS))
			stream.stream.writeAttribute("timeformat", root.getTimeFormat().qname);

		nodesAttributes.export(stream);
		edgesAttributes.export(stream);
		nodes.export(stream);
		edges.export(stream);

		stream.endElement(); // GRAPH
	}

}
