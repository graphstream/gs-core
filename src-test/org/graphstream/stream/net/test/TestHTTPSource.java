/*
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

/**
 * @since 2017-04-15
 * 
 * @author Sven Marquardt <Sven2q@hotmail.de>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
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
