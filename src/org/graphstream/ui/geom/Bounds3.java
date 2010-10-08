package org.graphstream.ui.geom;

/**
 * 3D Bounds.
 * 
 * @author Antoine Dutot
 * @since 20021215
 */
public class Bounds3 implements java.io.Serializable {
	// Attributes

	private static final long serialVersionUID = 4922239937149305829L;

	/**
	 * First axis.
	 */
	public float width;

	/**
	 * Second axis.
	 */
	public float height;

	/**
	 * Third axis.
	 */
	public float depth;

	// Attributes -- Shared

	/**
	 * Empty bounds.
	 */
	public static final Bounds3 NULL_BOUNDS = new Bounds3(0, 0, 0);

	/**
	 * Unit bounds.
	 */
	public static final Bounds3 UNIT_BOUNDS = new Bounds3();

	// Constructors

	/**
	 * New "unit" bounds (1,1,1).
	 */
	public Bounds3() {
		width = height = depth = 1;
	}

	/**
	 * New bounds (width,height,depth).
	 */
	public Bounds3(float width, float height, float depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
	}

	// Accessors

	// Commands

	/**
	 * Make this a copy of other.
	 */
	public void copy(Bounds3 other) {
		width = other.width;
		height = other.height;
		depth = other.depth;
	}

	/**
	 * Change the box to (x1,y1,z1,x2,y2,z2).
	 */
	public void set(float width, float height, float depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
	}

	// Commands -- misc.

	@Override
	public String toString() {
		StringBuffer buf;

		buf = new StringBuffer("Bounds3[");

		buf.append(width);
		buf.append(':');
		buf.append(height);
		buf.append(':');
		buf.append(depth);
		buf.append("]");

		return buf.toString();
	}
}