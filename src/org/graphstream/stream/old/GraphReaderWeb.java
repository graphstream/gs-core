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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.graphstream.graph.NotFoundException;
import org.graphstream.io.GraphParseException;

/**
 * Browses web pages recursively from a start URL and associates nodes to pages,
 * and edges to links between pages. 
 *
 * <p>
 * This reader does not read a file but web pages. It starts from a start URL
 * and creates a single node that represent it. Then it searches for each link in
 * this page, creates new nodes for each page referenced by a link and adds edges
 * between its current page and all the referenced pages.
 * </p>
 * 
 * <p>
 * For each newly referenced page, this process is executed anew until a given
 * limit : the depth. However, as you may not expect, the browsing is not
 * depth-first, but breadth-first.
 * </p>
 * 
 * <p>
 * As pages will be quickly numerous, there is an option to reference only the web
 * sites, that is the "host name" part of the URL discovered via the links. The pages
 * are still all parsed, but only new sites are reported as nodes.
 * </p>
 * 
 * <p>
 * Actually, the web page parser is not completely a HTML parser and it may skip
 * some links that it cannot parse. You can activate an error output on the
 * console or the output stream of your choice, or store errors in an error log
 * to see what links are ignored, what sites did not responded, what sites caused
 * a parse error, etc.
 * </p>
 * 
 * TODO: add "cookie" support.
 * TODO: add a "blacklist" to avoid fetching some sites.
 *
 * @since 2007
 */
public class GraphReaderWeb implements GraphReader
{
// Attributes
	
	/**
	 * Dictionary of pages/sites visited and remaining to visit.
	 */
	protected Dictionary dict;
	
// Constructors
	
	/**
	 * New reader that browse web pages recursively. It stop at the given
	 * recursive depth.
	 * @param maxDepth The recursive depth.
	 * @param onlySites Parse only web sites, not all the pages.
	 * @param debug If true, output what is parsed on the standard output.
	 */
	public GraphReaderWeb( int maxDepth, boolean onlySites, boolean debug )
	{
		dict = new Dictionary( maxDepth, onlySites, debug );
	}
	
	/**
	 * New reader that browse web pages recursively. It stop at the recursive
	 * depth of 3 by default.
	 */
	public GraphReaderWeb()
	{
		dict = new Dictionary( 3, false, false );
	}
	
// Access
	
	/**
	 * Number of pages or sites visited so far.
	 * This number is the number of pages or the number of sites seen and parsed so far. It is
	 * the number is sites when the {@link #setOnlySites(boolean)} method was called with value
	 * "true".
	 * @return The number of sites or pages visited.
	 */
	public int getVisitedPageCount()
	{
		return dict.getVisitedPageCount();
	}
	
// Commands

	/**
	 * Recursive depth of explorations of pages.
	 * @param depth An integer larger than 1.
	 */
	public void setMaxDepth( int depth )
	{
		dict.setMaxDepth( depth );
	}
	
	/**
	 * If true, only the sites are parsed, not all web pages.
	 * @param parseOnlySites If false all pages are parsed (the default).
	 */
	public void setOnlySites( boolean parseOnlySites )
	{
		dict.setOnlySites( parseOnlySites );
	}

	/**
	 * Add an URL that should not be explored. The given URL is an exact match,
	 * that is the URL must exactly correspond to the text given here. 
	 * @param exactMatch The URL to avoid.
	 */
	public void addToBlackList( String exactMatch )
	{
		dict.addToBlackList( exactMatch );
	}
	
	/**
	 * If true, remove all URL parts after the '?' character.
	 * @param on If true remove the URL part after the '?' character.
	 */
	public void setRemoveQueries( boolean on )
	{
		dict.setRemoveQueries( on );
	}
	
	/**
	 * Ask to put a "label" attribute on each node with the URL as value.
	 * @param on If true the label attribute is set (default off).
	 */
	public void setLabelAttributes( boolean on )
	{
		dict.setLabelAttributes( on );
	}
	
	/**
	 * Allow or disallow the output of log information on the console. Mainly
	 * for debugging. It allows to see easily what sites are not found, when
	 * I/O errors occur, or if some parse error occur.
	 * @param on In true the log is enabled. The default is false.
	 */
	public void setShowLog( boolean on )
	{
		dict.setShowLog( on );
	}
	
