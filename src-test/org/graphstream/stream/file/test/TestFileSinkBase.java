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

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSource;
import org.junit.Before;
import org.junit.Test;

/**
 * Base for tests on descendants of {@link org.graphstream.stream.file.FileSink}
 * .
 * 
 * <p>
 * This files does all the tests. To implement a test for a specific file
 * format, you have only to implement/override two methods :
 * <ul>
 * <li>Override the {@link #graphFileExtension()} method that will return the
 * name of a file with the correct extension for the file format.</li>
 * <li>Implement the {@link #setup()} method that initialise the {@link #input}
 * and {@link #output} fields. These fields contain an instance of the
 * {@link org.graphstream.stream.file.FileSink} you want to test and the
 * corresponding {@link org.graphstream.stream.file.FileSource} for reading back
 * the results of an output and test it.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Some tests may not be appropriate for some graph formats :
 * <ul>
 * <li>If your graph format does not support edge identifiers, set
 * {@link #formatHandlesEdgesIDs} to false.</li>
 * <li>If your graph format does not support dynamic graphs, set
 * {@link #formatHandleDynamics} to false.</li>
 * <li>If your graph format does not support attributes, set
 * {@link #formatHandlesAttributes} to false.</li>
 * </ul>
 * By default all these settings are set to true. You can change them in the
 * {@link #setup()} method.
 * </p>
 */
public abstract class TestFileSinkBase {
	// Attribute

	protected Graph outGraph;
	protected Graph inGraph;
	protected FileSource input;
	protected FileSink output;

	protected boolean formatHandlesEdgesIDs = true;
	protected boolean formatHandlesAttributes = true;
	protected boolean formatHandleDynamics = true;

	protected File theFile;

	// To implement or override

	@Before
	public void createTheFile() {
		try {
			theFile = File.createTempFile("test_", graphFileExtension());
		} catch (IOException e) {
			fail("Can not create temporary file");
		}
	}

	/**
	 * Method to implement to create the {@link #input} and {@link #output} fields.
	 * These fields contain the instance of the
	 * {@link org.graphstream.stream.file.FileSource} and
	 * {@link org.graphstream.stream.file.FileSink} to test.
	 */
	@Before
	public abstract void setup();

	/**
	 * Return the name of a graph file in the current graph output format. The name
	 * of the file must remain the same.
	 */
	protected abstract String graphFileExtension();

	// Test

	@Before
	public void setup2() {
		outGraph = new MultiGraph("out");
		inGraph = new MultiGraph("in");
	}

	@Test
	public void test_UndirectedTriangle_WriteAll_FileName() {
		createUndirectedTriangle();

		try {
			output.writeAll(outGraph, theFile.getAbsolutePath());
			input.addSink(inGraph);
			input.readAll(theFile.getAbsolutePath());
			testUndirectedTriangle();
		} catch (IOException e) {
			e.printStackTrace();
			fail("Should not happen !");
		}
	}

	@Test
	public void test_UndirectedTriangle_WriteAll_Stream() {
		createUndirectedTriangle();

		try {
			output.writeAll(outGraph, new FileOutputStream(theFile.getAbsolutePath()));
			input.addSink(inGraph);
			input.readAll(theFile.getAbsolutePath());
			testUndirectedTriangle();
		} catch (IOException e) {
			e.printStackTrace();
			fail("Should not happen !");
		}
	}

	@Test
	public void test_UndirectedTriangle_ByEvent() {
		try {
			output.begin(theFile.getAbsolutePath());
			output.nodeAdded("?", 1, "A");
			output.nodeAdded("?", 2, "B");
			output.nodeAdded("?", 3, "C");
			output.edgeAdded("?", 4, "AB", "A", "B", false);
			output.edgeAdded("?", 5, "BC", "B", "C", false);
			output.edgeAdded("?", 6, "CA", "C", "A", false);
			output.end();

			input.addSink(inGraph);
			input.readAll(theFile.getAbsolutePath());
			testUndirectedTriangle();
		} catch (IOException e) {
			e.printStackTrace();
			fail("Should not happen !");
		}
	}

	@Test
	public void test_DirectedTriangle() {
		createDirectedTriangle();

		try {
			output.writeAll(outGraph, new FileOutputStream(theFile.getAbsolutePath()));
			input.addSink(inGraph);
			input.readAll(theFile.getAbsolutePath());
			testDirectedTriangle();
		} catch (IOException e) {
			e.printStackTrace();
			fail("Should not happen !");
		}
	}

	@Test
	public void test_Attributes() {
		if (formatHandlesAttributes) {
			createAttributedTriangle();

			try {
				output.writeAll(outGraph, new FileOutputStream(theFile.getAbsolutePath()));
				input.addSink(inGraph);
				input.readAll(theFile.getAbsolutePath());

				testAttributedTriangle();
			} catch (IOException e) {
				e.printStackTrace();
				fail("Should not happen !");
			} finally {
				// removeFile(theFile.getAbsolutePath());
			}
		}
	}

