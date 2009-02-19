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

package org.miv.graphstream.algorithm.layout2.elasticbox;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;

import org.miv.graphstream.algorithm.layout2.Layout;
import org.miv.graphstream.algorithm.layout2.LayoutListener;
import org.miv.graphstream.graph.Element;
import org.miv.graphstream.graph.Graph;
import org.miv.pherd.Particle;
import org.miv.pherd.ParticleBox;
import org.miv.pherd.ParticleBoxListener;
import org.miv.pherd.ntree.Anchor;
import org.miv.pherd.ntree.BarycenterCellData;
import org.miv.pherd.ntree.Cell;
import org.miv.pherd.ntree.CellSpace;
import org.miv.pherd.ntree.OctreeCellSpace;
import org.miv.pherd.ntree.QuadtreeCellSpace;
import org.miv.util.Environment;
import org.miv.util.NotFoundException;
import org.miv.util.SingletonException;
import org.miv.util.geom.Point3;
import org.miv.util.geom.Vector3;

/**
 * An implementation of a graph layout that mostly follows the Fruchterman-Reingold
 * algorithm with the addition of a recursive space decomposition approximation like
 * the one introduced by Quigley and Eades.
 * 
 * <p>
 * The FR algorithm tries to produce aesthetically pleasing graph layouts where
 * the distance between nodes tries to approach a given constant (here one graph unit).
 * </p>
 *
 * <p>
 * In the FR algorithm a global temperature parameter is used
 * to constrain the displacement of nodes in order to attain stability. The
 * problem here is that the graph can evolve with time. Therefore the temperature
 * is elevated each time the graph changes so that the layout can adapt.
 * </p>
 * 
 * <p>
 * Another difference of this algorithm is that it does not put "walls"
 * around the graph. Here, the graph "floats" in space. Nodes do not stick
 * to the walls. This produces more aesthetic drawings.
 * </p>
 * 
 * <p>
 * This implementation also departs from the original algorithm in the way
 * forces are applied to nodes to boost the layout speed. This is called the
 * "burst mode" and one can switch back to the original FR
 * algorithm by setting a graph attribute "burst-mode" to a boolean value "false".
 * </p>
 * 
 * <p>
 * This implementation tries to avoid the O(n^2) complexity of force-based
 * graph layout algorithms by using a recursive space decomposition where
 * distant nodes are "aggregated" as a barycentre, following ideas well
 * described by Quigley A. and Eades P. () in "FADE: Graph drawing, clustering and
 * visual abstraction" in "Graph Drawing, Springer LNCS", Vol 1984/2001, pp. 77-80.
 * </p>
 * 
 * <p>
 * The original article: Fruchterman T. M. J., & Reingold E. M. (1991).
 * Graph Drawing by Force-Directed Placement. Software: Practice and
 * Experience, 21(11). 
 * </p>
 * 
 * <p>
 * This layout is NOT thread safe. You cannot put it in a thread an expect it
 * to listen at a graph without problems. To avoid these problems, you can use
 * the {@link org.miv.graphstream.algorithm.layout2.LayoutRunner} to avoid these
 * problems.
 * </p>
 * 
 * <p>
 * This algorithm understands several special attributes on the graph, on edges and on
 * nodes. Here is a list :
 * <ul>
 * 		<li>layout.weight : on edges and nodes, allows to change the importance of the element.</li>
 * 		<li>layout.force : on graphs, allows to vary the global layout force.</li>
 * 		<li>layout.ignored : on edges, allows to ignore an edge if the attribute is present and is value evaluates to a boolean.</li>
 *  	<li>layout.burst-mode : on graphs, if the attribute is present, allows to select the FR modified algorithm.</li>
 *		<li>layout.quality : on graphs, allows to setup five levels (0..4) of presets for the algorithm (0=speed, 4=quality).</li>
 *		<li>layout.cool-factor : on graphs, allows to specify how quick the global temperature decreases.</li>
 *		<li>layout.exact-zone : on graphs, the zone of space around a node that is explored instead of using barycentres (a percent of the total area, [0..1])</li>
 *		<li>layout.output-stats : create a "elasticbox.dat" file in the current directory. Each line is a step. For each line the data is as follows :
 *			<ul>
 *				<li>1 : Stabilisation (float [0..1] ;</li>
 *				<li>2 : N node moved at last step (integer) ;</li>
 *				<li>3 : Cool factor (float [0..1]) ;</li>
 *				<li>4 : Temperature (float [0..1]) ;</li>
 *				<li>5 : Energy (float) ;</li>
 *				<li>6 : Energy difference (float) ;</li>
 *				<li>7 : Maximum movement length at last step ;</li>
 *				<li>8 : Average movement length at last step ;</li>
 *				<li>9 : Overall graph width size (diagonal of graph).</li>
 *			</ul>
 *		</li>
 * </ul>
 * </p>
 * 
 * TODO: Temperature and simulated annealing ?
 * TODO: How to set temperature : obviously temperature should decrease more slowly when there are a lot of nodes.
 * 
 * @author Antoine Dutot
 * @since 2007
 */
