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
 * @since 2011-05-11
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.file.FileSourceEdge;
import org.junit.Before;
import org.junit.Test;

public class TestFileSourceEdge extends TestFileSourceBase {
	// Before

	@Before
	public void setUp() {
		graph = new MultiGraph("g1");
		input = new FileSourceEdge();
		testEdgeIds = false;
	}

	public static void main(String args[]) {
		TestFileSourceEdge fid = new TestFileSourceEdge();

		fid.setUp();
		fid.test_Access_ReadAll_Stream();
	}

	// Test

	@Override
	public String anUndirectedTriangle() {
		return TEST1_TRIANGLE;
	}

	protected static String TEST1_TRIANGLE = "A B\n" + "B C\n" + "C A\n";

	@Override
	public String aDirectedTriangle() {
		return TEST2_DIRECTED_TRIANGLE;
	}

	protected static String TEST2_DIRECTED_TRIANGLE = "A B\n" + "B C\n" + "A C\n";

	@Override
	public String basicAttributes() {
		return "";
	}

	@Test
	@Override
	public void test_DirectedTriangle() {
		input = new FileSourceEdge(true);

		try {
			input.addSink(graph);
			input.readAll(new StringReader(aDirectedTriangle()));
			String BCid = graph.getNode("B").getEdgeToward("C").getId();
			graph.removeEdge(BCid);
			graph.addEdge(BCid, "B", "C", false);
			directedTriangleTests();
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue("IOException, should not happen" + e.getMessage(), false);
		}
	}

	@Test
	@Override
	public void test_Attributes() {
		// NOP, edge format does not allow attributes.
	}

	@Override
	public String anUndirectedTriangleFileName() {
		return "src-test/org/graphstream/stream/file/test/data/undirectedTriangle.edge";
	}

	@Override
	public String anUndirectedTriangleHttpURL() {
		return "http://graphstream-project.org/media/data/undirectedTriangle.edge";
	}
}