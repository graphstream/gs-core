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

import org.graphstream.graph.Graph;
import org.graphstream.graph.NotFoundException;
import org.graphstream.io.GraphParseException;

/**
 * Graph reader for the Dynamic Graph Stream format.
 *
 * @since 2005
 * @see GraphReader
 * @see GraphReaderListenerExtended
 */
public class GraphReaderDGS extends GraphReaderBase
{
// Attribute
	
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
	 * An attribute set used everywhere.
	 */
	protected HashMap<String,Object> attributes = new HashMap<String,Object>();
	
	/**
	 * True as soon as the end of file is reached.
	 */
	protected boolean finished;
	
// Construction
	
	/**
	 * New reader for the DGS graph file format version 3.
	 */
	public GraphReaderDGS()
	{
		super( true /*EOL is significant*/ );
	}
	
	/**
	 * Setup a DGS reader (version 3) and add the given graph to the listeners.
	 * @param graph A graph that will be added to the list of listeners.
	 */
	public GraphReaderDGS( Graph graph )
	{
		super( graph, true /*EOL is significant*/ );
	}
	
// Command -- Parsing
	
	@Override
	public boolean nextEvents() throws GraphParseException, IOException
	{
		if( finished )
			return false;
		
		return next( false, false );
	}

	public boolean nextStep() throws GraphParseException, IOException
	{
		if( finished )
			return false;
		
		return next( true, false );
	}

	/**
	 * Read either one event or several.
	 * @param readSteps If true, read several events (usually starting with a
	 *        step event, but it may be preceded by other events), until
	 *        another step is encountered.
	 * @param stop If true stop at the next step encountered (and push it back
	 *        so that is is readable at the next call to this method).
	 * @return True if it remains things to read.
	 */
	protected boolean next( boolean readSteps, boolean stop )
		throws GraphParseException, IOException
	{
		String  key  = null;
		boolean loop = readSteps;

		// Sorted in probability of appearance ...
		
		do
		{
			key = getWordOrSymbolOrStringOrEolOrEof();
			
			if( key.equals( "ce" ) )
			{
				readCE();
			}
			else if( key.equals( "cn" ) )
			{
				readCN();
			}
			else if( key.equals( "ae" ) )
			{
				readAE();
			}
			else if( key.equals( "an" ) )
			{
				readAN();
			}
			else if( key.equals( "de" ) )
			{
				readDE();
			}
			else if( key.equals( "dn" ) )
			{
				readDN();
			}
			else if( key.equals( "st" ) )
			{
				if( readSteps )
				{
					if( stop )
					{
						loop = false;
						pushBack();
					}
					else
					{
						stop = true;
						readST();
					}
				}
				else
				{
					readST();
				}
			}
			else if( key.equals( "#" ) )
			{
				eatAllUntilEol();
				return next( readSteps, stop );
			}
			else if( key.equals( "EOL" ) )
			{
				// Probably an empty line.
				// NOP
				return next( readSteps, stop );
			}
			else if( key.equals( "EOF" ) )
			{
				finished = true;
				return false;
			}
			else
			{
				parseError( "unknown token '"+key+"'" );
			}
		}
		while( loop );
		
		return true;
	}
	
	protected void readCE()
		throws GraphParseException, IOException
	{
		String tag  = getStringOrWordOrNumber();
		
		readAttributes( attributes );
		
		for( GraphReaderListener listener: listeners )
			listener.edgeChanged( tag, attributes );
		for( GraphReaderListenerExtended listener: listeners2 )
		{
			for( String key: attributes.keySet() )
			{
				Object value = attributes.get( key );
				
				if( value == null )
				     listener.edgeChanged( tag, key, null,                  true );
				else listener.edgeChanged( tag, key, attributes.get( key ), false );
			}
		}
		
		if( eatEolOrEof() == StreamTokenizer.TT_EOF )
			pushBack();
	}
	
