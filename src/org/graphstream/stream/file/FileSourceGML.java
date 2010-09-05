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
import java.io.InputStream;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.graphstream.ui.geom.Point3;


/**
 * Graph reader for GML files.
 * 
 * <p>
 * This is an old file that respect a large part of the GML syntax but not all. It also adds
 * extensions to allow to include other files and to read and store complex (class) attributes.
 * </p>
 * 
 * TODO: check this effectively respects the GML syntax.
 * TODO: document the additional features.
 */
public class FileSourceGML extends FileSourceBase
{
// Attribute

	/**
	 * Currently built graph or null.
	 */
	protected CurrentGraph CG;

	/**
	 * Currently built node or null.
	 */
	protected CurrentNode CN;

	/**
	 * Currently built edge or null.
	 */
	protected CurrentEdge CE;

	/**
	 * Is the graph directed?. Set as soon as the "directed" property of the
	 * graph is read. It applies only on edged declared after this property is
	 * read.
	 */
	protected boolean directed = false;

	/**
	 * Are we currently inside an included file?. Numbers larger than 0 mean yes
	 * (and give the number of nested includes), 0 means no.
	 */
	protected int inInclude = 0;
	
	/**
	 * Allocator for elements without identifier.
	 */
	protected int newTag = 1;

	/**
	 * Set to true as soon as the main file finished.
	 */
	protected boolean finished = false;
	
	/**
	 * The graph name.
	 */
	protected String graphName = "GML_";
	
// Construction

	public FileSourceGML()
	{
	}

// Access

	/**
	 * Gives access to the currently parsed element (a graph, an edge or a node).
	 * 
	 * @return an instance of {@link CurrentThing} (that may be an instance of
	 *         {@link CurrentNode}, {@link CurrentEdge} or {@link CurrentGraph}
	 *         or null if nothing is currently parsed, in rare case).
	 */
	protected CurrentThing currentThing()
	{
		if( CE != null )
			return CE;

		if( CN != null )
			return CN;

		return CG;
	}

// Parsing -- By-event.

	@Override
	public void begin( String filename ) throws  IOException
	{
		super.begin( filename );
		init();
	}
	
	@Override
	public void begin( InputStream stream ) throws  IOException
	{
		super.begin( stream );
		init();
	}
	
	@Override
	public void begin( Reader reader ) throws  IOException
	{
		super.begin( reader );
		init();
	}
	
	@Override
	public void begin( URL url ) throws IOException
	{
		super.begin( url );
		init();
	}
	
	protected void init() throws IOException
	{
		CG = new CurrentGraph();

		String v = getWord();
		
		if( v.toLowerCase().equals( "creator" ) )
		     CG.addAttribute( "creator", getWordOrString() );
		else pushBack();
		
		eatWord( "graph" );
		eatSymbol( '[' );
		
		graphName = String.format( "%s_%d", graphName, System.currentTimeMillis()+((long)Math.random()*10) );
	}

	@Override
	public boolean nextEvents() throws  IOException
	{
		if( finished )
			return false;
		
		return next( false, true );
	}
	
	public boolean nextStep() throws  IOException
	{
		if( finished )
			return false;
		
		return next( true, false );
	}
	
