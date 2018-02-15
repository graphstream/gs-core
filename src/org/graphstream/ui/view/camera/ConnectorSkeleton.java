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
 * @since 2018-01-18
 * 
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 */
package org.graphstream.ui.view.camera;

import org.graphstream.ui.geom.Point3;

/**
 * Class used by DefaultCamera, implementation in gs-ui-... Skeleton for edges.
 * Data stored on the edge to retrieve the edge basic geometry and various
 * shared data between parts of the renderer.
 * 
 * XXX TODO This part needs much work. The skeleton geometry of an edge can be
 * various things: - An automatically computed shape (for multi-graphs and loop
 * edges). - An user specified shape: - A polyline (points are in absolute
 * coordinates). - A polycurve (in absolute coordinates). - A vector
 * representation (points are relative to an origin and the whole may be
 * rotated).
 */
public interface ConnectorSkeleton {
	public String kindString();

	/** If true the edge shape is a polyline made of size points. */
	public boolean isPoly();

	/** If true the edge shape is a loop defined by four points. */
	public boolean isCurve();

	/**
	 * If larger than one there are several edges between the two nodes of this
	 * edge.
	 */
	public int multi();

	/**
	 * This is only set when the edge is a curve, if true the starting and ending
	 * nodes of the edge are the same node.
	 */
	public boolean isLoop();

	public void setPoly(Object aSetOfPoints);

	public void setPoly(Point3[] aSetOfPoints);

	public void setCurve(double x0, double y0, double z0, double x1, double y1, double z1, double x2, double y2,
			double z2, double x3, double y3, double z3);

	public void setLine(double x0, double y0, double z0, double x1, double y1, double z1);

	public void setMulti(int aMulti);

	public boolean isMulti();

	public void setLoop(double x0, double y0, double z0, double x1, double y1, double z1, double x2, double y2,
			double z2);

	/** The number of points in the edge shape. */
	public int size();

	/** The i-th point of the edge shape. */
	public Point3 apply(int i);

	/**
	 * Change the i-th point in the set of points making up the shape of this edge.
	 */
	public void update(int i, Point3 p);

	/** The last point of the edge shape. */
	public Point3 to();

	/** The first point of the edge shape. */
	public Point3 from();

	/**
	 * Total length of the polyline defined by the points.
	 */
	public double length();

	/**
	 * Compute the length of each segment between the points making up this edge.
	 * This is mostly only useful for polylines. The results of this method is
	 * cached. It is only recomputed when a points changes in the shape. There are
	 * size-1 segments if the are size points. The segment 0 is between points 0 and
	 * 1.
	 */
	public double[] segmentsLengths();

	/**
	 * Length of the i-th segment. There are size-1 segments if there are size
	 * points. The segment 0 is between points 0 and 1.
	 */
	public double segmentLength(int i);

	/**
	 * Compute a point at the given percent on the shape and return it. The percent
	 * must be a number between 0 and 1.
	 */
	public Point3 pointOnShape(double percent);

	/**
	 * Compute a point at a given percent on the shape and store it in the target,
	 * also returning it. The percent must be a number between 0 and 1.
	 */
	public Point3 pointOnShape(double percent, Point3 target);

	/**
	 * Compute a point at a given percent on the shape and push it from the shape
	 * perpendicular to it at a given distance in GU. The percent must be a number
	 * between 0 and 1. The resulting points is returned.
	 */
	public Point3 pointOnShapeAndPerpendicular(double percent, double perpendicular);

	/**
	 * Compute a point at a given percent on the shape and push it from the shape
	 * perpendicular to it at a given distance in GU. The percent must be a number
	 * between 0 and 1. The result is stored in target and also returned.
	 */
	public Point3 pointOnShapeAndPerpendicular(double percent, double perpendicular, Point3 target);
}
