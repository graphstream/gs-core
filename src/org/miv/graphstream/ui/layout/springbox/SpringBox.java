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
package org.miv.graphstream.algorithm.layout2.springbox;

import java.io.*;
import java.util.*;

import org.miv.graphstream.algorithm.layout2.*;
import org.miv.graphstream.graph.Element;
import org.miv.graphstream.graph.Graph;
import org.miv.util.*;
import org.miv.util.geom.*;
import org.miv.util.set.*;

/**
 * Spring and electric forces based layout algorithm.
 * 
 * <p>
 * The spring box takes a graph description as input and continuously update its
 * internal representation of this graph following a spring-based layout
 * algorithm until it stabilises. The spring box outputs its computations to a
 * listener for each node of the graph.
 * </p>
 * 
 * <p>
 * The spring box can produce layouts both in two or three dimensions. It can
 * use a "spatial index", that is a division of space in cells so that when a node
 * explores its surroundings to find other nodes it can find them quicker. This,
 * however makes sense only if a node searches its surroundings only at a given
 * distance. The original algorithm uses all the nodes, this is more
 * correct, but usually very slow. Looking at only a neighbourhood, generally
 * produces the same results. 
 * </p>
 * 
 * <p>
 * The other speedup of the original algorithm is to use the average position of
 * all the nodes in distant cells instead of considering the nodes. This setting
 * also decrease the layout quality but speed it up a lot. When the layout becomes
 * acceptable, it is possible to turn off this setting to refine the layout and
 * produce very pleasant results.
 * </p>
 * 
 * <p>
 * When using the spatial index, the spring algorithm is very easily threadable. By
 * default the spring box does its work in the current thread. However, it is
 * possible to give it a power-of-two number of threads so that it performs its
 * computation in several threads. This may be helpful on computer that have
 * several CPUs or several CPU cores.
 * </p>
 * 
 * @author Antoine Dutot
 * @since 20050706
 */
public class SpringBox implements Layout
{
// Attribute

	/**
	 * Default attraction.
	 */
	protected float K1 = 0.3f; // 0.5 ??

	/**
	 * Default repulsion.
	 */
	protected float K2 = 0.12f; // 0.003 ??

	/**
	 * Nominal distance between nodes.
	 */
	protected float NL = 1f; // 0.1 ??

	/**
	 * Default spring force.
	 */
	protected float forceStrength = 0.02f;

	/**
	 * Zone the sprite is able to explore.
	 */
	protected int viewZone = 2;

	/**
	 * Glue nodes when they move of less than 1/glueNodeAt of the display width.
	 */
	protected float glueNodeAt = 10000;
	
	/**
	 * Quality level.
	 */
	protected int quality = 2;
	
// Attribute

	/**
	 * All the nodes by their id.
	 */
	protected HashMap<String, Node> nodes = new HashMap<String, Node>( 10000 );

	/**
	 * All the nodes.
	 */
	protected FixedArrayList<Node> nodesArray = new FixedArrayList<Node>( 1000 );

	/**
	 * All the edges by their id.
	 */
	protected HashMap<String, Edge> edges = new HashMap<String, Edge>( 10000 );

	/**
	 * All the edges.
	 */
	protected FixedArrayList<Edge> edgesArray = new FixedArrayList<Edge>( 1000 );

// Attribute -- Geometry

	/**
	 * Lowest point in space.
	 */
	protected Point3 lo = new Point3();// -0.1f, -0.01f, -0.10f );

	/**
	 * Highest point in space.
	 */
	protected Point3 hi = new Point3();// 0.1f, 0.01f, 0.01f );

// Attribute -- Node map

	/**
	 * Use the space map?.
	 */
	protected boolean useMap = true;

	/**
	 * Compute the layout in three dimensions?.
	 */
	protected boolean use3d = false;

	/**
	 * Use averages in the space map?. If true an average node position is
	 * computed for all cells in the map.
	 */
	protected boolean averageMap = false;

	/**
	 * Size of the space map. The map will be div*div cell large in 2D and
	 * div*div*div cell large in 3D.
	 */
	protected int div = 20;

	/**
	 * The space map in 3D.
	 */
	protected MapCell[][][] XYZ;

	/**
	 * The space map in 2D.
	 */
	protected MapCell[][] XY;

// Attribute

	/**
	 * Send 'move' messages?. Disabling this kind of event can speedup things a
	 * lot.
	 */
	protected boolean moveMessages = true;

	/**
	 * If enabled, nodes are never locked. A node is locked if when its computed
	 * movement is small, the node decides to stay in place instead.
	 */
	protected boolean glueNodes = true;

	/**
	 * Number of steps performed so far.
	 */
	protected int steps = 0;

// Attribute

	/**
	 * How many nodes moved during the last step?.
	 */
	protected int nodeMoved;

	/**
	 * Listeners.
	 */
	protected ArrayList<LayoutListener> listeners = new ArrayList<LayoutListener>();

	/**
	 * Used to measure step() time.
	 */
	protected long t1, t2;

// Attribute -- Options

	/**
	 * Number of threads to use to do the layout. This allows to better
	 * distributed the load on multi-processor machines. Values less or equal to
	 * 1 disable the use of threads. Furthermore, threads will be used only if
	 * the node-map is enabled.
	 */
	protected int useThreads = 1;

	/**
	 * Auto create nodes for edges that reference non-existing nodes?.
	 */
	protected boolean autoNodes = true;
	
	/**
	 * Strictly checks nodes are not duplicated or edges are not duplicated.
	 */
	protected boolean strict = true;
	
	/**
	 * Avoid to send to much "move messages".
	 */
	protected int sendMoveMessagesEvery = 20;
	
// Construction

	/**
	 * New empty spring box. The spring box can layout a graph both in two or
	 * three dimensions. Often, for very large graphs
	 * @param use3d Compute the layout in three dimensions?.
	 * @param divisions How many division of the space map. Less than 1 means no
	 *        space map. The space map, with a correct viewZone setting, allows
	 *        node to explore space more quickly.
	 * @param viewZone If divisions is larger than 1, this tells how many cells
	 *        a node can see around. The minimum should always at least be 2,
	 *        else nodes on the border of a cell would be badly layouted. Using
	 *        this setting, nodes are influenced by a neighbourhood only, not by
	 *        the whole graph. This usually speed up things a lot.
	 * @param averageMap If true and if divisions is larger than 1, and if
	 *        viewZone is larger than 1, use the average node position of each
	 *        neighbor cell instead of iterating on all the nodes contained in
	 *        the cell. This setting does not produce a perfect layout, but
	 *        usually produces good results and speeds up things by lightyears.
	 *        Deactivate it only at the end of the layout to refine it using
	 *        {@link #setAverageMap(boolean)}.
	 */
	public SpringBox( boolean use3d, int divisions, int viewZone,
			boolean averageMap, int threads )
	{
		this.useMap     = ( divisions > 1 );
		this.use3d      = use3d;
		this.averageMap = averageMap;
		this.div        = divisions;
		this.viewZone   = viewZone;
		this.strict     = false;

		setThreadCount( threads );
		
		if( viewZone < 2 )
			viewZone = 2;

		initMap();
	}

