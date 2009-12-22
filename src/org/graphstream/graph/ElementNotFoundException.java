package org.graphstream.graph;

/**
 * Thrown when a shearched object is not found.
 *
 * @author Antoine Dutot
 * @author Yoann Pigné
 * @since 20020615
 */
public class ElementNotFoundException
	extends RuntimeException
{
	private static final long serialVersionUID = 5089958436773409615L;

	/**
	 * Throws the message "not found".
	 */
	public
	ElementNotFoundException()
	{
		super( "not found" );
	}

	/**
	 * Throws <code>message</code>.
	 * @param message The message to throw.
	 */
	public
	ElementNotFoundException( String message )
	{
		super( "not found: " + message );
	}
}