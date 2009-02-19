package org.miv.graphstream.io.test;

import java.io.*;
import java.util.*;
import org.miv.graphstream.graph.*;
import org.miv.graphstream.graph.implementations.DefaultGraph;
import org.miv.graphstream.io.*;

public class TestGraphWriter
{
	public static void main( String args[] )
	{
		new TestGraphWriter();
	}
	
	public TestGraphWriter()
	{
		testGraphWriterHelper();
		testGraphWriter();
		testGraphWriter2();
	}
	
	public void testGraphWriterHelper()
	{
		// This test is mostly here to see if the automatic attribute
		// filter selector works.
		
		Graph graph = new DefaultGraph( "", false, true );
		
		graph.addEdge( "AB", "A", "B" );
		graph.addEdge( "BC", "B", "C" );
		graph.addEdge( "CA", "C", "A" );
		
		graph.getNode( "A" ).addAttribute( "label", "A" );
		graph.getNode( "B" ).addAttribute( "label", "B" );
		graph.getNode( "C" ).addAttribute( "label", "C" );

		graph.getEdge( "AB" ).addAttribute( "width", "2" );
		graph.getEdge( "BC" ).addAttribute( "width", "3" );
		graph.getEdge( "CA" ).addAttribute( "width", "4" );

		graph.getNode( "A" ).addAttribute( "value", 1 );
		graph.getNode( "B" ).addAttribute( "value", 2 );
		graph.getNode( "C" ).addAttribute( "value", 3 );

		ArrayList<Integer> av = new ArrayList<Integer>();
		ArrayList<Integer> bv = new ArrayList<Integer>();
		
		av.add( 4 );
		av.add( 5 );
		av.add( 6 );
		
		graph.getNode( "A" ).addAttribute( "array", av );
		graph.getNode( "B" ).addAttribute( "array", bv );
		graph.getNode( "C" ).addAttribute( "array", bv );

		// Should be removed from the automatic attribute output since the
		// array is empty.
		
		ArrayList<Object> cv = new ArrayList<Object>();
		
		graph.getNode( "A" ).addAttribute( "strange", cv );
		graph.getNode( "B" ).addAttribute( "strange", cv );
		graph.getNode( "C" ).addAttribute( "strange", cv );
		
		// Should be removed from the automatic attribute output since the
		// class will not match.
		
		graph.getNode( "A" ).addAttribute( "fun", "a" );
		graph.getNode( "B" ).addAttribute( "fun", new Integer( 2 ) );
		graph.getNode( "C" ).addAttribute( "fun", "c" );
		
		// Should be removed from the automatic attribute output since the
		// it is not present on all nodes.
		
		graph.getNode( "A" ).addAttribute( "alone", "alone" );
		
		// Output !
		
		GraphWriterHelper gwh = new GraphWriterHelper( graph );
		
		try
		{
			gwh.write( "testGraphWriterHelper1.dgs" );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
	
	public void testGraphWriter()
	{
		
	}
	
	public void testGraphWriter2()
	{
		
	}
}