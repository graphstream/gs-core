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
 * Copyright 2006 - 2010
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.stream.file;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Locale;

import org.graphstream.graph.CompoundAttribute;

/**
 * File output for the DGS (Dynamic Graph Stream) file format.
 */
public class FileSinkDGS
	extends FileSinkBase
{
// Attribute
	
	/**
	 * A shortcut to the output.
	 */
	protected PrintStream out;
	
	protected String graphName = "";
	
// Command
	
	@Override
	protected void outputHeader() throws IOException
	{
		out = (PrintStream) output;
		
		out.printf( "DGS004%n" );
		
		if( graphName.length() <= 0 )
		     out.printf( "null 0 0%n" );
		else out.printf( "\"%s\" 0 0%n", graphName );
	}

	@Override
	protected void outputEndOfFile() throws IOException
	{
		// NOP
	}

	public void edgeAttributeAdded( String graphId, long timeId, String edgeId, String attribute, Object value )
	{
		edgeAttributeChanged( graphId, timeId, edgeId, attribute, null, value );
	}

	public void edgeAttributeChanged( String graphId, long timeId, String edgeId, String attribute,
	        Object oldValue, Object newValue )
	{
		out.printf( "ce \"%s\" %s%n", edgeId, attributeString( attribute, newValue, false ) );
	}

	public void edgeAttributeRemoved( String graphId, long timeId, String edgeId, String attribute )
	{
		out.printf( "ce \"%s\" %s%n", edgeId, attributeString( attribute, null, true ) );
	}

	public void graphAttributeAdded( String graphId, long timeId, String attribute, Object value )
	{
		graphAttributeChanged( graphId, timeId, attribute, null, value );
	}

	public void graphAttributeChanged( String graphId, long timeId, String attribute, Object oldValue,
	        Object newValue )
	{
		out.printf( "cg %s%n", attributeString( attribute, newValue, false ) );
	}

	public void graphAttributeRemoved( String graphId, long timeId, String attribute )
	{
		out.printf( "cg %s%n", attributeString( attribute, null, true ) );
	}

	public void nodeAttributeAdded( String graphId, long timeId, String nodeId, String attribute, Object value )
	{
		nodeAttributeChanged( graphId, timeId, nodeId, attribute, null, value );
	}

	public void nodeAttributeChanged( String graphId, long timeId, String nodeId, String attribute,
	        Object oldValue, Object newValue )
	{
		out.printf( "cn \"%s\" %s%n", nodeId, attributeString( attribute, newValue, false ) );
	}

	public void nodeAttributeRemoved( String graphId, long timeId, String nodeId, String attribute )
	{
		out.printf( "cn \"%s\" %s%n", nodeId, attributeString( attribute, null, true ) );
	}

	public void edgeAdded( String graphId, long timeId, String edgeId, String fromNodeId, String toNodeId,
	        boolean directed )
	{
		out.printf( "ae \"%s\" \"%s\" %s \"%s\"%n", edgeId, fromNodeId, directed ? ">" : "", toNodeId );
	}

	public void edgeRemoved( String graphId, long timeId, String edgeId )
	{
		out.printf( "de \"%s\"%n", edgeId );
	}

	public void graphCleared( String graphId, long timeId )
	{
		out.printf( "clear%n" );
	}

	public void nodeAdded( String graphId, long timeId, String nodeId )
	{
		out.printf( "an \"%s\"%n", nodeId );
	}

	public void nodeRemoved( String graphId, long timeId, String nodeId )
	{
		out.printf( "dn \"%s\"%n", nodeId );
	}

	public void stepBegins( String graphId, long timeId, double step )
	{
		out.printf( Locale.US, "st %f%n", step );
	}
	
// Utility
	
	protected String attributeString( String key, Object value, boolean remove )
	{
		if( key == null || key.length() == 0 )
			return null;
		
		if( remove )
		{
			return String.format( " -\"%s\"", key );
		}
		else
		{
			if( value != null && value.getClass().isArray() )
			{
				Object[] values = (Object[]) value;
				StringBuffer sb = new StringBuffer();
				
				sb.append( String.format( " \"%s\":", key ) );
				
				if( values.length > 0 )
				     sb.append( valueString( values[0] ) );
				else sb.append( "\"\"" );
				
				for( int i=1; i<values.length; ++i )
					sb.append( String.format( ",%s", valueString( values[i] ) ) );
				
				return sb.toString();
			}
			else
			{
				return String.format( " \"%s\":%s", key, valueString( value ) );
			}
		}
	}
	
	protected String valueString( Object value )
	{
		if( value instanceof CharSequence )
		{
			return String.format( "\"%s\"", (CharSequence)value );
		}
		else if( value instanceof Number )
		{
			if( value instanceof Integer || value instanceof Short || value instanceof Byte || value instanceof Long )
			{
				return String.format( Locale.US, "%d", ((Number)value).longValue() );
			}
			else if( value instanceof Float || value instanceof Double )
			{
				return String.format( Locale.US, "%f", ((Number)value).doubleValue() );
			}
			else if( value instanceof Character )
			{
				return String.format( "\"%c\"", ((Character)value).charValue() );
			}
			else if( value instanceof Boolean )
			{
				return String.format( Locale.US, "\"%b\"", ((Boolean)value) );
			}
			else
			{
				return String.format( Locale.US, " %f", ((Number)value).doubleValue() );
			}
		}
		else if( value == null )
		{
			return "\"\"";
		}
		else if( value instanceof Object[] )
		{
			Object array[] = (Object[]) value;
			int    n       = array.length;
			StringBuffer sb = new StringBuffer();
			
			if( array.length > 0 )
				sb.append( valueString( array[0] ) );
			
			for( int i=1; i<n; i++ )
			{
				sb.append( "," );
				sb.append( valueString( array[i] ) );
			}
				
			return sb.toString();
		}
		else if( value instanceof HashMap<?,?> || value instanceof CompoundAttribute )
		{
			HashMap<?,?> hash;
			
			if( value instanceof CompoundAttribute )
			     hash = ((CompoundAttribute)value).toHashMap();
			else hash = (HashMap<?,?>) value;
			
			return hashToString( hash );
		}
		else
		{
			return String.format( "\"%s\"", value.toString());
		}
	}
	
	protected String hashToString( HashMap<?,?> hash )
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append( "[ " );
		
		for( Object key: hash.keySet() )
		{
			sb.append( attributeString( key.toString(), hash.get( key ), false ) );
			sb.append( " " );
		}
		
		sb.append( ']' );
		
		return sb.toString();
	}
}