	/**
	 * Spring empty box using a 2D space map with 20 divisions, a view zone of 4
	 * cells, 1 thread and no average of the map. This is a convenience
	 * constructor.
	 */
	public SpringBox()
	{
		// Setup reasonable defaults.
		
		this.useMap     = true;
		this.use3d      = false;
		this.averageMap = true;
		this.div        = 20;
		this.viewZone   = 4;
		int threads     = 1;
		this.strict     = false;
		
		// Setup this whole big shit.
		
		setThreadCount( threads );

		// Still check the environment for settings.
		
		setQuality( 1 );
		checkEnvironment();
		
		if( viewZone < 2 )
			viewZone = 2;
		
		initMap();
		
//		System.err.printf( "You are using the SpringBox layout algorithm !%n" );
	}
	
	protected void checkEnvironment()
	{
		Environment env = Environment.getGlobalEnvironment();
		
		if( env.hasParameter( "SpringBox.3d" ) )
			this.use3d = env.getBooleanParameter( "SpringBox.3d" );
		
		if( env.hasParameter( "Layout.3d" ) )
			this.use3d = env.getBooleanParameter( "Layout.3d" );
		
		if( env.hasParameter( "SpringBox.map" ) )
			this.useMap = env.getBooleanParameter( "SpringBox.map" );
		
		if( env.hasParameter( "SpringBox.bary" ) )
			this.averageMap = env.getBooleanParameter( "SpringBox.bary" );
		
		if( env.hasParameter( "SpringBox.div" ) )
			this.div = (int) env.getNumberParameter( "SpringBox.div" );
		
		if( env.hasParameter( "SpringBox.view" ) )
			this.viewZone = (int) env.getNumberParameter( "SpringBox.view" );
		
		if( env.hasParameter( "SpringBix.threads" ) )
		{
			int threads = (int) env.getNumberParameter( "SpringBox.threads" );
			setThreadCount( threads );
		}
	}

	/**
	 * Creates the space map.
	 */
	protected void initMap()
	{
		if( use3d )
		{
			XY = null;
			XYZ = new MapCell[div][][];

			for( int x = 0; x < div; ++x )
			{
				XYZ[x] = new MapCell[div][];

				for( int y = 0; y < div; ++y )
				{
					XYZ[x][y] = new MapCell[div];

					for( int z = 0; z < div; ++z )
					{
						XYZ[x][y][z] = new MapCell();
					}
				}
			}
		}
		else
		{
			XY = new MapCell[div][];
			XYZ = null;

			for( int x = 0; x < div; ++x )
			{
				XY[x] = new MapCell[div];

				for( int y = 0; y < div; ++y )
				{
					XY[x][y] = new MapCell();
				}
			}
		}
	}

	/**
	 * Recompute the space bounds according to the maximal and minimal node
	 * positions.
	 */
	protected void resetBounds()
	{
		int   n    = nodes.size();
		float xmin = Float.MAX_VALUE, ymin = Float.MAX_VALUE, zmin = Float.MAX_VALUE;
		float xmax = Float.MIN_VALUE, ymax = Float.MIN_VALUE, zmax = Float.MIN_VALUE;
		Node  node;

		Iterator<?> i = nodes.values().iterator();

		while( i.hasNext() )
		{
			node = (Node) i.next();

			if( !node.hiden )
			{
				if( node.x < xmin ) xmin = node.x;
				if( node.y < ymin ) ymin = node.y;
				if( node.z < zmin ) zmin = node.z;

				if( node.x > xmax ) xmax = node.x;
				if( node.y > ymax ) ymax = node.y;
				if( node.z > zmax ) zmax = node.z;
			}
		}

		if( n > 0 )
		{
			// Add a small offset to ensure each node is visible.

			// float w, h, d;
			float gapw, gaph, gapd;

			// w = xmax - xmin;
			// h = ymax - ymin;
			// d = zmax - zmin;
			gapw = 0;// 0.05f * w;
			gaph = 0;// 0.05f * h;
			gapd = 0;// 0.05f * d;

			lo.set( xmin - gapw, ymin - gaph, zmin - gapd );
			hi.set( xmax + gapw, ymax + gaph, zmax + gapd );
		}
		else
		{
			lo.set( 0, 0, 0 );
			hi.set( 1, 1, 1 );
		}
	}

	/**
	 * Clears the whole nodes and edges structures
	 */
	public void clear()
	{
		nodes.clear();
		nodesArray.clear();
		edges.clear();
		edgesArray.clear();
	}
	
// Access

	public String getLayoutAlgorithmName()
	{
		return "SpringBox";
	}
	
	/**
	 * Does the spring box checks duplicated nodes or edges ?
	 */
	public boolean isStrict()
	{
		return strict;
	}
	
	/**
	 * Number of divisions of the space map.
	 */
	public int getDivisions()
	{
		return div;
	}

	/**
	 * Number of threads to used when doing the layout.
	 * @return The number of threads.
	 */
	public int getThreads()
	{
		return useThreads;
	}

	/**
	 * Are nodes created automatically when an edge references non-existing
	 * nodes?.
	 * @return True if nodes are created automatically.
	 */
	public boolean getAutoCreateNodes()
	{
		return autoNodes;
	}

	/**
	 * Cell at position (x,y,z).
	 */
	public MapCell getCell( int x, int y, int z )
	{
		return XYZ[x][y][z];
	}

	/**
	 * Cell at position (x,y).
	 */
	public MapCell getCell( int x, int y )
	{
		return XY[x][y];
	}

	/**
	 * Number of nodes.
	 */
	public int getNodeCount()
	{
		return nodes.size();
	}

	/**
	 * Number of edges.
	 */
	public int getEdgeCount()
	{
		return edges.size();
	}

	/**
	 * All nodes.
	 */
	@SuppressWarnings("unchecked")
	public Iterator getNodes()
	{
		return nodes.values().iterator();
	}

	/**
	 * All edges.
	 */
	@SuppressWarnings("unchecked")
	public Iterator getEdges()
	{
		return edges.values().iterator();
	}

	/**
	 * The node with the identifier id or null if no node has this identifier.
	 */
	public Node getNode( String id )
	{
		return nodes.get( id );
	}

	/**
	 * The edge with the identifier id or null if no edge has this identifier.
	 * @param id The identifier to search.
	 * @return The corresponding edge or null if not found.
	 */
	public Edge getEdge( String id )
	{
		return edges.get( id );
	}

	/**
	 * How many node moved during the last step?. When this method returns zero,
	 * the layout stabilised.
	 */
	public int getNodeMoved()
	{
		return nodeMoved;
	}

	/**
	 * Percent of nodes moving (between [0..1]). When this number reaches 0, the
	 * layout is stabilised.
	 */
	public double getStabilization()
	{
		if( nodes.size() <= 0 )
			return 0;

		float stab = ( (float) nodeMoved / (float) nodes.size() ); 
		
		if( stab < 0.1f )
			stab = 0;
		
//		System.err.printf( "moved = %d (/%d) = %f%n", nodeMoved, nodes.size(), stab );
		
		return stab;
	}

	/**
	 * Smallest point in space of the layout bounding box.
	 */
	public Point3 getLowPoint()
	{
		return lo;
	}

	/**
	 * Largest point in space of the layout bounding box.
	 */
	public Point3 getHiPoint()
	{
		return hi;
	}

	/**
	 * Number of calls made to step() so far.
	 */
	public int getSteps()
	{
		return steps;
	}

	/**
	 * Time in nanoseconds used by the last call to step().
	 */
	public long getLastStepTime()
	{
		return( t2 - t1 );
	}

	/**
	 * Is map averaging on?.
	 * @return True if map averaging is enabled.
	 */
	public boolean getAverageMap()
	{
		return averageMap;
	}

	/**
	 * Force factor.
	 * @return The factor.
	 */
	public float getForce()
	{
		return forceStrength;
	}

