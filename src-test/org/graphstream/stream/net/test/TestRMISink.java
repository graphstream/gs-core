package org.graphstream.stream.net.test;

import java.rmi.RemoteException;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.rmi.RMISink;

public class TestRMISink {
	public static void main(String args[]) {
		try {
			new TestRMISink(args);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public TestRMISink(String args[]) throws RemoteException {
		RMISink sink = new RMISink();
		
		sink.bind("theSink");
		sleep(5000);
		
		Graph graph = new SingleGraph("testRMI");
		
		graph.addSink(sink);
		
		graph.addNode("A");
		graph.addNode("B");
		graph.addNode("C");
		graph.addEdge("AB", "A", "B");
		graph.addEdge("BC", "B", "C");
		graph.addEdge("CA", "C", "A");
		
		sink.unregister("theSink");			
	}
	
	protected void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
