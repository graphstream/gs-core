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
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.ui.layout.springbox.implementations;

import java.util.Random;

import org.graphstream.ui.layout.springbox.BarnesHutLayout;
import org.graphstream.ui.layout.springbox.NodeParticle;

public class LinLog extends BarnesHutLayout {
	protected double k = 1;

	/**
	 * Default general attraction factor.
	 */
	protected double aFactor = 1f;

	/**
	 * Default general repulsion factor.
	 */
	protected double rFactor = 1f;

	protected boolean edgeBased = true;

	protected double maxR = 0.5;

	protected double a = 0;

	protected double r = -1.2;

	// protected

	/**
	 * New "LinLog" 2D Barnes-Hut simulation.
	 */
	public LinLog() {
		this(false);
	}

	/**
	 * New "LinLog" Barnes-Hut simulation.
	 * 
	 * @param is3D
	 *            If true the simulation dimensions count is 3 else 2.
	 */
	public LinLog(boolean is3D) {
		this(is3D, new Random(System.currentTimeMillis()));
	}

	/**
	 * New "LinLog" Barnes-Hut simulation.
	 * 
	 * @param is3D
	 *            If true the simulation dimensions count is 3 else 2.
	 * @param randomNumberGenerator
	 *            The random number generator to use.
	 */
	public LinLog(boolean is3D, Random randomNumberGenerator) {
		super(is3D, randomNumberGenerator);
		setQuality(1);
		force = 3;
	}

	public void configure(double a, double r, boolean edgeBased, double force) {
		this.a = a;
		this.r = r;
		this.edgeBased = edgeBased;
		this.force = force;
	}

	@Override
	public String getLayoutAlgorithmName() {
		return "LinLog";
	}

	@Override
	public void setQuality(double qualityLevel) {
		super.setQuality(qualityLevel);

		if (quality >= 1) {
			viewZone = -1;
		} else {
			viewZone = k;
		}
	}

	@Override
	public void compute() {
		if (viewZone > 0)
			viewZone = area / 1.5;
		super.compute();
	}

	@Override
	protected void chooseNodePosition(NodeParticle n0, NodeParticle n1) {
		// double delta = k * 0.1;
		// if (n0.getEdges().size() == 1 && n1.getEdges().size() > 1) {
		// org.miv.pherd.geom.Point3 pos = n1.getPosition();
		// n0.moveTo(pos.x + delta, pos.y + delta, pos.z + delta);
		// } else if (n1.getEdges().size() == 1 && n0.getEdges().size() > 1) {
		// org.miv.pherd.geom.Point3 pos = n0.getPosition();
		// n1.moveTo(pos.x + delta, pos.y + delta, pos.z + delta);
		// }
	}

	@Override
	public NodeParticle newNodeParticle(String id) {
		return new LinLogNodeParticle(this, id);
	}
}