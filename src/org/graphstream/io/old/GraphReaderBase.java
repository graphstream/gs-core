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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.HashMap;

import org.graphstream.graph.NotFoundException;
import org.graphstream.io.GraphParseException;
import org.util.geom.Bounds3;
import org.util.geom.Point3;

/**
 * Base for various graph readers.
 * 
 * <p>
 * This is a piece of crap, but...
 * </p>
 * 
 * <p>
 * This class provides parsing utilities to help the creation of new graph
 * readers/parsers. It handles a stack of input files that allow to easily
 * implements "includes" (that is interrupting the parsing of a file to input
 * another one). It wraps stream tokenizers allowing to eat or get specific
 * token types easily.
 * </p>
 *
 * <p>
 * It is well suited for graph formats using text (not binary), but not for XML
 * based files where a real XML parser would probably be better.
 * </p>
 * 
 * @since 20040909
 */
abstract class GraphReaderBase implements GraphReader
{
// Attributes

	/**
	 * The quote character. Can be changed in descendants.
	 */
	protected int QUOTE_CHAR = '"';

	/**
	 * The comment character. Can be changed in descendants.
	 */
	protected int COMMENT_CHAR = '#';

	/**
	 * Is EOL significant?.
	 */
	protected boolean eol_is_significant = false;

	/**
	 * Stack of tokenizers/filenames. Each tokenizer is open on a file. When an
	 * include is found, the current tokenizer is pushed on the stack and a new
	 * one for the included file is created. Once the included file is parsed,
	 * the tokenizer is popped of the stack and the previous one is used.
	 */
	protected ArrayList<CurrentFile> tok_stack = new ArrayList<CurrentFile>();

	/**
	 * Current tokenizer.
	 */
	protected StreamTokenizer st;

	/**
	 * Current file name.
	 */
	protected String filename;

	/**
	 * Map of unknown attributes to corresponding classes.
	 */
	protected HashMap<String, String> attribute_classes = new HashMap<String, String>();

	/**
	 * Listener for each element of the graph (depreciated but still functional at this time).
	 */
	protected ArrayList<GraphReaderListener> listeners = new ArrayList<GraphReaderListener>();

	/**
	 * Listener for each element of the graph.
	 */
	protected ArrayList<GraphReaderListenerExtended> listeners2 = new ArrayList<GraphReaderListenerExtended>();
	
// Constructors

	/**
	 * No-op constructor.
	 */
	public GraphReaderBase()
	{}

	/**
	 * Setup the reader End-Of-Line policy.
	 * 
	 * @param eol_is_significant If true EOL will be returned as a token, else
	 *        it is ignored.
	 */
	public GraphReaderBase( boolean eol_is_significant )
	{
		this.eol_is_significant = eol_is_significant;
	}

	/**
	 * Setup the reader End-Of-Line policy and specific comment and quote
	 * characters.
	 * 
	 * @param eol_is_significant If true EOL will be returned as a token, else
	 *        it is ignored.
	 * @param commentChar Character used for one line comments.
	 * @param quoteChar Character used to enclose quotations.
	 */
	public GraphReaderBase( boolean eol_is_significant, int commentChar,
			int quoteChar )
	{
		this.eol_is_significant = eol_is_significant;

		this.COMMENT_CHAR = commentChar;
		this.QUOTE_CHAR = quoteChar;
	}

	/**
	 * Setup a reader with End-Of-Line policy and a reference to a graph that
	 * listens to it.
	 * @param eol_is_significant If true EOL will be returned as a token, else
	 *        it is ignored.
	 * @param graph a graph that will be added to the list of listeners
	 */
	public GraphReaderBase( org.graphstream.graph.Graph graph,
			boolean eol_is_significant )
	{
		this.eol_is_significant = eol_is_significant;
		GraphReaderListenerHelper l = new GraphReaderListenerHelper( graph );
		addGraphReaderListener( l );
	}

// Access
	
// Command -- Complete modeField.

	@Deprecated
	public void addGraphReaderListener( GraphReaderListener listener )
	{
		listeners.add( listener );
	}

	@Deprecated
	public void removeGraphReaderListener( GraphReaderListener listener )
	{
		int i = listeners.indexOf( listener );

		if( i >= 0 )
			listeners.remove( i );
	}
	
	public void addGraphReaderListener( GraphReaderListenerExtended listener )
	{
		listeners2.add( listener );
	}
	
	public void removeGraphReaderListener( GraphReaderListenerExtended listener )
	{
		int i = listeners2.indexOf( listener );
		
		if( i >= 0 )
			listeners2.remove( i );
	}

	public void read( String filename ) throws NotFoundException,
			GraphParseException, IOException
	{
		init( filename );
	}