	protected boolean next( boolean readStep, boolean stop ) throws  IOException
	{
		String  w;

		boolean loop = readStep;

		do
		{
			w = getWordOrSymbolOrEof();
			w = w.toLowerCase();
	
			if( w.equals( "directed" ) )
			{
				directed = parseBooleanProperty( w );
			}
			else if( w.equals( "id" ) || w.equals( "name" ) )
			{
				graphName = parseId();
				CG.attrs.put( "label", graphName );
				sendGraphAttributeAdded( graphName, "label", graphName );
				
				graphName = String.format( "%s_%d", graphName, System.currentTimeMillis()+((long)Math.random()*10) );
			}
			else if( w.equals( "map" ) )
			{
				String attr = getWordOrString();
				String cls  = getWordOrString();
				addAttributeClass( attr, cls );
			}
			else if( w.equals( "node" ) )
			{
				parseNode( Mode.ADD );
			}
			else if( w.equals( "edge" ) )
			{
				parseEdge( Mode.ADD );
			}
			else if( w.equals( "chgnode" ) )
			{
				parseNode( Mode.CHANGE );
			}
			else if( w.equals( "chgedge" ) )
			{
				parseEdge( Mode.CHANGE );
			}
			else if( w.equals( "delnode" ) )
			{
				eatSymbol( '[' );
				eatWord( "id" );
				
				String id = getWordOrSymbolOrNumberOrStringOrEolOrEof();
				
				eatSymbol( ']' );
				sendNodeRemoved( graphName, id );
			}
			else if( w.equals( "deledge" ) )
			{
				eatSymbol( '[' );
				eatWord( "id" );

				String id = getWordOrSymbolOrNumberOrStringOrEolOrEof();
				
				eatSymbol( ']' );
				sendEdgeRemoved( graphName, id );
			}
			else if( w.equals( "graphics" ) )
			{
				parseGraphics();
			}
			else if( w.equals( "external" ) )
			{
				parseExternalCommand();
			}
			else if( w.equals( "Version" ) || w.equals( "version" ) )
			{
				String version = getWordOrSymbolOrNumberOrStringOrEolOrEof();
				
				currentThing().addAttribute( "version", version );
			}
			else if( w.equals( "Creator" ) || w.equals( "creator" ) )
			{
				String creator = getWordOrSymbolOrNumberOrStringOrEolOrEof();
				
				currentThing().addAttribute( "creator", creator );
			}
			else if( w.equals( "step" ) )
			{
				if( readStep )
				{
					if( stop )
					{
						loop = false;
						pushBack();
					}
					else
					{
						stop = true;
						parseStep();
					}
				}
				else
				{
					parseStep();
				}
			}
			else if( w.equals( "include" ) )
			{
				inInclude++;
				pushTokenizer( getString() );
			}
			else if( w.equals( "]" ) )
			{
				if( inInclude > 0 )
					parseError( "expecting EOF here, got `]'" );
	
				// Do nothing, parsing finished.
				eatEof();
				finished = true;
				return false;
			}
			else if( w.equals( "EOF" ) )
			{
				if( inInclude > 0 )
				{
					inInclude--;
					popTokenizer();
				}
				else
				{
					if( ! finished )
						parseError( "expecting `]' here, got EOF inlude="+inInclude );
					return false;
				}
			}
			else
			{
				parseUnknown( w );
			}
		}
		while( loop );
		
		return true;
	}
	
	@Override
	public void end()
		throws  IOException
	{
		super.end();
	}

// Parsing.

	/**
	 * Parse an id string (the "id" token should have been eaten), and return
	 * it. The current element is not modified.
	 */
	protected String parseId() throws IOException
	{
		return getStringOrWordOrNumber();	// Allows the GML to parse single unquoted words.
	}

	/**
	 * Parse an unknown thing that may be interesting to the listeners.
	 */
	protected void parseExternalCommand() throws IOException
	{
		/*String command =*/ getString();
		// NOP. Not supported by the event system.
	}
	
	/**
	 * Parse a comment and store it as a property inside the current element.
	 */
	protected void parseComment() throws IOException
	{
		currentThing().addAttribute( "comment", getString() );
	}

	/**
	 * Parse the boolean value of a property, and store it in the current
	 * element.
	 * @return True if the property value means true.
	 */
	protected boolean parseBooleanProperty( String property ) throws IOException
	{
		String s;

		s = getStringOrWordOrNumber();

		if( ( ! isTrue( s ) ) && ( ! isFalse( s ) ) )
			parseError( "expecting boolean value (0/1,\"true\"/\"false\",\"yes\"/\"no\",\"on\"/\"off\"), got `" + s + "'" );

		boolean res = isTrue( s );
		
		currentThing().addAttribute( property, s );
		
		return res;
	}

	/**
	 * Parse the integer number value of an attribute, and store it in the
	 * current element as an Integer.
	 */
	protected void parseIntegerAttribute( String attribute ) throws IOException
	{
		double n;

		n = getNumber();

		if( n - ((int)n) != 0 )
			parseError( "expecting an integer not a real, got `" + n + "'" );

		currentThing().addAttribute( attribute, new Integer( (int) n ) );
	}

	/**
	 * Parse the real number value of an attribute, and store it in the current
	 * element as a Double.
	 */
	protected void parseRealAttribute( String attribute ) throws IOException
	{
		double n;

		n = getNumber();
		
		currentThing().addAttribute( attribute, new Double( n ) );
	}
	
