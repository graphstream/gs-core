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
 * @since 2009-02-19
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.ui.graphicGraph.stylesheet;

/**
 * A value and the units of the value.
 * 
 * <p>
 * As a style sheet may express values in several different units. This class
 * purpose is to pack the value and the units it is expressed in into a single
 * object.
 * </p>
 */
public class Value extends Number {
	// Attributes

	private static final long serialVersionUID = 1L;

	/**
	 * The value.
	 */
	public double value;

	/**
	 * The value units.
	 */
	public Style.Units units;

	// Constructor

	/**
	 * New value.
	 * 
	 * @param units
	 *            The value units.
	 * @param value
	 *            The value.
	 */
	public Value(Style.Units units, double value) {
		this.value = value;
		this.units = units;
	}

	/**
	 * New copy of another value.
	 * 
	 * @param other
	 *            The other value to copy.
	 */
	public Value(Value other) {
		this.value = other.value;
		this.units = other.units;
	}

	@Override
	public float floatValue() {
		return (float) value;
	}

	@Override
	public double doubleValue() {
		return value;
	}

	@Override
	public int intValue() {
		return (int) Math.round(value);
	}

	@Override
	public long longValue() {
		return Math.round(value);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append(value);

		switch (units) {
		case GU:
			builder.append("gu");
			break;
		case PX:
			builder.append("px");
			break;
		case PERCENTS:
			builder.append("%");
			break;
		default:
			builder.append("wtf (what's the fuck?)");
			break;
		}

		return builder.toString();
	}

	public boolean equals(Value o) {
		if (o != this) {
			if (!(o instanceof Value))
				return false;

			Value other = (Value) o;

			if (other.units != units)
				return false;

			if (other.value != value)
				return false;
		}

		return true;
	}
}