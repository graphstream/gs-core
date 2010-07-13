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
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.ui.graphicGraph.stylesheet;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Several values and the units of these values.
 * 
 * <p>
 * As a style sheet may express values in several different units. This class purpose is to
 * pack the value and the units it is expressed in into a single object.
 * </p>
 */
public class Values implements Iterable<Float>
{
// Attributes
	
	/**
	 * The value.
	 */
	public ArrayList<Float> values = new ArrayList<Float>();
	
	/**
	 * The values units.
	 */
	public Style.Units units;
	
// Constructor
	
	/**
	 * New value set with one initial value.
	 * @param units The values units.
	 * @param values A variable count of values.
	 */
	public Values( Style.Units units, float...values )
	{
		this.units = units;
		
		for( float value: values )
			this.values.add( value );
	}
	
	/**
	 * New copy of another value set.
	 * @param other The other values to copy.
	 */
	public Values( Values other )
	{
		this.values = new ArrayList<Float>( other.values );
		this.units  = other.units;
	}
	
	/**
	 * New set of one value.
	 * @param value The value to copy with its units.
	 */
	public Values( Value value )
	{
		this.values = new ArrayList<Float>();
		this.units  = value.units;
		
		values.add( value.value );
	}
	
	/**
	 * Number of values in this set.
	 * @return The number of values.
	 */
	public int size()
	{
		return values.size();
	}

	/**
	 * Number of values in this set.
	 * @return The number of values.
	 */
	public int getValueCount()
	{
		return values.size();
	}
	
	/**
	 * The i-th value of this set. If the index is less than zero, the first value is given,
	 * if the index if greater or equal to the number of values, the last value is given.
	 * @param i The value index.
	 * @return The corresponding value.
	 */
	public float get( int i )
	{
		if( i < 0 )
		     return values.get( 0 );
		else if( i >= values.size() )
		     return values.get( values.size()-1 );
		else return values.get( i );
	}
	
	/**
	 * Values units.
	 * @return The units used for each value.
	 */
	public Style.Units getUnits()
	{
		return units;
	}
	
	@Override
	public boolean equals( Object o )
	{
		if( o != this )
		{
			if( ! ( o instanceof Values ) )
				return false;
			
			Values other = (Values)o;
			
			if( other.units != units )
				return false;

			int n = values.size();

			if( other.values.size() != n )
				return false;
			
			for( int i=0; i<n; i++ )
			{
				if( ! other.values.get( i ).equals( values.get( i ) ) )
					return false;
			}
		}
		
		return true;
	}
	
	public Iterator<Float> iterator()
	{
		return values.iterator();
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append( '(' );
		for( float value: values )
		{
			builder.append( ' ' );
			builder.append( value );
		}
		builder.append( " )" );
		
		switch( units )
		{
			case GU:       builder.append( "gu" ); break;
			case PX:       builder.append( "px" ); break;
			case PERCENTS: builder.append( "%" );  break;
			default:       builder.append( "wtf (what's the fuck?)" ); break;
		}
		
		return builder.toString();
	}

	/**
	 * Copy the given values to this set. The units are also copied.
	 * @param values The values to copy.
	 */
	public void copy( Values values )
	{
		units = values.units;
		this.values.clear();
		this.values.addAll( values.values );
	}
	
	/**
	 * Append the given set of values at the end of this set.
	 * @param values The value set to append.
	 */
	public void addValues( float...values )
	{
		for( float value: values )
			this.values.add( value );
	}
	
	/**
	 * Insert the given value at the given index.
	 * @param i Where to insert the value.
	 * @param value The value to insert.
	 */
	public void insertValue( int i, float value )
	{
		values.add( i, value );
	}
	
	/**
	 * Change the i-th value.
	 * @param i The value index.
	 * @param value The value to put.
	 */
	public void setValue( int i, float value )
	{
		values.set( i, value );
	}
	
	/**
	 * Remove the i-th value.
	 * @param i The index at which the value is to be removed.
	 */
	public void removeValue( int i )
	{
		values.remove( i );
	}
	
	/**
	 * Change the values units.
	 * @param units The units.
	 */
	public void setUnits( Style.Units units )
	{
		this.units = units;
	}
}