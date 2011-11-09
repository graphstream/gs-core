/*
 * Copyright 2006 - 2011 
 *     Stefan Balev 	<stefan.balev@graphstream-project.org>
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
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