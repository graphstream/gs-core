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
 * @since 2009-07-26
 * 
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 */
package org.graphstream.ui.view.util;

import java.util.logging.Logger;

import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;
import org.graphstream.ui.graphicGraph.stylesheet.Value;
import org.graphstream.ui.graphicGraph.stylesheet.Values;
import org.graphstream.ui.geom.Point3;
import org.miv.pherd.geom.Vector3;

/**
 * p Various geometric informations on the graphic graph.
 * 
 * <p>
 * This class extends the GraphMetrics to provide not only metrics on the
 * graphic graph but also on the rendering canvas, and allow to convert from
 * graph metrics to canvas metrics and the reverse.
 * </p>
 * 
 * <p>
 * Here we call the canvas "view port" since this class allows to place a view
 * port inside the graph in order to zoom and pan the view.
 * </p>
 */
public class GraphMetrics {

	/**
	 * class level logger
	 */
	private static final Logger logger = Logger.getLogger(GraphMetrics.class.getSimpleName());

	// Attribute

	/**
	 * Graph lower position (bottom,left,front).
	 */
	public Point3 lo = new Point3();

	/**
	 * Graph higher position (top,right,back).
	 */
	public Point3 hi = new Point3();

	/**
	 * The lowest visible point.
	 */
	public Point3 loVisible = new Point3();

	/**
	 * The highest visible point.
	 */
	public Point3 hiVisible = new Point3();

	/**
	 * Graph dimension.
	 */
	public Vector3 size = new Vector3();

	/**
	 * The graph diagonal.
	 */
	public double diagonal = 1;

	/**
	 * The view port size.
	 */
	public double viewport[] = new double[4];

	/**
	 * The scaling factor to pass from graph units to pixels.
	 */
	public double ratioPx2Gu;

	/**
	 * The length for one pixel, according to the current transformation.
	 */
	public double px1;

	// Construction

	/**
	 * New canvas metrics with default values.
	 */
	public GraphMetrics() {
		setDefaults();
	}

	/**
	 * Set defaults value in the lo, hi and size fields to (-1) and (1)
	 * respectively.
	 */
	protected void setDefaults() {
		lo.set(-1, -1, -1);
		hi.set(1, 1, 1);
		size.set(2, 2, 2);

		diagonal = 1;
		ratioPx2Gu = 1;
		px1 = 1;
	}

	// Access

	/**
	 * The graph diagonal (the overall width).
	 * 
	 * @return The diagonal.
	 */
	public double getDiagonal() {
		return diagonal;
	}

	/**
	 * The graph bounds.
	 * 
	 * @return The size.
	 */
	public Vector3 getSize() {
		return size;
	}

	/**
	 * The graph lowest (bottom,left,front) point.
	 * 
	 * @return The lowest point.
	 */
	public Point3 getLowPoint() {
		return lo;
	}

	/**
	 * The graph highest (top,right,back) point.
	 * 
	 * @return The highest point.
	 */
	public Point3 getHighPoint() {
		return hi;
	}

	public double graphWidthGU() {
		return hi.x - lo.x;
	}

	public double graphHeightGU() {
		return hi.y - lo.y;
	}

	public double graphDepthGU() {
		return hi.z - lo.z;
	}

	// Access -- Convert values

	/**
	 * Convert a value in given units to graph units.
	 * 
	 * @param value
	 *            The value to convert.
	 * @param units
	 *            The units the value to convert is expressed in.
	 * @return The value converted to GU.
	 */
	public double lengthToGu(double value, StyleConstants.Units units) {
		switch (units) {
		case PX:
			// return (value - 0.01f) / ratioPx2Gu;
			return value / ratioPx2Gu;
		case PERCENTS:
			return (diagonal * value);
		case GU:
		default:
			return value;
		}
	}

	/**
	 * Convert a value in a given units to graph units.
	 * 
	 * @param value
	 *            The value to convert (it contains its own units).
	 */
	public double lengthToGu(Value value) {
		return lengthToGu(value.value, value.units);
	}

	/**
	 * Convert one of the given values in a given units to graph units.
	 * 
	 * @param values
	 *            The values set containing the value to convert (it contains its
	 *            own units).
	 * @param index
	 *            Index of the value to convert.
	 */
	public double lengthToGu(Values values, int index) {
		return lengthToGu(values.get(index), values.units);
	}

