package org.graphstream.stream.test;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.stream.Sink;
import org.graphstream.stream.SinkAdapter;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDGS;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

/**
 * Test the ability of Graphs to insert events in between actual streams. Two cases are envisioned.
 * First, while autocreate mode is activated, when an Add Edge event is created  with non existing nodes,
 * the forwarded stream of events must include Add Node events prior to the Add Edge Event.
 * Second, when a node is removed, Graphs should generate and stream Edge Remove events for all edges connected to the
 * node to be removed.
 */
public class TestAutoCreateInStreams {

    @Test
    public void testAutoCreate(){
        Graph g = new AdjacencyListGraph("ok", false, true);

        final LinkedList<String> an= new LinkedList<>();
        final String[] expectedAn = {"a","b","c","d","e"};

        Sink sink = new SinkAdapter(){
            @Override
            public void nodeAdded(String sourceId, long timeId, String nodeId) {
                an.add(nodeId);
            }
        };
        g.addSink(sink);

        // event from the constructivist API
        g.addEdge("ab", "a","b");

        // events from a pipe
        FileSource fs = new FileSourceDGS();
        try {
            fs.begin(new ByteArrayInputStream("DGS004\n0 0\nae bc b c\nae ac a c".getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        fs.addSink(g);
        try {
            while(fs.nextEvents());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // events from another pipe
        fs = new FileSourceDGS();
        try {
            fs.begin(new ByteArrayInputStream("DGS004\n0 0\nae dc d c\nae de d e".getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        fs.addSink(g);
        try {
            while(fs.nextEvents());
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(expectedAn.length, an.size());

        for(String nId :  expectedAn){
            assertEquals(nId, an.remove(0));
        }
    }


    @Test
    public void testAutoRemove() {
        Graph g = new AdjacencyListGraph("ok");

        final LinkedList<String> de = new LinkedList<>();
        final String[] expectedDe = {"ab", "ca", "bc"};

        Sink sink = new SinkAdapter() {
            @Override
            public void edgeRemoved(String sourceId, long timeId, String edgeId) {
                de.add(edgeId);
            }
        };
        g.addSink(sink);


        g.addNode("a");
        g.addNode("b");
        g.addNode("c");

        g.addEdge("ab", "a", "b");
        g.addEdge("bc", "b", "c");
        g.addEdge("ca", "c", "a");

        // event from the constructivist API
        g.removeNode("a");


        // events from a pipe
        FileSource fs = new FileSourceDGS();
        try {
            fs.begin(new ByteArrayInputStream("DGS004\n0 0\ndn b".getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        fs.addSink(g);
        try {
            while (fs.nextEvents()) ;
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(expectedDe.length, de.size());

        for (String eId : expectedDe) {
            assertEquals(eId, de.remove(0));
        }
    }
}