	/**
	 * Ask that the number of time a site reference another be exported as an attribute for
	 * edges. This will trigger "change edge" events as this number will change as new pages
	 * are discovered. The parameter is the attribute name used to store the value. The value
	 * is stored as an integer on each edge.
	 * @param weightAttribute The attribute name for edge weights.
	 */
	public void setWeightAttribute( String weightAttribute )
	{
		dict.setWeightAttribute( weightAttribute );
	}
	
// Command -- GreaphReader

	/**
	 * Read all pages recursively until a max depth if reached.
	 * @param URL the start url.
	 */
	public void read( String URL ) throws NotFoundException, GraphParseException, IOException
	{
		begin( URL );
		while( nextEvents() ) {}
		end();
	}

	/**
	 * Not implemented, will throw an IOException, we can only start from an URL.
	 */
	public void read( InputStream stream ) throws GraphParseException, IOException
	{
		throw new IOException( "the GraphReaderWeb can only start from an URL" );
	}

	/**
	 * Not implemented, will throw an IOException, we can only start from an URL.
	 */
	public void read( Reader reader ) throws GraphParseException, IOException
	{
		throw new IOException( "the GraphReaderWeb can only start from an URL" );
	}

	/**
	 * Not implemented, will throw an IOException, we can only start from an URL.
	 */
	public void begin( InputStream stream ) throws GraphParseException, IOException
	{
		throw new IOException( "the GraphReaderWeb can only start from an URL" );
	}

	/**
	 * Not implemented, will throw an IOException, we can only start from an URL.
	 */
	public void begin( Reader reader ) throws GraphParseException, IOException
	{
		throw new IOException( "the GraphReaderWeb can only start from an URL" );
	}

	/**
	 * Begin web exploration from the given URL.
	 * @param URL the start URL.
	 */
	public void begin( String URL ) throws NotFoundException, GraphParseException, IOException
	{
		WebItem start = null;
		
		start = new WebItem( URL, null, 0, dict );
		
		dict.register( start, null );
		dict.nodeEvent( start );
		start.parse( dict );
	}
	
	/**
	 * Use this to add a site to explore in addition to the first site.
	 * @param URL The site URL.
	 */
	public void addSite( String URL ) throws NotFoundException, GraphParseException, IOException
	{
		WebItem item = new WebItem( URL, null, 0, dict );
		
		if( ! dict.contains( item ) )
		{
			dict.register( item, null );
			dict.nodeEvent( item );
			dict.addToParse( item );
		}
	}

	/**
	 * End the web exploration and closes all open files cleanly.
	 */
	public void end() throws GraphParseException, IOException
	{
		dict.clear();
	}
	
	/**
	 * Restrict the parsing to sites that contain one of the strings given here.
	 * @param matches An array of strings.
	 */
	public void restrictToSites( String matches[] )
	{
		dict.restrictToSites( matches );
	}

	/**
	 * Parse a new page. Each new page parsed may trigger (for later calls of this method)
	 * the parsing of other new pages if the page contains links (until a max depth is reached).
	 * @return False if there are no more pages to browse, that is the max depth is
	 *         reached (Did you tough you could reach the end of the
	 *         Internet ?).
	 */
	public boolean nextEvents() throws GraphParseException, IOException
	{
		WebItem next = dict.pop();
		
		if( next != null )
		{
			next.parse( dict );
			return true;
		}
		
		return false;
	}

	/**
	 * Exactly the same as {@link #nextEvents()} there is no notion of step on
	 * the web.
	 * @return False if there are no more pages to browse.
	 * @see #nextEvents()
	 */
	public boolean nextStep() throws GraphParseException, IOException
	{
		return false;
	}

	@Deprecated
	public void addGraphReaderListener( GraphReaderListener listener )
	{
		dict.addListener( listener );
	}

	@Deprecated
	public void removeGraphReaderListener( GraphReaderListener listener )
	{
		dict.removeListener( listener );
	}

	public void addGraphReaderListener( GraphReaderListenerExtended listener )
	{
		dict.addListener( listener );
	}

