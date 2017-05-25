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
package org.graphstream.util.geom;

/**
 * 2D point.
 * 
 * A Point2 is a 2D location in an affine space described by three values along
 * the X, and Y axes. This differs from the Vector3 and Vector4 classes in that
 * it is only 2D and has no vector arithmetic bound to it (to points cannot be
 * added, this would have no mathematical meaning).
 * 
 * @author Antoine Dutot
 * @since 20001121 creation
 * @version 0.1
 */
public class Point2 implements java.io.Serializable {
	private static final long serialVersionUID = 965985679540486895L;

	/**
	 * Specific point at (0,0).
	 */
	public static final Point2 NULL_POINT2 = new Point2(0, 0);

	/**
	 * X axis value.
	 */
	public final double x;

	/**
	 * Y axis value.
	 */
	public final double y;


	/**
	 * New 2D point at (0,0).
	 */
	public Point2() {
		this.x = 0;
		this.y = 0;
	}

	/**
	 * New 2D point at (x,y).
	 * @param x the x value
	 * @param y the y value
	 */
	public Point2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * New copy of other.
	 * @param other the other Point2, to copy
	 */
	public Point2(Point2 other) {
		this.x = other.x;
		this.y = other.y;
	}

	/**
	 * @return true if all components are equal to zero
	 */
	public boolean isZero() {
		return (x == 0 && y == 0);
	}

	/**
	 * Create a new point linear interpolation of this and <code>other</code>.
	 * The new point is located between this and <code>other</code> if
	 * <code>factor</code> is between 0 and 1 (0 yields this point, 1 yields the
	 * <code>other</code> point).
	 *
	 * @param other the other Point2 to interpolate to
	 * @param factor the factor by which to interpolate
	 * @return a new Point2 representing the interpolation between this point and other
	 */
	public Point2 interpolate(Point2 other, double factor) {
		return new Point2(
				x + ((other.x - x) * factor),
				y + ((other.y - y) * factor));
	}

	/**
	 * Cartesion distance between this and <code>other</code>.
	 *
	 * @param other the point to get the distance to
	 * @return the cartesian distance between this and other
	 */
	public double distance(Point2 other) {
		double xx = other.x - x;
		double yy = other.y - y;
		return Math.sqrt((xx * xx) + (yy * yy));
	}

	/**
	 * Cartesion distance between this and the point (x,y).
	 *
\	 * @return the cartesian distance between this and other
	 * @param x the other x value
	 * @param y the other y value
	 * @return the cartesion distance between this and another (x,y)
	 */
	public double distance(double x, double y) {
		double xx = x - this.x;
		double yy = y - this.y;
		return Math.sqrt((xx * xx) + (yy * yy));
	}

	/**
	 * Create a new point that represents this point added to <code>dl</code> in each component.
	 *
	 * @param dl the value to add to each component in this point
	 * @return a new point representing the current point summed in each component with dl
	 */
	public Point2 add(double dl) {
		return new Point2(x + dl, y + dl);
	}

	/**
	 * Create a new point that represents this point added to <code>dx</code> in the x component.
	 *
	 * @param dx the value to add to the x component of this point
	 * @return a new point representing the current point summed in the x component with dx
	 */
	public Point2 addX(double dx) {
		return new Point2(x + dx, y);
	}

	/**
	 * Create a new point that represents this point added to <code>dy</code> in the y component.
	 *
	 * @param dy the value to add to the y component of this point
	 * @return a new point representing the current point summed in the y component with dy
	 */
	public Point2 addY(double dy) {
		return new Point2(x, y + dy);
	}

	/**
	 * Create a new point that represents this point added to (dx,dy). This is equivalent to
	 * <code>this.addX(dx).addY(dy)</code>.
	 *
	 * @param dx the value to add to the x component of this point
	 * @param dy the value to add to the y component of this point
	 * @return a new point representing the sum of this point and (dx,dy)
	 */
	public Point2 add(double dx, double dy) {
		return new Point2(x + dx, y + dy);
	}

	/**
	 * Create a new point that represents this point added to another point, p.
	 *
	 * @param p the other point to add to the current point
	 * @return a new point representing the sum of this point and p
	 */
	public Point2 add(Point2 p) {
		return new Point2(x + p.x, y + p.y);
	}

	/**
	 * Multiply each component of this point by a scalar.
	 * 
	 * @param scalar The scalar to multiply each component by
	 * @return a new point representing the current point with each component scaled
	 */
	public Point2 mul(double scalar) {
		return new Point2(x * scalar, y * scalar);
	}

	/**
	 * Multiply each component of this point by scaling factors sx, sy.
	 *
	 * @param sx the scalar to multiply the current x component by
	 * @param sy the scalar to multiply the current y component by
	 * @return a new point representing the current point with each component scaled
	 */
	public Point2 mul(double sx, double sy) {
		return new Point2(x * sx, y * sy);
	}

	/**
	 * Multiply each component of this point by the components in point s.
	 *
	 * @param s the point to multiply by
	 * @return a new point representing the current point with each component multiplied by the respective point in s
	 */
	public Point2 mul(Point2 s) {
		return new Point2(x * s.x, y * s.y);
	}

	/**
	 * Returns the dot product of the current vector with the components (ox,oy).
	 *
	 * @param ox the x component of the point to return the dot product of
	 * @param oy the y component of the point to return the dot product of
	 * @return the dot product of this point and (ox,oy)
	 */
	public double dot(double ox, double oy) {
		return ((x * ox) + (y * oy));
	}

	/**
	 * Returns the dot product of the current vector with the point other.
	 *
	 * @param other the point to return the dot product of
	 * @return the dot product of this point and (ox,oy,oz)
	 */
	public double dot(Point2 other) {
		return ((x * other.x) + (y * other.y));
	}

	/**
	 * @return the cartesion length of this point
	 */
	public double length() {
		return Math.sqrt((x * x) + (y * y));
	}

	/**
	 * @return a new point normalized to be a unit vector of the current point
	 */
	public Point2 normalize() {
		double len = length();

		if (len != 0) {
			return new Point2(x/len, y/len);
		} else {
			return this;
		}
	}

	@Override
	public String toString() {
		StringBuffer buf;

		buf = new StringBuffer("Point2[");

		buf.append(x);
		buf.append('|');
		buf.append(y);
		buf.append("]");

		return buf.toString();
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Point2 point2 = (Point2) o;

		return Double.compare(point2.x, x) == 0 && Double.compare(point2.y, y) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}