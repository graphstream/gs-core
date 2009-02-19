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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import org.miv.graphstream.algorithm.layout2.Layout;
import org.miv.graphstream.algorithm.layout2.LayoutListener;
import org.miv.graphstream.graph.Element;
import org.miv.graphstream.graph.Graph;
import org.miv.pherd.ParticleBox;
import org.miv.pherd.ParticleBoxListener;
import org.miv.pherd.ntree.Anchor;
import org.miv.pherd.ntree.BarycenterCellData;
import org.miv.pherd.ntree.CellSpace;
import org.miv.pherd.ntree.OctreeCellSpace;
import org.miv.pherd.ntree.QuadtreeCellSpace;
import org.miv.util.Environment;
import org.miv.util.NotFoundException;
import org.miv.util.SingletonException;
import org.miv.util.geom.Point3;

public class SpringBox implements Layout, ParticleBoxListener
{
// Attributes -- Data
	
	/**
	 * The nodes representation and the n-tree. The particle-box is an implementation of a recursive
	 * space decomposition method that is used here to break the O(n^2) complexity into something
	 * that is closer to O(n log n).
	 */
	protected ParticleBox nodes;
	
	/**
	 * The set of edges.
	 */
	protected HashMap<String,EdgeSpring> edges = new HashMap<String,EdgeSpring>();
	
	/**
	 * Random number generator.
	 */
	protected Random random;
	
	/**
	 * The lowest node position.
	 */
	protected Point3 lo = new Point3( 0, 0, 0 );
	
	/**
	 * The highest node position.
	 */
	protected Point3 hi = new Point3( 1, 1, 1 );
	
	/**
	 * Set of listeners.
	 */
	protected ArrayList<LayoutListener> listeners = new ArrayList<LayoutListener>();
	
	/**
	 * Output stream for statistics if in debug mode.
	 */
	protected PrintStream statsOut;
	
	/**
	 * Energy, and the history of energies.
	 */
	protected Energies energies = new Energies();
	
// Attributes -- Parameters
	
	/**
	 * The optimal distance between nodes.
	 */
	protected float k = 1f;

	/**
	 * Default attraction.
	 */
	protected float K1 = 0.06f; // 0.3 ??

	/**
	 * Default repulsion.
	 */
	protected float K2 = 0.024f; // 0.12 ??
	
	/**
	 * Global force strength. This is a factor in [0..1] that is used to scale all computed
	 * displacements.
	 */
	protected float force = 1f;
	
	/**
	 * The view distance at which the cells of the n-tree are explored exhaustively,
	 * after this the poles are used. This is a multiple of k. 
	 */
	protected float viewZone = 5f;

	/**
	 * The Barnes/Hut theta threshold to know if we use a pole or not.
	 */
	protected float theta = 1f;
	
	/**
	 * The quality level.
	 */
	protected int quality = 1;
	
	/**
	 * Number of nodes per space-cell.
	 */
	protected int nodesPerCell = 10;

// Attributes -- Statistics
	
	/**
	 * Current step.
	 */
	protected int time;
	
	/**
	 * The duration of the last step in milliseconds.
	 */
	protected long lastStepTime;
	
	/**
	 * The diagonal of the graph area at the current step.
	 */
	protected float area = 1;
	
	/**
	 * The maximum length of a node displacement at the current step.
	 */
	protected float maxMoveLength;
	
	/**
	 * Average move length.
	 */
	protected float avgLength;
	
	/**
	 * Number of nodes that moved during last step.
	 */
	protected int nodeMoveCount;
	
// Attributes -- Settings
		
	/**
	 * Compute the third coordinate ?.
	 */
	protected boolean is3D = false;
	
	/**
	 * Send node informations?.
	 */
	protected boolean sendNodeInfos = false;
	
	/**
	 * If true a file is created to output the statistics of the elastic box algorithm.
	 */
	protected boolean outputStats = true;
	
	/**
	 * If true a file is created for each node (!!!) and its movement statistics are logged.
	 */
	protected boolean outputNodeStats = false;
	
