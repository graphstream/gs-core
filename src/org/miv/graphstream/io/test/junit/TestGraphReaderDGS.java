/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.miv.graphstream.io.test.junit;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import org.junit.* ;
import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.implementations.MultiGraph;
import org.miv.graphstream.io.GraphParseException;
import org.miv.graphstream.io.GraphReaderDGS;
import org.miv.graphstream.io.GraphReaderListenerHelper;

import static org.junit.Assert.* ;

/**
 * Basic test for the GraphReader.
 * 
 * @author Antoine Dutot
 */
public class TestGraphReaderDGS
{
	Graph graph;
	
	@Before
	public void setUp()
	{
		graph = new MultiGraph();
	}
	
	@Test
	public void testBase() throws GraphParseException, IOException
	{
		GraphReaderDGS            reader       = new GraphReaderDGS();
		GraphReaderListenerHelper readerHelper = new GraphReaderListenerHelper( graph );
		
		// We can do the above two lines in one : new GraphReaderDGS( graph ); !!
		
		boolean available;
		
		reader.addGraphReaderListener( readerHelper );
		reader.begin( new StringReader( graph1 ) );

		available = reader.nextStep();
		assertTrue( available );
		assertTrue( graph.getNodeCount() == 3 );
		assertTrue( graph.getNode("A") != null );
		assertTrue( graph.getNode("B") != null );
		assertTrue( graph.getNode("C") != null );
		assertTrue( graph.getEdgeCount() == 0 );
		
		available = reader.nextStep();
		assertTrue( available );
		assertTrue( graph.getNodeCount() == 3 );
		assertTrue( graph.getEdge("AB") != null );
		assertTrue( graph.getEdge("BC") != null );
		assertTrue( graph.getEdge("CA") != null );
		assertTrue( graph.getEdgeCount() == 3 );
		
		Edge AB = graph.getEdge( "AB" );
		Edge BC = graph.getEdge( "BC" );
		Edge CA = graph.getEdge( "CA" );
		
		assertTrue( AB.isDirected() == false );
		assertTrue( BC.isDirected() );
		assertTrue( CA.isDirected() );
		assertTrue( BC.getNode0().getId().equals( "B" ) );
		assertTrue( CA.getNode0().getId().equals( "A" ) );
		
		available = reader.nextStep();
		assertTrue( available );
		assertTrue( graph.getNodeCount() == 3 );
		assertTrue( graph.getEdgeCount() == 3 );
		
		Node A = graph.getNode( "A" );
		Node B = graph.getNode( "B" );
		Node C = graph.getNode( "C" );
		
		assertTrue( A.hasAttribute( "foo" ) );
		assertTrue( A.hasAttribute( "bar" ) );
		assertTrue( B.hasAttribute( "foo" ) );
		assertTrue( B.hasAttribute( "bar" ) );
		assertTrue( C.hasAttribute( "foo" ) );
		assertTrue( C.hasAttribute( "bar" ) );
		assertTrue( A.getAttribute( "foo" ).equals( "zoor" ) );
		assertTrue( A.getAttribute( "bar" ).equals( "bluh" ) );
		assertTrue( B.getAttribute( "foo" ).equals( "zoor" ) );
		assertTrue( B.getAttribute( "bar" ).equals( "bluh" ) );
		assertTrue( C.getAttribute( "foo" ).equals( "zoor" ) );
		assertTrue( C.getAttribute( "bar" ).equals( "bluh" ) );
		
		available = reader.nextStep();
		assertTrue( ! available );
		assertTrue(   A.hasAttribute( "bar" ) );
		assertTrue( ! A.hasAttribute( "foo" ) );
		assertTrue(   B.hasAttribute( "bar" ) );
		assertTrue( ! B.hasAttribute( "foo" ) );
		assertTrue(   C.hasAttribute( "bar" ) );
		assertTrue( ! C.hasAttribute( "foo" ) );
	}
	
