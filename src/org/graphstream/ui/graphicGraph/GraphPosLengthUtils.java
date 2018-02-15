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
 * @since 2009-07-14
 * 
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Alex Bowen <bowen.a@gmail.com>
 * @author kitskub <kitskub@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.ui.graphicGraph;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Point2;
import org.graphstream.ui.geom.Point3;

import java.util.logging.Logger;

/**
 * Lots of small often used measuring algorithms on graphs.
 * 
 * <p>
 * Use this class with a static import.
 * </p>
 */
public class GraphPosLengthUtils {

	/**
	 * class level logger
	 */
	private static final Logger logger = Logger.getLogger(GraphPosLengthUtils.class.getSimpleName());

	/**
	 * Retrieve a node position from its attributes ("x", "y", "z", or "xy", or
	 * "xyz").
	 * 
	 * @param id
	 *            The node identifier.
	 * @return A newly allocated array of three floats containing the (x,y,z)
	 *         position of the node, or null if the node is not part of the graph.
	 */
	public static double[] nodePosition(Graph graph, String id) {
		Node node = graph.getNode(id);

		if (node != null)
			return nodePosition(node);

		return null;
	}

	/**
	 * Retrieve a node position from its attributes ("x", "y", "z", or "xy", or
	 * "xyz").
	 * 
	 * @param id
	 *            The node identifier.
	 * @return A newly allocated point containing the (x,y,z) position of the node,
	 *         or null if the node is not part of the graph.
	 */
	public static Point3 nodePointPosition(Graph graph, String id) {
		Node node = graph.getNode(id);

		if (node != null)
			return nodePointPosition(node);

		return null;
	}

	/**
	 * Like {@link #nodePosition(Graph,String)} but use an existing node as
	 * argument.
	 * 
	 * @param node
	 *            The node to consider.
	 * @return A newly allocated array of three floats containing the (x,y,z)
	 *         position of the node.
	 */
	public static double[] nodePosition(Node node) {
		double xyz[] = new double[3];

		nodePosition(node, xyz);

		return xyz;
	}

	/**
	 * Like {@link #nodePointPosition(Graph,String)} but use an existing node as
	 * argument.
	 * 
	 * @param node
	 *            The node to consider.
	 * @return A newly allocated point containing the (x,y,z) position of the node.
	 */
	public static Point3 nodePointPosition(Node node) {
		return nodePosition(node, new Point3());
	}

	/**
	 * Like {@link #nodePosition(Graph,String)}, but instead of returning a newly
	 * allocated array, fill up the array given as parameter. This array must have
	 * at least three cells.
	 * 
	 * @param id
	 *            The node identifier.
	 * @param xyz
	 *            An array of at least three cells.
	 * @throws RuntimeException
	 *             If the node with the given identifier does not exist.
	 */
	public static void nodePosition(Graph graph, String id, double xyz[]) {
		Node node = graph.getNode(id);

		if (node != null)
			nodePosition(node, xyz);

		throw new RuntimeException("node '" + id + "' does not exist");
	}

	/**
	 * Like {@link #nodePointPosition(Graph,String)}, but instead of returning a
	 * newly allocated array, fill up the array given as parameter. This array must
	 * have at least three cells.
	 * 
	 * @param id
	 *            The node identifier.
	 * @param pos
	 *            A point that will receive the node position.
	 * @throws RuntimeException
	 *             If the node with the given identifier does not exist.
	 */
	public static Point3 nodePosition(Graph graph, String id, Point3 pos) {
		Node node = graph.getNode(id);

		if (node != null)
			return nodePosition(node, pos);

		throw new RuntimeException("node '" + id + "' does not exist");
	}

	/**
	 * Like {@link #nodePosition(Graph,String,double[])} but use an existing node as
	 * argument.
	 * 
	 * @param node
	 *            The node to consider.
	 * @param xyz
	 *            An array of at least three cells.
	 */
	public static void nodePosition(Node node, double xyz[]) {
		if (xyz.length < 3)
			return;

		if (node.hasAttribute("xyz") || node.hasAttribute("xy")) {
			Object o = node.getAttribute("xyz");

			if (o == null)
				o = node.getAttribute("xy");

			if (o != null) {
				positionFromObject(o, xyz);
			}

		} else if (node.hasAttribute("x")) {
			xyz[0] = (double) node.getNumber("x");

			if (node.hasAttribute("y"))
				xyz[1] = (double) node.getNumber("y");

			if (node.hasAttribute("z"))
				xyz[2] = (double) node.getNumber("z");
		}
	}

