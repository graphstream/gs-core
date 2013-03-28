/*
 * Copyright 2006 - 2013
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
package org.graphstream.ui.swingViewer.basicRenderer.shapes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import org.graphstream.ui.graphicGraph.GraphicEdge;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.graphicGraph.StyleGroup;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.StrokeMode;
import org.graphstream.ui.swingViewer.util.GraphMetrics;

public class Arrow extends Shape {
	protected Color fillColor = Color.BLACK;

	protected Color strokeColor = Color.BLACK;

	protected int lengthGu = 0;

	protected int widthGu = 0;

	protected double x, y;

	protected Path2D.Float path = new Path2D.Float();

	public void setArrowLengthGu(int lengthGu) {
		this.lengthGu = lengthGu;
	}

	public void setArrowWidthGu(int widthGu) {
		this.widthGu = widthGu;
	}

	public void setFillColor(Color color) {
		fillColor = color;
	}

	public void setStrokeColor(Color color) {
		strokeColor = color;
	}

	@Override
	public void renderFill(Graphics2D g, GraphMetrics metrics) {
		g.setColor(fillColor);
		g.fill(path);
	}

	@Override
	public void renderStroke(Graphics2D g, GraphMetrics metrics) {
		g.setColor(strokeColor);
		g.draw(path);
	}

	// Utility

	protected void setPositionAndShape(GraphicEdge edge, GraphMetrics metrics) {
		// Compute the direction vector and some lengths.

		x = edge.to.x;
		y = edge.to.y;
		double vx = x - edge.from.x;
		double vy = y - edge.from.y;
		double off = evalTargetRadius(edge, metrics);

		// Normalise the vectors.

		double d = (double) Math.sqrt(vx * vx + vy * vy);

		vx /= d;
		vy /= d;

		// Choose an arrow "length".

		x -= vx * off;
		y -= vy * off;

		setShapeAt(x, y, vx, vy);
	}

	/**
	 * Compute the shape of the arrow.
	 * 
	 * @param x
	 *            Point at which the edge crosses the node shape.
	 * @param y
	 *            Point at which the edge crosses the node shape.
	 * @param dx
	 *            The arrow vector (and length).
	 * @param dy
	 *            The arrow vector (and length).
	 */
	protected void setShapeAt(double x, double y, double dx, double dy) {
		// Compute the edge vector (1) and the perpendicular vector (2).

		double dx2 = dy;
		double dy2 = -dx;

		// Normalise the vectors.

		double d2 = (double) Math.sqrt(dx2 * dx2 + dy2 * dy2);

		dx2 /= d2;
		dy2 /= d2;

		// Choose an arrow "width".

		dx2 *= widthGu;
		dy2 *= widthGu;

		// Create a polygon.

		path.reset();
		path.moveTo(x, y);
		path.lineTo(x - dx + dx2, y - dy + dy2);
		path.lineTo(x - dx - dx2, y - dy - dy2);
		path.closePath();
	}

	/**
	 * Evaluate the position of the arrow to avoid putting it above or under the
	 * target node.
	 * 
	 * @param edge
	 *            The edge.
	 * @param metrics
	 *            The metrics.
	 * @return The length from the node centre along the edge to position the
	 *         arrow.
	 */
	protected double evalTargetRadius(GraphicEdge edge, GraphMetrics metrics) {
		GraphicNode target = edge.to;
		StyleGroup group = target.getStyle();
		double w = metrics.lengthToGu(group.getSize(), 0);
		double h = group.getSize().size() > 1 ? metrics.lengthToGu(
				group.getSize(), 1) : w;

		if (w == h) {
			double b = group.getStrokeMode() != StrokeMode.NONE ? metrics
					.lengthToGu(group.getStrokeWidth()) : 0;
			return ((w / 2) + b);
		} else {
			return evalEllipseRadius(edge, w, h);
		}
	}

	/**
	 * Compute the length of a vector along the edge from the ellipse centre to
	 * the intersection between the edge and the ellipse.
	 * 
	 * @param edge
	 *            The edge representing the vector.
	 * @param w
	 *            The ellipse first radius (width/2).
	 * @param h
	 *            The ellipse second radius (height/2).
	 * @return The length of the radius along the edge vector.
	 */
	protected double evalEllipseRadius(GraphicEdge edge, double w, double h) {
		// Vector of the entering edge.

		double dx;
		double dy;

		dx = edge.to.x - edge.from.x;
		dy = edge.to.y - edge.from.y;

		// The entering edge must be deformed by the ellipse ratio to find the
		// correct angle.

		dy *= (w / h); // I searched a lot to find this line was missing ! Tsu !
						// This comment is in memory of this long search.

		// Find the angle of the entering vector with (1,0).

		double d = (double) Math.sqrt(dx * dx + dy * dy);
		double a = dx / d;

		// Compute the coordinates at which the entering vector and the ellipse
		// cross.

		a = (double) Math.acos(a);
		dx = (double) Math.cos(a) * w;
		dy = (double) Math.sin(a) * h;

		// The distance from the ellipse centre to the crossing point of the
		// ellipse and
		// vector. Yo !

		return Math.sqrt(dx * dx + dy * dy);
	}
}