	protected void readCN()
		throws GraphParseException, IOException
	{
		String tag = getStringOrWordOrNumber();
		
		readAttributes( attributes );
		
		for( GraphReaderListener listener: listeners )
			listener.nodeChanged( tag, attributes );
		for( GraphReaderListenerExtended listener: listeners2 )
		{
			for( String key: attributes.keySet() )
			{
				Object value = attributes.get( key );
				
				if( value == null )
				     listener.nodeChanged( tag, key, null,                  true );
				else listener.nodeChanged( tag, key, attributes.get( key ), false );
			}
		}
		
		if( eatEolOrEof() == StreamTokenizer.TT_EOF )
			pushBack();
	}
	
	protected void readAE()
		throws GraphParseException, IOException
	{
		int     dir      = 0;
		boolean directed = false;
		String  dirc     = null;
		String  tag      = null;
		String  fromTag  = null;
		String  toTag    = null;
		
		tag     = getStringOrWordOrNumber();
		fromTag = getStringOrWordOrNumber();
		dirc    = getWordOrSymbolOrNumberOrStringOrEolOrEof();
		
		if( dirc.equals( ">" ) )
		{
			directed = true;
			dir      = 1;
		}
		else if( dirc.equals( "<" ) )
		{
			directed = true;
			dir      = 2;
		}
		else
		{
			pushBack();
		}
		
		toTag = getStringOrWordOrNumber();
		
		if( dir == 2 )
		{
			String tmp = toTag;
			toTag      = fromTag;
			fromTag    = tmp;
		}
		
		readAttributes( attributes );
		
		for( GraphReaderListener listener: listeners )
			listener.edgeAdded( tag, fromTag, toTag, directed, attributes );
		for( GraphReaderListenerExtended listener: listeners2 )
			listener.edgeAdded( tag, fromTag, toTag, directed, attributes );
		
		if( eatEolOrEof() == StreamTokenizer.TT_EOF )
			pushBack();
	}
	
	protected void readAN()
		throws GraphParseException, IOException
	{
		String tag = getStringOrWordOrNumber();
		
		readAttributes( attributes );
		
		for( GraphReaderListener listener: listeners )
			listener.nodeAdded( tag, attributes );	
		for( GraphReaderListenerExtended listener: listeners2 )
			listener.nodeAdded( tag, attributes );	
		
		if( eatEolOrEof() == StreamTokenizer.TT_EOF )
			pushBack();
	}
	
	protected void readDE()
		throws GraphParseException, IOException
	{
		String tag = getStringOrWordOrNumber();
		
		for( GraphReaderListener listener: listeners )
			listener.edgeRemoved( tag );
		for( GraphReaderListenerExtended listener: listeners2 )
			listener.edgeRemoved( tag );
		
		if( eatEolOrEof() == StreamTokenizer.TT_EOF )
			pushBack();
	}
	
	protected void readDN()
		throws GraphParseException, IOException
	{
		String tag = getStringOrWordOrNumber();
		
		for( GraphReaderListener listener: listeners )
			listener.nodeRemoved( tag );	
		for( GraphReaderListenerExtended listener: listeners2 )
			listener.nodeRemoved( tag );	
		
		if( eatEolOrEof() == StreamTokenizer.TT_EOF )
			pushBack();
	}
	