	/**
	 * Parse a step.
	 */
	protected void parseStep() throws IOException
	{
		sendStepBegins( graphName, getNumber() );
	}

// Parsing -- Nodes and edges.

	/**
	 * Parse a node (the "node" keyword being eaten already).
	 */
	protected void parseNode( Mode mode ) throws IOException
	{
		CN = new CurrentNode();

		eatSymbol( '[' );
		parseNodeContent();
		nodeEvent( mode );
		
		CN = null;
	}

	/**
	 * Generate an add node event toward all listeners. Use the current node.
	 */
	protected void nodeEvent( Mode mode ) throws IOException
	{
		if( CN.tag == null )
			CN.tag = Integer.toString( newTag++ );

		if( mode == Mode.ADD )
		{
			sendNodeAdded( graphName, CN.tag );
			
			if( CN.attrs != null )
			{
				for( String key: CN.attrs.keySet() )
				{
					Object value = CN.attrs.get( key );
					sendNodeAttributeAdded( graphName, CN.tag, key, value );
				}
			}
		}
		else if( mode == Mode.CHANGE )
		{
			if( CN.attrs != null )
			{
				for( String key: CN.attrs.keySet() )
				{
					Object value = CN.attrs.get( key );
					sendNodeAttributeChanged( graphName, CN.tag, key, null, value );
				}
			}
		}
	}

	protected void parseNodeContent() throws IOException
	{
		String  w;
		boolean loop = true;

		while( loop )
		{
			w = getWordOrSymbolOrEof();
			w = w.toLowerCase();

			if( w.equals( "id" ) )
			{
				CN.tag = parseId();
			}
			else if( w.equals( "include" ) )
			{
				include( getString() );
			}
			else if( w.equals( "graphics" ) )
			{
				parseGraphics();
			}
			else if( w.equals( "]" ) )
			{
				if( inInclude > 0 )
					parseError( "expecting EOF here, got `]'" );

				loop = false;
			}
			else if( w.equals( "EOF" ) )
			{
				if( inInclude == 0 )
					parseError( "expecting `]' here, got EOF" );

				loop = false;
			}
			else
			{
				parseUnknown( w );
			}
		}
	}

	/**
	 * Generate an add edge event toward all listeners. Use the current edge.
	 */
	protected void parseEdge( Mode mode ) throws IOException
	{
		CE = new CurrentEdge();

		eatSymbol( '[' );
		parseEdgeContent();
		edgeEvent( mode );

		CE = null;
	}

	/**
	 * A new edge {@link #CE} has been read.
	 */
	protected void edgeEvent( Mode mode ) throws IOException
	{
		if( CE.src != null && CE.trg != null )
		{
			if( CE.tag == null )
				CE.tag = Integer.toString( newTag++ );

			boolean dir = directed;
			
			// Force the direction if indicated. This allows to un-direct an
			// edge even if the whole graph is directed or direct an edge even
			// if the whole graph is undirected.
			
			if( CE.directed == CurrentEdge.Direction.DIRECTED )
				dir = true;
			else if( CE.directed == CurrentEdge.Direction.UNDIRECTED )
				dir = false;
			
			if( mode != Mode.ADD )
				parseError( "cannot specify a source or target when changing an edge." );
			
			sendEdgeAdded( graphName, CE.tag, CE.src, CE.trg, dir );
			
			if( CE.attrs != null )
			{
				for( String key: CE.attrs.keySet() )
				{
					Object value = CE.attrs.get( key );
					sendEdgeAttributeAdded( graphName, CE.tag, key, value );
				}
			}
		}

		if( CE.src == null || CE.trg == null )
		{
			if( CE.src == null && CE.trg == null )
			{
				if( mode != Mode.CHANGE )
					parseError( "cannot add an edge without source and target" );
				
				if( CE.attrs != null )
				{
					for( String key: CE.attrs.keySet() )
					{
						Object value = CE.attrs.get( key );
						sendEdgeAttributeChanged( graphName, CE.tag, key, null, value );
					}
				}
			}
			else if( CE.src != null && CE.trg == null )
			{
				parseError( "only the source node was provided for edge, need a target" );
			}
			else if( CE.src == null && CE.trg != null )
			{
				parseError( "only the target node was provided for edge, need a source" );
			}
		}		
	}

