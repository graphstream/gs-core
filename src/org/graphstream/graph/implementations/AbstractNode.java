package org.graphstream.graph.implementations;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

import org.graphstream.graph.BreadthFirstIterator;
import org.graphstream.graph.DepthFirstIterator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.SourceBase;

/**
 * <p>
 * This class provides a basic implementation of {@code Node} interface, to
 * minimize the effort required to implement this interface.
 * </p>
 * 
 * <p>
 * This class implements all the methods of
 * {@link org.graphstream.graph.implementations#AbstractElement} and most of the
 * methods of {@link org.graphstream.graph#Node} (there are "only" ten abstract
 * methods). In addition to these, subclasses must provide implementations for
 * {@link #addEdgeCallback(AbstractEdge)} and {@link #removeEdgeCallback(AbstractEdge)} which
 * are called by the parent graph when an edge incident to this node is added to
 * or removed from the graph. This class has a low memory overhead (one
 * reference as field).
 * </p>
 */
public abstract class AbstractNode extends AbstractElement implements Node {

	// *** Fields ***

	/**
	 * The graph to which this node belongs
	 */
	private AbstractGraph graph;

	// *** Constructors

	/**
	 * Constructs a new node. This constructor copies the parameters into the
	 * corresponding fields
	 * 
	 * @param graph
	 *            The graph to which this node belongs.
	 * @param id
	 *            Unique identifier of this node.
	 */
	protected AbstractNode(AbstractGraph graph, String id) {
		super(id);
		this.graph = graph;
	}

	// *** Inherited from abstract element ***

	@Override
	protected void attributeChanged(String sourceId, long timeId,
			String attribute, AttributeChangeEvent event, Object oldValue,
			Object newValue) {
			graph.listeners.sendAttributeChangedEvent(sourceId, timeId, getId(),
					SourceBase.ElementType.NODE, attribute, event, oldValue,
					newValue);

	}

	@Override
	/**
	 * @return The id of the parent graph
	 * @see org.graphstream.graph.implementations.AbstractElement#myGraphId()
	 */
	protected String myGraphId() {
		return graph.getId();
	}

	@Override
	/**
	 * This implementation calls the corresponding method of the parent graph
	 * 
	 * @see org.graphstream.graph.implementations.AbstractElement#newEvent()
	 */
	protected long newEvent() {
		return graph.newEvent();
	}

	@Override
	/**
	 * This implementation calls the corresponding method of the parent graph
	 * 
	 * @see org.graphstream.graph.implementations.AbstractElement#nullAttributesAreErrors()
	 */
	protected boolean nullAttributesAreErrors() {
		return graph.nullAttributesAreErrors();
	}

	// *** Inherited from Node ***

	/**
	 * This implementation returns {@link #graph}.
	 * 
	 * @see org.graphstream.graph.Node#getGraph()
	 */
	public Graph getGraph() {
		return graph;
	}

	public abstract int getDegree();

	public abstract int getInDegree();

	public abstract int getOutDegree();

	// [has|get]Edge[Toward|From|Between](Node|int|String) -> 2 * 3 * 3 = 18
	// methods

	/**
	 * This implementation returns {@code true} if {@link #getEdgeToward(Node)}
	 * is not {@code null}.
	 * 
	 * @see org.graphstream.graph.Node#hasEdgeToward(org.graphstream.graph.Node)
	 */
	public boolean hasEdgeToward(Node node) {
		return getEdgeToward(node) != null;
	}

	/**
	 * This implementation returns {@code true} if {@link #getEdgeToward(int)}
	 * is not {@code null}.
	 * 
	 * @see org.graphstream.graph.Node#hasEdgeToward(int)
	 */
	public boolean hasEdgeToward(int index) {
		return getEdgeToward(index) != null;
	}

