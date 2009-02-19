package org.miv.graphstream.graph.test;

import java.io.PrintStream;

/**
 * Base for test classes.
 * 
 * @author Antoine Dutot
 */
public class TestBase
{
	/**
	 * Number of error encountered.
	 */
	protected int errorCount = 0;

	/**
	 * Simply check that the "test" argument is equal to "true". It this is not the
	 * case a message is printed on the standard error output and one error is counted.
	 * @param test The test result.
	 * @param message The message to print if test is false, this is like a printf() argument.
	 * @param args The arguments of the message.
	 */
	protected void check( boolean test, String message, Object...args )
	{
		if( ! test )
		{
			System.err.printf( message, args );
			
			try
			{
				throw new RuntimeException();
			}
			catch( RuntimeException e )
			{
				StackTraceElement[] stackTrace = e.getStackTrace();

				System.err.printf( "    %s.%s():%d (%s).%n",
						stackTrace[1].getClassName(),
						stackTrace[1].getMethodName(),
						stackTrace[1].getLineNumber(),
						stackTrace[1].getFileName() );
			}

			errorCount++;
		}
	}
	
	/**
	 * Display test results so far.
	 */
	protected void printTestResults()
	{
		printTestResults( null );
	}
	
	/**
	 * Display test results so far.
	 * @param message An additional message to print with the results.
	 */
	protected void printTestResults( String message )
	{
		PrintStream out = errorCount > 0 ? System.err : System.out;
		
		if( message != null )
			out.printf( "%s%n", message );
		
		if( errorCount > 0 )
		{
			out.printf( "%d errors!%n", errorCount );
			System.exit( 1 );
		}
		else
		{
			out.printf( "no errors%n" );
		}
	}
}
