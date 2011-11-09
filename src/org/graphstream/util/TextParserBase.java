/*
 * Copyright 2006 - 2011 
 *     Stefan Balev 	<stefan.balev@graphstream-project.org>
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
package org.graphstream.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;

/**
 * Base for various text parsers.
 *
 * <p>This class provides parsing utilities to help the creation of new text
 * readers/parsers.</p>
 * 
 * <p>This class provides two facilities. The first is a "stack" of input files
 * allowing to implement easily an "include" mechanism, that is the ability to
 * interrupt parsing in one file to read another, and restart the parsing in
 * the first file transparently.</p>
 * 
 * <p>The second is a very large set of methods that read textual things, words
 * numbers, symbols. These methods can eat the symbols (that is expect they are
 * here) and produce parsing exceptions if not found, or can try to read them
 * and provide them to the user, also producing an exception if not foud.</p>
 *
 * @author Antoine Dutot
 * @author Yoann Pigné
 * @since 20040909
 */
public abstract class
	TextParserBase
{
// Attributes

	/**
	 * The quote character. Can be changed in descendants.
	 */
	protected int QUOTE_CHAR   = '"';

	/**
	 * The comment character. Can be changed in descendants.
	 */
	protected int COMMENT_CHAR = '#';

	/**
	 * Is EOL signigicant?.
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

// Constructors

	/**
	 * No-op constructor.
	 */
	public
	TextParserBase()
	{
	}

	/**
	 * Setup the reader End-Of-Line policy.
	 * @param eol_is_significant If true EOL will be returned as a token, else it is ignored.
	 */
	public
	TextParserBase( boolean eol_is_significant )
	{
		this.eol_is_significant = eol_is_significant;
	}

	/**
	 * Setup the reader End-Of-Line policy and specific comment and quote characters.
	 * @param eol_is_significant If true EOL will be returned as a token, else it is ignored.
	 * @param commentChar Character used for one line comments.
	 * @param quoteChar Character used to enclose quotations.
	 */
	public
	TextParserBase( boolean eol_is_significant, int commentChar, int quoteChar )
	{
		this.eol_is_significant = eol_is_significant;

		this.COMMENT_CHAR = commentChar;
		this.QUOTE_CHAR   = quoteChar;
	}

// Commands
// Parsing -- Include mechanism

	/**
	 * Push a tokenizer created from the given file in the parsing stack and
	 * make it current.
	 * @param file Name of the file used to create the tokenizer.
	 */
	protected void
	pushTokenizer( String file )
		throws IOException
	{
		StreamTokenizer tok;
		CurrentFile     cur;

		try
		{
			tok = createTokenizerFrom( file ); //new StreamTokenizer( new BufferedReader( new FileReader( file ) ) );	// Scaring, isn't it?
			cur = new CurrentFile( file, tok );
		}
		catch( FileNotFoundException e )
		{
			throw new IOException( "cannot read file '"+file+"', not found: " + e.getMessage() );
		}

		configureTokenizer( tok );
		tok_stack.add( cur );

		st       = tok;
		filename = file;
	}

	/**
	 * Push a tokenizer created form the given styream in the parsing stack and
	 * make it current.
	 * @param stream The stream used to create the tokenizer.
	 */
	protected void
	pushTokenizer( InputStream stream )
		throws IOException
	{
		StreamTokenizer tok;
		CurrentFile     cur;

		tok = createTokenizerFrom( stream ); //new StreamTokenizer( new BufferedReader( new InputStreamReader( stream ) ) );	// Yes, quite scaring!!
		cur = new CurrentFile( "<?input-steam?>", tok );

		configureTokenizer( tok );
		tok_stack.add( cur );

		st       = tok;
		filename = "<?input-stream?>";
	}
	
	/**
	 * Method to override to create a tokenizer from a specific input source.
	 * @param file File name.
	 * @return The new tokenizer.
	 * @throws IOException For any I/O error.
	 */
	protected StreamTokenizer
	createTokenizerFrom( String file )
		throws IOException
	{
		return new StreamTokenizer( new BufferedReader( new FileReader( file ) ) );
	}
	
	/**
	 * Method to override to create a tokenizer from a specific input source.
	 * @param stream Input stream.
	 * @return The new tokenizer.
	 * @throws IOException For any I/O error.
	 */
	protected StreamTokenizer
	createTokenizerFrom( InputStream stream )
		throws IOException
	{
		return new StreamTokenizer( new BufferedReader( new InputStreamReader( stream ) ) );
	}
	
	/**
	 * Method to override to configure the tokenizer behavior. It is called each
	 * time a tokenizer is created (for the parsed file and all included files).
	 */
	protected void
	configureTokenizer( StreamTokenizer tok )
		throws IOException
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
	protected void
	popTokenizer()
		throws IOException
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

			st       = cur.tok;
			filename = cur.file;
		}
	}