	public void read( InputStream stream ) throws GraphParseException,
			IOException
	{
		init( stream );
	}
	
	public void read( Reader reader ) throws GraphParseException, IOException
	{
		init( reader );
	}

	/**
	 * Install a new tokenizer and start parsing the graph by calling
	 * {@link #parseGraph()}.
	 */
	protected void init( String file ) throws GraphParseException, IOException
	{
		pushTokenizer( file );
		parseGraph();
		popTokenizer();
	}

	/**
	 * Install a new tokenizer and start parsing the graph by calling
	 * {@link #parseGraph()}.
	 */
	protected void init( InputStream stream ) throws GraphParseException,
			IOException
	{
		pushTokenizer( stream );
		parseGraph();
		popTokenizer();
	}
	
	/**
	 * Install a new tokenizer and start parsing the graph by calling
	 * {@link #parseGraph()}.
	 */
	protected void init( Reader reader ) throws GraphParseException, IOException
	{
		pushTokenizer( reader );
		parseGraph();
		popTokenizer();
	}

	// Commands -- By-event modeField.

	public void begin( String filename ) throws NotFoundException,
			GraphParseException, IOException
	{
		pushTokenizer( filename );
	}

	public void begin( InputStream stream ) throws GraphParseException,
			IOException
	{
		pushTokenizer( stream );
	}

	public void begin( Reader reader ) throws GraphParseException,
			IOException
	{
		pushTokenizer( reader );
	}

	public abstract boolean nextEvents() throws GraphParseException,
			IOException;

	public void end() throws GraphParseException, IOException
	{
		popTokenizer();
	}

// Command

	/**
	 * Declare that when <code>attribute</code> is found, the corresponding
	 * <code>attribute_class</code> must be instanciated and inserted in the
	 * current element being parsed. This is equivalent to the "map" keyword of
	 * the GML file. An attribute appears in a GML file as a name followed by a
	 * "[...]" block. The contents of this block defines sub-attributes that
	 * must map to public fields of the attribute. Only attributes that are not
	 * handled specifically by this parser can be added.
	 * 
	 * @param attribute must name the attribute.
	 * @param attribute_class must be the complete name of a Java class that
	 *        will represent the attribute.
	 */
	public void addAttributeClass( String attribute, String attribute_class )
	{
		attribute_classes.put( attribute, attribute_class );
	}

	// Parsing

	/**
	 * Start parsing of the graph. This is called by the
	 * {@link #init(InputStream)}, which in turn is called
	 * by one of the "read()" methods. This method must be implemented by the
	 * descendant.
	 */
	protected abstract void parseGraph() throws GraphParseException,
			IOException;

// Command -- Parsing -- Include mechanism

	/**
	 * Include the content of a <code>file</code>. This pushes a new
	 * tokenizer on the input stack, calls the
	 * {@link #continueParsingInInclude()} method (that must be implemented to
	 * read the include contents) and when finished pops the tokenizer of the
	 * input stack.
	 */
	protected void include( String file ) throws IOException,
			GraphParseException
	{
		pushTokenizer( file );
		continueParsingInInclude();
		popTokenizer();
	}

	/**
	 * Must be implemented to read the content of an include. The current
	 * tokenizer will be set to the included file. When this method returns, the
	 * include file will be closed an parsing will continue where it was before
	 * inclusion.
	 */
	protected abstract void continueParsingInInclude() throws IOException,
			GraphParseException;

	/**
	 * Push a tokenizer created from a file name on the file stack and make it current.
	 * @param file Name of the file used as source for the tokenizer.
	 */
	protected void pushTokenizer( String file ) throws IOException,
			GraphParseException
	{
		StreamTokenizer tok;
		CurrentFile cur;

		try
		{
			tok = createTokenizerFrom( file ); // new StreamTokenizer( new
												// BufferedReader( new
												// FileReader( file ) ) ); //
												// Scaring, isn't it?
			cur = new CurrentFile( file, tok );
		}
		catch( FileNotFoundException e )
		{
			throw new GraphParseException( "cannot read file '" + file
					+ "', not found: " + e.getMessage() );
		}

		configureTokenizer( tok );
		tok_stack.add( cur );

		st = tok;
		filename = file;
	}

	/**
	 * Push a tokenizer created from a stream on the file stack and make it current.
	 * @param stream The stream used as source for the tokenizer.
	 */
	protected void pushTokenizer( InputStream stream ) throws IOException,
			GraphParseException
	{
		StreamTokenizer tok;
		CurrentFile cur;

		tok = createTokenizerFrom( stream ); // new StreamTokenizer( new
												// BufferedReader( new
												// InputStreamReader( stream ) )
												// ); // Yes, quite scaring!!
		cur = new CurrentFile( "<?input-steam?>", tok );

		configureTokenizer( tok );
		tok_stack.add( cur );

		st = tok;
		filename = "<?input-stream?>";
	}
	