public class ElasticBox implements Layout, ParticleBoxListener
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
	protected HashMap<String,Edge> edges = new HashMap<String,Edge>();
	
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
	
// Attributes -- Parameters
	
	/**
	 * The optimal distance between nodes.
	 */
	protected float k = 1f;
	
	/**
	 * Global force strength. This is a factor in [0..1] that is used to scale all computed
	 * displacements.
	 */
	protected float force = 0.01f;
	
	/**
	 * Global temperature. This is a factor that is used to multiply the force applied to all
	 * displacements. The temperature decreases with time. As soon as the graph is modified the
	 * temperature increases. Else if no modification occurs, it will slowly reach zero.
	 * The way the temperature decreases is controlled by the {@link #coolFactor}.
	 */
	protected float temperature = 1f;
	
	/**
	 * The temperature maximum, the temperature is computed from the cool factor and this max.
	 */
	protected float temperatureMax = 1f;
	
	/**
	 * The cool factor. This allows to decrease temperature. This factor is automatically
	 * computed.
	 */
	protected float coolFactor = 0.99f;
	
	/**
	 * The view distance at which the cells of the n-tree are explored exhaustively,
	 * after this the poles are used. This is a percent of the total graph area. 
	 */
	protected float viewZone = 0.06f;

	/**
	 * The maximum distance of view as a percent of the graph total area. For distant node poles
	 * (found within the n-tree) we consider them only if they are in a globe around us whose
	 * radius is this number*graph-area.
	 */
	protected float maxView = 1f;
	
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
	 * The diagonal of the graph area at the current step.
	 */
	protected float area = 1;
	
	/**
	 * The maximum length of a node displacement at the current step.
	 * This is set in the node#move(int) method.
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
	
	/**
	 * Current step.
	 */
	protected int time;
	
	/**
	 * The duration of the last step in milliseconds.
	 */
	protected long lastStepTime;
	
	/**
	 * Global energy.
	 */
	protected float energy;
	
	/**
	 * A circular array of the last values of energy.
	 */
	protected float[] energies = new float[256];
	
	/**
	 * The current position in the energies array.
	 */
	protected int energiesPos = 0;
	
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
	 * A hack of the original algorithm. Not suitable for all graphs.
	 */
	protected boolean burstMode = true;
	
	/**
	 * If true a file is created to output the statistics of the elastic box algorithm.
	 */
	protected boolean outputStats = false;
	
	/**
	 * If true a file is created for each node (!!!) and its movement statistics are logged.
	 */
	protected boolean outputNodeStats = false;
	
	/**
	 * If greater than one, move events are sent only every N steps. 
	 */
	protected int sendMoveEventsEvery = 1;

