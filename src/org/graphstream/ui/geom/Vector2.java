package org.graphstream.ui.geom;

public class Vector2 implements java.io.Serializable {
	// Attributes

	private static final long serialVersionUID = 8839258036865851454L;

	/**
	 * Sequence of 3 coefficients.
	 */
	public float data[];

	// Constructors

	/**
	 * New zero vector.
	 */
	public Vector2() {
		data = new float[2];
		data[0] = 0;
		data[1] = 0;
	}

	/**
	 * New (<code>x</code>,<code>y</code>) vector.
	 */
	public Vector2(float x, float y) {
		data = new float[2];
		data[0] = x;
		data[1] = y;
	}

	/**
	 * New vector copy of <code>other</code>.
	 */
	public Vector2(Vector2 other) {
		data = new float[2];
		copy(other);
	}

	/**
	 * New vector copy of <code>point</code>.
	 */
	public Vector2(Point2 point) {
		data = new float[2];
		copy(point);
	}

	// Predicates

	/**
	 * Are all components to zero?.
	 */
	public boolean isZero() {
		return (data[0] == 0 && data[1] == 0);
	}

	/**
	 * Is this equal to other ?
	 */
	@Override
	public boolean equals(Object other) {
		Vector2 v;

		if (!(other instanceof Vector2)) {
			return false;
		}

		v = (Vector2) other;

		return (data[0] == v.data[0] && data[1] == v.data[1]);
	}

	/**
	 * Is this equal to other ?
	 */
	public boolean equals(Vector2 other) {
		return (data[0] == other.data[0] && data[1] == other.data[1]);
	}

	/**
	 * Is i the index of a component ?
	 * 
	 * In other words, is i &gt;= 0 &amp;&amp; &lt; than #count() ?
	 */
	public boolean validComponent(int i) {
		return (i >= 0 && i < 2);
	}

	// Accessors:

	/**
	 * i-th element.
	 */
	public float at(int i) {
		return data[i];
	}

	@Override
	public Object clone() {
		return new Vector2(this);
	}

	// Accessors

	public float dotProduct(float ox, float oy) {
		return ((data[0] * ox) + (data[1] * oy));
	}

	/**
	 * Dot product of this and other.
	 */
	public float dotProduct(Vector2 other) {
		return ((data[0] * other.data[0]) + (data[1] * other.data[1]));
	}

	/**
	 * Cartesian length.
	 */
	public float length() {
		return (float) Math.sqrt((data[0] * data[0]) + (data[1] * data[1]));
	}

	// Commands

	/**
	 * Assign value to all elements.
	 */
	public void fill(float value) {
		data[0] = data[1] = value;
	}

	/**
	 * Explicitly set the i-th component to value.
	 */
	public void set(int i, float value) {
		data[i] = value;
	}

	/**
	 * Explicitly set the three components.
	 */
	public void set(float x, float y) {
		data[0] = x;
		data[1] = y;
	}

	/**
	 * Add each element of other to the corresponding element of this.
	 */
	public void add(Vector2 other) {
		data[0] += other.data[0];
		data[1] += other.data[1];
	}

	/**
	 * Substract each element of other to the corresponding element of this.
	 */
	public void sub(Vector2 other) {
		data[0] -= other.data[0];
		data[1] -= other.data[1];
	}

	/**
	 * Multiply each element of this by the corresponding element of other.
	 */
	public void mult(Vector2 other) {
		data[0] *= other.data[0];
		data[1] *= other.data[1];
	}

	/**
	 * Add value to each element.
	 */
	public void scalarAdd(float value) {
		data[0] += value;
		data[1] += value;
	}

	/**
	 * Substract value to each element.
	 */
	public void scalarSub(float value) {
		data[0] -= value;
		data[1] -= value;
	}

	/**
	 * Multiply each element by value.
	 */
	public void scalarMult(float value) {
		data[0] *= value;
		data[1] *= value;
	}

	/**
	 * Divide each element by value.
	 */
	public void scalarDiv(float value) {
		data[0] /= value;
		data[1] /= value;
	}

	/**
	 * Transform this into an unit vector.
	 * 
	 * @return the vector length.
	 */
	public float normalize() {
		float len = length();

		if (len != 0) {
			data[0] /= len;
			data[1] /= len;
		}

		return len;
	}

	// Utility

	/**
	 * Make this a copy of other.
	 */
	public void copy(Vector2 other) {
		data[0] = other.data[0];
		data[1] = other.data[1];
	}

	/**
	 * Make this a copy of <code>point</code>.
	 */
	public void copy(Point2 point) {
		data[0] = point.x;
		data[1] = point.y;
	}

	// Misc.

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("[");

		sb.append(data[0]);
		sb.append('|');
		sb.append(data[1]);
		sb.append(']');

		return sb.toString();
	}
}