	@Test
	public void test_Dynamic() {
		if (formatHandleDynamics) {
			try {
				output.begin(new FileOutputStream(theFile.getAbsolutePath()));
				outGraph.addSink(output);
				outGraph.stepBegins(0);
				outGraph.addNode("A");
				outGraph.addNode("B");
				outGraph.addNode("C");
				outGraph.stepBegins(1);
				outGraph.addEdge("AB", "A", "B");
				outGraph.addEdge("BC", "B", "C");
				outGraph.addEdge("CA", "C", "A");
				outGraph.stepBegins(2);
				outGraph.setAttribute("a", 1);
				outGraph.setAttribute("b", "foo");
				outGraph.getNode("A").setAttribute("a", 1);
				outGraph.getNode("B").setAttribute("b", "foo");
				outGraph.getNode("C").setAttribute("c", "bar");
				outGraph.stepBegins(3);
				outGraph.removeNode("A");
				outGraph.stepBegins(4);
				outGraph.removeEdge("BC");
				output.end();

				input.addSink(inGraph);
				input.begin(theFile.getAbsolutePath());
				testDynamicTriangleStep0();
				input.nextStep();
				testDynamicTriangleStep0_1();
				input.nextStep();
				testDynamicTriangleStep1_2();
				input.nextStep();
				testDynamicTriangleStep2_3();
				input.nextStep();
				testDynamicTriangleStep3_4();
				input.nextStep();
				testDynamicTriangleStep4();
				input.end();
			} catch (IOException e) {
				e.printStackTrace();
				fail("Should not happen !");
			}
		}
	}

	/**
	 * Create a simple undirected graph triangle (A--B--C--A).
	 */
	protected void createUndirectedTriangle() {
		outGraph.addNode("A");
		outGraph.addNode("B");
		outGraph.addNode("C");
		outGraph.addEdge("AB", "A", "B", false);
		outGraph.addEdge("BC", "B", "C", false);
		outGraph.addEdge("CA", "C", "A", false);
	}

	/**
	 * Create a directed triangle (A->B--C<-A).
	 */
	protected void createDirectedTriangle() {
		outGraph.addNode("A");
		outGraph.addNode("B");
		outGraph.addNode("C");
		outGraph.addEdge("AB", "A", "B", true);
		outGraph.addEdge("BC", "B", "C", false);
		outGraph.addEdge("CA", "A", "C", true);
	}

	protected void createAttributedTriangle() {
		outGraph.addNode("A");
		outGraph.addNode("B");
		outGraph.addNode("C");
		outGraph.addEdge("AB", "A", "B", true);
		outGraph.addEdge("BC", "B", "C", false);
		outGraph.addEdge("CA", "A", "C", true);
		outGraph.setAttribute("a", 1);
		outGraph.setAttribute("b", "foo");
		outGraph.getNode("A").setAttribute("a", 1);
		outGraph.getNode("B").setAttribute("b", "foo");
		outGraph.getNode("C").setAttribute("c", "bar");
	}

	protected void testUndirectedTriangle() {
		assertEquals(3, inGraph.getNodeCount());
		assertEquals(3, inGraph.getEdgeCount());

		Node A = inGraph.getNode("A");
		Node B = inGraph.getNode("B");
		Node C = inGraph.getNode("C");

		assertNotNull(A);
		assertNotNull(B);
		assertNotNull(C);

		if (formatHandlesEdgesIDs) {
			assertNotNull(inGraph.getEdge("AB"));
			assertNotNull(inGraph.getEdge("BC"));
			assertNotNull(inGraph.getEdge("CA"));
		}

		assertTrue(A.hasEdgeToward("B"));
		assertTrue(B.hasEdgeToward("C"));
		assertTrue(C.hasEdgeToward("A"));
		assertTrue(A.hasEdgeToward("C"));
		assertTrue(B.hasEdgeToward("A"));
		assertTrue(C.hasEdgeToward("B"));
	}

	protected void testDirectedTriangle() {
		assertEquals(3, inGraph.getNodeCount());
		assertEquals(3, inGraph.getEdgeCount());

		Node A = inGraph.getNode("A");
		Node B = inGraph.getNode("B");
		Node C = inGraph.getNode("C");

		assertNotNull(A);
		assertNotNull(B);
		assertNotNull(C);

		assertTrue(A.hasEdgeToward("B"));
		assertTrue(A.hasEdgeToward("C"));
		assertFalse(B.hasEdgeToward("A"));
		assertTrue(B.hasEdgeToward("C"));
		assertFalse(C.hasEdgeToward("A"));
		assertTrue(C.hasEdgeToward("B"));

		Edge AB = A.getEdgeToward("B");
		Edge BC = B.getEdgeToward("C");
		Edge CA = A.getEdgeToward("C");

		assertTrue(AB.isDirected());
		assertFalse(BC.isDirected());
		assertTrue(CA.isDirected());
	}

