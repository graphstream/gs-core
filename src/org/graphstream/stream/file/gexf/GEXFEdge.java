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

public class GEXFEdge implements GEXFElement {
	GEXF root;

	String id;
	String label;
	String source;
	String target;
	boolean directed;

	double weight = Double.NaN;

	GEXFAttValues attvalues;
	GEXFSpells spells;

	public GEXFEdge(GEXF root, String id, String source, String target, boolean directed) {
		this.root = root;

		this.id = id;
		this.label = id;
		this.source = source;
		this.target = target;
		this.directed = directed;

		spells = new GEXFSpells(root);
		attvalues = new GEXFAttValues(root);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.file.gexf.GEXFElement#export(org.graphstream.stream
	 * .file.gexf.SmartXMLWriter)
	 */
	public void export(SmartXMLWriter stream) throws XMLStreamException {
		stream.startElement("edge");

		stream.stream.writeAttribute("id", id);
		stream.stream.writeAttribute("label", label);
		stream.stream.writeAttribute("source", source);
		stream.stream.writeAttribute("target", target);
		stream.stream.writeAttribute("type", directed ? "directed" : "undirected");

		if (!Double.isNaN(weight))
			stream.stream.writeAttribute("weight", Double.toString(weight));

		if (root.isExtensionEnable(Extension.DYNAMICS))
			spells.export(stream);

		attvalues.export(stream);

		stream.endElement(); // EDGE
	}
}
