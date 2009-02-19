/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.miv.graphstream.algorithm.layout2.springboxLeRetour;

import org.miv.util.geom.Vector3;

/**
 * Edge representation.
 */
public class EdgeSpring
{
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
	public float weight = 1f;
	
	/**
	 * The attraction force on this edge.
	 */
	public Vector3 spring = new Vector3();
	
	/**
	 * Make this edge ignored by the layout algorithm ?.
	 */
	public boolean ignored = false;
	
	/**
	 * The edge attraction energy.
	 */
	public float attE;
	
	/**
	 * New edge between two given nodes.
	 * @param id The edge identifier.
	 * @param n0 The first node.
	 * @param n1 The second node.
	 */
	public EdgeSpring( String id, NodeParticle n0, NodeParticle n1 )
	{
		this.id    = id;
		this.node0 = n0;
		this.node1 = n1;
	}

	/**
	 * Considering the two nodes of the edge, return the one that was not
	 * given as argument.
	 * @param node One of the nodes of the edge.
	 * @return The other node.
	 */
	public NodeParticle getOpposite( NodeParticle node )
	{
		if( node0 == node )
			return node1;

		return node0;
	}
	
	/**
	 * Compute the attraction force on this edge.
	 */
	public void attraction()
	{
		if( ! ignored )
		{
/*			Point3 p0 = node0.getPosition();
			Point3 p1 = node1.getPosition();
		
			spring.set( p1.x - p0.x, p1.y - p0.y, is3D ? p1.z - p0.z : 0 );

			float len = spring.normalize();
		
			if( k != 0 )
			{
				float factor = ( len*len/k ) * weight * node0.getWeight() * node1.getWeight();
				energy += factor*2;
				spring.scalarMult( factor );

				attE = factor;
			}
*/		}
	}
}