	/**
	 * The percentage of nodes that have moved at the previous step.
	 * @return A number between 0 and 1.
	 */
	public float getMovePercent()
	{
		int s = nodes.size();
		
		if( s > 0 )
			return( nodeMoved / s );
		
		return 0;
	}
	
// Command -- Map

	/**
	 * Allow or disallow node or edges duplicates.
	 * @param on If true, the node or edges duplicates throws SingletonExceptions.
	 */
	public void setStrict( boolean on )
	{
		strict = on;
	}
	
	/**
	 * Change the number of divisions of the cell map. A value of 1 or less
	 * disable the cell map. This method recreates the map and may be quite
	 * heavy, use it carefully ;-).
	 * @param divisions The new number of divisions of the cell map, a number
	 *        less or equal to 1 disables the cell map.
	 */
	public void setDivisions( int divisions )
	{
		if( divisions != div )
		{
			System.err.printf( "Set divisions to %d%n", divisions );
			
			if( divisions <= 1 )
			{
				div = 1;
				useMap = false;
				unmapNodes();
			}
			else
			{
				div = divisions;
				useMap = true;
				resetBounds();
				unmapNodes();
				initMap();
				remapNodes();
			}
		}
	}
	
	/**
	 * Set the number of threads to use to to the layout.
	 * @param threads The number of threads.
	 */
	public void setThreadCount( int threads )
	{
		if( threads < 1 )
			threads = 1;

		useThreads = threads;
	}

	/**
	 * Automatically create nodes when an edges references non-existing nodes?.
	 * @param on True if nodes are to be created automatically.
	 */
	public void setAutoCreatedNode( boolean on )
	{
		autoNodes = on;
	}

	/**
	 * Add a listener for layout events.
	 */
	public void addListener( LayoutListener listener )
	{
		listeners.add( listener );
	}

	/**
	 * Remove a listener for layout events.
	 */
	public void removeListener( LayoutListener listener )
	{
		int n = listeners.size();

		for( int i = 0; i < n; ++i )
		{
			if( listeners.get( i ) == listener )
			{
				listeners.remove( i );
				return;
			}
		}
	}

	/**
	 * Reset the average node position of each cell in the space map.
	 */
	protected void resetMapAverage()
	{
		if( averageMap )
		{
			if( use3d )
			{
				for( int z = 0; z < div; ++z )
				{
					for( int y = 0; y < div; ++y )
					{
						for( int x = 0; x < div; ++x )
						{
							XYZ[x][y][z].reset();
						}
					}
				}
			}
			else
			{
				for( int y = 0; y < div; ++y )
				{
					for( int x = 0; x < div; ++x )
					{
						XY[x][y].reset();
					}
				}
			}
		}
	}

	/**
	 * Compute the average node position of each cell in the space map.
	 */
	protected void averageMap()
	{
		if( averageMap )
		{
			if( use3d )
			{
				for( int z = 0; z < div; ++z )
				{
					for( int y = 0; y < div; ++y )
					{
						for( int x = 0; x < div; ++x )
						{
							XYZ[x][y][z].average();
						}
					}
				}
			}
			else
			{
				for( int y = 0; y < div; ++y )
				{
					for( int x = 0; x < div; ++x )
					{
						XY[x][y].average();
					}
				}
			}
		}
	}

	/**
	 * Compute and assign the cell of each node.
	 */
	protected void remapNodes()
	{
		// long t1=System.nanoTime();
		resetMapAverage();

		//int n = nodesArray.size();
		// long t2=System.nanoTime();
		
		Iterator<Node> it = nodesArray.iterator();
		while(it.hasNext())
		{
			Node node = it.next();
			unmap( node );
			map( node );
		}
		// long t3=System.nanoTime();
		averageMap();
		// long t4=System.nanoTime();
		// System.err.printf( "reset=%f unmap/map=%f avg=%f%n",
		// (t2-t1)/1000000f,
		// (t3-t2)/1000000f,
		// (t4-t3)/1000000f );
	}

	/**
	 * Remove a node from its cell.
	 */
	protected void unmap( Node node )
	{
		if( node.mapx >= 0 )
		{
			if( use3d )
				XYZ[node.mapx][node.mapy][node.mapz].remove( node.id );
			else
				XY[node.mapx][node.mapy].remove( node.id );
		}
	}

	/**
	 * Add a node to its cell.
	 */
	protected void map( Node node )
	{
		if( node.hiden )
			return;

		
		float px = node.x;
		float py = node.y;
		float pz = node.z;
		float w = hi.x - lo.x;
		float h = hi.y - lo.y;
		float d = hi.z - lo.z;
		int x, y, z;

		if( use3d )
		{
			px = Math.abs( lo.x - px );
			py = Math.abs( lo.y - py );
			pz = Math.abs( lo.z - pz );

			x = (int) ( ( px / w ) * ( div - 1 ) );
			y = (int) ( ( py / h ) * ( div - 1 ) );
			z = (int) ( ( pz / d ) * ( div - 1 ) );

			MapCell cell = XYZ[x][y][z];

			cell.put( node.id, node );

			if( averageMap )
			{
				cell.x += node.x;
				cell.y += node.y;
				cell.z += node.z;
			}

			node.mapx = x;
			node.mapy = y;
			node.mapz = z;
		}
		else
		{
			px = Math.abs( lo.x - px );
			py = Math.abs( lo.y - py );

			x = (int) ( ( px / w ) * ( div - 1 ) );
			y = (int) ( ( py / h ) * ( div - 1 ) );

			
			MapCell cell = XY[x][y];

			cell.put( node.id, node );

			if( averageMap )
			{
				cell.x += node.x;
				cell.y += node.y;
			}

			node.mapx = x;
			node.mapy = y;
		}
	}

	/**
	 * Remove all nodes from the cell map.
	 */
	protected void unmapNodes()
	{
//		int n = nodesArray.size();

		Iterator<Node> it = nodesArray.iterator();
		while(it.hasNext())
		{
			Node node = it.next();
			node.mapx = node.mapy = node.mapz = -1;
		}
	}

// Command

	/**
	 * Switch the sending of 'move' messages. This kind of message is the most
	 * used one. Disabling them for a moment can speed up the computation a lot.
	 * The drawback is that the GUI cannot follow the layout.
	 */
	public void setMoveMessages( boolean on )
	{
		moveMessages = on;
	}

	/**
	 * Switch node gluing. If enabled, nodes stop moving when their computed
	 * movement is too small. This feature can lead the layout algorithm to stop
	 * with a bad layout.
	 */
	public void setGlueNodes( boolean on )
	{
		glueNodes = on;
	}

	/**
	 * Activate or deactive the space map averaging.
	 */
	public void setAverageMap( boolean on )
	{
		averageMap = on;
	}

	/**
	 * Change the view zone of each node. Each node will see zone cells in space
	 * around (this works only if a space map is used, naturally).
	 */
	public void setViewZone( int zone )
	{
		viewZone = zone;

		if( viewZone < 2 )
		{
			viewZone = 2;
			System.err.println( "view zone was < 2: set to 2" );
		}
	}

	/**
	 * Set the displacement force.
	 */
	public void setForce( float force )
	{
		forceStrength = force;
	}

	/**
	 * Add a node id to the graph description.
	 * @param id Identifier of the node.
	 * @throws SingletonException If a node with this id already exists.
	 */
	public Node addNode( String id ) throws SingletonException
	{
		Node n = new Node( id, lo, hi );
		Node old = nodes.put( id, n );

		if( old != null )
		{
			nodes.put( id, old );
			
			if( strict )
				throw new SingletonException( "a node with id '" + id
					+ "' already exists" );
		}
		else
		{
			nodesArray.add( n );
			n.iid = nodesArray.getLastIndex();
		}
		
		return n;
	}

