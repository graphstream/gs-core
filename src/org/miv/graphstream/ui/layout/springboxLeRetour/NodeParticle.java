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

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import org.miv.graphstream.algorithm.layout2.LayoutListener;
import org.miv.pherd.Particle;
import org.miv.pherd.ntree.BarycenterCellData;
import org.miv.pherd.ntree.Cell;
import org.miv.util.geom.Vector3;

public class NodeParticle extends Particle
{
// Attributes
	
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
	public Vector3 disp ;

	/**
	 * Last computed displacement vector length.
	 */
	public float len;

	/**
	 * Attraction energy for this node only.
	 */
	public float attE;
	
	/**
	 * Repulsion energy for this node only.
	 */
	public float repE;

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
	 * @param box The spring box.
	 * @param id The node identifier.
	 */
	public NodeParticle( SpringBox box, String id )
	{
		this( box, id,
			 (box.random.nextFloat()*2*box.k)-box.k,
			 (box.random.nextFloat()*2*box.k)-box.k,
			  box.is3D ? (box.random.nextFloat()*2*box.k)-box.k : 0 );

		this.box = box;
	}
	
	/**
	 * New node at a given position.
	 * @param box The spring box.
	 * @param id The node identifier.
	 * @param x The abscissa.
	 * @param y The ordinate.
	 * @param z The depth.
	 */
	public NodeParticle( SpringBox box, String id, float x, float y, float z )
	{
		super( id, x, y, box.is3D ? z : 0 );
		this.box = box;
		disp = new Vector3();
		createDebug();
	}

	/**
	 * Create a file for statistics about this node.
	 */
	protected void createDebug()
	{
		if( box.outputNodeStats )
		{
			try
			{
				out = new PrintStream( new FileOutputStream( "out"+getId()+".data" ) );
			}
			catch( Exception e )
			{
				e.printStackTrace();
				System.exit( 1 );
			}
		}
	}

// Access
	
	/**
	 * All the edges connected to this node.
	 * @return A set of edges.
	 */
	public Collection<EdgeSpring> getEdges()
	{
		return neighbours;
	}
	
// Commands

	@Override
	public void move( int time )
	{
		if( ! frozen )
		{
			disp.fill( 0 );
			
			Vector3 delta = new Vector3();
			
			repE = 0;
			attE = 0;
			
			if( box.viewZone < 0 )
			     repulsionN2( delta );
			else repulsionNLogN( delta );
			
			attraction( delta );
			
			disp.scalarMult( box.force );
			
			len = disp.length();
			
			if( len > (box.area/2) )
			{
				disp.scalarMult( (box.area/2)/len );
				len = box.area/2;
			}
			
			box.avgLength += len;
			
			if( len > box.maxMoveLength )
				box.maxMoveLength = len;
		}
	}
	
	@Override
	public void nextStep( int time )
	{
		// Apply it to the position:
		// We apply the movement vector only if this vector moves from a significant
		// length (relative to the graph total area). Else we do nothing and let the
		// node in place. This is another mechanism to allow the layout to stabilise...
		
//		if( len > box.area * 0.0000001f )
		{
			nextPos.x = pos.x + disp.data[0];
			nextPos.y = pos.y + disp.data[1];
			
			if( box.is3D )
			     nextPos.z = pos.z + disp.data[2];

			box.nodeMoveCount++;
			moved = true;
		}
		
		// Eventually output movement information to the listeners.

		if( box.sendNodeInfos )
		{
			for( LayoutListener listener: box.listeners )
				listener.nodeInfos( (String)id, (float)disp.data[0], (float)disp.data[1],
					box.is3D ? (float)disp.data[2] : 0 );
		}
		
		if( out != null )
		{
			out.printf( Locale.US, "%s %f %f %f%n", getId(), len, attE, repE );
			out.flush();
		}
		
		super.nextStep( time );
	}
	