// Constructors
	
	public ElasticBox()
	{
		this( false );
	}
	
	public ElasticBox( boolean is3D )
	{
		this( is3D, new Random( System.currentTimeMillis() ) );
	}
	
	public ElasticBox( boolean is3D, Random randomNumberGenerator )
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
		
		System.err.printf( "You are using the ElasticBox layout algorithm !%n" );
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

	public long getLastStepTime()
	{
		return lastStepTime;
	}

	public String getLayoutAlgorithmName()
	{
		return "Fruchterman-Reingold (modified)";
	}

	public int getNodeMoved()
	{
		return nodeMoveCount;
	}

	public double getStabilization()
	{
		// The stability is attained when the global energy of the graph do not vary anymore.
		
		int range = 200;
		
		if( time > range )
		{
			float max   = 10;
			float eprev = getPreviousEnergyValue( range );
			float diff  = (float) Math.abs( energy - eprev );
			diff = diff > max ? max : diff;
			
			if( diff < 1 )
				diff = 0;
			
			return 1 - ( diff / max );
		}

		return 0;
	}
	
	public double getStabilizationOld()
	{
		// Old way : count the number of nodes that moved compared to the number of nodes.
		// Problem : we may not attain stability easily or take a very long time to attain it.  

		float pc = nodes.getParticleCount();
		
		if( pc > 0 )
			return (nodeMoveCount / pc);
		
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
				heat();
				break;
			case 1:
				viewZone = 2*k;
				heat();
				break;				
			case 2:
				viewZone = 5*k;
				heat();
				break;
			case 3:
				viewZone = 10*k;
				heat();
				break;
			case 4:
				viewZone = -1;
				heat();
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
		
//		long t2 = System.currentTimeMillis();

		maxMoveLength = Float.MIN_VALUE;

		k          = 1f;
		t1         = System.currentTimeMillis();
		nodeMoveCount = 0;
		coolFactor = 1f - ( 1f / ((Math.max(nodes.getParticleCount(),1000))*2) );

		temperatureMax = area / 100;
		energy         = 0;
		
		// Loop on edges to compute edge attraction.
		
		for( Edge edge : edges.values() )
			edge.attraction();
		
//		long t3 = System.currentTimeMillis();
		
		// Loop on nodes to compute node repulsion.
		
		avgLength=0;
		nodes.step();
		
//		long t4 = System.currentTimeMillis();
		
		if( nodeMoveCount > 0 )
			avgLength /= nodeMoveCount;
		
		// Ready for next step.

		addEnergyValue();
		printStats();
		cool();
		time++;
		lastStepTime = System.currentTimeMillis() - t1;
//		System.err.printf( "TIME=%f  (A=%f  B=%f  C=%f)%n",
//				lastStepTime/1000f,
//				(t2-t1)/1000f,
//				(t3-t2)/1000f,
//				(t4-t3)/1000f);
		
		for( LayoutListener listener: listeners )
			listener.stepCompletion( (float)getStabilization() );
	}
	
	protected void addEnergyValue()
	{
		energiesPos = ( energiesPos + 1 ) % energies.length;
		
		energies[energiesPos] = energy;
	}
	
	protected float getPreviousEnergyValue( int stepsBack )
	{
		if( stepsBack >= energies.length )
			stepsBack = energies.length - 1;
		
		int pos = ( energies.length + ( energiesPos - stepsBack ) ) % energies.length;
		
		return energies[pos];
	}
	
	/**
	 * Output some statistics on the elastic box process. This method is active only if
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
	                statsOut = new PrintStream( "elasticBox.dat" );
                }
                catch( FileNotFoundException e )
                {
	                e.printStackTrace();
                }
			}
			
			if( statsOut != null )
			{
				float energyDiff = energy - getPreviousEnergyValue( 30 );
				
				statsOut.printf( Locale.US, "%f %d %f %f %f %f %f %f%n",
						getStabilization(), nodeMoveCount,
						coolFactor, temperature,
						energy,  energyDiff,
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
	
	protected void heat()
	{
		temperature += 0.1f;
		
		if( temperature > 1f )
			temperature = 1f;
	}
	
	protected void cool()
	{
		float stab = 1-(float)getStabilization();
	
		temperature *= coolFactor;
		temperature *= stab;
		
		if( temperature <= 0.09f )
			temperature = 0f;
	}
	
	public void shake()
	{
		temperature = 1f;
		clearEnergies();
	}
	
	/**
	 * Randomise the energies array.
	 */
	protected void clearEnergies()
	{
		for( int i=0; i<energies.length; ++i )
			energies[i] = (float) ( ( Math.random() * 2000 ) - 1000 );
	}

