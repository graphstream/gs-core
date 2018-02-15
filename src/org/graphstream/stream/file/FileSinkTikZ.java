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
 * @since 2011-04-13
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author kitskub <kitskub@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file;

import org.graphstream.ui.graphicGraph.stylesheet.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.logging.Logger;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;
import org.graphstream.stream.GraphReplay;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicEdge;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.graphicGraph.StyleGroup;
import org.graphstream.ui.graphicGraph.StyleGroupSet;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.FillMode;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.SizeMode;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;

/**
 * An export of a graph to PGF/TikZ format.
 * <a>http://sourceforge.net/projects/pgf/</a>
 * 
 * This allows to include graph in a latex document. Only
 * <code>writeAll(Graph,*)</code> is working, dynamics is not handle. If the
 * exported graph is a GraphicGraph, then CSS style of the graph will be used.
 * 
 * For a better rendering, it is strongly recommended to run previously a layout
 * algorithm that will add coordinates on nodes. Else, random coordinates will
 * be choosen for nodes. Layout can be run in this way : <code>
 * Graph g;
 * ...
 * SpringBox sbox = new SpringBox();
 * 
 * g.addSink(sbox);
 * sbox.addAttributeSink(g);
 * 
 * do sbox.compute(); while (sbox.getStabilization() < 0.9);
 * 
 * g.removeSink(sbox);
 * sbox.remoteAttributeSink(g);
 * </code>
 * 
 * TikZ pictures are scalable so pixel units is not handle here. The picture is
 * bounded in a box which width and height can be defined by adding attributes
 * to the graph:
 * <ul>
 * <li>"ui.tikz.width"</li>
 * <li>"ui.tikz.height"</li>
 * </ul>
 * The value of these attributes has to be considered as centimeters.
 * 
 * Common supported style :
 * <ul>
 * <li>"fill-color", alpha is supported to</li>
 * <li>"size" in "gu"</li>
 * </ul>
 * 
 * Node supported style :
 * <ul>
 * <li>"shape" with "box", "rounded-box", "circle", "triangle", "diamond"</li>
 * <li>"stroke-mode" with "plain"</li>
 * <li>"stroke-color", alpha is supported to</li>
 * <li>"stroke-width" in "gu"</li>
 * </ul>
 * 
 * Edge supported style :
 * <ul>
 * </ul>
 */
public class FileSinkTikZ extends FileSinkBase {
	private static final Logger LOGGER = Logger.getLogger(FileSinkTikZ.class.getName());

	/**
	 * Node attribute storing coordinates.
	 */
	public static final String XYZ_ATTR = "xyz";

	/**
	 * Graph attribute storing width of the TikZ picture.
	 */
	public static final String WIDTH_ATTR = "ui.tikz.width";

	/**
	 * Graph attribute storing height of the TikZ picture.
	 */
	public static final String HEIGHT_ATTR = "ui.tikz.height";

	public static final double DEFAULT_WIDTH = 10;

	public static final double DEFAULT_HEIGHT = 10;

	/**
	 * Define the default minimum size of nodes when using a dynamic size. This size
	 * is in millimeter.
	 */
	public static final double DISPLAY_MIN_SIZE_IN_MM = 2;

	/**
	 * Define the default maximum size of nodes when using a dynamic size. This size
	 * is in millimeter.
	 */
	public static final double DISPLAY_MAX_SIZE_IN_MM = 10;

	protected PrintWriter out;

	protected HashMap<String, String> colors = new HashMap<String, String>();
	protected HashMap<String, String> classes = new HashMap<String, String>();
	protected HashMap<String, String> classNames = new HashMap<String, String>();

	protected int classIndex = 0;
	protected int colorIndex = 0;

	protected double width = Double.NaN;
	protected double height = Double.NaN;

	protected boolean layout = false;

	protected GraphicGraph buffer;

	protected String css = null;

	protected double minSize = 0;

	protected double maxSize = 0;

	protected double displayMinSize = DISPLAY_MIN_SIZE_IN_MM;

	protected double displayMaxSize = DISPLAY_MAX_SIZE_IN_MM;

	private double xmin, ymin, xmax, ymax;

	private PointsWrapper points;
	private Locale l = Locale.ROOT;

	protected static String formatId(String id) {
		return "node" + id.replaceAll("\\W", "_");
	}

