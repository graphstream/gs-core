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
 * @since 2018-01-18
 * 
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 */
package org.graphstream.ui.view.camera;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicEdge;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.graphicGraph.GraphicSprite;
import org.graphstream.ui.graphicGraph.stylesheet.Selector;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;
import org.graphstream.ui.graphicGraph.stylesheet.Values;
import org.graphstream.ui.view.util.GraphMetrics;
import org.graphstream.ui.view.util.InteractiveElement;

/**
 * Define a view of the graph or a part of the graph.
 * 
 * The camera can be seen as an element in charge of projecting the graph
 * elements in graph units (GU) into rendering space units, often in pixels. It
 * defines the transformation, an affine matrix, to pass from the first to the
 * second (in fact its the back-end that does it).
 * 
 * It also contains the graph metrics. This is a set of values that give the
 * overall dimensions of the graph in graph units, as well as the view port, the
 * area on the screen (or any rendering surface) that will receive the results
 * in pixels (or any rendering units). The two mains methods for this operation
 * are [[Camera.pushView(GraphicGraph)]] and [[Camera.popView()]].
 * 
 * The user of the camera must set both the view port and the graph bounds in
 * order for the camera to correctly project the graph view (the Renderer does
 * that before using the Camera, at each frame). The camera model is as follows:
 * the camera defines a center at which it always points. It can zoom on the
 * graph (as if the camera angle of view was changing), pan in any direction by
 * moving its center of view and rotate along the axe going from the center to
 * the camera position (camera can rotate around two axes in 3D, but this is a
 * 2D camera).
 * 
 * There are two modes: - an "auto-fit" mode where the camera always show the
 * whole graph even if it changes in size, by automatically changing the center
 * and zoom values, - and a "user" mode where the camera center (looked-at
 * point), zoom and panning are specified and will not be modified in the bounds
 * of the graph change.
 * 
 * The camera is also able to answer questions like: "what element is visible
 * actually?", or "on what element is the mouse cursor actually?".
 * 
 * The camera is also able to compute sprite positions according to their
 * attachment, as well as maintaining a list of all elements out of the view, so
 * that it is not needed to render them.
 */
public class DefaultCamera2D implements Camera {

	/** Information on the graph overall dimension and position. */
	protected GraphMetrics metrics = new GraphMetrics();

	/** Automatic centering of the view. */
	protected boolean autoFit = true;

	/** The camera center of view. */
	protected Point3 center = new Point3();

	/** The camera zoom. */
	protected double zoom = 1;

	/** The rotation angle (along an axis perpendicular to the view). */
	protected double rotation = 0;

	/** Padding around the graph. */
	protected Values padding = new Values(Units.GU, 0, 0, 0);

	/** The rendering back-end. */
	protected Backend bck = null;

	/**
	 * Which node is visible. This allows to mark invisible nodes to fasten
	 * visibility tests for nodes, attached sprites and edges. The visibility test
	 * is heavy, and we often need to test for nodes visibility. This allows to do
	 * it only once per rendering step. Hence the storage of the invisible nodes
	 * here.
	 */
	protected HashSet<String> nodeInvisible = new HashSet<>();

	/** Which sprite is visible. */
	protected HashSet<String> spriteInvisible = new HashSet<>();

	/**
	 * The graph view port, if any. The graph view port is a view inside the graph
	 * space. It allows to compute the view according to a specified area of the
	 * graph space instead of the graph dimensions.
	 */
	protected double[] gviewport = null;

	protected GraphicGraph graph;

	public DefaultCamera2D(GraphicGraph graph) {
		this.graph = graph;
	}

	@Override
	public Point3 getViewCenter() {
		// TODO Auto-generated method stub
		return center;
	}

	@Override
	public void setViewCenter(double x, double y, double z) {
		setAutoFitView(false);
		center.set(x, y, z);
		graph.graphChanged = true;
	}

	public void setViewCenter(Point3 p) {
		setViewCenter(p.x, p.y, p.z);
	}