// Low level parsing

	/**
	 * Push back the last read thing, so that it can be read anew. This allows
	 * to explore one token ahead, and if not corresponding to what is
	 * expected, go back.
	 */
	protected void
	pushBack()
	{
		st.pushBack();
	}

	/**
	 * Read EOF or report garbage at end of file.
	 */
	protected void
	eatEof()
		throws IOException
	{
		int tok = st.nextToken();

		if( tok != StreamTokenizer.TT_EOF )
			parseError( "garbage at end of file, expecting EOF, " + gotWhat( tok ) );
	}

	/**
	 * Read EOL.
	 */
	protected void
	eatEol()
		throws IOException
	{
		int tok = st.nextToken();

		if( tok != StreamTokenizer.TT_EOL )
			parseError( "expecting EOL, " + gotWhat( tok ) );
	}

	/**
	 * Read an expected <code>word</code> token or generate a parse error.
	 */
	protected void
	eatWord( String word )
		throws IOException
	{
		int tok = st.nextToken();

		if( tok != StreamTokenizer.TT_WORD )
			parseError( "expecting `" + word + "', " + gotWhat( tok ) );

		if( ! st.sval.equals( word ) )
			parseError( "expecting `" + word + "' got `" + st.sval + "'" );
	}
	
	/**
	 * Eat either a word or another, and return the eated one.
	 * @param word1 The first word to eat.
	 * @param word2 The alternative word to eat.
	 * @return The word eaten.
	 */
	protected String
	eatOneOfTwoWords( String word1, String word2 )
		throws IOException
	{
		int tok = st.nextToken();
		
		if( tok != StreamTokenizer.TT_WORD )
			parseError( "expecting `" + word1 + "' or  `" + word2 + "', " + gotWhat( tok ) );
		
		if( st.sval.equals( word1 ) )
			return word1;
	
		if( st.sval.equals( word2 ) )
			return word2;
		
		parseError( "expecting `" + word1 + "' or `" + word2 + "' got `" + st.sval + "'" );
		return null;
	}

	/**
	 * Eat the expected symbol or generate a parse error.
	 */
	protected void
	eatSymbol( char symbol )
		throws IOException
	{
		int tok = st.nextToken();

		if( tok != symbol )
			parseError( "expecting symbol `" + symbol + "', " + gotWhat( tok ) );
	}

	/**
	 * Eat one of the list of expected <code>symbols</code> or generate a parse
	 * error none of <code>symbols</code> can be found.
	 */
	protected void
	eatSymbols( String symbols )
		throws IOException
	{
		int     tok = st.nextToken();
		int     n   = symbols.length();
		boolean f   = false;

		for( int i=0; i<n; ++i )
		{
			if( tok == symbols.charAt( i ) )
			{
				f = true;
				i = n;
			}
		}

		if( ! f )
			parseError( "expecting one of symbols `" + symbols + "', " + gotWhat( tok ) );
	}

	/**
	 * Eat the expected <code>word</code> or push back what was read
	 * so that it can be read anew.
	 */
	protected void
	eatWordOrPushbak( String word )
		throws IOException
	{
		int tok = st.nextToken();

		if( tok != StreamTokenizer.TT_WORD )
			pushBack();

		if( ! st.sval.equals( word ) )
			pushBack();
	}

	/**
	 * Eat the expected <code>symbol</code> or push back what was read
	 * so that it can be read anew.
	 */
	protected void
	eatSymbolOrPushback( char symbol )
		throws IOException
	{
		int tok = st.nextToken();

		if( tok != symbol )
			pushBack();
	}

	/**
	 * Eat all until an EOL is found. The EOL is also eaten. This works only if
	 * EOL is significant (else it does nothing).
	 */
	protected void
	eatAllUntilEol()
		throws IOException
	{
		if( ! eol_is_significant )
			return;

		int tok = st.nextToken();

		if( tok == StreamTokenizer.TT_EOF )
			return;

		while( ( tok != StreamTokenizer.TT_EOL ) && ( tok != StreamTokenizer.TT_EOF ) )
		{
			tok = st.nextToken();
		}
	}

	/**
	 * Eat all availables EOLs.
	 */
	protected void
	eatAllEols()
		throws IOException
	{
		if( ! eol_is_significant )
			return;

		int tok = st.nextToken();

		while( tok == StreamTokenizer.TT_EOL )
			tok = st.nextToken();

		pushBack();
	}

	/**
	 * Read a word or generate a parse error.
	 */
	protected String
	getWord()
		throws IOException
	{
		int tok = st.nextToken();

		if( tok != StreamTokenizer.TT_WORD )
			parseError( "expecting a word, " + gotWhat( tok ) );

		return st.sval;
	}

	/**
	 * Get a symbol or push back what was read so that it can be read anew. If
	 * no symbol is found, 0 is returned.
	 */
	protected char
	getSymbolOrPushback()
		throws IOException
	{
		int tok = st.nextToken();

		if( tok > 0
		&&  tok != StreamTokenizer.TT_WORD
		&&  tok != StreamTokenizer.TT_NUMBER
		&&  tok != StreamTokenizer.TT_EOL
		&&  tok != StreamTokenizer.TT_EOF
		&&  tok != QUOTE_CHAR
		&&  tok != COMMENT_CHAR )
		{
			return (char) tok;
		}
		
		pushBack();

		return (char) 0;
	}

	/**
	 * Read a string constant (between quotes) or generate a parse error.
	 * Return the content of the string without the quotes.
	 */
	protected String
	getString()
		throws IOException
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
	protected String
	getWordOrNumber()
		throws IOException
	{
		int tok = st.nextToken();

		if( tok != StreamTokenizer.TT_WORD && tok != StreamTokenizer.TT_NUMBER )
			parseError( "expecting a word or number, " + gotWhat( tok ) );

		if( tok == StreamTokenizer.TT_NUMBER )
		{
			// If st.nval is an integer, as it is stored into a double,
			// toString() will transform it by automatically adding ".0", we
			// prevent this. The tokenizer does not allow to read integers.

			if( ( st.nval - ((int)st.nval) ) == 0 )
			     return Integer.toString( (int) st.nval );
			else return Double.toString( st.nval );
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
	protected String
	getStringOrNumber()
		throws IOException
	{
		int tok = st.nextToken();

		if( tok != QUOTE_CHAR && tok != StreamTokenizer.TT_NUMBER )
			parseError( "expecting a string constant or a number, " + gotWhat( tok ) );

		if( tok == StreamTokenizer.TT_NUMBER )
		{
			if( ( st.nval - ((int)st.nval) ) == 0 )
			     return Integer.toString( (int) st.nval );
			else return Double.toString( st.nval );
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
	protected String
	getStringOrWordOrNumber()
		throws IOException
	{
		int tok = st.nextToken();

//		if( tok != QUOTE_CHAR && tok != StreamTokenizer.TT_NUMBER )
//			parseError( "expecting a string constant or a number, " + gotWhat( tok ) );

		if( tok == StreamTokenizer.TT_NUMBER )
		{
			if( ( st.nval - ((int)st.nval) ) == 0 )
			     return Integer.toString( (int) st.nval );
			else return Double.toString( st.nval );
		}
		else
		{
			return st.sval;
		}
	}

	/**
	 * Read a word or string or generate a parse error.
	 */
	protected String
	getWordOrString()
		throws IOException
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
	protected String
	getWordOrSymbol()
		throws IOException
	{
		int tok = st.nextToken();

		if( tok == StreamTokenizer.TT_NUMBER || tok == QUOTE_CHAR || tok == StreamTokenizer.TT_EOF )
			parseError( "expecting a word or symbol, " + gotWhat( tok ) );

		if( tok == StreamTokenizer.TT_WORD )
		     return st.sval;
		else return Character.toString( (char) tok );
	}

	/**
	 * Read a word or symbol or push back the read thing so taht it
	 * is readable anew. In the second case, null is returned.
	 */
	protected String
	getWordOrSymbolOrPushback()
		throws IOException
	{
		int tok = st.nextToken();

		if( tok == StreamTokenizer.TT_NUMBER || tok == QUOTE_CHAR || tok == StreamTokenizer.TT_EOF )
		{
			pushBack();
			return null;
		}

		if( tok == StreamTokenizer.TT_WORD )
		     return st.sval;
		else return Character.toString( (char) tok );
	}

	/**
	 * Read a word or symbol or string or generate a parse error.
	 */
	protected String
	getWordOrSymbolOrString()
		throws IOException
	{
		int tok = st.nextToken();

		if( tok == StreamTokenizer.TT_NUMBER || tok == StreamTokenizer.TT_EOF )
			parseError( "expecting a word, symbol or string, " + gotWhat( tok ) );

		if( tok == QUOTE_CHAR )
			return st.sval;

		if( tok == StreamTokenizer.TT_WORD )
		     return st.sval;
		else return Character.toString( (char) tok );
	}

	/**
	 * Read a word or symbol or string or number or generate a parse error.
	 */
	protected String
	getAllExceptedEof()
		throws IOException
	{
		int tok = st.nextToken();

		if( tok == StreamTokenizer.TT_EOF )
			parseError( "expecting all excepted EOF, " + gotWhat( tok ) );
		
		if( tok == StreamTokenizer.TT_NUMBER || tok == StreamTokenizer.TT_EOF )
		{
			if( ( st.nval - ((int)st.nval) ) == 0 )
			     return Integer.toString( (int) st.nval );
			else return Double.toString( st.nval );
		}

		if( tok == QUOTE_CHAR )
			return st.sval;

		if( tok == StreamTokenizer.TT_WORD )
		     return st.sval;
		else return Character.toString( (char) tok );
	}

	/**
	 * Read a word, a symbol or EOF, or generate a parse error. If this is EOF,
	 * the string "EOF" is returned.
	 */
	protected String
	getWordOrSymbolOrEof()
		throws IOException
	{
		int tok = st.nextToken();

		if( tok == StreamTokenizer.TT_NUMBER || tok == QUOTE_CHAR )
			parseError( "expecting a word or symbol, " + gotWhat( tok ) );

		if( tok == StreamTokenizer.TT_WORD )
		     return st.sval;
		else if( tok == StreamTokenizer.TT_EOF )
		     return "EOF";
		else return Character.toString( (char) tok );
	}

	/**
	 * Read a word or symbol or string or EOF or generate a parse error.
	 */
	protected String
	getWordOrSymbolOrStringOrEof()
		throws IOException
	{
		int tok = st.nextToken();

		if( tok == StreamTokenizer.TT_NUMBER )
			parseError( "expecting a word, symbol or string, " + gotWhat( tok ) );

		if( tok == QUOTE_CHAR )
			return st.sval;

		if( tok == StreamTokenizer.TT_WORD )
		     return st.sval;
		else if( tok == StreamTokenizer.TT_EOF )
		     return "EOF";
		else return Character.toString( (char) tok );
	}

	/**
	 * Read a number or generate a parse error.
	 */
	protected double
	getNumber()
		throws IOException
	{
		int tok = st.nextToken();

		if( tok != StreamTokenizer.TT_NUMBER )
			parseError( "expecting a number, " + gotWhat( tok ) );

		return st.nval;
	}

	/**
	 * Read a number (possibly with an exponent) or generate a parse error.
	 */
	protected double
	getNumberExp()
		throws IOException
	{
		int tok = st.nextToken();

		if( tok != StreamTokenizer.TT_NUMBER )
			parseError( "expecting a number, " + gotWhat( tok ) );
	
		double nb  = st.nval;

		tok = st.nextToken();

		if( tok == StreamTokenizer.TT_WORD && ( st.sval.startsWith( "e-" ) || st.sval.startsWith( "e+" ) ) )
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
	protected String
	gotWhat( int token )
	{
		switch( token )
		{
			case StreamTokenizer.TT_NUMBER: return "got number `" + st.nval + "'";
			case StreamTokenizer.TT_WORD:   return "got word `" + st.sval + "'";
			case StreamTokenizer.TT_EOF:    return "got EOF";
			default:
				if( token == QUOTE_CHAR )
				     return "got string constant `" + st.sval + "'";
				else return "unknown symbol `" + token + "' (" + ((char)token) + ")";
		}
	}

	/**
	 * Generate a parse error.
	 */
	protected void
	parseError( String message )
		throws IOException
	{
		throw new ParseException(
			"parse error: " + filename + ": " + st.lineno() + ": " + message );
	}

// Accessors

	/**
	 * True if the <code>string</code> represents a truth statement ("1",
	 * "true", "yes", "on").
	 */
	protected boolean
	isTrue( String string )
	{
		if( string.equals( "1" ) )    return true;
		if( string.equals( "true" ) ) return true;
		if( string.equals( "yes" ) )  return true;
		if( string.equals( "on" ) )   return true;

		return false;
	}

	/**
	 * True if the <code>string</code> represents a false statement ("0",
	 * "false", "no", "off").
	 */
	protected boolean
	isFalse( String string )
	{
		if( string.equals( "0" ) )     return true;
		if( string.equals( "false" ) ) return true;
		if( string.equals( "no" ) )    return true;
		if( string.equals( "off" ) )   return true;

		return false;
	}

	/**
	 * Uses {@link #isTrue(String)} and {@link #isFalse(String)} to determine
	 * if <code>value</code> is a truth value and return the corresponding
	 * boolean.
	 *
	 * @throws NumberFormatException if the <code>value</code> is not a truth
	 * value.
	 */
	protected boolean
	getBoolean( String value )
		throws NumberFormatException
	{
		if( isTrue( value ) )  return true;
		if( isFalse( value ) ) return false;
		throw new NumberFormatException( "not a truth value `" + value + "'" );
	}

	/**
	 * Try to transform <code>value</code> into a double.
	 * @throws NumberFormatException if the <code>value</code> is not a double.
	 */
	protected double
	getReal( String value )
		throws NumberFormatException
	{
		return Double.parseDouble( value );
	}

	/**
	 * Try to transform <code>value</code> into a long.
	 * @throws NumberFormatException if the <code>value</code> is not a long.
	 */
	protected long
	getInteger( String value )
		throws NumberFormatException
	{
		return Long.parseLong( value );
	}
/*
	 **
	 * Get a number triplet with numbers separated by comas and return a new
	 * point for it. For example "0,1,2".
	 *
	protected Point3
	getPoint3( String value )
		throws NumberFormatException
	{
		int p0 = value.indexOf( ',' );
		int p1 = value.indexOf( ',',  p0+1 );

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

		throw new NumberFormatException( "value '"+value+"' not in a valid point3 format" );
	}

	 **
	 * Get a number triplet with numbers separated by comas and return new
	 * bounds for it. For example "0,1,2".
	 *
	protected Bounds3
	getBounds3( String value )
		throws NumberFormatException
	{
		int p0 = value.indexOf( ',' );
		int p1 = value.indexOf( ',',  p0+1 );

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

		throw new NumberFormatException( "value '"+value+"' not in a valid point3 format" );
	}
*/
// Nested classes

/**
 * Currently processed file.
 *
 * <p>The graph reader base can process includes in files, and handles a stack
 * of files.</p>
 * 
 * @author Antoine Dutot
 * @author Yoann Pigné
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
	
	public
	CurrentFile( String f, StreamTokenizer t )
	{
		file = f;
		tok  = t;
	}
}

/**
 * 
 * Parse error exception.
 *
 * @author Antoine Dutot
 * @author Yoann Pigné
 * @since  20061116
 */
public static class ParseException extends IOException
{
	private static final long serialVersionUID = 1L;

	public
	ParseException() {}
	
	public
	ParseException( String message ) { super( message ); }
}

}