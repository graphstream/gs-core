package org.graphstream.graph.test;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SimpleAdjacencyListGraph;
import org.graphstream.stream.Timeline;
import org.graphstream.util.VerboseSink;

public class DemoTimeline {
	
	public void timeline() throws Exception {
		Graph g = new SimpleAdjacencyListGraph("g");
		Timeline timeline = new Timeline();
		timeline.addSink(new VerboseSink());

		timeline.begin(g);

		g.stepBegins(0.0);
		g.addNode("A");
		g.addNode("B");
		g.stepBegins(1.0);
		g.addNode("C");

		timeline.end();

		System.out.printf("############\n");
		System.out.printf("# Play :\n");
		timeline.play();
		System.out.printf("############\n");
		System.out.printf("# Playback :\n");
		timeline.playback();
		System.out.printf("############\n");
		System.out.printf("# Sequence :\n");
		int i = 0;
		for (Graph it : timeline) {
			System.out.printf(" Graph#%d %s\n", i, toString(it));
		}
		System.out.printf("############\n");
	}

	private static String toString(Graph<?,?> g) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("id=\"").append(g.getId()).append("\" node={");

		for (Node n : g)
			buffer.append("\"").append(n.getId()).append("\", ");
		buffer.append("}, edges={");
		for (Edge e : g.getEachEdge())
			buffer.append("\"").append(e.getId()).append("\":\"")
					.append(e.getSourceNode().getId()).append("\"--\"")
					.append(e.getTargetNode().getId()).append("\", ");
		buffer.append("}");

		return buffer.toString();
	}
}
