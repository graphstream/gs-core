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
import java.util.Iterator;

import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.stylesheet.Rule;
import org.graphstream.ui.graphicGraph.stylesheet.StyleSheet;

/**
 * Transforms a graph into a SVG description.
 * 
 * <p>Do not confuse this with the SVG export capabilities of the graph viewer. The SVG export
 * of the viewer provides the most exact copy of what you see on screen. This class is made
 * to export only nodes and edges without styling to SVG.</p>
 * 
 * 
 * <p>Although there is no styling, each node and edge is put in a SVG group with the
 * identifier of the corresponding element in the graph. A minimal CSS style sheet is included
 * in the generated file and it is easy to add another.</p>
 */
public class FileSinkSVG
	extends FileSinkBase
{
// Attribute
	
	/**
	 * The output.
	 */
	protected PrintStream out;
	
	/**
	 * What element ?.
	 */
	protected enum What { NODE, EDGE, OTHER };
	
	/**
	 * The positions of each node.
	 */
	protected HashMap<String,Point3> nodePos = new HashMap<String,Point3>();
	
// Construction

	public FileSinkSVG()
	{
		// NOP.
	}
	
// Command

	@Override
	public void end() throws IOException
	{
		if( out != null )
		{
			out.flush();
			out.close();
			out = null;
		}
	}

// Command

	@Override
    protected void outputHeader() throws IOException
    {
		out = (PrintStream) output;
		
		out.printf( "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>%n" );
		out.printf( "<svg" +
				" xmlns:svg=\"http://www.w3.org/2000/svg\"" +
				" width=\"100%%\"" +
				" height=\"100%%\"" +
				">%n" );

//		TODO
//		outputStyle( styleSheet );
    }

	@Override
    protected void outputEndOfFile() throws IOException
    {
		outputNodes();
		out.printf( "</svg>%n" );
    }

	public void edgeAttributeAdded( String graphId, long timeId, String edgeId, String attribute, Object value )
    {
		// NOP
    }

	public void edgeAttributeChanged( String graphId, long timeId, String edgeId, String attribute,
            Object oldValue, Object newValue )
    {
		// NOP
    }

	public void edgeAttributeRemoved( String graphId, long timeId, String edgeId, String attribute )
    {
		// NOP
    }

	public void graphAttributeAdded( String graphId, long timeId, String attribute, Object value )
    {
		// NOP
    }

	public void graphAttributeChanged( String graphId, long timeId, String attribute, Object oldValue,
            Object newValue )
    {
		// NOP
    }

	public void graphAttributeRemoved( String graphId, long timeId, String attribute )
    {
		// NOP
    }

	public void nodeAttributeAdded( String graphId, long timeId, String nodeId, String attribute, Object value )
    {
		setNodePos( nodeId, attribute, value );
    }

	public void nodeAttributeChanged( String graphId, long timeId, String nodeId, String attribute,
            Object oldValue, Object newValue )
    {
		setNodePos( nodeId, attribute, newValue );
    }

	public void nodeAttributeRemoved( String graphId, long timeId, String nodeId, String attribute )
    {
		// NOP
    }

	public void edgeAdded( String graphId, long timeId, String edgeId, String fromNodeId, String toNodeId,
            boolean directed )
    {
		Point3 p0 = nodePos.get( fromNodeId );
		Point3 p1 = nodePos.get( toNodeId );
		
		if( p0 != null && p1 != null )
		{
			out.printf( "  <g id=\"%s\">%n", edgeId );
			out.printf( "    <line x1=\"%f\" y1=\"%f\" x2=\"%f\" y2=\"%f\"/>%n", p0.x, p0.y, p1.x, p1.y );
			out.printf( "  </g>%n" );
		}
    }

	public void edgeRemoved( String graphId, long timeId, String edgeId )
    {
		// NOP
    }

	public void graphCleared( String graphId, long timeId )
    {
		// NOP
    }

	public void nodeAdded( String graphId, long timeId, String nodeId )
    {
		nodePos.put( nodeId, new Point3() );
    }

	public void nodeRemoved( String graphId, long timeId, String nodeId )
    {
		nodePos.remove( nodeId );
    }

	public void stepBegins( String graphId, long timeId, double time )
    {
		// NOP
    }
	
// Utility
	
	protected void setNodePos( String nodeId, String attribute, Object value )
	{
		Point3 p = nodePos.get( nodeId );
		
		if( p == null )
		{
			p = new Point3( (float) Math.random(), (float) Math.random(), 0f );
			nodePos.put( nodeId, p );
		}
		
		if( attribute.equals( "x" ) )
		{
			if( value instanceof Number )
				p.x = ((Number)value).floatValue();
		}
		else if( attribute.equals( "y" ) )
		{
			if( value instanceof Number )
				p.y = ((Number)value).floatValue();
		}
		else if( attribute.equals( "z" ) )
		{
			if( value instanceof Number )
				p.z = ((Number)value).floatValue();
		}
		
		else if( attribute.equals( "xy" ) )
		{
			if( value instanceof Object[] )
			{
				Object xy[] = ((Object[])value);
		
				if( xy.length > 1 )
				{
					p.x = ((Number)xy[0]).floatValue(); 
					p.y = ((Number)xy[1]).floatValue();
				}
			}
		}
		else if( attribute.equals( "xyz" ) )
		{
			if( value instanceof Object[] )
			{
				Object xyz[] = ((Object[])value);
			
				if( xyz.length > 1 )
				{
					p.x = ((Number)xyz[0]).floatValue(); 
					p.y = ((Number)xyz[1]).floatValue();
				}
				
				if( xyz.length > 2 )
				{
					p.z = ((Number)xyz[2]).floatValue();
				}
			}
		}
    }
	
	protected void outputStyle( String styleSheet )
	{
		String style = null;
		
		if( styleSheet != null )
		{
			StyleSheet ssheet = new StyleSheet();
			
			try
            {
				if( styleSheet.startsWith( "url(" ) )
				{
					styleSheet = styleSheet.substring( 5 );
					
					int pos = styleSheet.lastIndexOf( ')' );
					
					styleSheet = styleSheet.substring( 0, pos );
					
		                ssheet.parseFromFile( styleSheet );
				}
				else
				{
		                ssheet.parseFromString( styleSheet );
				}
				
				style = styleSheetToSVG( ssheet );
            }
            catch( IOException e )
            {
                e.printStackTrace();
                ssheet = null;
            }
		}
		
		if( style == null )
			style = "circle { fill: grey; stroke: none; } line { stroke-width: 1; stroke: black; }";
		
		out.printf( "<defs><style type=\"text/css\"><![CDATA[%n" );
		out.printf( "    %s%n", style );
		out.printf( "]]></style></defs>%n" );
	}
	
	protected void outputNodes( )
	{
		Iterator<?extends String> keys = nodePos.keySet().iterator();
		
		while( keys.hasNext() )
		{
			String key = keys.next();
			Point3 pos = nodePos.get( key );
			
			out.printf( "  <g id=\"%s\">%n", key );
			out.printf( "    <circle cx=\"%f\" cy=\"%f\" r=\"4\"/>%n", pos.x, pos.y );
			out.printf( "  </g>%n" );
		}
	}
	
	protected String styleSheetToSVG( StyleSheet sheet )
	{
		StringBuilder out = new StringBuilder();
		
		addRule( out, sheet.getDefaultGraphRule() );
		
		return out.toString();
	}
	
	protected void addRule( StringBuilder out, Rule rule )
	{
		//Style style = rule.getStyle();

		// TODO
	}
}