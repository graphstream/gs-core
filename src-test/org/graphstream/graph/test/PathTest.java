package org.graphstream.graph.test;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.DefaultGraph;
import org.junit.Test;

public class PathTest {

	@Test(expected = IllegalArgumentException.class)
	public void add_nodeHeadMustBeInEdge() {
		Graph graph = createSimpleGraph();
		Path path = new Path();

		path.setRoot(graph.getNode("a"));

		// this has to fail as there is no edge between nodes "a" and "c"
		path.add(graph.getEdge("cd"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void add_whenAddingEdgeRootMustBeSet() {
		Graph graph = createSimpleGraph();
		Path path = new Path();

		// this has to fail as root of the path is not set
		path.add(graph.getEdge("ab"));
	}

	private Graph createSimpleGraph() {
		Graph graph = new DefaultGraph("test");
		graph.setStrict(false);
		graph.setAutoCreate(true);

		graph.addEdge("ab", "a", "b");
		graph.addEdge("bc", "b", "c");
		graph.addEdge("cd", "c", "d");

		return graph;
	}
}