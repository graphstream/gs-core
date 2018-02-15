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

import java.util.LinkedList;

import javax.xml.stream.XMLStreamException;

public class GEXFSpells implements GEXFElement {
	GEXF root;
	LinkedList<GEXFSpell> spells;

	public GEXFSpells(GEXF root) {
		this.root = root;
		this.spells = new LinkedList<GEXFSpell>();
	}

	public void start() {
		double date = root.step;

		if (spells.size() == 0 || spells.getLast().closed) {
			GEXFSpell spell = new GEXFSpell(root);
			spell.start = date;

			spells.add(spell);
		}
	}

	public void end() {
		double date = root.step;

		if (spells.size() > 0 && !spells.getLast().closed) {
			GEXFSpell spell = spells.getLast();

			spell.end = date;
			spell.closed = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.file.gexf.GEXFElement#export(org.graphstream.stream
	 * .file.gexf.SmartXMLWriter)
	 */
	public void export(SmartXMLWriter stream) throws XMLStreamException {
		if (spells.size() == 0)
			return;

		stream.startElement("spells");

		for (int i = 0; i < spells.size(); i++)
			spells.get(i).export(stream);

		stream.endElement(); // SPELLS
	}
}
