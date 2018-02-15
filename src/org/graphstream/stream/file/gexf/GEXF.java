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

import java.util.HashSet;

import javax.xml.stream.XMLStreamException;

import org.graphstream.stream.PipeBase;

public class GEXF extends PipeBase implements GEXFElement {
	public static final String XMLNS = "http://www.gexf.net/1.2draft";
	public static final String XMLNS_XSI = "http://www.w3.org/2001/XMLSchema-instance";
	public static final String XMLNS_SL = "http://www.gexf.net/1.2draft http://www.gexf.net/1.2draft/gexf.xsd";
	public static final String XMLNS_VIZ = "http://www.gexf.net/1.2draft/viz";

	public static final String VERSION = "1.2";

	GEXFMeta meta;
	GEXFGraph graph;

	int currentAttributeIndex;

	double step;

	HashSet<Extension> extensions;

	TimeFormat timeFormat;

	public GEXF() {
		meta = new GEXFMeta();
		currentAttributeIndex = 0;
		step = 0;
		graph = new GEXFGraph(this);
		timeFormat = TimeFormat.DOUBLE;

		extensions = new HashSet<Extension>();
		extensions.add(Extension.DATA);
		extensions.add(Extension.DYNAMICS);
		extensions.add(Extension.VIZ);
	}

	public TimeFormat getTimeFormat() {
		return timeFormat;
	}

	public boolean isExtensionEnable(Extension ext) {
		return extensions.contains(ext);
	}

	public void disable(Extension ext) {
		extensions.remove(ext);
	}

	public void enable(Extension ext) {
		extensions.add(ext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.file.gexf.GEXFElement#export(org.graphstream.stream
	 * .file.gexf.SmartXMLWriter)
	 */
	public void export(SmartXMLWriter stream) throws XMLStreamException {
		stream.startElement("gexf");

		stream.stream.writeAttribute("xmlns", XMLNS);
		stream.stream.writeAttribute("xmlns:xsi", XMLNS_XSI);

		if (isExtensionEnable(Extension.VIZ))
			stream.stream.writeAttribute("xmlns:viz", XMLNS_VIZ);

		stream.stream.writeAttribute("xsi:schemaLocation", XMLNS_SL);
		stream.stream.writeAttribute("version", VERSION);

		meta.export(stream);
		graph.export(stream);

		stream.endElement(); // GEXF
	}

	int getNewAttributeIndex() {
		return currentAttributeIndex++;
	}

	GEXFAttribute getNodeAttribute(String key) {
		return graph.nodesAttributes.attributes.get(key);
	}

	GEXFAttribute getEdgeAttribute(String key) {
		return graph.edgesAttributes.attributes.get(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.PipeBase#stepBegins(java.lang.String, long,
	 * double)
	 */
	public void stepBegins(String sourceId, long timeId, double step) {
		this.step = step;
		super.stepBegins(sourceId, timeId, step);
	}
}
