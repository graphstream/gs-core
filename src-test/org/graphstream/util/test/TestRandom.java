/*
 * Copyright 2006 - 2011 
 *     Stefan Balev 	<stefan.balev@graphstream-project.org>
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
 * 
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
package org.graphstream.util.test;

import org.graphstream.util.Random;

/**
 * Tests The {@link Random} class.
 * @author yoann
 * 
 */
public class TestRandom
{
	public static void main(String[] str)
	{
		System.out.printf("--------------%n");
		Random.reinit(23);		
		System.out.printf( "3 random numbers : %1.4f %1.4f and %1.4f%n", (float)Random.next(), (float)Random.next(), (float)Random.next() );
		System.out.printf( "reinit the seed%n");
		Random.reinit();
		System.out.printf( "3 random numbers : %1.4f %1.4f and %1.4f%n", (float)Random.next(), (float)Random.next(), (float)Random.next() );
		

		System.out.printf("%n--------------%n");
		System.out.printf( "A multi-thread try%n");
		for(int i = 0; i< 2; i++)
		{
			Runnable t = new MyThread();
			( (Thread) t ).start();
		}	
	}
}
	class MyThread extends Thread
	{
		@Override
        public void run()
		{
			double id = Random.next();
			for(int i=0; i<5; i++)
			{
				yield();
				System.out.printf( "thread %3.0f  -> 3 random numbers : %1.4f %1.4f and %1.4f%n", (float)(id*1000.0), (float)Random.next(), (float)Random.next(), (float)Random.next() );
			}
		}
	}