// Graph representation
	
	protected void addNode( String id ) throws SingletonException
	{
		nodes.addParticle( new Node( id ) );
		heat();
	}

	public void moveNode( String id, float dx, float dy, float dz )
	{
		Node node = (Node) nodes.getParticle( id );
		
		if( node != null )
		{
			node.move( dx, dy, dz );
			heat();
			clearEnergies();
		}
	}

	public void freezeNode( String id, boolean on )
	{
		Node node = (Node) nodes.getParticle( id );
		
		if( node != null )
		{
			node.frozen = on;
			
			if( on == false )
				heat();
		}
	}

	protected void setNodeWeight( String id, float weight )
	{
		Node node = (Node) nodes.getParticle( id );
		
		if( node != null )
			node.setWeight( weight );
	}

	protected void removeNode( String id ) throws NotFoundException
	{
		Node node = (Node) nodes.removeParticle( id );
		
		if( node != null )
		{
			node.removeNeighborEdges();
			heat();
		}
	}

	protected void addEdge( String id, String from, String to, boolean directed )
			throws NotFoundException, SingletonException
	{
		Node n0 = (Node) nodes.getParticle( from );
		Node n1 = (Node) nodes.getParticle( to );
		
		if( n0 != null && n1 != null )
		{
			Edge e = new Edge( id, n0, n1 );
			Edge o = edges.put( id, e );
			
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
			
			heat();
		}
	}

	protected void addEdgeBreakPoint( String edgeId, int points )
	{
		System.err.printf( "edge break points are not handled yet." );
	}
	
	protected void ignoreEdge( String edgeId, boolean on )
	{
		Edge edge = edges.get( edgeId );
		
		if( edge != null )
		{
			edge.ignored = on;
		}
	}

	protected void setEdgeWeight( String id, float weight )
	{
		Edge edge = edges.get( id );
		
		if( edge != null )
			edge.weight = weight;
	}

	protected void removeEdge( String id ) throws NotFoundException
	{
		Edge e = edges.remove( id );
		
		if( e != null )
		{
			e.node0.unregisterEdge( e );
			e.node1.unregisterEdge( e );
			heat();
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
			{
				listener.nodeMoved( (String)id, x, y, z );
			}
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
	
// Nested classes
	
/**
 * Node representation. 
 */
protected class Node extends Particle
{
// Attributes
	
	/**
	 * Set of edge connected to this node.
	 */
	public ArrayList<Edge> neighbours = new ArrayList<Edge>();
	
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

// Constructors
	
	/**
	 * New node.
	 * @param id The node identifier.
	 */
	public Node( String id )
	{
		this( id,
			(random.nextFloat()*2*k)-k,
			(random.nextFloat()*2*k)-k,
			is3D ? (random.nextFloat()*2*k)-k : 0 );
	}
	
	/**
	 * New node at a given position.
	 * @param id The node identifier.
	 * @param x The abscissa.
	 * @param y The ordinate.
	 * @param z The depth.
	 */
	public Node( String id, float x, float y, float z )
	{
		super( id, x, y, is3D ? z : 0 );
		disp = new Vector3();
		createDebug();
	}
	
	protected void createDebug()
	{
		if( outputNodeStats )
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
	public Collection<Edge> getEdges()
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
			
			if( viewZone < 0 )
			     repulsionN2( delta );
			else repulsionNLogN( delta );
			
			attraction();
			
			len = disp.length();
			
			avgLength += len;
			
			if( len > maxMoveLength )
				maxMoveLength = len;
			
			if( getId().equals( "10_10" ) )
			{
				Vector3 v = new Vector3( disp );
				v.normalize();
				System.err.printf( "disp = %s%n", v );
			}
		}
	}
	
	@Override
	public void nextStep( int time )
	{
		float l;
		
		// Compute the movement length (force):
		
		if( burstMode )
		{
			// Displacement force relative to the area size :
			// The "burst modeField", departs from the original Fruchterman-Reingold algorithm by not
			// limiting the displacement to the temperature (min of the displacement and
			// temperature). Instead we only ensure the max movement will never be larger than
			// the graph area/2 to avoid introducing to much force.
			
			l = len;
			
			//disp.normalize();
			disp.scalarMult( ( (area/6)/maxMoveLength ) * force );
			//disp.scalarMult( ( (area)/maxMoveLength ) * force );
			
/*			// Inertia :
			// To avoid too large oscillations, always caused by a too large attraction factor
			// we divide the force applied to the node by the square of its the part of its degree
			// that is superior to the average degree. The effect
			// is to make very connected nodes difficult to move (nodes that have a degree higher
			// than the average degree): as if they have a lot of inertia.
			
			float n = getEdges().size();
			
			if( n > 0 )
			{
				float avgDegree = (edges.size()*2f)/nodes.getParticleCount();
				n = n - avgDegree;
				
				if( n > 1 )
				{
					float ratio = 1f / n; 
					//disp.scalarMult( ratio*ratio );
//					disp.scalarMult( ratio * ratio );
					disp.scalarMult( ratio );	// The square of the degree slows down thing a lot for very regular graphs.
				}
			}
*/		
			// Temperature :
			// A temperature factor cools as time passes and scales the movement of nodes.

			if( temperature < 0.4f )
				disp.scalarMult( temperature );
			
			l = disp.length();
		}
		else
		{
			// The original Fruchterman-Reingold implementation :
			// The displacement length is bounded by a temperature value that decreases with time.
			
			len = disp.normalize();

			float t = temperatureMax * temperature;
		
			disp.scalarMult( t < len ? t : len );
			disp.scalarMult( force );
		
			l = disp.length();
		}
		
		// Apply it to the position:
		// We apply the movement vector only if this vector moves from a significant
		// length (relative to the graph total area). Else we do nothing and let the
		// node in place. This is another mechanism to allow the layout to stabilise...
		
		if( l > area * 0.0000001f )
		{
			nextPos.x = pos.x + disp.data[0];
			nextPos.y = pos.y + disp.data[1];
			
			if( is3D )
			     nextPos.z = pos.z + disp.data[2];

			nodeMoveCount++;
			moved = true;
		}
		
		// Eventually output movement information to the listeners.

		if( sendNodeInfos )
		{
			for( LayoutListener listener: listeners )
				listener.nodeInfos( (String)id, (float)disp.data[0], (float)disp.data[1],
					is3D ? (float)disp.data[2] : 0 );
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
	
	protected void repulsionN2( Vector3 delta )
	{
		Iterator<Object> i = nodes.getParticleIdIterator();
		
		while( i.hasNext() )
		{
			Node node = (Node) nodes.getParticle( i.next() );
			
			if( node != this )
			{
				delta.set( node.pos.x - pos.x, node.pos.y - pos.y, is3D ? node.pos.z - pos.z : 0 );
				
				float len = delta.normalize();
				
				float factor = -(k*k)/len * weight * node.weight;
				delta.scalarMult( factor );
				disp.add( delta );
				energy += factor;
			}
		}
	}
	
	protected void repulsionNLogN( Vector3 delta )
	{
		// Explore the n-tree from the root cell and consider the contents
		// of one cell only if it does intersect an area around the current
		// node. Else take its (weighted) barycenter into account.
		
		recurseRepulsion( nodes.getNTree().getRootCell(), delta );
	}
	
	protected void recurseRepulsion( Cell cell, Vector3 delta )
	{
		if( intersection( cell ) )
		{
			if( cell.isLeaf() )
			{
				Iterator<? extends Particle> i = cell.getParticles();
				
				while( i.hasNext() )
				{
					Node node = (Node) i.next();
					
					if( node != this )
					{
						delta.set( node.pos.x - pos.x, node.pos.y - pos.y, is3D ? node.pos.z - pos.z : 0 );

						float len = delta.normalize();

						if( len != 0 )
						{
							float factor = (-(k*k)/len) * weight * node.weight;
							energy += factor;
							repE   += factor;
							delta.scalarMult( factor );
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
			BarycenterCellData bary = (BarycenterCellData) cell.getData();
			
			delta.set( bary.center.x - pos.x,
			           bary.center.y - pos.y,
			    is3D ? bary.center.z - pos.z : 0 );

			float len = delta.normalize();
			
			if( len < area*maxView )
			{
				if( len != 0 )
				{
					float factor = (-(k*k)/len) * bary.weight * weight;
					energy += factor;
					repE   += factor;
					delta.scalarMult( factor );
				}

				disp.add( delta );
			}
		}
	}
	
	protected void attraction()
	{
		for( Edge edge : neighbours )
		{
			if( ! edge.ignored )
			{
				if( this == edge.node0 )
				     disp.add( edge.spring );
				else disp.sub( edge.spring );

				attE += edge.attE;
			}
		}
	}
	
	protected boolean intersection( Cell cell )
	{
		float x1 = cell.getSpace().getLoAnchor().x;
		float y1 = cell.getSpace().getLoAnchor().y;
		float z1 = cell.getSpace().getLoAnchor().z;
		float x2 = cell.getSpace().getHiAnchor().x;
		float y2 = cell.getSpace().getHiAnchor().y;
		float z2 = cell.getSpace().getHiAnchor().z;
		
		float X1 = pos.x - area*viewZone;
		float Y1 = pos.y - area*viewZone;
		float Z1 = pos.z - area*viewZone;
		float X2 = pos.x + area*viewZone;
		float Y2 = pos.y + area*viewZone;
		float Z2 = pos.z + area*viewZone;
		
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
	public void registerEdge( Edge e )
	{
		neighbours.add( e );
	}

	/**
	 * The given edge is no more connected to this node.
	 * @param e THe edge to disconnect.
	 */
	public void unregisterEdge( Edge e )
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
		for( Edge edge: neighbours )
		{
			removeEdge( edge.id );
		}
	}
	
	/**
	 * Move the node by a random vector.
	 */
	public void shake()
	{
		pos.x += random.nextFloat() * k * 2 - 1;
		pos.y += random.nextFloat() * k * 2 - 1;
		
		if( is3D )
			pos.z += random.nextFloat() * k * 2 - 1;
	}
}

/**
 * Edge representation.
 */
protected class Edge
{
	/**
	 * The edge identifier.
	 */
	public String id;
	
	/**
	 * Source node.
	 */
	public Node node0;
	
	/**
	 * Target node.
	 */
	public Node node1;
	
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
	public Edge( String id, Node n0, Node n1 )
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
	public Node getOpposite( Node node )
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
			Point3 p0 = node0.getPosition();
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
		}
	}
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
		else if( attribute.equals( "layout.cool-factor" ) )
		{
			if( value instanceof Number )
			{
				float factor = ((Number)value).floatValue();
				
				factor = factor > 1 ? 1 : factor;
				factor = factor < 0 ? 0 : factor;
				
				coolFactor = factor;
				heat();
				System.err.printf( "layout.elasticBox.cool-factor: %f%n", coolFactor );
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
				heat();
				System.err.printf( "layout.elasticBox.exact-zone: %f of [0..1]%n", viewZone );
			}
		}
		else if( attribute.equals( "layout.burst-mode" ) )
		{
			burstMode = ( value != null );
			
			System.err.printf( "layout.elasticBox.burst-mode: %b%n", burstMode );
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