	/**
	 * Push a tokenizer created from a reader on the file stack and make it current.
	 * @param reader The reader used as source for the tokenizer.
	 */
	protected void pushTokenizer( Reader reader ) throws IOException,
			GraphParseException
	{
		StreamTokenizer tok;
		CurrentFile cur;
		
		tok = createTokenizerFrom( reader );
		cur = new CurrentFile( "<?reader?>", tok );
		configureTokenizer( tok );
		tok_stack.add( cur );
		
		st = tok;
		filename = "<?reader?>";
		
	}

	/**
	 * Method to override to create a tokenizer from a specific input source.
	 * 
	 * @param file File name.
	 * @return The new tokenizer.
	 * @throws IOException For any I/O error.
	 */
	protected StreamTokenizer createTokenizerFrom( String file )
			throws IOException
	{
		return new StreamTokenizer( new BufferedReader( new FileReader( file ) ) );
	}

	/**
	 * Method to override to create a tokenizer from a specific input source.
	 * 
	 * @param stream Input stream.
	 * @return The new tokenizer.
	 * @throws IOException For any I/O error.
	 */
	protected StreamTokenizer createTokenizerFrom( InputStream stream )
			throws IOException
	{
		return new StreamTokenizer( new BufferedReader( new InputStreamReader(
				stream ) ) );
	}

	/**
	 * Method to override to create a tokenizer from a specific input source.
	 * 
	 * @param reader The reader.
	 * @return The new tokenizer.
	 * @throws IOException For any I/O error.
	 */
	protected StreamTokenizer createTokenizerFrom( Reader reader )
			throws IOException
	{
		return new StreamTokenizer( new BufferedReader( reader ) );
	}

	/**
	 * Method to override to configure the tokenizer behaviour. It is called each
	 * time a tokenizer is created (for the parsed file and all included files).
	 */
	protected void configureTokenizer( StreamTokenizer tok ) throws IOException
	{
		if( COMMENT_CHAR > 0 )
			tok.commentChar( COMMENT_CHAR );
		tok.quoteChar( QUOTE_CHAR );
		tok.eolIsSignificant( eol_is_significant );
		tok.wordChars( '_', '_' );
		tok.parseNumbers();
	}

	/**
	 * Remove the current tokenizer from the stack and restore the previous one
	 * (if any).
	 */
	protected void popTokenizer() throws IOException, GraphParseException
	{
		int n = tok_stack.size();

		if( n <= 0 )
			throw new RuntimeException( "poped one too many tokenizer" );

		n -= 1;

		tok_stack.remove( n );

		if( n > 0 )
		{
			n -= 1;

			CurrentFile cur = tok_stack.get( n );

			st = cur.tok;
			filename = cur.file;
		}
	}

	// Low level parsing

	/**
	 * Push back the last read thing, so that it can be read anew. This allows
	 * to explore one token ahead, and if not corresponding to what is expected,
	 * go back.
	 */
	protected void pushBack()
	{
		st.pushBack();
	}

	/**
	 * Read EOF or report garbage at end of file.
	 */
	protected void eatEof() throws IOException, GraphParseException
	{
		int tok = st.nextToken();

		if( tok != StreamTokenizer.TT_EOF )
			parseError( "garbage at end of file, expecting EOF, "
					+ gotWhat( tok ) );
	}

	/**
	 * Read EOL.
	 */
	protected void eatEol() throws IOException, GraphParseException
	{
		int tok = st.nextToken();

		if( tok != StreamTokenizer.TT_EOL )
			parseError( "expecting EOL, " + gotWhat( tok ) );
	}

	/**
	 * Read EOL or EOF.
	 * @return The token read StreamTokenizer.TT_EOL or StreamTokenizer.TT_EOF.
	 */
	protected int eatEolOrEof() throws IOException, GraphParseException
	{
		int tok = st.nextToken();

		if( tok != StreamTokenizer.TT_EOL && tok != StreamTokenizer.TT_EOF )
			parseError( "expecting EOL or EOF, " + gotWhat( tok ) );

		return tok;
	}

	/**
	 * Read an expected <code>word</code> token or generate a parse error.
	 */
	protected void eatWord( String word ) throws IOException,
			GraphParseException
	{
		int tok = st.nextToken();

		if( tok != StreamTokenizer.TT_WORD )
			parseError( "expecting `" + word + "', " + gotWhat( tok ) );

		if( !st.sval.equals( word ) )
			parseError( "expecting `" + word + "' got `" + st.sval + "'" );
	}
	
