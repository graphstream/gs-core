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
import java.util.Iterator;
import java.util.Map;

import org.graphstream.ui.graphicGraph.stylesheet.Rule;
import org.graphstream.ui.graphicGraph.stylesheet.StyleSheet;
import org.util.geom.Point3;

/**
 * Transforms a graph into a SVG description.
 * 
 * <p>Do not confuse this with the SVG export capabilities of the graph viewer. The SVG export
 * of the viewer provides the most exact copy of what you see on screen. This class is made
 * to export only nodes and edges without styling to SVG.</p>
 * 
 * <p>Although there is no styling, each node and edge is put in a SVG group with the
 * identifier of the corresponding element in the graph. A minimal CSS style sheet is included
 * in the generated file and it is easy to add another.</p>
 */
public class GraphWriterSVG implements GraphWriter
{
// Attribute
	
	/**
	 * The output.
	 */
	protected PrintStream out;
	
	/**
	 * Set of filtered node attributes.
	 */
	protected HashSet<String> nodeForbiddenAttrs = new HashSet<String>();
	
	/**
	 * Set of filtered edges attributes.
	 */
	protected HashSet<String> edgeForbiddenAttrs = new HashSet<String>();
	
	/**
	 * What element ?.
	 */
	protected enum What { NODE, EDGE, OTHER };
	
	/**
	 * The positions of each node.
	 */
	protected HashMap<String,Point3> nodePos = new HashMap<String,Point3>();
	
// Construction

	public GraphWriterSVG()
	{
		// NOP.
	}
	
// Command
	
	public void begin( String fileName, String graphName ) throws IOException
	{
		begin( fileName, graphName, null );
	}
	
	public void begin( String fileName, String graphName, String styleSheet ) throws IOException
	{
		if( out != null )
			throw new IOException( "cannot call begin() twice without having called end() first." );
	
		out = new PrintStream( new BufferedOutputStream( new FileOutputStream( fileName ) ) );
		
		outputHeader( graphName, styleSheet );
	}

	public void begin( OutputStream stream, String graphName ) throws IOException
	{
		begin( stream, graphName, null );
	}

	public void begin( OutputStream stream, String graphName, String styleSheet ) throws IOException
	{
		if( out != null )
			throw new IOException( "cannot call begin() twice without having called end() first." );
	
		out = new PrintStream( new BufferedOutputStream( stream ) );
		
		outputHeader( graphName, styleSheet );
	}
	
	protected void outputHeader( String graphName, String styleSheet )
	{
		out.printf( "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>%n" );
		out.printf( "<svg" +
				" xmlns:svg=\"http://www.w3.org/2000/svg\"" +
				" width=\"100%%\"" +
				" height=\"100%%\"" +
				">%n" );
		
		outputStyle( styleSheet );
		
//		if( graphName.length() > 0 )
	}
	
	public void end() throws IOException
	{
		if( out != null )
		{
			outputNodes();
			
			out.printf( "</svg>%n" );
			out.flush();
			out.close();
			out = null;
		}
	}

	public void addNode( String id, Map<String, Object> attributes ) throws IOException
	{
		if( out == null )
			throw new IOException( "use begin before using the writer!" );

		getNodePos( id, attributes );
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

	@Deprecated
	public void changeNode( String id, Map<String, Object> attributes ) throws IOException
	{
		// Does not exist.
	}
	
	public void changeNode( String id, String attribute, Object value, boolean remove ) throws IOException
	{
		// Does not exist.
	}

	public void delNode( String id ) throws IOException
	{
		// Does not exist.
	}
	
	public void addEdge( String id, String node0Id, String node1Id, boolean directed, Map<String, Object> attributes ) throws IOException
	{
		if( out == null )
			throw new IOException( "use begin before using the writer!" );
		
		Point3 p0 = getNodePos( node0Id, null );
		Point3 p1 = getNodePos( node1Id, null );
		
		if( p0 != null && p1 != null )
		{
			out.printf( "  <g id=\"%s\">%n", id );
			out.printf( "    <line x1=\"%f\" y1=\"%f\" x2=\"%f\" y2=\"%f\"/>%n", p0.x, p0.y, p1.x, p1.y );
			out.printf( "  </g>%n" );
		}
	}
	
	@Deprecated
	public void changeEdge( String id, Map<String, Object> attributes ) throws IOException
	{
		// Does not exist.
	}
	
	public void changeEdge( String id, String attribute, Object value, boolean removed )
	{
		// Does not exist.
	}

	public void delEdge( String id ) throws IOException
	{
		// Does not exist.
	}

	public void step( double time ) throws IOException
	{
		// Does not exist.
	}
	
	@Deprecated
	public void changeGraph( Map<String, Object> attributes ) throws IOException
	{
		// Does not exist.
	}
	
	public void changeGraph( String attribute, Object value, boolean removed ) throws IOException
	{
		// Does not exist.
	}
	
	public void flush()
	{
		out.flush();
	}
	
// Utility

	protected Point3 getNodePos( String id, Map<String,Object> attributes )
	{
		Point3 p = nodePos.get( id );
		
		if( p == null )
		{
			p = new Point3( (float) Math.random()*10, (float) Math.random()*10, 0f );
			nodePos.put( id, p );
		}
		
		if( attributes != null )
		{
			if( attributes.get("x") != null )
			{
				p.x = ((Number)attributes.get( "x" )).floatValue();
				p.y = ((Number)attributes.get( "y" )).floatValue();
			}
			else if( attributes.get( "xy" ) != null )
			{
				Object xy[] = ((Object[])attributes.get( "xy" ));
				
				p.x = ((Number)xy[0]).floatValue(); 
				p.y = ((Number)xy[1]).floatValue(); 
			}
			else if( attributes.get( "xyz" ) != null )
			{
				Object xyz[] = ((Object[])attributes.get( "xyz" ));
				
				p.x = ((Number)xyz[0]).floatValue(); 
				p.y = ((Number)xyz[1]).floatValue(); 
			}
			
			p.x *= 10;
			p.y *= 10;
		}
		
		return p;
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

// Attribute filtering

	public void unfilterAllAttributes()
	{
		nodeForbiddenAttrs.clear();
		edgeForbiddenAttrs.clear();
	}

	public void unfilterAllEdgeAttributes()
	{
		edgeForbiddenAttrs.clear();
	}

	public void unfilterAllNodeAttributes()
	{
		nodeForbiddenAttrs.clear();
	}

	public void unfilterEdgeAttribute( String name )
	{
		edgeForbiddenAttrs.remove( name );
	}

	public void unfilterNodeAttribute( String name )
	{
		nodeForbiddenAttrs.remove( name );
	}

	public void filterEdgeAttribute( String name )
	{
		edgeForbiddenAttrs.add( name );
	}

	public void filterNodeAttribute( String name )
	{
		nodeForbiddenAttrs.add( name );
	}
}