	@Override
	public double getViewPercent() {
		// TODO Auto-generated method stub
		return zoom;
	}

	@Override
	public void setViewPercent(double percent) {
		setAutoFitView(false);
		setZoom(percent);
		graph.graphChanged = true;
	}

	/**
	 * Set the zoom (or percent of the graph visible), 1 means the graph is fully
	 * visible.
	 *
	 * @param z
	 *            The zoom.
	 */
	public void setZoom(double z) {
		zoom = z;
		graph.graphChanged = true;
	}

	@Override
	public double getViewRotation() {
		// TODO Auto-generated method stub
		return rotation;
	}

	@Override
	public void setViewRotation(double theta) {
		// TODO Auto-generated method stub
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
	public void setViewport(double x, double y, double viewportWidth, double viewportHeight) {
		metrics.setViewport(x, y, viewportWidth, viewportHeight);
	}

	@Override
	public double getGraphDimension() {
		return metrics.getDiagonal();
	}

	public boolean spriteContains(GraphicElement elt, double x, double y) {
		GraphicSprite sprite = (GraphicSprite) elt;
		Values size = getNodeOrSpriteSize(elt);
		double w2 = metrics.lengthToPx(size, 0) / 2;
		double h2 = w2;
		if (size.size() > 1)
			h2 = metrics.lengthToPx(size, 1) / 2;

		Point3 dst = spritePositionPx(sprite);

		double x1 = dst.x - w2;
		double x2 = dst.x + w2;
		double y1 = dst.y - h2;
		double y2 = dst.y + h2;

		if (x < x1)
			return false;
		else if (y < y1)
			return false;
		else if (x > x2)
			return false;
		else if (y > y2)
			return false;
		else
			return true;
	}

	public Point3 spritePositionPx(GraphicSprite sprite) {
		return getSpritePosition(sprite, new Point3(), Units.PX);
	}

	/**
	 * Compute the real position of a sprite according to its eventual attachment in
	 * graph units.
	 * 
	 * @param sprite
	 *            The sprite.
	 * @param pos
	 *            Receiver for the sprite 2D position, can be null.
	 * @param units
	 *            The units in which the position must be computed (the sprite
	 *            already contains units).
	 * @return The same instance as the one given by parameter pos or a new one if
	 *         pos was null, containing the computed position in the given units.
	 */
	public Point3 getSpritePosition(GraphicSprite sprite, Point3 pos, Units units) {
		if (sprite.isAttachedToNode())
			return getSpritePositionNode(sprite, pos, units);
		else if (sprite.isAttachedToEdge())
			return getSpritePositionEdge(sprite, pos, units);
		else
			return getSpritePositionFree(sprite, pos, units);
	}

	public Point3 getSpritePositionFree(GraphicSprite sprite, Point3 position, Units units) {
		Point3 pos = position;

		if (pos == null) {
			pos = new Point3();
		}

		if (sprite.getUnits() == units) {
			pos.x = sprite.getX();
			pos.y = sprite.getY();
		} else if (units == Units.GU && sprite.getUnits() == Units.PX) {
			pos.x = sprite.getX();
			pos.y = sprite.getY();
			bck.inverseTransform(pos);
		} else if (units == Units.PX && sprite.getUnits() == Units.GU) {
			pos.x = sprite.getX();
			pos.y = sprite.getY();
			bck.transform(pos);
		} else if (units == Units.GU && sprite.getUnits() == Units.PERCENTS) {
			pos.x = metrics.lo.x + (sprite.getX() / 100f) * metrics.graphWidthGU();
			pos.y = metrics.lo.y + (sprite.getY() / 100f) * metrics.graphHeightGU();
		} else if (units == Units.PX && sprite.getUnits() == Units.PERCENTS) {
			pos.x = (sprite.getX() / 100f) * metrics.viewport[2];
			pos.y = (sprite.getY() / 100f) * metrics.viewport[3];
		} else {
			throw new RuntimeException(
					"Unhandled yet sprite positioning convertion " + sprite.getUnits() + " to " + units + ".");
		}

		return pos;
	}

	public Point3 getSpritePositionEdge(GraphicSprite sprite, Point3 position, Units units) {
		Point3 pos = position;

		if (pos == null)
			pos = new Point3();

		GraphicEdge edge = sprite.getEdgeAttachment();
		ConnectorSkeleton info = (ConnectorSkeleton) edge.getAttribute(Skeleton.attributeName);

		if (info != null) {
			double o = metrics.lengthToGu(sprite.getY(), sprite.getUnits());
			if (o == 0) {
				Point3 p = info.pointOnShape(sprite.getX());
				pos.x = p.x;
				pos.y = p.y;
			} else {
				Point3 p = info.pointOnShapeAndPerpendicular(sprite.getX(), o);
				pos.x = p.x;
				pos.y = p.y;
			}
		} else {
			double x = 0.0;
			double y = 0.0;
			double dx = 0.0;
			double dy = 0.0;
			double d = sprite.getX();
			double o = metrics.lengthToGu(sprite.getY(), sprite.getUnits());

			x = edge.from.x;
			y = edge.from.y;
			dx = edge.to.x - x;
			dy = edge.to.y - y;

			if (d > 1)
				d = 1;
			if (d < 0)
				d = 0;

			x += dx * d;
			x += dy * d;

			if (o != 0) {
				d = Math.sqrt(dx * dx + dy * dy);
				dx /= d;
				dy /= d;

				x += -dy * o;
				y += dx * o;
			}

			pos.x = x;
			pos.y = y;
		}

		if (units == Units.PX)
			bck.transform(pos);

		return pos;
	}

	/**
	 * Compute the position of a sprite if attached to a node.
	 * 
	 * @param sprite
	 *            The sprite.
	 * @param position
	 *            Where to stored the computed position, if null, the position is
	 *            created.
	 * @param units
	 *            The units the computed position must be given into.
	 * @return The same instance as pos, or a new one if pos was null.
	 */
	public Point3 getSpritePositionNode(GraphicSprite sprite, Point3 position, Units units) {
		Point3 pos = position;

		if (pos == null)
			pos = new Point3();

		double spriteX = metrics.lengthToGu(sprite.getX(), sprite.getUnits());
		double spriteY = metrics.lengthToGu(sprite.getY(), sprite.getUnits());

		GraphicNode node = sprite.getNodeAttachment();
		pos.x = node.x + spriteX;
		pos.y = node.y + spriteY;

		if (units == Units.PX)
			bck.transform(pos);

		return pos;
	}

	public boolean nodeContains(GraphicElement elt, double x, double y) {

		Values size = getNodeOrSpriteSize(elt);
		double w2 = metrics.lengthToPx(size, 0) / 2;
		double h2 = w2;
		if (size.size() > 1)
			h2 = metrics.lengthToPx(size, 1) / 2;
		Point3 dst = bck.transform(elt.getX(), elt.getY(), 0);
		double x1 = (dst.x) - w2;
		double x2 = (dst.x) + w2;
		double y1 = (dst.y) - h2;
		double y2 = (dst.y) + h2;

		if (x < x1)
			return false;
		else if (y < y1)
			return false;
		else if (x > x2)
			return false;
		else if (y > y2)
			return false;
		else
			return true;
	}

	public boolean edgeContains(GraphicElement elt, double x, double y) {

		Values size = elt.getStyle().getSize();
		double w2 = metrics.lengthToPx(size, 0) / 2;
		double h2 = w2;
		if (size.size() > 1)
			h2 = metrics.lengthToPx(size, 1) / 2;
		Point3 dst = bck.transform(elt.getX(), elt.getY(), 0);

		double x1 = (dst.x) - w2;
		double x2 = (dst.x) + w2;
		double y1 = (dst.y) - h2;
		double y2 = (dst.y) + h2;

		if (x < x1)
			return false;
		else if (y < y1)
			return false;
		else if (x > x2)
			return false;
		else if (y > y2)
			return false;
		else
			return true;
	}

	public Values getNodeOrSpriteSize(GraphicElement elt) {
		AreaSkeleton info = (AreaSkeleton) elt.getAttribute(Skeleton.attributeName);

		if (info != null) {
			return new Values(Units.GU, info.theSize().x, info.theSize().y);
		} else {
			return elt.getStyle().getSize();
		}
	}

	public Point3 getNodeOrSpritePositionGU(GraphicElement elt, Point3 pos) {
		Point3 p = pos;
		if (p == null)
			p = new Point3();

		if (elt instanceof GraphicNode) {
			p.x = ((GraphicNode) elt).getX();
			p.y = ((GraphicNode) elt).getY();
		} else if (elt instanceof GraphicSprite) {
			p = getSpritePosition(((GraphicSprite) elt), p, Units.GU);
		}

		return p;
	}

	@Override
	public void removeGraphViewport() {
		gviewport = null;
	}

	@Override
	public void setGraphViewport(double minx, double miny, double maxx, double maxy) {
		gviewport = new double[4];
		gviewport[0] = minx;
		gviewport[1] = miny;
		gviewport[2] = maxx;
		gviewport[3] = maxy;
	}

	@Override
	public void resetView() {
		setAutoFitView(true);
		setViewRotation(0);
	}

	/**
	 * Set the camera view in the given graphics and backup the previous transform
	 * of the graphics. Call {@link #popView()} to restore the saved transform. You
	 * can only push one time the view.
	 * 
	 * @param graph
	 *            The graphic graph (used to check element visibility).
	 */
	public void pushView(GraphicGraph graph) {
		bck.pushTransform();
		setPadding(graph);

		if (autoFit)
			autoFitView();
		else
			userView();

		checkVisibility(graph);
	}

	/** Restore the transform that was used before {@link #pushView()} is used. */
	public void popView() {
		bck.popTransform();
	}

	public void checkVisibility(GraphicGraph graph) {
		nodeInvisible.clear();
		spriteInvisible.clear();

		if (!autoFit) {
			// If autoFit is on, we know the whole graph is visible anyway.
			double X = metrics.viewport[0];
			double Y = metrics.viewport[1];
			double W = metrics.viewport[2];
			double H = metrics.viewport[3];

			graph.nodes().forEach(node -> {
				GraphicNode n = (GraphicNode) node;
				boolean visible = isNodeIn(n, X, Y, X + W, Y + H) && (!n.hidden) && n.positionned;

				if (!visible) {
					nodeInvisible.add(node.getId());
				}
			});

			graph.sprites().forEach(sprite -> {
				boolean visible = isSpriteIn(sprite, X, Y, X + W, Y + H) && (!sprite.hidden);

				if (!visible) {
					spriteInvisible.add(sprite.getId());
				}
			});
		}
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
	public boolean isNodeIn(GraphicNode node, double X1, double Y1, double X2, double Y2) {
		Values size = getNodeOrSpriteSize(node);// node.getStyle.getSize
		double w2 = metrics.lengthToPx(size, 0) / 2;
		double h2 = w2;
		if (size.size() > 1)
			h2 = metrics.lengthToPx(size, 1) / 2;

		Point3 src = new Point3(node.getX(), node.getY(), 0);

		bck.transform(src);

		double x1 = src.x - w2;
		double x2 = src.x + w2;
		double y1 = src.y - h2;
		double y2 = src.y + h2;

		if (x2 < X1)
			return false;
		else if (y2 < Y1)
			return false;
		else if (x1 > X2)
			return false;
		else if (y1 > Y2)
			return false;
		else
			return true;
	}

	public boolean isEdgeIn(GraphicEdge edge, double X1, double Y1, double X2, double Y2) {
		Values size = edge.getStyle().getSize();
		double w2 = metrics.lengthToPx(size, 0) / 2;
		double h2 = w2;
		if (size.size() > 1)
			h2 = metrics.lengthToPx(size, 1) / 2;

		Point3 src = new Point3(edge.getX(), edge.getY(), 0);

		bck.transform(src);

		double x1 = src.x - w2;
		double x2 = src.x + w2;
		double y1 = src.y - h2;
		double y2 = src.y + h2;

		if (x2 < X1)
			return false;
		else if (y2 < Y1)
			return false;
		else if (x1 > X2)
			return false;
		else if (y1 > Y2)
			return false;
		else
			return true;
	}

	/**
	 * Set the graph padding. Called in pushView.
	 * 
	 * @param graph
	 *            The graphic graph.
	 */
	public void setPadding(GraphicGraph graph) {
		padding.copy(graph.getStyle().getPadding());
	}

	/**
	 * Compute a transformation matrix that pass from graph units (user space) to
	 * pixel units (device space) so that the whole graph is visible.
	 * 
	 * @return The transformation modified.
	 */
	public void autoFitView() {
		double sx = 0.0;
		double sy = 0.0;
		double tx = 0.0;
		double ty = 0.0;
		double padXgu = paddingXgu() * 2;
		double padYgu = paddingYgu() * 2;
		double padXpx = paddingXpx() * 2;
		double padYpx = paddingYpx() * 2;

		if (padXpx > metrics.viewport[2])
			padXpx = metrics.viewport[2] / 10.0;
		if (padYpx > metrics.viewport[3])
			padYpx = metrics.viewport[3] / 10.0;

		sx = (metrics.viewport[2] - padXpx) / (metrics.size.data[0] + padXgu); // Ratio along X
		sy = (metrics.viewport[3] - padYpx) / (metrics.size.data[1] + padYgu); // Ratio along Y
		tx = metrics.lo.x + (metrics.size.data[0] / 2); // Center of graph in X
		ty = metrics.lo.y + (metrics.size.data[1] / 2); // Center of graph in Y

		if (sx > sy) // The least ratio.
			sx = sy;
		else
			sy = sx;

		bck.beginTransform();
		bck.setIdentity();
		bck.translate(metrics.viewport[2] / 2, metrics.viewport[3] / 2, 0); // 4. Place the whole result at the center
																			// of the view port.
		if (rotation != 0)
			bck.rotate(rotation / (180.0 / Math.PI), 0, 0, 1); // 3. Eventually apply a Z axis rotation.
		bck.scale(sx, -sy, 0); // 2. Scale the graph to pixels. Scale -y since we reverse the view (top-left to
								// bottom-left).
		bck.translate(-tx, -ty, 0); // 1. Move the graph so that its real center is at (0,0).
		bck.endTransform();

		zoom = 1;

		center.set(tx, ty, 0);
		metrics.ratioPx2Gu = sx;
		metrics.loVisible.copy(metrics.lo);
		metrics.hiVisible.copy(metrics.hi);
	}

	/**
	 * Compute a transformation that pass from graph units (user space) to a pixel
	 * units (device space) so that the view (zoom and center) requested by the user
	 * is produced.
	 * 
	 * @return The transformation modified.
	 */
	public void userView() {
		double sx = 0.0;
		double sy = 0.0;
		double tx = 0.0;
		double ty = 0.0;
		double padXgu = paddingXgu() * 2;
		double padYgu = paddingYgu() * 2;
		double padXpx = paddingXpx() * 2;
		double padYpx = paddingYpx() * 2;
		double gw;
		if (gviewport != null)
			gw = gviewport[2] - gviewport[0];
		else
			gw = metrics.size.data[0];

		double gh;
		if (gviewport != null)
			gh = gviewport[3] - gviewport[1];
		else
			gh = metrics.size.data[1];

		if (padXpx > metrics.viewport[2])
			padXpx = metrics.viewport[2] / 10.0;
		if (padYpx > metrics.viewport[3])
			padYpx = metrics.viewport[3] / 10.0;

		sx = (metrics.viewport[2] - padXpx) / ((gw + padXgu) * zoom);
		sy = (metrics.viewport[3] - padYpx) / ((gh + padYgu) * zoom);

		tx = center.x;
		ty = center.y;

		if (sx > sy) // The least ratio.
			sx = sy;
		else
			sy = sx;

		bck.beginTransform();
		bck.setIdentity();
		bck.translate(metrics.viewport[2] / 2, metrics.viewport[3] / 2, 0); // 4. Place the whole result at the center
																			// of the view port.
		if (rotation != 0)
			bck.rotate(rotation / (180.0 / Math.PI), 0, 0, 1); // 3. Eventually apply a rotation.
		bck.scale(sx, -sy, 0); // 2. Scale the graph to pixels. Scale -y since we reverse the view (top-left to
								// bottom-left).
		bck.translate(-tx, -ty, 0); // 1. Move the graph so that the give center is at (0,0).
		bck.endTransform();

		metrics.ratioPx2Gu = sx;

		double w2 = (metrics.viewport[2] / sx) / 2f;
		double h2 = (metrics.viewport[3] / sx) / 2f;

		metrics.loVisible.set(center.x - w2, center.y - h2);
		metrics.hiVisible.set(center.x + w2, center.y + h2);
	}

	public double paddingXgu() {
		if (padding.units == Units.GU && padding.size() > 0)
			return padding.get(0);
		else
			return 0;
	}

	public double paddingYgu() {
		if (padding.units == Units.GU && padding.size() > 1)
			return padding.get(1);
		else
			return paddingXgu();
	}

	public double paddingXpx() {
		if (padding.units == Units.PX && padding.size() > 0)
			return padding.get(0);
		else
			return 0;
	}

	public double paddingYpx() {
		if (padding.units == Units.PX && padding.size() > 1)
			return padding.get(1);
		else
			return paddingXpx();
	}

	/**
	 * Set the graphic graph bounds (the lowest and highest points).
	 * 
	 * @param minx
	 *            Lowest abscissa.
	 * @param miny
	 *            Lowest ordinate.
	 * @param minz
	 *            Lowest depth.
	 * @param maxx
	 *            Highest abscissa.
	 * @param maxy
	 *            Highest ordinate.
	 * @param maxz
	 *            Highest depth.
	 */
	public void setBounds(double minx, double miny, double minz, double maxx, double maxy, double maxz) {
		metrics.setBounds(minx, miny, minz, maxx, maxy, maxz);
	}

	/** Set the graphic graph bounds from the graphic graph. */
	public void setBounds(GraphicGraph graph) {
		setBounds(graph.getMinPos().x, graph.getMinPos().y, 0, graph.getMaxPos().x, graph.getMaxPos().y, 0);
	}

	@Override
	public GraphMetrics getMetrics() {
		// TODO Auto-generated method stub
		return metrics;
	}

	/**
	 * Enable or disable automatic adjustment of the view to see the entire graph.
	 * 
	 * @param on
	 *            If true, automatic adjustment is enabled.
	 */
	public void setAutoFitView(boolean on) {
		if (autoFit && (!on)) {
			// We go from autoFit to user view, ensure the current center is at the
			// middle of the graph, and the zoom is at one.

			zoom = 1;
			center.set(metrics.lo.x + (metrics.size.data[0] / 2), metrics.lo.y + (metrics.size.data[1] / 2), 0);
		}

		autoFit = on;
	}

	public void setBackend(Backend backend) {
		this.bck = backend;
	}

	/**
	 * Transform a point in graph units into pixels.
	 * 
	 * @return The transformed point.
	 */
	public Point3 transformGuToPx(double x, double y, double z) {
		return bck.transform(x, y, 0);
	}

	/**
	 * Return the given point in pixels converted in graph units (GU) using the
	 * inverse transformation of the current projection matrix. The inverse matrix
	 * is computed only once each time a new projection matrix is created.
	 * 
	 * @param x
	 *            The source point abscissa in pixels.
	 * @param y
	 *            The source point ordinate in pixels.
	 * @return The resulting points in graph units.
	 */
	public Point3 transformPxToGu(double x, double y) {
		return bck.inverseTransform(x, y, 0);
	}

	protected boolean styleVisible(GraphicElement element) {
		Values visibility = element.getStyle().getVisibility();

		switch (element.getStyle().getVisibilityMode()) {
		case HIDDEN:
			return false;
		case AT_ZOOM:
			return (zoom == visibility.get(0));
		case UNDER_ZOOM:
			return (zoom <= visibility.get(0));
		case OVER_ZOOM:
			return (zoom >= visibility.get(0));
		case ZOOM_RANGE:
			if (visibility.size() > 1)
				return (zoom >= visibility.get(0) && zoom <= visibility.get(1));
			else
				return true;
		case ZOOMS:
			return Arrays.asList(Selector.Type.values()).contains(visibility.get(0));
		default:
			return true;
		}

	}

	public boolean isTextVisible(GraphicElement element) {
		Values visibility = element.getStyle().getTextVisibility();

		switch (element.getStyle().getTextVisibilityMode()) {
		case HIDDEN:
			return false;
		case AT_ZOOM:
			return (zoom == visibility.get(0));
		case UNDER_ZOOM:
			return (zoom <= visibility.get(0));
		case OVER_ZOOM:
			return (zoom >= visibility.get(0));
		case ZOOM_RANGE:
			if (visibility.size() > 1)
				return (zoom >= visibility.get(0) && zoom <= visibility.get(1));
			else
				return true;
		case ZOOMS:
			return Arrays.asList(Selector.Type.values()).contains(visibility.get(0));
		default:
			return true;
		}
	}

	/**
	 * True if the element should be visible on screen. The method used is to
	 * transform the center of the element (which is always in graph units) using
	 * the camera actual transformation to put it in pixel units. Then to look in
	 * the style sheet the size of the element and to test if its enclosing
	 * rectangle intersects the view port. For edges, its two nodes are used. As a
	 * speed-up by default if the camera is in automatic fitting mode, all element
	 * should be visible, and the test always returns true.
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
				return !spriteInvisible.contains(element.getId());
			default:
				return false;
			}
		}
	}

	/**
	 * Check if a sprite is visible in the current view port.
	 * 
	 * @param sprite
	 *            The sprite to check.
	 * @return True if visible.
	 */
	public boolean isSpriteVisible(GraphicSprite sprite) {
		return isSpriteIn(sprite, 0, 0, metrics.viewport[2], metrics.viewport[3]);
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
	public boolean isSpriteIn(GraphicSprite sprite, double X1, double Y1, double X2, double Y2) {
		Values size = getNodeOrSpriteSize(sprite);
		double w2 = metrics.lengthToPx(size, 0) / 2;
		double h2 = w2;
		if (size.size() > 1)
			h2 = metrics.lengthToPx(size, 1) / 2;

		// val src = new Point3()
		// getSpritePosition(sprite, src, Units.GU)
		// bck.transform(src)

		Point3 src = spritePositionPx(sprite);
		double x1 = src.x - w2;
		double x2 = src.x + w2;
		double y1 = src.y - h2;
		double y2 = src.y + h2;

		if (x2 < X1)
			return false;
		else if (y2 < Y1)
			return false;
		else if (x1 > X2)
			return false;
		else if (y1 > Y2)
			return false;
		else
			return true;

	}

	/**
	 * Check if an edge is visible in the current view port.
	 * 
	 * @param edge
	 *            The edge to check.
	 * @return True if visible.
	 */
	public boolean isEdgeVisible(GraphicEdge edge) {
		if (!((GraphicNode) edge.getNode0()).positionned || !((GraphicNode) edge.getNode1()).positionned) {
			return false;
		} else if (edge.hidden) {
			return false;
		} else {
			boolean node0Invis = nodeInvisible.contains(edge.getNode0().getId());
			boolean node1Invis = nodeInvisible.contains(edge.getNode1().getId());

			return !(node0Invis && node1Invis);
		}
	}

	/**
	 * Search for the first GraphicElement among those specified. Multiple elements
	 * are resolved by priority- {@link InteractiveElement.NODE} >
	 * {@link InteractiveElement.EDGE} > {@link InteractiveElement.SPRITE}, (in that
	 * order) that contains the point at coordinates (x, y).
	 *
	 * @param graph
	 *            The graph to search for.
	 * @param x
	 *            The point abscissa.
	 * @param y
	 *            The point ordinate.
	 * @return The first node or sprite at the given coordinates or null if nothing
	 *         found.
	 */
	@Override
	public GraphicElement findGraphicElementAt(GraphicGraph graph, EnumSet<InteractiveElement> types, double x,
			double y) {
		double xT = x + metrics.viewport[0];
		double yT = y + metrics.viewport[1];

		if (types.contains(InteractiveElement.NODE)) {
			Optional<Node> node = graph.nodes().filter(n -> nodeContains((GraphicElement) n, xT, yT)).findFirst();
			if (node.isPresent()) {
				if (isVisible((GraphicElement) node.get())) {
					return (GraphicElement) node.get();
				}
			}
		}
		if (types.contains(InteractiveElement.EDGE)) {
			Optional<Edge> edge = graph.edges().filter(e -> edgeContains((GraphicElement) e, xT, yT)).findFirst();
			if (edge.isPresent()) {
				if (isVisible((GraphicElement) edge.get())) {
					return (GraphicElement) edge.get();
				}
			}
		}
		if (types.contains(InteractiveElement.SPRITE)) {
			Optional<GraphicSprite> sprite = graph.sprites().filter(s -> spriteContains(s, xT, yT)).findFirst();
			if (sprite.isPresent()) {
				if (isVisible((GraphicElement) sprite.get())) {
					return (GraphicElement) sprite.get();
				}
			}
		}
		return null;
	}

	public double[] graphViewport() {
		return gviewport;
	}

	@Override
	public Collection<GraphicElement> allGraphicElementsIn(GraphicGraph graph, EnumSet<InteractiveElement> types,
			double x1, double y1, double x2, double y2) {
		// add offset of viewport, because find(...) is always called without offset
		// in most cases the offset of the viewport is 0 anyway
		double x1T = x1 + metrics.viewport[0];
		double y1T = y1 + metrics.viewport[1];
		double x2T = x2 + metrics.viewport[0];
		double y2T = y2 + metrics.viewport[1];

		List<GraphicElement> elts = new ArrayList<GraphicElement>();

		Stream nodeStream = null;
		Stream edgeStream = null;
		Stream spriteStream = null;

		if (types.contains(InteractiveElement.NODE)) {

			nodeStream = graph.nodes().filter(n -> isNodeIn((GraphicNode) n, x1T, y1T, x2T, y2T));
		} else {
			nodeStream = Stream.empty();
		}

		if (types.contains(InteractiveElement.EDGE)) {
			edgeStream = graph.edges().filter(e -> isEdgeIn((GraphicEdge) e, x1T, y1T, x2T, y2T));
		} else {
			edgeStream = Stream.empty();
		}

		if (types.contains(InteractiveElement.SPRITE)) {
			spriteStream = graph.sprites().filter(e -> isSpriteIn((GraphicSprite) e, x1T, y1T, x2T, y2T));
		} else {
			spriteStream = Stream.empty();
		}

		Stream<GraphicElement> s = Stream.concat(nodeStream, Stream.concat(edgeStream, spriteStream));
		return s.collect(Collectors.toList());
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
}
