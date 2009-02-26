/*
 * This file is part of GraphStream.
 * 
 * GraphStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * GraphStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with GraphStream.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.miv.graphstream.ui.graphicGraph.stylesheet;


/**
 * A value and the units of the value.
 * 
 * <p>
 * As a style sheet may express values in several different units. This class purpose is to
 * pack the value and the units it is expressed in into a single object.
 * </p>
 */
public class Value extends Number
{
// Attributes
	
    private static final long serialVersionUID = 1L;

	/**
	 * The value.
	 */
	public float value;
	
	/**
	 * The value units.
	 */
	public Style.Units units;
	
// Constructor
	
	/**
	 * New value.
	 * @param value The value.
	 * @param units The value units.
	 */
	public Value( float value, Style.Units units )
	{
		this.value = value;
		this.units = units;
	}
	
	/**
	 * New copy of another value.
	 * @param other The other value to copy.
	 */
	public Value( Value other )
	{
		this.value = other.value;
		this.units = other.units;
	}

	@Override
    public double doubleValue()
    {
	    return value;
    }

	@Override
    public float floatValue()
    {
	    return value;
    }

	@Override
    public int intValue()
    {
	    return Math.round( value );
    }

	@Override
    public long longValue()
    {
	    return Math.round( value );
    }
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append( value );
		
		switch( units )
		{
			case GU:       builder.append( "gu" ); break;
			case PX:       builder.append( "px" ); break;
			case PERCENTS: builder.append( "%" );  break;
			default:       builder.append( "wtf (what's the fuck?)" ); break;
		}
		
		return builder.toString();
	}
}