	/**
	 * Remove a node from the graph description.
	 * @param id Identifier of the node.
	 * @throws NotFoundException If no node matches id.
	 */
	public void removeNode( String id ) throws NotFoundException
	{
		Node n = nodes.get( id );

		if( n != null )
		{
			// Remove edges of the node.

			while( n.neighbours.size() > 0 )
				removeEdge( n.neighbours.get( n.neighbours.size() - 1 ).id );

			// Remove the node.

			nodes.remove( id );
			nodesArray.remove( n.iid );
			System.err.println( "removes edges to removed node!!!" );
		}
		else
		{
			if( strict )
				throw new NotFoundException( "cannot remove node '" + id + "'" );
		}
	}

	/**
	 * Add an edge id between node from and node to.
	 * @param id Identifier of the edge.
	 * @param from Identifier of the source node.
	 * @param to Identifier of the destination node.
	 * @param directed True if the edge is directed from the from node to the to
	 *        node.
	 */
	public void addEdge( String id, String from, String to, boolean directed )
			throws NotFoundException, SingletonException
	{
		Node n0 = nodes.get( from );
		Node n1 = nodes.get( to );

		if( autoNodes && n0 == null )
		{
			addNode( from );
			n0 = getNode( from );
		}

		if( autoNodes && n1 == null )
		{
			addNode( to );
			n1 = getNode( to );
		}
		
		if( n0 == null && ! strict )
		{
			addNode( from );
			n0 = nodes.get( from );
		}
		
		if( n1 == null && ! strict )
		{
			addNode( to );
			n1 = nodes.get( to );
		}

		if( n0 != null && n1 != null )
		{
			Edge e = new Edge( id, n0, n1 );
			Edge old = edges.put( id, e );

			if( old != null )
			{
				edges.put( id, old );
				
				if( strict )
					throw new SingletonException( "an edge with id '" + id
						+ "' already exists" );
			}
			else
			{
				edgesArray.add( e );
				e.iid = edgesArray.getLastIndex();

				n0.register( e );
				n1.register( e );
			}
		}
		else
		{
			throw new NotFoundException( "node '" + from + "' or node '" + to
					+ "' not found when adding edge '" + id + "'" );
		}
		
		//positionNodes( n0, n1 );
	}
	
	protected void positionNodes( Node from, Node to )
	{
		int fn = from.neighbours.size();
		int tn = to.neighbours.size();
		
		if( fn == 1 && tn == 1 )
		{
			float vx = to.x - from.x; 
			float vy = to.y - from.y; 
			float vz = to.z - from.z;
			
			from.x += vx * 0.4f;
			from.y += vy * 0.4f;
			from.z += vz * 0.4f;
			
			to.x -= vx * 0.4f;
			to.y -= vy * 0.4f;
			to.z -= vz * 0.4f;
			
			from.anchorx = from.x;
			from.anchory = from.y;
			from.anchorz = from.z;
			to.anchorx = to.x;
			to.anchory = to.y;
			to.anchorz = to.z;
		}
		else if( ( fn == 1 && tn > 1 ) || ( tn == 1 && fn > 1 ) )
		{
			float vx = to.x - from.x;
			float vy = to.y - from.y; 
			float vz = to.z - from.z;

			if( fn == 1 )
			{
				from.x += 0.9f * vx;
				from.y += 0.9f * vy;
				from.z += 0.9f * vz;
				from.anchorx = from.x;
				from.anchory = from.y;
				from.anchorz = from.z;
			}
			else
			{
				to.x -= 0.9f * vx;
				to.y -= 0.9f * vy;
				to.z -= 0.9f * vz;
				to.anchorx = to.x;
				to.anchory = to.y;
				to.anchorz = to.z;
			}
		}
	}
	
	public void addEdgeBreakPoint( String edgeId, int points )
	{
		// TODO: implement this.
		System.err.printf( "edge break points are not implemented yet." );
	}
	
	public void ignoreEdge( String id, boolean on )
	{
		System.err.printf( "ignore-edge is not implemented yet." );
	}

	/**
	 * Remove the edge id.
	 * @param id Identifier of the edge.
	 */
	public void removeEdge( String id ) throws NotFoundException
	{
		Edge e = edges.remove( id );

		if( e != null )
		{
			// TODO update neighbors...
			e.node0.unregister( e );
			e.node1.unregister( e );
			edgesArray.remove( e.iid );
		}
		else
		{
			if( strict )
				throw new NotFoundException( "cannot remove edge '" + id + "'" );
		}
	}

	/**
	 * Freeze or unfreeze a node.
	 * @param id The node to frezze.
	 * @param frozen If true the node is frozen, else unfrozen.
	 */
	public void freezeNode( String id, boolean frozen )
	{
		Node node = nodes.get( id );

		if( node != null )
		{
			node.frozen = frozen;
//System.err.printf( "Node %s %s%n", id, frozen ? "frozen" : " free" );
		}
	}

	/**
	 * Unfreeze all nodes.
	 */
	public void unfreeze()
	{
		for( Node node: nodes.values() )
		{
			node.frozen = false;
		}
	}

	/**
	 * Move a node by a vector. The vector is made of scalar that represent a
	 * portion of the graph display bounds. For example the x component of the
	 * vector represent x% of the graph width.
	 * @param id Node identifier.
	 * @param dx X component of the vector.
	 * @param dy Y component of the vector.
	 * @param dz Z component of the vector.
	 */
	public void moveNode( String id, float dx, float dy, float dz )
	{
		Node node = nodes.get( id );
		
		if( node != null )
		{
/*			node.x += ( dx * ( hi.x - lo.x ) );
			node.y += ( dy * ( hi.y - lo.y ) );
			node.z += ( dz * ( hi.z - lo.z ) );
*/			
			float factor = 0.3f;
			
			node.x += dx * factor;
			node.y += dy * factor;
			node.z += dz * factor;
			
//System.err.printf("force move node %s (->%f %f)  (%f %f)%n", id, dx, dy, node.x, node.y );
			
			if( moveMessages )
				nodeMoved( node.id, node.x, node.y, node.z );
		}
	}
	
	public void setNodeWeight( String id, float weight )
	{
		// TODO
	}
	
	public void setEdgeWeight( String id, float weight )
	{
		// TODO
	}

	/**
	 * Shake the graph. Add a random vector whose length is 10% of the size of
	 * the graph to all node positions.
	 */
	public void shake()
	{
		float w = hi.x - lo.x;
		float h = hi.y - lo.y;
		float d = hi.z - lo.z;

		w *= 0.4;
		h *= 0.4;
		d *= 0.4;

		Iterator<Node> it = nodesArray.iterator();

		while( it.hasNext() )
		{
			Node node = it.next();

			if( node != null )
			{
				node.x += ( 2 * Math.random() - 1 ) * w;
				node.y += ( 2 * Math.random() - 1 ) * h;

				if( use3d )
					node.z += ( 2 * Math.random() - 1 ) * d;

				if( moveMessages )
					nodeMoved( node.id, node.x, node.y, node.z );
			}
		}
	}