	/**
	 * Read an expected word among the given word list or generate a parse error.
	 * @param words The expected words.
	 */
	protected void eatWords( String...words )
		throws IOException, GraphParseException
	{
		int tok = st.nextToken();
		
		if( tok != StreamTokenizer.TT_WORD )
			parseError( "expecting one of `" + words + "', " + gotWhat( tok ) );
		
		boolean found = false;
		
		for( String word: words )
		{
			if( st.sval.equals( word ) )
			{
				found = true;
				break;
			}
		}
		
		if( ! found )
			parseError( "expecting one of `" + words + ", got `" + st.sval + "'" );
	}

	/**
	 * Eat either a word or another, and return the eated one.
	 * 
	 * @param word1 The first word to eat.
	 * @param word2 The alternative word to eat.
	 * @return The word eaten.
	 */
	protected String eatOneOfTwoWords( String word1, String word2 )
			throws IOException, GraphParseException
	{
		int tok = st.nextToken();

		if( tok != StreamTokenizer.TT_WORD )
			parseError( "expecting `" + word1 + "' or  `" + word2 + "', "
					+ gotWhat( tok ) );

		if( st.sval.equals( word1 ) )
			return word1;

		if( st.sval.equals( word2 ) )
			return word2;

		parseError( "expecting `" + word1 + "' or `" + word2 + "' got `"
				+ st.sval + "'" );
		return null;
	}

	/**
	 * Eat the expected symbol or generate a parse error.
	 */
	protected void eatSymbol( char symbol ) throws IOException,
			GraphParseException
	{
		int tok = st.nextToken();

		if( tok != symbol )
			parseError( "expecting symbol `" + symbol + "', " + gotWhat( tok ) );
	}

	/**
	 * Eat one of the list of expected <code>symbols</code> or generate a
	 * parse error none of <code>symbols</code> can be found.
	 */
	protected void eatSymbols( String symbols ) throws IOException,
			GraphParseException
	{
		int tok = st.nextToken();
		int n = symbols.length();
		boolean f = false;

		for( int i = 0; i < n; ++i )
		{
			if( tok == symbols.charAt( i ) )
			{
				f = true;
				i = n;
			}
		}

		if( !f )
			parseError( "expecting one of symbols `" + symbols + "', "
					+ gotWhat( tok ) );
	}

	/**
	 * Eat the expected <code>word</code> or push back what was read so that
	 * it can be read anew.
	 */
	protected void eatWordOrPushbak( String word ) throws IOException,
			GraphParseException
	{
		int tok = st.nextToken();

		if( tok != StreamTokenizer.TT_WORD )
			pushBack();

		if( !st.sval.equals( word ) )
			pushBack();
	}

	/**
	 * Eat the expected <code>symbol</code> or push back what was read so that
	 * it can be read anew.
	 */
	protected void eatSymbolOrPushback( char symbol ) throws IOException,
			GraphParseException
	{
		int tok = st.nextToken();

		if( tok != symbol )
			pushBack();
	}

	/**
	 * Eat all until an EOL is found. The EOL is also eaten. This works only if
	 * EOL is significant (else it does nothing).
	 */
	protected void eatAllUntilEol() throws IOException, GraphParseException
	{
		if( !eol_is_significant )
			return;

		int tok = st.nextToken();

		if( tok == StreamTokenizer.TT_EOF )
			return;

		while( ( tok != StreamTokenizer.TT_EOL )
				&& ( tok != StreamTokenizer.TT_EOF ) )
		{
			tok = st.nextToken();
		}
	}

	/**
	 * Eat all availables EOLs.
	 */
	protected void eatAllEols() throws IOException, GraphParseException
	{
		if( !eol_is_significant )
			return;

		int tok = st.nextToken();

		while( tok == StreamTokenizer.TT_EOL )
			tok = st.nextToken();

		pushBack();
	}

	/**
	 * Read a word or generate a parse error.
	 */
	protected String getWord() throws IOException, GraphParseException
	{
		int tok = st.nextToken();

		if( tok != StreamTokenizer.TT_WORD )
			parseError( "expecting a word, " + gotWhat( tok ) );

		return st.sval;
	}

	/**
	 * Get a symbol.
	 */
	protected char getSymbol() throws IOException, GraphParseException
	{
		int tok = st.nextToken();

		if( tok > 0 && tok != StreamTokenizer.TT_WORD
				&& tok != StreamTokenizer.TT_NUMBER
				&& tok != StreamTokenizer.TT_EOL
				&& tok != StreamTokenizer.TT_EOF && tok != QUOTE_CHAR
				&& tok != COMMENT_CHAR )
		{
			return (char) tok;
		}

		parseError( "expecting a symbol, " + gotWhat( tok ) );
		return (char) 0;	// Never reached.
	}
	