	/**
	 * Like {@link #nodePosition(Graph,String,Point3)} but use an existing node as
	 * argument.
	 * 
	 * @param node
	 *            The node to consider.
	 * @param pos
	 *            A point that will serve as the default position if node doesn't
	 *            have position
	 */
	public static Point3 nodePosition(Node node, Point3 pos) {
		if (node.hasAttribute("xyz") || node.hasAttribute("xy")) {
			Object o = node.getAttribute("xyz");

			if (o == null)
				o = node.getAttribute("xy");

			if (o != null) {
				return positionFromObject(o, pos);
			}
		} else if (node.hasAttribute("x")) {
			double x = node.getNumber("x");
			double y;
			double z;

			if (node.hasAttribute("y"))
				y = node.getNumber("y");
			else
				y = pos.y;

			if (node.hasAttribute("z"))
				z = node.getNumber("z");
			else
				z = pos.z;

			return new Point3(x, y, z);
		}
		return pos;
	}

	/**
	 * Try to convert an object to a position. The object can be an array of
	 * numbers, an array of base numeric types or their object counterparts.
	 * 
	 * @param o
	 *            The object to try to convert.
	 * @param xyz
	 *            The result.
	 */
	public static void positionFromObject(Object o, double xyz[]) {
		if (o instanceof Object[]) {
			Object oo[] = (Object[]) o;

			if (oo.length > 0 && oo[0] instanceof Number) {
				xyz[0] = ((Number) oo[0]).doubleValue();
				if (oo.length > 1)
					xyz[1] = ((Number) oo[1]).doubleValue();
				if (oo.length > 2)
					xyz[2] = ((Number) oo[2]).doubleValue();
			}
		} else if (o instanceof Double[]) {
			Double oo[] = (Double[]) o;
			if (oo.length > 0)
				xyz[0] = oo[0];
			if (oo.length > 1)
				xyz[1] = oo[1];
			if (oo.length > 2)
				xyz[2] = oo[2];
		} else if (o instanceof Float[]) {
			Float oo[] = (Float[]) o;
			if (oo.length > 0)
				xyz[0] = oo[0];
			if (oo.length > 1)
				xyz[1] = oo[1];
			if (oo.length > 2)
				xyz[2] = oo[2];
		} else if (o instanceof Integer[]) {
			Integer oo[] = (Integer[]) o;
			if (oo.length > 0)
				xyz[0] = oo[0];
			if (oo.length > 1)
				xyz[1] = oo[1];
			if (oo.length > 2)
				xyz[2] = oo[2];
		} else if (o instanceof double[]) {
			double oo[] = (double[]) o;
			if (oo.length > 0)
				xyz[0] = oo[0];
			if (oo.length > 1)
				xyz[1] = oo[1];
			if (oo.length > 2)
				xyz[2] = oo[2];
		} else if (o instanceof float[]) {
			float oo[] = (float[]) o;
			if (oo.length > 0)
				xyz[0] = oo[0];
			if (oo.length > 1)
				xyz[1] = oo[1];
			if (oo.length > 2)
				xyz[2] = oo[2];
		} else if (o instanceof int[]) {
			int oo[] = (int[]) o;
			if (oo.length > 0)
				xyz[0] = oo[0];
			if (oo.length > 1)
				xyz[1] = oo[1];
			if (oo.length > 2)
				xyz[2] = oo[2];
		} else if (o instanceof Number[]) {
			Number oo[] = (Number[]) o;
			if (oo.length > 0)
				xyz[0] = oo[0].doubleValue();
			if (oo.length > 1)
				xyz[1] = oo[1].doubleValue();
			if (oo.length > 2)
				xyz[2] = oo[2].doubleValue();
		} else if (o instanceof Point3) {
			Point3 oo = (Point3) o;
			xyz[0] = oo.x;
			xyz[1] = oo.y;
			xyz[2] = oo.z;
		} else if (o instanceof Point2) {
			Point2 oo = (Point2) o;
			xyz[0] = oo.x;
			xyz[1] = oo.y;
			xyz[2] = 0;
		} else {
			logger.warning(String.format("Do not know how to handle xyz attribute %s.", o.getClass().getName()));
		}
	}