	/**
	 * Method to call repeatedly to compute the layout. This method implements
	 * the layout algorithm proper. It must be called in a loop, until the
	 * layout stabilises. You can know if the layout is stable by using the
	 * {@link #getNodeMoved()} method that returns the number of node that have
	 * moved during the last call to step(). When this number of zero (or
	 * sufficiently small) the graph layout stabilised.
	 * 
	 * The listener is called by this method, therefore each call to step() will
	 * also trigger layout events, allowing to reproduce the layout process
	 * graphically for example. You can insert the listener only when the layout
	 * stabilised, and then call step() anew if you do not want to observe the
	 * layout process.
	 */
	public void compute()
	{
		t1 = System.nanoTime();

		nodeMoved = 0;

		stepNodes();
		commitNodes();
		resetBounds();

		if( useMap )
			remapNodes();

		steps++;

		t2 = System.nanoTime();
	}

	/**
	 * Make all the nodes run.
	 */
	protected void stepNodes()
	{
		if( useThreads > 1 && useMap )
		     stepNodesThreads( useThreads );
		else stepNodesSingleThread();
	}

	/**
	 * Make all the nodes run in this thread.
	 */
	protected void stepNodesSingleThread()
	{
		int n = nodesArray.size();
		int i = 0;
		
		Iterator<Node> it = nodesArray.iterator();
		
		while(it.hasNext())
		{
			stepNode( it.next() );
			t2 = System.nanoTime();

			if( t2 - t1 > 1000000000 )
			{
				for( LayoutListener listener: listeners )
					listener.stepCompletion( (float) i / (float) n );
			}
			i++;
		}
	}