	/**
	 * Get a symbol or push back what was read so that it can be read anew. If
	 * no symbol is found, 0 is returned.
	 */
	protected char getSymbolOrPushback() throws IOException,
			GraphParseException
	{
		int tok = st.nextToken();

		if( tok > 0 && tok != StreamTokenizer.TT_WORD
				&& tok != StreamTokenizer.TT_NUMBER
				&& tok != StreamTokenizer.TT_EOL
				&& tok != StreamTokenizer.TT_EOF && tok != QUOTE_CHAR
				&& tok != COMMENT_CHAR )
		{
			return (char) tok;
		}

		pushBack();

		return (char) 0;
	}

	/**
	 * Read a string constant (between quotes) or generate a parse error. Return
	 * the content of the string without the quotes.
	 */
	protected String getString() throws IOException, GraphParseException
	{
		int tok = st.nextToken();

		if( tok != QUOTE_CHAR )
			parseError( "expecting a string constant, " + gotWhat( tok ) );

		return st.sval;
	}

	/**
	 * Read a word or number or generate a parse error. If it is a number it is
	 * converted to a string before being returned.
	 */
	protected String getWordOrNumber() throws IOException, GraphParseException
	{
		int tok = st.nextToken();

		if( tok != StreamTokenizer.TT_WORD && tok != StreamTokenizer.TT_NUMBER )
			parseError( "expecting a word or number, " + gotWhat( tok ) );

		if( tok == StreamTokenizer.TT_NUMBER )
		{
			// If st.nval is an integer, as it is stored into a double,
			// toString() will transform it by automatically adding ".0", we
			// prevent this. The tokenizer does not allow to read integers.

			if( ( st.nval - ( (int) st.nval ) ) == 0 )
				return Integer.toString( (int) st.nval );
			else
				return Double.toString( st.nval );
		}
		else
		{
			return st.sval;
		}
	}

	/**
	 * Read a string or number or generate a parse error. If it is a number it
	 * is converted to a string before being returned.
	 */
	protected String getStringOrNumber() throws IOException,
			GraphParseException
	{
		int tok = st.nextToken();

		if( tok != QUOTE_CHAR && tok != StreamTokenizer.TT_NUMBER )
			parseError( "expecting a string constant or a number, "
					+ gotWhat( tok ) );

		if( tok == StreamTokenizer.TT_NUMBER )
		{
			if( ( st.nval - ( (int) st.nval ) ) == 0 )
				return Integer.toString( (int) st.nval );
			else
				return Double.toString( st.nval );
		}
		else
		{
			return st.sval;
		}
	}

	/**
	 * Read a string or number or generate a parse error. If it is a number it
	 * is converted to a string before being returned.
	 */
	protected String getStringOrWordOrNumber() throws IOException,
			GraphParseException
	{
		int tok = st.nextToken();

		if( tok == StreamTokenizer.TT_EOL || tok == StreamTokenizer.TT_EOF )
			parseError( "expecting word, string or number, " + gotWhat( tok ) );
		
		if( tok == StreamTokenizer.TT_NUMBER )
		{
			if( ( st.nval - ( (int) st.nval ) ) == 0 )
				return Integer.toString( (int) st.nval );
			else
				return Double.toString( st.nval );
		}
		else
		{
			return st.sval;
		}
	}
	
	/**
	 * Read a string or number or generate a parse error. The returned value
	 * is converted to a Number of a String depending on its type.
	 */
	protected Object getStringOrWordOrNumberO() throws IOException,
			GraphParseException
	{
		int tok = st.nextToken();

		if( tok == StreamTokenizer.TT_EOL || tok == StreamTokenizer.TT_EOF )
			parseError( "expecting word, string or number, " + gotWhat( tok ) );

		if( tok == StreamTokenizer.TT_NUMBER )
		{
			return st.nval;
		}
		else
		{
			return st.sval;
		}		
	}
	
	/**
	 * Read a string or number or generate a parse error. The returned value
	 * is converted to a Number of a String depending on its type.
	 */
	protected Object getStringOrWordOrSymbolOrNumberO() throws IOException,
			GraphParseException
	{
		int tok = st.nextToken();

		if( tok == StreamTokenizer.TT_EOL || tok == StreamTokenizer.TT_EOF )
			parseError( "expecting word, string or number, " + gotWhat( tok ) );

		if( tok == StreamTokenizer.TT_NUMBER )
		{
			return st.nval;
		}
		else if( tok == StreamTokenizer.TT_WORD )
		{
			return st.sval;
		}		
		else return Character.toString( (char) tok );
	}

