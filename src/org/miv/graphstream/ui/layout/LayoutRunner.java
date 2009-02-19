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

package org.miv.graphstream.algorithm.layout2;

import java.io.IOException;
import java.util.ArrayList;

import org.miv.mbox.*;
import org.miv.graphstream.graph.*;
import org.miv.graphstream.graph.implementations.GraphListenerProxyThread;

/**
 * Shell that runs a layout algorithm in a thread.
 * 
 * <p>
 * The layout runner is an utility class whose purpose is to run a layout algorithm in
 * its own thread.</p>
 * 
 * <p>
 * As the layout now runs in its own thread, you cannot directly put a listener on it.
 * To listen at the layout, use the {@link #getListenerProxy(LayoutListener)}. You pass
 * the listener that you would register in the layout to this method and it creates a
 * "proxy" that will send events from the layout to this listener. The proxy runs both
 * in the thread of the layout runner and in your thread and does all the work of crossing
 * the thread boundary. However to receive events you will have to refresh regularly
 * the proxy so that it fetches events from the layout runner thread.
 * </p>
 * 
 * <p>
 * To modify directly a graph (without layout listener) according to the layout running in a
 * distinct thread, you can also use {@link #getListenerProxy(Graph)} method. This creates
 * a proxy that will modify the graph each time you refresh it. 
 * </p>
 * 
 * <p>
 * The layout is still a listener on the graph, but a {@link org.miv.graphstream.graph.GraphListenerProxy}
 * is used. You have to pass a graph reference at construction, but this reference is used only
 * to build the graph listener proxy and is then never used (it is therefore safe to call
 * the constructor of this class in the thread of the graph). The proxy does the work of
 * crossing the thread boundary to copy all events occurring in the graph to the layout that listen
 * at the graph.
 * </p>
 * 
 * @author Antoine Dutot
 * @author Yoann Pigné
 * @since 20061208
 */
public class LayoutRunner extends Thread implements MBoxListener
{
// Attributes
	
	/**
	 * Mail box for receiving incoming events, commands and settings (mainly from the remote).
	 */
	protected MBoxStandalone inbox;
	
	/**
	 * The layout algorithm.
	 */
	protected Layout layout;
	
	/**
	 * Proxy to listen at the graph.
	 */
	protected GraphListenerProxy proxy;
	
	/**
	 * While true, this thread runs.
	 */
	protected boolean loop;
	
	/**
	 * Is the layout paused ?.
	 */
	protected boolean pause;
	
	/**
	 * Pause in milliseconds between each step. Allows to yield the processor
	 * to other tasks and not eat all the CPU cycles. 
	 */
	protected int pauseMs = 1;
	
	/**
	 * If true, the process is not slowed down when the layout starts to stabilise.
	 */
	protected boolean noSlowDown = false;

	/**
	 * List of remote command objects.
	 */
	protected ArrayList<LayoutRemote> activeRemotes = new ArrayList<LayoutRemote>();
	
// Constructors
	
	public LayoutRunner( Graph inputGraph, Layout layout )
	{
		this.inbox  = new MBoxStandalone( this );
		this.layout = layout;
		this.proxy  = new GraphListenerProxyThread( inputGraph );

		proxy.addGraphListener( layout );
	}
	
// Access
	
	/**
	 * The underlying layout (be careful! it runs in this runner thread).
	 * @return The layout algorithm.
	 */
	public Layout getLayout()
	{
		return layout;
	}
	
	/**
	 * Instantiate a new layout using the default layout class.
	 * @return A layout.
	 */
	public static Layout newDefaultLayout()
	{
//		return new org.miv.graphstream.algorithm.layout2.elasticbox.ElasticBox();
		return new org.miv.graphstream.algorithm.layout2.springbox.SpringBox();
	}

	/**
	 * A listener proxy on the layout. The proxy allows to cross a thread boundary.
	 * The proxy listens at the layout algorithm which runs in one thread and copy
	 * back the events to the given listener that runs in another thread.
	 * @param listener The real listener to register in the proxy.
	 * @return The proxy.
	 */
	public LayoutListenerProxy getListenerProxy( LayoutListener listener )
	{
		LayoutListenerProxy proxy = new LayoutListenerProxy( listener );
		layout.addListener( proxy );
		return proxy;
	}
	
	/**
	 * A listener proxy on the layout. The proxy allows to cross a thread boundary.
	 * The proxy listens at the layout algorithm which runs in one thread and copy
	 * back the events to the given graph that runs in another thread.
	 * @param graph The graph to modify according to layout events.
	 * @return The proxy.
	 */
	public LayoutListenerProxy getListenerProxy( Graph graph )
	{
		LayoutListenerProxy proxy = new LayoutListenerProxy( graph );
		layout.addListener( proxy );
		return proxy;		
	}
	
	/**
	 * Create a new remote for accessing this renderer from another thread.
	 * @return A newly created remote.
	 */
	public LayoutRemote newRemote()
	{
		LayoutRemote remote = new LayoutRemote( inbox );
		
		activeRemotes.add( remote );
		
		return remote;
	}
	
// Commands

