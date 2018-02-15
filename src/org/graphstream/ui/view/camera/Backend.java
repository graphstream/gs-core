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
 * Class used by DefaultCamera, implementation in gs-ui-...
 */
public interface Backend {

	/**
	 * Transform a point in graph units into pixel units.
	 * 
	 * @return the transformed point.
	 */
	Point3 transform(double x, double y, double z);

	/**
	 * Pass a point in transformed coordinates (pixels) into the reverse transform
	 * (into graph units).
	 * 
	 * @return the transformed point.
	 */
	Point3 inverseTransform(double x, double y, double z);

	/**
	 * Transform a point in graph units into pixel units, the given point is
	 * transformed in place and also returned.
	 */
	Point3 transform(Point3 p);

	/**
	 * Transform a point in pixel units into graph units, the given point is
	 * transformed in place and also returned.
	 */
	Point3 inverseTransform(Point3 p);

	/**
	 * Push the actual transformation on the matrix stack, installing a copy of it
	 * on the top of the stack.
	 */
	void pushTransform();

	/** Begin the work on the actual transformation matrix. */
	void beginTransform();

	/** Make the top-most matrix as an identity matrix. */
	void setIdentity();

	/** Multiply the to-most matrix by a translation matrix. */
	void translate(double tx, double ty, double tz);

	/** Multiply the top-most matrix by a rotation matrix. */
	void rotate(double angle, double ax, double ay, double az);

	/** Multiply the top-most matrix by a scaling matrix. */
	void scale(double sx, double sy, double sz);

	/**
	 * End the work on the actual transformation matrix, installing it as the actual
	 * modelview matrix. If you do not call this method, all the scaling,
	 * translation and rotation are lost.
	 */
	void endTransform();

	/**
	 * Pop the actual transformation of the matrix stack, restoring the previous one
	 * in the stack.
	 */
	void popTransform();

	/** Enable or disable anti-aliasing. */
	void setAntialias(Boolean on);

	/** Enable or disable the hi-quality mode. */
	void setQuality(Boolean on);
}