	protected void testAttributedTriangle() {
		assertEquals(3, inGraph.getNodeCount());
		assertEquals(3, inGraph.getEdgeCount());

		Node A = inGraph.getNode("A");
		Node B = inGraph.getNode("B");
		Node C = inGraph.getNode("C");

		assertNotNull(A);
		assertNotNull(B);
		assertNotNull(C);

		assertEquals(1.0, ((Number) inGraph.getAttribute("a")).doubleValue(), 1E-12);
		assertEquals("foo", inGraph.getAttribute("b"));

		assertEquals(1.0, ((Number) A.getAttribute("a")).doubleValue(), 1E-12);
		assertEquals("foo", B.getAttribute("b"));
		assertEquals("bar", C.getAttribute("c"));
	}

	protected void testDynamicTriangleStep0() {
		assertEquals(0, inGraph.getNodeCount());
		assertEquals(0, inGraph.getEdgeCount());
	}

	protected void testDynamicTriangleStep0_1() {
		assertEquals(3, inGraph.getNodeCount());
		assertEquals(0, inGraph.getEdgeCount());

		Node A = inGraph.getNode("A");
		Node B = inGraph.getNode("B");
		Node C = inGraph.getNode("C");

		assertNotNull(A);
		assertNotNull(B);
		assertNotNull(C);

		assertEquals(0, A.getAttributeCount());
		assertEquals(0, B.getAttributeCount());
		assertEquals(0, C.getAttributeCount());
	}

	protected void testDynamicTriangleStep1_2() {
		assertEquals(3, inGraph.getNodeCount());
		assertEquals(3, inGraph.getEdgeCount());

		Node A = inGraph.getNode("A");
		Node B = inGraph.getNode("B");
		Node C = inGraph.getNode("C");

		assertNotNull(A);
		assertNotNull(B);
		assertNotNull(C);

		assertTrue(A.hasEdgeToward("B"));
		assertTrue(A.hasEdgeToward("C"));
		assertTrue(B.hasEdgeToward("A"));
		assertTrue(B.hasEdgeToward("C"));
		assertTrue(C.hasEdgeToward("A"));
		assertTrue(C.hasEdgeToward("B"));
	}

	protected void testDynamicTriangleStep2_3() {
		assertEquals(3, inGraph.getNodeCount());
		assertEquals(3, inGraph.getEdgeCount());

		Node A = inGraph.getNode("A");
		Node B = inGraph.getNode("B");
		Node C = inGraph.getNode("C");

		assertNotNull(A);
		assertNotNull(B);
		assertNotNull(C);

		assertTrue(inGraph.hasAttribute("a"));
		assertTrue(inGraph.hasAttribute("b"));
		assertTrue(A.hasAttribute("a"));
		assertTrue(B.hasAttribute("b"));
		assertTrue(C.hasAttribute("c"));

		assertEquals(Integer.valueOf(1), inGraph.getAttribute("a"));
		assertEquals("foo", inGraph.getAttribute("b"));
		assertEquals(Integer.valueOf(1), A.getAttribute("a"));
		assertEquals("foo", B.getAttribute("b"));
		assertEquals("bar", C.getAttribute("c"));
	}

	protected void testDynamicTriangleStep3_4() {
		assertEquals(2, inGraph.getNodeCount());
		assertEquals(1, inGraph.getEdgeCount());

		Node A = inGraph.getNode("A");
		Node B = inGraph.getNode("B");
		Node C = inGraph.getNode("C");

		assertNull(A);
		assertNotNull(B);
		assertNotNull(C);

		assertFalse(B.hasEdgeToward("A"));
		assertTrue(B.hasEdgeToward("C"));
		assertFalse(C.hasEdgeToward("A"));
		assertTrue(C.hasEdgeToward("B"));
	}

	protected void testDynamicTriangleStep4() {
		assertEquals(2, inGraph.getNodeCount());
		assertEquals(0, inGraph.getEdgeCount());

		Node A = inGraph.getNode("A");
		Node B = inGraph.getNode("B");
		Node C = inGraph.getNode("C");

		assertNull(A);
		assertNotNull(B);
		assertNotNull(C);

		assertFalse(B.hasEdgeToward("A"));
		assertFalse(B.hasEdgeToward("C"));
		assertFalse(C.hasEdgeToward("A"));
		assertFalse(C.hasEdgeToward("B"));
	}
}