	public void removeGraphReaderListener( GraphReaderListenerExtended listener )
	{
		dict.removeListener( listener );
	}

// Nested classes

	/**
	 * Set of visited pages and set of pages remaining to visit. This also
	 * contains the set of listeners. Each change in the dictionary is sent
	 * to the listeners.
	 */
	protected static class Dictionary
	{
	// Attributes
	
		/**
		 * The maximum depth of exploration. Three by default.
		 */
		public int maxDepth = 3;
		
		/**
		 * Process only web sites, not all pages.
		 */
		public boolean onlySites = false;
		
		/**
		 * Show the error log.
		 */
		public boolean showLog = false;

		/**
		 * The already explored items.
		 */
		protected HashMap<String,WebItem> items;
		
		/**
		 * The items remaining to explore. Order matters.
		 */
		protected LinkedList<WebItem> toExplore;
		
		/**
		 * Set of listeners.
		 */
		protected ArrayList<GraphReaderListener> listeners;
		
		/**
		 * Set of listeners.
		 */
		protected ArrayList<GraphReaderListenerExtended> listeners2;

		/**
		 * List of attribute reused for each node or edge declaration.
		 */
		protected HashMap<String,Object> attributes;
		
		/**
		 * Name allocator for edges.
		 */
		protected int edgeId = 1;
		
		/**
		 * List of strings an URL must contain to be processed.
		 */
		protected String[] restrict; 
		
		/**
		 * If true output what is parsed on standard output.
		 */
		protected boolean debug = false;

		/**
		 * The weight attribute, if null do not export weights.
		 */
		protected String weightAttribute = null;
		
		/**
		 * List of URL patterns that must not be parsed.
		 */
		protected HashSet<Pattern> blackList;

		/**
		 * Remove all the URL part after the '?' character?.
		 */
		protected boolean removeQueries = false;

		/**
		 * Add a "label" attribute whose value is the URL on each node?.
		 */
		protected boolean setLabelAttrs = false;
		
	// Constructors
		
		/**
		 * New empty dictionary.
		 * @param maxDepth The maximum recursive exploration depth.
		 * @param onlySites Only parse sites, not all web pages.
		 * @param debug If true, output what is parsed on standard output.
		 */
		public Dictionary( int maxDepth, boolean onlySites, boolean debug )
		{
			this.maxDepth  = maxDepth > 1 ? maxDepth : 1;
			this.onlySites = onlySites;
			this.debug     = debug;
			
			items      = new HashMap<String,WebItem>();
			toExplore  = new LinkedList<WebItem>();
			listeners  = new ArrayList<GraphReaderListener>();
			listeners2 = new ArrayList<GraphReaderListenerExtended>();
			attributes = new HashMap<String,Object>();
			blackList  = new HashSet<Pattern>();
		}
		
	// Access
		
		/**
		 * True if sites are restricted.
		 * @return True if restricted.
		 * @see #restrictToSites(String[])
		 */
		public boolean isRestricted()
		{
			return( restrict != null );
		}
		
		/**
		 * Test if a HREF is accepted or not according to the site restrictions.
		 * @param href The HREF to test.
		 * @return The given HREF if it is allowed, else null.
		 * @see #restrictToSites(String[])
		 */
		public String restrictHref( String href )
		{
			if( isRestricted() )
			{
				for( String s: restrict )
				{
					if( href.contains( s ) )
						return href;
				}
				
				href = null;
			}
			
			return href;
		}
		
		/**
		 * True if the given URL is on the black list.
		 * @param URL The URL to test.
		 * @return True if the URL is blacklisted.
		 */
		public boolean isBlackListed( String URL )
		{
			for( Pattern p: blackList )
			{
				Matcher m = p.matcher( URL );

				if( m.matches() )
					return true;
			}
			
			return false;
		}
		
		/**
		 * Does the dictionary already contains the item?. 
		 * @param item A site or page.
		 * @return True if the item is already in the dictionary.
		 * @see #contains(String)
		 */
		public boolean contains( WebItem item )
		{
			return contains( item.id );
		}
		
