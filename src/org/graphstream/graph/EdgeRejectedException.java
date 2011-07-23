package org.graphstream.graph;
/**
 * This exception is thrown when on of the endpoints of an edge does not accept
 * the edge. This can happen for example when one tries to add an edge between
 * two already connected nodes is a single graph.
 * 
 */
public class EdgeRejectedException extends RuntimeException {
	private static final long serialVersionUID = 4952910935083960955L;

	public EdgeRejectedException() {
		super("Edge rejected");
	}

	public EdgeRejectedException(String message) {
		super(message);
	}
}
