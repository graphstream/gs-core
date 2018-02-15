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
 * @author kitskub <kitskub@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.ui.geom;

/**
 * A three component vector made of doubles.
 * 
 * @author Antoine Dutot
 * @since 20000613
 * @version 0.1
 */
public class Vector3 extends Vector2 {
	// Attributes:

	private static final long serialVersionUID = 8839258036865851454L;

	// Constructors

	/**
	 * New zero vector.
	 */
	public Vector3() {
		data = new double[3];
		data[0] = 0;
		data[1] = 0;
		data[2] = 0;
	}

	/**
	 * New (<code>x</code>,<code>y</code>,<code>z</code>) vector.
	 */
	public Vector3(double x, double y, double z) {
		data = new double[3];
		data[0] = x;
		data[1] = y;
		data[2] = z;
	}

	/**
	 * New vector copy of <code>other</code>.
	 */
	public Vector3(Vector3 other) {
		data = new double[3];
		copy(other);
	}

	/**
	 * New vector copy of <code>point</code>.
	 */
	public Vector3(Point3 point) {
		data = new double[3];
		copy(point);
	}

	// Predicates

	/**
	 * Are all components to zero?.
	 */
	@Override
	public boolean isZero() {
		return (data[0] == 0 && data[1] == 0 && data[2] == 0);
	}

	/**
	 * Is this equal to other ?
	 */
	@Override
	public boolean equals(Object other) {
		Vector3 v;

		if (!(other instanceof Vector3)) {
			return false;
		}

		v = (Vector3) other;

		return (data[0] == v.data[0] && data[1] == v.data[1] && data[2] == v.data[2]);
	}

	/**
	 * Is i the index of a component ?
	 * 
	 * In other words, is i &gt;= 0 &amp;&amp; &lt; than #count() ?
	 */
	@Override
	public boolean validComponent(int i) {
		return (i >= 0 && i < 3);
	}

	// Access

	@Override
	public Object clone() {
		return new Vector3(this);
	}

	// Access

	public double dotProduct(double ox, double oy, double oz) {
		return ((data[0] * ox) + (data[1] * oy) + (data[2] * oz));
	}

	/**
	 * Dot product of this and other.
	 */
	public double dotProduct(Vector3 other) {
		return ((data[0] * other.data[0]) + (data[1] * other.data[1]) + (data[2] * other.data[2]));
	}

	/**
	 * Cartesian length.
	 */
	@Override
	public double length() {
		return Math.sqrt((data[0] * data[0]) + (data[1] * data[1]) + (data[2] * data[2]));
	}

	public double z() {
		return data[2];
	}

	// Commands

	/**
	 * Assign value to all elements.
	 */
	@Override
	public void fill(double value) {
		data[0] = data[1] = data[2] = value;
	}

	/**
	 * Explicitly set the i-th component to value.
	 */
	@Override
	public void set(int i, double value) {
		data[i] = value;
	}

	/**
	 * Explicitly set the three components.
	 */
	public void set(double x, double y, double z) {
		data[0] = x;
		data[1] = y;
		data[2] = z;
	}

	/**
	 * Add each element of other to the corresponding element of this.
	 */
	public void add(Vector3 other) {
		data[0] += other.data[0];
		data[1] += other.data[1];
		data[2] += other.data[2];
	}

	/**
	 * Substract each element of other to the corresponding element of this.
	 */
	public void sub(Vector3 other) {
		data[0] -= other.data[0];
		data[1] -= other.data[1];
		data[2] -= other.data[2];
	}

	/**
	 * Multiply each element of this by the corresponding element of other.
	 */
	public void mult(Vector3 other) {
		data[0] *= other.data[0];
		data[1] *= other.data[1];
		data[2] *= other.data[2];
	}

	/**
	 * Add value to each element.
	 */
	@Override
	public void scalarAdd(double value) {
		data[0] += value;
		data[1] += value;
		data[2] += value;
	}

	/**
	 * Substract value to each element.
	 */
	@Override
	public void scalarSub(double value) {
		data[0] -= value;
		data[1] -= value;
		data[2] -= value;
	}

	/**
	 * Multiply each element by value.
	 */
	@Override
	public void scalarMult(double value) {
		data[0] *= value;
		data[1] *= value;
		data[2] *= value;
	}

	/**
	 * Divide each element by value.
	 */
	@Override
	public void scalarDiv(double value) {
		data[0] /= value;
		data[1] /= value;
		data[2] /= value;
	}

	/**
	 * Set this to the cross product of this and other.
	 */
	public void crossProduct(Vector3 other) {
		double x;
		double y;

		x = (data[1] * other.data[2]) - (data[2] * other.data[1]);
		y = (data[2] * other.data[0]) - (data[0] * other.data[2]);
		data[2] = (data[0] * other.data[1]) - (data[1] * other.data[0]);
		data[0] = x;
		data[1] = y;
	}

	/**
	 * Set this to the cross product of A and B.
	 */
	public void crossProduct(Vector3 A, Vector3 B) {
		data[0] = (A.data[1] * B.data[2]) - (A.data[2] * B.data[1]);
		data[1] = (A.data[2] * B.data[0]) - (A.data[0] * B.data[2]);
		data[2] = (A.data[0] * B.data[1]) - (A.data[1] * B.data[0]);
	}

	/**
	 * Transform this into an unit vector.
	 * 
	 * @return the vector length.
	 */
	@Override
	public double normalize() {
		double len = length();

		if (len != 0) {
			data[0] /= len;
			data[1] /= len;
			data[2] /= len;
		}

		return len;
	}

	// Utility

	/**
	 * Make this a copy of other.
	 */
	public void copy(Vector3 other) {
		data[0] = other.data[0];
		data[1] = other.data[1];
		data[2] = other.data[2];
	}

	/**
	 * Make this a copy of <code>point</code>.
	 */
	public void copy(Point3 point) {
		data[0] = point.x;
		data[1] = point.y;
		data[2] = point.z;
	}

	// Misc.

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("[");

		sb.append(data[0]);
		sb.append('|');
		sb.append(data[1]);
		sb.append('|');
		sb.append(data[2]);
		sb.append(']');

		return sb.toString();
	}
}