		/**
		 * Does the dictionary already contains a page whose URL is the given
		 * string ?. The dictionary contains the page if it has already been
		 * parsed or is in the list of pages to explore.
		 * @param id The URL of a page as a string.
		 * @return True if the URL is already in the dictionary.
		 */
		public boolean contains( String id )
		{
			return( items.containsKey( id ) );
//			return( items.containsKey( id ) || toExplore.contains( id ) );
		}
		
		/**
		 * The web item corresponding to the given URL or null if no web item references this URL.
		 * @param id The URL of a page.
		 * @return The corresponding web item or null if not referenced.
		 */
		public WebItem get( String id )
		{
			return items.get( id );
		}
		
		/**
		 * Return the next page or site to explore or null if either you reached
		 * the end of the Internet (huh?) or you explored the links at the given
		 * maximum depth.
		 * @return A web page or site or null if parsing is finished.
		 */
		public WebItem pop()
		{
			if( ! toExplore.isEmpty() )
				return toExplore.removeFirst();
			
			return null;

		//	return toExplore.pollFirst();
		/*
			int n = toExplore.size();
			
			if( n > 0 )
				return toExplore.remove( n - 1 );

			return null;
		*/
		}
		
		/**
		 * Number of pages or sites visited so far.
		 * This number is the number of pages or the number of sites seen and parsed so far. It is
		 * the number is sites when the {@link #setOnlySites(boolean)} method was called with value
		 * "true".
		 * @return The number of sites or pages visited.
		 */
		public int getVisitedPageCount()
		{
			return items.size();
		}
		
		/**
		 * If true remove the URL part after the '?'.
		 */
		public boolean getRemoveQueries()
		{
			return removeQueries;
		}
		
	// Commands

		/**
		 * Generate a new unique edge identifier.
		 * @return An unique edge identifier.
		 */
		public String newEdgeId()
		{
			return String.format( "%d", edgeId++ );
		}
		
		/**
		 * Sites must contain one of these strings to be accepted.
		 * @param matches The array of strings.
		 */
		public void restrictToSites( String[] matches )
		{
			restrict = matches;
		}

		/**
		 * Add an URL pattern that should not be explored. The given URL is a Java regular
		 * expression. When parsing pages, if an URL matches one of these patterns, it will
		 * not be used.
		 * @param URLMatch The URL pattern to avoid.
		 */
		public void addToBlackList( String URLMatch )
		{
			blackList.add( Pattern.compile( URLMatch ) );
		}
		
		/**
		 * Recursive depth of explorations of pages.
		 * @param depth An integer larger than 1.
		 */
		public void setMaxDepth( int depth )
		{
			if( depth < 1 )
				depth = 1;
			
			maxDepth = depth;
		}
		
		/**
		 * If true, only the sites are parsed, not all web pages.
		 * @param parseOnlySites If false all pages are parsed (the default).
		 */
		public void setOnlySites( boolean parseOnlySites )
		{
			onlySites = parseOnlySites;
		}
		
		/**
		 * If true, remove all URL parts after the '?' character.
		 * @param on If true remove the URL part after the '?' character.
		 */
		public void setRemoveQueries( boolean on )
		{
			removeQueries = on;
		}
		
		/**
		 * Ask to put a "label" attribute on each node with the URL as value.
		 * @param on If true the label attribute is set (default off).
		 */
		public void setLabelAttributes( boolean on )
		{
			setLabelAttrs = on;
		}
		
		/**
		 * Allow or disallow the output of log information on the console. Mainly
		 * for debugging. It allows to see easily what sites are not found, when
		 * I/O errors occur, or if some parse error occur.
		 * @param on In true the log is enabled. The default is false.
		 */
		public void setShowLog( boolean on )
		{
			showLog = on;
		}
		
		/**
		 * Ask that the number of time a site reference another be exported as an attribute for
		 * edges. This will trigger "change edge" events as this number will change as new pages
		 * are discovered. The parameter is the attribute name used to store the value. The value
		 * is stored as an integer on each edge.
		 * @param weightAttribute The attribute name for edge weights.
		 */
		public void setWeightAttribute( String weightAttribute )
		{
			this.weightAttribute = weightAttribute;
		}

		/**
		 * Clear this dictionary.
		 */
		public void clear()
		{
			items.clear();
			toExplore.clear();
			edgeId = 1;
		}
		
