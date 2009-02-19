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

package org.miv.graphstream.io.test;

import java.io.IOException;

import org.miv.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.miv.graphstream.algorithm.generator.Generator;
import org.miv.graphstream.algorithm.layout2.LayoutAlgorithm;
import org.miv.graphstream.algorithm.layout2.elasticbox.ElasticBox;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.implementations.DefaultGraph;
import org.miv.graphstream.io.GraphWriterHelper;

public class TestGraphWriterSVG2
{
	public static void main( String args[] )
	{
		new TestGraphWriterSVG2();
	}
	
	public TestGraphWriterSVG2()
	{
		// Generate a cool graph (and start layout during generation to speed up things).
		
		System.out.printf( "Graph generation ..." );
		
		Graph graph = new DefaultGraph( "bar", false, true );
		Generator generator = new DorogovtsevMendesGenerator();
		LayoutAlgorithm layout = new LayoutAlgorithm();
		
		long t1 = System.currentTimeMillis();
		layout.begin( graph, new ElasticBox() );
		generator.begin( graph );
		for( int i=0; i<500; i++ )
		{
			generator.nextElement();
			layout.step();
			layout.step();
			layout.step();
		}
		generator.end();
		long t2 = System.currentTimeMillis();
		System.out.printf( " (%f seconds)%n", (t2-t1)/1000f );

		// Finish the layout the graph.
		
		System.out.printf( "Layout ..." );
		t1 = System.currentTimeMillis();
		
		layout.stepUntilStabilized( 10000 );
		layout.end();
		
		t2 = System.currentTimeMillis();
		System.out.printf( " (%f seconds)%n", (t2-t1)/1000f );
		
		// Write the graph as SVG.
		
		System.out.printf( "Output as SVG ...%n" );
		
		GraphWriterHelper out = new GraphWriterHelper( graph );

		try
        {
	        out.write( "bar.svg" );
        }
        catch( IOException e )
        {
	        e.printStackTrace();
        }
        
        System.out.printf( "OK, all went well...%n" );
	}
}