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
 * @since 2009-07-26
 * 
 * @author Alex Bowen <bowen.a@gmail.com>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author lucaslugao <lucaslugaoguimaraes@gmail.com>
 * @author kitskub <kitskub@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.ui.view;

import java.util.Map;
import java.util.TreeMap;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.stream.Source;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.LayoutRunner;
import org.graphstream.ui.layout.Layouts;
import org.graphstream.ui.view.camera.Camera;

/**
 * Set of views on a graphic graph.
 * 
 * <p>
 * The viewer class is in charge of maintaining :
 * <ul>
 * <li>A "graphic graph" (a special graph that internally stores the graph under
 * the form of style sets of "graphic" elements, suitable to draw the graph, but
 * not to adapted to used it as a general graph),</li>
 * <li>The eventual proxy pipe from which the events come from (but graph events
 * can come from any kind of source),</li>
 * <li>A default view, and eventually more views on the graphic graph.</li>
 * <li>A flag that allows to repaint the view only if the graphic graph changed.
 * <li>
 * </ul>
 * </p>
 * 
 * <p>
 * The graphic graph can be created by the viewer or given at construction (to
 * share it with another viewer).
 * </p>
 * 
 * <p>
 * <u>Once created, the viewer runs in a loop inside the UI thread. You cannot
 * call methods on it directly if you are not in this thread</u>. The only
 * operation that you can use in other threads is the constructor, the
 * {@link #addView(View)}, {@link #removeView(String)} and the {@link #close()}
 * methods. Other methods are not protected from concurrent accesses.
 * </p>
 * 
 * <p>
 * Some constructors allow a {@link ProxyPipe} as argument. If given, the
 * graphic graph is made listener of this pipe and the pipe is "pumped" during
 * the view loop. This allows to run algorithms on a graph in the main thread
 * (or any other thread) while letting the viewer run in the ui thread.
 * </p>
 * 
 * <p>
 * Be very careful: due to the nature of graph events in GraphStream, the viewer
 * is not aware of events that occured on the graph <u>before</u> its creation.
 * There is a special mechanism that replay the graph if you use a proxy pipe or
 * if you pass the graph directly. However, when you create the viewer by
 * yourself and only pass a {@link Source}, the viewer <u>will not</u> display
 * the events that occured on the source before it is connected to it.
 * </p>
 */
public abstract class Viewer {

	// Attributes

	/**
	 * How does the viewer synchronise its internal graphic graph with the graph
	 * displayed. The graph we display can be in the Swing thread (as will be the
	 * viewer, therefore in the same thread as the viewer), in another thread, or on
	 * a distant machine.
	 */
	public enum ThreadingModel {
		GRAPH_IN_GUI_THREAD, GRAPH_IN_ANOTHER_THREAD, GRAPH_ON_NETWORK
	};

	/**
	 * Name of the default view.
	 */
	public abstract String getDefaultID();

	// Attribute

	/**
	 * If true the graph we display is in another thread, the synchronisation
	 * between the graph and the graphic graph must therefore use thread proxies.
	 */
	protected boolean graphInAnotherThread = true;

	/**
	 * The graph observed by the views.
	 */
	protected GraphicGraph graph;

	/**
	 * If we have to pump events by ourself.
	 */
	protected ProxyPipe pumpPipe;

	/**
	 * If we take graph events from a source in this thread.
	 */
	protected Source sourceInSameThread;

	/**
	 * The set of views.
	 */
	protected final Map<String, View> views = new TreeMap<String, View>();

	/**
	 * What to do when a view frame is closed.
	 */
	protected CloseFramePolicy closeFramePolicy = CloseFramePolicy.EXIT;

	// Attribute

	/**
	 * Optional layout algorithm running in another thread.
	 */
	protected LayoutRunner optLayout = null;

	/**
	 * If there is a layout in another thread, this is the pipe coming from it.
	 */
	protected ProxyPipe layoutPipeIn = null;

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
	public String newGGId() {
		return String.format("GraphicGraph_%d", (int) (Math.random() * 10000));
	}

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
	public abstract void init(GraphicGraph graph, ProxyPipe ppipe, Source source);

