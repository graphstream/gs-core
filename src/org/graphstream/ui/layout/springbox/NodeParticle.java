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
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author kitskub <kitskub@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.ui.layout.springbox;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.graphstream.ui.geom.Vector3;
import org.miv.pherd.Particle;

/**
 * Base implementation of a node particle to be used in the
 * {@link BarnesHutLayout} to represent nodes and choose their positions.
 * 
 * <p>
 * Several abstract methods have to be overrided to provide a computation of the
 * layout (all the attraction/repulsion computation is done in this class):
 * <ul>
 * <li>{@link #attraction(Vector3)}</li>
 * <li>{@link #repulsionN2(Vector3)}</li>
 * <li>{@link #repulsionNLogN(Vector3)}</li>
 * </ul>
 * </p>
 */
public abstract class NodeParticle extends Particle {
	/**
	 * Set of edge connected to this node.
	 */
	public ArrayList<EdgeSpring> neighbours = new ArrayList<EdgeSpring>();

	/**
	 * Should the node move?.
	 */
	public boolean frozen = false;

	/**
	 * Displacement vector.
	 */
	public Vector3 disp;

	/**
	 * Last computed displacement vector length.
	 */
	public double len;

	/**
	 * Attraction energy for this node only.
	 */
	public double attE;

	/**
	 * Repulsion energy for this node only.
	 */
	public double repE;

	/**
	 * If non null, all this node statistics will be output to this stream.
	 */
	public PrintStream out;

	/**
	 * The box.
	 */
	protected BarnesHutLayout box;

	// Constructors

	/**
	 * New node.
	 * 
	 * The node is placed at random in the space of the simulation.
	 * 
	 * @param box
	 *            The spring box.
	 * @param id
	 *            The node identifier.
	 */
	public NodeParticle(BarnesHutLayout box, String id) {
		// this(box, id, box.getCenterPoint().x, box.getCenterPoint().y, box.is3D() ?
		// box.getCenterPoint().z : 0);
		this(box, id, box.randomXInsideBounds(), box.randomYInsideBounds(), box.is3D ? box.randomZInsideBounds() : 0);
		// this(box, id, (box.random.nextDouble() * 2) - 1, (box.random
		// .nextDouble() * 2) - 1,
		// box.is3D ? (box.random.nextDouble() * 2) - 1 : 0);

		this.box = box;
	}

	/**
	 * New node at a given position.
	 * 
	 * @param box
	 *            The spring box.
	 * @param id
	 *            The node identifier.
	 * @param x
	 *            The abscissa.
	 * @param y
	 *            The ordinate.
	 * @param z
	 *            The depth.
	 */
	public NodeParticle(BarnesHutLayout box, String id, double x, double y, double z) {
		super(id, x, y, box.is3D ? z : 0);
		this.box = box;
		disp = new Vector3();
		createDebug();
	}

	/**
	 * Create a file for statistics about this node.
	 */
	protected void createDebug() {
		if (box.outputNodeStats) {
			try {
				out = new PrintStream(new FileOutputStream("out" + getId() + ".data"));
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	/**
	 * All the edges connected to this node.
	 * 
	 * @return A set of edges.
	 */
	public Collection<EdgeSpring> getEdges() {
		return neighbours;
	}

	@Override
	public void move(int time) {
		if (!frozen) {
			disp.fill(0);

			Vector3 delta = new Vector3();

			repE = 0;
			attE = 0;

			if (box.viewZone < 0)
				repulsionN2(delta);
			else
				repulsionNLogN(delta);

			attraction(delta);

			if (box.gravity != 0)
				gravity(delta);

			disp.scalarMult(box.force);

			len = disp.length();

			if (len > (box.area / 2)) {
				disp.scalarMult((box.area / 2) / len);
				len = box.area / 2;
			}

			box.avgLength += len;

			if (len > box.maxMoveLength)
				box.maxMoveLength = len;
		}
	}

	@Override
	public void nextStep(int time) {
		if (!frozen) {
			nextPos.x = pos.x + disp.data[0];
			nextPos.y = pos.y + disp.data[1];

			if (box.is3D)
				nextPos.z = pos.z + disp.data[2];

			box.nodeMoveCount++;
			moved = true;
		} else {
			nextPos.x = pos.x;
			nextPos.y = pos.y;
			if (box.is3D)
				nextPos.z = pos.z;
		}

		if (out != null) {
			out.printf(Locale.US, "%s %f %f %f%n", getId(), len, attE, repE);
			out.flush();
		}

		super.nextStep(time);
	}

	/**
	 * Force a node to move from a given vector.
	 * 
	 * @param dx
	 *            The x component.
	 * @param dy
	 *            The y component.
	 * @param dz
	 *            The z component.
	 */
	public void moveOf(double dx, double dy, double dz) {
		pos.set(pos.x + dx, pos.y + dy, pos.z + dz);
	}

	/**
	 * Force a node to move at a given position.
	 * 
	 * @param x
	 *            The new x.
	 * @param y
	 *            The new y.
	 * @param z
	 *            The new z.
	 */
	public void moveTo(double x, double y, double z) {
		pos.set(x, y, z);
		moved = true;
	}

	/**
	 * Compute the repulsion for each other node. This is the most precise way, but
	 * the algorithm is a time hog : complexity is O(n^2).
	 * 
	 * @param delta
	 *            The computed displacement vector.
	 */
	protected abstract void repulsionN2(Vector3 delta);

	/**
	 * Compute the repulsion for each node in the viewing distance, and use the
	 * n-tree to find them. For a certain distance the node repulsion is computed
	 * one by one. At a larger distance the repulsion is computed using nodes
	 * barycenters.
	 * 
	 * @param delta
	 *            The computed displacement vector.
	 */
	protected abstract void repulsionNLogN(Vector3 delta);

	/**
	 * Compute the global attraction toward each connected node.
	 * 
	 * @param delta
	 *            The computed displacement vector.
	 */
	protected abstract void attraction(Vector3 delta);

	/**
	 * Compute the global attraction toward the layout center (if enabled).
	 * 
	 * @param delta
	 *            The computed displacement vector.
	 * @see BarnesHutLayout#useGravity
	 */
	protected abstract void gravity(Vector3 delta);

	/**
	 * The given edge is connected to this node.
	 * 
	 * @param e
	 *            The edge to connect.
	 */
	public void registerEdge(EdgeSpring e) {
		neighbours.add(e);
	}

	/**
	 * The given edge is no more connected to this node.
	 * 
	 * @param e
	 *            THe edge to disconnect.
	 */
	public void unregisterEdge(EdgeSpring e) {
		int i = neighbours.indexOf(e);

		if (i >= 0) {
			neighbours.remove(i);
		}
	}

	/**
	 * Remove all edges connected to this node.
	 */
	public void removeNeighborEdges() {
		ArrayList<EdgeSpring> edges = new ArrayList<EdgeSpring>(neighbours);

		for (EdgeSpring edge : edges)
			box.removeEdge(box.getLayoutAlgorithmName(), edge.id);

		neighbours.clear();
	}

	@Override
	public void inserted() {
	}

	@Override
	public void removed() {
	}
}