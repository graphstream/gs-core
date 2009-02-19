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
package org.miv.graphstream.ui;

import org.miv.graphstream.algorithm.layout2.Layout;
import org.miv.graphstream.algorithm.layout2.LayoutListener;
import org.miv.graphstream.algorithm.layout2.LayoutListenerProxy;
import org.miv.graphstream.algorithm.layout2.LayoutRunner;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.ui.graphicGraph.stylesheet.Style;

/**
 * Interface to viewers that display the graph.
 * 
 * <p>
 * The {@link Graph#display()} method returns an instance of the default implementation of
 * GraphViewer that allows to control the view, and to listen at it. You can also create
 * your own instances of specific graph viewers.
 * </p>
 * 
 * <p>
 * It is often the case that the graph viewer runs in a different thread than the application.
 * So be careful when using the listeners to correctly protect them. In the default UI packages
 * there are severals helpers to encapsulate listeners. 
 * </p>
 * 
 * @author Antoine Dutot
 * @author Yoann Pigné
 */
public interface GraphViewer
{
// Constructors
	
	/**
	 * Open a viewer and initialise it on the given graph.
	 * 
	 * <p>
	 * The graph reference is not kept. A call to this method can only
	 * be done once on the viewer. Often, once open, the viewer runs in
	 * a distinct thread.
	 * </p>
	 * 
	 * <p>The purpose of this method is to offer a constructor, as
	 * GraphViewer objects will most often be created using forname().
	 * </p>
	 * 
	 * @param graph The graph to render.
	 */
	void open( Graph graph );
	
	/**
	 * Open a viewer and initialise it on the given graph.
	 * 
	 * <p>
	 * The graph reference is not kept. A call to this method can only
	 * be done once on the viewer. Often, once open, the viewer runs in
	 * a distinct thread.
	 * </p>
	 * 
	 * <p>
	 * If the autoLayout argument is true a special thread is created to
	 * run the a default layout algorithm (a Fruchterman and Reingold variant).
	 * </p>
	 * 
	 * <p>The purpose of this method is to offer a constructor, as
	 * GraphViewer objects will most often be created using forname().
	 * </p>
	 * 
	 * @param graph The graph to render.
	 * @param autoLayout If true an additional thread is created that will compute the positions
	 * 		of nodes to make the graph the more readable possible.
	 */
	void open( Graph graph, boolean autoLayout );
		
	/**
	 * Open a viewer and initialise it on the given graph.
	 * 
	 * <p>
	 * The graph reference is not kept. A call to this method can only
	 * be done once on the viewer. Often, once open, the viewer runs in
	 * a distinct thread.
	 * </p>
	 * 
	 * <p>
	 * If the layout argument is no null, a special thread is created to
	 * run the a given layout algorithm.
	 * </p>
	 * 
	 * <p>The purpose of this method is to offer a constructor, as
	 * GraphViewer objects will most often be created using forname().
	 * </p>
	 * 
	 * @param graph The graph to render.
	 * @param layout If non null an additional thread is created that will compute the positions
	 * 		of nodes to make the graph the more readable possible using this layout instance.
	 */
	void open( Graph graph, Layout layout );
	
	/**
	 * Close definitively the viewer. If there is a layout thread it is also terminated.
	 */
	void close();
	
// Access
	
	/**
	 * The component that contains the graph rendering. For example when using the Swing backend,
	 * this returns a JComponent (often a JFrame or a  JPanel).
	 * @return A GUI toolkit component containing the graph rendering.
	 */
	Object getComponent();
	
	/**
	 * The layout remote allows to control the layout algorithm that runs in another thread (if any).
	 * @return A layout remote, or null if no layout has been enabled.
	 */
	LayoutRunner.LayoutRemote getLayoutRemote();
	
	/**
	 * Get a way to listen at the layout running in its thread using a proxy. The given listener
	 * is put in the proxy and the proxy is used as listener for the layout. The proxy allows
	 * to skip the thread frontier.
	 * @param listener The layout listener to register.
	 * @return A proxy that allows to cross a thread frontier, this listener is "passive" and its
	 *   checkEvents() method must be called regularly to synchronise it.
	 */
	LayoutListenerProxy getLayoutListenerProxy( LayoutListener listener );
	
	/**
	 * Like the {@link #getLayoutListenerProxy(LayoutListener)} method but the given graph will be
	 * modified automatically by the proxy (positions of the nodes will be updated according to the
	 * layout algorithm). In order to work the checkEvents() method of the proxy must be called
	 * regularly. This method allows to synchronise the proxy with the layout.
	 * @param graph The graph to modify.
	 * @return A proxy that allows to cross a thread frontier, this listener is "passive" and its
	 *   checkEvents() method must be called regularly to synchronise it.
	 */
	LayoutListenerProxy getLayoutListenerProxy( Graph graph );
	
	/**
	 * The viewer remote allows to control the viewer from another thread.
	 * @return A newly created viewer remote.
	 */
	GraphViewerRemote newViewerRemote();
	
	/**
	 * Set of types of screen shot made available to the
	 * {@link GraphViewerRemote#screenShot(String, GraphViewer.ScreenshotType)}
	 * method.  
	 * @return The possible output formats for a screen shots, other types will not be honored
	 *         by the screenShot() method.
	 */
	ScreenshotType[] getScreenShotTypes();
	
// Commands
	
	/**
	 * Set the rendering quality from 0 (low) to 4 (high).
	 * @param value A value between 0 to 5 (not included).
	 */
	void setQuality( int value );
	
