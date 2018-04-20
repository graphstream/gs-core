package org.graphstream.stream.netstream.test;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.binary.ByteProxy;
import org.graphstream.stream.netstream.NetStreamUtils;
import org.graphstream.util.VerboseSink;

import java.io.IOException;
import java.net.InetAddress;

public class ExampleNetStreamClientSends {
    public static void main(String[] args) throws IOException, InterruptedException {


    System.out.println("server...");

    Graph g = new MultiGraph("G",false,true);

    VerboseSink logout = new VerboseSink();
    logout.setPrefix("server logout");
    g.addSink(logout);

    ByteProxy server = null;
    try {
        server = new ByteProxy(NetStreamUtils.getDefaultNetStreamFactory(), 2001);
    } catch (IOException e) {
        e.printStackTrace();
    }
    server.addSink(g);
    server.start();


    System.out.println("client...");

    String id = "ME";
    String label = "Mr or Ms ME";

    ByteProxy client = null;
    try {
        client = new ByteProxy(NetStreamUtils.getDefaultNetStreamFactory(), ByteProxy.Mode.CLIENT,
                InetAddress.getLocalHost(), 2001);
    } catch (IOException e) {
        e.printStackTrace();
    }
    client.start();

    Graph graphClient = new DefaultGraph("client");

    graphClient.addSink(client);

    //VerboseSink clientVSink = new VerboseSink();
    //clientVSink.setPrefix("client graph logout");
    //graphClient.addSink(clientVSink);

    Node n = graphClient.addNode(id);
    n.setAttribute( "ui.label", label);
    n.setAttribute( "nope", "ok", "not ok");

    client.stop();

    }
}