	/**
	 * If greater than one, move events are sent only every N steps. 
	 */
	protected int sendMoveEventsEvery = 1;

// Constructors
	
	public SpringBox()
	{
		this( false );
	}
	
	public SpringBox( boolean is3D )
	{
		this( is3D, new Random( System.currentTimeMillis() ) );
	}
	
	public SpringBox( boolean is3D, Random randomNumberGenerator )
	{
		CellSpace space;
		
		this.is3D   = is3D;
		this.random = randomNumberGenerator;

		checkEnvironment();
		
		if( is3D )
		     space = new OctreeCellSpace( new Anchor( -1, -1, -1 ), new Anchor( 1, 1, 1 ) );
		else space = new QuadtreeCellSpace( new Anchor( -1, -1, -0.01f ), new Anchor( 1, 1, 0.01f ) );
		
		this.nodes = new ParticleBox( nodesPerCell, space, new BarycenterCellData() );
		
		nodes.addParticleBoxListener( this );
		setQuality( quality );
		
		System.err.printf( "You are using the SpringBox (sur le retour) layout algorithm !%n" );
	}
	
	protected void checkEnvironment()
	{
		Environment env = Environment.getGlobalEnvironment();
		
		if( env.hasParameter( "Layout.3d" ) )
			this.is3D = env.getBooleanParameter( "Layout.3d" );
	}

// Access

	public Point3 getLowPoint()
	{
		return nodes.getNTree().getLowestPoint();
	}

	public Point3 getHiPoint()
	{
		return nodes.getNTree().getHighestPoint();
	}
	
	public ParticleBox getSpatialIndex()
	{
		return nodes;
	}

	public long getLastStepTime()
	{
		return lastStepTime;
	}

	public String getLayoutAlgorithmName()
	{
		return "SpringBox's back";
	}

	public int getNodeMoved()
	{
		return nodeMoveCount;
	}

	public double getStabilization()
	{
		if( time > energies.getBufferSize() )
			return energies.getStabilization();
		
		return 0;
	}
	
	public int getSteps()
	{
		return time;
	}
	
	public int getQuality()
	{
		return quality;
	}
	
	public float getForce()
	{
		return force;
	}

// Commands

	public void setSendNodeInfos( boolean on )
	{
		sendNodeInfos = on;
	}
	
	public void addListener( LayoutListener listener )
	{
		listeners.add( listener );
	}

	public void removeListener( LayoutListener listener )
	{
		int pos = listeners.indexOf( listener );
		
		if( pos >= 0 )
		{
			listeners.remove( pos );
		}
	}

	public void setForce( float value )
	{
		this.force = value;
	}

	public void setQuality( int qualityLevel )
	{
		quality = qualityLevel;
		
		switch( qualityLevel )
		{
			case 0:
				viewZone = k;
				break;
			case 1:
				viewZone = 2*k;
				break;				
			case 2:
				viewZone = 5*k;
				break;
			case 3:
				viewZone = 10*k;
				break;
			case 4:
				viewZone = -1;
				break;
			default:
				System.err.printf( "invalid quality level %d%n", qualityLevel );
				break;
		}
	}

	public void clear()
	{
		// TODO
		throw new RuntimeException( "clear() TODO in ElasticBox. Sorry ;-)" );
	}

	public void compute()
	{
		long t1;
		
		computeArea();
		
		maxMoveLength = Float.MIN_VALUE;
		k             = 1f;
		t1            = System.currentTimeMillis();
		nodeMoveCount = 0;
		avgLength     = 0;
/*
		for( Edge edge : edges.values() )
			edge.attraction();
*/		
		nodes.step();
		
		if( nodeMoveCount > 0 )
			avgLength /= nodeMoveCount;
		
		// Ready for the next step.

		energies.storeEnergy();
		printStats();
		time++;
		lastStepTime = System.currentTimeMillis() - t1;
		
		for( LayoutListener listener: listeners )
			listener.stepCompletion( (float)getStabilization() );
	}
	