	/**
	 * Try to convert an object to a position. The object can be an array of
	 * numbers, an array of base numeric types or their object counterparts.
	 * 
	 * @param o
	 *            The object to try to convert.
	 * @param pos
	 *            The default position if object doesn't have position data.
	 */
	public static Point3 positionFromObject(Object o, Point3 pos) {
		double x = pos.x, y = pos.y, z = pos.z;
		if (o instanceof Object[]) {
			Object oo[] = (Object[]) o;

			if (oo.length > 0 && oo[0] instanceof Number) {
				x = ((Number) oo[0]).doubleValue();
				if (oo.length > 1)
					y = ((Number) oo[1]).doubleValue();
				if (oo.length > 2)
					z = ((Number) oo[2]).doubleValue();
			}
		} else if (o instanceof Double[]) {
			Double oo[] = (Double[]) o;
			if (oo.length > 0)
				x = oo[0];
			if (oo.length > 1)
				y = oo[1];
			if (oo.length > 2)
				z = oo[2];
		} else if (o instanceof Float[]) {
			Float oo[] = (Float[]) o;
			if (oo.length > 0)
				x = oo[0];
			if (oo.length > 1)
				y = oo[1];
			if (oo.length > 2)
				z = oo[2];
		} else if (o instanceof Integer[]) {
			Integer oo[] = (Integer[]) o;
			if (oo.length > 0)
				x = oo[0];
			if (oo.length > 1)
				y = oo[1];
			if (oo.length > 2)
				z = oo[2];
		} else if (o instanceof double[]) {
			double oo[] = (double[]) o;
			if (oo.length > 0)
				x = oo[0];
			if (oo.length > 1)
				y = oo[1];
			if (oo.length > 2)
				z = oo[2];
		} else if (o instanceof float[]) {
			float oo[] = (float[]) o;
			if (oo.length > 0)
				x = oo[0];
			if (oo.length > 1)
				y = oo[1];
			if (oo.length > 2)
				z = oo[2];
		} else if (o instanceof int[]) {
			int oo[] = (int[]) o;
			if (oo.length > 0)
				x = oo[0];
			if (oo.length > 1)
				y = oo[1];
			if (oo.length > 2)
				z = oo[2];
		} else if (o instanceof Number[]) {
			Number oo[] = (Number[]) o;
			if (oo.length > 0)
				x = oo[0].doubleValue();
			if (oo.length > 1)
				y = oo[1].doubleValue();
			if (oo.length > 2)
				z = oo[2].doubleValue();
		} else if (o instanceof Point3) {
			Point3 oo = (Point3) o;
			x = oo.x;
			y = oo.y;
			z = oo.z;
		} else if (o instanceof Point2) {
			Point2 oo = (Point2) o;
			x = oo.x;
			y = oo.y;
			z = 0;
		} else {
			logger.warning(String.format("Do not know how to handle xyz attribute %s%n", o.getClass().getName()));
		}
		return new Point3(x, y, z);
	}

	/**
	 * Compute the edge length of the given edge according to its two nodes
	 * positions.
	 * 
	 * @param id
	 *            The identifier of the edge.
	 * @return The edge length or -1 if the nodes of the edge have no positions.
	 * @throws RuntimeException
	 *             If the edge cannot be found.
	 */
	public static double edgeLength(Graph graph, String id) {
		Edge edge = graph.getEdge(id);

		if (edge != null)
			return edgeLength(edge);

		throw new RuntimeException("edge '" + id + "' cannot be found");
	}

	/**
	 * Like {@link #edgeLength(Graph,String)} but use an existing edge as argument.
	 * 
	 * @param edge
	 * @return The edge length or -1 if the nodes of the edge have no positions.
	 */
	public static double edgeLength(Edge edge) {
		double xyz0[] = nodePosition(edge.getNode0());
		double xyz1[] = nodePosition(edge.getNode1());

		if (xyz0 == null || xyz1 == null)
			return -1;

		xyz0[0] = xyz1[0] - xyz0[0];
		xyz0[1] = xyz1[1] - xyz0[1];
		xyz0[2] = xyz1[2] - xyz0[2];

		return Math.sqrt(xyz0[0] * xyz0[0] + xyz0[1] * xyz0[1] + xyz0[2] * xyz0[2]);
	}
}