	protected void parseEdgeContent()
		throws IOException
	{
		String  w;
		boolean loop = true;

		while( loop )
		{
			w = getWordOrSymbolOrEof();
			w = w.toLowerCase();

			if( w.equals( "id" ) )
			{
				CE.tag = parseId();
			}
			else if( w.equals( "source" ) )
			{
				CE.src = parseId();
			}
			else if( w.equals( "target" ) )
			{
				CE.trg = parseId();
			}
			else if( w.equals( "include" ) )
			{
				include( getString() );
			}
			else if( w.equals( "graphics" ) )
			{
				parseGraphics();
			}
			else if( w.equals( "directed" ) )
			{
				String s = getWordOrNumberOrStringOrEolOrEof();
				
				if( ( ! isTrue( s ) ) && ( ! isFalse( s ) ) )
					parseError( "expecting boolean value (0/1,\"true\"/\"false\",\"yes\"/\"no\",\"on\"/\"off\"), got `" + s + "'" );

				if( isTrue( s ) )
					CE.directed = CurrentEdge.Direction.DIRECTED;
				else if( isFalse( s ) )
					CE.directed = CurrentEdge.Direction.UNDIRECTED;
			}
			else if( w.equals( "]" ) )
			{
				if( inInclude > 0 )
					parseError( "expecting EOF here, got `]'" );

				loop = false;
			}
			else if( w.equals( "EOF" ) )
			{
				if( inInclude == 0 )
					parseError( "expecting `]' here, got EOF" );

				loop = false;
			}
			else
			{
				parseUnknown( w );
			}
		}
	}
	
// Parsing -- Graphics
	
	protected void parseGraphics()
		throws  IOException
	{
		String key;
		boolean loop = true;
		
		eatSymbol( '[' );
		
		CurrentThing ct = currentThing();
		
		do
		{
			key = getWordOrSymbolOrEof();
			key = key.toLowerCase();
			
			if( key.equals( "x" ) || key.equals( "y" ) || key.equals( "z" )  )
			{
				double value = getNumber();
				
				ct.addAttribute( key, value );
			}
			else if( key.equals( "w" ) )
			{
				double value = getNumber();
				
				ct.addAttribute( "width", value );
			}
			else if( key.equals( "h" ) )
			{
				double value = getNumber();
				
				ct.addAttribute( "height", value );
			}
			else if( key.equals( "d" ) )
			{
				double value = getNumber();
				
				ct.addAttribute( "depth", value );				
			}
			else if( key.equals( "color" ) )
			{
				String clr = getWordOrSymbolOrNumberOrStringOrEolOrEof();
				
				ct.addAttribute( "color", clr );
			}
			else if( key.equals( "type" ) )
			{
				String type = getWordOrString();
				
				if( type.equals( "arc" ) )
				{
					ct.addAttribute( "arc", readPoints() );
				}
				else if( type.equals( "bitmap" ) || type.equals( "image" ) )
				{
					String name = getWordOrString();

					ct.addAttribute( "icon", name );
				}
				else if( type.equals( "line" ) )
				{
					ct.addAttribute( "line", readPoints() );
				}
				else if( type.equals( "oval" ) )
				{
					ct.addAttribute( "oval", readPoints() );
				}
				else if( type.equals( "polygon" ) )
				{
					ct.addAttribute( "polygon", readPoints() );
				}
				else if( type.equals( "rectangle" ) )
				{
					ct.addAttribute( "rectangle", null );
				}
				else if( type.equals( "text" ) )
				{
					String text = getWordOrSymbolOrNumberOrStringOrEolOrEof();
					
					ct.addAttribute( "label", text );
				}
				else
				{
					parseUnknownAndForget( key );
					//parseError( "expecting 'arc', 'bitmap', 'image', 'line', 'oval', 'polygon', 'rectangle' or 'text', got '"+type+"'" );
				}
			}
			else if( key.equals( "]" ) )
			{
				loop = false;
			}
			else
			{
				parseError( "expecting 'x', 'y', 'z', 'w', 'h', 'd', ot 'type'" );
			}
		
		} while( loop );
	}
	