	/**
	 * Read a word or string or generate a parse error.
	 */
	protected String getWordOrString() throws IOException, GraphParseException
	{
		int tok = st.nextToken();

		if( tok == StreamTokenizer.TT_WORD || tok == QUOTE_CHAR )
			return st.sval;

		parseError( "expecting a word or string, " + gotWhat( tok ) );
		return null;
	}

	/**
	 * Read a word or symbol or generate a parse error.
	 */
	protected String getWordOrSymbol() throws IOException, GraphParseException
	{
		int tok = st.nextToken();

		if( tok == StreamTokenizer.TT_NUMBER || tok == QUOTE_CHAR
				|| tok == StreamTokenizer.TT_EOF )
			parseError( "expecting a word or symbol, " + gotWhat( tok ) );

		if( tok == StreamTokenizer.TT_WORD )
			return st.sval;
		else
			return Character.toString( (char) tok );
	}

	/**
	 * Read a word or symbol or push back the read thing so that it is readable
	 * anew. In the second case, null is returned.
	 */
	protected String getWordOrSymbolOrPushback() throws IOException,
			GraphParseException
	{
		int tok = st.nextToken();

		if( tok == StreamTokenizer.TT_NUMBER || tok == QUOTE_CHAR
				|| tok == StreamTokenizer.TT_EOF )
		{
			pushBack();
			return null;
		}

		if( tok == StreamTokenizer.TT_WORD )
			return st.sval;
		else
			return Character.toString( (char) tok );
	}

	/**
	 * Read a word or symbol or string or generate a parse error.
	 */
	protected String getWordOrSymbolOrString() throws IOException,
			GraphParseException
	{
		int tok = st.nextToken();

		if( tok == StreamTokenizer.TT_NUMBER || tok == StreamTokenizer.TT_EOF )
			parseError( "expecting a word, symbol or string, " + gotWhat( tok ) );

		if( tok == QUOTE_CHAR )
			return st.sval;

		if( tok == StreamTokenizer.TT_WORD )
			return st.sval;
		else
			return Character.toString( (char) tok );
	}

	/**
	 * Read a word or symbol or string or number or generate a parse error.
	 */
	protected String getAllExceptedEof() throws IOException,
			GraphParseException
	{
		int tok = st.nextToken();

		if( tok == StreamTokenizer.TT_EOF )
			parseError( "expecting all excepted EOF, " + gotWhat( tok ) );

		if( tok == StreamTokenizer.TT_NUMBER || tok == StreamTokenizer.TT_EOF )
		{
			if( ( st.nval - ( (int) st.nval ) ) == 0 )
				return Integer.toString( (int) st.nval );
			else
				return Double.toString( st.nval );
		}

		if( tok == QUOTE_CHAR )
			return st.sval;

		if( tok == StreamTokenizer.TT_WORD )
			return st.sval;
		else
			return Character.toString( (char) tok );
	}

	/**
	 * Read a word, a symbol or EOF, or generate a parse error. If this is EOF,
	 * the string "EOF" is returned.
	 */
	protected String getWordOrSymbolOrEof() throws IOException,
			GraphParseException
	{
		int tok = st.nextToken();

		if( tok == StreamTokenizer.TT_NUMBER || tok == QUOTE_CHAR )
			parseError( "expecting a word or symbol, " + gotWhat( tok ) );

		if( tok == StreamTokenizer.TT_WORD )
			return st.sval;
		else if( tok == StreamTokenizer.TT_EOF )
			return "EOF";
		else
			return Character.toString( (char) tok );
	}

	/**
	 * Read a word or symbol or string or EOL/EOF or generate a parse error.
	 * If EOL is read the "EOL" string is returned. If EOF is read the "EOF"
	 * string is returned.
	 * @return A string. 
	 */
	protected String getWordOrSymbolOrStringOrEolOrEof() throws IOException,
			GraphParseException
	{
		int tok = st.nextToken();

		if( tok == StreamTokenizer.TT_NUMBER )
			parseError( "expecting a word, symbol or string, " + gotWhat( tok ) );

		if( tok == QUOTE_CHAR )
			return st.sval;

		if( tok == StreamTokenizer.TT_WORD )
			return st.sval;

		if( tok == StreamTokenizer.TT_EOF )
			return "EOF";

		if( tok == StreamTokenizer.TT_EOL )
			return "EOL";

		return Character.toString( (char) tok );
	}