	public static String graph1 =
		"DGS004\n" +
		"Foo1 0 0\n" +
		// Step 0, add nodes.
		"st 0                                      \n" +
		"an A                                      \n" +
		"an \"B\"                                  \n" +
		"an C                                      \n" +
		// Step 1, add edges, some directed, others not.
		"st 1                                      \n" +
		"ae AB A \"B\"                             \n" +
		"ae \"BC\" \"B\" > \"C\"                   \n" +
		"ae CA C < A                               \n" +
		// Step 2 add a "foo" and "bar attribute on each node.
		"st 2                                      \n" +
		"cn A foo:zoor +bar:bluh                   \n" +
		"cn B +foo:\"zoor\" +\"bar\"=\"bluh\"      \n" +
		"cn C \"foo\":\"zoor\" \"bar\"=\"bluh\"    \n" +
		// Step 3 remove the "foo" attribute from each node.
		"st 3                                      \n" +
		"cn A -foo                                 \n" +
		"cn B -\"foo\"                             \n" +
		"cn C -foo                                 \n";

	@Test
	public void testAttributeValues() throws GraphParseException, IOException
	{
		GraphReaderDGS            reader       = new GraphReaderDGS();
		GraphReaderListenerHelper readerHelper = new GraphReaderListenerHelper( graph );
		
		reader.addGraphReaderListener( readerHelper );
		reader.begin( new StringReader( graph2 ) );

		boolean available = reader.nextStep();

		assertTrue( ! available );
		assertTrue( graph.getNodeCount() == 3 );
		assertTrue( graph.getEdgeCount() == 3 );
		
		Node A  = graph.getNode( "A" );
		Node B  = graph.getNode( "B" );
		Node C  = graph.getNode( "C" );
		Edge AB = graph.getEdge( "AB" );
		Edge BC = graph.getEdge( "BC" );
		Edge CA = graph.getEdge( "CA" );
		
		assertTrue( A  != null && B  != null && C  != null );
		assertTrue( AB != null && BC != null && CA != null );
		
		assertTrue( A.getAttributeCount() == 1 );
		assertTrue( B.getAttributeCount() == 1 );
		assertTrue( C.getAttributeCount() == 3 );
		
		assertTrue( AB.getAttributeCount() == 1 );
		assertTrue( BC.getAttributeCount() == 1 );
		assertTrue( CA.getAttributeCount() == 1 );
		
		assertTrue( A.getAttribute("a").equals( "floc" ) );
		assertTrue( A.getAttribute("a") instanceof String );
		assertTrue( A.hasLabel( "a" ) );
		assertTrue( A.getLabel("a").equals( "floc" ) );
		
		assertTrue( B.getAttribute("b").equals( "broc" ) );
		assertTrue( B.getAttribute("b") instanceof String );
		assertTrue( B.hasLabel( "b" ) );
		assertTrue( B.getLabel("b").equals( "broc" ) );
		
		assertTrue( C.getAttribute("n") instanceof Number );
		assertTrue( C.getAttribute("m") instanceof Number );
		assertTrue( ((Number)C.getAttribute("n")).floatValue() == 1 );
		assertTrue( ((Number)C.getAttribute("m")).floatValue() == 2 );
		assertTrue( C.hasNumber( "n" ) );
		assertTrue( C.hasNumber( "m" ) );
		assertTrue( C.getNumber("n") == 1 );
		assertTrue( C.getNumber("m") == 2 );
		assertTrue( C.getAttribute( "o" ) instanceof Object[] );
		assertTrue( C.hasArray( "o" ) );
		
		Object[] o = C.getArray( "o" );
		
		assertTrue( o != null );
		assertTrue( o[0].equals( "a" ) );
		assertTrue( o[1].equals( "b" ) );
		assertTrue( o[2].equals( "c d" ) );
		assertTrue( o[3] instanceof Number );
		assertTrue( o[4] instanceof Number );
		assertTrue( o[5] instanceof Number );
		assertTrue( ((Number)o[3]).floatValue() == 1 );
		assertTrue( ((Number)o[4]).floatValue() == 2 );
		assertTrue( ((Number)o[5]).floatValue() == 3 );
		
		assertTrue( BC.getAttributeCount() == 1 );
		assertTrue( BC.getAttribute( "w" ) instanceof String );
		assertTrue( BC.getAttribute("w").equals( "thing" ) );
		assertTrue( BC.hasLabel( "w" ) );
		
		assertTrue( CA.getAttributeCount() == 1 );
		assertTrue( CA.getAttribute( "w" ) instanceof String );
		assertTrue( CA.getAttribute("w").equals( "thing" ) );
		assertTrue( CA.hasLabel( "w" ) );
	}
	
