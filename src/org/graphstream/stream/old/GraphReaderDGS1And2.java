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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import org.graphstream.graph.NotFoundException;
import org.graphstream.io.GraphParseException;

/**
 * Graph reader for the dynamic graph stream format.
 *
 * @since 20050909
 * @see GraphReader
 * @see GraphReaderListenerExtended
 */
public class
	GraphReaderDGS1And2
extends
	GraphReaderBase
{
// Constants
	
	/**
	 * Types of attributes.
	 */
	protected enum AttributeType { NUMBER, VECTOR, STRING };
	
	/**
	 * Pair <name,type> defining an attribute.
	 */
	protected static class AttributeFormat
	{
		/**
		 * Name of the attribute.
		 */
		public String name;
		
		/**
		 * Type of the attribute.
		 */
		public AttributeType type;
		
		/**
		 * New format descriptor for an attribute.
		 * @param name The attribute name.
		 * @param type The attribute type.
		 */
		public
		AttributeFormat( String name, AttributeType type )
		{
			this.name = name;
			this.type = type;
		}
		
		/**
		 * Attribute name.
		 * @return The name.
		 */
		public String
		getName()
		{
			return name;
		}
		
		/**
		 * Attribute format.
		 * @return The format.
		 */
		public AttributeType
		getType()
		{
			return type;
		}
	}
	
// Attributes
	
	/**
	 * Format version.
	 */
	protected int version;
	
	/**
	 * Name of the graph.
	 */
	protected String graphName;
	
	/**
	 * Number of step given in the header.
	 */
	protected int stepCountAnnounced;
	
	/**
	 * Number of events given in the header.
	 */
	protected int eventCountAnnounced;
	
	/**
	 * Real number of step at current time.
	 */
	protected int stepCount;
	
	/**
	 * Real number of events at current time.
	 */
	protected int eventCount;
	
	/**
	 * Attribute count and type expected for each node add and modify command.
	 */
	protected ArrayList<AttributeFormat> nodesFormat = new ArrayList<AttributeFormat>();
	
	/**
	 * Attribute count and type expected for each edges add and modify command.
	 */
	protected ArrayList<AttributeFormat> edgesFormat = new ArrayList<AttributeFormat>();

	/**
	 * An attribute set.
	 */
	protected HashMap<String,Object> attributes = new HashMap<String,Object>();
	
// Constructors
	
	/**
	 * New reader for the DGS graph file format versions 1 and 2.
	 */
	public
	GraphReaderDGS1And2()
	{
		super( true /* EOL is significant */ );
	}
	
	/**
	 * Setup a DGS reader (version 1 and 2) and add the given graph to the listeners.
	 * 
	 * @param graph A graph form with a default graph listener will be created and added to the list of listeners. 
	 */
	public GraphReaderDGS1And2(org.graphstream.graph.Graph graph)
	{
		super(graph, true);
	}

// Access
	
// Command
	
	@Override
	public boolean
	nextEvents()
		throws GraphParseException, IOException
	{
		String key = getWordOrSymbolOrStringOrEolOrEof();
		String tag = null;
		
		if( key.equals( "ce" ) )
		{
			tag = getStringOrWordOrNumber();
			
			readAttributes( edgesFormat );
			
			for( GraphReaderListener listener: listeners )
				listener.edgeChanged( tag, attributes );
			for( GraphReaderListenerExtended listener: listeners2 )
			{
				for( String k: attributes.keySet() )
					listener.edgeChanged( tag, k, attributes.get( k ), false );
			}
			
			if( eatEolOrEof() == StreamTokenizer.TT_EOF )
				return false;
		}
		else if( key.equals( "cn" ) )
		{
			tag = getStringOrWordOrNumber();
			
			readAttributes( nodesFormat );
			
			for( GraphReaderListener listener: listeners )
				listener.nodeChanged( tag, attributes );
			for( GraphReaderListenerExtended listener: listeners2 )
			{
				for( String k: attributes.keySet() )
					listener.nodeChanged( tag, k, attributes.get( k ), false );
			}
			
			if( eatEolOrEof() == StreamTokenizer.TT_EOF )
				return false;
		}
		else if( key.equals( "ae" ) )
		{
			tag            = getStringOrWordOrNumber();
			String fromTag = getStringOrWordOrNumber();
			String toTag   = getStringOrWordOrNumber();
			
			readAttributes( edgesFormat );
			
			for( GraphReaderListener listener: listeners )
				listener.edgeAdded( tag, fromTag, toTag, false /*TODO add directedness in DGS*/, attributes );
			for( GraphReaderListenerExtended listener: listeners2 )
				listener.edgeAdded( tag, fromTag, toTag, false /*TODO add directedness in DGS*/, attributes );
			
			if( eatEolOrEof() == StreamTokenizer.TT_EOF )
				return false;
		}
		else if( key.equals( "an" ) )
		{
			tag = getStringOrWordOrNumber();
			
			readAttributes( nodesFormat );
			
			for( GraphReaderListener listener: listeners )
				listener.nodeAdded( tag, attributes );
			for( GraphReaderListenerExtended listener: listeners2 )
				listener.nodeAdded( tag, attributes );
			
			if( eatEolOrEof() == StreamTokenizer.TT_EOF )
				return false;
		}
		else if( key.equals( "de" ) )
		{
			tag = getStringOrWordOrNumber();
			
			for( GraphReaderListener listener: listeners )
				listener.edgeRemoved( tag );
			for( GraphReaderListenerExtended listener: listeners2 )
				listener.edgeRemoved( tag );
			
			if( eatEolOrEof() == StreamTokenizer.TT_EOF )
				return false;
		}
		else if( key.equals( "dn" ) )
		{
			tag = getStringOrWordOrNumber();
			
			for( GraphReaderListener listener: listeners )
				listener.nodeRemoved( tag );
			for( GraphReaderListenerExtended listener: listeners2 )
				listener.nodeRemoved( tag );
			
			if( eatEolOrEof() == StreamTokenizer.TT_EOF )
				return false;
		}
		else if( key.equals( "st" ) )
		{
			String w = getWordOrNumber();
			
			try
			{
				double time = Double.parseDouble( w );
				
				for( GraphReaderListener listener: listeners )
					listener.stepBegins( time );
				for( GraphReaderListenerExtended listener: listeners2 )
					listener.stepBegins( time );
			}
			catch( NumberFormatException e )
			{
				parseError( "expecting a number after `st', got `" + w + "'" );
			}
			
			if( eatEolOrEof() == StreamTokenizer.TT_EOF )
				return false;
		}
		else if( key == "#" )
		{
			eatAllUntilEol();
		}
		else if( key == "EOL" )
		{
			return true;
		}
		else if( key == "EOF" )
		{
			return false;
		}
		else
		{
			parseError( "found an unknown key in file '"+key+"' (expecting an,ae,cn,ce,dn,de or st)" );
		}

		return true;
	}

	/**
	 * tries to read all the events between 2 steps
	 */
	public boolean
	nextStep()
		throws GraphParseException, IOException
	{
		String key = "";
		String tag = null;
		
		while(! key.equals("st") && ! key.equals("EOF"))
		{
			key = getWordOrSymbolOrStringOrEolOrEof();
			
			if( key.equals( "ce" ) )
			{
				tag = getStringOrWordOrNumber();
				
				readAttributes( edgesFormat );
				
				for( GraphReaderListener listener: listeners )
					listener.edgeChanged( tag, attributes );
				for( GraphReaderListenerExtended listener: listeners2 )
				{
					for( String k: attributes.keySet() )
						listener.edgeChanged( tag, k, attributes.get( k ), false );
				}
				
				if( eatEolOrEof() == StreamTokenizer.TT_EOF )
					return false;
			}
			else if( key.equals( "cn" ) )
			{
				tag = getStringOrWordOrNumber();
				
				readAttributes( nodesFormat );
				
				for( GraphReaderListener listener: listeners )
					listener.nodeChanged( tag, attributes );
				for( GraphReaderListenerExtended listener: listeners2 )
				{
					for( String k: attributes.keySet() )
						listener.nodeChanged( tag, k, attributes.get( k ), false );
				}
				
				if( eatEolOrEof() == StreamTokenizer.TT_EOF )
					return false;
			}
			else if( key.equals( "ae" ) )
			{
				tag            = getStringOrWordOrNumber();
				String fromTag = getStringOrWordOrNumber();
				String toTag   = getStringOrWordOrNumber();
				
				readAttributes( edgesFormat );
				
				for( GraphReaderListener listener: listeners )
					listener.edgeAdded( tag, fromTag, toTag, false /*TODO add directedness in DGS*/, attributes );
				for( GraphReaderListenerExtended listener: listeners2 )
					listener.edgeAdded( tag, fromTag, toTag, false /*TODO add directedness in DGS*/, attributes );
				
				if( eatEolOrEof() == StreamTokenizer.TT_EOF )
					return false;
			}
			else if( key.equals( "an" ) )
			{
				tag = getStringOrWordOrNumber();
				
				readAttributes( nodesFormat );
				
				for( GraphReaderListener listener: listeners )
					listener.nodeAdded( tag, attributes );
				for( GraphReaderListenerExtended listener: listeners2 )
					listener.nodeAdded( tag, attributes );
				
				if( eatEolOrEof() == StreamTokenizer.TT_EOF )
					return false;
			}
			else if( key.equals( "de" ) )
			{
				tag = getStringOrWordOrNumber();
				
				for( GraphReaderListener listener: listeners )
					listener.edgeRemoved( tag );
				for( GraphReaderListenerExtended listener: listeners2 )
					listener.edgeRemoved( tag );
				
				if( eatEolOrEof() == StreamTokenizer.TT_EOF )
					return false;
			}
			else if( key.equals( "dn" ) )
			{
				tag = getStringOrWordOrNumber();
				
				for( GraphReaderListener listener: listeners )
					listener.nodeRemoved( tag );
				for( GraphReaderListenerExtended listener: listeners2 )
					listener.nodeRemoved( tag );
				
				if( eatEolOrEof() == StreamTokenizer.TT_EOF )
					return false;
			}
			else if( key.equals( "st" ) )
			{
				String w = getWordOrNumber();
				
				try
				{
					double time = Double.parseDouble( w );
					
					for( GraphReaderListener listener: listeners )
						listener.stepBegins( time );
					for( GraphReaderListenerExtended listener: listeners2 )
						listener.stepBegins( time );
				}
				catch( NumberFormatException e )
				{
					parseError( "expecting a number after `st', got `" + w + "'" );
				}
				
				if( eatEolOrEof() == StreamTokenizer.TT_EOF )
					return false;
			}
			else if( key == "#" )
			{
				eatAllUntilEol();
			}
			else if( key == "EOL" )
			{
				// NOP
			}
			else if( key == "EOF" )
			{
				return false;
			}
			else
			{
				parseError( "found an unknown key in file '"+key+"' (expecting an,ae,cn,ce,dn,de or st)" );
			}
		}

		return true;
	}

	protected void
	readAttributes( ArrayList<AttributeFormat> formats )
		throws IOException, GraphParseException
	{
		attributes.clear();
		
		if( formats.size() > 0 )
		{
			for( AttributeFormat format: formats )
			{
				if( format.type == AttributeType.NUMBER )
				{
					readNumberAttribute( format.name );
				}
				else if( format.type == AttributeType.VECTOR )
				{
					readVectorAttribute( format.name );
				}
				else if( format.type == AttributeType.STRING )
				{
					readStringAttribute( format.name );
				}
			}
		}
	}
	
	protected void
	readNumberAttribute( String name )
		throws IOException, GraphParseException
	{
		int tok = st.nextToken();
		
		if( isNull( tok ) )
		{
			attributes.put( name, new Double( 0 ) );
		}
		else
		{
			st.pushBack();
			
			double n = getNumber();
			
			attributes.put( name, new Double( n ) );
		}
	}
	
	protected void
	readVectorAttribute( String name )
		throws IOException, GraphParseException
	{
		int tok = st.nextToken();
		
		if( isNull( tok ) )
		{
			attributes.put( name, new ArrayList<Double>() );
		}
		else
		{
			
			boolean loop = true;
			
			ArrayList<Double> vector = new ArrayList<Double>();
			
			while( loop )
			{
				if( tok != StreamTokenizer.TT_NUMBER )
					parseError( "expecting a number, " + gotWhat( tok ) );
				
				vector.add( st.nval );
				
				tok = st.nextToken();
				
				if( tok != ',' )
				{
				     loop = false;
				     st.pushBack();
				}
				else
				{
					tok = st.nextToken();
				}
			}
	
			attributes.put( name, vector );
		}
	}
	
	protected void
	readStringAttribute( String name )
		throws IOException, GraphParseException
	{
		String s = getStringOrWordOrNumber();

		attributes.put( name, s );
	}
	
	protected boolean
	isNull( int tok )
	{
		if( tok == StreamTokenizer.TT_WORD )
			return( st.sval.equals( "null" ) );
		
		return false;
	}

	@Override
	protected void
	parseGraph()
		throws GraphParseException, IOException
	{
		begin( filename );
		while( nextEvents() ) {}
		end();
		//throw new IOException( "This graph reader does not support reading the graph in one large operation. See GraphReader.begin()." );
	}

	@Override
	public void begin( String filename )
		throws GraphParseException, IOException, NotFoundException
	{
		super.begin( filename );
		init();
	}
		
	@Override
	public void begin( InputStream stream )
		throws GraphParseException, IOException
	{
		super.begin( stream );
		init();
	}
	
	@Override
	public void begin( Reader reader )
		throws GraphParseException, IOException
	{
		super.begin( reader );
		init();
	}
	
	protected void init()
		throws GraphParseException, IOException
	{
		st.parseNumbers();

		String magic = eatOneOfTwoWords( "DGS001", "DGS002" );
		
		if( magic.equals( "DGS001"  ) )
		     version = 1;
		else version = 2;

		eatEol();
		graphName = getWord();
		stepCountAnnounced = (int)getNumber();//Integer.parseInt( getWord() );
		eventCountAnnounced = (int)getNumber();//Integer.parseInt( getWord() );
		eatEol();
		attributes.clear();
		attributes.put( "label", graphName );
		
		for( GraphReaderListener listener: listeners )
			listener.graphChanged( attributes );
		for( GraphReaderListenerExtended listener: listeners2 )
			listener.graphChanged( "label", graphName, false );
		
		readAttributeFormat();
	}
	
	protected void
	readAttributeFormat()
		throws IOException, GraphParseException
	{
		int tok = st.nextToken();
		
		if( tok == StreamTokenizer.TT_WORD && st.sval.equals( "nodes" ) )
		{
			parseAttributeFormat( nodesFormat );
			tok = st.nextToken();
		}

		if( tok == StreamTokenizer.TT_WORD && st.sval.equals( "edges" ) )
		{
			parseAttributeFormat( edgesFormat );
		}
		else
		{
			st.pushBack();
		}
	}
	
	protected void
	parseAttributeFormat( ArrayList<AttributeFormat> format )
		throws IOException, GraphParseException
	{
		int tok = st.nextToken();
		
		while( tok != StreamTokenizer.TT_EOL )
		{
			if( tok == StreamTokenizer.TT_WORD )
			{
				String name = st.sval;
				
				eatSymbol( ':' );
				
				tok = st.nextToken();
				
				if( tok == StreamTokenizer.TT_WORD )
				{
					String type = st.sval.toLowerCase();
					
					if( type.equals( "number" ) || type.equals( "n" ) )
					{
						format.add( new AttributeFormat( name, AttributeType.NUMBER ) );
					}
					else if( type.equals( "string" ) || type.equals( "s" ) )
					{
						format.add( new AttributeFormat( name, AttributeType.STRING ) );
					}
					else if( type.equals( "vector" ) || type.equals( "v" ) )
					{
						format.add( new AttributeFormat( name, AttributeType.VECTOR ) );
					}
					else
					{
						parseError( "unknown attribute type `" + type + "' (only `number', `vector' and `string' are accepted)" );
					}
				}
				else
				{
					parseError( "expecting an attribute type, got `" + gotWhat( tok ) + "'" );
				}
			}
			else
			{
				parseError( "expecting an attribute name, got `" + gotWhat( tok ) + "'" );
			}
			
			tok = st.nextToken();
		}
	}
	
	@Override
	protected void
	continueParsingInInclude()
		throws IOException, GraphParseException
	{
	}

	@Override
	protected StreamTokenizer
	createTokenizerFrom( String file )
		throws IOException
	{
		InputStream is = null;
		
		try
		{
			is = new GZIPInputStream( new FileInputStream( file ) );
		}
		catch( IOException e )
		{
			is = new FileInputStream( file ); 
		}
	
		return new StreamTokenizer( new BufferedReader( new InputStreamReader( is ) ) );
	}
	
	@Override
	protected StreamTokenizer
	createTokenizerFrom( InputStream stream )
		throws IOException
	{
//		InputStream is = null;
//		
//		try
//		{
//			is = new GZIPInputStream( stream );
//		}
//		catch( IOException e )
//		{
//			is = stream; 
//		}
//		
//		return new StreamTokenizer( new BufferedReader( new InputStreamReader( is ) ) );
		return new StreamTokenizer( new BufferedReader( new InputStreamReader( stream ) ) );
	}
	
	@Override
	protected void
	configureTokenizer( StreamTokenizer tok )
		throws IOException
	{
		if( COMMENT_CHAR > 0 )
			tok.commentChar( COMMENT_CHAR );
	//	tok.quoteChar( QUOTE_CHAR );
		tok.eolIsSignificant( eol_is_significant );
		tok.wordChars( '_', '_' );
		tok.ordinaryChar( '1' );
		tok.ordinaryChar( '2' );
		tok.ordinaryChar( '3' );
		tok.ordinaryChar( '4' );
		tok.ordinaryChar( '5' );
		tok.ordinaryChar( '6' );
		tok.ordinaryChar( '7' );
		tok.ordinaryChar( '8' );
		tok.ordinaryChar( '9' );
		tok.ordinaryChar( '0' );
		tok.ordinaryChar( '.' );
		tok.ordinaryChar( '-' );
		tok.wordChars( '1', '1' );
		tok.wordChars( '2', '2' );
		tok.wordChars( '3', '3' );
		tok.wordChars( '4', '4' );
		tok.wordChars( '5', '5' );
		tok.wordChars( '6', '6' );
		tok.wordChars( '7', '7' );
		tok.wordChars( '8', '8' );
		tok.wordChars( '9', '9' );
		tok.wordChars( '0', '0' );
		tok.wordChars( '.', '.' );
		tok.wordChars( '-', '-' );
		//tok.parseNumbers();
	}
}