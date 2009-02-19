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

import org.miv.graphstream.algorithm.generator.Generator;
import org.miv.graphstream.algorithm.generator.GridGenerator;

/**
 * Test the elastic box with a simple 5x5 grid.
 * 
 * @author Antoine Dutot
 */
public class TestElasticBoxGrid extends TestElasticBoxFile
{
	public static void main( String args[] )
	{
		new TestElasticBoxGrid( args );
	}

	public TestElasticBoxGrid( String[] args )
    {
	    init( args, 1 );
	    testGenerator();
    }

	protected void testGenerator()
	{
		int i = 0;
		int max = 30;

		layout.setForce( 0.1f );
		Generator generator = new GridGenerator();
		generator.begin( graph );
		loop = true;
		
		while( loop )
		{
			if( i < max )
			{
				generator.nextElement();
				i++;
			}
			else if( i == max )
			{
				generator.end();
			}
	
			maxForce = 0;
			viewer.pumpEvents();
			layout.compute();
			colorSprites();
			sleep( 1 );
		}
	}
}