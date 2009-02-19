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

package org.miv.graphstream.algorithm.layout2.elasticbox.test;

public class TestElasticBoxTriangle extends TestElasticBoxFile
{
	public static void main( String args[] )
	{
		new TestElasticBoxTriangle( args );
	}
	
	public TestElasticBoxTriangle( String args[] )
	{
		init( args, 1 );
		testTriangle();
	}
	
	protected void testTriangle()
	{
		graph.addEdge( "AB", "A", "B" );
		graph.addEdge( "BC", "B", "C" );
		graph.addEdge( "CA", "C", "A" );
		layout.setForce( 0.1f );
		loop = true;
		
		while( loop )
		{
			maxForce = 0;
			layout.compute();
			colorSprites();
			sleep( 1 );
		}
	}
}
