package org.graphstream.graph;

/**
 * Singleton exception.
 *
 * <p>This exception can be raised when a class instance should be unique in one
 * context, but was instanciated several times. This can be used in a
 * "singleton" pattern, but also in set classes (containers) when the same
 * element has been inserted multiple times but should not.</p>
 *
 * @author Antoine Dutot
 * @author Yoann Pigné
 * @since 19990811
 * @version 0.1
 */
public class IdAlreadyInUseException
	extends RuntimeException
{
	private static final long serialVersionUID = -3000770118436738366L;

	/**
	 * Throws the message "singleton exception".
	 */
	public
	IdAlreadyInUseException()
	{
		super( "singleton exception" );
	}

	/**
	 * Throws a given message.
	 * @param message The message to throw.
	 */
	public
	IdAlreadyInUseException( String message )
	{
		super( "singleton exception: " + message );
	}
}