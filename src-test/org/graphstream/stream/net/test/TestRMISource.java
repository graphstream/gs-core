package org.graphstream.stream.net.test;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.rmi.RMISource;

public class TestRMISource {
	public static void main(String args[]) {
		try {
			LocateRegistry.createRegistry(1099);
			
			new TestRMISource(args);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public TestRMISource(String args[]) throws RemoteException {
		RMISource source = new RMISource();
		
		source.bind("theSink");
		Graph graph = new SingleGraph("TestRMI");
		
		source.addSink(graph);
		
		graph.display();
	}
}
