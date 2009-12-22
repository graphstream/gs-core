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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;

import org.graphstream.graph.NotFoundException;
import org.graphstream.io.GraphParseException;

/**
 * Graph reader for GraphViz "dot" files.
 * 
 * @since 20041109
 * @see GraphReader
 * @see GraphReaderListenerExtended
 */
public class GraphReaderDOT extends GraphReaderBase implements GraphReader
{
// Attribute
	
	/**
	 * Is the graph directed.
	 */
	protected boolean directed = false;
	
	/**
	 * Hash map used everywhere to transmit attributes. Avoids to create one
	 * for every node or edge attribute.
	 */
	protected HashMap<String,Object> attributes = new HashMap<String,Object>();
	
	/**
	 * Hash map of general attributes specified for all nodes.
	 */
	protected HashMap<String,Object> nodesAttributes = new HashMap<String,Object>();
	
	/**
	 * Hash map of general attributes specified for all edges.
	 */
	protected HashMap<String,Object> edgesAttributes = new HashMap<String,Object>();
	
	/**
	 * Allocate for edge identifiers.
	 */
	protected int edgeId = 1;
	
	/**
	 * Set that allow to now which nodes have already been declared and which
	 * node has not yet been declared. This allows to avoid the implicit
	 * declaration of nodes and send the correct set of attributes
	 */
	protected HashSet<String> nodes = new HashSet<String>();
	
// Construction
	
	public 
	GraphReaderDOT()
	{
	}
	
// Access
	
// Commands

	@Override
	protected void continueParsingInInclude() throws IOException, GraphParseException
	{
		// Should not happen, DOT files cannot be nested.
	}

	@Override
	public boolean nextEvents() throws GraphParseException, IOException
	{
		String  w;
		boolean remains = true;
		
		w = getWordOrSymbolOrNumberOrStringOrEolOrEof();

		if( w.equals( "node" ) )
		{
			// Store general nodes attributes.
			
			parseAttributeBlock( "node", nodesAttributes );
			eatSymbolOrPushback( ';' );
		}
		else if( w.equals( "edge" ) )
		{
			// Store general edges attributes.
			
			parseAttributeBlock( "edge", edgesAttributes );
			eatSymbolOrPushback( ';' );
		}
		else if( w.equals( "graph" ) )
		{
			// Graph attributes.
			
			attributes.clear();
			parseAttributeBlock( "graph", attributes );
			eatSymbolOrPushback( ';' );
			
			for( GraphReaderListener listener: listeners )
				listener.graphChanged( attributes );
			for( GraphReaderListenerExtended listener: listeners2 )
			{
				for( String key: attributes.keySet() )
					listener.graphChanged( key, attributes.get( key ), false );
			}
		}
		else if( w.equals( "}" ) )
		{
			// End of the graph.
			
			remains = false;
		}
		else if( w.equals( "EOF" ) )
		{
			parseError( "expecting '}' here, got EOF" );
		}
		else if( w.equals( "EOL" ) )
		{
			parseError( "should not get EOL" );
		}
		else
		{
			// Parse an edge or node specification.
			
			parseId( w );
			eatSymbolOrPushback( ';' );
		}
		
		return remains;
	}

	@Override
	public void begin( String fileName )throws NotFoundException, GraphParseException, IOException
	{
		super.begin( fileName );
		init();
	}
	
	@Override
	public void begin( InputStream stream ) throws GraphParseException, IOException
	{
		super.begin( stream );
		init();
	}
	
	@Override
	public void begin( Reader reader ) throws GraphParseException, IOException
	{
		super.begin( reader );
		init();
	}
	
	protected void init()
		throws GraphParseException, IOException
	{
		st.slashSlashComments( true );
		st.slashStarComments( true );
		
		readHeader();
	}
	
	@Override
	public void end() throws IOException, GraphParseException
	{
		eatEof();
		super.end();
	}
	
	@Override
	protected void parseGraph() throws GraphParseException, IOException
	{
		boolean loop = true;
		
		begin( filename );

		while( loop )
		{
			loop = nextEvents();
		}

		end();
	}

	public boolean nextStep() throws GraphParseException, IOException
	{
		return nextEvents();
	}
	
// Commands

	/**
	 * Read the header of a DOT file.
	 */
	protected void readHeader() throws GraphParseException, IOException
	{
		String w = getWord();
		
		if( w.equals( "strict" ) )
			w = getWord();

		if( w.equals( "graph" ) )
		     directed = false;
		else if( w.equals( "digraph" ) )
		     directed = true;
		else parseError( "waiting 'graph' or 'digraph' here" );

		w = getWordOrSymbolOrString();

		if( ! w.equals( "{" ) )
		{
			attributes.clear();
			attributes.put( "label", w );
			
			for( GraphReaderListener listener: listeners )
				listener.graphChanged( attributes );
			for( GraphReaderListenerExtended listener: listeners2 )
				listener.graphChanged( "label", w, false );
			
			w = getWordOrSymbol();
		}

		if( ! w.equals( "{" ) )
			parseError( "waiting '{' here" );
	}

