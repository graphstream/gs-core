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

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.stream.XMLStreamException;

public class GEXFAttValues implements GEXFElement {
	GEXF root;
	HashMap<Integer, LinkedList<GEXFAttValue>> values;

	public GEXFAttValues(GEXF root) {
		this.root = root;
		this.values = new HashMap<Integer, LinkedList<GEXFAttValue>>();
	}

	public void attributeUpdated(GEXFAttribute decl, Object value) {
		if (!values.containsKey(decl.id))
			values.put(decl.id, new LinkedList<GEXFAttValue>());

		LinkedList<GEXFAttValue> attr = values.get(decl.id);

		if (value != null) {
			if (attr.size() > 0) {
				if (attr.getLast().start == root.step)
					attr.removeLast();
				else
					attr.getLast().end = root.step;
			}

			GEXFAttValue av = new GEXFAttValue(root, Integer.toString(decl.id), formatValue(value));
			attr.add(av);
		} else {
			if (attr.size() > 0)
				attr.getLast().end = root.step;
		}
	}

	String formatValue(Object o) {
		if (o == null)
			return "<null>";

		if (o.getClass().isArray()) {
			StringBuilder buffer = new StringBuilder();

			for (int i = 0; i < Array.getLength(o); i++) {
				Object ochild = Array.get(o, i);

				if (i > 0)
					buffer.append("|");

				if (ochild != null)
					buffer.append(ochild.toString());
			}

			o = buffer;
		}

		return o.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.file.gexf.GEXFElement#export(org.graphstream.stream
	 * .file.gexf.SmartXMLWriter)
	 */
	public void export(SmartXMLWriter stream) throws XMLStreamException {
		if (values.size() == 0)
			return;

		stream.startElement("attvalues");

		for (LinkedList<GEXFAttValue> attrValues : values.values()) {
			for (int i = 0; i < attrValues.size(); i++)
				attrValues.get(i).export(stream);
		}

		stream.endElement(); // ATTVALUES
	}
}
