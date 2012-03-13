/*
 * Copyright 2006 - 2012
 *      Stefan Balev       <stefan.balev@graphstream-project.org>
 *      Julien Baudry	<julien.baudry@graphstream-project.org>
 *      Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *      Yoann Pigné	<yoann.pigne@graphstream-project.org>
 *      Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
 *  
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
package org.graphstream.stream.netstream;

import java.io.IOException;
import java.net.UnknownHostException;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.netstream.packing.Base64Packer;
import org.graphstream.stream.netstream.packing.Base64Unpacker;
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
 * 
 * 
 * ExampleWithViewer.java
 * @since Aug 17, 2011
 * 
 * @author Yoann Pigné

 */
public class ExampleWithViewer {

	public static void main(String[] args) throws UnknownHostException,
			IOException, InterruptedException {
		// ----- On the receiver side -----
		//
		// - a graph that will display the received events
		Graph g = new MultiGraph("G");
		g.display();
		// - the receiver that waits for events
		NetStreamReceiver net = new NetStreamReceiver(2001);
		
		net.setUnpacker(new Base64Unpacker());
		
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
				
				nsc.setPacker(new Base64Packer());
			
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