	/**
	 * Parse an attribute block for the given element. The
	 * element can be "node", "edge" or "graph". This means that
	 * all subsequent nodes, edge or graphs will have these attributes. The
	 * attributes are accumulated in the {@link #attributes} variable.
	 */
	protected void parseAttributeBlock( String element, HashMap<String,Object> attributes ) throws IOException,
		GraphParseException
	{
		if( ( ! element.equals( "node" ) )
		&&  ( ! element.equals( "edge" ) )
		&&  ( ! element.equals( "graph" ) ) )
			parseError( "attribute list for an unknown element type '"+element+"'" );

		boolean loop = true;
		String  id, sw, w;
		char    s;

		eatSymbol( '[' );

		while( loop )
		{
			id = getWordOrSymbolOrString();

			if( id.equals( "]" ) )
			{
				// Case of an empty block "[]"...

				loop = false;
			}
			else
			{
				// An attribute ID was read, is there a value?

				sw = getWordOrSymbol();
				w  = null;

				if( sw.equals( "=" ) )
					w = getAllExceptedEof();
				else pushBack();

				//System.err.println("What to do with attribute '"+id+"'='"+w+"'??");

				putAttribute( id, w, attributes );
				
				// Now look what come next, a ',' or a ']'?

				s = getSymbolOrPushback();

				if( s == ']' )
				{
					loop = false;
				}
				else if( s != ',' )
				{
					parseError( "expecting ',' or ']' here" );
				}
			}
		}
	}

	/**
	 * Like {@link #parseAttributeBlock(String, HashMap)} but only if a '[' is found
	 * else do nothing.
	 */
	protected void maybeParseAttributeBlock( String element, HashMap<String,Object> attributes )
		throws IOException,
		       GraphParseException
	{
		char s;

		s = getSymbolOrPushback();

		if( s == '[' )
		{
			pushBack();
			parseAttributeBlock( element, attributes );
		}
		else
		{
			if( s != 0 )
				pushBack();
		}
	}
	
	/**
	 * Parse a node, edge or single attribute.
	 */
	protected void parseId( String id ) throws GraphParseException, IOException
	{
		String w;
		
		if( id.equals( "subgraph" ) )
		{
			parseError( "subgraphs are not supported yet with the DOT import" );
		//	SG = parseSubgraph();
		//	id = SG.get_id();
		}

		w = getWordOrSymbol();
		
		if( w.equals( "=" ) )
		{
			// Read a single attribute.

//			if( SG != null )
//				parseError( "cannot assign to a subgraph" );

			String val = getWordOrString();

			attributes.clear();
			attributes.put( id, val );

			for( GraphReaderListener listener: listeners )
				listener.graphChanged( attributes );
			for( GraphReaderListenerExtended listener: listeners2 )
				listener.graphChanged( id, val, false );
		}
		else if( w.startsWith( "-" ) )
		{
			// Read an edge.

			char symbol = getSymbol();
			boolean directed = false;
			
			if( symbol == '>' )
				directed = true;
			else if( symbol == '-' )
				directed = false;
			else parseError( "expecting '>' or '-', got '"+symbol+"'" );

			EdgeRepr e = parseEdge( id );
			this.attributes.clear();
			this.attributes.putAll( edgesAttributes );
			maybeParseAttributeBlock( "edge", this.attributes );
			
			if( ! nodes.contains( e.from ) )
				declareNode( e.from, nodesAttributes );
			
			if( ! nodes.contains( e.to ) )
				declareNode( e.to, nodesAttributes );
			
			for( GraphReaderListener listener: listeners )
				listener.edgeAdded( e.id, e.from, e.to, directed, this.attributes );
			for( GraphReaderListenerExtended listener: listeners2 )
				listener.edgeAdded( e.id, e.from, e.to, directed, this.attributes );
		}
		else
		{
			// Read a node

//			if( SG != null )
//			{
//			}
//			else
			{
				pushBack();
				this.attributes.clear();
				this.attributes.putAll( nodesAttributes );
				maybeParseAttributeBlock( "node", this.attributes );
				declareNode( id, this.attributes );
			}
		}
	}

	protected void declareNode( String id, HashMap<String,Object> attributes )
		throws IOException, GraphParseException
	{
		nodes.add( id );
		
		for( GraphReaderListener listener: listeners )
			listener.nodeAdded( id, attributes );
		for( GraphReaderListenerExtended listener: listeners2 )
			listener.nodeAdded( id, attributes );
	}
	
	protected EdgeRepr parseEdge( String node0Id ) throws IOException, GraphParseException
	{
		String node1Id;

		node1Id = getStringOrWordOrNumber();

		if( node1Id.equals( "subgraph" ) )
		{
			parseError( "Subgraphs are not yet handled by this DOT import" );
			return null;
//			SG = parse_subgraph();
//			node1_id = SG.get_id();
		}
		else
		{
			String w = getWordOrSymbolOrPushback();

			if( w != null )
			{
				if( w.startsWith( "-" ) )
				{
					// Chain of edges.
					
					eatSymbols( ">-" );
					parseEdge( node1Id );
				}
				else
				{
					pushBack();
				}
			}

			return new EdgeRepr( Integer.toString( edgeId++ ), node0Id, node1Id );
		}
	}

	protected void putAttribute( String id, String value, HashMap<String,Object> attributes )
	{
		attributes.put( id, value );
	}
	
	protected class EdgeRepr
	{
		public String id;
		public String from;
		public String to;
		public EdgeRepr( String id, String from, String to ) { this.id = id; this.from = from; this.to = to; }
	}
}