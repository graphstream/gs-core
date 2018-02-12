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
 * 3D point.
 * 
 * A Point3 is a 3D location in an affine space described by three values along
 * the X, the Y and the Z axis. Note the difference with Vector3 wich is defined
 * as an array and ensures that the three coordinates X, Y and Z are consecutive
 * in memory. Here there are three separate attributes. Further, a point has no
 * vector arithmetic bound to it (to points cannot be added, this would have no
 * mathematical meaning).
 * 
 * @author Antoine Dutot
 * @since 19990829
 * @version 0.1
 */
public class Point3 extends Point2 implements java.io.Serializable {
	private static final long serialVersionUID = 5971336344439693816L;

	/**
	 * Specific point at (0,0,0).
	 */
	public static final Point3 NULL_POINT3 = new Point3(0, 0, 0);

	/**
	 * Z axis value.
	 */
	public final double z;


	/**
	 * New 3D point at (0,0,0).
	 */
	public Point3() {
		super();
		this.z = 0;
	}

	/**
	 * New 3D point at (x,y,0).
	 * @param x the x value
	 * @param y the y value
	 */
	public Point3(double x, double y) {
		super(x, y);
		this.z = 0;
	}

	/**
	 * New 3D point at (x,y,z).
	 * @param x the x value
	 * @param y the y value
	 * @param z the z value
	 */
	public Point3(double x, double y, double z) {
		super(x, y);
		this.z = z;
	}

	/**
	 * New copy of other.
	 * @param other the other Point3, to copy
	 */
	public Point3(Point3 other) {
		super(other);
		this.z = other.z;
	}

	@Override
	public boolean isZero() {
		return (x == 0 && y == 0 && z == 0);
	}

	/**
	 * Create a new point linear interpolation of this and <code>other</code>.
	 * The new point is located between this and <code>other</code> if
	 * <code>factor</code> is between 0 and 1 (0 yields this point, 1 yields the
	 * <code>other</code> point).
	 *
	 * @param other the other Point3 to interpolate to
	 * @param factor the factor by which to interpolate
	 * @return a new Point3 representing the interpolation between this point and other
	 */
	public Point3 interpolate(Point3 other, double factor) {
		return new Point3(
				x + ((other.x - x) * factor),
				y + ((other.y - y) * factor),
				z + ((other.z - z) * factor));
	}

	/**
	 * Cartesion distance between this and <code>other</code>.
	 *
	 * @param other the point to get the distance to
	 * @return the cartesian distance between this and other
	 */
	public double distance(Point3 other) {
		double xx = other.x - x;
		double yy = other.y - y;
		double zz = other.z - z;
		return Math.sqrt((xx * xx) + (yy * yy) + (zz * zz));
	}

	/**
	 * Cartesion distance between this and the point (x,y,z).
	 *
\	 * @return the cartesian distance between this and other
	 * @param x the other x value
	 * @param y the other y value
	 * @param z the other z value
	 * @return the cartesion distance between this and another (x,y,z)
	 */
	public double distance(double x, double y, double z) {
		double xx = x - this.x;
		double yy = y - this.y;
		double zz = z - this.z;
		return Math.sqrt((xx * xx) + (yy * yy) + (zz * zz));
	}

	@Override
	public Point3 add(double dl) {
		return new Point3(x + dl, y + dl, z + dl);
	}

	@Override
	public Point3 addX(double dx) {
		return new Point3(x + dx, y, z);
	}

	@Override
	public Point3 addY(double dy) {
		return new Point3(x, y + dy, z);
	}

	/**
	 * Create a new point that represents this point added to <code>dz</code> in the z component.
	 *
	 * @param dz the value to add to the z component of this point
	 * @return a new point representing the current point summed in the z component with dz
	 */
	public Point3 addZ(double dz) {
		return new Point3(x, y, z + dz);
	}

	@Override
	public Point3 add(double dx, double dy) {
		return new Point3(x + dx, y + dy, z);
	}

	@Override
	public Point3 add(Point2 p) {
		return new Point3(x + p.x, y + p.y, z);
	}

	/**
	 * Create a new point that represents this point added to (dx,dy,dz). This is equivalent to
	 * <code>this.addX(dx).addY(dy).addZ(dz)</code>.
	 *
	 * @param dx the value to add to the x component of this point
	 * @param dy the value to add to the y component of this point
	 * @param dz the value to add to the z component of this point
	 * @return a new point representing the sum of this point and (dx,dy,dz)
	 */
	public Point3 add(double dx, double dy, double dz) {
		return new Point3(x + dx, y + dy, z + dz);
	}

	/**
	 * Create a new point that represents this point added to another point, p.
	 *
	 * @param p the other point to add to the current point
	 * @return a new point representing the sum of this point and p
	 */
	public Point3 add(Point3 p) {
		return new Point3(x + p.x, y + p.y, z + p.z);
	}

	@Override
	public Point3 mul(double scalar) {
		return new Point3(x * scalar, y * scalar, z * scalar);
	}

	/**
	 * Multiply each component of this point by scaling factors sx, sy, sz.
	 *
	 * @param sx the scalar to multiply the current x component by
	 * @param sy the scalar to multiply the current y component by
	 * @param sz the scalar to multiply the current z component by
	 * @return a new point representing the current point with each component scaled
	 */
	public Point3 mul(double sx, double sy, double sz) {
		return new Point3(x * sx, y * sy, z * sz);
	}

	/**
	 * Multiply each component of this point by the components in point s.
	 *
	 * @param s the point to multiply by
	 * @return a new point representing the current point with each component multiplied by the respective point in s
	 */
	public Point3 mul(Point3 s) {
		return new Point3(x * s.x, y * s.y, z * s.z);
	}

	/**
	 * Returns the dot product of the current vector with the point other.
	 *
	 * @param other the point to return the dot product of
	 * @return the dot product of this point and (ox,oy,oz)
	 */
	public double dot(Point3 other) {
		return ((x * other.x) + (y * other.y) + (z * other.z));
	}

	/**
	 * Returns the dot product of the current vector with the components (ox,oy,oz).
	 *
	 * @param ox the x component of the point to return the dot product of
	 * @param oy the y component of the point to return the dot product of
	 * @param oz the z component of the point to return the dot product of
	 * @return the dot product of this point and (ox,oy,oz)
	 */
	public double dot(double ox, double oy, double oz) {
		return ((x * ox) + (y * oy) + (z * oz));
	}

	@Override
	public double length() {
		return Math.sqrt((x * x) + (y * y) + (z * z));
	}

	/**
	 * Return the cross product of this point and another point, other.
	 *
	 * @param other the point to return the cross product of
	 * @return the cross product
	 */
	public Point3 cross(Point3 other) {
		double newx = (y * other.z) - (z * other.y);
		double newy = (z * other.x) - (x * other.z);
		double newz = (x * other.y) - (y * other.x);
		return new Point3(newx, newy, newz);
	}

	@Override
	public Point3 normalize() {
		double len = length();

		if (len != 0) {
			return new Point3(x/len, y/len, z/len);
		} else {
			return this;
		}
	}

	@Override
	public String toString() {
		StringBuffer buf;

		buf = new StringBuffer("Point3[");

		buf.append(x);
		buf.append('|');
		buf.append(y);
		buf.append('|');
		buf.append(z);
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
		Point3 point3 = (Point3) o;

		return super.equals(o) && Double.compare(point3.z, z) == 0; 
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}