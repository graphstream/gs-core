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
package org.graphstream.util.set;

import java.util.NoSuchElementException;

/**
 * Array list with immutable element indices, that contains only integers.
 * 
 * <p>A fixed array list is like an array list, but it ensures the property that
 * each element will always stay at the same index, even if elements are
 * removed in between. The counterpart of this property is that the array
 * handles by itself the insertion of new elements (since when an element is
 * removed in the middle, this position can be reused), and therefore indices
 * cannot be chosen (i.e. only the {@link #add(int)} method are usable to insert new elements in the
 * array).</p>
 * 
 * <p>This is the reason why this does not implement the List interface, because
 * the add(int,E) method cannot be implemented.</p>
 * 
 * <p>Furthermore, this array cannot contain the NULL_VALUE, because it marks
 * unused positions within the array using it.</p>
 * 
 * @author Antoine Dutot
 * @since 20040912
 */
public class FixedIntArrayList
{
// Attributes

	public static final int NULL_VALUE = Integer.MIN_VALUE;
	
	/**
	 * List of elements.
	 */
	protected IntArray elements = new IntArray();

	/**
	 * List of free indices.
	 */
	protected IntArray freeIndices = new IntArray();

	/**
	 * Last inserted element index.
	 */
	protected int lastIndex = -1;

// Constructors

	public
	FixedIntArrayList()
	{
		elements    = new IntArray();
		freeIndices = new IntArray();
	}

	public
	FixedIntArrayList( int capacity )
	{
		elements    = new IntArray( capacity );
		freeIndices = new IntArray( 16 );
	}

// Accessors

	/**
	 * Number of elements in the array.
	 * @return The number of elements in the array.
	 */
	public int
	size()
	{
		return elements.size() - freeIndices.size();
	}

	/**
	 * Real size of the array, counting elements that have been erased.
	 * @see #unsafeGet(int)
	 */
	public int
	realSize()
	{
		return elements.size();
	}

	public boolean
	isEmpty()
	{
		return( size() == 0 );
	}

	/**
	 * I-th element.
	 * @param i The element index.
	 * @return The element at index <code>i</code>.
	 */
	public int
	get( int i )
	{
		int value = elements.get( i );

		if( value == NULL_VALUE )
			throw new NoSuchElementException( "no element at index " + i );

		return value;
	}

	/**
	 * I-th element. Like the {@link #get(int)} method but it does not check
	 * the element does not exists at the given index.
	 * @param i The element index.
	 * @return The element at index <code>i</code>.
	 */
	public int
	unsafeGet( int i )
	{
		return elements.get( i );
	}

	@Override
    public boolean
	equals( Object o )
	{
		if( o instanceof FixedIntArrayList )
		{
			FixedIntArrayList other = (FixedIntArrayList) o;

			int n = size();

			if( other.size() == n )
			{
				for( int i=0; i<n; ++i )
				{
					int e0 = elements.get( i );
					int e1 = other.elements.get( i );

					if( e0 != e1 )
					{
						if( e0 == NULL_VALUE && e1 != NULL_VALUE )
							return false;

						if( e0 != NULL_VALUE && e1 == NULL_VALUE )
							return false;

						if( ! ( e0 == e1 ) )
							return false;
					}
				}

				return true;
			}
		}

		return false;
	}

	public java.util.Iterator<Integer>
	iterator()
	{
		return new FixedIntArrayIterator();
	}

	/**
	 * Last index used by the {@link #add(int)} method.
	 * @return The last insertion index.
	 */
	public int
	getLastIndex()
	{
		return lastIndex;
	}
	
	/**
	 * The index that will be used in case of a next insertion in this array.
	 * @return
	 */
	public int
	getNextAddIndex()
	{
		int n = freeIndices.size();
		
		if( n > 0 )
		     return freeIndices.get( n - 1 );
		else return elements.size();
	}

	public int[]
	toArray()
	{
		int n = size();
		int m = elements.size();
		int j = 0;
		int a[] = new int[n];

		for( int i=0; i<m; ++i )
		{
			int e = elements.get( i );

			if( e != NULL_VALUE )
				a[j++] = e;
		}

		assert( j == n );

		return a;
	}

	public <T> T[]
	toArray( T[] a )
	{
		// TODO
		throw new RuntimeException( "not implemented yet" );
	}

// Commands

	/**
	 * Add one <code>element</code> in the array. The index used for inserting
	 * the element is then available using {@link #getLastIndex()}.
	 * @see #getLastIndex()
	 * @param element The element to add.
	 * @return Always true.
	 * @throws NullPointerException If a null value is inserted.
	 */
	public boolean
	add( int element )
	{
		if( element == NULL_VALUE )
			throw new java.lang.NullPointerException( "this array cannot contain null value" );

		int n = freeIndices.size();

		if( n > 0 )
		{
			int i = freeIndices.get(n - 1 );
			freeIndices.prune( n - 1 );
			elements.set( i, element );
			lastIndex = i;
		}
		else
		{
			elements.add( element );
			lastIndex = elements.size() - 1;
		}

		return true;
	}

	/**
	 * Remove the element at index <code>i</code>.
	 * @param i Index of the element to remove.
	 * @return The removed element.
	 */
	public int
	remove( int i )
	{
		int n = elements.size();

		if( i < 0 || i >= n )
			throw new ArrayIndexOutOfBoundsException( "index "+i+" does not exist" );

		if( n > 0 )
		{
			if( elements.get( i ) == NULL_VALUE )
				throw new NullPointerException( "no element stored at index " + i );

			if( i == ( n - 1 ) )
			{
				int value = elements.get( i );
				elements.prune( i );
				return value;
			}
			else
			{
				int e = elements.get( i );
				elements.set( i, NULL_VALUE );
				freeIndices.add( i );
				return e;
			}
		}

		throw new ArrayIndexOutOfBoundsException( "index "+i+" does not exist" );
	}

	protected void
	removeIt( int i )
	{
		remove( i );
	}

	public void
	clear()
	{
		elements    = new IntArray();
		freeIndices = new IntArray();
	}

// Nested classes

protected class FixedIntArrayIterator
	implements java.util.Iterator<Integer>
{
	int i;

	public
	FixedIntArrayIterator()
	{
		i = -1;
	}

	public boolean
	hasNext()
	{
		int n = elements.size();

		for( int j=i+1; j<n; ++j )
		{
			if( elements.get( j ) != NULL_VALUE )
				return true;
		}

		return false;
	}

	public Integer
	next()
	{
		int n = elements.size();

		for( int j=i+1; j<n; ++j )
		{
			int e = elements.get( j );

			if( e != NULL_VALUE )
			{
				i = j;
				return e;
			}
		}

		throw new NoSuchElementException( "no more elements in iterator" );
	}

	public void
	remove()
		throws UnsupportedOperationException
	{
		if( i >= 0 && i < elements.size() && elements.get( i ) != NULL_VALUE )
		{
			removeIt( i );	// A parent class method cannot be called if it has
							// the same name as one in the inner class
							// (normal), but even if they have distinct
							// arguments types. Hence this strange removeIt()
							// method...
		}
		else
		{
			throw new IllegalStateException( "no such element" );
		}
	}
}

}