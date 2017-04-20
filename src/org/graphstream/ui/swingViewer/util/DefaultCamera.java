/*
 * Copyright 2006 - 2016
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
 * 
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
package org.graphstream.ui.swingViewer.util;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Point2;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.geom.Vector2;
import org.graphstream.ui.graphicGraph.GraphicEdge;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.graphicGraph.GraphicSprite;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;
import org.graphstream.ui.graphicGraph.stylesheet.Values;
import org.graphstream.ui.view.Camera;
import org.graphstream.ui.view.util.CubicCurve;
import org.graphstream.ui.view.util.InteractiveElement;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Define how the graph is viewed.
 *
 * <p>
 * The camera is in charge of projecting the graph spaces in graph units (GU)
 * into user spaces (often in pixels). It defines the transformation (an affine
 * matrix) to passe from the first to the second. It also contains the graph
 * metrics, a set of values that give the overall dimensions of the graph in
 * graph units, as well as the view port, the area on the screen (or any
 * rendering surface) that will receive the results in pixels (or rendering
 * units).
 * </p>
 *
 * <p>
 * The camera defines a centre at which it always points. It can zoom on the
 * graph, pan in any direction and rotate along two axes.
 * </p>
 *
 * <p>
 * Knowing the transformation also allows to provide services like "what element
 * is not invisible ?" (not in the camera view) or "on what element is the mouse
 * cursor actually ?".
 * </p>
 */
public class DefaultCamera implements Camera {
	/**
	 * class level logger
	 */
	private static final Logger logger = Logger.getLogger(DefaultCamera.class.getSimpleName());

	// Attribute

	/**
	 * The graph.
	 */
	protected GraphicGraph graph = null;

	/**
	 * Information on the graph overall dimension and position.
	 */
	protected GraphMetrics metrics = new GraphMetrics();

	/**
	 * Automatic centring of the view.
	 */
	protected boolean autoFit = true;

	/**
	 * The camera centre of view.
	 */
	protected Point3 center = new Point3();

	/**
	 * The camera zoom.
	 */
	protected double zoom;

	/**
	 * The graph-space -> pixel-space transformation.
	 */
	protected AffineTransform Tx = new AffineTransform();

	/**
	 * The inverse transform of Tx.
	 */
	protected AffineTransform xT;

	/**
	 * The previous affine transform.
	 */
	protected AffineTransform oldTx;

	/**
	 * The rotation angle.
	 */
	protected double rotation;

	/**
	 * Padding around the graph.
	 */
	protected Values padding = new Values(Style.Units.GU, 0, 0, 0);

	/**
	 * Which node is visible. This allows to mark invisible nodes to fasten
	 * visibility tests for nodes, attached sprites and edges.
	 */
	protected HashSet<String> nodeInvisible = new HashSet<String>();

	/**
	 * The graph view port, if any. The graph view port is a view inside the
	 * graph space. It allows to compute the view according to a specified area
	 * of the graph space instead of the graph dimensions.
	 */
	protected double gviewport[] = null;
	protected double gviewportDiagonal = 0;

	// Construction

	/**
	 * New camera.
	 */
	public DefaultCamera(GraphicGraph graph) {
		this.graph = graph;
	}

	// Access

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.ui.swingViewer.util.Camera#getViewCenter()
	 */
	public Point3 getViewCenter() {
		return center;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.ui.swingViewer.util.Camera#setViewCenter(double,
	 * double, double)
	 */
	public void setViewCenter(double x, double y, double z) {
		setAutoFitView(false);
		center.set(x, y, z);
		graph.graphChanged = true;
	}