	public static String graph2 =
		"DGS004\n" +
		"Foo2 0 0\n" +
		//
		"st 0                                             \n" +
		"an A         a:floc                              \n" +
		"an B         b=broc                              \n" +
		"an C         n:1   m:2   o:a,b,\"c d\",1,2,3     \n" +
		"ae AB A B    w:\"multi word attribute\"          \n" +
		"ae BC B C    +w:thing                            \n" +
		"ae CA C A    \"w\":\"thing\"                     \n" ;
	
	@Test
	@SuppressWarnings("all")
	public void testHashesAttributeValues() throws GraphParseException, IOException
	{
		GraphReaderDGS            reader       = new GraphReaderDGS();
		GraphReaderListenerHelper readerHelper = new GraphReaderListenerHelper( graph );
		
		reader.addGraphReaderListener( readerHelper );
		reader.begin( new StringReader( graph3 ) );

		boolean available = reader.nextStep();

		assertTrue( ! available );
		assertTrue( graph.getNodeCount() == 3 );
		
		Node A = graph.getNode( "A" );
		Node B = graph.getNode( "B" );
		Node C = graph.getNode( "C" );
		
		assertTrue( A != null && B != null && C != null );
		
		assertTrue( A.getAttributeCount() == 1 );
		assertTrue( B.getAttributeCount() == 1 );
		assertTrue( C.getAttributeCount() == 1 );
		assertTrue( A.hasHash( "a" ) );
		assertTrue( B.hasHash( "b" ) );
		assertTrue( C.hasHash( "c" ) );
		
		HashMap<?,?> a = A.getHash( "a" );
		HashMap<?,?> b = B.getHash( "b" );
		HashMap<?,?> c = C.getHash( "c" );
		
		assertTrue( a != null && b != null && c != null );
		assertTrue( a.size() == 3 );
		assertTrue( a.containsKey( "u" ) );
		assertTrue( a.containsKey( "v" ) );
		assertTrue( a.containsKey( "w" ) );
		assertTrue( a.get("u") instanceof String );
		assertTrue( a.get("v") instanceof String );
		assertTrue( a.get("w") instanceof String );
		assertTrue( a.get("u").equals( "floc" ) );
		assertTrue( a.get("v").equals( "bloc" ) );
		assertTrue( a.get("w").equals( "groc" ) );
		
		assertTrue( b.size() == 3 );
		assertTrue( b.containsKey( "i" ) );
		assertTrue( b.containsKey( "j" ) );
		assertTrue( b.containsKey( "k" ) );
		assertTrue( b.get("i") instanceof String );
		assertTrue( b.get("j") instanceof Object[] );
		assertTrue( b.get("k") instanceof Number );
		assertTrue( b.get("i").equals( "broc" ) );
		assertTrue( ((Number)b.get("k")).floatValue() == 1 );
		
		Object[] o = (Object[]) b.get("j");
		
		assertTrue( o.length == 3 );
		assertTrue( o[0] instanceof String );
		assertTrue( o[1] instanceof String );
		assertTrue( o[2] instanceof String );
		assertTrue( o[0].equals( "a" ) );
		assertTrue( o[1].equals( "b" ) );
		assertTrue( o[2].equals( "c" ) );
		
		assertTrue( c.size() == 1 );
		assertTrue( c.containsKey( "a" ) );
		assertTrue( c.get("a") instanceof HashMap<?,?> );
		
		HashMap<String,Object> h = (HashMap<String,Object>)c.get( "a" );
		
		assertTrue( h != null );
		assertTrue( h.size() == 2 );
		assertTrue( h.get( "i" ) instanceof Number );
		assertTrue( h.get( "j" ) instanceof Number );
		assertTrue( ((Number)h.get( "i" )).floatValue() == 1 );
		assertTrue( ((Number)h.get( "j" )).floatValue() == 2 );
	}
	
	public static String graph3 = 
		"DGS004\n" +
		"Foo3 0 0\n" +
		//
		"st 0                                       \n" +
		"an A         a:[u:floc  v:bloc  w:groc]    \n" +
		"an B         b=[i:broc  j:a,b,c  k:1]      \n" +
		"an C         c:[a:[i:1 j:2]]               \n" ;
}