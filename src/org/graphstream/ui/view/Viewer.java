package org.graphstream.ui.view;

import org.graphstream.graph.Graph;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.stream.Source;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.layout.Layout;

public interface Viewer {
	
	/**
	 * What to do when a view frame is closed.
	 */
	public static enum CloseFramePolicy {
		CLOSE_VIEWER, HIDE_ONLY, EXIT
	};
	
	/**
	 * Create a new unique identifier for a graph.
	 * 
	 * @return The new identifier.
	 */
	String newGGId() ;
	/**
	 * Initialise the viewer.
	 * 
	 * @param graph
	 *            The graphic graph.
	 * @param ppipe
	 *            The source of events from another thread or machine (null if
	 *            source != null).
	 * @param source
	 *            The source of events from this thread (null if ppipe != null).
	 */
	void init(GraphicGraph graph, ProxyPipe ppipe, Source source) ;
	/**
	 * Close definitively this viewer and all its views.
	 */
	public void close() ;
	// Access

	

	/**
	 * What to do when a frame is closed.
	 */
	public CloseFramePolicy getCloseFramePolicy() ;
	/**
	 * New proxy pipe on events coming from the viewer through a thread.
	 * 
	 * @return The new proxy pipe.
	 */
	public ProxyPipe newThreadProxyOnGraphicGraph() ;
	

	/**
	 * New viewer pipe on the events coming from the viewer through a thread.
	 * 
	 * @return The new viewer pipe.
	 */
	public ViewerPipe newViewerPipe() ;
	/**
	 * The underlying graphic graph. Caution : Use the returned graph only in
	 * the Swing thread !!
	 */
	public GraphicGraph getGraphicGraph() ;
	
	 /* The view that correspond to the given identifier.
	 * 
	 * @param id
	 *            The view identifier.
	 * @return A view or null if not found.
	 */
	public View getView(String id);
	
	 /* The default view. This is a shortcut to a call to
	 * {@link #getView(String)} with {@link #DEFAULT_VIEW_ID} as parameter.
	 * 
	 * @return The default view or null if no default view has been installed.
	 */
	public View getDefaultView() ;
	
	// Command

	/**
	 * Build the default graph view and insert it. The view identifier is
	 * {@link #DEFAULT_VIEW_ID}. You can request the view to be open in its own
	 * frame.
	 * 
	 * @param openInAFrame
	 *            It true, the view is placed in a frame, else the view is only
	 *            created and you must embed it yourself in your application.
	 */
	public View addDefaultView(boolean openInAFrame);
	/**
	 * Add a view using its identifier. If there was already a view with this
	 * identifier, it is closed and returned (if different of the one added).
	 * 
	 * @param view
	 *            The view to add.
	 * @return The old view that was at the given identifier, if any, else null.
	 */
	public View addView(View view) ;
	/**
	 * Add a new default view with a specific renderer. If a view with the same
	 * id exists, it is removed and closed. By default the view is open in a
	 * frame.
	 * 
	 * @param id
	 *            The new view identifier.
	 * @param renderer
	 *            The renderer to use.
	 * @return The created view.
	 */
	public View addView(String id, GraphRenderer<?, ?> renderer);

	/**
	 * Same as {@link #addView(String, GraphRenderer)} but allows to specify
	 * that the view uses a frame or not.
	 * 
	 * @param id
	 *            The new view identifier.
	 * @param renderer
	 *            The renderer to use.
	 * @param openInAFrame
	 *            If true the view is open in a frame, else the returned view is
	 *            a JPanel that can be inserted in a GUI.
	 * @return The created view.
	 */
	public View addView(String id, GraphRenderer<?, ?> renderer, boolean openInAFrame);

	/**
	 * Remove a view. The view is not closed.
	 * 
	 * @param id
	 *            The view identifier.
	 */
	public void removeView(String id) ;

	
	/**
	 * Compute the overall bounds of the graphic graph according to the nodes
	 * and sprites positions. We can only compute the graph bounds from the
	 * nodes and sprites centres since the node and graph bounds may in certain
	 * circumstances be computed according to the graph bounds. The bounds are
	 * stored in the graph metrics.
	 */
	void computeGraphMetrics() ;

	/**
	 * What to do when the frame containing one or more views is closed.
	 * 
	 * @param policy
	 *            The close frame policy.
	 */
	public void setCloseFramePolicy(CloseFramePolicy policy);

	// Optional layout algorithm

	/**
	 * Enable or disable the "xyz" attribute change when a node is moved in the
	 * views. By default the "xyz" attribute is changed.
	 * 
	 * By default, each time a node of the graphic graph is moved, its "xyz"
	 * attribute is reset to follow the node position. This is useful only if
	 * someone listen at the graphic graph or use the graphic graph directly.
	 * But this operation is quite costly. Therefore by default if this viewer
	 * runs in its own thread, and the main graph is in another thread, xyz
	 * attribute change will be disabled until a listener is added.
	 * 
	 * When the viewer is created to be used only in the swing thread, this
	 * feature is always on.
	 */
	public void enableXYZfeedback(boolean on) ;
	/**
	 * Launch an automatic layout process that will position nodes in the
	 * background.
	 */
	public void enableAutoLayout() ;

	/**
	 * Launch an automatic layout process that will position nodes in the
	 * background.
	 * 
	 * @param layoutAlgorithm
	 *            The algorithm to use (see Layouts.newLayoutAlgorithm() for the
	 *            default algorithm).
	 */
	public void enableAutoLayout(Layout layoutAlgorithm);
	/**
	 * Disable the running automatic layout process, if any.
	 */
	public void disableAutoLayout() ;
	/** Dirty replay of the graph. */
	void replayGraph(Graph graph) ;

}
