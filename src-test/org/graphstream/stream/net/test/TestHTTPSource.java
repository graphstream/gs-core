package org.graphstream.stream.net.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.net.HTTPSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestHTTPSource {
	private static final String NODE_1 = "/node/1";

	final static String ROOT_URL = "http://localhost:8080/g1";

	static HTTPSource source;

	static Graph graph;

	/**
	 * Create server and attach graph to it
	 */
	@BeforeClass
	public static void setUp() {
		source = new HTTPSource("g1", 8080);
		graph = new DefaultGraph("g1");
		source.addSink(graph);
	}

	@Test
	public void testAddAndDeleteNode() throws IOException {

		HttpUriRequest request = new HttpPost(ROOT_URL + NODE_1);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
		assertThat(graph.getNodeCount(), is(1));
		request = new HttpDelete(ROOT_URL + NODE_1);
		httpResponse = HttpClientBuilder.create().build().execute(request);
		assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
		assertThat(graph.getNodeCount(), is(0));

	}

	@Test
	public void testAddAndDeleteEdge() throws ClientProtocolException, IOException {
		HttpUriRequest request = new HttpPost(ROOT_URL + NODE_1);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
		request = new HttpPost(ROOT_URL + "/node/2");
		httpResponse = HttpClientBuilder.create().build().execute(request);
		assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
		request = new HttpPost(ROOT_URL + "/edge/1/1/2/false");
		httpResponse = HttpClientBuilder.create().build().execute(request);
		assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
		assertThat(graph.getNodeCount(), is(2));
		assertThat(graph.getEdgeCount(), is(1));
		request = new HttpDelete(ROOT_URL + "/edge/1");
		httpResponse = HttpClientBuilder.create().build().execute(request);
		assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
	}

	@Test
	public void testStep() throws ClientProtocolException, IOException {
		HttpUriRequest request = new HttpPost(ROOT_URL + "/step/1");
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		assertThat(httpResponse.getStatusLine().getStatusCode(), is(200));
		request = new HttpPost(ROOT_URL + "/step/A");
		httpResponse = HttpClientBuilder.create().build().execute(request);
		assertThat(httpResponse.getStatusLine().getStatusCode(), is(400));
	}

	@After
	public void cleanGraph() {
		graph.clear();
	}

	/*
	 * Stop server
	 */
	@AfterClass
	public static void cleanUp() {
		source.stop();
	}

}