	public void move( float dx, float dy, float dz )
	{
		pos.set( pos.x + dx, pos.y + dy, pos.z + dz );
	}
	
	/**
	 * Compute the repulsion for each other node. This is the most precise way, but the algorithm
	 * is a time hog : complexity is O(n^2). 
	 * @param delta The computed displacement vector.
	 */
	protected void repulsionN2( Vector3 delta )
	{
		Iterator<Object> i = box.nodes.getParticleIdIterator();
		
		while( i.hasNext() )
		{
			NodeParticle node = (NodeParticle) box.nodes.getParticle( i.next() );
			
			if( node != this )
			{				
				delta.set( node.pos.x - pos.x, node.pos.y - pos.y, box.is3D ? node.pos.z - pos.z : 0 );
				
				float len    = delta.normalize();
				float factor = len != 0 ? ( box.K2 / ( len * len ) ) : 0.00001f;

				delta.scalarMult( -factor );
				disp.add( delta );
				box.energies.accumulateEnergy( factor );	// TODO check this
			}
		}
	}
	
	/**
	 * Compute the repulsion for each node in the viewing distance, and use the n-tree to find
	 * them. For a certain distance the node repulsion is computed one by one. At a larger distance
	 * the repulsion is computed using nodes barycenters.
	 * @param delta The computed displacement vector.
	 */
	protected void repulsionNLogN( Vector3 delta )
	{
		// Explore the n-tree from the root cell and consider the contents
		// of one cell only if it does intersect an area around the current
		// node. Else take its (weighted) barycenter into account.
	
//		nDirect = 0;
		recurseRepulsion( box.nodes.getNTree().getRootCell(), delta );
/*		
				System.err.printf( "enlarge view -> %f!%n", box.viewZone );
				directCount = 0;
//				directCount2 = 100000;
			}
		}
*/ /*	else
		{
			directCount2--;
			
			if( directCount2 <= 0 )
			{
				box.viewZone--;
				System.err.printf( "shrink view -> %f!%n", box.viewZone );				
				directCount2 = 100000;
			}
		}
*/ /*	else if( nDirect > 200 )
		{
			directCount2++;
			
			if( directCount2 > 100 )
			{
				box.viewZone--;
				System.err.printf( "strech view -> %f!%n", box.viewZone );
				directCount2 = 0;
			}
		}
*/	}
	
	protected void recurseRepulsion( Cell cell, Vector3 delta )
	{
		if( intersection( cell ) )
		{
			if( cell.isLeaf() )
			{
				Iterator<? extends Particle> i = cell.getParticles();
				
				while( i.hasNext() )
				{
					NodeParticle node = (NodeParticle) i.next();
					
					if( node != this )
					{
						delta.set( node.pos.x - pos.x, node.pos.y - pos.y, box.is3D ? node.pos.z - pos.z : 0 );

						float len = delta.normalize();

						if( len > 0 )// && len < ( box.k * box.viewZone ) )
						{
							float factor = len != 0 ? ( box.K2 / ( len * len ) ) : 0.00001f;
							box.energies.accumulateEnergy( factor );	// TODO check this
							repE   += factor;
							delta.scalarMult( -factor );
						}
						
						disp.add( delta );
					}
				}
			}
			else
			{
				int div = cell.getSpace().getDivisions();
				
				for( int i=0; i<div; i++ )
					recurseRepulsion( cell.getSub( i ), delta );
			}
		}
		else
		{
			if( cell != this.cell )
			{
				BarycenterCellData bary = (BarycenterCellData) cell.getData();
				
				float dist = bary.distanceFrom( pos );
				float size = cell.getSpace().getSize();
				
				if( ( ! cell.isLeaf() ) && ( ( size/dist ) > box.theta ) )
				{
					int div = cell.getSpace().getDivisions();
					
					for( int i=0; i<div; i++ )
						recurseRepulsion( cell.getSub( i ), delta );
				}
				else
				{				
					if( bary.weight != 0 )
					{
	//					System.err.printf( "applying bary %s [depth=%d weight=%d]%n", cell.getId(), cell.getDepth(), (int)bary.weight );
						delta.set(  bary.center.x - pos.x,
						            bary.center.y - pos.y,
						 box.is3D ? bary.center.z - pos.z : 0 );
			
						float len = delta.normalize();
						
						//if( len < 0.2f * box.area )
						{
							if( len != 0 )
							{
								float factor = len != 0 ? ( ( box.K2 / ( len * len ) ) * (bary.weight) ) : 0.00001f;
								box.energies.accumulateEnergy( factor );
								delta.scalarMult( -factor );
								repE   += factor;
							}
			
							disp.add( delta );
						}
					}
				}
			}
		}
	}
	
