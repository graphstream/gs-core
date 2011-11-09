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
package org.graphstream.util;


/**
 * A simple static class that gives a static method {@link #next()} that returns
 * a randomly generated double value. The advantage of this class in comparison
 * to the Math.random() method is that when the first call to the next() method
 * is made, the object initialises itself with the value of the "ranbomSeed" key
 * if it exists. The Seed can then be reinitialised with its previous value or
 * with another value.
 * 
 * @author Yoann Pigné
 */
public class Random
{
	public static java.util.Random random;

	static int seed = 1239945;

	/**
	 * Static method that returns a random double value between 0 and 1. A
	 * static <code>java.util.Random</code> is created and eventually
	 * initialised with the \"randomSeed\" key in the default
	 * {@link Environment} table.
	 * @return A random double between 0 and 1.
	 */
	public static double next()
	{
		testRandom();
		return random.nextDouble();
	}

	/**
	 * Forces the random object to be reinitialised with the given seed.
	 * @param seed ...
	 */
	public static void reinit( int seed )
	{
		Environment.getGlobalEnvironment().setParameter( "randomSeed",
				"" + seed );
		random = null;
		testRandom();
	}

	/**
	 * Forces the random object to be reinitialised with the same seed.
	 */
	public static void reinit()
	{
		Environment.getGlobalEnvironment().setParameter( "randomSeed",
				"" + seed );
		random = null;
		testRandom();
	}

	/**
	 * Creates a random object if it does not exist and tries to initialise it
	 * with the \"randomSeed\" key in the default environment.
	 */
	private static void testRandom()
	{
		if( random == null )
		{

			String randomSeed = Environment.getGlobalEnvironment()
					.getParameter( "randomSeed" );
			if( randomSeed != null && !randomSeed.equals( "" ) )
			{
				seed = ( new Integer( randomSeed ) ).intValue();
			}
			random = new java.util.Random( seed );
		}
	}
}