		/**
		 * Register a new page. This marks the page as processed and describe
		 * a new node (item) and link (parentItem to item) to the listeners.
		 * @param item The new page.
		 * @param parentItem The page the links to the given item.
		 */
		public void register( WebItem item, WebItem parentItem )
			throws GraphParseException
		{
			items.put( item.id, item );
		}
		
		/**
		 * Register a page that will only be parsed but not reported as a link.
		 * This is used in "onlySite" modeField. In this modeField we consider
		 * all the pages, but store and report only the sites. However, to
		 * search for other sites we need to parse all pages. To avoid re-parsing
		 * pages we also register the pages in the dictionary but do not
		 * associate the item with them.
		 * @param id The page id (URL as a string).
		 */
		public void registerOriginal( String id )
		{
			items.put( id, null );
		}
		
		/**
		 * Add a new site to the list of pages to explore later.
		 * @param item The item to add.
		 */
		public void addToParse( WebItem item )
		{
			toExplore.add( item );
		}

		/**
		 * Add a listener.
		 * @param listener The listener to add.
		 */
		public void addListener( GraphReaderListener listener )
		{
			listeners.add( listener );
		}
		
		/**
		 * Remove a listener.
		 * @param listener The listener to remove.
		 */
		public void removeListener( GraphReaderListener listener )
		{
			int pos = listeners.indexOf( listener );
			
			if( pos >= 0 )
				listeners.remove( pos );
		}

		/**
		 * Add a listener.
		 * @param listener The listener to add.
		 */
		public void addListener( GraphReaderListenerExtended listener )
		{
			listeners2.add( listener );
		}
		
		/**
		 * Remove a listener.
		 * @param listener The listener to remove.
		 */
		public void removeListener( GraphReaderListenerExtended listener )
		{
			int pos = listeners2.indexOf( listener );
			
			if( pos >= 0 )
				listeners2.remove( pos );
		}

		/**
		 * Send a "new node" event to listeners.
		 * @param item The item to declare.
		 */
		public void nodeEvent( WebItem item )
			throws GraphParseException
		{
			attributes.clear();

			if( setLabelAttrs )
				attributes.put( "label", item.id );
			
			for( GraphReaderListener listener: listeners )
				listener.nodeAdded( item.id, attributes );
			for( GraphReaderListenerExtended listener: listeners2 )
				listener.nodeAdded( item.id, attributes );
		}

		/**
		 * Send a "new edge" event to listeners.
		 * @param from The edge source node.
		 * @param to The edge target node.
		 */
		public void edgeEvent( WebItem from, WebLink to )
			throws GraphParseException
		{
			attributes.clear();
			
			if( weightAttribute != null )
				attributes.put( weightAttribute, 1 );
			
			if( from != null && ( ! from.id.equals( to.id ) ) )
			{
				for( GraphReaderListener listener: listeners )
				{
					try
					{
						listener.edgeAdded( to.id,
								from.id,
								to.to.id,
								true, attributes );
					}
					catch( Exception e )
					{
						e.printStackTrace();
					}
				}
				for( GraphReaderListenerExtended listener: listeners2 )
				{
					try
					{
						listener.edgeAdded( to.id, from.id, to.to.id, true, attributes );
					}
					catch( Exception e )
					{
						e.printStackTrace();
					}
				}
			}			
		}
		
