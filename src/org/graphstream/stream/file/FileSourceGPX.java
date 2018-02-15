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
 * @since 2012-02-09
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * Source to read GPX (GPS eXchange Format) data an XML extension to exchange
 * gps coordinates, routes and tracks.
 * 
 * Read more about GPX at
 * <a href="https://en.wikipedia.org/wiki/GPS_eXchange_Format">Wikipedia</a>
 * 
 */
public class FileSourceGPX extends FileSourceXML {

	/**
	 * Parser used by this source.
	 */
	protected GPXParser parser;

	public FileSourceGPX() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSourceXML#afterStartDocument()
	 */
	protected void afterStartDocument() throws IOException, XMLStreamException {
		parser = new GPXParser();
		parser.__gpx();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSourceXML#beforeEndDocument()
	 */
	protected void beforeEndDocument() throws IOException, XMLStreamException {
		parser = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSourceXML#nextEvents()
	 */
	public boolean nextEvents() throws IOException {
		return false;
	}

	protected class WayPoint {
		String name;
		double lat, lon, ele;
		HashMap<String, Object> attributes;

		WayPoint() {
			attributes = new HashMap<String, Object>();
			name = null;
			lat = lon = ele = 0;
		}

		void deploy() {
			sendNodeAdded(sourceId, name);
			sendNodeAttributeAdded(sourceId, name, "xyz", new double[] { lon, lat, ele });

			for (String key : attributes.keySet())
				sendNodeAttributeAdded(sourceId, name, key, attributes.get(key));
		}
	}

	protected class GPXParser extends Parser implements GPXConstants {

		int automaticPointId;
		int automaticRouteId;
		int automaticEdgeId;

		GPXParser() {
			automaticRouteId = 0;
			automaticPointId = 0;
			automaticEdgeId = 0;
		}

		/**
		 * Base for read points since points can be one of "wpt", "rtept", "trkpt".
		 * 
		 * @param elementName
		 * @return
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private WayPoint waypoint(String elementName) throws IOException, XMLStreamException {
			XMLEvent e;
			WayPoint wp = new WayPoint();
			EnumMap<WPTAttribute, String> attributes;
			LinkedList<String> links = new LinkedList<String>();

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, elementName);

			attributes = getAttributes(WPTAttribute.class, e.asStartElement());

			if (!attributes.containsKey(WPTAttribute.LAT)) {
				newParseError(e, false, "attribute 'lat' is required");
			}

			if (!attributes.containsKey(WPTAttribute.LON)) {
				newParseError(e, false, "attribute 'lon' is required");
			}

			wp.lat = Double.parseDouble(attributes.get(WPTAttribute.LAT));
			wp.lon = Double.parseDouble(attributes.get(WPTAttribute.LON));
			wp.ele = 0;

			wp.attributes.put("lat", wp.lat);
			wp.attributes.put("lon", wp.lon);

			e = getNextEvent();

			if (isEvent(e, XMLEvent.START_ELEMENT, "ele")) {
				pushback(e);
				wp.ele = __ele();
				wp.attributes.put("ele", wp.ele);

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "time")) {
				pushback(e);
				wp.attributes.put("time", __time());

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "magvar")) {
				pushback(e);
				wp.attributes.put("magvar", __magvar());

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "geoidheight")) {
				pushback(e);
				wp.attributes.put("geoidheight", __geoidheight());

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "name")) {
				pushback(e);
				wp.name = __name();

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "cmt")) {
				pushback(e);
				wp.attributes.put("cmt", __cmt());

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "desc")) {
				pushback(e);
				wp.attributes.put("desc", __desc());

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "src")) {
				pushback(e);
				wp.attributes.put("src", __src());

				e = getNextEvent();
			}

			while (isEvent(e, XMLEvent.START_ELEMENT, "link")) {
				pushback(e);
				links.add(__link());

				e = getNextEvent();
			}

			wp.attributes.put("link", links.toArray(new String[links.size()]));

			if (isEvent(e, XMLEvent.START_ELEMENT, "sym")) {
				pushback(e);
				wp.attributes.put("sym", __sym());

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "type")) {
				pushback(e);
				wp.attributes.put("type", __type());

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "fix")) {
				pushback(e);
				wp.attributes.put("fix", __fix());

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "sat")) {
				pushback(e);
				wp.attributes.put("sat", __sat());

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "hdop")) {
				pushback(e);
				wp.attributes.put("hdop", __hdop());

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "vdop")) {
				pushback(e);
				wp.attributes.put("vdop", __vdop());

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "pdop")) {
				pushback(e);
				wp.attributes.put("pdop", __pdop());

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "ageofdgpsdata")) {
				pushback(e);
				wp.attributes.put("ageofdgpsdata", __ageofdgpsdata());

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "dgpsid")) {
				pushback(e);
				wp.attributes.put("dgpsid", __dgpsid());

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "extensions")) {
				pushback(e);
				__extensions();

				e = getNextEvent();
			}

			checkValid(e, XMLEvent.END_ELEMENT, elementName);

			if (wp.name == null)
				wp.name = String.format("wp#%08x", automaticPointId++);

			return wp;
		}

		/**
		 * <pre>
		 * name       : GPX
		 * attributes : GPXAttribute
		 * structure  : METADATA? WPT* RTE* TRK* EXTENSIONS?
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private void __gpx() throws IOException, XMLStreamException {
			XMLEvent e;
			EnumMap<GPXAttribute, String> attributes;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "gpx");

			attributes = getAttributes(GPXAttribute.class, e.asStartElement());

			if (!attributes.containsKey(GPXAttribute.VERSION)) {
				newParseError(e, false, "attribute 'version' is required");
			} else {
				sendGraphAttributeAdded(sourceId, "gpx.version", attributes.get(GPXAttribute.VERSION));
			}

			if (!attributes.containsKey(GPXAttribute.CREATOR)) {
				newParseError(e, false, "attribute 'creator' is required");
			} else {
				sendGraphAttributeAdded(sourceId, "gpx.creator", attributes.get(GPXAttribute.CREATOR));
			}

			e = getNextEvent();

			if (isEvent(e, XMLEvent.START_ELEMENT, "metadata")) {
				pushback(e);
				__metadata();

				e = getNextEvent();
			}

			while (isEvent(e, XMLEvent.START_ELEMENT, "wpt")) {
				pushback(e);
				__wpt();

				e = getNextEvent();
			}

			while (isEvent(e, XMLEvent.START_ELEMENT, "rte")) {
				pushback(e);
				__rte();

				e = getNextEvent();
			}

			while (isEvent(e, XMLEvent.START_ELEMENT, "trk")) {
				pushback(e);
				__trk();

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "extensions")) {
				pushback(e);
				__extensions();

				e = getNextEvent();
			}

			checkValid(e, XMLEvent.END_ELEMENT, "gpx");
		}

		/**
		 * <pre>
		 * name       : METADATA
		 * attributes : 
		 * structure  : NAME? DESC? AUTHOR? COPYRIGHT? LINK* TIME? KEYWORDS? BOUNDS? EXTENSIONS?
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private void __metadata() throws IOException, XMLStreamException {
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "metadata");

			e = getNextEvent();

			if (isEvent(e, XMLEvent.START_ELEMENT, "name")) {
				pushback(e);
				sendGraphAttributeAdded(sourceId, "gpx.metadata.name", __name());

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "desc")) {
				pushback(e);
				sendGraphAttributeAdded(sourceId, "gpx.metadata.desc", __desc());

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "author")) {
				pushback(e);
				sendGraphAttributeAdded(sourceId, "gpx.metadata.author", __author());

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "copyright")) {
				pushback(e);
				sendGraphAttributeAdded(sourceId, "gpx.metadata.copyright", __copyright());

				e = getNextEvent();
			}

			LinkedList<String> links = new LinkedList<String>();

			while (isEvent(e, XMLEvent.START_ELEMENT, "link")) {
				pushback(e);
				links.add(__link());

				e = getNextEvent();
			}

			if (links.size() > 0)
				sendGraphAttributeAdded(sourceId, "gpx.metadata.links", links.toArray(new String[links.size()]));

			if (isEvent(e, XMLEvent.START_ELEMENT, "time")) {
				pushback(e);
				sendGraphAttributeAdded(sourceId, "gpx.metadata.time", __time());

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "keywords")) {
				pushback(e);
				sendGraphAttributeAdded(sourceId, "gpx.metadata.keywords", __keywords());

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "bounds")) {
				pushback(e);
				__bounds();

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "extensions")) {
				pushback(e);
				__extensions();

				e = getNextEvent();
			}

			checkValid(e, XMLEvent.END_ELEMENT, "metadata");
		}

		/**
		 * <pre>
		 * name       : WPT
		 * attributes : WPTAttribute
		 * structure  : ELE? TIME? MAGVAR? GEOIDHEIGHT? NAME? CMT? DESC? SRC? LINK* SYM? TYPE? FIX? SAT? HDOP? VDOP? PDOP? AGEOFDGPSDATA? DGPSID? EXTENSIONS?
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private void __wpt() throws IOException, XMLStreamException {
			WayPoint wp = waypoint("wpt");
			wp.deploy();
		}

		/**
		 * <pre>
		 * name       : RTE
		 * attributes : 
		 * structure  : NAME? CMT? DESC? SRC? LINK* NUMBER? TYPE? EXTENSIONS? RTEPT*
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private void __rte() throws IOException, XMLStreamException {
			XMLEvent e;
			String name, cmt, desc, src, type, time;
			int number;
			LinkedList<String> links = new LinkedList<String>();
			LinkedList<WayPoint> points = new LinkedList<WayPoint>();

			name = cmt = desc = src = type = time = null;
			number = -1;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "rte");

			e = getNextEvent();

			if (isEvent(e, XMLEvent.START_ELEMENT, "name")) {
				pushback(e);
				name = __name();

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "time")) {
				pushback(e);
				time = __time();

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "cmt")) {
				pushback(e);
				cmt = __cmt();

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "desc")) {
				pushback(e);
				desc = __desc();

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "src")) {
				pushback(e);
				src = __src();

				e = getNextEvent();
			}

			while (isEvent(e, XMLEvent.START_ELEMENT, "link")) {
				pushback(e);
				links.add(__link());

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "number")) {
				pushback(e);
				number = __number();

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "type")) {
				pushback(e);
				type = __type();

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "extensions")) {
				pushback(e);
				__extensions();

				e = getNextEvent();
			}

			while (isEvent(e, XMLEvent.START_ELEMENT, "rtept")) {
				pushback(e);
				points.addLast(__rtept());

				e = getNextEvent();
			}

			checkValid(e, XMLEvent.END_ELEMENT, "rte");

			if (name == null)
				name = String.format("route#%08x", automaticRouteId++);

			sendGraphAttributeAdded(sourceId, "routes." + name, Boolean.TRUE);
			sendGraphAttributeAdded(sourceId, "routes." + name + ".desc", desc);
			sendGraphAttributeAdded(sourceId, "routes." + name + ".cmt", cmt);
			sendGraphAttributeAdded(sourceId, "routes." + name + ".src", src);
			sendGraphAttributeAdded(sourceId, "routes." + name + ".type", type);
			sendGraphAttributeAdded(sourceId, "routes." + name + ".time", time);
			sendGraphAttributeAdded(sourceId, "routes." + name + ".number", number);

			for (int i = 0; i < points.size(); i++) {
				points.get(i).deploy();

				if (i > 0) {
					String eid = String.format("seg#%08x", automaticEdgeId++);
					sendEdgeAdded(sourceId, eid, points.get(i - 1).name, points.get(i).name, true);
					sendEdgeAttributeAdded(sourceId, eid, "route", name);
				}
			}
		}

		/**
		 * <pre>
		 * name       : TRK
		 * attributes : 
		 * structure  : NAME? CMT? DESC? SRC? LINK* NUMBER? TYPE? EXTENSIONS? TRKSEG*
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private void __trk() throws IOException, XMLStreamException {
			XMLEvent e;
			String name, cmt, desc, src, type, time;
			int number;
			LinkedList<String> links = new LinkedList<String>();

			name = cmt = desc = src = type = time = null;
			number = -1;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "trk");

			e = getNextEvent();

			if (isEvent(e, XMLEvent.START_ELEMENT, "name")) {
				pushback(e);
				name = __name();

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "time")) {
				pushback(e);
				time = __time();

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "cmt")) {
				pushback(e);
				cmt = __cmt();

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "desc")) {
				pushback(e);
				desc = __desc();

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "src")) {
				pushback(e);
				src = __src();

				e = getNextEvent();
			}

			while (isEvent(e, XMLEvent.START_ELEMENT, "link")) {
				pushback(e);
				links.add(__link());

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "number")) {
				pushback(e);
				number = __number();

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "type")) {
				pushback(e);
				type = __type();

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "extensions")) {
				pushback(e);
				__extensions();

				e = getNextEvent();
			}

			if (name == null)
				name = String.format("route#%08x", automaticRouteId++);

			sendGraphAttributeAdded(sourceId, "tracks." + name, Boolean.TRUE);
			sendGraphAttributeAdded(sourceId, "tracks." + name + ".desc", desc);
			sendGraphAttributeAdded(sourceId, "tracks." + name + ".cmt", cmt);
			sendGraphAttributeAdded(sourceId, "tracks." + name + ".src", src);
			sendGraphAttributeAdded(sourceId, "tracks." + name + ".type", type);
			sendGraphAttributeAdded(sourceId, "tracks." + name + ".time", time);
			sendGraphAttributeAdded(sourceId, "tracks." + name + ".number", number);

			while (isEvent(e, XMLEvent.START_ELEMENT, "trkseg")) {
				pushback(e);
				List<WayPoint> wps = __trkseg();

				for (int i = 0; i < wps.size(); i++) {
					wps.get(i).deploy();

					if (i > 0) {
						String eid = String.format("seg#%08x", automaticEdgeId++);
						sendEdgeAdded(sourceId, eid, wps.get(i - 1).name, wps.get(i).name, true);
						sendEdgeAttributeAdded(sourceId, eid, "route", name);
					}
				}

				e = getNextEvent();
			}

			checkValid(e, XMLEvent.END_ELEMENT, "trk");
		}

		/**
		 * <pre>
		 * name       : EXTENSIONS
		 * attributes : 
		 * structure  :
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private void __extensions() throws IOException, XMLStreamException {
			XMLEvent e;
			int stack = 0;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "extensions");

			e = getNextEvent();

			while (!(isEvent(e, XMLEvent.END_ELEMENT, "extensions") && stack == 0)) {
				if (isEvent(e, XMLEvent.END_ELEMENT, "extensions"))
					stack--;
				else if (isEvent(e, XMLEvent.START_ELEMENT, "extensions"))
					stack++;

				e = getNextEvent();
			}
		}

		/**
		 * <pre>
		 * name       : NAME
		 * attributes : 
		 * structure  : string
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private String __name() throws IOException, XMLStreamException {
			String name;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "name");

			name = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "name");

			return name;
		}

		/**
		 * <pre>
		 * name       : DESC
		 * attributes : 
		 * structure  : string
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private String __desc() throws IOException, XMLStreamException {
			String desc;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "desc");

			desc = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "desc");

			return desc;
		}

		/**
		 * <pre>
		 * name       : AUTHOR
		 * attributes : 
		 * structure  : NAME? EMAIL? LINK?
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private String __author() throws IOException, XMLStreamException {
			String author = "";
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "author");

			e = getNextEvent();

			if (isEvent(e, XMLEvent.START_ELEMENT, "name")) {
				pushback(e);
				author += __name();

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "email")) {
				pushback(e);
				author += " <" + __email() + ">";

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "link")) {
				pushback(e);
				author += " (" + __link() + ")";

				e = getNextEvent();
			}

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "author");

			return author;
		}

		/**
		 * <pre>
		 * name       : COPYRIGHT
		 * attributes : COPYRIGHTAttribute
		 * structure  : YEAR? LICENCE?
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private String __copyright() throws IOException, XMLStreamException {
			String copyright;
			XMLEvent e;
			EnumMap<COPYRIGHTAttribute, String> attributes;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "copyright");

			attributes = getAttributes(COPYRIGHTAttribute.class, e.asStartElement());

			if (!attributes.containsKey(COPYRIGHTAttribute.AUTHOR)) {
				newParseError(e, false, "attribute 'author' is required");
				copyright = "unknown";
			} else
				copyright = attributes.get(COPYRIGHTAttribute.AUTHOR);

			e = getNextEvent();

			if (isEvent(e, XMLEvent.START_ELEMENT, "year")) {
				pushback(e);
				copyright += " " + __year();

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "license")) {
				pushback(e);
				copyright += " " + __license();

				e = getNextEvent();
			}

			checkValid(e, XMLEvent.END_ELEMENT, "copyright");

			return copyright;
		}

		/**
		 * <pre>
		 * name       : LINK
		 * attributes : LINKAttribute
		 * structure  : TEXT? TYPE?
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private String __link() throws IOException, XMLStreamException {
			String link;
			XMLEvent e;
			EnumMap<LINKAttribute, String> attributes;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "link");

			attributes = getAttributes(LINKAttribute.class, e.asStartElement());

			if (!attributes.containsKey(LINKAttribute.HREF)) {
				newParseError(e, false, "attribute 'href' is required");
				link = "unknown";
			} else
				link = attributes.get(LINKAttribute.HREF);

			e = getNextEvent();

			if (isEvent(e, XMLEvent.START_ELEMENT, "text")) {
				pushback(e);
				__text();

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "type")) {
				pushback(e);
				__type();

				e = getNextEvent();
			}

			checkValid(e, XMLEvent.END_ELEMENT, "link");

			return link;
		}

		/**
		 * <pre>
		 * name       : TIME
		 * attributes : 
		 * structure  : string
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private String __time() throws IOException, XMLStreamException {
			String time;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "time");

			time = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "time");

			return time;
		}

		/**
		 * <pre>
		 * name       : KEYWORDS
		 * attributes : 
		 * structure  : string
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private String __keywords() throws IOException, XMLStreamException {
			String keywords;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "keywords");

			keywords = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "keywords");

			return keywords;
		}

		/**
		 * <pre>
		 * name       : BOUNDS
		 * attributes : BOUNDSAttribute
		 * structure  :
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private void __bounds() throws IOException, XMLStreamException {
			XMLEvent e;
			EnumMap<BOUNDSAttribute, String> attributes;
			double minlat, maxlat, minlon, maxlon;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "bounds");

			attributes = getAttributes(BOUNDSAttribute.class, e.asStartElement());

			if (!attributes.containsKey(BOUNDSAttribute.MINLAT)) {
				newParseError(e, false, "attribute 'minlat' is required");
			}

			if (!attributes.containsKey(BOUNDSAttribute.MAXLAT)) {
				newParseError(e, false, "attribute 'maxlat' is required");
			}

			if (!attributes.containsKey(BOUNDSAttribute.MINLON)) {
				newParseError(e, false, "attribute 'minlon' is required");
			}

			if (!attributes.containsKey(BOUNDSAttribute.MAXLON)) {
				newParseError(e, false, "attribute 'maxlon' is required");
			}

			minlat = Double.parseDouble(attributes.get(BOUNDSAttribute.MINLAT));
			maxlat = Double.parseDouble(attributes.get(BOUNDSAttribute.MAXLAT));
			minlon = Double.parseDouble(attributes.get(BOUNDSAttribute.MINLON));
			maxlon = Double.parseDouble(attributes.get(BOUNDSAttribute.MAXLON));

			sendGraphAttributeAdded(sourceId, "gpx.bounds", new double[] { minlat, minlon, maxlat, maxlon });

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "bounds");
		}

		/**
		 * <pre>
		 * name       : ELE
		 * attributes : 
		 * structure  : double
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private double __ele() throws IOException, XMLStreamException {
			String ele;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "ele");

			ele = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "ele");

			return Double.parseDouble(ele);
		}

		/**
		 * <pre>
		 * name       : MAGVAR
		 * attributes : 
		 * structure  : double in [0,360]
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private double __magvar() throws IOException, XMLStreamException {
			String magvar;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "magvar");

			magvar = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "magvar");

			return Double.parseDouble(magvar);
		}

		/**
		 * <pre>
		 * name       : GEOIDHEIGHT
		 * attributes : 
		 * structure  : double
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private double __geoidheight() throws IOException, XMLStreamException {
			String geoidheight;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "geoidheight");

			geoidheight = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "geoidheight");

			return Double.parseDouble(geoidheight);
		}

		/**
		 * <pre>
		 * name       : CMT
		 * attributes : 
		 * structure  : string
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private String __cmt() throws IOException, XMLStreamException {
			String cmt;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "cmt");

			cmt = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "cmt");

			return cmt;
		}

		/**
		 * <pre>
		 * name       : SRC
		 * attributes : 
		 * structure  : string
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private String __src() throws IOException, XMLStreamException {
			String src;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "src");

			src = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "src");

			return src;
		}

		/**
		 * <pre>
		 * name       : SYM
		 * attributes : 
		 * structure  : string
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private String __sym() throws IOException, XMLStreamException {
			String sym;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "sym");

			sym = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "sym");

			return sym;
		}

		/**
		 * <pre>
		 * name       : TEXT
		 * attributes : 
		 * structure  : string
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private String __text() throws IOException, XMLStreamException {
			String text;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "text");

			text = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "text");

			return text;
		}

		/**
		 * <pre>
		 * name       : TYPE
		 * attributes : 
		 * structure  : string
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private String __type() throws IOException, XMLStreamException {
			String type;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "type");

			type = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "type");

			return type;
		}

		/**
		 * <pre>
		 * name       : FIX
		 * attributes : 
		 * structure  : enum FixType
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private String __fix() throws IOException, XMLStreamException {
			String fix;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "fix");

			fix = __characters();

			if (!fix.toLowerCase().matches("^(none|2d|3d|dgps|pps)$"))
				newParseError(e, true, "invalid fix type, expecting one of 'none', '2d', '3d', 'dgps', 'pps'");

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "fix");

			return fix;
		}

		/**
		 * <pre>
		 * name       : SAT
		 * attributes : 
		 * structure  : positive integer
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private int __sat() throws IOException, XMLStreamException {
			String sat;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "sat");

			sat = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "sat");

			return Integer.parseInt(sat);
		}

		/**
		 * <pre>
		 * name       : HDOP
		 * attributes : 
		 * structure  : double
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private double __hdop() throws IOException, XMLStreamException {
			String hdop;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "hdop");

			hdop = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "hdop");

			return Double.parseDouble(hdop);
		}

		/**
		 * <pre>
		 * name       : VDOP
		 * attributes : 
		 * structure  : double
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private double __vdop() throws IOException, XMLStreamException {
			String vdop;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "vdop");

			vdop = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "vdop");

			return Double.parseDouble(vdop);
		}

		/**
		 * <pre>
		 * name       : PDOP
		 * attributes : 
		 * structure  : double
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private double __pdop() throws IOException, XMLStreamException {
			String pdop;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "pdop");

			pdop = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "pdop");

			return Double.parseDouble(pdop);
		}

		/**
		 * <pre>
		 * name       : AGEOFDGPSDATA
		 * attributes : 
		 * structure  : double
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private double __ageofdgpsdata() throws IOException, XMLStreamException {
			String ageofdgpsdata;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "ageofdgpsdata");

			ageofdgpsdata = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "ageofdgpsdata");

			return Double.parseDouble(ageofdgpsdata);
		}

		/**
		 * <pre>
		 * name       : DGPSID
		 * attributes : 
		 * structure  : integer in [0,1023]
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private int __dgpsid() throws IOException, XMLStreamException {
			String dgpsid;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "dgpsid");

			dgpsid = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "dgpsid");

			return Integer.parseInt(dgpsid);
		}

		/**
		 * <pre>
		 * name       : NUMBER
		 * attributes : 
		 * structure  : positive integer
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private int __number() throws IOException, XMLStreamException {
			String number;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "number");

			number = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "number");

			return Integer.parseInt(number);
		}

		/**
		 * <pre>
		 * name       : RTEPT
		 * attributes : 
		 * structure  : __wptType
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private WayPoint __rtept() throws IOException, XMLStreamException {
			return waypoint("rtept");
		}

		/**
		 * <pre>
		 * name       : TRKPT
		 * attributes : 
		 * structure  : __wptType
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private WayPoint __trkpt() throws IOException, XMLStreamException {
			return waypoint("trkpt");
		}

		/**
		 * <pre>
		 * name       : TRKSEG
		 * attributes : 
		 * structure  : TRKPT* EXTENSIONS?
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private List<WayPoint> __trkseg() throws IOException, XMLStreamException {
			LinkedList<WayPoint> points = new LinkedList<WayPoint>();
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "trkseg");

			e = getNextEvent();

			while (isEvent(e, XMLEvent.START_ELEMENT, "trkpt")) {
				pushback(e);
				points.addLast(__trkpt());

				e = getNextEvent();
			}

			if (isEvent(e, XMLEvent.START_ELEMENT, "extensions")) {
				pushback(e);
				__extensions();

				e = getNextEvent();
			}

			checkValid(e, XMLEvent.END_ELEMENT, "trkseg");

			return points;
		}

		/**
		 * <pre>
		 * name       : EMAIL
		 * attributes : EMAILAttribute
		 * structure  :
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private String __email() throws IOException, XMLStreamException {
			XMLEvent e;
			EnumMap<EMAILAttribute, String> attributes;
			String email = "";

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "email");

			attributes = getAttributes(EMAILAttribute.class, e.asStartElement());

			if (!attributes.containsKey(EMAILAttribute.ID)) {
				newParseError(e, false, "attribute 'version' is required");
			} else
				email += attributes.get(EMAILAttribute.ID);

			email += "@";

			if (!attributes.containsKey(EMAILAttribute.DOMAIN)) {
				newParseError(e, false, "attribute 'version' is required");
			} else
				email += attributes.get(EMAILAttribute.DOMAIN);

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "email");

			return email;
		}

		/**
		 * <pre>
		 * name       : YEAR
		 * attributes : 
		 * structure  : string
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private String __year() throws IOException, XMLStreamException {
			String year;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "year");

			year = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "year");

			return year;
		}

		/**
		 * <pre>
		 * name       : LICENSE
		 * attributes : 
		 * structure  : string
		 * </pre>
		 * 
		 * @throws IOException
		 * @throws XMLStreamException
		 */
		private String __license() throws IOException, XMLStreamException {
			String license;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "license");

			license = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "license");

			return license;
		}
	}

	public static interface GPXConstants {
		public static enum Balise {
			GPX, METADATA, WPT, RTE, TRK, EXTENSIONS, NAME, DESC, AUTHOR, COPYRIGHT, LINK, TIME, KEYWORDS, BOUNDS, ELE, MAGVAR, GEOIDHEIGHT, CMT, SRC, SYM, TYPE, FIX, SAT, HDOP, VDOP, PDOP, AGEOFDGPSDATA, DGPSID, NUMBER, RTEPT, TRKSEG, TRKPT, YEAR, LICENCE, TEXT, EMAIL, PT
		}

		public static enum GPXAttribute {
			CREATOR, VERSION
		}

		public static enum WPTAttribute {
			LAT, LON
		}

		public static enum LINKAttribute {
			HREF
		}

		public static enum EMAILAttribute {
			ID, DOMAIN
		}

		public static enum PTAttribute {
			LAT, LON
		}

		public static enum BOUNDSAttribute {
			MINLAT, MAXLAT, MINLON, MAXLON
		}

		public static enum COPYRIGHTAttribute {
			AUTHOR
		}

		public static enum FixType {
			T_NONE, T_2D, T_3D, T_DGPS, T_PPS
		}
	}
}
