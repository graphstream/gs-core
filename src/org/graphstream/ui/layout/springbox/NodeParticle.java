/*
 * Copyright 2006 - 2012
 *      Stefan Balev       <stefan.balev@graphstream-project.org>
 *      Julien Baudry	<julien.baudry@graphstream-project.org>
 *      Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *      Yoann Pigné	<yoann.pigne@graphstream-project.org>
 *      Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
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
package org.graphstream.ui.layout.springbox;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import org.graphstream.ui.geom.Vector3;
import org.miv.pherd.Particle;
import org.miv.pherd.ntree.BarycenterCellData;
import org.miv.pherd.ntree.Cell;

public class NodeParticle extends Particle {
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
	protected SpringBox box;

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
	public NodeParticle(SpringBox box, String id) {
		this(box, id, (box.random.nextDouble() * 2 * box.k) - box.k,
				(box.random.nextDouble() * 2 * box.k) - box.k,
				box.is3D ? (box.random.nextDouble() * 2 * box.k) - box.k : 0);

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
	public NodeParticle(SpringBox box, String id, double x, double y, double z) {
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
				out = new PrintStream(new FileOutputStream("out" + getId()
						+ ".data"));
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
	}

	/**
	 * Compute the repulsion for each other node. This is the most precise way,
	 * but the algorithm is a time hog : complexity is O(n^2).
	 * 
	 * @param delta
	 *            The computed displacement vector.
	 */
	protected void repulsionN2(Vector3 delta) {
		Iterator<Object> i = box.nodes.getParticleIdIterator();

		while (i.hasNext()) {
			NodeParticle node = (NodeParticle) box.nodes.getParticle(i.next());

			if (node != this) {
				delta.set(node.pos.x - pos.x, node.pos.y - pos.y,
						box.is3D ? node.pos.z - pos.z : 0);

				double len = delta.normalize();
				double factor = len != 0 ? ((box.K2 / (len * len)) * node.weight)
						: 0.00001f;

				delta.scalarMult(-factor);
				disp.add(delta);
				box.energies.accumulateEnergy(factor); // TODO check this
			}
		}
	}

	/**
	 * Compute the repulsion for each node in the viewing distance, and use the
	 * n-tree to find them. For a certain distance the node repulsion is
	 * computed one by one. At a larger distance the repulsion is computed using
	 * nodes barycenters.
	 * 
	 * @param delta
	 *            The computed displacement vector.
	 */
	protected void repulsionNLogN(Vector3 delta) {
		// Explore the n-tree from the root cell and consider the contents
		// of one cell only if it does intersect an area around the current
		// node. Else take its (weighted) barycenter into account.

		recurseRepulsion(box.nodes.getNTree().getRootCell(), delta);
	}

	protected void recurseRepulsion(Cell cell, Vector3 delta) {
		if (intersection(cell)) {
			if (cell.isLeaf()) {
				Iterator<? extends Particle> i = cell.getParticles();

				while (i.hasNext()) {
					NodeParticle node = (NodeParticle) i.next();

					if (node != this) {
						delta.set(node.pos.x - pos.x, node.pos.y - pos.y,
								box.is3D ? node.pos.z - pos.z : 0);

						double len = delta.normalize();

						if (len > 0)// && len < ( box.k * box.viewZone ) )
						{
							if (len < box.k)
								len = box.k; // XXX NEW To prevent infinite
												// repulsion.
							double factor = len != 0 ? ((box.K2 / (len * len)) * node.weight)
									: 0.00001;
							box.energies.accumulateEnergy(factor); // TODO check
																	// this
							repE += factor;
							delta.scalarMult(-factor);

							disp.add(delta);
						}
					}
				}
			} else {
				int div = cell.getSpace().getDivisions();

				for (int i = 0; i < div; i++)
					recurseRepulsion(cell.getSub(i), delta);
			}
		} else {
			if (cell != this.cell) {
				BarycenterCellData bary = (BarycenterCellData) cell.getData();

				double dist = bary.distanceFrom(pos);
				double size = cell.getSpace().getSize();

				if ((!cell.isLeaf()) && ((size / dist) > box.theta)) {
					int div = cell.getSpace().getDivisions();

					for (int i = 0; i < div; i++)
						recurseRepulsion(cell.getSub(i), delta);
				} else {
					if (bary.weight != 0) {
						delta.set(bary.center.x - pos.x, bary.center.y - pos.y,
								box.is3D ? bary.center.z - pos.z : 0);

						double len = delta.normalize();

						if (len > 0) {
							if (len < box.k)
								len = box.k; // XXX NEW To prevent infinite
												// repulsion.
							double factor = len != 0 ? ((box.K2 / (len * len)) * (bary.weight))
									: 0.00001f;
							box.energies.accumulateEnergy(factor);
							delta.scalarMult(-factor);
							repE += factor;

							disp.add(delta);
						}
					}
				}
			}
		}
	}

	protected void attraction(Vector3 delta) {
		for (EdgeSpring edge : neighbours) {
			if (!edge.ignored) {
				NodeParticle other = edge.getOpposite(this);

				delta.set(other.pos.x - pos.x, other.pos.y - pos.y,
						box.is3D ? other.pos.z - pos.z : 0);

				double len = delta.normalize();
				double k = box.k * edge.weight;
				double factor = box.K1 * (len - k);

				// delta.scalarMult( factor );
				delta.scalarMult(factor * (1f / (neighbours.size() * 0.1f)));
				// ^^^ XXX NEW inertia based on the node degree. This is one
				// of the amelioration of the Spring-Box algorithm. Compare
				// it to the Force-Atlas algorithm that does this on
				// **repulsion**.

				disp.add(delta);
				attE += factor;
				box.energies.accumulateEnergy(factor);
			}
		}
	}

	protected boolean intersection(Cell cell) {
		double k = box.k;
		double vz = box.viewZone;

		double x1 = cell.getSpace().getLoAnchor().x;
		double y1 = cell.getSpace().getLoAnchor().y;
		double z1 = cell.getSpace().getLoAnchor().z;

		double x2 = cell.getSpace().getHiAnchor().x;
		double y2 = cell.getSpace().getHiAnchor().y;
		double z2 = cell.getSpace().getHiAnchor().z;

		double X1 = pos.x - (k * vz);
		double Y1 = pos.y - (k * vz);
		double Z1 = pos.z - (k * vz);
		double X2 = pos.x + (k * vz);
		double Y2 = pos.y + (k * vz);
		double Z2 = pos.z + (k * vz);

		if (X2 < x1 || X1 > x2)
			return false;

		if (Y2 < y1 || Y1 > y2)
			return false;

		if (Z2 < z1 || Z1 > z2)
			return false;

		return true;
	}

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

	/**
	 * Move the node by a random vector.
	 */
	public void shake() {
		double k = box.k;

		pos.x += box.random.nextFloat() * k * 2 - 1;
		pos.y += box.random.nextFloat() * k * 2 - 1;

		if (box.is3D)
			pos.z += box.random.nextFloat() * k * 2 - 1;
	}

	@Override
	public void inserted() {

	}

	@Override
	public void removed() {
	}
}