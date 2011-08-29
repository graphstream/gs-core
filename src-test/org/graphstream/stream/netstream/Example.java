/**
 * 
 * Copyright (c) 2011 University of Luxembourg
 * 
 * @file Example.java
 * @date Aug 17, 2011
 * 
 * @author Yoann Pigné
 * 
 */
package org.graphstream.stream.netstream;

import java.io.IOException;
import java.net.UnknownHostException;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.thread.ThreadProxyPipe;

/**
 * A simple example of use of the NetStream sender and receiver.
 * <p>
 * This is a dummy example where the Java Sender and Receiver are used together.
 * This is not the normal use of the NetStream, normally the receiver should be
 * connected to a sender from another language or the opposite. For a
 * java-to-java network communication other tools based on RMI are probably more
 * suitable.
 * </p>
 * <p>
 * In this class a basic receiver plugged to a displayed multigraph is created
 * and waits for events on the "default" stream, on port 2001 at "localhost". In
 * another thread, a Sender is created with a graph connected to its sink, so
 * that any event from the graph will end-up to the sender. The sender connects
 * to "localhost" on port 2001 and sends, on the default stream, any received
 * event to the receiver, through the network, using the NetStream protocol.
 * </p>
 */
public class Example {

	public static void main(String[] args) throws UnknownHostException,
			IOException, InterruptedException {
		// ----- On the receiver side -----
		//
		// - a graph that will display the received events
		Graph g = new MultiGraph("G");
		g.display();
		// - the receiver that waits for events
		NetStreamReceiver net = new NetStreamReceiver(2001);
		// - received events end up in the "default" pipe
		ThreadProxyPipe pipe = net.getDefaultStream();
		// - plug the pipe to the sink of the graph
		pipe.addSink(g);

		// ----- The sender side (in another thread) ------
		//
		new Thread() {

			public void run() {
				// - the original graph from which events are generated
				Graph g = new MultiGraph("G");
				// - the sender
				NetStreamSender nsc = null;
				try {
					nsc = new NetStreamSender(2001);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				// - plug the graph to the sender so that graph events can be
				// sent automatically
				g.addSink(nsc);
				// - generate some events on the client side
				String style = "node{fill-mode:plain;fill-color:#567;size:6px;}";
				g.addAttribute("stylesheet", style);
				g.addAttribute("ui.antialias", true);
				g.addAttribute("layout.stabilization-limit", 0);
				for (int i = 0; i < 500; i++) {
					g.addNode(i + "");
					if (i > 0) {
						g.addEdge(i + "-" + (i - 1), i + "", (i - 1) + "");
						g.addEdge(i + "--" + (i / 2), i + "", (i / 2) + "");
					}
				}
			}
		}.start();

		// ----- Back to the receiver side -----
		//
		// -The receiver pro-actively checks for events on the ThreadProxyPipe
		while (true) {
			pipe.pump();
			Thread.sleep(100);
		}

	}
}