	/**
	 * The main loop. Process messages, then run one step of the layout
	 * algorithm, in a loop.
	 */
	@Override
	public void run()
	{
		setName( "GraphStream layout thread" );
		
		loop = true;
		
		while( loop )
		{
			inbox.processMessages();

			if( ! pause )
			{
				proxy.checkEvents();
				layout.compute();
				informRemotes();
				Thread.yield();

				double stable = layout.getStabilization();
				
				if( noSlowDown )
				{
					sleep( pauseMs );				
				}
				else
				{
					if( stable <= 0f )
					{
						sleep( 500 );
	//					System.err.printf( "Layout STABLE%n" );
					}
					else if( stable <= 0.08f )
					{
						sleep( 160 );		// 6 Hz
	//					System.err.printf( "Layout ALMOST-STABLE %f%n", stable );
					}
					else if( stable <= 0.1f )
					{
						sleep( 80 );		// 12 Hz
	//					System.err.printf( "Layout ALMOST-STABLE %f%n", stable );					
					}
					else
					{
						sleep( pauseMs );
	//					System.err.printf( "Layout %f%n", stable );
					}
				}
			}
			else
			{
				sleep( 100 );
			}
		}
		
		proxy.unregisterFromGraph();
	}
	
	protected void informRemotes()
	{
		for( LayoutRemote remote: activeRemotes )
		{
			remote.inbox.post( "Layout", "FORCE", layout.getForce() );
			remote.inbox.post( "Layout", "QLTY", layout.getQuality() );
		}
	}
	
	/**
	 * Sleep during the given number of milliseconds.
	 * @param ms The number of milliseconds to sleep.
	 */
	protected void sleep( int ms )
	{
		try
		{
			Thread.sleep( ms );
		}
		catch( InterruptedException e )
		{
		}
	}
	
	/**
	 * Set the number of milliseconds to wait between each step.
	 * @param ms The time to wait in milliseconds.
	 */
	public void setPauseTime( int ms )
	{
		if( ms >= 0 )
			pauseMs = ms;
	}

	/**
	 * Process the messages from remote commands. Does nothing when receiving
	 * a non valid message.
	 * @param from The remote command name.
	 * @param data The message contents.
	 */
	public void processMessage( String from, Object[] data )
	{
		if( data.length > 0 )
		{
			if( data[0] instanceof String )
			{
				if( data[0].equals( "FORCE" ) )
				{
					if( data.length >= 2 && data[1] instanceof Number )
					{
						Number n = (Number) data[1];
						layout.setForce( n.floatValue() );
					}
				}
				else if( data[0].equals( "QUALITY" ) )
				{
					if( data.length >= 2 && data[1] instanceof Number )
					{
						Number v = (Number) data[1];
						int    n = v.intValue(); 
						
						layout.setQuality( n );
						
						if( n < 0 ) n = 0;
						if( n > 4 ) n = 4;
						
						pauseMs = 5-n;
					}
				}
				else if( data[0].equals( "PAUSE" ) )
				{
					pause = true;
				}
				else if( data[0].equals( "PLAY" ) )
				{
					pause = false;
				}
				else if( data[0].equals( "STOP" ) )
				{
					loop = false;
				}
				else if( data[0].equals( "SHAKE" ) )
				{
					layout.shake();
				}
				else if( data[0].equals( "SAVE_POS" ) )
				{
					if( data.length >= 2 && data[1] instanceof String )
					{
						try
						{
							layout.outputPos( (String) data[1] );
						}
						catch( IOException e )
						{
							e.printStackTrace();
						}
					}
				}
				else if( data[0].equals( "FMN" ) )
				{
					if( data.length == 5 && data[1] instanceof String
					&&  data[2] instanceof Number && data[3] instanceof Number
					&&  data[4] instanceof Number )
					{
						String nodeId = (String) data[1];
						float  x      = ((Number)data[2]).floatValue();
						float  y      = ((Number)data[3]).floatValue();
						float  z      = ((Number)data[4]).floatValue();
						
						layout.moveNode( nodeId, x, y, z );
					}
				}
				else if( data[0].equals( "FREEZEN" ) )
				{
					if( data.length == 3 && data[1] instanceof String && data[2] instanceof Boolean )
					{
						layout.freezeNode( (String)data[1], ((Boolean)data[2]).booleanValue() );
					}
				}
				else if( data[0].equals( "PRIORITY" ) )
				{
					if( data.length == 2 && data[1] instanceof Integer )
					{
						int ms = (Integer) data[1];
						
						if( ms >= 0 )
						{
							setPauseTime( ms );
						}
					}
				}
				else if( data[0].equals( "NOSLOWDOWN" ) )
				{
					if( data.length == 2 && data[1] instanceof Boolean )
					{
						noSlowDown = (Boolean)data[1];
					}
				}
				else
				{
					// What to do ?
					System.err.printf( "LayoutRunner: uncaught message from %s: %s[%d]%n", from, data[0], data.length );
				}
			}
			else
			{
				// What to do ?
				System.err.printf( "LayoutRunner: uncaught message from %s: [%d]%n", from, data.length );
				for( int i=0; i<data.length; ++i )
					System.err.printf( "    %s%n", data[i].getClass().getName() );
			}
		}
		else
		{
			// What to do ?
			System.err.printf( "LayoutRunner: uncaught message from %s: empty%n", from );
		}
	}
	
// Nested classes
	
