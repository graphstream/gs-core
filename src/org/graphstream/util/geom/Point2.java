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
	// Attributes

	private static final long serialVersionUID = 965985679540486895L;

	/**
	 * X axis value.
	 */
	public final double x;

	/**
	 * Y axis value.
	 */
	public final double y;

	// Attributes -- Shared

	/**
	 * Specific point at (0,0).
	 */
	public static final Point2 NULL_POINT2 = new Point2(0, 0);

	// Constructors

	/**
	 * New 2D point at (0,0).
	 */
	public Point2() {
		this.x = 0;
		this.y = 0;
	}

	/**
	 * New 2D point at (x,y).
	 */
	public Point2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * New copy of other.
	 */
	public Point2(Point2 other) {
		this.x = other.x;
		this.y = other.y;
	}

	// Accessors

	/**
	 * Are all components to zero?.
	 */
	public boolean isZero() {
		return (x == 0 && y == 0);
	}

	// /**
	// * Is other equal to this ?
	// */
	// public boolean
	// equals( const Point2 < double > & other ) const
	// {
	// return( x == other.x
	// and y == other.y
	// and z == other.z );
	// }

	/**
	 * Create a new point linear interpolation of this and <code>other</code>.
	 * The new point is located between this and <code>other</code> if
	 * <code>factor</code> is between 0 and 1 (0 yields this point, 1 yields the
	 * <code>other</code> point).
	 */
	public Point2 interpolate(Point2 other, double factor) {
		Point2 p = new Point2(x + ((other.x - x) * factor), y
				+ ((other.y - y) * factor));

		return p;
	}

	/**
	 * Distance between this and <code>other</code>.
	 */
	public double distance(Point2 other) {
		double xx = other.x - x;
		double yy = other.y - y;
		return Math.abs(Math.sqrt((xx * xx) + (yy * yy)));
	}

	// Commands -- moving

	/**
	 * Move of given vector (dl,dl).
	 */
	public Point2 add(double dl) {
		return new Point2(x + dl, y + dl);
	}

	/**
	 * Move of given vector (dx,dy).
	 */
	public Point2 add(double dx, double dy) {
		return new Point2(x + dx, y + dy);
	}

	/**
	 * Move of given point <code>p</code>.
	 */
	public Point2 add(Point2 p) {
		return new Point2(x + p.x, y + p.y);
	}

	/**
	 * Move horizontally of dx.
	 */
	public Point2 addX(double dx) {
		return new Point2(x + dx, y);
	}

	/**
	 * Move vertically of dy.
	 */
	public Point2 addY(double dy) {
		return new Point2(x, y + dy);
	}
	/**
	 * Scale of factor (sx,sy).
	 */
	public Point2 mul(double scalar) {
		return new Point2(x * scalar, y * scalar);
	}

	/**
	 * Scale of factor (sx,sy).
	 */
	public Point2 mul(double sx, double sy) {
		return new Point2(x * sx, y * sy);
	}

	/**
	 * Scale by factor s.
	 */
	public Point2 mul(Point2 s) {
		return new Point2(x * s.x, y * s.y);
	}

	public double dot(double ox, double oy) {
		return ((x * ox) + (y * oy));
	}

	/**
	 * Dot product of this and other.
	 */
	public double dot(Point2 other) {
		return ((x * other.x) + (y * other.y));
	}

	/**
	 * Cartesian length.
	 */
	public double length() {
		return Math.sqrt((x * x) + (y * y));
	}

	/**
	 * Transform this into an unit vector.
	 * 
	 * @return the new vector
	 */
	public Point2 normalize() {
		double len = length();

		if (len != 0) {
			return new Point3(x/len, y/len);
		} else {
			return this;
		}
	}

	// Commands -- misc.

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