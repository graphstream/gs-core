/*
 * This file is part of GraphStream.
 * 
 * GraphStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * GraphStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with GraphStream.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.ui2.old.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.Timer;

import org.graphstream.graph.Graph;
import org.graphstream.ui2.old.GraphViewerBase;
import org.graphstream.ui2.old.graphicGraph.GraphicGraph;
import org.graphstream.ui2.old.layout.Layout;
import org.graphstream.ui2.old.layout.LayoutRunner;

/**
 * A graph viewer using swing.
 */
public class SwingGraphViewer extends GraphViewerBase implements ActionListener
{
// Attributes
	
	/**
	 * Optional window to contain the main pane.
	 */
	public JFrame window;
	
	/**
	 * The panel containing everything.
	 */
	public SwingGraphRenderer renderer;
	
	/**
	 * Draw a frame once every while.
	 */
	public Timer timer;
	
	/**
	 * Time allocated for drawing one frame. The timer will fire an
	 * action event that will trigger a redraw every msPerFrame. By
	 * default the value is 40 milliseconds, which makes 25 frames
	 * per second.
	 */
	public int msPerFrame = 40;
	
// Constructors
	
	/**
	 * Default constructor that force you to call the {@link #open(Graph)},
	 * {@link #open(Graph, boolean)}, {@link #open(Graph, Layout)} or
	 * {@link #open(Graph, Layout, boolean, boolean)} methods.
	 */
	public SwingGraphViewer()
	{
		// Need to call open().
	}
	
	/**
	 * New graph viewer on the given graph. This creates a graph viewer in its own window.
	 * The {@link #open(Graph)} method is called for you.
	 * @param graph The graph to display.
	 */
	public SwingGraphViewer( Graph graph )
	{
		this( graph, false );
	}
	
	/**
	 * Like {@link #SwingGraphViewer(Graph, boolean)} but allows in addition to specify the layout
	 * class.
	 * @param graph The graph to display.
	 * @param layout The layout class.
	 * @param isPanel If true a the renderer is put in a panel, else a window is created for the renderer.
	 */
	public SwingGraphViewer( Graph graph, Layout layout, boolean isPanel )
	{
		this( graph, layout, isPanel, false );
	}
	
	/**
	 * Like {@link #SwingGraphViewer(Graph, boolean)} but allows in addition to specify if a default
	 * layout should be used.
	 * @param graph The graph to display.
	 * @param autoLayout If true, use the default layout, else no layout is used.
	 * @param isPanel If true a the renderer is put in a panel, else a window is created for the renderer.
	 */
	public SwingGraphViewer( Graph graph, boolean autoLayout, boolean isPanel )
	{
		this( graph, autoLayout, isPanel, false );
	}
	
	/**
	 * New graph viewer on the given graph. This creates a graph viewer in a window or panel. If
	 * the graph viewer is in a panel, you can insert it in your Swing GUI using {@link #getComponent()}.
	 * However remember that due to the fact {@link org.graphstream.ui2.old.GraphViewer} does not
	 * impose any GUI toolkit, you must cast the component to the 'java.awt.Component' class. Also
	 * see {@link #getSwingComponent()}.
	 * @param graph The graph to display.
	 * @param isPanel If true a the renderer is put in a panel, else a window is created for the renderer.
	 */
	public SwingGraphViewer( Graph graph, boolean isPanel )
	{
		open( graph, LayoutRunner.newDefaultLayout(), isPanel, false );
	}

	/**
	 * Like {@link #SwingGraphViewer(Graph, Layout, boolean)} but allows in addition to specify if
	 * the graph is manipulated inside the Swing thread.
	 * @param graph The graph to display.
	 * @param layout The layout class.
	 * @param isPanel If true a the renderer is put in a panel, else a window is created for the renderer.
	 * @param direct Is the graph changed, manipulated, in the Swing thread only.
	 */
	public SwingGraphViewer( Graph graph, Layout layout, boolean isPanel, boolean direct )
	{
		open( graph, layout, isPanel, direct );
	}

	/**
	 * Like {@link #SwingGraphViewer(Graph, boolean, boolean)} but allows in addition to specify if
	 * the graph is manipulated inside the Swing thread.
	 * @param graph The graph to display.
	 * @param autoLayout If true, use the default layout, else no layout is used.
	 * @param isPanel If true a the renderer is put in a panel, else a window is created for the renderer.
	 * @param direct Is the graph changed, manipulated, in the Swing thread only.
	 */
	public SwingGraphViewer( Graph graph, boolean autoLayout, boolean isPanel, boolean direct )
	{
		open( graph, autoLayout, isPanel, direct );
	}
		
