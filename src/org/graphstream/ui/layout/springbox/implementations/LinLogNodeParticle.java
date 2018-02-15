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
 * @since 2012-06-19
 * 
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author kitskub <kitskub@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.ui.layout.springbox.implementations;

import java.util.Iterator;

import org.graphstream.ui.geom.Vector3;
import org.graphstream.ui.layout.springbox.EdgeSpring;
import org.graphstream.ui.layout.springbox.Energies;
import org.graphstream.ui.layout.springbox.GraphCellData;
import org.graphstream.ui.layout.springbox.NodeParticle;
import org.miv.pherd.Particle;
import org.miv.pherd.ParticleBox;
import org.miv.pherd.ntree.Cell;

public class LinLogNodeParticle extends NodeParticle {
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
	public LinLogNodeParticle(LinLog box, String id) {
		this(box, id, (box.getRandom().nextDouble() * 2 * box.k) - box.k,
				(box.getRandom().nextDouble() * 2 * box.k) - box.k,
				box.is3D() ? (box.getRandom().nextDouble() * 2 * box.k) - box.k : 0);

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
	public LinLogNodeParticle(LinLog box, String id, double x, double y, double z) {
		super(box, id, x, y, z);
	}

	@Override
	protected void repulsionN2(Vector3 delta) {
		LinLog box = (LinLog) this.box;
		boolean is3D = box.is3D();
		ParticleBox nodes = box.getSpatialIndex();
		Energies energies = box.getEnergies();
		Iterator<Object> i = nodes.getParticleIdIterator();
		int deg = neighbours.size();

		while (i.hasNext()) {
			LinLogNodeParticle node = (LinLogNodeParticle) nodes.getParticle(i.next());

			if (node != this) {
				delta.set(node.pos.x - pos.x, node.pos.y - pos.y, is3D ? node.pos.z - pos.z : 0);

				// double len = delta.normalize();
				double len = delta.length();

				if (len > 0) {
					double degFactor = box.edgeBased ? deg * node.neighbours.size() : 1;
					double factor = 1;
					double r = box.r;

					factor = -degFactor * (Math.pow(len, r - 2)) * node.weight * weight * box.rFactor;

					if (factor < -box.maxR) {
						factor = -box.maxR;
					}

					energies.accumulateEnergy(factor); // TODO check this
					delta.scalarMult(factor);
					disp.add(delta);
					repE += factor;
				}
			}
		}
	}

	@Override
	protected void repulsionNLogN(Vector3 delta) {
		// Explore the n-tree from the root cell and consider the contents
		// of one cell only if it does intersect an area around the current
		// node. Else take its (weighted) barycenter into account.

		recurseRepulsion(box.getSpatialIndex().getNTree().getRootCell(), delta);
	}

	protected void recurseRepulsion(Cell cell, Vector3 delta) {
		LinLog box = (LinLog) this.box;
		boolean is3D = box.is3D();
		Energies energies = box.getEnergies();
		int deg = neighbours.size();

		if (intersection(cell)) {
			if (cell.isLeaf()) {
				Iterator<? extends Particle> i = cell.getParticles();

				while (i.hasNext()) {
					LinLogNodeParticle node = (LinLogNodeParticle) i.next();

					if (node != this) {
						delta.set(node.pos.x - pos.x, node.pos.y - pos.y, is3D ? node.pos.z - pos.z : 0);

						// double len = delta.normalize();
						double len = delta.length();

						if (len > 0) {
							double degFactor = box.edgeBased ? deg * node.neighbours.size() : 1;
							double factor = 1;
							double r = box.r;

							factor = -degFactor * (Math.pow(len, r - 2)) * node.weight * weight * box.rFactor;

							if (factor < -box.maxR) {
								factor = -box.maxR;
							}

							energies.accumulateEnergy(factor); // TODO check this
							delta.scalarMult(factor);
							disp.add(delta);
							repE += factor;
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
				GraphCellData bary = (GraphCellData) cell.getData();
				double dist = bary.distanceFrom(pos);
				double size = cell.getSpace().getSize();

				if ((!cell.isLeaf()) && ((size / dist) > box.getBarnesHutTheta())) {
					int div = cell.getSpace().getDivisions();

					for (int i = 0; i < div; i++)
						recurseRepulsion(cell.getSub(i), delta);
				} else {
					if (bary.weight != 0) {
						delta.set(bary.center.x - pos.x, bary.center.y - pos.y, is3D ? bary.center.z - pos.z : 0);

						// double len = delta.normalize();
						double len = delta.length();

						if (len > 0) {
							double degFactor = box.edgeBased ? deg * bary.degree : 1;
							double factor = 1;
							double r = box.r;

							factor = -degFactor * (Math.pow(len, r - 2)) * bary.weight * weight * box.rFactor;

							if (factor < -box.maxR) {
								factor = -box.maxR;
							}

							energies.accumulateEnergy(factor); // TODO check this
							delta.scalarMult(factor);
							disp.add(delta);
							repE += factor;
						}
					}
				}
			}
		}
	}

	@Override
	protected void attraction(Vector3 delta) {
		LinLog box = (LinLog) this.box;
		boolean is3D = box.is3D();
		Energies energies = box.getEnergies();

		for (EdgeSpring edge : neighbours) {
			if (!edge.ignored) {
				LinLogNodeParticle other = (LinLogNodeParticle) edge.getOpposite(this);

				delta.set(other.pos.x - pos.x, other.pos.y - pos.y, is3D ? other.pos.z - pos.z : 0);

				// double len = delta.normalize();
				double len = delta.length();

				if (len > 0) {
					double factor = 1;
					double a = box.a;

					factor = (Math.pow(len, a - 2)) * edge.weight * box.aFactor;

					energies.accumulateEnergy(factor);
					delta.scalarMult(factor);
					disp.add(delta);
					attE += factor;
				}
			}
		}
	}

	@Override
	protected void gravity(Vector3 delta) {
	}

	protected boolean intersection(Cell cell) {
		LinLog box = (LinLog) this.box;

		double k = box.k;
		double vz = box.getViewZone();

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
}