	/**
	 * Read a sequence of points definition.
	 * @return The point sequences as an array list.
	 */
	protected ArrayList<Point3> readPoints()
		throws IOException
	{
		boolean loop = true;
		String  key  = null;

		ArrayList<Point3> points = new ArrayList<Point3>();
		
		eatSymbol( '[' );
		
		while( loop )
		{
			key = getWordOrSymbolOrString();
			
			if( key.equals( "point" ) )
			{
				points.add( readPoint() );
			}
			else if( key.equals( "]" ) )
			{
				loop = false;
			}
			else
			{
				parseError( "expecting 'point' or ']', got '"+key+"'" );
			}
		}
		
		return points;
	}
	
	/**
	 * Read a sing point.
	 * @return Return the point.
	 */
	protected Point3 readPoint()
		throws IOException
	{
		boolean loop  = true;
		String  key   = null;
		Point3  point = new Point3();
		
		eatSymbol( '[' );
		
		while( loop )
		{
			key = getWordOrSymbolOrString();
			
			if( key.equals( "x" ) )
			{
				float x = (float) getNumber();
				
				point.setX( x );
			}
			else if( key.equals( "y" ) )
			{
				float y = (float) getNumber();
				
				point.setY( y );
			}
			else if( key.equals( "z" ) )
			{
				float z = (float) getNumber();
				
				point.setZ( z );				
			}
			else if( key.equals( "]" ) )
			{
				loop = false;
			}
			else
			{
				parseError( "expecting 'x', 'y', 'z', or ']', got '"+key+"'" );
			}
		}
		
		return point;
	}

// Parsing -- Unknown property or attribute class loading and setting
// mechanism.

	/**
	 * Parse an unknown attribute or property. An attribute is a name followed
	 * by a "[..]" block. A property is a name followed by a string constant, a
	 * word or a number.
	 * @param unknown The name of the unknown attribute.
	 */
	protected void parseUnknown( String unknown )
		throws 
			   IOException
	{
		int tok = st.nextToken();

		if( tok == '[' )
		{
			Object attr = parseUnknownAttribute( unknown );

			currentThing().addAttribute( unknown, attr );
		}
		else if( tok  == '"' )
		{
			currentThing().addAttribute( unknown, st.sval );
		}
		else if( tok == StreamTokenizer.TT_NUMBER )
		{
			currentThing().addAttribute( unknown, st.nval );
		}
		else if( tok == StreamTokenizer.TT_WORD )
		{
			currentThing().addAttribute( unknown, st.sval );
		}
		else
		{
			parseError( "waiting a '[', a string constant, a word or a number, " + gotWhat( tok ) );
		}
	}
	
	/**
	 * Like {@link #parseUnknown(String)} but dot insert the parsed thing in the
	 * current object.
	 * @param unknown The name of the unknown attribute.
	 */
	protected void parseUnknownAndForget( String unknown )
		throws  IOException
	{
		int tok = st.nextToken();

		if( tok == '[' )
		{
			parseUnknownAttribute( unknown );
		}
		else if( tok  == '"' )
		{
		}
		else if( tok == StreamTokenizer.TT_NUMBER )
		{
		}
		else if( tok == StreamTokenizer.TT_WORD )
		{
		}
		else
		{
			parseError( "waiting a '[', a string constant, a word or a number, " + gotWhat( tok ) );
		}		
	}