	/**
	 * Close definitively this viewer and all its views.
	 */
	public abstract void close();
	// Access

	/**
	 * What to do when a frame is closed.
	 */
	public CloseFramePolicy getCloseFramePolicy() {
		return closeFramePolicy;
	}

	/**
	 * New proxy pipe on events coming from the viewer through a thread.
	 * 
	 * @return The new proxy pipe.
	 */
	public ProxyPipe newThreadProxyOnGraphicGraph() {
		ThreadProxyPipe tpp = new ThreadProxyPipe();
		tpp.init(graph);
		return tpp;
	}

	/**
	 * New viewer pipe on the events coming from the viewer through a thread.
	 * 
	 * @return The new viewer pipe.
	 */
	public ViewerPipe newViewerPipe() {
		ThreadProxyPipe tpp = new ThreadProxyPipe();
		tpp.init(graph, false);

		enableXYZfeedback(true);

		return new ViewerPipe(String.format("viewer_%d", (int) (Math.random() * 10000)), tpp);
	}

	/**
	 * The underlying graphic graph. Caution : Use the returned graph only in the UI
	 * thread !!
	 */
	public GraphicGraph getGraphicGraph() {
		return graph;
	}

	/**
	 * The view that correspond to the given identifier.
	 * 
	 * @param id
	 *            The view identifier.
	 * @return A view or null if not found.
	 */
	public View getView(String id) {
		synchronized (views) {
			return views.get(id);
		}
	}

	/**
	 * The default view. This is a shortcut to a call to {@link #getView(String)}
	 * with {@link #DEFAULT_VIEW_ID} as parameter.
	 * 
	 * @return The default view or null if no default view has been installed.
	 */
	public View getDefaultView() {
		return getView(getDefaultID());
	}

	// Command
	/**
	 * Create a new instance of the default graph renderer.
	 */
	public abstract GraphRenderer<?, ?> newDefaultGraphRenderer();

	/**
	 * Build the default graph view and insert it. The view identifier is
	 * {@link #DEFAULT_VIEW_ID}. You can request the view to be open in its own
	 * frame.
	 * 
	 * @param openInAFrame
	 *            It true, the view is placed in a frame, else the view is only
	 *            created and you must embed it yourself in your application.
	 */
	public View addDefaultView(boolean openInAFrame) {
		synchronized (views) {
			GraphRenderer<?, ?> renderer = newDefaultGraphRenderer();
			View view = renderer.createDefaultView(this, getDefaultID());

			addView(view);

			if (openInAFrame)
				view.openInAFrame(true);

			return view;
		}
	}

	/**
	 * Add a view using its identifier. If there was already a view with this
	 * identifier, it is closed and returned (if different of the one added).
	 * 
	 * @param view
	 *            The view to add.
	 * @return The old view that was at the given identifier, if any, else null.
	 */
	public View addView(View view) {
		synchronized (views) {
			View old = views.put(view.getIdView(), view);

			if (old != null && old != view)
				old.close(graph);

			return old;
		}
	}

	/**
	 * Add a new default view with a specific renderer. If a view with the same id
	 * exists, it is removed and closed. By default the view is open in a frame.
	 * 
	 * @param id
	 *            The new view identifier.
	 * @param renderer
	 *            The renderer to use.
	 * @return The created view.
	 */
	public View addView(String id, GraphRenderer<?, ?> renderer) {
		return addView(id, renderer, true);
	}

	/**
	 * Same as {@link #addView(String, GraphRenderer)} but allows to specify that
	 * the view uses a frame or not.
	 * 
	 * @param id
	 *            The new view identifier.
	 * @param renderer
	 *            The renderer to use.
	 * @param openInAFrame
	 *            If true the view is open in a frame, else the returned view is a
	 *            JPanel that can be inserted in a GUI.
	 * @return The created view.
	 */
	public View addView(String id, GraphRenderer<?, ?> renderer, boolean openInAFrame) {
		synchronized (views) {
			View view = renderer.createDefaultView(this, id);
			addView(view);

			if (openInAFrame)
				view.openInAFrame(true);

			return view;
		}
	}

