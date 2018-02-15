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

import org.graphstream.ui.geom.Point3;

/**
 * Edge representation.
 * 
 * <p>
 * This is mainly used to store data about an edge, all the computation is done
 * in the node particle.
 * </p>
 */
public class EdgeSpring {
	/**
	 * The edge identifier.
	 */
	public String id;

	/**
	 * Source node.
	 */
	public NodeParticle node0;

	/**
	 * Target node.
	 */
	public NodeParticle node1;

	/**
	 * Edge weight.
	 */
	public double weight = 1f;

	/**
	 * The attraction force on this edge.
	 */
	public Point3 spring = new Point3();

	/**
	 * Make this edge ignored by the layout algorithm ?.
	 */
	public boolean ignored = false;

	/**
	 * The edge attraction energy.
	 */
	public double attE;

	/**
	 * New edge between two given nodes.
	 * 
	 * @param id
	 *            The edge identifier.
	 * @param n0
	 *            The first node.
	 * @param n1
	 *            The second node.
	 */
	public EdgeSpring(String id, NodeParticle n0, NodeParticle n1) {
		this.id = id;
		this.node0 = n0;
		this.node1 = n1;
	}

	/**
	 * Considering the two nodes of the edge, return the one that was not given as
	 * argument.
	 * 
	 * @param node
	 *            One of the nodes of the edge.
	 * @return The other node.
	 */
	public NodeParticle getOpposite(NodeParticle node) {
		return node0 == node ? node1 : node0;
	}
}