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
 * @since 2012-03-26
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 */
package org.graphstream.stream.file;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.function.Consumer;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.StyleGroup;
import org.graphstream.ui.graphicGraph.StyleGroupSet;
import org.graphstream.ui.graphicGraph.stylesheet.Color;
import org.graphstream.ui.graphicGraph.stylesheet.Colors;
import org.graphstream.ui.graphicGraph.stylesheet.Selector;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.graphicGraph.stylesheet.Value;
import org.graphstream.ui.graphicGraph.stylesheet.Values;
import org.graphstream.ui.graphicGraph.stylesheet.Selector.Type;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.StrokeMode;
import org.graphstream.ui.graphicGraph.stylesheet.StyleSheet;

public class FileSinkSVG2 implements FileSink {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSink#begin(java.lang.String)
	 */
	public void begin(String fileName) throws IOException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSink#begin(java.io.OutputStream)
	 */
	public void begin(OutputStream stream) throws IOException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSink#begin(java.io.Writer)
	 */
	public void begin(Writer writer) throws IOException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSink#end()
	 */
	public void end() throws IOException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSink#flush()
	 */
	public void flush() throws IOException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.file.FileSink#writeAll(org.graphstream.graph.Graph ,
	 * java.lang.String)
	 */
	public void writeAll(Graph graph, String fileName) throws IOException {
		FileWriter out = new FileWriter(fileName);
		writeAll(graph, out);
		out.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.file.FileSink#writeAll(org.graphstream.graph.Graph ,
	 * java.io.OutputStream)
	 */
	public void writeAll(Graph graph, OutputStream stream) throws IOException {
		OutputStreamWriter out = new OutputStreamWriter(stream);
		writeAll(graph, out);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.file.FileSink#writeAll(org.graphstream.graph.Graph ,
	 * java.io.Writer)
	 */
	public void writeAll(Graph g, Writer w) throws IOException {
		XMLWriter out = new XMLWriter();
		SVGContext ctx = new SVGContext();

		try {
			out.start(w);
		} catch (XMLStreamException e) {
			throw new IOException(e);
		} catch (FactoryConfigurationError e) {
			throw new RuntimeException(e);
		}

		try {
			ctx.init(out, g);
			ctx.writeElements(out, g);
			ctx.end(out);
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}

		try {
			out.end();
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}

	private static String d(double d) {
		return String.format(Locale.ROOT, "%f", d);
	}

	private static double getX(Node n) {
		if (n.hasNumber("x"))
			return n.getNumber("x");

		if (n.hasArray("xy")) {
			Object[] xy = n.getArray("xy");

			if (xy != null && xy.length > 0 && xy[0] instanceof Number)
				return ((Number) xy[0]).doubleValue();
		}

		if (n.hasArray("xyz")) {
			Object[] xyz = n.getArray("xyz");

			if (xyz != null && xyz.length > 0 && xyz[0] instanceof Number)
				return ((Number) xyz[0]).doubleValue();
		}

		System.err.printf("[WARNING] no x attribute for node \"%s\" %s\n", n.getId(), n.hasAttribute("xyz"));

		return Math.random();
	}

	private static double getY(Node n) {
		if (n.hasNumber("y"))
			return n.getNumber("y");

		if (n.hasArray("xy")) {
			Object[] xy = n.getArray("xy");

			if (xy != null && xy.length > 1 && xy[1] instanceof Number)
				return ((Number) xy[1]).doubleValue();
		}

		if (n.hasArray("xyz")) {
			Object[] xyz = n.getArray("xyz");

			if (xyz != null && xyz.length > 1 && xyz[1] instanceof Number)
				return ((Number) xyz[1]).doubleValue();
		}

		return Math.random();
	}

	private static String getSize(Value v) {
		String u = v.units.name().toLowerCase();
		return String.format(Locale.ROOT, "%f%s", v.value, u);
	}

	private static String getSize(Values v, int index) {
		String u = v.units.name().toLowerCase();
		return String.format(Locale.ROOT, "%f%s", v.get(index), u);
	}

	static class SVGContext {
		StyleGroupSet groups;
		StyleSheet stylesheet;
		HashMap<StyleGroup, SVGStyle> svgStyles;
		ViewBox viewBox;

		public SVGContext() {
			stylesheet = new StyleSheet();
			groups = new StyleGroupSet(stylesheet);
			svgStyles = new HashMap<StyleGroup, SVGStyle>();
			viewBox = new ViewBox(0, 0, 1000, 1000);
		}

		public void init(XMLWriter out, Graph g) throws IOException, XMLStreamException {
			if (g.hasAttribute("ui.stylesheet")) {
				stylesheet.load(((String) g.getAttribute("ui.stylesheet")));
			}

			groups.addElement(g);
			viewBox.compute(g, groups.getStyleFor(g));

			out.open("svg");
			out.attribute("xmlns", "http://www.w3.org/2000/svg");
			out.attribute("xmlns:dc", "http://purl.org/dc/elements/1.1/");
			out.attribute("xmlns:cc", "http://creativecommons.org/ns#");
			out.attribute("xmlns:rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
			out.attribute("xmlns:svg", "http://www.w3.org/2000/svg");

			out.attribute("viewBox",
					String.format(Locale.ROOT, "%f %f %f %f", viewBox.x1, viewBox.y1, viewBox.x2, viewBox.y2));

			out.attribute("id", g.getId());
			out.attribute("version", "1.1");

			try {
				g.edges().forEach(e -> {
					groups.addElement(e);

					if (e.hasAttribute("ui.style")) {
						try {
							stylesheet.parseStyleFromString(new Selector(Type.EDGE, e.getId(), null),
									(String) e.getAttribute("ui.style"));
						} catch (IOException ex) {
							throw new RuntimeException(ex);
						}
					}

					groups.checkElementStyleGroup(e);
				});

				g.nodes().forEach(n -> {
					groups.addElement(n);

					if (n.hasAttribute("ui.style")) {
						try {
							stylesheet.parseStyleFromString(new Selector(Type.NODE, n.getId(), null),
									(String) n.getAttribute("ui.style"));
						} catch (IOException ex) {
							throw new RuntimeException(ex);
						}
					}

					groups.checkElementStyleGroup(n);
				});
			} catch (RuntimeException e) {
				if (e.getCause() instanceof IOException)
					throw (IOException) e.getCause();

				if (e.getCause() instanceof XMLStreamException)
					throw (IOException) e.getCause();
			}

			for (StyleGroup group : groups.groups())
				svgStyles.put(group, new SVGStyle(group));

			out.open("defs");
			for (SVGStyle svgStyle : svgStyles.values())
				svgStyle.writeDef(out);
			out.close();
		}

		public void end(XMLWriter out) throws XMLStreamException {
			out.close();
		}

		public void writeElements(XMLWriter out, Graph g) throws XMLStreamException {
			out.open("g");
			out.attribute("id", "graph-misc");
			writeElement(out, g);
			out.close();

			Iterator<HashSet<StyleGroup>> it = groups.getZIterator();

			out.open("g");
			out.attribute("id", "elements");

			while (it.hasNext()) {
				HashSet<StyleGroup> set = it.next();

				for (StyleGroup sg : set)
					for (Element e : sg.elements())
						writeElement(out, e);
			}

			out.close();
		}

		public void writeElement(XMLWriter out, Element e) throws XMLStreamException {
			String id = "";
			SVGStyle style = null;
			String transform = null;

			if (e instanceof Edge) {
				id = String.format("egde-%s", e.getId());
				style = svgStyles.get(groups.getStyleFor((Edge) e));
			} else if (e instanceof Node) {
				id = String.format("node-%s", e.getId());
				style = svgStyles.get(groups.getStyleFor((Node) e));
				transform = String.format(Locale.ROOT, "translate(%f,%f)", viewBox.convertX((Node) e),
						viewBox.convertY((Node) e));
			} else if (e instanceof Graph) {
				id = "graph-background";
				style = svgStyles.get(groups.getStyleFor((Graph) e));
			}

			out.open("g");
			out.attribute("id", id);
			out.open("path");

			if (style != null)
				out.attribute("style", style.getElementStyle(e));

			if (transform != null)
				out.attribute("transform", transform);

			out.attribute("d", getPath(e, style));
			out.close();

			if (e.hasLabel("label"))
				writeElementText(out, (String) e.getAttribute("label"), e, style.group);

			out.close();
		}

		public void writeElementText(XMLWriter out, String text, Element e, StyleGroup style)
				throws XMLStreamException {
			if (style == null || style.getTextVisibilityMode() != StyleConstants.TextVisibilityMode.HIDDEN) {
				double x, y;

				x = 0;
				y = 0;

				if (e instanceof Node) {
					x = viewBox.convertX((Node) e);
					y = viewBox.convertY((Node) e);
				} else if (e instanceof Edge) {
					Node n0, n1;

					n0 = ((Edge) e).getNode0();
					n1 = ((Edge) e).getNode0();

					x = viewBox.convertX((getX(n0) + getX(n1)) / 2);
					y = viewBox.convertY((getY(n0) + getY(n1)) / 2);
				}

				out.open("g");
				out.open("text");
				out.attribute("x", d(x));
				out.attribute("y", d(y));

				if (style != null) {
					if (style.getTextColorCount() > 0)
						out.attribute("fill", toHexColor(style.getTextColor(0)));

					switch (style.getTextAlignment()) {
					case CENTER:
						out.attribute("text-anchor", "middle");
						out.attribute("alignment-baseline", "central");
						break;
					case LEFT:
						out.attribute("text-anchor", "start");
						break;
					case RIGHT:
						out.attribute("text-anchor", "end");
						break;
					default:
						break;
					}

					switch (style.getTextSize().units) {
					case PX:
					case GU:
						out.attribute("font-size", d(style.getTextSize().value));
						break;
					case PERCENTS:
						out.attribute("font-size", d(style.getTextSize().value) + "%");
						break;
					}

					if (style.getTextFont() != null)
						out.attribute("font-family", style.getTextFont());

					switch (style.getTextStyle()) {
					case NORMAL:
						break;
					case ITALIC:
						out.attribute("font-style", "italic");
						break;
					case BOLD:
						out.attribute("font-weight", "bold");
						break;
					case BOLD_ITALIC:
						out.attribute("font-weight", "bold");
						out.attribute("font-style", "italic");
						break;
					}
				}

				out.characters(text);
				out.close();
				out.close();
			}
		}

		public String getPath(Element e, SVGStyle style) {
			StringBuilder buffer = new StringBuilder();

			if (e instanceof Node) {
				double sx, sy;
				Values size = style.group.getSize();

				sx = getValue(size.get(0), size.units, true);

				if (size.getValueCount() > 1)
					sy = getValue(size.get(1), size.units, false);
				else
					sy = getValue(size.get(0), size.units, false);

				switch (style.group.getShape()) {
				case ROUNDED_BOX:
					double rx, ry;

					rx = Math.min(5, sx / 2);
					ry = Math.min(5, sy / 2);

					concat(buffer, " m ", d(-sx / 2 + rx), " ", d(-sy / 2));
					concat(buffer, " h ", d(sx - 2 * rx));
					concat(buffer, " a ", d(rx), ",", d(ry), " 0 0 1 ", d(rx), ",", d(ry));
					concat(buffer, " v ", d(sy - 2 * ry));
					concat(buffer, " a ", d(rx), ",", d(ry), " 0 0 1 -", d(rx), ",", d(ry));
					concat(buffer, " h ", d(-sx + 2 * rx));
					concat(buffer, " a ", d(rx), ",", d(ry), " 0 0 1 -", d(rx), ",-", d(ry));
					concat(buffer, " v ", d(-sy + 2 * ry));
					concat(buffer, " a ", d(rx), ",", d(ry), " 0 0 1 ", d(rx), "-", d(ry));
					concat(buffer, " z");
					break;
				case BOX:
					concat(buffer, " m ", d(-sx / 2), " ", d(-sy / 2));
					concat(buffer, " h ", d(sx));
					concat(buffer, " v ", d(sy));
					concat(buffer, " h ", d(-sx));
					concat(buffer, " z");
					break;
				case DIAMOND:
					concat(buffer, " m ", d(-sx / 2), " 0");
					concat(buffer, " l ", d(sx / 2), " ", d(-sy / 2));
					concat(buffer, " l ", d(sx / 2), " ", d(sy / 2));
					concat(buffer, " l ", d(-sx / 2), " ", d(sy / 2));
					concat(buffer, " z");
					break;
				case TRIANGLE:
					concat(buffer, " m ", d(0), " ", d(-sy / 2));
					concat(buffer, " l ", d(sx / 2), " ", d(sy));
					concat(buffer, " h ", d(-sx));
					concat(buffer, " z");
					break;
				default:
				case CIRCLE:
					concat(buffer, " m ", d(-sx / 2), " 0");
					concat(buffer, " a ", d(sx / 2), ",", d(sy / 2), " 0 1 0 ", d(sx), ",0");
					concat(buffer, " ", d(sx / 2), ",", d(sy / 2), " 0 1 0 -", d(sx), ",0");
					concat(buffer, " z");
					break;
				}
			} else if (e instanceof Graph) {
				concat(buffer, " M ", d(viewBox.x1), " ", d(viewBox.y1));
				concat(buffer, " L ", d(viewBox.x2), " ", d(viewBox.y1));
				concat(buffer, " L ", d(viewBox.x2), " ", d(viewBox.y2));
				concat(buffer, " L ", d(viewBox.x1), " ", d(viewBox.y2));
				concat(buffer, " Z");
			} else if (e instanceof Edge) {
				Node src, trg;

				double x1, y1;
				double x2, y2;

				src = ((Edge) e).getSourceNode();
				trg = ((Edge) e).getTargetNode();

				x1 = viewBox.convertX(src);
				y1 = viewBox.convertY(src);
				x2 = viewBox.convertX(trg);
				y2 = viewBox.convertY(trg);

				concat(buffer, " M ", d(x1), " ", d(y1));
				concat(buffer, " L ", d(x2), " ", d(y2));
			}

			return buffer.toString();
		}

		public double getValue(Value v, boolean horizontal) {
			return getValue(v.value, v.units, horizontal);
		}

		public double getValue(double d, StyleConstants.Units units, boolean horizontal) {
			switch (units) {
			case PX:
				// TODO
				return d;
			case GU:
				// TODO
				return d;
			case PERCENTS:
				if (horizontal)
					return (viewBox.x2 - viewBox.x1) * d / 100.0;
				else
					return (viewBox.y2 - viewBox.y1) * d / 100.0;
			}

			return d;
		}
	}

	static class ViewBox {
		double x1, y1, x2, y2;
		double x3, y3, x4, y4;

		double[] padding = { 0, 0 };

		ViewBox(double x1, double y1, double x2, double y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}

		void compute(Graph g, StyleGroup style) {
			x3 = y3 = Double.MAX_VALUE;
			x4 = y4 = Double.MIN_VALUE;

			g.nodes().forEach(n -> {
				x3 = Math.min(x3, getX(n));
				y3 = Math.min(y3, getY(n));

				x4 = Math.max(x4, getX(n));
				y4 = Math.max(y4, getY(n));
			});

			Values v = style.getPadding();

			if (v.getValueCount() > 0) {
				padding[0] = v.get(0);
				padding[1] = v.getValueCount() > 1 ? v.get(1) : v.get(0);
			}
		}

		double convertX(double x) {
			return (x2 - x1 - 2 * padding[0]) * (x - x3) / (x4 - x3) + x1 + padding[0];
		}

		double convertX(Node n) {
			return convertX(getX(n));
		}

		double convertY(double y) {
			return (y2 - y1 - 2 * padding[1]) * (y - y3) / (y4 - y3) + y1 + padding[1];
		}

		double convertY(Node n) {
			return convertY(getY(n));
		}
	}

	static class SVGStyle {

		static int gradientId = 0;

		String style;
		StyleGroup group;
		boolean gradient;
		boolean dynfill;

		public SVGStyle(StyleGroup group) throws XMLStreamException {

			this.group = group;
			this.gradient = false;
			this.dynfill = false;

			switch (group.getType()) {
			case EDGE:
				buildEdgeStyle();
				break;
			case NODE:
				buildNodeStyle();
				break;
			case GRAPH:
				buildGraphStyle();
				break;
			case SPRITE:
			default:
				break;
			}
		}

		void buildNodeStyle() {
			StringBuilder styleSB = new StringBuilder();

			switch (group.getFillMode()) {
			case GRADIENT_RADIAL:
			case GRADIENT_HORIZONTAL:
			case GRADIENT_VERTICAL:
			case GRADIENT_DIAGONAL1:
			case GRADIENT_DIAGONAL2:
				concat(styleSB, "fill:url(#%gradient-id%);");
				this.gradient = true;
				break;
			case PLAIN:
				concat(styleSB, "fill:", toHexColor(group.getFillColor(0)), ";");
				concat(styleSB, "fill-opacity:", d(group.getFillColor(0).getAlpha() / 255.0), ";");
				break;
			case DYN_PLAIN:
				dynfill = true;
				concat(styleSB, "fill:%fill-color%;");
				concat(styleSB, "fill-opacity:%fill-opacity%;");
				break;
			case IMAGE_TILED:
			case IMAGE_SCALED:
			case IMAGE_SCALED_RATIO_MAX:
			case IMAGE_SCALED_RATIO_MIN:
			case NONE:
				break;
			}

			concat(styleSB, "fill-rule:nonzero;");

			if (group.getStrokeMode() != StrokeMode.NONE) {
				concat(styleSB, "stroke:", toHexColor(group.getStrokeColor(0)), ";");
				concat(styleSB, "stroke-width:", getSize(group.getStrokeWidth()), ";");
			}

			style = styleSB.toString();
		}

		void buildGraphStyle() {
			buildNodeStyle();
		}

		void buildEdgeStyle() {
			StringBuilder styleSB = new StringBuilder();

			switch (group.getFillMode()) {
			case GRADIENT_RADIAL:
			case GRADIENT_HORIZONTAL:
			case GRADIENT_VERTICAL:
			case GRADIENT_DIAGONAL1:
			case GRADIENT_DIAGONAL2:
				concat(styleSB, "stroke:url(#%gradient-id%);");
				this.gradient = true;
				break;
			case PLAIN:
			case DYN_PLAIN:
				concat(styleSB, "stroke:", toHexColor(group.getFillColor(0)), ";");
				break;
			case IMAGE_TILED:
			case IMAGE_SCALED:
			case IMAGE_SCALED_RATIO_MAX:
			case IMAGE_SCALED_RATIO_MIN:
			case NONE:
				break;
			}

			concat(styleSB, "stroke-width:", getSize(group.getSize(), 0), ";");

			style = styleSB.toString();
		}

		public void writeDef(XMLWriter out) throws XMLStreamException {
			if (gradient) {
				String gid = String.format("gradient%x", gradientId++);
				String type = "linearGradient";
				String x1 = null, x2 = null, y1 = null, y2 = null;

				switch (group.getFillMode()) {
				case GRADIENT_RADIAL:
					type = "radialGradient";
					break;
				case GRADIENT_HORIZONTAL:
					x1 = "0%";
					y1 = "50%";
					x2 = "100%";
					y2 = "50%";
					break;
				case GRADIENT_VERTICAL:
					x1 = "50%";
					y1 = "0%";
					x2 = "50%";
					y2 = "100%";
					break;
				case GRADIENT_DIAGONAL1:
					x1 = "0%";
					y1 = "0%";
					x2 = "100%";
					y2 = "100%";
					break;
				case GRADIENT_DIAGONAL2:
					x1 = "100%";
					y1 = "100%";
					x2 = "0%";
					y2 = "0%";
					break;
				default:
					break;
				}

				out.open(type);
				out.attribute("id", gid);
				out.attribute("gradientUnits", "objectBoundingBox");

				if (type.equals("linearGradient")) {
					out.attribute("x1", x1);
					out.attribute("y1", y1);
					out.attribute("x2", x2);
					out.attribute("y2", y2);
				}

				for (int i = 0; i < group.getFillColorCount(); i++) {
					out.open("stop");
					out.attribute("stop-color", toHexColor(group.getFillColor(i)));
					out.attribute("stop-opacity", d(group.getFillColor(i).getAlpha() / 255.0));
					out.attribute("offset", Double.toString(i / (double) (group.getFillColorCount() - 1)));
					out.close();
				}

				out.close();

				style = style.replace("%gradient-id%", gid);
			}
		}

		public String getElementStyle(Element e) {
			String st = style;

			if (dynfill) {
				if (group.getFillColorCount() > 1) {
					String color, opacity;
					double d = e.hasNumber("ui.color") ? e.getNumber("ui.color") : 0;

					double a, b;
					Colors colors = group.getFillColors();
					int s = Math.min((int) (d * group.getFillColorCount()), colors.size() - 2);

					a = s / (double) (colors.size() - 1);
					b = (s + 1) / (double) (colors.size() - 1);

					d = (d - a) / (b - a);

					Color c1 = colors.get(s), c2 = colors.get(s + 1);

					color = String.format("#%02x%02x%02x", (int) (c1.getRed() + d * (c2.getRed() - c1.getRed())),
							(int) (c1.getGreen() + d * (c2.getGreen() - c1.getGreen())),
							(int) (c1.getBlue() + d * (c2.getBlue() - c1.getBlue())));

					opacity = Double.toString((c1.getAlpha() + d * (c2.getAlpha() - c1.getAlpha())) / 255.0);

					st = st.replace("%fill-color%", color);
					st = st.replace("%fill-opacity%", opacity);
				}
			}

			return st;
		}
	}

	static class XMLWriter {
		XMLStreamWriter out;
		int depth;
		boolean closed;

		void start(Writer w) throws XMLStreamException, FactoryConfigurationError, IOException {
			if (out != null)
				end();

			out = XMLOutputFactory.newInstance().createXMLStreamWriter(w);
			out.writeStartDocument();
		}

		void end() throws XMLStreamException {
			out.writeEndDocument();
			out.flush();
			out.close();
			out = null;
		}

		void open(String name) throws XMLStreamException {
			out.writeCharacters("\n");
			for (int i = 0; i < depth; i++)
				out.writeCharacters("  ");

			out.writeStartElement(name);
			depth++;
		}

		void close() throws XMLStreamException {
			out.writeEndElement();
			depth--;
		}

		void attribute(String key, String value) throws XMLStreamException {
			out.writeAttribute(key, value);
		}

		void characters(String data) throws XMLStreamException {
			out.writeCharacters(data);
		}
	}

	private static void concat(StringBuilder buffer, Object... args) {
		if (args != null) {
			for (int i = 0; i < args.length; i++)
				buffer.append(args[i].toString());
		}
	}

	private static String toHexColor(Color c) {
		return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId, String attribute, Object value) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeChanged(java.lang.String ,
	 * long, java.lang.String, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void edgeAttributeChanged(String sourceId, long timeId, String edgeId, String attribute, Object oldValue,
			Object newValue) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeRemoved(java.lang.String ,
	 * long, java.lang.String, java.lang.String)
	 */
	public void edgeAttributeRemoved(String sourceId, long timeId, String edgeId, String attribute) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#graphAttributeAdded(java.lang.String ,
	 * long, java.lang.String, java.lang.Object)
	 */
	public void graphAttributeAdded(String sourceId, long timeId, String attribute, Object value) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.AttributeSink#graphAttributeChanged(java.lang.
	 * String, long, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void graphAttributeChanged(String sourceId, long timeId, String attribute, Object oldValue,
			Object newValue) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.AttributeSink#graphAttributeRemoved(java.lang.
	 * String, long, java.lang.String)
	 */
	public void graphAttributeRemoved(String sourceId, long timeId, String attribute) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId, String attribute, Object value) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeChanged(java.lang.String ,
	 * long, java.lang.String, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void nodeAttributeChanged(String sourceId, long timeId, String nodeId, String attribute, Object oldValue,
			Object newValue) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeRemoved(java.lang.String ,
	 * long, java.lang.String, java.lang.String)
	 */
	public void nodeAttributeRemoved(String sourceId, long timeId, String nodeId, String attribute) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#edgeAdded(java.lang.String, long,
	 * java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId,
			boolean directed) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#edgeRemoved(java.lang.String, long,
	 * java.lang.String)
	 */
	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#graphCleared(java.lang.String, long)
	 */
	public void graphCleared(String sourceId, long timeId) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#nodeAdded(java.lang.String, long,
	 * java.lang.String)
	 */
	public void nodeAdded(String sourceId, long timeId, String nodeId) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#nodeRemoved(java.lang.String, long,
	 * java.lang.String)
	 */
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#stepBegins(java.lang.String, long,
	 * double)
	 */
	public void stepBegins(String sourceId, long timeId, double step) {
	}
}
