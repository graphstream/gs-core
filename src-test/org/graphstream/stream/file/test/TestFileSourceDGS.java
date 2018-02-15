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
 * @since 2009-04-22
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file.test;

import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.file.FileSourceDGS;
import org.junit.*;

/**
 * Test the file input in DGS format.
 */
public class TestFileSourceDGS extends TestFileSourceBase {
	// Before

	@Before
	public void setUp() {
		graph = new MultiGraph("g1");
		input = new FileSourceDGS();
	}

	// Test

	@Override
	public String anUndirectedTriangle() {
		return TEST1_TRIANGLE;
	}

	protected static String TEST1_TRIANGLE = "DGS004\n" + "\"test1\" 0 0\n" + "an A\n" + "an B\n" + "an C\n"
			+ "ae AB A B\n" + "ae BC B C\n" + "ae CA C A\n";

	@Override
	public String aDirectedTriangle() {
		return TEST2_DIRECTED_TRIANGLE;
	}

	protected static String TEST2_DIRECTED_TRIANGLE = "DGS004\n" + "\"test2\" 0 0\n" + "an A\n" + "an B\n" + "an C\n"
			+ "ae AB A > B\n" + "ae BC B C\n" + "ae CA C < A\n";

	@Override
	public String basicAttributes() {
		return TEST3_ATTRIBUTES;
	}

	protected static String TEST3_ATTRIBUTES = "DGS004\n" + "\"test3\" 0 0\n" + "an A a:1 b:\"truc\" c:\"true\"\n"
			+ "an B aa:1,2,3,4 bb:foo cc:bar\n" + "an C aaa=1.234\n" + "ae AB A B\n" + "ae BC B C\n" + "ae CA C A\n";

	@Override
	public String anUndirectedTriangleFileName() {
		return "src-test/org/graphstream/stream/file/test/data/undirectedTriangle.dgs";
	}

	@Override
	public String anUndirectedTriangleHttpURL() {
		return "http://graphstream-project.org/media/data/undirectedTriangle.dgs";
	}
}