	/**
	 * Read a word or number or string or EOL/EOF or generate a parse error.
	 * If EOL is read the "EOL" string is returned. If EOF is read the "EOF"
	 * string is returned. If a number is returned, it is converted to a string
	 * as follows: if it is an integer, only the integer part is converted to a
	 * string without dot or comma and no leading zeros. If it is a float the
	 * fractional part is also converted and the dot is used as separator.
	 * @return A string. 
	 */
	protected String getWordOrNumberOrStringOrEolOrEof() throws IOException,
			GraphParseException
	{
		int tok = st.nextToken();

		if( tok == StreamTokenizer.TT_NUMBER )
		{
			if( st.nval - ((int)st.nval) != 0 )
				return Double.toString( st.nval );
			
			return Integer.toString( (int) st.nval );
		}

		if( tok == QUOTE_CHAR )
			return st.sval;

		if( tok == StreamTokenizer.TT_WORD )
			return st.sval;

		if( tok == StreamTokenizer.TT_EOF )
			return "EOF";

		if( tok == StreamTokenizer.TT_EOL )
			return "EOL";

		parseError( "expecting a word, a number, a string, EOL or EOF, " + gotWhat( tok ) );
		return null; // Never happen, parseError throws unconditionally an exception.
	}
	
	/**
	 * Read a word or string or EOL/EOF or generate a parse error.
	 * If EOL is read the "EOL" string is returned. If EOF is read the "EOF"
	 * string is returned. 
	 * @return A string. 
	 */
	protected String getWordOrStringOrEolOrEof() throws IOException, GraphParseException
	{
		int tok = st.nextToken();
		
		if( tok == StreamTokenizer.TT_WORD )
			return st.sval;
		
		if( tok == QUOTE_CHAR )
			return st.sval;
		
		if( tok == StreamTokenizer.TT_EOL )
			return "EOL";
		
		if( tok == StreamTokenizer.TT_EOF )
			return "EOF";
		
		parseError( "expecting a word, a string, EOL or EOF, " + gotWhat( tok ) );
		return null; // Never happen, parseError throws unconditionally an exception.		
	}

	// Ordre: Word | String | Symbol | Number | Eol | Eof
	
	/**
	 * Read a word or number or string or EOL/EOF or generate a parse error.
	 * If EOL is read the "EOL" string is returned. If EOF is read the "EOF"
	 * string is returned. If a number is returned, it is converted to a string
	 * as follows: if it is an integer, only the integer part is converted to a
	 * string without dot or comma and no leading zeros. If it is a float the
	 * fractional part is also converted and the dot is used as separator.
	 * @return A string. 
	 */
	protected String getWordOrSymbolOrNumberOrStringOrEolOrEof() throws IOException,
			GraphParseException
	{
		int tok = st.nextToken();

		if( tok == StreamTokenizer.TT_NUMBER )
		{
			if( st.nval - ((int)st.nval) != 0 )
				return Double.toString( st.nval );
			
			return Integer.toString( (int) st.nval );
		}

		if( tok == QUOTE_CHAR )
			return st.sval;

		if( tok == StreamTokenizer.TT_WORD )
			return st.sval;

		if( tok == StreamTokenizer.TT_EOF )
			return "EOF";

		if( tok == StreamTokenizer.TT_EOL )
			return "EOL";

		return Character.toString( (char) tok );
	}
	
	/**
	 * Read a number or generate a parse error.
	 */
	protected double getNumber() throws IOException, GraphParseException
	{
		int tok = st.nextToken();

		if( tok != StreamTokenizer.TT_NUMBER )
			parseError( "expecting a number, " + gotWhat( tok ) );

		return st.nval;
	}

	/**
	 * Read a number (possibly with an exponent) or generate a parse error.
	 */
	protected double getNumberExp() throws IOException, GraphParseException
	{
		int tok = st.nextToken();

		if( tok != StreamTokenizer.TT_NUMBER )
			parseError( "expecting a number, " + gotWhat( tok ) );

		double nb = st.nval;

		tok = st.nextToken();

		if( tok == StreamTokenizer.TT_WORD
				&& ( st.sval.startsWith( "e-" ) || st.sval.startsWith( "e+" ) ) )
		{
			double exp = Double.parseDouble( st.sval.substring( 2 ) );
			return Math.pow( nb, exp );
		}
		else
		{
			st.pushBack();
		}

		return nb;
	}

	/**
	 * Return a string containing "got " then the content of the current
	 * <code>token</code>.
	 */
	protected String gotWhat( int token )
	{
		switch( token )
		{
			case StreamTokenizer.TT_NUMBER :
				return "got number `" + st.nval + "'";
			case StreamTokenizer.TT_WORD :
				return "got word `" + st.sval + "'";
			case StreamTokenizer.TT_EOF :
				return "got EOF";
			default:
				if( token == QUOTE_CHAR )
					return "got string constant `" + st.sval + "'";
				else
					return "unknown symbol `" + token + "' (" + ( (char) token )
							+ ")";
		}
	}

