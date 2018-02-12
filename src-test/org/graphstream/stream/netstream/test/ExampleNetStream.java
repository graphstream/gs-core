/*
 * Copyright 2006 - 2016
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
 *
 * This file is part of GraphStream <http://graphstream-project.org>.
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
package org.graphstream.stream.netstream.test;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.binary.ByteProxy;
import org.graphstream.stream.netstream.NetStreamUtils;

import java.io.IOException;
import java.net.InetAddress;

/**
 * @since 01/02/16.
 */
public class ExampleNetStream {
    public static void main(String... args) throws IOException {
        //
        // Create a byte proxy server that will use NetStream as encoder/decoder protocol.
        // Start it to listen to connection.
        //

        ByteProxy server = new ByteProxy(NetStreamUtils.getDefaultNetStreamFactory(), 10000);
        server.start();

        DefaultGraph graphServer = new DefaultGraph("server");
        graphServer.addSink(server);

        server.setReplayable(graphServer);

        //
        // Create now a byte proxy client that will connect to the previous server.
        //

        ByteProxy client = new ByteProxy(NetStreamUtils.getDefaultNetStreamFactory(), ByteProxy.Mode.CLIENT, InetAddress.getLocalHost(), 10000);
        client.start();

        Graph graphClient = new DefaultGraph("client");
        client.addSink(graphClient);

        graphClient.display();

        //
        // Add some elements in the server graph. It should appear in the client graph.
        //

        graphServer.addNode("A");
        graphServer.addNode("B");
        graphServer.addNode("C");

        graphServer.addEdge("AB", "A", "B");
        graphServer.addEdge("AC", "A", "C");
        graphServer.addEdge("BC", "B", "C");
    }
}