	protected void attraction( Vector3 delta )
	{
		for( EdgeSpring edge : neighbours )
		{
			if( ! edge.ignored )
			{
				NodeParticle other = edge.getOpposite( this );
				
				delta.set( other.pos.x - pos.x, other.pos.y - pos.y, box.is3D ? other.pos.z - pos.z : 0 );

				float len = delta.normalize();
				float k   = box.k;
				
				float factor = box.K1 * ( len - k );

				delta.scalarMult( factor );
				disp.add( delta );
				attE += factor;
				
				box.energies.accumulateEnergy( factor );
			}
		}
	}
	
	protected boolean intersection( Cell cell )
	{
		float k  = box.k;
		float vz = box.viewZone;
		
		float x1 = cell.getSpace().getLoAnchor().x;
		float y1 = cell.getSpace().getLoAnchor().y;
		float z1 = cell.getSpace().getLoAnchor().z;
		float x2 = cell.getSpace().getHiAnchor().x;
		float y2 = cell.getSpace().getHiAnchor().y;
		float z2 = cell.getSpace().getHiAnchor().z;
		
		float X1 = pos.x - ( k * vz );
		float Y1 = pos.y - ( k * vz );
		float Z1 = pos.z - ( k * vz );
		float X2 = pos.x + ( k * vz );
		float Y2 = pos.y + ( k * vz );
		float Z2 = pos.z + ( k * vz );
		
		// Only when the area is before or after the cell there cannot
		// exist an intersection (case a and b). Else there must be an
		// intersection (cases c, d, e and f).
		//
		// |-a-|   +---------+   |-b-|
		//         |         |
		//       |-c-|     |-d-|
		//         |         |
		//         |  |-e-|  |
		//         |         |
		//       |-+----f----+-|
		//         |         |
		//         +---------+
		
		if( X2 < x1 || X1 > x2 )
			return false;
		
		if( Y2 < y1 || Y1 > y2 )
			return false;
				
		if( Z2 < z1 || Z1 > z2 )
			return false;
		
		return true;
	}

	/**
	 * The given edge is connected to this node.
	 * @param e The edge to connect.
	 */
	public void registerEdge( EdgeSpring e )
	{
		neighbours.add( e );
	}

	/**
	 * The given edge is no more connected to this node.
	 * @param e THe edge to disconnect.
	 */
	public void unregisterEdge( EdgeSpring e )
	{
		int i = neighbours.indexOf( e );

		if( i >= 0 )
		{
			neighbours.remove( i );
		}
	}
	
	/**
	 * Remove all edges connected to this node.
	 */
	public void removeNeighborEdges()
	{
		for( EdgeSpring edge: neighbours )
		{
			box.removeEdge( edge.id );
		}
	}
	
	/**
	 * Move the node by a random vector.
	 */
	public void shake()
	{
		float k = box.k;
		
		pos.x += box.random.nextFloat() * k * 2 - 1;
		pos.y += box.random.nextFloat() * k * 2 - 1;
		
		if( box.is3D )
			pos.z += box.random.nextFloat() * k * 2 - 1;
	}
}