	/**
	 * Parse an unknown attribute and store it inside the current element (the
	 * attribute and first '[' have been eaten already). The attribute must
	 * have a description to be parsed. Use {@link
	 * #addAttributeClass(String,String)}. If the parser as no information on
	 * what class to instantiate when this attribute is read, the attribute is
	 * only skipped.
	 * @param unknown The attribute name.
	 * @return The instantiated and filled object if a mapping from the name of
	 *         the attribute to a class name exists (via the "map" keyword or
	 *         via the addAttributeClass() method). Else null is returned.
	 */
	protected Object parseUnknownAttribute( String unknown )
		throws 
			   IOException
	{
		String   w, s, c;
		boolean  loop = true;
		Class<?> cls;
		Object   obj;

		c = attribute_classes.get( unknown );

		if( c == null )
		{
			// Read and ignore the attribute.
			
			int count = 0;

			while( loop )
			{
				w = getAllExceptedEof();

				if( w.equals( "[" ) )
				{
					count++;
				}
				else if( w.equals( "]" ) )
				{
					if( count > 0 )
					     count--;
					else loop = false;
				}
			}
			
			System.err.printf( "GraphReaderGML: ignoring attribute '%s', not class map.%n", unknown );

			return null;
		}
		else
		{
			// Read and store the attribute.
			
			cls = findClass( c );
			obj = instanciateClass( cls );
			
//			System.err.printf( "GraphReaderGML: mapping attribute '%s' to class '%s'.%n", unknown, cls.getName() );
			
			while( loop )
			{
				w = getWordOrSymbol();

				if( w.equals( "]" ) )
				{
					loop = false;
				}
				else
				{
					s = getAllExceptedEof();

					if( s.equals( "[" ) )
					{
						try
						{
							Field  field = cls.getField( w );
							Object fobj  = readComposedField( w, field );
						
							field.set( obj, fobj );
						}
						catch( IllegalAccessException e )
						{
							parseError( "cannot modify field '"+w+"' of class '"+cls.getName()+"': " + e.getMessage() );
						}
						catch( NoSuchFieldException e )
						{
							parseError( "cannot find field '"+w+"' of class '"+cls.getName()+"': " + e.getMessage() );
						}
					}
					else
					{
						storeFieldValue( cls, obj, w, s );
					}
				}
			}

			return obj;
		}
	}
	
	/**
	 * Recursive method to read the contents of a field that is in fact an object.
	 * If this object is itself composed of fields, they are read recursively.
	 * @param fieldName The name of the field.
	 * @param field The field to read.
	 * @return The object read.
	 */
	protected Object readComposedField( String fieldName, Field field )
		throws 
			IOException
	{
		// The "[" has been eaten.
		
		boolean  loop = true;
		String   key  = null;
		Class<?> cls  = field.getClass();
		Object   obj  = instanciateClass( cls );
		
		while( loop )
		{
			key = getWordOrSymbol();
			
			if( key.equals( "]" ) )
			{
				loop = false;
			}
			else
			{
				String s = getAllExceptedEof();
				
				if( s.equals( "[" ) )
				{
					try
					{
						Field  f2   = cls.getField( key );
						Object fobj = readComposedField( key, f2 );
					
						f2.set( obj, fobj );
					}
					catch( IllegalAccessException e )
					{
						parseError( "cannot modify field '"+key+"' of class '"+cls.getName()+"': " + e.getMessage() );
					}
					catch( NoSuchFieldException e )
					{
						parseError( "cannot find field '"+key+"' of class '"+cls.getName()+"': " + e.getMessage() );
					}
				}
				else
				{
					storeFieldValue( cls, obj, key, s );
				}
			}
		}
		
		return obj;
	}

	/**
	 * Find a class by its name and return it.
	 * @param cls The class name.
	 * @return The class definition.
	 */
	protected Class<?> findClass( String cls )
		throws 
			   IOException
	{
		Class<?> c = null;

		try
		{
			c = Class.forName( cls );
		}
		catch( ExceptionInInitializerError e )
		{
			parseError( "cannot initialize class `" + cls + "': " + e.getMessage() );
		}
		catch( LinkageError e )
		{
			parseError( "cannot link class `" + cls + "': " + e.getMessage() );
		}
		catch( ClassNotFoundException e )
		{
			parseError( "cannot find class `" + cls + "': " + e.getMessage() );
		}

		return c;
	}

	/**
	 * Instantiate a given class using its default constructor. This method is
	 * mainly here to throw appropriate parse errors if instantiation fails.
	 * @param cls The class.
	 * @return The new instance of the class.
	 */
	protected Object instanciateClass( Class<?> cls )
		throws IOException		       
	{
		Object obj = null;

		try
		{
			obj = cls.newInstance();
		}
		catch( ExceptionInInitializerError e )
		{
			parseError( "cannot instanciate class `" + cls.getName() + "': " + e.getMessage() );
		}
		catch( InstantiationException e )
		{
			parseError( "cannot instanciate class `" + cls.getName() + "': " + e.getMessage() );
		}
		catch( IllegalAccessException e )
		{
			parseError( "cannot instanciate class `" + cls.getName() + "', illegal access: " + e.getMessage() );
		}
		catch( SecurityException e )
		{
			parseError( "cannot instanciate class `" + cls.getName() + "', security error: " + e.getMessage() );
		}

		return obj;
	}