	/**
	 * Output some statistics on the layout process. This method is active only if
	 * {@link #outputStats} is true.
	 */
	protected void printStats()
	{
		if( outputStats )
		{
			if( statsOut == null )
			{
	            try
                {
	                statsOut = new PrintStream( "springBox.dat" );
                }
                catch( FileNotFoundException e )
                {
	                e.printStackTrace();
                }
			}
			
			if( statsOut != null )
			{
				float energyDiff = energies.getEnergy() - energies.getPreviousEnergyValue( 30 );
				
				statsOut.printf( Locale.US, "%f %d %f %f %f %f%n",
						getStabilization(), nodeMoveCount,
						energies.getEnergy(),
						energyDiff,
						maxMoveLength, avgLength,
						area );
				statsOut.flush();
			}
		}
	}
	
	protected void computeArea()
	{
		area = getHiPoint().distance( getLowPoint() );
	}
	
	public void shake()
	{
		energies.clearEnergies();
	}

// Graph representation
	
	protected void addNode( String id ) throws SingletonException
	{
		nodes.addParticle( new NodeParticle( this, id ) );
	}

	public void moveNode( String id, float dx, float dy, float dz )
	{
		NodeParticle node = (NodeParticle) nodes.getParticle( id );
		
		if( node != null )
		{
			node.move( dx, dy, dz );
			energies.clearEnergies();
		}
	}

	public void freezeNode( String id, boolean on )
	{
		NodeParticle node = (NodeParticle) nodes.getParticle( id );
		
		if( node != null )
		{
			node.frozen = on;
		}
	}

	protected void setNodeWeight( String id, float weight )
	{
		NodeParticle node = (NodeParticle) nodes.getParticle( id );
		
		if( node != null )
			node.setWeight( weight );
	}

	protected void removeNode( String id ) throws NotFoundException
	{
		NodeParticle node = (NodeParticle) nodes.removeParticle( id );
		
		if( node != null )
		{
			node.removeNeighborEdges();
		}
	}

	protected void addEdge( String id, String from, String to, boolean directed )
			throws NotFoundException, SingletonException
	{
		NodeParticle n0 = (NodeParticle) nodes.getParticle( from );
		NodeParticle n1 = (NodeParticle) nodes.getParticle( to );
		
		if( n0 != null && n1 != null )
		{
			EdgeSpring e = new EdgeSpring( id, n0, n1 );
			EdgeSpring o = edges.put( id, e );
			
			if( o != null )
			{
				//throw new SingletonException( "edge '"+id+"' already exists" );
				System.err.printf( "edge '%s' already exists%n", id );
			}
			else
			{
				n0.registerEdge( e );
				n1.registerEdge( e );
			}
		}
	}

	protected void addEdgeBreakPoint( String edgeId, int points )
	{
		System.err.printf( "edge break points are not handled yet." );
	}
	
	protected void ignoreEdge( String edgeId, boolean on )
	{
		EdgeSpring edge = edges.get( edgeId );
		
		if( edge != null )
		{
			edge.ignored = on;
		}
	}

	protected void setEdgeWeight( String id, float weight )
	{
		EdgeSpring edge = edges.get( id );
		
		if( edge != null )
			edge.weight = weight;
	}

	protected void removeEdge( String id ) throws NotFoundException
	{
		EdgeSpring e = edges.remove( id );
		
		if( e != null )
		{
			e.node0.unregisterEdge( e );
			e.node1.unregisterEdge( e );
		}
	}

	public void outputPos( String filename ) throws IOException
	{
		// TODO Auto-generated method stub
	}

	public void inputPos( String filename ) throws IOException
	{
		// TODO Auto-generated method stub
	}

// Particle box listener

	public void particleAdded( Object id, float x, float y, float z, Object mark )
	{
	}

	public void particleMarked( Object id, Object mark )
	{
	}

	public void particleMoved( Object id, float x, float y, float z )
	{
		if( ( time % sendMoveEventsEvery ) == 0 )
		{
			for( LayoutListener listener: listeners )
				listener.nodeMoved( (String)id, x, y, z );
		}
	}