	/**
	 * Make all the nodes run in two distinct threads.
	 * @param threadCount Number of threads to use (actually not used, always
	 *        use 2 threads).
	 */
	protected void stepNodesThreads( int threadCount )
	{
		if( threadCount == 4 )
		{
			// x y z w h d
			NodeRunner nr1 = new NodeRunner( "NodeRunner 1", 0, 0, 0, div / 2,
					div / 2, div );
			NodeRunner nr2 = new NodeRunner( "NodeRunner 2", div / 2, 0, 0,
					div / 2, div / 2, div );
			NodeRunner nr3 = new NodeRunner( "NodeRunner 3", 0, div / 2, 0,
					div / 2, div / 2, div );
			NodeRunner nr4 = new NodeRunner( "NodeRunner 4", div / 2, div / 2,
					0, div / 2, div / 2, div );

			nr1.start();
			nr2.start();
			nr3.start();
			nr4.start();

			try
			{
				nr1.join();
				nr2.join();
				nr3.join();
				nr4.join();
			}
			catch( InterruptedException e )
			{
				e.printStackTrace();
			}
		}
		else
		{
			NodeRunner nr1 = new NodeRunner( "NodeRunner 1", 0, 0, 0, div / 2,
					div, div );
			NodeRunner nr2 = new NodeRunner( "NodeRunner 2", div / 2, 0, 0,
					div / 2, div, div );

			nr1.start();
			nr2.start();

			try
			{
				nr1.join();
				nr2.join();
			}
			catch( InterruptedException e )
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Commit all the nodes displacements.
	 */
	protected void commitNodes()
	{
		Iterator<Node> it = nodesArray.iterator();
		
		while(it.hasNext())
		{
			Node node = it.next();
			commitNode( node );
		}
	}

	/**
	 * Make one node run. The displacement is computed and stored, but the node
	 * is not moved. The node movement is done in a later step.
	 */
	protected void stepNode( Node node )
	{
		if( node.frozen )
			return;

		if( node.mapx < 0 && useMap )
			return;

		if( node.hiden )
			return;

		int n;

		n = node.neighbours.size();

		// if( n > 0 )
		{
			node.anchorx = 0;
			node.anchory = 0;
			node.anchorz = 0;

			// Compute attraction forces.

			for( int i = 0; i < n; ++i )
				nodeAttraction( node, node.neighbours.get( i ).getOpposite( node ) );

			// Compute repulsion forces.

			if( ! useMap )
			{
				nodeRepulsionForAll( node );
			}
			else
			{
				if( averageMap )
				     nodeRepulsionInAverageSurroundings( node );
				else nodeRepulsionInSurroundings( node );
			}
		}
	}

	protected void commitNode( Node node )
	{
		if( node.frozen )
		{
			if( moveMessages )
				nodeMoved( node.id, node.x, node.y, node.z );
			
			return;
		}
		
		// Merge forces to compute displacement vector.
		//
		// Apply displacement force only if it moves the node from a
		// meaningful distance. The move is scaled by a factor to avoid
		// oscillations.

		float glueThresold = ( hi.x - lo.x ) / glueNodeAt;
		
		if( use3d )
		{
			if( glueNodes )
			{
				if( ( ( node.anchorx >  glueThresold ) || ( node.anchory >  glueThresold ) || ( node.anchorz >  glueThresold ) )
				 || ( ( node.anchorx < -glueThresold ) || ( node.anchory < -glueThresold ) || ( node.anchorz < -glueThresold ) ) )
				{
				/*
					if( node.anchorx >  60 )		// Why 60 ? Good question !
						node.anchorx =  60;
					if( node.anchorx < -60 )
						node.anchorx = -60;
					if( node.anchory >  60 )
						node.anchory =  60;
					if( node.anchory < -60 )
						node.anchory = -60;
					if( node.anchorz >  60 )
						node.anchorz =  60;
					if( node.anchorz < -60 )
						node.anchorz = -60;
				*/	
					node.x += node.anchorx * forceStrength;
					node.y += node.anchory * forceStrength;
					node.z += node.anchorz * forceStrength;

					nodeMoved++;

					if( moveMessages && ( steps %  sendMoveMessagesEvery == 0 ) )
						nodeMoved( node.id, node.x, node.y, node.z );
				}
			}
			else
			{/*
				if( node.anchorx >  60 )
					node.anchorx =  60;
				if( node.anchorx < -60 )
					node.anchorx = -60;
				if( node.anchory >  60 )
					node.anchory =  60;
				if( node.anchory < -60 )
					node.anchory = -60;
				if( node.anchorz >  60 )
					node.anchorz =  60;
				if( node.anchorz < -60 )
					node.anchorz = -60;
			*/	
				node.x += node.anchorx * forceStrength;
				node.y += node.anchory * forceStrength;
				node.z += node.anchorz * forceStrength;

				nodeMoved++;

				if( moveMessages && ( steps %  sendMoveMessagesEvery == 0 ) )
					nodeMoved( node.id, node.x, node.y, node.z );
			}
		}
		else
		{
			if( glueNodes )
			{
				if( ( ( node.anchorx >  glueThresold ) || ( node.anchory >  glueThresold ) )
				 || ( ( node.anchorx < -glueThresold ) || ( node.anchory < -glueThresold ) ) )
				{/*
					if( node.anchorx > 60 )
						node.anchorx = 60;
					if( node.anchorx < -60 )
						node.anchorx = -60;
					if( node.anchory > 60 )
						node.anchory = 60;
					if( node.anchory < -60 )
						node.anchory = -60;
*/
					node.x += node.anchorx * forceStrength;
					node.y += node.anchory * forceStrength;

					nodeMoved++;

					if( moveMessages && ( steps %  sendMoveMessagesEvery == 0 ) )
						nodeMoved( node.id, node.x, node.y, node.z );
				}
			}
			else
			{/*
				if( node.anchorx > 60 )
					node.anchorx = 60;
				if( node.anchorx < -60 )
					node.anchorx = -60;
				if( node.anchory > 60 )
					node.anchory = 60;
				if( node.anchory < -60 )
					node.anchory = -60;
*/
				node.x += node.anchorx * forceStrength;
				node.y += node.anchory * forceStrength;

				nodeMoved++;

				if( moveMessages && ( steps %  sendMoveMessagesEvery == 0 ) )
					nodeMoved( node.id, node.x, node.y, node.z );
			}
		}
	}

	/**
	 * Compute the node attraction. Should be in the node class!!!!.
	 */
	protected void nodeAttraction( Node node, Node other )
	{
		float vx, vy, vz, d, fa;

		if( other.hiden )
			return;

		if( use3d )
		{
			vx = other.x - node.x;
			vy = other.y - node.y;
			vz = other.z - node.z;
			d = (float) Math.sqrt( ( vx * vx ) + ( vy * vy ) + ( vz * vz ) ) + 0.00001f;
			vx = vx / d;
			vy = vy / d;
			vz = vz / d;
			fa = K1 * ( d - NL );

			node.anchorx += ( fa * vx );
			node.anchory += ( fa * vy );
			node.anchorz += ( fa * vz );
		}
		else
		{
			vx = other.x - node.x;
			vy = other.y - node.y;
			d = (float) Math.sqrt( ( vx * vx ) + ( vy * vy ) ) + 0.00001f;
			vx = vx / d;
			vy = vy / d;
			fa = K1 * ( d - NL );

			node.anchorx += ( fa * vx );
			node.anchory += ( fa * vy );
		}
	}

	/**
	 * Compute the node repulsion. Should be in the node class!!!!.
	 */
	protected void nodeRepulsion( Node node, float x, float y, float z,
			float weight )
	{
		float vx, vy, vz, d, fr;

		if( use3d )
		{
			vx = node.x - x;
			vy = node.y - y;
			vz = node.z - z;
			d = (float) Math.sqrt( ( vx * vx ) + ( vy * vy ) + ( vz * vz ) ) + 0.00001f;
			vx = vx / d;
			vy = vy / d;
			vz = vz / d;
			fr = ( K2 / ( d * d ) ) * weight;

			node.anchorx += ( fr * vx );
			node.anchory += ( fr * vy );
			node.anchorz += ( fr * vz );
		}
		else
		{
			vx = node.x - x;
			vy = node.y - y;
			d = (float) Math.sqrt( ( vx * vx ) + ( vy * vy ) ) + 0.00001f;
			vx = vx / d;
			vy = vy / d;
			fr = ( K2 / ( d * d ) ) * weight;

			node.anchorx += ( fr * vx );
			node.anchory += ( fr * vy );
		}
	}

	/**
	 * Compute the node repulsion only in its local environment.
	 * Should be in the node class!!!!.
	 */
	@SuppressWarnings("unchecked")
	protected void nodeRepulsionInSurroundings( Node node )
	{
		int pw, ph, pd;
		int px = node.mapx;
		int py = node.mapy;
		int pz = node.mapz;

		pw = px + viewZone;
		px -= viewZone;
		ph = py + viewZone;
		py -= viewZone;
		pd = pz + viewZone;
		pz -= viewZone;

		pw = pw >= div ? div - 1 : pw;
		ph = ph >= div ? div - 1 : ph;
		pd = pd >= div ? div - 1 : pd;

		px = px < 0 ? 0 : px;
		py = py < 0 ? 0 : py;
		pz = pz < 0 ? 0 : pz;

		if( use3d )
		{
			for( int z = pz; z < pd; ++z )
			{
				for( int y = py; y < ph; ++y )
				{
					for( int x = px; x < pw; ++x )
					{
						Iterator i = getCell( x, y, z ).values().iterator();

						while( i.hasNext() )
						{
							Node other = (Node) i.next();

							if( ( other.hiden == false ) && ( node != other ) )
								nodeRepulsion( node, other.x, other.y, other.z,
										1 );
						}
					}
				}
			}
		}
		else
		{
			for( int y = py; y < ph; ++y )
			{
				for( int x = px; x < pw; ++x )
				{
					Iterator i = getCell( x, y ).values().iterator();

					while( i.hasNext() )
					{
						Node other = (Node) i.next();

						if( ( other.hiden == false ) && ( node != other ) )
							nodeRepulsion( node, other.x, other.y, other.z, 1 );
					}
				}
			}
		}
	}

	/**
	 * Compute the node repulsion only in its local environment using average
	 * node locations. Should be in the node class!!!!.
	 */
	@SuppressWarnings("unchecked")
	protected void nodeRepulsionInAverageSurroundings( Node node )
	{
		int pw, ph, pd;
		int px = node.mapx;
		int py = node.mapy;
		int pz = node.mapz;

		pw = px + viewZone;
		px -= viewZone;
		ph = py + viewZone;
		py -= viewZone;
		pd = pz + viewZone;
		pz -= viewZone;

		pw = pw >= div ? div - 1 : pw;
		ph = ph >= div ? div - 1 : ph;
		pd = pd >= div ? div - 1 : pd;

		px = px < 0 ? 0 : px;
		py = py < 0 ? 0 : py;
		pz = pz < 0 ? 0 : pz;

		if( use3d )
		{
			for( int z = pz; z < pd; ++z )
			{
				for( int y = py; y < ph; ++y )
				{
					for( int x = px; x < pw; ++x )
					{
						if( x == node.mapx && y == node.mapy && z == node.mapz )
						{
							Iterator i = getCell( x, y, z ).values().iterator();

							while( i.hasNext() )
							{
								Node other = (Node) i.next();

								if( ( other.hiden == false )
										&& ( node != other ) )
									nodeRepulsion( node, other.x, other.y,
											other.z, 1 );
							}
						}
						else
						{
							MapCell cell = getCell( x, y, z );

							nodeRepulsion( node, cell.x, cell.y, cell.z, cell
									.size() );
						}
					}
				}
			}
		}
		else
		{
			for( int y = py; y < ph; ++y )
			{
				for( int x = px; x < pw; ++x )
				{
					if( x == node.mapx && y == node.mapy )
					{
						Iterator i = getCell( x, y ).values().iterator();

						while( i.hasNext() )
						{
							Node other = (Node) i.next();

							if( ( other.hiden == false ) && ( node != other ) )
								nodeRepulsion( node, other.x, other.y, 0, 1 );
						}
					}
					else
					{
						MapCell cell = getCell( x, y );

						nodeRepulsion( node, cell.x, cell.y, 0, cell.size() );
					}
				}
			}
		}
	}

	/**
	 * Is this a slogan?.
	 */
	protected void nodeRepulsionForAll( Node node )
	{
		Iterator<Node> it = nodesArray.iterator();

		while(it.hasNext())
		{
			Node other = it.next();

			if( ( !other.hiden ) && ( node != other ) )
				nodeRepulsion( node, other.x, other.y, other.z, 1 );
		}
	}

	/**
	 * Send a nodeMoved event to all listeners.
	 */
	protected void nodeMoved( String id, float x, float y, float z )
	{
		int n = listeners.size();

		for( int i = 0; i < n; ++i )
		{
			listeners.get( i ).nodeMoved( id, x, y, z );
		}
	}

// Output

	/**
	 * Read a position file.
	 */
	public void inputPos( String filename ) throws java.io.IOException
	{
		if( filename == null )
			throw new IOException( "no filename given" );

		// The scanner is in average five time slower thant the
		// StreamTokenizer, but it reads floats and doubles....

		System.out.println( "Reading positions in '" + filename + "':" );
		long t1 = System.currentTimeMillis();

		Scanner scanner = new Scanner( new BufferedInputStream(
				new FileInputStream( filename ) ) );
		int ignored = 0;
		int mapped = 0;
		int line = 1;
		String id = null;
		float x = 0, y = 0, z = 0;

		scanner.useLocale( Locale.US );
		scanner.useDelimiter( "\\s|\\n|:" );

		try
		{
			while( scanner.hasNext() )
			{
				id = scanner.next();

				x = scanner.nextFloat();
				y = scanner.nextFloat();
				z = scanner.nextFloat();

				line++;
				// System.out.printf( "[%d4]\u001B[K\u001B[200D", line );

				// Configure the node read.

				Node node = nodes.get( id );

				if( node != null )
				{
					node.x = x;
					node.y = y;
					node.z = z;
					mapped++;

					if( moveMessages )
						nodeMoved( node.id, node.x, node.y, node.z );
				}
				else
				{
					ignored++;
				}
			}
		}
		catch( InputMismatchException e )
		{
			e.printStackTrace();
			throw new IOException( "parse error '" + filename + "':" + line
					+ ": " + e.getMessage() );
		}
		catch( NoSuchElementException e )
		{
			throw new IOException( "unexpected end of file '" + filename + "':"
					+ line + ": " + e.getMessage() );
		}
		catch( IllegalStateException e )
		{
			throw new IOException( "scanner error '" + filename + "':" + line
					+ ": " + e.getMessage() );
		}

		scanner.close();
		long t2 = System.currentTimeMillis();

		System.out.println( "    ignored: " + ignored );
		System.out.println( "    mapped:  " + mapped );
		System.out.println( "    time:    " + ( t2 - t1 ) + "ms" );
	}

	/**
	 * Read a position file.
	 */
	public void inputPos2( String filename ) throws java.io.IOException
	{
		if( filename == null )
			throw new IOException( "no filename given" );

		System.out.println( "Reading positions in '" + filename + "':" );
		long t1 = System.currentTimeMillis();

		int ignored = 0;
		int mapped = 0;
		boolean loop = true;

		StreamTokenizer st = new StreamTokenizer( new BufferedReader(
				new FileReader( filename ) ) );

		st.eolIsSignificant( true );
		st.parseNumbers();

		while( loop )
		{
			int tok = st.nextToken();
			float x, y, z;
			String id;

			if( tok == StreamTokenizer.TT_EOF )
				break;

			if( tok != StreamTokenizer.TT_NUMBER
					&& tok != StreamTokenizer.TT_WORD )
				throw new IOException( "parse error: expecting a node name at "
						+ st.lineno() + " got " + tok );

			if( tok == StreamTokenizer.TT_NUMBER )
				id = Integer.toString( (int) st.nval );
			else
				id = st.sval;

			tok = st.nextToken();

			if( tok != StreamTokenizer.TT_WORD && tok != 58 )
				throw new IOException( "parse error: expecting ':' marker at "
						+ st.lineno() + " got " + tok );

			x = readNumber( st );
			y = readNumber( st );
			z = readNumber( st );

			tok = st.nextToken();

			if( tok == StreamTokenizer.TT_EOF )
			{
				loop = false;
			}
			else if( tok == StreamTokenizer.TT_EOL )
			{}
			else
			{
				throw new IOException(
						"parse error: expecting a EOF or EOL at " + st.lineno() );
			}

			// Configure the node read.

			Node node = nodes.get( id );

			if( node != null )
			{
				node.x = x;
				node.y = y;
				node.z = z;
				mapped++;
			}
			else
			{
				ignored++;
			}
		}

		long t2 = System.currentTimeMillis();

		System.out.println( "    ignored: " + ignored );
		System.out.println( "    mapped:  " + mapped );
		System.out.println( "    time:    " + ( t2 - t1 ) + "ms" );
	}

	protected float readNumber( StreamTokenizer st ) throws IOException
	{
		int tok = st.nextToken();

		if( tok != StreamTokenizer.TT_NUMBER )
			throw new IOException( "parse error: expecting a number at "
					+ st.lineno() + " got " + tok );

		float n = (float) st.nval;

		tok = st.nextToken();

		if( tok == 44 )
		{
			tok = st.nextToken();

			if( tok != StreamTokenizer.TT_NUMBER )
				throw new IOException( "parse error: expecting a number at "
						+ st.lineno() + " got " + tok );
		}

		return n;
	}

	/**
	 * Output a position file.
	 */
	public void outputPos( String filename ) throws java.io.IOException
	{
		System.out.println( "Output positions in '" + filename + "':" );

		PrintStream out = new PrintStream( new BufferedOutputStream(
				new FileOutputStream( filename ) ) );

		for( Node node: nodes.values() )
		{
			out.printf( Locale.US, "%s:%f %f %f%n", node.id, node.x, node.y,
					node.z );
		}

		out.flush();
		out.close();

		System.out.println( "Output OK" );
		System.out.flush();
	}
	
	/**
	 * Set the overall quality level. There are five quality levels.
	 * @param qualityLevel The quality level in [0..4].
	 */
	public void setQuality( int qualityLevel )
	{
		if( qualityLevel < 0 ) qualityLevel = 0;
		else if( qualityLevel > 4 ) qualityLevel = 4;
		
		quality = qualityLevel;
		
		switch( qualityLevel )
		{
			case 0:
					setViewZone( 4 );
					setDivisions( 80 );
					setAverageMap( true );
					setGlueNodes( true );
					glueNodeAt = 1000;
				break;
			case 1:
					setViewZone( 5 );
					setDivisions( 10 );
					setAverageMap( true );
					setGlueNodes( true );
					glueNodeAt = 1000;
				break;
			case 2:
					setViewZone( 5 );
					setDivisions( 10 );
					setAverageMap( true );
					setGlueNodes( true );
					glueNodeAt = 10000;
				break;
			case 3:
					setViewZone( 2 );
					setDivisions( 1 );
					setAverageMap( false );
					setGlueNodes( true );
					glueNodeAt = 10000;
				break;
			case 4:
					setViewZone( 5 );
					setDivisions( 1 );
					setAverageMap( false );
					setGlueNodes( true );
					glueNodeAt = 1000000;
				break;
		}
		
//		System.err.printf("QUAL=%d MAP=%b DIV=%d VIEW=%d%n", qualityLevel, useMap, div, viewZone );
	}

// Nested classes

	/**
	 * A node of the spring box.
	 */
	public static class Node
	{
		/**
		 * Node identifier.
		 */
		public String id;

		/**
		 * Index of the node.
		 */
		public int iid;

		/**
		 * Position of the node.
		 */
		public float x, y, z;

		/**
		 * States.
		 */
		public boolean hiden, frozen;

		/**
		 * Cell of the map the node occupies.
		 */
		public int mapx, mapy, mapz;

		/**
		 * Index of the cell.
		 */
		public int index;

		/**
		 * Computed current destination for the node.
		 */
		public float anchorx, anchory, anchorz;

		/**
		 * All node neighbours.
		 */
		public ArrayList<Edge> neighbours = new ArrayList<Edge>();

		public int visibleEdges;

		/**
		 * New node.
		 * @param id The new node identifier.
		 */
		public Node( String id, Point3 lo, Point3 hi )
		{
			this.id = id;

			float w = hi.x - lo.x;
			float h = hi.y - lo.y;
			float d = hi.z - lo.z;

			w = w == 0 ? 1 : w;
			h = h == 0 ? 1 : h;
			d = d == 0 ? 1 : d;

			iid = -1;
			x = lo.x + ( (float) Math.random() ) * w;
			y = lo.y + ( (float) Math.random() ) * h;
			z = lo.z + ( (float) Math.random() ) * d;
			mapx = -1;
		}

		public void register( Edge e )
		{
			neighbours.add( e );
		}

		public void unregister( Edge e )
		{
			int i = neighbours.indexOf( e );
			if( i >= 0 )
				neighbours.remove( i );
		}

		public Collection<Edge> getEdges()
		{
			return neighbours;
		}

		public boolean allNeighboursHiden()
		{
			int n = neighbours.size();

			for( int i = 0; i < n; ++i )
			{
				if( !neighbours.get( i ).getOpposite( this ).hiden )
					return false;
			}

			return true;
		}

		public int visibleEdges( boolean recompute )
		{
			if( hiden )
				return 0;

			if( recompute )
			{
				int n = neighbours.size();
				visibleEdges = 0;

				for( int i = 0; i < n; ++i )
				{
					if( !neighbours.get( i ).getOpposite( this ).hiden )
						visibleEdges++;
				}
			}

			return visibleEdges;
		}

		/**
		 * Compute a new node position at the barycenter of all visible
		 * neighbours. If all neighbours are hidden, get a random position in the
		 * space of the given locator and return false.
		 */
		public boolean barycenter( Locator locator )
		{
			int n = neighbours.size();
			int count = 0;
			float bx = 0, by = 0, bz = 0;

			for( int i = 0; i < n; ++i )
			{
				Node other = neighbours.get( i ).getOpposite( this );

				if( !other.hiden )
				{
					count += 1;
					bx += other.x;
					by += other.y;
					bz += other.z;
				}
			}

			visibleEdges = count;
			//
			if( count <= 0 )
			{
				x = (float) ( Math.random() * locator.getHiPoint().x );
				y = (float) ( Math.random() * locator.getHiPoint().y );
				z = (float) ( Math.random() * locator.getHiPoint().z );

				return false;
			}
			else
			{
				x = bx / (float) count;
				y = by / (float) count;
				z = bz / (float) count;

				return true;
			}
		}
	}

	/**
	 * An edge of the spring box.
	 */
	public static class Edge
	{
		/**
		 * Edge identifier.
		 */
		public String id;

		/**
		 * Index of the edge.
		 */
		public int iid;

		/**
		 * Node at one end of the edge.
		 */
		public Node node0, node1;

		/**
		 * New edge between two given nodes.
		 * @param id The edge identifier.
		 * @param n0 The first node.
		 * @param n1 The second node.
		 */
		public Edge( String id, Node n0, Node n1 )
		{
			this.id = id;

			iid = -1;
			node0 = n0;
			node1 = n1;
		}

		/**
		 * Considering the two nodes of the edge return the one that was not
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
	}

	/**
	 * Node runner.
	 * 
	 * The node runner is a thread that run the nodes of only a sub-grid of the
	 * node map.
	 * 
	 * @author Antoine Dutot
	 * @since 7 janv. 2006
	 */
	class NodeRunner extends Thread
	{
		// Attributes

		/**
		 * Position of the sub-grid in the grid (bottom-left corner).
		 */
		protected int x, y, z;

		/**
		 * Dimension of the sub-grid.
		 */
		protected int w, h, d;

		// protected long lastStep = 0;

		// Constructors

		/**
		 * New node runner running the nodes in the sub-grid at (x,y) with
		 * dimensions (w,h).
		 * @param x Position along X of the sub-grid (left corner).
		 * @param y Position along Y of the sub-grid (bottom corner).
		 * @param z Position along Z of the sub-grid (front corner).
		 * @param w Dimensions of the sub-grid along X.
		 * @param h Dimensions of the sub-grid along Y.
		 * @param d Dimensions of the sub-grid along Z.
		 */
		public NodeRunner( String id, int x, int y, int z, int w, int h, int d )
		{
			super( id );

			this.x = x;
			this.y = y;
			this.z = z;
			this.w = w;
			this.h = h;
			this.d = d;

			// System.err.printf( "NodeRunner[(%d,%d) (%d,%d)]%n", x, y, w, h );
		}

		// Access

		// Command

		/**
		 * Run only the nodes in the sub-map.
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void run()
		{
			// long t1 = System.currentTimeMillis();
			// long t2 = 0;

			if( use3d )
			{
				// float n = w*h*d;
				float c = 0;

				for( int pz = z; pz < z + d; ++pz )
				{
					for( int py = y; py < y + h; ++py )
					{
						for( int px = x; px < x + w; ++px )
						{
							Iterator i = getCell( px, py, pz ).values()
									.iterator();

							while( i.hasNext() )
							{
								stepNode( (Node) i.next() );
							}

							c++;
						}

						t2 = System.currentTimeMillis();

						/*
						 * if( t2-t1 >= 1000 ) { System.out.printf( "Thread(%s):
						 * %d%%%n", getName(), (int)((c/n)*100) ); }
						 */}
				}
			}
			else
			{
				// float n = w*h;
				float c = 0;

				for( int py = y; py < y + h; ++py )
				{
					for( int px = x; px < x + w; ++px )
					{
						Iterator i = getCell( px, py ).values().iterator();

						while( i.hasNext() )
						{
							stepNode( (Node) i.next() );
						}

						c++;
					}

					t2 = System.currentTimeMillis();

					/*
					 * if( t2-t1 >= 1000 ) { System.out.printf( "Thread(%s):
					 * %d%%%n", getName(), (int)((c/n)*100) ); }
					 */}
			}
		}
	}

	public int getQuality()
    {
	    return quality;
    }

	public void setSendNodeInfos( boolean send )
    {
		moveMessages = send;
    }

	public void afterEdgeAdd( Graph graph, org.miv.graphstream.graph.Edge edge )
    {
		addEdge( edge.getId(), edge.getNode0().getId(), edge.getNode1().getId(), edge.isDirected() );
    }

	public void afterNodeAdd( Graph graph, org.miv.graphstream.graph.Node node )
    {
/*		Node n =*/ addNode( node.getId() );
/*		
		// If nodes already have positions, use it.
		
		if( node.hasAttribute( "x" ) && node.hasAttribute( "y" ) )
		{
			n.x = (float) node.getNumber( "x" );
			n.y = (float) node.getNumber( "x" );
			n.anchorx = n.x;
			n.anchory = n.y;
		}
		else if( node.hasAttribute( "xy" ) )
		{
			Object xy[] = (Object[]) node.getAttribute( "xy" );
			
			n.x = ((Number)xy[0]).floatValue();
			n.y = ((Number)xy[1]).floatValue();
			n.anchorx = n.x;
			n.anchory = n.y;
		}
		else if( node.hasAttribute( "xyz" ) )
		{
			Object xyz[] = (Object[]) node.getAttribute( "xyz" );
			
			n.x = ((Number)xyz[0]).floatValue();
			n.y = ((Number)xyz[1]).floatValue();
			n.z = ((Number)xyz[2]).floatValue();
			n.anchorx = n.x;
			n.anchory = n.y;
			n.anchorz = n.z;
		}
 */   }

	public void attributeChanged( Element element, String attribute, Object oldValue,
            Object newValue )
    {
    }

	public void beforeEdgeRemove( Graph graph, org.miv.graphstream.graph.Edge edge )
    {
		removeEdge( edge.getId() );
    }

	public void beforeGraphClear( Graph graph )
    {
		throw new RuntimeException( "Not yet implemented" );
    }

	public void beforeNodeRemove( Graph graph, org.miv.graphstream.graph.Node node )
    {
		removeNode( node.getId() );
    }

	public void stepBegins( Graph graph, double time )
    {
    }
}