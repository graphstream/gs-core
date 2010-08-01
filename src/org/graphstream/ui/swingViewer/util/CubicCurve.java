/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */
package org.graphstream.ui.swingViewer.util;

import org.graphstream.ui.geom.*;
import java.awt.geom.*;

/** Utility methods to deal with cubic Bézier curves. */
public class CubicCurve
{
	/** Evaluate a cubic Bézier curve according to control points `x0`, `x1`, `x2` and `x3` and 
	 * return the position at parametric position `t` of the curve.
	 * @return The coordinate at parametric position `t` on the curve. */
	public static float eval( float x0, float x1, float x2, float x3, float t ) {
		float tt = ( 1f - t );
		
		return x0 * (tt*tt*tt) + 3f * x1 * t * (tt*tt) + 3f * x2 * (t*t) * tt + x3 * (t*t*t);
	}

	/** Evaluate a cubic Bézier curve according to control points `p0`, `p1`, `p2` and `p3` and 
	 * return the position at parametric position `t` of the curve.
	 * @return The point at parametric position `t` on the curve. */
	public static Point2 eval( Point2 p0, Point2 p1, Point2 p2, Point2 p3, float t ) {
		return new Point2( eval( p0.x, p1.x, p2.x, p3.x, t ), eval( p0.y, p1.y, p2.y, p3.y, t ) );
	}

	/** Evaluate a cubic Bézier curve according to control points `p0`, `p1`, `p2` and `p3` and 
	 * return the position at parametric position `t` of the curve.
	 * @return The point at parametric position `t` on the curve. */
	public static Point2D.Float eval( Point2D.Float p0, Point2D.Float p1, Point2D.Float p2, Point2D.Float p3, float t ) {
		return new Point2D.Float( eval( p0.x, p1.x, p2.x, p3.x, t ), eval( p0.y, p1.y, p2.y, p3.y, t ) );
	}
	
	/** Evaluate a cubic Bézier curve according to control points `p0`, `p1`, `p2` and `p3` and 
	 * store the position at parametric position `t` of the curve in `result`.
	 * @return the given reference to `result`. */
	public static Point2 eval( Point2 p0, Point2 p1, Point2 p2, Point2 p3, float t, Point2 result ) {
		result.set( eval( p0.x, p1.x, p2.x, p3.x, t ), eval( p0.y, p1.y, p2.y, p3.y, t ) );
		return result;
	}
	
	/** Derivative of a cubic Bézier curve according to control points `x0`, `x1`, `x2` and `x3` 
	 * at parametric position `t` of the curve.
	 * @return The derivative at parametric position `t` on the curve. */
	public static float derivative( float x0, float x1, float x2, float x3, float t ) {
		return 3 * ( x3 - 3 * x2 + 3 * x1 - x0 ) * t*t +
				2 * ( 3 * x2 - 6 * x1 + 3 * x0 ) * t +
				    ( 3 * x1 - 3 * x0 );
	}
	
	/** Derivative point of a cubic Bézier curve according to control points `x0`, `x1`, `x2` and
	 * `x3` at parametric position `t` of the curve.
	 * @return The derivative point at parametric position `t` on the curve. */
	public static Point2 derivative( Point2 p0, Point2 p1, Point2 p2, Point3 p3, float t ) {
		return new Point2( derivative( p0.x, p1.x, p2.x, p3.x, t ), derivative( p0.y, p1.y, p2.y, p3.y, t ) );
	}

	/** Store in `result` the derivative point of a cubic Bézier curve according to control points
	 * `x0`, `x1`, `x2` and `x3` at parametric position `t` of the curve.
	 * @return the given reference to `result`. */
	public static Point2 derivative( Point2 p0, Point2 p1, Point2 p2, Point3 p3, float t, Point2 result ) {
		result.set( derivative( p0.x, p1.x, p2.x, p3.x, t ), derivative( p0.y, p1.y, p2.y, p3.y, t ) );
		return result;
	}

	/** The perpendicular vector to the curve defined by control points `p0`, `p1`, `p2` and `p3`
	 * at parametric position `t`.
	 * @return A vector perpendicular to the curve at position `t`. */
	public static Vector2 perpendicular( Point2 p0, Point2 p1, Point2 p2, Point2 p3, float t ) {
		return new Vector2( derivative( p0.y, p1.y, p2.y, p3.y, t ), -derivative( p0.x, p1.x, p2.x, p3.x, t ) );
	}

	/** Store in `result` the perpendicular vector to the curve defined by control points `p0`,
	 * `p1`, `p2` and `p3`  at parametric position `t`.
	 * @return the given reference to `result`. */
	public static Vector2 perpendicular( Point2 p0, Point2 p1, Point2 p2, Point2 p3, float t, Vector2 result ) {
		result.set( derivative( p0.y, p1.y, p2.y, p3.y, t ), -derivative( p0.x, p1.x, p2.x, p3.x, t ) );
		return result;
	}
	
	/** The perpendicular vector to the curve defined by control points `p0`, `p1`, `p2` and `p3`
	 * at parametric position `t`.
	 * @return A vector perpendicular to the curve at position `t`. */
	public static Point2D.Float perpendicular( Point2D.Float p0, Point2D.Float p1, Point2D.Float p2, Point2D.Float p3, float t ) {
		return new Point2D.Float( derivative( p0.y, p1.y, p2.y, p3.y, t ), -derivative( p0.x, p1.x, p2.x, p3.x, t ) );
	}
}