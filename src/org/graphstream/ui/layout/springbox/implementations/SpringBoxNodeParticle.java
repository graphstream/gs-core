/*
 * Copyright 2006 - 2016
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
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
package org.graphstream.ui.layout.springbox.implementations;

import java.util.Iterator;

import org.graphstream.ui.geom.Vector3;
import org.graphstream.ui.layout.springbox.EdgeSpring;
import org.graphstream.ui.layout.springbox.Energies;
import org.graphstream.ui.layout.springbox.GraphCellData;
import org.graphstream.ui.layout.springbox.NodeParticle;
import org.miv.pherd.Particle;
import org.miv.pherd.ParticleBox;
import org.miv.pherd.geom.Point3;
import org.miv.pherd.ntree.Anchor;
import org.miv.pherd.ntree.Cell;

public class SpringBoxNodeParticle extends NodeParticle {
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
	public SpringBoxNodeParticle(SpringBox box, String id) {
		//this(box, id, box.getCenterPoint().x, box.getCenterPoint().y, box.is3D() ? box.getCenterPoint().z : 0);
		this(box, id,  box.randomXInsideBounds(), box.randomYInsideBounds(), box.is3D() ? box.randomZInsideBounds() : 0);	
		//this(box, id, (box.getRandom().nextDouble() * 2 * box.k) - box.k, (box.getRandom().nextDouble() * 2 * box.k) - box.k, box.is3D() ? (box.getRandom().nextDouble() * 2 * box.k) - box.k : 0);

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
	public SpringBoxNodeParticle(SpringBox box, String id, double x, double y,
			double z) {
		super(box, id, x, y, z);
	}

	@Override
	protected void repulsionN2(Vector3 delta) {
		SpringBox box = (SpringBox) this.box;
		boolean is3D = box.is3D();
		ParticleBox nodes = box.getSpatialIndex();
		Energies energies = box.getEnergies();
		Iterator<Object> i = nodes.getParticleIdIterator();

		while (i.hasNext()) {
			SpringBoxNodeParticle node = (SpringBoxNodeParticle) nodes
					.getParticle(i.next());

			if (node != this) {
				delta.set(node.pos.x - pos.x, node.pos.y - pos.y,
						is3D ? node.pos.z - pos.z : 0);

				double len = delta.normalize();

				if(len > 0) {
					if (len < box.k)
						len = box.k; // XXX NEW To prevent infinite
									// repulsion.
				
					double factor = ((box.K2 / (len * len)) * node.weight);

					energies.accumulateEnergy(factor); // TODO check this
					delta.scalarMult(-factor);
					disp.add(delta);
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
		SpringBox box = (SpringBox) this.box;
		boolean is3D = box.is3D();
		Energies energies = box.getEnergies();

		if (intersection(cell)) {
			if (cell.isLeaf()) {
				Iterator<? extends Particle> i = cell.getParticles();

				while (i.hasNext()) {
					SpringBoxNodeParticle node = (SpringBoxNodeParticle) i.next();

					if (node != this) {
						delta.set(node.pos.x - pos.x, node.pos.y - pos.y, is3D ? node.pos.z
								- pos.z : 0);

						double len = delta.normalize();

						if (len > 0)// && len < ( box.k * box.viewZone ) )
						{
							if (len < box.k)
								len = box.k; // XXX NEW To prevent infinite
												// repulsion.
							double factor = ((box.K2 / (len * len)) * node
									.weight);
							energies.accumulateEnergy(factor); // TODO check
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
				GraphCellData bary = (GraphCellData) cell.getData();

				double dist = bary.distanceFrom(pos);
				double size = cell.getSpace().getSize();

				if ((!cell.isLeaf())
						&& ((size / dist) > box.getBarnesHutTheta())) {
					int div = cell.getSpace().getDivisions();

					for (int i = 0; i < div; i++)
						recurseRepulsion(cell.getSub(i), delta);
				} else {
					if (bary.weight != 0) {
						delta.set(bary.center.x - pos.x, bary.center.y - pos.y,
								is3D ? bary.center.z - pos.z : 0);

						double len = delta.normalize();

						if (len > 0) {
							if (len < box.k)
								len = box.k; // XXX NEW To prevent infinite
												// repulsion.
							double factor = ((box.K2 / (len * len)) * (bary.weight));
							energies.accumulateEnergy(factor);
							delta.scalarMult(-factor);
							repE += factor;

							disp.add(delta);
						}
					}
				}
			}
		}
	}

	@Override
	protected void attraction(Vector3 delta) {
		SpringBox box = (SpringBox) this.box;
		boolean is3D = box.is3D();
		Energies energies = box.getEnergies();
		int neighbourCount = neighbours.size();

		for (EdgeSpring edge : neighbours) {
			if (!edge.ignored) {
				NodeParticle other = edge.getOpposite(this);
				Point3 opos = other.getPosition();

				delta.set(opos.x - pos.x, opos.y - pos.y, is3D ? opos.z - pos.z
						: 0);

				double len = delta.normalize();
				double k = box.k * edge.weight;
				double factor = box.K1 * (len - k);

				// delta.scalarMult( factor );
				delta.scalarMult(factor * (1f / (neighbourCount * 0.1f)));
				// ^^^ XXX NEW inertia based on the node degree. This is one
				// of the amelioration of the Spring-Box algorithm. Compare
				// it to the Force-Atlas algorithm that does this on
				// **repulsion**.

				disp.add(delta);
				attE += factor;
				energies.accumulateEnergy(factor);
			}
		}
	}
	
	protected void gravity(Vector3 delta) {
		SpringBox box = (SpringBox) this.box;
		boolean is3D = box.is3D();
		//org.graphstream.ui.geom.Point3 center = box.getCenterPoint();
		//delta.set(center.x - pos.x, center.y - pos.y, is3D ? center.z - pos.z : 0);
		delta.set(-pos.x, -pos.y, is3D ? -pos.z : 0);// Use (0,0,0) instead of the layout center.
		delta.normalize();
		delta.scalarMult(box.getGravityFactor());
		disp.add(delta);
	}

	protected boolean intersection(Cell cell) {
		SpringBox box = (SpringBox) this.box;

		double k = box.k;
		double vz = box.getViewZone();
		
		Anchor lo = cell.getSpace().getLoAnchor();
		Anchor hi = cell.getSpace().getHiAnchor();

		double x1 = lo.x;
		double x2 = hi.x;
		double X1 = pos.x - (k * vz);
		double X2 = pos.x + (k * vz);

		if (X2 < x1 || X1 > x2)
			return false;

		double y1 = lo.y;
		double y2 = hi.y;
		double Y1 = pos.y - (k * vz);
		double Y2 = pos.y + (k * vz);

		if (Y2 < y1 || Y1 > y2)
			return false;

		double z1 = lo.z;
		double z2 = hi.z;
		double Z1 = pos.z - (k * vz);
		double Z2 = pos.z + (k * vz);

		if (Z2 < z1 || Z1 > z2)
			return false;

		return true;
	}
}