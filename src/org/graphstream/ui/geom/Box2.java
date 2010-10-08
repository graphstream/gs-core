package org.graphstream.ui.geom;

/**
 * 2D box.
 * 
 * <p>
 * A Box2 is a rectangle characterised by two 2D points.
 * </p>
 * 
 * @author Antoine Dutot
 * @since 20001121
 * @version 0.1
 */
public class Box2 implements java.io.Serializable {
	// Attributes

	private static final long serialVersionUID = -4937535364235067814L;

	/**
	 * Minimum point.
	 */
	public Point2 p1;

	/**
	 * Maximum point.
	 */
	public Point2 p2;

	// Attributes -- Shared

	/**
	 * Box with two null points.
	 */
	public static final Box2 NULL_BOX = new Box2();

	// Constructors

	public Box2() {
		p1 = new Point2(0, 0);
		p2 = new Point2(0, 0);
	}

	/**
	 * New box from point (x1,y1) to point (x2,y2).
	 */
	public Box2(float x1, float y1, float x2, float y2) {
		p1 = new Point2(x1, y1);
		p2 = new Point2(x2, y2);
	}

	/**
	 * New copy of other.
	 */
	public Box2(Box2 other) {
		copy(other);
	}

	/**
	 * New box from point (x1,y1) to point (x2,y2).
	 */
	public void make(float x1, float y1, float x2, float y2) {
		set(x1, y1, x2, y2);
	}

	/**
	 * New copy of other.
	 */
	public void makeCopy(Box2 other) {
		copy(other);
	}

	// Predicates

	// /**
	// * Is other equal to this ?
	// */
	// public boolean
	// equals( const Box2 < float > other )
	// {
	// return( p1.equals( other.p1 ) and p2.equals( other.p2 ) );
	// }

	// Commands

	/**
	 * Make this a copy of other.
	 */
	public void copy(Box2 other) {
		p1.copy(other.p1);
		p2.copy(other.p2);
	}

	/**
	 * Change the box to (x1,y1,x2,y2).
	 */
	public void set(float x1, float y1, float x2, float y2) {
		p1.moveTo(x1, y1);
		p2.moveTo(x2, y2);
	}

	/**
	 * Set geometry as the union of a and b.
	 * 
	 * This always works, even if a and b have no common part.
	 */
	public void unionOf(Box2 a, Box2 b) throws IllegalArgumentException {
		if (a == this || b == this) {
			throw new IllegalArgumentException("parameters cannot be this");
		}

		if (a.p1.x < b.p1.x) {
			p1.x = a.p1.x;
		} else {
			p1.x = b.p1.x;
		}

		if (a.p1.y < b.p1.y) {
			p1.y = b.p1.y;
		} else {
			p1.y = b.p1.y;
		}

		if (a.p2.x > b.p2.x) {
			p2.x = a.p2.x;
		} else {
			p2.x = b.p2.x;
		}

		if (a.p2.y > b.p2.y) {
			p2.y = a.p2.y;
		} else {
			p2.y = b.p2.y;
		}
	}

	/**
	 * Set geometry as the union of this and other.
	 */
	public void unionWith(Box2 other) throws IllegalArgumentException {
		if (other == this) {
			throw new IllegalArgumentException("parameter cannot be this");
		}

		if (other.p1.x < p1.x) {
			p1.x = other.p1.x;
		}

		if (other.p1.y < p1.y) {
			p1.y = other.p1.y;
		}

		if (other.p2.x > p2.x) {
			p2.x = other.p2.x;
		}

		if (other.p2.y > p2.y) {
			p2.y = other.p2.y;
		}
	}

	/**
	 * Set geometry as the intersection of a and b.
	 * 
	 * If this intersection is empty, one or all components of p1 will be
	 * greater than the corresponding components of p2.
	 */
	public void intersectionOf(Box2 a, Box2 b) throws IllegalArgumentException {
		if (a == this || b == this) {
			throw new IllegalArgumentException("parameter cannot be this");
		}

		// We consider first the width (x part) There is 6 cases (each char
		// is a pixel, even `|'):
		// : :
		// a.p1.x a.p2.x
		// |----------a-----------|
		// |--b1--| : :
		// |--b2--| : |--b6--|
		// : |--b3--| :
		// : |--b4--|
		// : :
		// |------------b5-------------|
		// : :

		if (a.p1.x >= b.p1.x) {
			p1.x = a.p1.x;
		} else {
			p1.x = b.p1.x;
		}

		if (a.p2.x <= b.p2.x) {
			p2.x = a.p2.x;
		} else {
			p2.x = b.p2.x;
		}

		if (a.p1.y >= b.p1.y) {
			p1.y = a.p1.y;
		} else {
			p1.y = b.p1.y;
		}

		if (a.p2.y <= b.p2.y) {
			p2.y = a.p2.y;
		} else {
			p2.y = b.p2.y;
		}
	}

	/**
	 * Set geometry as the intersection of this and other.
	 * 
	 * If this intersection is empty, one or all components of p1 will be
	 * greater than the corresponding components of p2.
	 */
	public void intersectionWith(Box2 other) throws IllegalArgumentException {
		if (other == this) {
			throw new IllegalArgumentException();
		}

		if (other.p1.x >= p1.x) {
			p1.x = other.p1.x;
		}

		if (other.p1.y >= p1.y) {
			p1.y = other.p1.y;
		}

		if (other.p2.x <= p2.x) {
			p2.x = other.p2.x;
		}

		if (other.p2.y <= p2.y) {
			p2.y = other.p2.y;
		}
	}

	// Commands -- moving

	/**
	 * Move of the vector (dx,dy,dz).
	 */
	public void move(float dx, float dy) {
		p1.move(dx, dy);
		p2.move(dx, dy);
	}

	// Misc.

	public String tostring() {
		StringBuffer buf;

		buf = new StringBuffer("Box2[(");

		buf.append(p1.x);
		buf.append(':');
		buf.append(p1.y);
		buf.append(")(");

		buf.append(p2.x);
		buf.append(':');
		buf.append(p2.y);
		buf.append(")]");

		return buf.toString();
	}
}