	/**
	 * Add a listener for events on nodes and sprites (like a node clicked, or dragged for example).
	 * Be careful : this method is to be used only if you plan to receive node and sprite events
	 * in the same thread as the graph viewer (for example, by default in the Swing thread). To
	 * put a listener on the graph viewer in a different thread, put the listener on the 
	 * GraphViewerRemote (see {@link #newViewerRemote()}). 
	 * @param listener The listener to register.
	 */
	void addGraphViewerListener( GraphViewerListener listener );
	
	/**
	 * Remove a previously registered listener.
	 * @param listener The listener to remove.
	 */
	void removeGraphViewerListener( GraphViewerListener listener );
	
	/**
	 * Set or change the layout algorithm.
	 * @param layoutClassName The new layout algorithm.
	 */
	void setLayout( String layoutClassName );
	
	/**
	 * Remove any layout algorithm.
	 */
	void removeLayout();
	
// Sprites

	/**
	 * Declare a new sprite.
	 * @param id The unique identifier of the sprite.
	 * @return An instance of Sprite.
	 */
	Sprite addSprite( String id );

	/**
	 * The same as {@link #addSprite(String)} but do not create a Sprite instance to represent
	 * the sprite. Use this method if you intend to subclass the Sprite class.
	 * @param id The sprite unique identifier.
	 */
	void addSpriteNoInstance( String id );
	
	/**
	 * Remove a sprite.
	 * @param id The unique identifier of the sprite.
	 */
	void removeSprite( String id );

	/**
	 * Attach a sprite to a node.
	 * 
	 * <p>
	 * The use of this method is discouraged, use the corresponding {@link Sprite#attachToNode(String)}
	 * method of the {@link Sprite} class.
	 * </p>
	 * 
	 * @param id The sprite identifier.
	 * @param nodeId The node identifier.
	 */
	void attachSpriteToNode( String id, String nodeId );

	/**
	 * Attach a sprite to an edge.
	 * 
	 * <p>
	 * The use of this method is discouraged, use the corresponding {@link Sprite#attachToEdge(String)}
	 * method of the {@link Sprite} class.
	 * </p>
	 * 
	 * @param id The sprite id.
	 * @param edgeId The edge id.
	 */
	void attachSpriteToEdge( String id, String edgeId );

	/**
	 * Detach a sprite from the node or edge it was attached to.
	 * 
	 * <p>
	 * The use of this method is discouraged, use the corresponding {@link Sprite#detach()}
	 * method of the {@link Sprite} class.
	 * </p>
	 * 
	 * @param id The sprite identifier.
	 */
	void detachSprite( String id );

	/**
	 * Position a sprite along an edge.
	 * 
	 * <p>
	 * The use of this method is discouraged, use the corresponding {@link Sprite#position(float)}
	 * method of the {@link Sprite} class.
	 * </p>
	 * 
	 * @param id The sprite identifier.
	 * @param percent The percent of the distance between the two nodes of the edge.
	 */
	void positionSprite( String id, float percent );

	/**
	 * Position a sprite in the graph, node or edge space.
	 * 
	 * <p>
	 * The use of this method is discouraged, use the corresponding {@link Sprite#position(float, float, float, org.miv.graphstream.ui.graphicGraph.stylesheet.Style.Units)}
	 * method of the {@link Sprite} class.
	 * </p>
	 * 
	 * @param id The sprite identifier.
	 * @param x First coordinate.
	 * @param y Second coordinate.
	 * @param z Third coordinate.
	 * @param units for measuring length.
	 */
	void positionSprite( String id, float x, float y, float z, Style.Units units );

	/**
	 * Like {@link #positionSprite(String, float, float, float, org.miv.graphstream.ui.graphicGraph.stylesheet.Style.Units)}
	 * but consider the units are unchanged. By default the units are graph units.
	 * 
	 * <p>
	 * The use of this method is discouraged, use the corresponding {@link Sprite#position(float, float, float)}
	 * method of the {@link Sprite} class.
	 * </p>
	 * 
	 * @param id The sprite identifier.
	 * @param x First coordinate.
	 * @param y Second coordinate.
	 * @param z Third coordinate.
	 */
	void positionSprite( String id, float x, float y, float z );
	
	/**
	 * Add or change an attribute on a sprite.
	 * 
	 * <p>
	 * The use of this method is discouraged, use the corresponding {@link Sprite#addAttribute(String, Object...)}
	 * method of the {@link Sprite} class.
	 * </p>
	 * 
	 * @param id The sprite identifier.
	 * @param attribute The attribute name.
	 * @param value The attribute value.
	 */
	void addSpriteAttribute( String id, String attribute, Object value );

	/**
	 * Remove an attribute from a sprite.
	 * 
	 * <p>
	 * The use of this method is discouraged, use the corresponding {@link Sprite#removeAttribute(String)}
	 * method of the {@link Sprite} class.
	 * </p>
	 * 
	 * @param id The sprite identifier.
	 * @param attribute The attribute name.
	 */
	void removeSpriteAttribute( String id, String attribute );
	
// Constants
	
	/**
	 * This enumeration contains the definition of a variety of image format that may
	 * be available for producing screen shots. Implementing classes are not due
	 * to propose any of these services. If any of these services are given, the
	 * list of types can be returned with method getScreenShotTypes().
	 * 
	 */
	public static enum ScreenshotType
	{
		SVG( "Scalable Vector Graphics (*.svg)" ),
		PNG( "Portable Network Graphics (*.png)" ),
		EPS( "Encapsulated PostScript (*.eps)" ),
		PDF( "Portable Document Format (*.pdf)" ),
		BMP( "Bitmap (*.bmp)" ),
		JPG( "Jpeg (*.jpg)" );
		
		/**
		 * The message identifier.
		 */
		public String tag;

		ScreenshotType( String tag )
		{
			this.tag = tag;
		}

		/**
		 * Get the message identifier.
		 * @return The message identifier.
		 */
		public String getTag()
		{
			return tag;
		}
	};
}