package org.graphstream.ui.geom;

/**
 * 
 * Interpolated float value.
 * 
 * An interpolated value is a value that, when changed does not directly take
 * the new value, but smoothly changes from its old value to its new value.
 * 
 * @author antoine
 * @since 28 d�c. 2005
 */
public class InterpolatedFloat {
	// Attributes

	/**
	 * Current value.
	 */
	protected float value = 0;

	/**
	 * Objective value.
	 */
	protected float destValue = 0;

	/**
	 * Delta multiplier. The delta is the difference between the current value
	 * and the objective value.
	 */
	protected float deltaMult = 0.05f;

	/**
	 * Interpolation limit.
	 */
	protected float limit;

	// Constructors

	/**
	 * New interpolated float.
	 * 
	 * @param initialValue
	 *            The value this float gets at start.
	 * @param deltaMult
	 *            The multiplier between each step.
	 */
	public InterpolatedFloat(float initialValue, float deltaMult) {
		this.destValue = initialValue;
		this.value = initialValue;
		this.deltaMult = deltaMult;
	}

	// Accessors

	/**
	 * The interpolated value.
	 * 
	 * @return The value.
	 */
	public float getValue() {
		return value;
	}

	/**
	 * The non interpolated value.
	 * 
	 * @return The value.
	 */
	public float getDirectValue() {
		return destValue;
	}

	// Commands

	/**
	 * Effectively product the smooth scaling.
	 */
	public void energy() {
		float delta = (destValue - value) * deltaMult;

		if (Math.abs(delta) > limit) {
			value += delta;
		} else {
			value = destValue;
		}
	}

	/**
	 * Set the value. The value will smoothly switch to it.
	 * 
	 * @param newValue
	 *            The new value.
	 */
	public void setValue(float newValue) {
		destValue = newValue;
		limit = (float) (Math.abs(destValue - value) * 0.001f);
	}

	/**
	 * Bypass the interpolation by jumping directly to the new value.
	 * 
	 * @param newValue
	 *            The new value.
	 */
	public void setDirectValue(float newValue) {
		destValue = value = newValue;
	}

	/**
	 * Add a value to the current destination value.
	 * 
	 * @param increment
	 *            The increment.
	 */
	public void incrValue(float increment) {
		destValue += increment;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append(value);
		buf.append(" [->");
		buf.append(destValue);
		buf.append(" (*");
		buf.append(deltaMult);
		buf.append(")]");

		return buf.toString();
	}
}