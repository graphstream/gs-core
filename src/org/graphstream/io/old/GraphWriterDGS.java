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

package org.graphstream.io.old;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.graphstream.graph.CompoundAttribute;

/**
 * Graph writer for the DGS format.
 *
 * @since 20070103
 */
public class GraphWriterDGS implements GraphWriter
{
// Attribute
	
	/**
	 * The output.
	 */
	protected PrintStream out;
	
	/**
	 * The set of node attributes actually filtered.
	 */
	protected HashSet<String> nodesForbiddenAttrs = new HashSet<String>();
	
	/**
	 * The set of edge attributes actually filtered.
	 */
	protected HashSet<String> edgesForbiddenAttrs = new HashSet<String>();
	
// Construction
	
	/**
	 * Empty.
	 */
	public GraphWriterDGS()
	{
		// NOP.
	}
	
// Command
	
	public void begin( String fileName, String graphName ) throws IOException
	{
		if( out != null )
			throw new IOException( "cannot call begin() twice without having called end() first." );
	
		out = new PrintStream( new BufferedOutputStream( new FileOutputStream( fileName ) ) );
		
		outputHeader( graphName );
	}

	public void begin( OutputStream stream, String graphName ) throws IOException
	{
		if( out != null )
			throw new IOException( "cannot call begin() twice without having called end() first." );
	
		out = new PrintStream( new BufferedOutputStream( stream ) );
		
		outputHeader( graphName );
	}
	
	public void end() throws IOException
	{
		if( out != null )
		{
			out.flush();
			out.close();
			out = null;
		}
	}
	
	public void flush()
	{
		if( out != null )
		{
			out.flush();
		}
	}
	
	protected void outputAttributes( Map<String,Object> attributes, Set<String> forbidden )
	{
		for( String key: attributes.keySet() )
		{
			if( ! forbidden.contains( key ) )
			{
				String value = attributeString( key, attributes.get( key ), false );

				if( value != null )
					out.printf( "%s", value );
			}
//				outputAttribute( key, attributes.get( key ), false );
		}
	}
	
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
	
	public void addNode( String id, Map<String, Object> attributes ) throws IOException
	{
		if( out == null )
			throw new IOException( "use begin before using the writer!" );
		
		out.printf( "an \"%s\"", id );
		
		if( attributes != null )
			outputAttributes( attributes, nodesForbiddenAttrs );
		
		out.printf( "%n" );
	}

	@Deprecated
	public void changeNode( String id, Map<String, Object> attributes ) throws IOException
	{
		if( out == null )
			throw new IOException( "use begin before using the writer!" );
		
		out.printf( "cn \"%s\"", id );
	
		if( attributes != null )
			outputAttributes( attributes, nodesForbiddenAttrs );
		
		out.printf( "%n" );
	}
	
	public void changeNode( String id, String attribute, Object value, boolean remove )
		throws IOException
	{
		if( out == null )
			throw new IOException( "use begin before using the writer!" );
		
		out.printf( "cn \"%s\"", id );

		if( ! nodesForbiddenAttrs.contains( attribute ) )
			out.printf( attributeString( attribute, value, remove ) );

		out.printf( "%n" );		
	}

	public void delNode( String id ) throws IOException
	{
		if( out == null )
			throw new IOException( "use begin before using the writer!" );
	
		out.printf( "dn \"%s\"%n", id );
	}

	public void addEdge( String id, String node0Id, String node1Id, boolean directed, Map<String, Object> attributes ) throws IOException
	{
		if( out == null )
			throw new IOException( "use begin before using the writer!" );
		
		out.printf( "ae \"%s\" \"%s\" %s \"%s\"", id, node0Id, directed ? ">" : "", node1Id );
		
		if(attributes != null)
			outputAttributes( attributes, edgesForbiddenAttrs );
		
		out.printf( "%n" );
	}

	@Deprecated
	public void changeEdge( String id, Map<String, Object> attributes ) throws IOException
	{
		if( out == null )
			throw new IOException( "use begin before using the writer!" );
		
		out.printf( "ce \"%s\"", id );
		
		if(attributes != null)
			outputAttributes( attributes, edgesForbiddenAttrs );
	
		out.printf( "%n" );
	}
	
	public void changeEdge( String id, String attribute, Object value, boolean remove )
		throws IOException
	{
		if( out == null )
			throw new IOException( "use begin before using the writer!" );
		
		out.printf( "ce \"%s\"", id );

		if( ! edgesForbiddenAttrs.contains( attribute ) )
			out.printf( attributeString( attribute, value, remove ) );

		out.printf( "%n" );		
	}

	public void delEdge( String id ) throws IOException
	{
		if( out == null )
			throw new IOException( "use begin before using the writer!" );
		
		out.printf( "de \"%s\"%n", id );
	}

	public void step( double time ) throws IOException
	{
		if( out == null )
			throw new IOException( "use begin before using the writer!" );
		
		out.printf( Locale.US, "st %f%n", time );
	}

	@Deprecated
	public void changeGraph( Map<String,Object> attributes )
	{
		// Does not exist in DGS (but should!).
	}
	
	public void changeGraph( String attribute, Object value, boolean remove )
	{
		// Does not exist in DGS (but should!).
	}
	
	protected void outputHeader( String graphName )
	{
		out.printf( "DGS004%n" );
		
		if( graphName.length() <= 0 )
		     out.printf( "null 0 0%n" );
		else out.printf( "\"%s\" 0 0%n", graphName );
	}
	
// Attribute filtering

	public void unfilterNodeAttribute( String name )
	{
		nodesForbiddenAttrs.remove( name );
	}

	public void unfilterEdgeAttribute( String name )
	{
		edgesForbiddenAttrs.remove( name );
	}
	
	protected void unfilterAttribute( String name )
	{
		edgesForbiddenAttrs.remove( name );
		nodesForbiddenAttrs.remove( name );
	}

	public void filterNodeAttribute( String name )
	{
		nodesForbiddenAttrs.add( name );
	}

	public void filterEdgeAttribute( String name )
	{
		edgesForbiddenAttrs.add( name );
	}
	
	public void unfilterAllAttributes()
	{
		edgesForbiddenAttrs.clear();
		nodesForbiddenAttrs.clear();
	}
	
	public void unfilterAllNodeAttributes()
	{
		nodesForbiddenAttrs.clear();
	}
	
	public void unfilterAllEdgeAttributes()
	{
		edgesForbiddenAttrs.clear();
	}
}