	@Override
    public void open( Graph graph )
    {
		open( graph, true );
    }

	@Override
    public void open( Graph graph, boolean autoLayout )
    {
		open( graph, autoLayout ? LayoutRunner.newDefaultLayout() : null );
    }

	@Override
    public void open( Graph graph, Layout layout )
    {
		open( graph, layout, false, false );
    }
	
	public void open( Graph graph, Layout layout, boolean isPanel, boolean direct )
	{
		init( graph, direct );
		
		if( layout != null )
			setLayout( layout );
		
		if( ! isPanel )
			window = new JFrame( "GraphStream viewer" );
		
		initPane();
    }
	
	public void open( Graph graph, boolean autoLayout, boolean isPanel, boolean direct )
	{
		open( graph, autoLayout ? LayoutRunner.newDefaultLayout() : null, isPanel, direct );
	}

	@Override
    public void close()
    {
    	super.close();
		timer.stop();
		
		if( window != null )
			window.setVisible( false );
    }
    
	@Override
    public void setQuality( int quality )
    {
    	renderer.setQuality( quality );
    }
	
	@Override
	public void setStepsVisible( boolean on )
	{
		renderer.setStepsVisible( on );
	}

	protected void initPane()
	{
		renderer = new SwingGraphRenderer( this );
		timer    = new Timer( msPerFrame, this );
		
		if( window != null )
			window.addKeyListener( renderer );
		
		timer.setCoalesce( true );
		timer.setDelay( msPerFrame );
		timer.setRepeats( true );
		timer.start();
		
		if( window != null )
		{
			//ImageIcon icon = createImageIcon( "GraphStreamSmallLogo24.png", "" );

			ArrayList<Image> imageList = new ArrayList<Image>();
			
			imageList.add( createImageIcon( "GraphStreamSmallLogo24.png", "" ).getImage() );
			imageList.add( createImageIcon( "GSLogo11a32.png", "" ).getImage() );
			imageList.add( createImageIcon( "GSLogo11a64.png", "" ).getImage() );
			imageList.add( createImageIcon( "GSLogo11a128.png", "" ).getImage() );
			
			window.setIconImages( imageList );
			
			window.add( renderer );
			window.setSize( 300, 300 );
			window.setMinimumSize( new Dimension( 200, 200 ) );
			window.setVisible( true );
			window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
//			window.setIconImage( icon.getImage() );
		}
	}
	
// Access
	
	public Object getComponent()
	{
		if( window != null )
			return window;
		
		return renderer;
	}
	
	/**
	 * Like the {@link #getComponent()} method inherited from {@link org.graphstream.ui2.old.GraphViewer},
	 * but returns a swing component. This allows to easily put your renderer into your own GUIs.
	 * @return The Swing component containing the renderer.
	 */
	public Component getSwingComponent()
	{
		if( window != null )
			return window;
	
		return renderer;
	}
	
	public SwingGraphRenderer getRenderer()
	{
		return renderer;
	}
	
	public GraphicGraph getGraph()
	{
		return graph;
	}

// Commands
	
	public void actionPerformed( ActionEvent e )
	{
		if( e.getSource() == timer )
		{
			step();
			renderer.setShowLayoutCompletion( layoutProxy );
			renderer.display();
		}
	}

	public ScreenshotType[] getScreenShotTypes()
    {
	    return renderer.getScreenshotTypes();
    }

	@Override
	public void screenShot( String fileName, ScreenshotType type )
    {
		switch( type )
		{
			case BMP:
				renderer.screenshot( fileName+".bmp" );
				break;
			case EPS:
				renderer.screenshot( fileName+".eps" );
				break;
			case JPG:
				renderer.screenshot( fileName+".jpg" );
				break;
			case PDF:
				renderer.screenshot( fileName+".pdf" );
				break;
			case PNG:
				renderer.screenshot( fileName+".png" );
				break;
			case SVG:
				renderer.screenshot( fileName+".svg" );
				break;
		}
    }

	protected ImageIcon createImageIcon( String path, String description )
	{
	    java.net.URL imgURL = getClass().getResource( path );
	    
	    if (imgURL != null)
	    {
	        return new ImageIcon( imgURL, description );
	    }
	    else
	    {
	    	return new ImageIcon( path, description );
	    }
	}
}