	public void particleRemoved( Object id )
	{
	}

	public void stepFinished( int time )
	{
	}

	public void particleAdded( Object id, float x, float y, float z )
    {
    }

	public void particleAttributeChanged( Object id, String attribute, Object newValue,
            boolean removed )
    {
    }	
	
// Graph listener

	public void afterEdgeAdd( Graph graph, org.miv.graphstream.graph.Edge edge )
	{
		addEdge(
				edge.getId(),
				edge.getNode0().getId(),
				edge.getNode1().getId(),
				edge.isDirected() );
	}
	
	public void afterNodeAdd( Graph graph, org.miv.graphstream.graph.Node node )
	{
		addNode( node.getId() );
	}
	
	public void beforeEdgeRemove( Graph graph, org.miv.graphstream.graph.Edge edge )
	{
		removeEdge( edge.getId() );
	}
	
	public void beforeNodeRemove( Graph graph, org.miv.graphstream.graph.Node node )
	{
		removeNode( node.getId() );
	}
	
	public void attributeChanged( Element element, String attribute, Object oldValue, Object newValue )
	{
		if( element instanceof org.miv.graphstream.graph.Graph )
		{
			graphAttributeChanged( attribute, newValue );
		}
		else if( element instanceof org.miv.graphstream.graph.Node )
		{
			nodeAttributeChanged( element.getId(), attribute, newValue );
		}
		else if( element instanceof org.miv.graphstream.graph.Edge )
		{
			edgeAttributeChanged( element.getId(), attribute, newValue );
		}
	}
	
	public void beforeGraphClear( Graph graph )
	{
		// TODO
		throw new RuntimeException( "TODO implement graph clear in ElasticBox !!" );
	}
	
	protected void graphAttributeChanged( String attribute, Object value )
	{
		if( attribute.equals( "layout.force" ) )
		{
			if( value instanceof Number )
				setForce( ((Number)value).floatValue() );
			System.err.printf( "layout.elasticBox.force: %f%n", ((Number)value).floatValue() );
		}
		else if( attribute.equals( "layout.quality" ) )
		{
			if( value instanceof Number )
			{
				int q = ((Number)value).intValue();
				
				q = q > 4 ? 4 : q;
				q = q < 0 ? 0 : q;
				
				setQuality( q );
				System.err.printf( "layout.elasticBox.quality: %d%n", q );
			}
		}
		else if( attribute.equals( "layout.exact-zone" ) )
		{
			if( value instanceof Number )
			{
				float factor = ((Number)value).floatValue();
				
				factor = factor > 1 ? 1 : factor;
				factor = factor < 0 ? 0 : factor;
				
				viewZone = factor;
				System.err.printf( "layout.elasticBox.exact-zone: %f of [0..1]%n", viewZone );
			}
		}
		else if( attribute.equals( "layout.output-stats" ) )
		{
			if( value == null )
			     outputStats = false;
			else outputStats = true;
			
			System.err.printf( "layout.elasticBox.output-stats: %b%n", outputStats );
		}
	}
	
	protected void nodeAttributeChanged( String id, String attribute, Object value )
	{
		if( attribute.equals( "layout.weight" ) )
		{
			if( value instanceof Number )
				setNodeWeight( id, ((Number)value).floatValue() );
			else if( value == null )
				setNodeWeight( id, 1 );
		}
	}
	
	protected void edgeAttributeChanged( String id, String attribute, Object value )
	{
		if( attribute.equals( "layout.weight" ) )
		{
			if( value instanceof Number )
				setEdgeWeight( id, ((Number)value).floatValue() );
			else if( value == null )
				setEdgeWeight( id, 1 );
		}
		else if( attribute.equals( "layout.ignored" ) )
		{
			if( value instanceof Boolean )
				ignoreEdge( id, (Boolean)value );
		}
	}

	public void stepBegins(Graph graph, double time)
	{
	}
}