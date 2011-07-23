package org.graphstream.graph.implementations;

import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

public class TestNode extends AbstractNode {

	protected TestNode(AbstractGraph graph, String id) {
		super(graph, id);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean addEdgeCallback(AbstractEdge edge) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void clearCallback() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getDegree() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T extends Edge> T getEdge(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Edge> T getEdgeBetween(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Edge> T getEdgeFrom(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Edge> Iterator<T> getEdgeIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Edge> T getEdgeToward(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Edge> Iterator<T> getEnteringEdgeIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getInDegree() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T extends Edge> Iterator<T> getLeavingEdgeIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getOutDegree() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void removeEdgeCallback(AbstractEdge edge) {
		// TODO Auto-generated method stub

	}

}
