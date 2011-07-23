package org.graphstream.graph.implementations;

import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

public class TestGraph extends AbstractGraph {

	public TestGraph(String id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void addEdgeCallback(AbstractEdge edge) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void addNodeCallback(AbstractNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void clearCallback() {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends Edge> T getEdge(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected <T extends Edge> T getEdgeByIndex(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getEdgeCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T extends Edge> Iterator<T> getEdgeIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Node> T getNode(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected <T extends Node> T getNodeByIndex(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNodeCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T extends Node> Iterator<T> getNodeIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void removeEdgeCallback(AbstractEdge edge) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void removeNodeCallback(AbstractNode node) {
		// TODO Auto-generated method stub

	}

}