	protected void readST()
		throws GraphParseException, IOException
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
			pushBack();
	}

	protected void readAttributes( HashMap<String,Object> attributes )
		throws GraphParseException, IOException
	{
		boolean del = false;
		String  key = getWordOrSymbolOrStringOrEolOrEof();
		
		attributes.clear();
		
		if( key.equals( "-" ) )
		{
			key = getWordOrSymbolOrStringOrEolOrEof();
			del = true;
		}
		
		if( key.equals( "+" ) )
			key = getWordOrSymbolOrStringOrEolOrEof();
		
		while( ! key.equals( "EOF" ) && ! key.equals( "EOL" ) && ! key.equals( "]" ) )
		{
			if( del )
			     attributes.put( key, null );
			else attributes.put( key, readAttributeValue( key ) );
			
			key = getWordOrSymbolOrStringOrEolOrEof();
			
			if( key.equals( "-" ) )
			{
				key = getWordOrStringOrEolOrEof();
				del = true;
			}
			
			if( key.equals( "+" ) )
			{
				key = getWordOrStringOrEolOrEof();
				del = false;
			}
		}
		
		pushBack();
	}
	
	/**
	 * Read an attribute. The "key" (attribute name) is already read.
	 * @param key The attribute name, already read.
	 */
	protected Object readAttributeValue( String key )
		throws GraphParseException, IOException
	{
		ArrayList<Object> vector = null;
		Object            value  = null;
		Object            value2 = null;
		String            next   = null;
		
		eatSymbols( ":=" );
		
		value = getStringOrWordOrSymbolOrNumberO();
		
		if( value.equals( "[" ) )
		{
			HashMap<String,Object> map = new HashMap<String,Object>();
			
			readAttributes( map );;
			eatSymbol( ']' );
			
			value = map;
		}
		else
		{
			pushBack();
			
			value = getStringOrWordOrNumberO();
			next  = getWordOrSymbolOrNumberOrStringOrEolOrEof();
			
			while( next.equals( "," ) )
			{
				if( vector == null )
				{
					vector = new ArrayList<Object>();
					vector.add( value );
				}
				
				value2 = getStringOrWordOrNumberO();
				next   = getWordOrSymbolOrNumberOrStringOrEolOrEof();
				
				vector.add( value2 );
			}
			
			pushBack();
		}
		
		if( vector != null )
		     return vector.toArray();
		else return value;
	}
	
// Command -- Basic parsing
	
	@Override
	protected void parseGraph() throws GraphParseException, IOException
	{
		begin( filename );
		while( nextEvents() ) {}
		end();
	}

	@Override
	public void begin( String filename )
		throws GraphParseException, IOException, NotFoundException
	{
		super.begin( filename );
		begin();
	}
	
	@Override
	public void begin( InputStream stream )
		throws GraphParseException, IOException
	{
		super.begin( stream );
		begin();
	}
	
	@Override
	public void begin( Reader reader )
		throws GraphParseException, IOException
	{
		super.begin( reader );
		begin();
	}
	
	protected void begin()
		throws GraphParseException, IOException
	{
		st.parseNumbers();
		eatWords( "DGS003", "DGS004" );
		
		version = 3;
		
		eatEol();
		graphName           = getWordOrString();
		stepCountAnnounced  = (int)getNumber();//Integer.parseInt( getWord() );
		eventCountAnnounced = (int)getNumber();//Integer.parseInt( getWord() );
		eatEol();
		
		if(  graphName != null )
		{
			attributes.clear();
			attributes.put( "label", graphName );
			
			for( GraphReaderListener listener: listeners )
				listener.graphChanged( attributes );
			for( GraphReaderListenerExtended listener: listeners2 )
				listener.graphChanged( "label", graphName, false );
		}
	}
	
	@Override
	protected void continueParsingInInclude() throws IOException,
			GraphParseException
	{
	}
	@Override
	protected StreamTokenizer createTokenizerFrom( String file )
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
	protected StreamTokenizer createTokenizerFrom( InputStream stream )
		throws IOException
	{
		return new StreamTokenizer( new BufferedReader( new InputStreamReader( stream ) ) );
	}
	
	@Override
	protected void configureTokenizer( StreamTokenizer tok )
		throws IOException
	{
		if( COMMENT_CHAR > 0 )
			tok.commentChar( COMMENT_CHAR );
	//	tok.quoteChar( QUOTE_CHAR );
		tok.eolIsSignificant( eol_is_significant );
		tok.parseNumbers();
		tok.wordChars( '_', '_' );
	}
}