		/**
		 * A edge weight changed.
		 * @param from The source node.
		 * @param to The target node and edge description.
		 */
		public void edgeChangeEvent( WebItem from, WebLink to )
		{
			attributes.clear();
			
			if( weightAttribute != null )
				attributes.put( weightAttribute, to.weight );
			
			for( GraphReaderListener listener: listeners )
			{
				try
				{
					listener.edgeChanged( to.id, attributes );
				}
				catch( Exception e )
				{
					e.printStackTrace();
				}
			}

			if( weightAttribute != null )
			{
				for( GraphReaderListenerExtended listener: listeners2 )
				{
					try
					{
						listener.edgeChanged( to.id, weightAttribute, to.weight, false );
					}
					catch( Exception e )
					{
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * Represents a web page or site.
	 */
	protected static class WebItem
	{
	// Constants
		
		/**
		 * Possible error.
		 */
		public static enum ErrorCode
		{
			MALFORMED_URL,
			NOT_FOUND,
			IO_ERROR,
			ILLEGAL_URL,
			IGNORED,
			UNKNOWN
		};
		
		/**
		 * Simple error container.
		 */
		public static class Error
		{
			public String msg;
			public ErrorCode code;
			public Error( String msg, ErrorCode code ) { this.msg = msg; this.code = code; };
			@Override
			public String toString() { return String.format("error: %s: %s", code, msg ); }
		}

		protected static Pattern STARTER     = Pattern.compile( "(<script\\s[^>]*>)|(<!--)|(href\\s*=\\s*)" );
		protected static Pattern END_COMMENT = Pattern.compile( "-->" );
		protected static Pattern END_SCRIPT  = Pattern.compile( "</script>" );
		protected static Pattern DQUOTED_REF = Pattern.compile( "\"[^\"]*\"" );
		protected static Pattern QUOTED_REF  = Pattern.compile( "'[^']*'" );
		protected static Pattern WORD        = Pattern.compile( "[^\\s>]+" );
		
	// Attributes
		
		/**
		 * The page or site name (text of the link).
		 */
		public String id;
		
		/**
		 * The original page name if we are looking at sites only.
		 */
		public String originalId;
		
		/**
		 * The page or site URL.
		 */
		public URL url;
		
		/**
		 * The depth where this item has been found in the current exploration.
		 */
		public int depth;
		
		/**
		 * Eventual error.
		 */
		public Error error;
		
		/**
		 * Set of already existing edges toward other sites.
		 */
		public HashMap<WebItem,WebLink> edges;
		
	// Constructors

		/**
		 * New web item.
		 * @param URL The item URL as a string.
		 * @param parent The parent URL (if any).
		 * @param depth The depth at which this item has been found.
		 * @param dict The dictionary. 
		 */
		public WebItem( String URL, URL parent, int depth, Dictionary dict )
		{
			try
			{
				this.id    = URL;
				this.depth = depth;
				this.edges = new HashMap<WebItem,WebLink>();
				
				if( parent != null )
				     this.url = new URL( parent, URL );
				else this.url = new URL( URL );
			
				if( dict.onlySites )
				{
					originalId = id;
					id  = url.getHost();
//					url = new URL( String.format( "%s://%s", url.getProtocol(), id ) );
				}
			}
			catch( MalformedURLException e )
			{
				error = new Error( e.getMessage(), ErrorCode.MALFORMED_URL );
				
				if( dict.showLog )
					System.err.printf( "** [31;1m%s[0m%n", error.toString() );
			}
		}
	
	// Access
		
		/**
		 * Does this item has errors?.
		 * @return True if errors where encountered.
		 */
		public boolean isErroneous()
		{
			return( error != null );
		}
		
		/**
		 * Does this item points toward (has a link toward) the given item?.
		 * @param item The item to check.
		 * @return True if this web item has a link toward the given item.
		 */
		public boolean hasEdgeToward( WebItem item )
		{
			return( edges.get( item ) != null );
		}
		
		/**
		 * Return the link between this item and another.
		 * @param item The other item. 
		 * @return The link or null if there are no link yet between the two items.
		 */
		public WebLink getEdgeToward( WebItem item )
		{
			return edges.get( item );
		}
		
	// Commands
		
		/**
		 * Add a link between this item and the given item.
		 * @param item The item to reference.
		 * @return true if the edge was added, false if the edge already exist.
		 */
		protected boolean addEdgeToward( WebItem item, Dictionary dict )
			throws GraphParseException
		{
			WebLink link = getEdgeToward( item );
			
			if( link == null )//&& ! item.hasEdgeToward( this ) )
			{
				link = new WebLink( dict.newEdgeId(), item );
				edges.put( item, link );
				dict.edgeEvent( this, link );

				return true;
			}
			else
			{
				link.increment();
				dict.edgeChangeEvent( this, link );
			}
			
			return false;
		}
		
		/**
		 * Try to parse the URL represented by this object. This method does not
		 * return any result. Errors are recorded in this element.
		 * @param dict The visited pages dictionary and pages remaining to
		 *        visit, this dictionary may be modified by this method.
		 */
		public void parse( Dictionary dict ) throws IOException
		{
			if( error != null )
				return;
			
			if( depth+1 >= dict.maxDepth )
				return;

			// This is not a fully featured HTML parser, we only search for
			// href. This method works as this :
			// - We scan for "href =", "<script ..>" and "<!--. The last two are
			//   to be avoided, but can contain "href" references therefore they
			//   have a specific treatment.
			//      - For <script we look if the tag looks like "<script ../>"
			//        if not we skip at the next "</script>" tag.
			//      - For <!-- we skip directly to the corresponding "-->" tag.
			//      - For href, we have to read the "href=" so we look for either:
			//          - "foo bar" (double quoted block containing spaces),
			//          - 'foo bar' (quoted block containing spaces),
			//          - foo, (an unquoted word without spaces (yes this still exists)).
			//			-> we report each one of these href things.

			try
			{
				if( dict.debug )
					System.out.printf( "[%d] parsing site '%s' %s ... ", depth, id,
							originalId != null ? String.format( "(%s)", originalId ) : "" );
				
				Scanner scanner = new Scanner( new BufferedReader( new InputStreamReader( url.openStream() ) ) );
				boolean loop    = true;

				if( dict.debug )
					System.out.printf( "open%n" );
				
				scanner.useDelimiter( "[\\s>]" );
			
				while( loop )
				{
					String next = scanner.findWithinHorizon( STARTER, 0 );

					if( next == null )
					{
						loop = false;
						
						if( dict.debug )
							System.out.printf( "parsing ok.%n", depth );
					}
					else if( next.startsWith( "<!--" ) )
					{
						next = scanner.findWithinHorizon( END_COMMENT, 0 );
						
						if( next == null )
							System.err.printf( "unterminated comment in %s%n", id );	// We are at the end of the stream.
					}
					else if( next.startsWith( "<script" ) )
					{
						if( ! next.endsWith( "/>" ) )
						{
							next = scanner.findWithinHorizon( END_SCRIPT, 0 );
							
							if( next == null )
								System.err.printf( "unterminated script in %s%n", id );	// We are at the end of the stream.
						}
					}
					else if( next.startsWith( "href" ) )
					{
						String href = null;
						
						if( scanner.hasNext( DQUOTED_REF ) )
						{
							href = scanner.next( DQUOTED_REF );
							href = href.substring( 1, href.length()-1 );
						}
						else if( scanner.hasNext( QUOTED_REF ) )
						{
							href = scanner.next( QUOTED_REF );
							href = href.substring( 1, href.length()-1 );
						}
						else if( scanner.hasNext( WORD ) )
						{
							href = scanner.next( WORD );
						}
						
						if( href == null )
						{
							System.err.printf( "** [31;1mBAD HREF...[0m%n" );
						}
						else
						{
							String old = new String( href );
							
							href = checkHref( href, dict );	// Check if we keep the HREF (heuristic).
							
							if( href != null )
							{
								// We got the HREF, now register it.
								
								WebItem item = new WebItem( href, url, depth+1, dict );
								
								if( ! item.isErroneous() )
									newWebItemFound( item, dict );
							}
							else
							{
							//	if( dict.debug )
							//		System.out.printf( "        X '%s'%n", old );
								
								if( dict.showLog )
									System.err.printf( "** [31;1mIGNORING %s...[0m%n", old );
							}
						}
					}
				}
			}
			catch( FileNotFoundException e )
			{
				error = new Error( e.getMessage(), ErrorCode.NOT_FOUND );
				
				if( dict.showLog )
					System.err.printf( "** [31;1m%s[0m%n", error.toString() );
			}
			catch( IllegalArgumentException e )
			{
				error = new Error( e.getMessage(), ErrorCode.ILLEGAL_URL );
				
				if( dict.showLog )
					System.err.printf( "** [31;1m%s[0m%n", error.toString() );
			}
			catch( IOException e )
			{
				error = new Error( e.getMessage(), ErrorCode.IO_ERROR );
				
				if( dict.showLog )
					System.err.printf( "** [31;1m%s[0m%n", error.toString() );
			}
			catch( GraphParseException e )
			{
				e.printStackTrace();
				throw new IOException( String.format( "Unexpected error (graph parse error?) : %s", e.getMessage() ) );
			}
			catch( Exception e )
			{
				e.printStackTrace();
				error = new Error( e.getMessage(), ErrorCode.UNKNOWN );

				if( dict.showLog )
				{
					System.err.printf( "** [31;1m%s: %s[0m%n", e.getClass().getName(), error.toString() );
					e.printStackTrace();
				}
				throw new IOException( String.format( "Unexpected error : %s", e.getMessage() ) );
			}
		}

		/**
		 * The parser found a web item, we have to register it in the dictionary, and update
		 * our links toward web items of the page we represent.
		 * @param item The item found in this page.
		 * @param dict The dictionary.
		 */
		protected void newWebItemFound( WebItem item, Dictionary dict )
			throws GraphParseException
		{
			if( ! dict.contains( item ) )
			{
				// The dictionary does not contain the item. Register it
				// and create a node for it as well as a link between the
				// page of this web item and this new web item.

				dict.nodeEvent( item );
				dict.register( item, this );
				dict.addToParse( item );
				
				WebItem me = dict.get( id ); 	// If in "onlySites" modeField, we get the "reference" site.
				assert me != null : "WTF ???";
				
				boolean ok = me.addEdgeToward( item, dict );
				
				if( dict.debug && ok )
					System.out.printf( "    -> '%s' (%s)%n", item.id, item.originalId != null ? item.originalId : "" );
				
				if( item.originalId != null && ! dict.contains( item.originalId ) )
					dict.registerOriginal( item.originalId );
			}
			else
			{
				if( item.originalId != null )
				{
					// Case where the item is a site only, we
					// still need to register all the pages,
					// however we do not store the WebItem
					// itself, only the fact the page exists to
					// avoid re-parsing it.
					
					if( ! dict.contains( item.originalId ) )
					{
						dict.registerOriginal( item.originalId );
						dict.addToParse( item );
					}
				}

				// The dictionary already contains the new web item,
				// however we have to check if a link between this
				// web item and the new web item.

				WebItem me = null;

				me   = dict.get( id );		// If in "sitesOnly" modeField, we get the "reference" site.
				item = dict.get( item.id );	// Also get the original item to which we are making a ref.
				
				assert me != null : "WTF ???";
				assert item != null : "WTF ???";
				boolean added = me.addEdgeToward( item, dict );
				
				if( dict.debug && added )
					System.out.printf( "    <- '%s' (%s)%n", item.id, item.originalId != null ? item.originalId : "" );
			}
		}
		
		/**
		 * Checks a HREF value is usable. This is only an heuristic, it may let pass
		 * HREFs that will not be usable. However, it should not block valid HREF.
		 * @param href The HREF to check.
		 * @return The HREF or null of it is blocked.
		 */
		protected String checkHref( String href, Dictionary dict )
		{
			if( href != null )
			{
				if( dict.isBlackListed( href ) )
					return null;
				
				if( href.startsWith( "javascript:" ) )
					return null;
				
				if( href.endsWith( ".css" ) )
					return null;
					
				int pos = href.indexOf( '#' );
				
				if( pos > 0 )
				{
					// Remove anchors...
					
					href = href.substring( 0, pos );
				}
				
				if( dict.getRemoveQueries() )
				{
					pos = href.indexOf( '?' );
					
					if( pos > 0 )
					{
						// Remove queries...
						
						href = href.substring( 0, pos );
					}
				}
				
				return dict.restrictHref( href );
			}
			
			return null;
		}
	}
	
	/**
	 * Representation of an edge.
	 */
	protected static class WebLink
	{
		/**
		 * Edge identifier.
		 */
		public String id;
		
		/**
		 * The edge weight.
		 */
		public float weight;
		
		/**
		 * Edge destination.
		 */
		public WebItem to;
		
		/**
		 * New web link toward a given item.
		 * @param to The item.
		 */
		public WebLink( String id, WebItem to )
		{
			this.id     = id;
			this.to     = to;
			this.weight = 1;
		}
		
		/**
		 * Increment the connection.
		 */
		public void increment()
		{
			weight += 1;
		}
	}
}