	/**
	 * Remove a view. The view is not closed.
	 * 
	 * @param id
	 *            The view identifier.
	 */
	public void removeView(String id) {
		synchronized (views) {
			views.remove(id);
		}
	}

	/**
	 * Compute the overall bounds of the graphic graph according to the nodes and
	 * sprites positions. We can only compute the graph bounds from the nodes and
	 * sprites centres since the node and graph bounds may in certain circumstances
	 * be computed according to the graph bounds. The bounds are stored in the graph
	 * metrics.
	 */
	public void computeGraphMetrics() {
		graph.computeBounds();

		synchronized (views) {
			Point3 lo = graph.getMinPos();
			Point3 hi = graph.getMaxPos();
			for (final View view : views.values()) {
				Camera camera = view.getCamera();
				if (camera != null) {
					camera.setBounds(lo.x, lo.y, lo.z, hi.x, hi.y, hi.z);
				}
			}
		}
	}

	/**
	 * What to do when the frame containing one or more views is closed.
	 * 
	 * @param policy
	 *            The close frame policy.
	 */
	public void setCloseFramePolicy(CloseFramePolicy policy) {
		synchronized (views) {
			closeFramePolicy = policy;
		}
	}

	// Optional layout algorithm

	/**
	 * Enable or disable the "xyz" attribute change when a node is moved in the
	 * views. By default the "xyz" attribute is changed.
	 * 
	 * By default, each time a node of the graphic graph is moved, its "xyz"
	 * attribute is reset to follow the node position. This is useful only if
	 * someone listen at the graphic graph or use the graphic graph directly. But
	 * this operation is quite costly. Therefore by default if this viewer runs in
	 * its own thread, and the main graph is in another thread, xyz attribute change
	 * will be disabled until a listener is added.
	 * 
	 * When the viewer is created to be used only in the ui thread, this feature is
	 * always on.
	 */
	public void enableXYZfeedback(boolean on) {
		synchronized (views) {
			graph.feedbackXYZ(on);
		}
	}

	/**
	 * Launch an automatic layout process that will position nodes in the
	 * background.
	 */
	public void enableAutoLayout() {
		enableAutoLayout(Layouts.newLayoutAlgorithm());
	}

	/**
	 * Launch an automatic layout process that will position nodes in the
	 * background.
	 * 
	 * @param layoutAlgorithm
	 *            The algorithm to use (see Layouts.newLayoutAlgorithm() for the
	 *            default algorithm).
	 */
	public void enableAutoLayout(Layout layoutAlgorithm) {
		synchronized (views) {
			if (optLayout == null) {
				// optLayout = new LayoutRunner(graph, layoutAlgorithm, true,
				// true);
				optLayout = new LayoutRunner(graph, layoutAlgorithm, true, false);
				graph.replay();
				layoutPipeIn = optLayout.newLayoutPipe();
				layoutPipeIn.addAttributeSink(graph);
			}
		}
	}

	/**
	 * Disable the running automatic layout process, if any.
	 */
	public void disableAutoLayout() {
		synchronized (views) {
			if (optLayout != null) {
				((ThreadProxyPipe) layoutPipeIn).unregisterFromSource();
				layoutPipeIn.removeSink(graph);
				layoutPipeIn = null;
				optLayout.release();
				optLayout = null;
			}
		}
	}

	/** Dirty replay of the graph. */
	public void replayGraph(Graph graph) {
		// Replay all graph attributes.

		graph.attributeKeys().forEach(key -> {
			this.graph.setAttribute(key, graph.getAttribute(key));
		});

		// Replay all nodes and their attributes.

		graph.nodes().forEach(node -> {
			Node n = this.graph.addNode(node.getId());

			node.attributeKeys().forEach(key -> {
				n.setAttribute(key, node.getAttribute(key));
			});
		});

		// Replay all edges and their attributes.

		graph.edges().forEach(edge -> {
			Edge e = this.graph.addEdge(edge.getId(), edge.getSourceNode().getId(), edge.getTargetNode().getId(),
					edge.isDirected());

			edge.attributeKeys().forEach(key -> {
				e.setAttribute(key, edge.getAttribute(key));
			});
		});
	}
}