	/**
	 * Generate a parse error.
	 */
	protected void parseError( String message ) throws IOException,
			GraphParseException
	{
		throw new GraphParseException( "parse error: " + filename + ": "
				+ st.lineno() + ": " + message );
	}

// Access

	/**
	 * True if the <code>string</code> represents a truth statement ("1",
	 * "true", "yes", "on").
	 */
	protected boolean isTrue( String string )
	{
		string = string.toLowerCase();
		
		if( string.equals( "1" ) )
			return true;
		if( string.equals( "true" ) )
			return true;
		if( string.equals( "yes" ) )
			return true;
		if( string.equals( "on" ) )
			return true;

		return false;
	}

	/**
	 * True if the <code>string</code> represents a false statement ("0",
	 * "false", "no", "off").
	 */
	protected boolean isFalse( String string )
	{
		string = string.toLowerCase();
		
		if( string.equals( "0" ) )
			return true;
		if( string.equals( "false" ) )
			return true;
		if( string.equals( "no" ) )
			return true;
		if( string.equals( "off" ) )
			return true;

		return false;
	}

	/**
	 * Uses {@link #isTrue(String)} and {@link #isFalse(String)} to determine if
	 * <code>value</code> is a truth value and return the corresponding
	 * boolean.
	 * 
	 * @throws NumberFormatException if the <code>value</code> is not a truth
	 *         value.
	 */
	protected boolean getBoolean( String value ) throws NumberFormatException
	{
		if( isTrue( value ) )
			return true;
		if( isFalse( value ) )
			return false;
		throw new NumberFormatException( "not a truth value `" + value + "'" );
	}

	/**
	 * Try to transform <code>value</code> into a double.
	 * 
	 * @throws NumberFormatException if the <code>value</code> is not a
	 *         double.
	 */
	protected double getReal( String value ) throws NumberFormatException
	{
		return Double.parseDouble( value );
	}

	/**
	 * Try to transform <code>value</code> into a long.
	 * 
	 * @throws NumberFormatException if the <code>value</code> is not a long.
	 */
	protected long getInteger( String value ) throws NumberFormatException
	{
		return Long.parseLong( value );
	}

	/**
	 * Get a number triplet with numbers separated by comas and return a new
	 * point for it. For example "0,1,2".
	 */
	protected Point3 getPoint3( String value ) throws NumberFormatException
	{
		int p0 = value.indexOf( ',' );
		int p1 = value.indexOf( ',', p0 + 1 );

		if( p0 > 0 && p1 > 0 )
		{
			String n0, n1, n2;
			float v0, v1, v2;

			n0 = value.substring( 0, p0 );
			n1 = value.substring( p0 + 1, p1 );
			n2 = value.substring( p1 + 1 );

			v0 = Float.parseFloat( n0 );
			v1 = Float.parseFloat( n1 );
			v2 = Float.parseFloat( n2 );

			return new Point3( v0, v1, v2 );
		}

		throw new NumberFormatException( "value '" + value
				+ "' not in a valid point3 format" );
	}

	/**
	 * Get a number triplet with numbers separated by comas and return new
	 * bounds for it. For example "0,1,2".
	 */
	protected Bounds3 getBounds3( String value ) throws NumberFormatException
	{
		int p0 = value.indexOf( ',' );
		int p1 = value.indexOf( ',', p0 + 1 );

		if( p0 > 0 && p1 > 0 )
		{
			String n0, n1, n2;
			float v0, v1, v2;

			n0 = value.substring( 0, p0 );
			n1 = value.substring( p0 + 1, p1 );
			n2 = value.substring( p1 + 1 );

			v0 = Float.parseFloat( n0 );
			v1 = Float.parseFloat( n1 );
			v2 = Float.parseFloat( n2 );

			return new Bounds3( v0, v1, v2 );
		}

		throw new NumberFormatException( "value '" + value
				+ "' not in a valid point3 format" );
	}

// Nested classes

	/**
	 * Currently processed file.
	 * <p>
	 * The graph reader base can process includes in files, and handles a stack
	 * of files.
	 * </p>
	 * 
	 * @author Antoine Dutot
	 * @author Yoann Pign�
	 */
	protected static class CurrentFile
	{
		/**
		 * The file name.
		 */
		public String file;

		/**
		 * The stream tokenizer.
		 */
		public StreamTokenizer tok;

		public CurrentFile( String f, StreamTokenizer t )
		{
			file = f;
			tok = t;
		}
	}
}