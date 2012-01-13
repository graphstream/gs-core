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
import org.graphstream.stream.netstream.packing.Base64Unpacker;
import org.graphstream.stream.thread.ThreadProxyPipe;

/**
 *  A simple example of use of the NetStream receiver.
 *
 *  ExampleReceiver.java
 *  @since Aug 19, 2011
 *
 *  @author Yoann Pigné
 *
 */
public class ExampleReceiver {

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		// ----- On the receiver side -----
		//
		// - a graph that will display the received events
		Graph g = new MultiGraph("G",false,true);
		g.display();
		// - the receiver that waits for events
		NetStreamReceiver net = new NetStreamReceiver("localhost",2001,false);
		
		net.setUnpacker(new  Base64Unpacker());
		
		// - received events end up in the "default" pipe
		ThreadProxyPipe pipe = net.getStream("default");
		// - plug the pipe to the sink of the graph
		pipe.addSink(g);
		// -The receiver pro-actively checks for events on the ThreadProxyPipe
		while (true) {
			pipe.pump();
			Thread.sleep(100);
		}
	}
}
