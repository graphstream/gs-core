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
 * @since 2009-12-22
 * 
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Alex Bowen <bowen.a@gmail.com>
 * @author kitskub <kitskub@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.ui.geom;

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
	// Attributes

	private static final long serialVersionUID = 5971336344439693816L;

	/**
	 * Z axis value.
	 */
	public double z;

	// Attributes -- Shared

	/**
	 * Specific point at (0,0,0).
	 */
	public static final Point3 NULL_POINT3 = new Point3(0, 0, 0);

	// Constructors

	/**
	 * New 3D point at(0,0,0).
	 */
	public Point3() {
	}

	/**
	 * New 3D point at (x,y,0).
	 */
	public Point3(double x, double y) {
		set(x, y, 0);
	}

	/**
	 * New 3D point at(x,y,z).
	 */
	public Point3(double x, double y, double z) {
		set(x, y, z);
	}

	/**
	 * New copy of other.
	 */
	public Point3(Point3 other) {
		copy(other);
	}

	public Point3(Vector3 vec) {
		copy(vec);
	}

	public Point3(float data[]) {
		this(0, data);
	}

	public Point3(double data[]) {
		this(0, data);
	}

	public Point3(int start, float data[]) {
		if (data != null) {
			if (data.length > start + 0)
				x = data[start + 0];
			if (data.length > start + 1)
				y = data[start + 1];
			if (data.length > start + 2)
				z = data[start + 2];
		}
	}

	public Point3(int start, double data[]) {
		if (data != null) {
			if (data.length > start + 0)
				x = data[start + 0];
			if (data.length > start + 1)
				y = data[start + 1];
			if (data.length > start + 2)
				z = data[start + 2];
		}
	}

	// Predicates

	/**
	 * Are all components to zero?.
	 */
	@Override
	public boolean isZero() {
		return (x == 0 && y == 0 && z == 0);
	}

	// /**
	// * Is other equal to this ?
	// */
	// public boolean
	// equals( const Point3 < double > & other ) const
	// {
	// return( x == other.x
	// and y == other.y
	// and z == other.z );
	// }

	/**
	 * Create a new point linear interpolation of this and <code>other</code>. The
	 * new point is located between this and <code>other</code> if
	 * <code>factor</code> is between 0 and 1 (0 yields this point, 1 yields the
	 * <code>other</code> point).
	 */
	public Point3 interpolate(Point3 other, double factor) {
		Point3 p = new Point3(x + ((other.x - x) * factor), y + ((other.y - y) * factor), z + ((other.z - z) * factor));

		return p;
	}

	/**
	 * Distance between this and <code>other</code>.
	 */
	public double distance(Point3 other) {
		double xx = other.x - x;
		double yy = other.y - y;
		double zz = other.z - z;
		return Math.abs(Math.sqrt((xx * xx) + (yy * yy) + (zz * zz)));
	}

	/**
	 * Distance between this and point (x,y,z).
	 */
	public double distance(double x, double y, double z) {
		double xx = x - this.x;
		double yy = y - this.y;
		double zz = z - this.z;
		return Math.abs(Math.sqrt((xx * xx) + (yy * yy) + (zz * zz)));
	}

	// Commands

	/**
	 * Make this a copy of other.
	 */
	public void copy(Point3 other) {
		x = other.x;
		y = other.y;
		z = other.z;
	}

	public void copy(Vector3 vec) {
		x = vec.data[0];
		y = vec.data[1];
		z = vec.data[2];
	}

	/**
	 * Like #moveTo().
	 */
	public void set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// Commands -- moving

	/**
	 * Move to absolute position (x,y,z).
	 */
	public void moveTo(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Move of given vector(dx,dy,dz).
	 */
	public void move(double dx, double dy, double dz) {
		this.x += dx;
		this.y += dy;
		this.z += dz;
	}

	/**
	 * Move of given point <code>p</code>.
	 */
	public void move(Point3 p) {
		this.x += p.x;
		this.y += p.y;
		this.z += p.z;
	}

	/**
	 * Move of given vector d.
	 */
	public void move(Vector3 d) {
		this.x += d.data[0];
		this.y += d.data[1];
		this.z += d.data[2];
	}

	/**
	 * Move in depth of dz.
	 */
	public void moveZ(double dz) {
		z += dz;
	}

	/**
	 * Scale of factor (sx,sy,sz).
	 */
	public void scale(double sx, double sy, double sz) {
		x *= sx;
		y *= sy;
		z *= sz;
	}

	/**
	 * Scale by factor s.
	 */
	public void scale(Point3 s) {
		x *= s.x;
		y *= s.y;
		z *= s.z;
	}

	/**
	 * Scale by factor s.
	 */
	public void scale(Vector3 s) {
		x *= s.data[0];
		y *= s.data[1];
		z *= s.data[2];
	}

	/**
	 * Scale by a given scalar.
	 * 
	 * @param scalar
	 *            The multiplier.
	 */
	public void scale(double scalar) {
		x *= scalar;
		y *= scalar;
		z *= scalar;
	}

	/**
	 * Change only depth at absolute coordinate z.
	 */
	public void setZ(double z) {
		this.z = z;
	}

	/**
	 * Exchange the values of this and other.
	 */
	public void swap(Point3 other) {
		double t;

		if (other != this) {
			t = this.x;
			this.x = other.x;
			other.x = t;

			t = this.y;
			this.y = other.y;
			other.y = t;

			t = this.z;
			this.z = other.z;
			other.z = t;
		}
	}

	// Commands -- misc.

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
		if (!super.equals(o)) {
			return false;
		}

		Point3 point3 = (Point3) o;

		if (Double.compare(point3.z, z) != 0) {
			return false;
		}

		return true;
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