	/**
	 * Store a value (given as a string) inside the field of an object. The
	 * value is converted to the type of the field automatically.
	 * @param cls The class of the object where the value will be stored.
	 * @param obj The object where the value will be stored.
	 * @param field The field of the object where the value will be stored.
	 * @param value The value to store.
	 */
	protected void storeFieldValue( Class<?> cls, Object obj, String field, String value )
		throws IOException
	{
		Field fields[] = cls.getFields();

		for( int i=0; i<fields.length; i++ )
		{
			if( fields[i].getName().equals( field ) )
			{
				Class<?> c = fields[i].getType();

				try
				{
					if( c.equals( Boolean.TYPE ) )
					{
						fields[i].setBoolean( obj, getBoolean( value ) );
					}
					else if( c.equals( Byte.TYPE ) )
					{
						fields[i].setByte( obj, (byte) getInteger( value ) );
					}
					else if( c.equals( Character.TYPE ) )
					{
						fields[i].setChar( obj, value.charAt( 0 ) );
					}
					else if( c.equals( Double.TYPE ) )
					{
						fields[i].setDouble( obj, getReal( value ) );
					}
					else if( c.equals( Float.TYPE ) )
					{
						fields[i].setFloat( obj, (float) getReal( value ) );
					}
					else if( c.equals( Integer.TYPE ) )
					{
						fields[i].setInt( obj, (int) getInteger( value ) );
					}
					else if( c.equals( Short.TYPE ) )
					{
						fields[i].setShort( obj, (short) getInteger( value ) );
					}
					else if( c.equals( Long.TYPE ) )
					{
						fields[i].setLong( obj, getInteger( value ) );
					}
					else if( c.equals( String.class ) )
					{
						fields[i].set( obj, value );
					}
					else if( c.equals( org.graphstream.ui.geom.Point3.class ) )
					{
						fields[i].set( obj, getPoint3( value ) );
					}
					else if( c.equals( org.graphstream.ui.geom.Bounds3.class ) )
					{
						fields[i].set( obj, getBounds3( value ) );
					}
					else
					{
						parseError( "unknown field type `" + c.getName() + "', cannot set its value to `" + value + "'" );
					}
				}
				catch( IllegalAccessException e )
				{
					parseError( "cannot access field `" + fields[i].getName() + "' of class `" + c.getName() + "'" );
				}
				catch( NumberFormatException e )
				{
					parseError( "cannot transform value `" + value + "' into a number (boolean, integer or real)" );
				}

				return;
			}
		}

		parseError( "cannot find field `" + field + "' in class `" + cls.getName() + "'" );
	}

// Parsing -- Include mechanism

	/**
	 * Continue parsing inside an included file. The current state is used to
	 * determine where to continue parsing.
	 */
	@Override
	protected void continueParsingInInclude()
		throws IOException
	{
		if( CE != null )
		{
			parseEdgeContent();
		}
		else if( CN != null )
		{
			parseNodeContent();
		}
		else
		{
			boolean loop = true;
			
			while( loop )
			{
				loop = nextEvents();
			}
		}
	}

// Nested classes

	protected static enum Mode { ADD, CHANGE };
	
	/**
	 * Used as buffer during the construction of an element.
	 */
	protected static class CurrentThing
	{
		public HashMap<String,Object> attrs = new HashMap<String,Object>();

		public void addAttribute( String attribute, Object value )
		{
			attrs.put( attribute, value );
		}
	}

	/**
	 * Used as buffer during the construction of the graph.
	 */
	protected static class CurrentGraph
		extends CurrentThing
	{
		public CurrentGraph() {}
	}

	/**
	 * Used as buffer during the construction of an edge.
	 */
	protected static class CurrentEdge
		extends CurrentThing
	{
		protected static enum Direction { DIRECTED, UNDIRECTED, NOT_SPECIFIED }
		
		public String    tag      = null;
		public String    src      = null;
		public String    trg      = null;
		public Direction directed = Direction.NOT_SPECIFIED;
	}

	/**
	 * Used as buffer during the construction of a node.
	 */
	protected static class CurrentNode
		extends CurrentThing
	{
		public String tag = null;
	}
}