	/**
	 * This implementation returns {@code true} if {@link #getEdgeToward(Node)}
	 * is not {@code null}.
	 * 
	 * @see org.graphstream.graph.Node#hasEdgeToward(java.lang.String)
	 */
	public boolean hasEdgeToward(String id) {
		return getEdgeToward(id) != null;
	}

	/**
	 * This implementation returns {@code true} if {@link #getEdgeFrom(Node)} is
	 * not {@code null}.
	 * 
	 * @see org.graphstream.graph.Node#hasEdgeFrom(org.graphstream.graph.Node)
	 */
	public boolean hasEdgeFrom(Node node) {
		return getEdgeFrom(node) != null;
	}

	/**
	 * This implementation returns {@code true} if {@link #getEdgeFrom(int)} is
	 * not {@code null}.
	 * 
	 * @see org.graphstream.graph.Node#hasEdgeFrom(int)
	 */
	public boolean hasEdgeFrom(int index) {
		return getEdgeFrom(index) != null;
	}

	/**
	 * This implementation returns {@code true} if {@link #getEdgeFrom(Node)} is
	 * not {@code null}.
	 * 
	 * @see org.graphstream.graph.Node#hasEdgeFrom(java.lang.String)
	 */
	public boolean hasEdgeFrom(String id) {
		return getEdgeFrom(id) != null;
	}

	/**
	 * This implementation returns {@code true} if {@link #getEdgeBetween(Node)}
	 * is not {@code null}.
	 * 
	 * @see org.graphstream.graph.Node#hasEdgeBetween(org.graphstream.graph.Node)
	 */
	public boolean hasEdgeBetween(Node node) {
		return getEdgeBetween(node) != null;
	}

	/**
	 * This implementation returns {@code true} if {@link #getEdgeBetween(int)}
	 * is not {@code null}.
	 * 
	 * @see org.graphstream.graph.Node#hasEdgeBetween(int)
	 */
	public boolean hasEdgeBetween(int index) {
		return getEdgeBetween(index) != null;
	}

	/**
	 * This implementation returns {@code true} if {@link #getEdgeBetween(Node)}
	 * is not {@code null}.
	 * 
	 * @see org.graphstream.graph.Node#hasEdgeBetween(java.lang.String)
	 */
	public boolean hasEdgeBetween(String id) {
		return getEdgeBetween(id) != null;
	}

	public abstract <T extends Edge> T getEdgeToward(Node node);

	/**
	 * This implementation uses {@link #getEdgeToward(Node)}
	 * 
	 * @see org.graphstream.graph.Node#getEdgeToward(int)
	 */
	public <T extends Edge> T getEdgeToward(int index) {
		return getEdgeToward(graph.getNode(index));
	}

	/**
	 * This implementation uses {@link #getEdgeToward(Node)}
	 * 
	 * @see org.graphstream.graph.Node#getEdgeToward(java.lang.String)
	 */
	public <T extends Edge> T getEdgeToward(String id) {
		return getEdgeToward(graph.getNode(id));
	}

	public abstract <T extends Edge> T getEdgeFrom(Node node);

	/**
	 * This implementation uses {@link #getEdgeFrom(Node)}
	 * 
	 * @see org.graphstream.graph.Node#getEdgeFrom(int)
	 */
	public <T extends Edge> T getEdgeFrom(int index) {
		return getEdgeFrom(graph.getNode(index));
	}

	/**
	 * This implementation uses {@link #getEdgeFrom(Node)}
	 * 
	 * @see org.graphstream.graph.Node#getEdgeFrom(java.lang.String)
	 */
	public <T extends Edge> T getEdgeFrom(String id) {
		return getEdgeFrom(graph.getNode(id));
	}

	public abstract <T extends Edge> T getEdgeBetween(Node node);

	/**
	 * This implementation uses {@link #getEdgeBetween(Node)}
	 * 
	 * @see org.graphstream.graph.Node#getEdgeBetween(int)
	 */
	public <T extends Edge> T getEdgeBetween(int index) {
		return getEdgeBetween(graph.getNode(index));
	}