	/**
	 * Remote command class that allows to send messages to a layout runner in another thread
	 * safely.
	 * 
	 * @author Antoine Dutot
	 */
	public static class LayoutRemote implements MBoxListener
	{
	// Attributes
		
		/**
		 * The message box of the layout runner.
		 */
		protected MBoxStandalone outbox;
	
		/**
		 * The input message box of this remote.
		 */
		protected MBoxStandalone inbox;
		
		/**
		 * The quality setting of the layout.
		 */
		protected int quality;
		
		/**
		 * The force setting of the layout.
		 */
		protected float force;
		
	// Constructors
		
		/**
		 * New remote command sending messages to the given message box.
		 * @param towardTheLayoutRunner The message box where to send messages.
		 */
		protected LayoutRemote( MBoxStandalone towardTheLayoutRunner )
		{
			outbox = towardTheLayoutRunner;
			inbox  = new MBoxStandalone( this );
		}
	
	// Access
		
		/**
		 * The current quality setting of the layout. For this method to report values, you
		 * must call {@link #pumpEvents()}.
		 * @return An integer between 0 and 4.
		 */
		public int getQuality()
		{
			return quality;
		}
		
		/**
		 * The current force setting of the layout. For this method to report values, you
		 * must call {@link #pumpEvents()}.
		 * @return A float value.
		 */
		public float getForce()
		{
			return force;
		}
		
	// Commands
		
		/**
		 * Stop the layout process.
		 */
		public void stop()
		{
			outbox.post( "llr", "STOP" );
		}
		
		/**
		 * Run the layout process.
		 */
		public void play()
		{
			outbox.post( "llr", "PLAY" );
		}
		
		/**
		 * Pause the layout process.
		 */
		public void pause()
		{
			outbox.post( "llr", "PAUSE" );
		}
		
		/**
		 * Send a "shake" message to the layout. The shake message asks the layout
		 * to do "anything" to escape a bad layout situation.
		 */
		public void shake()
		{
			outbox.post( "llr", "SHAKE" );
		}
		
		/**
		 * Change the global layout speed. 
		 * TODO: this should not be here, only few layout algorithms use "force".
		 * @param force The global force.
		 */
		public void setForce( float force )
		{
			outbox.post( "llr", "FORCE", force );
		}
		
		/**
		 * Set the overall quality of the layout, an integer between 0 and 4.
		 * @param quality An integer between 0 and 4, 0 is lower quality.
		 */
		public void setQuality( int quality )
		{
			outbox.post(  "llr", "QUALITY", quality );
		}

		/**
		 * Change the layout process priority.
		 * @param ms The number of milliseconds to wait between each step of the layout.
		 */
		public void setPriority( int ms )
		{
			outbox.post( "llr", "PRIORITY", ms );
		}
		
		/**
		 * If true, the layout process will not slow down when the layout stabilises.
		 * @param on If false, the process will slow down when the layout stabilises.
		 */
		public void setNoSlowDown( boolean on )
		{
			outbox.post( "llr", "NOSLOWDOWN", on );
		}
		
		/**
		 * Ask the layout to save the node positions in a file.
		 * @param fileName The file name.
		 */
		public void savePositions( String fileName )
		{
			outbox.post( "llr", "SAVEPOS", fileName );
		}
		
		/**
		 * Move a node by force to a given position.
		 * @param id The node identifier.
		 * @param x The new node X.
		 * @param y The new node Y.
		 * @param z The new node Z.
		 */
		public void forceMoveNode( String id, float x, float y, float z )
		{
			outbox.post( "llr", "FMN", id, x, y, z );
		}
		
		/**
		 * Freeze or un-freeze a node. When frozen, a node is not moved by the layout process.
		 * @param id The node identifier.
		 * @param freezed If true freeze the node, else un-freeze it. 
		 */
		public void freezeNode( String id, boolean freezed )
		{
			outbox.post( "llr", "FREEZEN", id, freezed );
		}
		
		/**
		 * Check if there are incoming events from the layout process. This method must be called
		 * regularly to check if the layout sent some informations.
		 */
		public void pumpEvents()
		{
			inbox.processMessages();
		}

		public void processMessage( String from, Object[] data )
        {
			if( data.length == 2 && data[0].equals( "QLTY" ) && data[1] instanceof Number )
			{
				quality = ((Number)data[1]).intValue();
			}
			else if( data.length == 2 && data[0].equals( "FORCE" ) && data[1] instanceof Number )
			{
				force = ((Number)data[1]).floatValue();
			}
        }
	}
}