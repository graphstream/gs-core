package org.miv.graphstream.graph;

/**
 * Helper class that allows to listen at a graph an either copy it or send events
 * across thread boundaries, for example.
 * 
 * <p>
 * This interface and its implementations has many usages. It acts as a proxy that allows to put a
 * listener on a graph and manipulate, translate, send these events to other graph listeners.
 * </p>
 * 
 * <p>The two main implementations of this class are a version that allows to maintain an exact
 * copy of the graph (an "input graph" and an "output graph"), and another that allows to do the
 * same but in addition is able to do this
 * across threads boundaries. This means that the copy of the graph, the output graph,
 * can be in another thread.
 * </p>
 * 
 * <p>
 * This class is (or maybe) "passive", you must check that events coming from graph are available
 * regularly by calling the
 * {@link #checkEvents()} method. Indeed, events may be buffered, or need a translation phase,
 * etc. This however is not always the case.
 * </p>
 * 
 * <p>
 * This method will check if some events occurred in the input graph
 * and will modify the output graph accordingly (if any) and forward events to each registered
 * graph listener.
 * </p>
 * 
 * @see org.miv.graphstream.graph.GraphListener
 * @author Antoine Dutot
 * @author Yoann Pigné
 * @since 20061208
 */
public interface GraphListenerProxy extends GraphListener
{
// Commands

	/**
	 * Ask the proxy to unregister from the graph (stop receive events) as soon as possible
	 * (when the next event will occur in the graph).
	 */
	void unregisterFromGraph();
	
	/**
	 * Add a listener to the events of the input graph.
	 * @param listener The listener to call for each event in the input graph.
	 */
	void addGraphListener( GraphListener listener );
	
	/**
	 * Remove a listener. 
	 * @param listener The listener to remove.
	 */
	void removeGraphListener( GraphListener listener );
	
	/**
	 * This method must be called regularly to check if the input graph sent
	 * events. If some event occurred, the listeners will be called and the output graph
	 * given as argument will be modified (if any).
	 */
	void checkEvents();	
}