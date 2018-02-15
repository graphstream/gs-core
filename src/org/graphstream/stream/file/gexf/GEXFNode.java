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
 */
package org.graphstream.stream.file.gexf;

import javax.xml.stream.XMLStreamException;

public class GEXFNode implements GEXFElement {
	GEXF root;

	String id;
	String label;

	GEXFAttValues attvalues;
	GEXFSpells spells;

	//
	// VIZ Extension
	float x;
	float y;
	float z;
	boolean position;

	//

	public GEXFNode(GEXF root, String id) {
		this.root = root;

		this.id = id;
		this.label = id;

		spells = new GEXFSpells(root);
		attvalues = new GEXFAttValues(root);
		position = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.file.gexf.GEXFElement#export(org.graphstream.stream
	 * .file.gexf.SmartXMLWriter)
	 */
	public void export(SmartXMLWriter stream) throws XMLStreamException {
		stream.startElement("node");

		stream.stream.writeAttribute("id", id);
		stream.stream.writeAttribute("label", label);

		if (position && root.isExtensionEnable(Extension.VIZ)) {
			stream.startElement("viz:position");
			stream.stream.writeAttribute("x", Float.toString(x));
			stream.stream.writeAttribute("y", Float.toString(y));
			stream.stream.writeAttribute("z", Float.toString(z));
			stream.endElement(); // POSITION
		}

		attvalues.export(stream);

		if (root.isExtensionEnable(Extension.DYNAMICS))
			spells.export(stream);

		stream.endElement(); // SPELLS
	}
}