	/**
	 * Convert a value in a given units to pixels.
	 * 
	 * @param value
	 *            The value to convert.
	 * @param units
	 *            The units the value to convert is expressed in.
	 * @return The value converted in pixels.
	 */
	public double lengthToPx(double value, StyleConstants.Units units) {
		switch (units) {
		case GU:
			// return (value - 0.01f) * ratioPx2Gu;
			return value * ratioPx2Gu;
		case PERCENTS:
			return (diagonal * value) * ratioPx2Gu;
		case PX:
		default:
			return value;
		}
	}

	/**
	 * Convert a value in a given units to pixels.
	 * 
	 * @param value
	 *            The value to convert (it contains its own units).
	 */
	public double lengthToPx(Value value) {
		return lengthToPx(value.value, value.units);
	}

	/**
	 * Convert one of the given values in a given units pixels.
	 * 
	 * @param values
	 *            The values set containing the value to convert (it contains its
	 *            own units).
	 * @param index
	 *            Index of the value to convert.
	 */
	public double lengthToPx(Values values, int index) {
		return lengthToPx(values.get(index), values.units);
	}

	public double positionPixelToGu(int pixels, int index) {
		double l = lengthToGu(pixels, Units.PX);

		switch (index) {
		case 0:
			l -= graphWidthGU() / 2.0;
			l = (hi.x + lo.x) / 2.0 + l;
			break;
		case 1:
			l -= graphHeightGU() / 2.0;
			l = (hi.y + lo.y) / 2.0 + l;
			break;
		default:
			throw new IllegalArgumentException();
		}

		logger.fine(String.format("%spixel[%d] %d --> %fgu", this, index, pixels, l));

		return l;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(String.format("Graph Metrics :%n"));

		builder.append(String.format("        lo         = %s%n", lo));
		builder.append(String.format("        hi         = %s%n", hi));
		builder.append(String.format("        visible lo = %s%n", loVisible));
		builder.append(String.format("        visible hi = %s%n", hiVisible));
		builder.append(String.format("        size       = %s%n", size));
		builder.append(String.format("        diag       = %f%n", diagonal));
		builder.append(String.format("        viewport   = %s%n", viewport));
		builder.append(String.format("        ratio      = %fpx = 1gu%n", ratioPx2Gu));

		return builder.toString();
	}

	// Command

	/**
	 * Set the output view port size in pixels.
	 * 
	 * @param viewportWidth
	 *            The width in pixels of the view port.
	 * @param viewportHeight
	 *            The width in pixels of the view port.
	 */
	public void setViewport(double viewportX, double viewportY, double viewportWidth, double viewportHeight) {
		viewport[0] = viewportX;
		viewport[1] = viewportY;
		viewport[2] = viewportWidth;
		viewport[3] = viewportHeight;
	}

	/**
	 * The ratio to pass by multiplication from pixels to graph units. This ratio
	 * must be larger than zero, else nothing is changed.
	 * 
	 * @param ratio
	 *            The ratio.
	 */
	public void setRatioPx2Gu(double ratio) {
		if (ratio > 0) {
			ratioPx2Gu = ratio;
			px1 = 0.95f / ratioPx2Gu;
		} else if (ratio == 0)
			throw new RuntimeException("ratio PX to GU cannot be zero");
		else if (ratio < 0)
			throw new RuntimeException(String.format("ratio PX to GU cannot be negative (%f)", ratio));
	}

	/**
	 * Set the graphic graph bounds (the lowest and highest points).
	 * 
	 * @param minx
	 *            Lowest abscissa.
	 * @param miny
	 *            Lowest ordinate.
	 * @param minz
	 *            Lowest depth.
	 * @param maxx
	 *            Highest abscissa.
	 * @param maxy
	 *            Highest ordinate.
	 * @param maxz
	 *            Highest depth.
	 */
	public void setBounds(double minx, double miny, double minz, double maxx, double maxy, double maxz) {
		lo.x = minx;
		lo.y = miny;
		lo.z = minz;
		hi.x = maxx;
		hi.y = maxy;
		hi.z = maxz;

		size.data[0] = hi.x - lo.x;
		size.data[1] = hi.y - lo.y;
		size.data[2] = hi.z - lo.z;
		diagonal = Math.sqrt(size.data[0] * size.data[0] + size.data[1] * size.data[1] + size.data[2] * size.data[2]);
	}
}