	public FileSinkTikZ() {
		buffer = new GraphicGraph("tikz-buffer");
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public void setDisplaySize(double min, double max) {
		this.displayMinSize = min;
		this.displayMaxSize = max;
	}

	public void setCSS(String css) {
		this.css = css;
	}

	public void setLayout(boolean layout) {
		this.layout = layout;
	}

	protected double getNodeX(Node n) {
		if (n.hasAttribute(XYZ_ATTR))
			return ((Number) (n.getArray(XYZ_ATTR)[0])).doubleValue();

		if (n.hasAttribute("x"))
			return n.getNumber("x");

		return Double.NaN;
	}

	protected double getNodeY(Node n) {
		if (n.hasAttribute(XYZ_ATTR))
			return ((Number) (n.getArray(XYZ_ATTR)[1])).doubleValue();

		if (n.hasAttribute("y"))
			return n.getNumber("y");

		return Double.NaN;
	}

	protected String getNodeStyle(Node n) {
		String style = "tikzgsnode";

		if (n instanceof GraphicNode) {
			GraphicNode gn = (GraphicNode) n;

			style = classNames.get(gn.style.getId());

			if (gn.style.getFillMode() == FillMode.DYN_PLAIN) {
				double uicolor = gn.getNumber("ui.color");

				if (Double.isNaN(uicolor))
					uicolor = 0;

				int c = gn.style.getFillColorCount();
				int s = 1;
				double d = 1.0 / (c - 1);

				while (s * d < uicolor && s < c)
					s++;

				uicolor -= (s - 1) * d;
				uicolor *= c;

				style += String.format(Locale.ROOT, ", fill=%s!%d!%s", checkColor(gn.style.getFillColor(0)),
						(int) (uicolor * 100), checkColor(gn.style.getFillColor(1)));
			}

			if (gn.style.getSizeMode() == SizeMode.DYN_SIZE) {
				double uisize = gn.getNumber("ui.size");

				if (Double.isNaN(uisize))
					uisize = minSize;

				uisize = (uisize - minSize) / (maxSize - minSize);
				uisize = uisize * (displayMaxSize - displayMinSize) + displayMinSize;

				style += String.format(Locale.ROOT, ", minimum size=%fmm", uisize);
			}
		}

		return style;
	}

	protected String getEdgeStyle(Edge e) {
		String style = "tikzgsnode";

		if (e instanceof GraphicEdge) {
			GraphicEdge ge = (GraphicEdge) e;

			style = classNames.get(ge.style.getId());

			if (ge.style.getFillMode() == FillMode.DYN_PLAIN) {
				double uicolor = ge.getNumber("ui.color");

				if (Double.isNaN(uicolor))
					uicolor = 0;

				int c = ge.style.getFillColorCount();
				int s = 1;
				double d = 1.0 / (c - 1);

				while (s * d < uicolor && s < c)
					s++;

				uicolor -= (s - 1) * d;
				uicolor *= c;

				style += String.format(Locale.ROOT, ", draw=%s!%d!%s", checkColor(ge.style.getFillColor(s - 1)),
						(int) (uicolor * 100), checkColor(ge.style.getFillColor(s)));
			}

			if (ge.style.getSizeMode() == SizeMode.DYN_SIZE) {
				double uisize = ge.getNumber("ui.size");

				if (Double.isNaN(uisize) || uisize < 0.01)
					uisize = 1;

				style += String.format(Locale.ROOT, ", line width=%fpt", uisize);
			}
		}

		return style;
	}

	protected String checkColor(Color c) {
		String rgb = String.format(Locale.ROOT, "%.3f,%.3f,%.3f", c.getRed() / 255.0f, c.getGreen() / 255.0f,
				c.getBlue() / 255.0f);

		if (colors.containsKey(rgb))
			return colors.get(rgb);

		String key = String.format("tikzC%02d", colorIndex++);
		colors.put(rgb, key);

		return key;
	}

	/**
	 * Convert a StyleGroup to tikz style.
	 * 
	 * @param group
	 *            the style group to convert
	 * @return string representation of the style group usable in TikZ.
	 */
	protected String getTikzStyle(StyleGroup group) {
		StringBuilder buffer = new StringBuilder();
		LinkedList<String> style = new LinkedList<String>();

		for (int i = 0; i < group.getFillColorCount(); i++)
			checkColor(group.getFillColor(i));

		switch (group.getType()) {
		case NODE: {
			if (group.getFillMode() != FillMode.DYN_PLAIN) {
				String fill = checkColor(group.getFillColor(0));
				style.add("fill=" + fill);
			}

			if (group.getFillColor(0).getAlpha() < 255)
				style.add(String.format(Locale.ROOT, "fill opacity=%.2f", group.getFillColor(0).getAlpha() / 255.0f));

			switch (group.getStrokeMode()) {
			case DOTS:
			case DASHES:
			case PLAIN:
				String stroke = checkColor(group.getStrokeColor(0));
				style.add("draw=" + stroke);
				style.add("line width=" + String.format(Locale.ROOT, "%.1fpt", group.getStrokeWidth().value));

				if (group.getStrokeColor(0).getAlpha() < 255)
					style.add(String.format(Locale.ROOT, "draw opacity=%.2f",
							group.getStrokeColor(0).getAlpha() / 255.0f));

				break;
			default:
				LOGGER.warning(String.format("unhandled stroke mode : %s%n", group.getStrokeMode()));
			}

			switch (group.getShape()) {
			case CIRCLE:
				style.add("circle");
				break;
			case ROUNDED_BOX:
				style.add("rounded corners=2pt");
			case BOX:
				style.add("rectangle");
				break;
			case TRIANGLE:
				style.add("isosceles triangle");
				break;
			case DIAMOND:
				style.add("diamond");
				break;
			default:
				LOGGER.warning(String.format("unhandled shape : %s%n", group.getShape()));
			}

			String text = checkColor(group.getTextColor(0));
			style.add("text=" + text);

			switch (group.getSize().units) {
			case GU:
				style.add("minimum size=" + String.format(Locale.ROOT, "%.1fcm", group.getSize().values.get(0)));
				break;
			case PX:
				style.add("minimum size=" + String.format(Locale.ROOT, "%.1fpt", group.getSize().values.get(0)));
				break;
			default:
				LOGGER.warning(
						String.format("%% [warning] units %s are not compatible with TikZ.%n", group.getSize().units));
			}

			style.add("inner sep=0pt");
		}
			break;
		case EDGE: {
			if (group.getFillMode() != FillMode.DYN_PLAIN) {
				String fill = checkColor(group.getFillColor(0));
				style.add("draw=" + fill);
			}

			if (group.getFillColor(0).getAlpha() < 255)
				style.add(String.format(Locale.ROOT, "draw opacity=%.2f", group.getFillColor(0).getAlpha() / 255.0f));

			switch (group.getSize().units) {
			case PX:
			case GU:
				style.add("line width=" + String.format(Locale.ROOT, "%.1fpt", group.getSize().values.get(0)));
				break;
			default:
				LOGGER.warning(
						String.format("%% [warning] units %s are not compatible with TikZ.%n", group.getSize().units));
			}
		}
			break;
		default:
			LOGGER.warning(String.format("unhandled group type : %s%n", group.getType()));
		}

		for (int i = 0; i < style.size(); i++) {
			if (i > 0)
				buffer.append(",");

			buffer.append(style.get(i));
		}

		return buffer.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSinkBase#outputHeader()
	 */
	protected void outputHeader() throws IOException {
		out = (PrintWriter) output;

		colors.clear();
		classes.clear();
		classNames.clear();

		buffer.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSinkBase#outputEndOfFile()
	 */
	protected void outputEndOfFile() throws IOException {
		if (Double.isNaN(width)) {
			if (buffer.hasNumber(WIDTH_ATTR))
				width = buffer.getNumber(WIDTH_ATTR);
			else
				width = DEFAULT_WIDTH;
		}

		if (Double.isNaN(height)) {
			if (buffer.hasNumber(HEIGHT_ATTR))
				height = buffer.getNumber(HEIGHT_ATTR);
			else
				height = DEFAULT_WIDTH;
		}

		checkLayout();

		if (css != null)
			buffer.setAttribute("ui.stylesheet", css);

		points = new PointsWrapper();

		//
		// Begin tikzpicture
		//
		out.printf("%%%n%% Do not forget \\usepackage{tikz} in header.%n%%%n");
		out.printf("\\begin{tikzpicture}");

		checkAndOutputStyle();
		checkXYandSize();

		buffer.nodes().forEach(n -> {
			double x, y;

			x = getNodeX(n);
			y = getNodeY(n);

			if (Double.isNaN(x) || Double.isNaN(y)) {
				x = Math.random() * width;
				y = Math.random() * height;
			} else {
				x = width * (x - xmin) / (xmax - xmin);
				y = height * (y - ymin) / (ymax - ymin);
			}

			out.printf(l, "\t\\node[inner sep=0pt] (%s) at (%f,%f) {};%n", formatId(n.getId()), x, y);
		});

		StyleGroupSet sgs = buffer.getStyleGroups();

		for (HashSet<StyleGroup> groups : sgs.zIndex()) {
			for (StyleGroup group : groups) {
				switch (group.getType()) {
				case NODE:
					for (Element e : group.elements())
						outputNode((Node) e);
					break;
				case EDGE:
					for (Element e : group.elements())
						outputEdge((Edge) e);
					break;
				default:
				}
			}
		}

		//
		// End of tikzpicture.
		//
		out.printf("\\end{tikzpicture}%n");
	}

	private void checkLayout() {
		if (!layout)
			return;

		SpringBox sbox = new SpringBox();

		GraphReplay replay = new GraphReplay("replay");
		replay.addSink(sbox);
		sbox.addAttributeSink(buffer);

		replay.replay(buffer);

		do
			sbox.compute();
		while (sbox.getStabilization() < 0.9);

		buffer.removeSink(sbox);
		sbox.removeAttributeSink(buffer);
	}

	private void checkXYandSize() {
		xmin = ymin = Double.MAX_VALUE;
		xmax = ymax = Double.MIN_VALUE;

		buffer.nodes().forEach(n -> {
			double x, y;

			x = getNodeX(n);
			y = getNodeY(n);

			if (!Double.isNaN(x) && !Double.isNaN(y)) {
				xmin = Math.min(xmin, x);
				xmax = Math.max(xmax, x);
				ymin = Math.min(ymin, y);
				ymax = Math.max(ymax, y);
			} else {
				LOGGER.warning(String.format("%% [warning] missing node (x,y).%n"));
			}

			if (n.hasNumber("ui.size")) {
				minSize = Math.min(minSize, n.getNumber("ui.size"));
				maxSize = Math.max(maxSize, n.getNumber("ui.size"));
			}
		});

		if (minSize == maxSize)
			maxSize += 1;

		buffer.edges().forEach(e -> {
			points.setElement(e);

			if (points.check()) {
				for (int i = 0; i < points.getPointsCount(); i++) {
					double x = points.getX(i);
					double y = points.getY(i);

					xmin = Math.min(xmin, x);
					xmax = Math.max(xmax, x);
					ymin = Math.min(ymin, y);
					ymax = Math.max(ymax, y);
				}
			}
		});
	}

	private void checkAndOutputStyle() {
		String nodeStyle = "circle,draw=black,fill=black";
		String edgeStyle = "draw=black";
		StyleGroupSet sgs = buffer.getStyleGroups();

		for (StyleGroup sg : sgs.groups()) {
			String key = String.format("class%02d", classIndex++);
			classNames.put(sg.getId(), key);
			classes.put(key, getTikzStyle(sg));
		}

		out.printf("[%n");

		for (String key : classes.keySet())
			out.printf(l, "\t%s/.style={%s},%n", key, classes.get(key));

		out.printf(l, "\ttikzgsnode/.style={%s},%n", nodeStyle);
		out.printf(l, "\ttikzgsedge/.style={%s}%n", edgeStyle);

		out.printf("]%n");

		for (String rgb : colors.keySet())
			out.printf(l, "\t\\definecolor{%s}{rgb}{%s}%n", colors.get(rgb), rgb);
	}

	private void outputNode(Node n) {
		String label;
		String style = getNodeStyle(n);

		label = n.hasAttribute("label") ? (String) n.getLabel("label")
				: (n.hasAttribute("ui.label") ? (String) n.getLabel("ui.label") : "");

		out.printf(l, "\t\\node[%s] at (%s) {%s};%n", style, formatId(n.getId()), label);
	}

	private void outputEdge(Edge e) {
		String style = getEdgeStyle(e);
		String uiPoints = "";
		points.setElement(e);

		if (points.check()) {
			for (int i = 0; i < points.getPointsCount(); i++) {
				double x, y;

				x = points.getX(i);
				y = points.getY(i);
				x = width * (x - xmin) / (xmax - xmin);
				y = height * (y - ymin) / (ymax - ymin);

				uiPoints = String.format(l, "%s-- (%.3f,%.3f) ", uiPoints, x, y);
			}
		}

		out.printf(l, "\t\\draw[%s] (%s) %s%s (%s);%n", style, formatId(e.getSourceNode().getId()), uiPoints,
				e.isDirected() ? "->" : "--", formatId(e.getTargetNode().getId()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#graphAttributeAdded(java.lang.String ,
	 * long, java.lang.String, java.lang.Object)
	 */
	public void graphAttributeAdded(String sourceId, long timeId, String attribute, Object value) {
		buffer.graphAttributeAdded(sourceId, timeId, attribute, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.AttributeSink#graphAttributeChanged(java.lang.
	 * String, long, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void graphAttributeChanged(String sourceId, long timeId, String attribute, Object oldValue,
			Object newValue) {
		buffer.graphAttributeChanged(sourceId, timeId, attribute, oldValue, newValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.AttributeSink#graphAttributeRemoved(java.lang.
	 * String, long, java.lang.String)
	 */
	public void graphAttributeRemoved(String sourceId, long timeId, String attribute) {
		buffer.graphAttributeRemoved(sourceId, timeId, attribute);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId, String attribute, Object value) {
		buffer.nodeAttributeAdded(sourceId, timeId, nodeId, attribute, value);
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
		buffer.nodeAttributeChanged(sourceId, timeId, nodeId, attribute, oldValue, newValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeRemoved(java.lang.String ,
	 * long, java.lang.String, java.lang.String)
	 */
	public void nodeAttributeRemoved(String sourceId, long timeId, String nodeId, String attribute) {
		buffer.nodeAttributeRemoved(sourceId, timeId, nodeId, attribute);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId, String attribute, Object value) {
		buffer.edgeAttributeAdded(sourceId, timeId, edgeId, attribute, value);
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
		buffer.edgeAttributeChanged(sourceId, timeId, edgeId, attribute, oldValue, newValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeRemoved(java.lang.String ,
	 * long, java.lang.String, java.lang.String)
	 */
	public void edgeAttributeRemoved(String sourceId, long timeId, String edgeId, String attribute) {
		buffer.edgeAttributeRemoved(sourceId, timeId, edgeId, attribute);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#nodeAdded(java.lang.String, long,
	 * java.lang.String)
	 */
	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		buffer.nodeAdded(sourceId, timeId, nodeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#nodeRemoved(java.lang.String, long,
	 * java.lang.String)
	 */
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		buffer.nodeRemoved(sourceId, timeId, nodeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#edgeAdded(java.lang.String, long,
	 * java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId,
			boolean directed) {
		buffer.edgeAdded(sourceId, timeId, edgeId, fromNodeId, toNodeId, directed);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#edgeRemoved(java.lang.String, long,
	 * java.lang.String)
	 */
	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		buffer.edgeRemoved(sourceId, timeId, edgeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#graphCleared(java.lang.String, long)
	 */
	public void graphCleared(String sourceId, long timeId) {
		buffer.graphCleared(sourceId, timeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#stepBegins(java.lang.String, long,
	 * double)
	 */
	public void stepBegins(String sourceId, long timeId, double step) {
		buffer.stepBegins(sourceId, timeId, step);
	}

	protected class PointsWrapper {
		Object[] points;

		PointsWrapper() {
		}

		public void setElement(Element e) {
			if (e.hasArray("ui.points"))
				points = e.getArray("ui.points");
			else
				points = null;
		}

		public boolean check() {
			if (points == null)
				return false;

			for (int i = 0; i < points.length; i++) {
				if (!(points[i] instanceof Point3) && !points[i].getClass().isArray())
					return false;
			}

			return true;
		}

		public int getPointsCount() {
			return points == null ? 0 : points.length;
		}

		public double getX(int i) {
			if (points == null || i >= points.length)
				return Double.NaN;

			Object p = points[i];

			if (p instanceof Point3)
				return ((Point3) p).x;
			else {
				Object x = Array.get(p, 0);

				if (x instanceof Number)
					return ((Number) x).doubleValue();
				else
					return Array.getDouble(p, 0);
			}
		}

		public double getY(int i) {
			if (i >= points.length)
				return Double.NaN;

			Object p = points[i];

			if (p instanceof Point3)
				return ((Point3) p).y;
			else {
				Object y = Array.get(p, 0);

				if (y instanceof Number)
					return ((Number) y).doubleValue();
				else
					return Array.getDouble(p, 1);
			}
		}
	}
}
