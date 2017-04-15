package org.graphstream.stream.net.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.net.HTTPSource;
import org.junit.Test;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.MatcherAssert.*;

public class TestHTTPSource {
	private final static String USER_AGENT = "Mozilla/5.0";
	@Test
	public void testServer() {
		try {
			HTTPSource source = new HTTPSource("localhost/g1", 8080);
			Graph graph = new DefaultGraph("g1");
			source.addSink(graph);
			int edgeId = 0;
			int nodeId=0;
			graph.setStrict(false);
			graph.setAutoCreate(true);
			final Edge first = graph.addEdge(Integer.toString(edgeId++),Integer.toString(nodeId++),Integer.toString(nodeId++));
			final Edge second = graph.addEdge(Integer.toString(edgeId++),Integer.toString(nodeId++),Integer.toString(nodeId++));
			source.start();
			assertThat(first.getId(), is("0"));
			assertThat(second.getId(), is("1"));
			URL url = new URL("http://localhost:8080/g1/edit?q=an&"+nodeId++);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("USER_AGENT", USER_AGENT);
			String inputLine;
			StringBuilder builder = new StringBuilder();
			try(BufferedReader in= new BufferedReader(new InputStreamReader(con.getInputStream()))){
				while((inputLine = in.readLine()) != null){
					builder.append(inputLine);
				}
				in.close();
				System.out.println(builder.toString());
			}catch (Exception e) {
				// TODO: handle exception
			}
			source.stop();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