	public void setViewCenter(double x, double y) {
		setViewCenter(x, y, 0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.ui.swingViewer.util.Camera#getViewPercent()
	 */
	public double getViewPercent() {
		return zoom;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.ui.swingViewer.util.Camera#setViewPercent(double)
	 */
	public void setViewPercent(double percent) {
		setAutoFitView(false);
		setZoom(percent);
		graph.graphChanged = true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.ui.swingViewer.util.Camera#getViewRotation()
	 */
	public double getViewRotation() {
		return rotation;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.ui.swingViewer.util.Camera#getMetrics()
	 */
	public GraphMetrics getMetrics() {
		return metrics;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(String.format("Camera :%n"));

		builder.append(String.format("    autoFit  = %b%n", autoFit));
		builder.append(String.format("    center   = %s%n", center));
		builder.append(String.format("    rotation = %f%n", rotation));
		builder.append(String.format("    zoom     = %f%n", zoom));
		builder.append(String.format("    padding  = %s%n", padding));
		builder.append(String.format("    metrics  = %s%n", metrics));

		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.ui.swingViewer.util.Camera#resetView()
	 */
	public void resetView() {
		setAutoFitView(true);
		setViewRotation(0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.ui.swingViewer.util.Camera#setBounds(double, double,
	 * double, double, double, double)
	 */
	public void setBounds(double minx, double miny, double minz, double maxx, double maxy, double maxz) {
		metrics.setBounds(minx, miny, minz, maxx, maxy, maxz);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.ui.swingViewer.util.Camera#getGraphDimension()
	 */
	public double getGraphDimension() {
		if (gviewport != null)
			return gviewportDiagonal;

		return metrics.diagonal;
	}

	/**
	 * True if the element should be visible on screen. The method used is to
	 * transform the center of the element (which is always in graph units)
	 * using the camera actual transformation to put it in pixel units. Then to
	 * look in the style sheet the size of the element and to test if its
	 * enclosing rectangle intersects the view port. For edges, its two nodes
	 * are used. As a speed-up by default if the camera is in automatic fitting
	 * mode, all element should be visible, and the test always returns true.
	 *
	 * @param element
	 *            The element to test.
	 * @return True if the element is visible and therefore must be rendered.
	 */
	public boolean isVisible(GraphicElement element) {
		if (autoFit) {
			return ((!element.hidden) && (element.style.getVisibilityMode() != StyleConstants.VisibilityMode.HIDDEN));
		} else {
			switch (element.getSelectorType()) {
			case NODE:
				return !nodeInvisible.contains(element.getId());
			case EDGE:
				return isEdgeVisible((GraphicEdge) element);
			case SPRITE:
				return isSpriteVisible((GraphicSprite) element);
			default:
				return false;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.ui.swingViewer.util.Camera#inverseTransform(double,
	 * double)
	 */
	public Point3 transformPxToGu(double x, double y) {
		Point2D.Double p = new Point2D.Double(x, y);
		xT.transform(p, p);
		return new Point3(p.x, p.y, 0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.ui.swingViewer.util.Camera#transform(double, double)
	 */
	public Point3 transformGuToPx(double x, double y, double z) {
		Point2D.Double p = new Point2D.Double(x, y);
		Tx.transform(p, p);
		return new Point3(p.x, p.y, 0);
	}

	/**
	 * Process each node to check if it is in the actual view port, and mark
	 * invisible nodes. This method allows for fast node, sprite and edge
	 * visibility checking when drawing. This must be called before each
	 * rendering (if the view port changed).
	 */
	public void checkVisibility(GraphicGraph graph) {
		double X = metrics.viewport[0];
		double Y = metrics.viewport[1];
		double W = metrics.viewport[2];
		double H = metrics.viewport[3];

		nodeInvisible.clear();

		for (Node node : graph) {
			boolean visible = isNodeIn((GraphicNode) node, X, Y, X + W, Y + H) && (!((GraphicNode) node).hidden)
					&& ((GraphicNode) node).positionned;

			if (!visible)
				nodeInvisible.add(node.getId());
		}
	}

	/**
	 * Search for the first GraphicElement among those specified.  Multiple elements are resolved by priority- {@link InteractiveElement.NODE} > {@link InteractiveElement.EDGE} > {@link InteractiveElement.SPRITE}, (in that order) that contains the
	 * point at coordinates (x, y).
	 *
	 * @param graph
	 *            The graph to search for.
	 * @param x
	 *            The point abscissa.
	 * @param y
	 *            The point ordinate.
	 * @return The first node or sprite at the given coordinates or null if
	 *         nothing found.
	 */
	@Override
	public GraphicElement findGraphicElementAt(GraphicGraph graph, EnumSet<InteractiveElement> types, double x, double y) {
		if (types.contains(InteractiveElement.NODE)) {
			for (Node n : graph) {
				GraphicNode node = (GraphicNode) n;

				if (nodeContains(node, x, y))
					return node;
			}
		}

		if (types.contains(InteractiveElement.EDGE)) {
			for (Edge edge : graph.getEdgeSet()) {
			    if (edge instanceof  GraphicEdge && edgeContains((GraphicEdge)edge, x,y))
					return (GraphicEdge)edge;
			}
        }

		if (types.contains(InteractiveElement.SPRITE)) {
			for (GraphicSprite sprite : graph.spriteSet()) {
				if (spriteContains(sprite, x, y))
					return sprite;
			}
		}

		return null;
	}

	@Override
	public Collection<GraphicElement> allGraphicElementsIn(GraphicGraph graph, EnumSet<InteractiveElement> types, double x1, double y1, double x2, double y2) {
		List<GraphicElement> elts = new ArrayList<GraphicElement>();

		if (types.contains(InteractiveElement.NODE)) {
			for (Node node : graph) {
				if (isNodeIn((GraphicNode) node, x1, y1, x2, y2))
					elts.add((GraphicNode) node);
			}
		}

		if (types.contains(InteractiveElement.EDGE)) {
			for (Edge edge : graph.getEdgeSet()) {
				if (isEdgeIn((GraphicEdge) edge, x1, y1, x2, y2))
					elts.add((GraphicEdge) edge);
			}
		}

		if (types.contains(InteractiveElement.SPRITE)) {
			for (GraphicSprite sprite : graph.spriteSet()) {
				if (isSpriteIn(sprite, x1, y1, x2, y2))
					elts.add(sprite);
			}
		}

		return Collections.unmodifiableList(elts);
	}

	/**
	 * Compute the real position of a sprite according to its eventual
	 * attachment in graph units.
	 *
	 * @param sprite
	 *            The sprite.
	 * @param pos
	 *            Receiver for the sprite 2D position, can be null.
	 * @param units
	 *            The units in which the position must be computed (the sprite
	 *            already contains units).
	 * @return The same instance as the one given by parameter pos or a new one
	 *         if pos was null, containing the computed position in the given
	 *         units.
	 */
	public Point2D.Double getSpritePosition(GraphicSprite sprite, Point2D.Double pos, Units units) {
		if (sprite.isAttachedToNode())
			return getSpritePositionNode(sprite, pos, units);
		else if (sprite.isAttachedToEdge())
			return getSpritePositionEdge(sprite, pos, units);
		else
			return getSpritePositionFree(sprite, pos, units);
	}

	public double[] getGraphViewport() {
		return gviewport;
	}

	// Command

	public void setGraphViewport(double minx, double miny, double maxx, double maxy) {
		setAutoFitView(false);
		setViewCenter(minx + (maxx - minx) / 2.0, miny + (maxy - miny) / 2.0);

		gviewport = new double[4];
		gviewport[0] = minx;
		gviewport[1] = miny;
		gviewport[2] = maxx;
		gviewport[3] = maxy;

		gviewportDiagonal = Math.sqrt((maxx - minx) * (maxx - minx) + (maxy - miny) * (maxy - miny));

		setZoom(1);
	}

	public void removeGraphViewport() {
		logger.fine("Graph viewport removed for [" + this + "].");
		gviewport = null;
		resetView();
	}

	/**
	 * Set the camera view in the given graphics and backup the previous
	 * transform of the graphics. Call {@link #popView(Graphics2D)} to restore
	 * the saved transform. You can only push one time the view.
	 *
	 * @param g2
	 *            The Swing graphics to change.
	 */
	public void pushView(GraphicGraph graph, Graphics2D g2) {
		if (oldTx == null) {
			oldTx = g2.getTransform(); // Backup the Swing transform.

			if (autoFit)
				autoFitView(g2);
			else
				userView(g2);

			// g2.setTransform(Tx); // Set the final transform, a composition of
			// the old Swing transform and our new coordinate system.
		} else {
			throw new RuntimeException("DefaultCamera.pushView() / popView() wrongly nested");
		}

		checkVisibility(graph);
	}

	/**
	 * Restore the transform that was used before
	 * {@link #pushView(GraphicGraph, Graphics2D)} is used.
	 *
	 * @param g2
	 *            The Swing graphics to restore.
	 */
	public void popView(Graphics2D g2) {
		if (oldTx != null) {
			g2.setTransform(oldTx); // Set back the old Swing Transform.
			oldTx = null;
		}
	}

	/**
	 * Compute a transformation matrix that pass from graph units (user space)
	 * to pixel units (device space) so that the whole graph is visible.
	 *
	 * @param g2
	 *            The Swing graphics.
	 */
	protected void autoFitView(Graphics2D g2) {
		double sx, sy;
		double tx, ty;
		double padXgu = getPaddingXgu() * 2;
		double padYgu = getPaddingYgu() * 2;
		double padXpx = getPaddingXpx() * 2;
		double padYpx = getPaddingYpx() * 2;

		sx = (metrics.viewport[2] - padXpx) / (metrics.size.data[0] + padXgu); // Ratio
		// along
		// X
		sy = (metrics.viewport[3] - padYpx) / (metrics.size.data[1] + padYgu); // Ratio
		// along
		// Y
		tx = metrics.lo.x + (metrics.size.data[0] / 2); // Centre of graph in X
		ty = metrics.lo.y + (metrics.size.data[1] / 2); // Centre of graph in Y

		if (sx <= 0) {
			sx = (metrics.viewport[2] - Math.min(metrics.viewport[2] - 1, padXpx)) / (metrics.size.data[0] + padXgu);
		}

		if (sy <= 0) {
			sy = (metrics.viewport[3] - Math.min(metrics.viewport[3] - 1, padYpx)) / (metrics.size.data[1] + padYgu);
		}

		if (sx > sy) // The least ratio.
			sx = sy;
		else
			sy = sx;

		g2.translate(metrics.viewport[2] / 2, metrics.viewport[3] / 2);
		if (rotation != 0)
			g2.rotate(rotation / (180 / Math.PI));
		g2.scale(sx, -sy);
		g2.translate(-tx, -ty);

		Tx = g2.getTransform();
		xT = new AffineTransform(Tx);
		try {
			xT.invert();
		} catch (NoninvertibleTransformException e) {
			logger.warning("Cannot inverse gu2px matrix.");
		}

		zoom = 1;

		center.set(tx, ty, 0);
		metrics.setRatioPx2Gu(sx);
		metrics.loVisible.copy(metrics.lo);
		metrics.hiVisible.copy(metrics.hi);
	}

	/**
	 * Compute a transformation that pass from graph units (user space) to a
	 * pixel units (device space) so that the view (zoom and centre) requested
	 * by the user is produced.
	 *
	 * @param g2
	 *            The Swing graphics.
	 */
	protected void userView(Graphics2D g2) {
		double sx, sy;
		double tx, ty;
		double padXgu = getPaddingXgu() * 2;
		double padYgu = getPaddingYgu() * 2;
		double padXpx = getPaddingXpx() * 2;
		double padYpx = getPaddingYpx() * 2;
		double gw = gviewport != null ? gviewport[2] - gviewport[0] : metrics.size.data[0];
		double gh = gviewport != null ? gviewport[3] - gviewport[1] : metrics.size.data[1];

		sx = (metrics.viewport[2] - padXpx) / ((gw + padXgu) * zoom);
		sy = (metrics.viewport[3] - padYpx) / ((gh + padYgu) * zoom);
		tx = center.x;
		ty = center.y;

		if (sx > sy) // The least ratio.
			sx = sy;
		else
			sy = sx;

		g2.translate((metrics.viewport[2] / 2), (metrics.viewport[3] / 2));
		if (rotation != 0)
			g2.rotate(rotation / (180 / Math.PI));
		g2.scale(sx, -sy);
		g2.translate(-tx, -ty);

		Tx = g2.getTransform();
		xT = new AffineTransform(Tx);
		try {
			xT.invert();
		} catch (NoninvertibleTransformException e) {
			logger.log(Level.WARNING, "Cannot inverse gu2px matrix.", e);
		}

		metrics.setRatioPx2Gu(sx);

		double w2 = (metrics.viewport[2] / sx) / 2;
		double h2 = (metrics.viewport[3] / sx) / 2;

		metrics.loVisible.set(center.x - w2, center.y - h2);
		metrics.hiVisible.set(center.x + w2, center.y + h2);
	}

	/**
	 * Enable or disable automatic adjustment of the view to see the entire
	 * graph.
	 *
	 * @param on
	 *            If true, automatic adjustment is enabled.
	 */
	public void setAutoFitView(boolean on) {
		if (autoFit && (!on)) {
			// We go from autoFit to user view, ensure the current centre is at
			// the
			// middle of the graph, and the zoom is at one.

			zoom = 1;
			center.set(metrics.lo.x + (metrics.size.data[0] / 2), metrics.lo.y + (metrics.size.data[1] / 2), 0);
		}

		autoFit = on;
	}

	/**
	 * Set the zoom (or percent of the graph visible), 1 means the graph is
	 * fully visible.
	 *
	 * @param z
	 *            The zoom.
	 */
	public void setZoom(double z) {
		zoom = z;
		graph.graphChanged = true;
	}

	/**
	 * Set the rotation angle around the centre.
	 *
	 * @param theta
	 *            The rotation angle in degrees.
	 */
	public void setViewRotation(double theta) {
		rotation = theta;
		graph.graphChanged = true;
	}

	/**
	 * Set the output view port size in pixels.
	 *
	 * @param viewportWidth
	 *            The width in pixels of the view port.
	 * @param viewportHeight
	 *            The width in pixels of the view port.
	 */
	public void setViewport(double viewportX, double viewportY, double viewportWidth, double viewportHeight) {
		metrics.setViewport(viewportX, viewportY, viewportWidth, viewportHeight);
	}

	/**
	 * Set the graph padding.
	 *
	 * @param graph
	 *            The graphic graph.
	 */
	public void setPadding(GraphicGraph graph) {
		padding.copy(graph.getStyle().getPadding());
	}

	// Utility

	protected double getPaddingXgu() {
		if (padding.units == Style.Units.GU && padding.size() > 0)
			return padding.get(0);

		return 0;
	}

	protected double getPaddingYgu() {
		if (padding.units == Style.Units.GU && padding.size() > 1)
			return padding.get(1);

		return getPaddingXgu();
	}

	protected double getPaddingXpx() {
		if (padding.units == Style.Units.PX && padding.size() > 0)
			return padding.get(0);

		return 0;
	}

	protected double getPaddingYpx() {
		if (padding.units == Style.Units.PX && padding.size() > 1)
			return padding.get(1);

		return getPaddingXpx();
	}

	/**
	 * Check if a sprite is visible in the current view port.
	 *
	 * @param sprite
	 *            The sprite to check.
	 * @return True if visible.
	 */
	protected boolean isSpriteVisible(GraphicSprite sprite) {
		return isSpriteIn(sprite, metrics.viewport[0], metrics.viewport[1], metrics.viewport[0] + metrics.viewport[2],
				metrics.viewport[1] + metrics.viewport[3]);
	}

	/**
	 * Check if an edge is visible in the current view port.
	 *
	 * @param edge
	 *            The edge to check.
	 * @return True if visible.
	 */
	protected boolean isEdgeVisible(GraphicEdge edge) {
		GraphicNode node0 = edge.getNode0();
		GraphicNode node1 = edge.getNode1();

		if (edge.hidden)
			return false;

		if ((!node1.positionned) || (!node0.positionned))
			return false;

		boolean node0Invis = nodeInvisible.contains(node0.getId());
		boolean node1Invis = nodeInvisible.contains(node1.getId());

		return !(node0Invis && node1Invis);
	}

	/**
	 * Is the given node visible in the given area.
	 *
	 * @param node
	 *            The node to check.
	 * @param X1
	 *            The min abscissa of the area.
	 * @param Y1
	 *            The min ordinate of the area.
	 * @param X2
	 *            The max abscissa of the area.
	 * @param Y2
	 *            The max ordinate of the area.
	 * @return True if the node lies in the given area.
	 */
	protected boolean isNodeIn(GraphicNode node, double X1, double Y1, double X2, double Y2) {
		Values size = node.getStyle().getSize();
		double w2 = metrics.lengthToPx(size, 0) / 2;
		double h2 = size.size() > 1 ? metrics.lengthToPx(size, 1) / 2 : w2;
		Point2D.Double src = new Point2D.Double(node.getX(), node.getY());
		boolean vis = true;

		Tx.transform(src, src);

		double x1 = src.x - w2;
		double x2 = src.x + w2;
		double y1 = src.y - h2;
		double y2 = src.y + h2;

		if (x2 < X1)
			vis = false;
		else if (y2 < Y1)
			vis = false;
		else if (x1 > X2)
			vis = false;
		else if (y1 > Y2)
			vis = false;

		return vis;
	}

	/**
	 * Is the given sprite visible in the given area.
	 *
	 * @param edge
	 *            The edge to check.
	 * @param X1
	 *            The min abscissa of the area.
	 * @param Y1
	 *            The min ordinate of the area.
	 * @param X2
	 *            The max abscissa of the area.
	 * @param Y2
	 *            The max ordinate of the area.
	 * @return True if the edge lies in the given area.
	 */
	protected boolean isEdgeIn(GraphicEdge edge, double X1, double Y1, double X2, double Y2) {
		Values size = edge.getStyle().getSize();
		double w2 = metrics.lengthToPx(size, 0) / 2;
		double h2 = size.size() > 1 ? metrics.lengthToPx(size, 1) / 2 : w2;
		Point2D.Double src = new Point2D.Double(edge.getX(), edge.getY());
		boolean vis = true;

		Tx.transform(src, src);

		double x1 = src.x - w2;
		double x2 = src.x + w2;
		double y1 = src.y - h2;
		double y2 = src.y + h2;

		if (x2 < X1)
			vis = false;
		else if (y2 < Y1)
			vis = false;
		else if (x1 > X2)
			vis = false;
		else if (y1 > Y2)
			vis = false;

		return vis;
	}

	/**
	 * Is the given sprite visible in the given area.
	 *
	 * @param sprite
	 *            The sprite to check.
	 * @param X1
	 *            The min abscissa of the area.
	 * @param Y1
	 *            The min ordinate of the area.
	 * @param X2
	 *            The max abscissa of the area.
	 * @param Y2
	 *            The max ordinate of the area.
	 * @return True if the node lies in the given area.
	 */
	protected boolean isSpriteIn(GraphicSprite sprite, double X1, double Y1, double X2, double Y2) {
		if (sprite.isAttachedToNode() && nodeInvisible.contains(sprite.getNodeAttachment().getId())) {
			return false;
		} else if (sprite.isAttachedToEdge() && !isEdgeVisible(sprite.getEdgeAttachment())) {
			return false;
		} else {
			Values size = sprite.getStyle().getSize();
			double w2 = metrics.lengthToPx(size, 0) / 2;
			double h2 = size.size() > 1 ? metrics.lengthToPx(size, 1) / 2 : w2;
			Point2D.Double src = spritePositionPx(sprite);// new Point2D.Double(
			// sprite.getX(),
			// sprite.getY() );

			// Tx.transform( src, src );

			double x1 = src.x - w2;
			double x2 = src.x + w2;
			double y1 = src.y - h2;
			double y2 = src.y + h2;

			if (x2 < X1)
				return false;
			if (y2 < Y1)
				return false;
			if (x1 > X2)
				return false;
			if (y1 > Y2)
				return false;

			return true;
		}
	}

	protected Point2D.Double spritePositionPx(GraphicSprite sprite) {
		Point2D.Double pos = new Point2D.Double();

		return getSpritePosition(sprite, pos, Units.PX);
		// if( sprite.getUnits() == Units.PX )
		// {
		// return new Point2D.Double( sprite.getX(), sprite.getY() );
		// }
		// else if( sprite.getUnits() == Units.GU )
		// {
		// Point2D.Double pos = new Point2D.Double( sprite.getX(), sprite.getY()
		// );
		// return (Point2D.Double) Tx.transform( pos, pos );
		// }
		// else// if( sprite.getUnits() == Units.PERCENTS )
		// {
		// return new Point2D.Double(
		// (sprite.getX()/100f)*metrics.viewport.data[0],
		// (sprite.getY()/100f)*metrics.viewport.data[1] );
		// }
	}

	/**
	 * Check if a node contains the given point (x,y).
	 *
	 * @param elt
	 *            The node.
	 * @param x
	 *            The point abscissa.
	 * @param y
	 *            The point ordinate.
	 * @return True if (x,y) is in the given element.
	 */
	protected boolean nodeContains(GraphicElement elt, double x, double y) {
		Values size = elt.getStyle().getSize();
		double w2 = metrics.lengthToPx(size, 0) / 2;
		double h2 = size.size() > 1 ? metrics.lengthToPx(size, 1) / 2 : w2;
		Point2D.Double src = new Point2D.Double(elt.getX(), elt.getY());
		Point2D.Double dst = new Point2D.Double();

		Tx.transform(src, dst);

		dst.x -= metrics.viewport[0];
		dst.y -= metrics.viewport[1];

		double x1 = dst.x - w2;
		double x2 = dst.x + w2;
		double y1 = dst.y - h2;
		double y2 = dst.y + h2;

		if (x < x1)
			return false;
		if (y < y1)
			return false;
		if (x > x2)
			return false;
		if (y > y2)
			return false;

		return true;
	}

	/**
	 * Check if an edge contains the given point (x,y).
	 *
	 * @param elt
	 *            The edge.
	 * @param x
	 *            The point abscissa.
	 * @param y
	 *            The point ordinate.
	 * @return True if (x,y) is in the given element.
	 */
	protected boolean edgeContains(GraphicElement elt, double x, double y) {
		Values size = elt.getStyle().getSize();
		double w2 = metrics.lengthToPx(size, 0) / 2;
		double h2 = size.size() > 1 ? metrics.lengthToPx(size, 1) / 2 : w2;
		Point2D.Double src = new Point2D.Double(elt.getX(), elt.getY());
		Point2D.Double dst = new Point2D.Double();

		Tx.transform(src, dst);

		dst.x -= metrics.viewport[0];
		dst.y -= metrics.viewport[1];

		double x1 = dst.x - w2;
		double x2 = dst.x + w2;
		double y1 = dst.y - h2;
		double y2 = dst.y + h2;

		if (x < x1)
			return false;
		if (y < y1)
			return false;
		if (x > x2)
			return false;
		if (y > y2)
			return false;

		return true;
	}

	/**
	 * Check if a sprite contains the given point (x,y).
	 *
	 * @param elt
	 *            The sprite.
	 * @param x
	 *            The point abscissa.
	 * @param y
	 *            The point ordinate.
	 * @return True if (x,y) is in the given element.
	 */
	protected boolean spriteContains(GraphicElement elt, double x, double y) {
		Values size = elt.getStyle().getSize();
		double w2 = metrics.lengthToPx(size, 0) / 2;
		double h2 = size.size() > 1 ? metrics.lengthToPx(size, 1) / 2 : w2;
		Point2D.Double dst = spritePositionPx((GraphicSprite) elt); // new
		// Point2D.Double(
		// elt.getX(),
		// elt.getY()
		// );
		// Point2D.Double dst = new Point2D.Double();

		// Tx.transform( src, dst );
		dst.x -= metrics.viewport[0];
		dst.y -= metrics.viewport[1];

		double x1 = dst.x - w2;
		double x2 = dst.x + w2;
		double y1 = dst.y - h2;
		double y2 = dst.y + h2;

		if (x < x1)
			return false;
		if (y < y1)
			return false;
		if (x > x2)
			return false;
		if (y > y2)
			return false;

		return true;
	}

	/**
	 * Compute the position of a sprite if it is not attached.
	 *
	 * @param sprite
	 *            The sprite.
	 * @param pos
	 *            Where to stored the computed position, if null, the position
	 *            is created.
	 * @param units
	 *            The units the computed position must be given into.
	 * @return The same instance as pos, or a new one if pos was null.
	 */
	protected Point2D.Double getSpritePositionFree(GraphicSprite sprite, Point2D.Double pos, Units units) {
		if (pos == null)
			pos = new Point2D.Double();

		if (sprite.getUnits() == units) {
			pos.x = sprite.getX();
			pos.y = sprite.getY();
		} else if (units == Units.GU && sprite.getUnits() == Units.PX) {
			pos.x = sprite.getX();
			pos.y = sprite.getY();

			xT.transform(pos, pos);
		} else if (units == Units.PX && sprite.getUnits() == Units.GU) {
			pos.x = sprite.getX();
			pos.y = sprite.getY();

			Tx.transform(pos, pos);
		} else if (units == Units.GU && sprite.getUnits() == Units.PERCENTS) {
			pos.x = metrics.lo.x + (sprite.getX() / 100f) * metrics.graphWidthGU();
			pos.y = metrics.lo.y + (sprite.getY() / 100f) * metrics.graphHeightGU();
		} else if (units == Units.PX && sprite.getUnits() == Units.PERCENTS) {
			pos.x = (sprite.getX() / 100f) * metrics.viewport[2];
			pos.y = (sprite.getY() / 100f) * metrics.viewport[3];
		} else {
			throw new RuntimeException("Unhandled yet sprite positioning.");
		}

		return pos;
	}

	/**
	 * Compute the position of a sprite if attached to a node.
	 *
	 * @param sprite
	 *            The sprite.
	 * @param pos
	 *            Where to stored the computed position, if null, the position
	 *            is created.
	 * @param units
	 *            The units the computed position must be given into.
	 * @return The same instance as pos, or a new one if pos was null.
	 */
	protected Point2D.Double getSpritePositionNode(GraphicSprite sprite, Point2D.Double pos, Units units) {
		if (pos == null)
			pos = new Point2D.Double();

		GraphicNode node = sprite.getNodeAttachment();

		pos.x = node.x + sprite.getX();
		pos.y = node.y + sprite.getY();

		if (units == Units.PX)
			Tx.transform(pos, pos);

		return pos;
	}

	/**
	 * Compute the position of a sprite if attached to an edge.
	 *
	 * @param sprite
	 *            The sprite.
	 * @param pos
	 *            Where to stored the computed position, if null, the position
	 *            is created.
	 * @param units
	 *            The units the computed position must be given into.
	 * @return The same instance as pos, or a new one if pos was null.
	 */
	protected Point2D.Double getSpritePositionEdge(GraphicSprite sprite, Point2D.Double pos, Units units) {
		if (pos == null)
			pos = new Point2D.Double();

		GraphicEdge edge = sprite.getEdgeAttachment();

		if (edge.isCurve()) {
			double ctrl[] = edge.getControlPoints();
			Point2 p0 = new Point2(edge.from.getX(), edge.from.getY());
			Point2 p1 = new Point2(ctrl[0], ctrl[1]);
			Point2 p2 = new Point2(ctrl[1], ctrl[2]);
			Point2 p3 = new Point2(edge.to.getX(), edge.to.getY());
			Vector2 perp = CubicCurve.perpendicular(p0, p1, p2, p3, sprite.getX());
			double y = metrics.lengthToGu(sprite.getY(), sprite.getUnits());

			perp.normalize();
			perp.scalarMult(y);

			pos.x = CubicCurve.eval(p0.x, p1.x, p2.x, p3.x, sprite.getX()) - perp.data[0];
			pos.y = CubicCurve.eval(p0.y, p1.y, p2.y, p3.y, sprite.getX()) - perp.data[1];
		} else {
			double x = ((GraphicNode) edge.getSourceNode()).x;
			double y = ((GraphicNode) edge.getSourceNode()).y;
			double dx = ((GraphicNode) edge.getTargetNode()).x - x;
			double dy = ((GraphicNode) edge.getTargetNode()).y - y;
			double d = sprite.getX(); // Percent on the edge.
			double o = metrics.lengthToGu(sprite.getY(), sprite.getUnits());
			// Offset from the position given by percent, perpendicular to the
			// edge.

			d = d > 1 ? 1 : d;
			d = d < 0 ? 0 : d;

			x += dx * d;
			y += dy * d;

			d = (double) Math.sqrt(dx * dx + dy * dy);
			dx /= d;
			dy /= d;

			x += -dy * o;
			y += dx * o;

			pos.x = x;
			pos.y = y;

			if (units == Units.PX) {
				Tx.transform(pos, pos);
			}
		}

		return pos;
	}
}