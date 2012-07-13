package org.graphstream.ui.spriteManager;

/**
 * Launched when a sprite is created with a dot in its identifier, which is
 * impossible in the current implementation.
 */
public class InvalidSpriteIDException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidSpriteIDException(String message) {
		super(message);
	}
	
	public InvalidSpriteIDException() {
		super();
	}
}
