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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.xml.stream.XMLStreamException;

public interface GEXFElement {
	public static enum Extension {
		VIZ, DYNAMICS, DATA
	}

	public static enum TimeFormat {
		INTEGER("integer", new DecimalFormat("#", new DecimalFormatSymbols(Locale.ROOT))), DOUBLE("double",
				new DecimalFormat("#.0###################", new DecimalFormatSymbols(Locale.ROOT))), DATE("date",
						new SimpleDateFormat("yyyy-MM-dd")), DATETIME("datetime",
								new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZ"));
		String qname;
		Format format;

		TimeFormat(String qname, Format f) {
			this.qname = qname;
			this.format = f;
		}
	}

	public static enum DefaultEdgeType {
		UNDIRECTED("undirected"), DIRECTED("directed"), MUTUAL("mutual");

		final String qname;

		DefaultEdgeType(String qname) {
			this.qname = qname;
		}
	}

	public static enum IDType {
		INTEGER("integer"), STRING("string");

		final String qname;

		IDType(String qname) {
			this.qname = qname;
		}
	}

	public static enum Mode {
		STATIC("static"), DYNAMIC("dynamic");

		final String qname;

		Mode(String qname) {
			this.qname = qname;
		}
	}

	public static enum ClassType {
		NODE("node"), EDGE("edge");

		final String qname;

		ClassType(String qname) {
			this.qname = qname;
		}
	}

	public static enum AttrType {
		INTEGER("integer"), LONG("long"), DOUBLE("double"), FLOAT("float"), BOOLEAN("boolean"), LISTSTRING(
				"liststring"), STRING("string"), ANYURI("anyURI");

		final String qname;

		AttrType(String qname) {
			this.qname = qname;
		}
	}

	void export(SmartXMLWriter stream) throws XMLStreamException;
}
