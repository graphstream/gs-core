package org.graphstream.graph.implementations;


/**
 * <p>In fact the class AbstractEdge has no abstract methods 
 * (I know, I'll burn in hell for this, but I called it so just for symmetry
 * with AbstractNode and AbstractGraph.</p>
 * 
 * <p>I do nothing here, but you can put some extra data, for example where
 * the edge is in its nodes' data structure so that the node can locate it faster.</p>
 * 
 * @author Stefan Balev
 *
 */
public class ToyEdge extends AbstractEdge {

	protected ToyEdge(String id, AbstractNode source, AbstractNode target, boolean directed) {
		super(id, source, target, directed);
	}

}