	/**
	 * This implementation uses {@link #getEdgeBetween(Node)}
	 * 
	 * @see org.graphstream.graph.Node#getEdgeBetween(java.lang.String)
	 */
	public <T extends Edge> T getEdgeBetween(String id) {
		return getEdgeBetween(graph.getNode(id));
	}

	// get[_|Entering|Leaving]EdgeIterator

	public abstract <T extends Edge> Iterator<T> getEdgeIterator();

	public abstract <T extends Edge> Iterator<T> getEnteringEdgeIterator();

	public abstract <T extends Edge> Iterator<T> getLeavingEdgeIterator();

	// getEach[_Entering|Leaving]Edge

	/**
	 * This implementation uses {@link #getEdgeIterator()}
	 * 
	 * @see org.graphstream.graph.Node#getEachEdge()
	 */
	public <T extends Edge> Iterable<T> getEachEdge() {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				Iterator<T> it = getEdgeIterator();
				return new AbstractGraph.ImmutableIterator<T>(it);
			}
		};
	}

	/**
	 * This implementation uses {@link #getEnteringEdgeIterator()}
	 * 
	 * @see org.graphstream.graph.Node#getEachEnteringEdge()
	 */
	public <T extends Edge> Iterable<T> getEachEnteringEdge() {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				Iterator<T> it = getEnteringEdgeIterator();
				return new AbstractGraph.ImmutableIterator<T>(it);
			}
		};
	}

	/**
	 * This implementation uses {@link #getLeavingEdgeIterator()}
	 * 
	 * @see org.graphstream.graph.Node#getEachLeavingEdge()
	 */
	public <T extends Edge> Iterable<T> getEachLeavingEdge() {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				Iterator<T> it = getLeavingEdgeIterator();
				return new AbstractGraph.ImmutableIterator<T>(it);
			}
		};
	}

	// get[_|Entering|Leaving]EdgeSet

	/**
	 * This implementation uses {@link #getEdgeIterator()} and
	 * {@link #getDegree()}
	 * 
	 * @see org.graphstream.graph.Node#getEdgeSet()
	 */
	public <T extends Edge> Collection<T> getEdgeSet() {
		return new AbstractCollection<T>() {
			@Override
			public Iterator<T> iterator() {
				Iterator<T> it =  getEdgeIterator();
				return new AbstractGraph.ImmutableIterator<T>(it);
			}

			@Override
			public int size() {
				return getDegree();
			}
		};
	}

	/**
	 * This implementation uses {@link #getEnteringEdgeIterator()} and
	 * {@link #geIntDegree()}
	 * 
	 * @see org.graphstream.graph.Node#getEnteringEdgeSet()
	 */
	public <T extends Edge> Collection<T> getEnteringEdgeSet() {
		return new AbstractCollection<T>() {
			@Override
			public Iterator<T> iterator() {
				Iterator<T> it = getEnteringEdgeIterator();
				return new AbstractGraph.ImmutableIterator<T>(it);
			}

			@Override
			public int size() {
				return getInDegree();
			}
		};
	}

	/**
	 * This implementation uses {@link #getLeavingIterator()} and
	 * {@link #geOuttDegree()}
	 * 
	 * @see org.graphstream.graph.Node#getLeavingEdgeSet()
	 */
	public <T extends Edge> Collection<T> getLeavingEdgeSet() {
		return new AbstractCollection<T>() {
			@Override
			public Iterator<T> iterator() {
				Iterator<T> it = getLeavingEdgeIterator();
				return new AbstractGraph.ImmutableIterator<T>(it);
			}

			@Override
			public int size() {
				return getOutDegree();
			}
		};
	}

	/**
	 * This implementation uses {@link #getEdgeIterator()}
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Edge> iterator() {
		return getEdgeIterator();
	}

	public abstract <T extends Edge> T getEdge(int i);

	/**
	 * This implementation uses {@link #getEdgeIterator()}
	 * 
	 * @see org.graphstream.graph.Node#getNeighborNodeIterator()
	 */
	public <T extends Node> Iterator<T> getNeighborNodeIterator() {
		return new Iterator<T>() {
			Iterator<Edge> edgeIterator = getEdgeIterator();

			public boolean hasNext() {
				return edgeIterator.hasNext();
			}

			public T next() {
				return edgeIterator.next().getOpposite(AbstractNode.this);
			}

			public void remove() {
				edgeIterator.remove();
			}
		};
	}

	// breadth- and depth-first iterator

	/**
	 * This implementation creates an instance of
	 * {@link org.graphstream.graph#BreadthFirstIterator} and returns it.
	 * 
	 * @see org.graphstream.graph.Node#getBreadthFirstIterator()
	 */
	public <T extends Node> Iterator<T> getBreadthFirstIterator() {
		return new BreadthFirstIterator<T>(this);
	}

	/**
	 * This implementation creates an instance of
	 * {@link org.graphstream.graph#BreadthFirstIterator} and returns it.
	 * 
	 * @see org.graphstream.graph.Node#getBreadthFirstIterator(boolean)
	 */
	public <T extends Node> Iterator<T> getBreadthFirstIterator(boolean directed) {
		return new BreadthFirstIterator<T>(this, directed);
	}

	/**
	 * This implementation creates an instance of
	 * {@link org.graphstream.graph#DepthFirstIterator} and returns it.
	 * 
	 * @see org.graphstream.graph.Node#getDepthFirstIterator()
	 */
	public <T extends Node> Iterator<T> getDepthFirstIterator() {
		return new DepthFirstIterator<T>(this);
	}

	/**
	 * This implementation creates an instance of
	 * {@link org.graphstream.graph#DepthFirstIterator} and returns it.
	 * 
	 * @see org.graphstream.graph.Node#getDepthFirstIterator(boolean)
	 */
	public <T extends Node> Iterator<T> getDepthFirstIterator(boolean directed) {
		return new DepthFirstIterator<T>(this, directed);
	}

	// *** Other methods ***

	/**
	 * This method must be called by the constructors of the edges incident to
	 * this node. Subclasses use it to add the edge to their data structure.
	 * 
	 * @param edge
	 *            a new edge incident to this node
	 */
	protected abstract boolean addEdgeCallback(AbstractEdge edge);

	/**
	 * This method must be called before removing an edge incident to this node.
	 * Subclasses use it to remove the edge from their data structure.
	 * 
	 * @param edge
	 *            an edge incident to this node that will be removed
	 */
	protected abstract void removeEdgeCallback(AbstractEdge edge);

	/**
	 * Checks if an edge enters this node. Utility method that can be useful in
	 * subclasses.
	 * 
	 * @param e
	 *            an edge
	 * @return {@code true} if {@code e} is entering edge for this node.
	 */
	public boolean isEnteringEdge(Edge e) {
		return e.getTargetNode() == this
				|| (!e.isDirected() && e.getSourceNode() == this);
	}

	/**
	 * Checks if an edge leaves this node. Utility method that can be useful in
	 * subclasses.
	 * 
	 * @param e
	 *            an edge
	 * @return {@code true} if {@code e} is leaving edge for this node.
	 */
	public boolean isLeavingEdge(Edge e) {
		return e.getSourceNode() == this
				|| (!e.isDirected() && e.getTargetNode() == this);
	}

	/**
	 * Checks if an edge is incident to this node. Utility method that can be useful in
	 * subclasses.
	 * 
	 * @param e
	 *            an edge
	 * @return {@code true} if {@code e} is incident edge for this node.
	 */
	public boolean isIncidentEdge(Edge e) {
		return e.getSourceNode() == this || e.getTargetNode() == this;
	}
}
