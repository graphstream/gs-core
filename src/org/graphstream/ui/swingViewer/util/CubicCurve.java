/*
 * Copyright 2006 - 2011 
 *     Stefan Balev 	<stefan.balev@graphstream-project.org>
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
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

import org.graphstream.ui.geom.*;
import java.awt.geom.*;

/** Utility methods to deal with cubic Bézier curves. */
public class CubicCurve {
	/**
	 * Evaluate a cubic Bézier curve according to control points `x0`, `x1`,
	 * `x2` and `x3` and return the position at parametric position `t` of the
	 * curve.
	 * 
	 * @return The coordinate at parametric position `t` on the curve.
	 */
	public static double eval(double x0, double x1, double x2, double x3, double t) {
		double tt = (1f - t);

		return x0 * (tt * tt * tt) + 3f * x1 * t * (tt * tt) + 3f * x2
				* (t * t) * tt + x3 * (t * t * t);
	}

	/**
	 * Evaluate a cubic Bézier curve according to control points `p0`, `p1`,
	 * `p2` and `p3` and return the position at parametric position `t` of the
	 * curve.
	 * 
	 * @return The point at parametric position `t` on the curve.
	 */
	public static Point2 eval(Point2 p0, Point2 p1, Point2 p2, Point2 p3,
			double t) {
		return new Point2(eval(p0.x, p1.x, p2.x, p3.x, t), eval(p0.y, p1.y,
				p2.y, p3.y, t));
	}

	/**
	 * Evaluate a cubic Bézier curve according to control points `p0`, `p1`,
	 * `p2` and `p3` and return the position at parametric position `t` of the
	 * curve.
	 * 
	 * @return The point at parametric position `t` on the curve.
	 */
	public static Point2D.Double eval(Point2D.Double p0, Point2D.Double p1,
			Point2D.Double p2, Point2D.Double p3, double t) {
		return new Point2D.Double(eval(p0.x, p1.x, p2.x, p3.x, t), eval(p0.y,
				p1.y, p2.y, p3.y, t));
	}

	/**
	 * Evaluate a cubic Bézier curve according to control points `p0`, `p1`,
	 * `p2` and `p3` and store the position at parametric position `t` of the
	 * curve in `result`.
	 * 
	 * @return the given reference to `result`.
	 */
	public static Point2 eval(Point2 p0, Point2 p1, Point2 p2, Point2 p3,
			double t, Point2 result) {
		result.set(eval(p0.x, p1.x, p2.x, p3.x, t),
				eval(p0.y, p1.y, p2.y, p3.y, t));
		return result;
	}

	/**
	 * Derivative of a cubic Bézier curve according to control points `x0`,
	 * `x1`, `x2` and `x3` at parametric position `t` of the curve.
	 * 
	 * @return The derivative at parametric position `t` on the curve.
	 */
	public static double derivative(double x0, double x1, double x2, double x3,
			double t) {
		return 3 * (x3 - 3 * x2 + 3 * x1 - x0) * t * t + 2
				* (3 * x2 - 6 * x1 + 3 * x0) * t + (3 * x1 - 3 * x0);
	}

	/**
	 * Derivative point of a cubic Bézier curve according to control points
	 * `x0`, `x1`, `x2` and `x3` at parametric position `t` of the curve.
	 * 
	 * @return The derivative point at parametric position `t` on the curve.
	 */
	public static Point2 derivative(Point2 p0, Point2 p1, Point2 p2, Point3 p3,
			double t) {
		return new Point2(derivative(p0.x, p1.x, p2.x, p3.x, t), derivative(
				p0.y, p1.y, p2.y, p3.y, t));
	}

	/**
	 * Store in `result` the derivative point of a cubic Bézier curve according
	 * to control points `x0`, `x1`, `x2` and `x3` at parametric position `t` of
	 * the curve.
	 * 
	 * @return the given reference to `result`.
	 */
	public static Point2 derivative(Point2 p0, Point2 p1, Point2 p2, Point3 p3,
			double t, Point2 result) {
		result.set(derivative(p0.x, p1.x, p2.x, p3.x, t),
				derivative(p0.y, p1.y, p2.y, p3.y, t));
		return result;
	}

	/**
	 * The perpendicular vector to the curve defined by control points `p0`,
	 * `p1`, `p2` and `p3` at parametric position `t`.
	 * 
	 * @return A vector perpendicular to the curve at position `t`.
	 */
	public static Vector2 perpendicular(Point2 p0, Point2 p1, Point2 p2,
			Point2 p3, double t) {
		return new Vector2(derivative(p0.y, p1.y, p2.y, p3.y, t), -derivative(
				p0.x, p1.x, p2.x, p3.x, t));
	}

	/**
	 * Store in `result` the perpendicular vector to the curve defined by
	 * control points `p0`, `p1`, `p2` and `p3` at parametric position `t`.
	 * 
	 * @return the given reference to `result`.
	 */
	public static Vector2 perpendicular(Point2 p0, Point2 p1, Point2 p2,
			Point2 p3, double t, Vector2 result) {
		result.set(derivative(p0.y, p1.y, p2.y, p3.y, t),
				-derivative(p0.x, p1.x, p2.x, p3.x, t));
		return result;
	}

	/**
	 * The perpendicular vector to the curve defined by control points `p0`,
	 * `p1`, `p2` and `p3` at parametric position `t`.
	 * 
	 * @return A vector perpendicular to the curve at position `t`.
	 */
	public static Point2D.Double perpendicular(Point2D.Double p0,
			Point2D.Double p1, Point2D.Double p2, Point2D.Double p3, double t) {
		return new Point2D.Double(derivative(p0.y, p1.y, p2.y, p3.y, t),
				-derivative(p0.x, p1.x, p2.x, p3.x, t));
	}
}