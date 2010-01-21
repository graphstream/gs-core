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
 */

package org.graphstream.ui.swingViewer.util;

/**
 * A simple counter that allows to count the number of frames per second.
 * 
 * @author Antoine Dutot
 */
public class FpsCounter
{
// Attribute

	/**
	 * Time measure.
	 */
	protected double t1, t2;
	
	/**
	 * The last frame time.
	 */
	protected double time;
	
	/**
	 * Counter for the average.
	 */
	protected int count = 0;
	
	/**
	 * The average time.
	 */
	protected double avgTime;
	
// Construction

	public FpsCounter()
	{
	}
	
// Access
	
	/**
	 * The number of frames per second according to the last measured frame (instantaneous measure).
	 * @return The estimated frame-per-second measure of the last frame.
	 */
	public double getFramesPerSecond()
	{
		return( 1000000000.0 / time );
	}
	
	/**
	 * The duration in seconds of the last measured frame.
	 * @return The last frame time in seconds.
	 */
	public double getLastFrameTimeInSeconds()
	{
		return( time / 1000000000.0 );
	}
	
	/**
	 * The number of frames times used to compute the average frame-per-second and frame time. This
	 * number augments with the measures until a maximum, where it is reset to 0.
	 * @return The number of frames measure.
	 */
	public int getAverageMeasureCount()
	{
		return count;
	}
	
	/**
	 * The average frame-per-second measure. 
	 * @return The average number of frames per second.
	 * @see #getAverageMeasureCount()
	 */
	public double getAverageFramesPerSecond()
	{
		return( 1000000000.0 / ( avgTime / count ) );
	}
	
	/**
	 * The average frame time.
	 * @return The time used by a frame in average.
	 */
	public double getAverageFrameTimeInSeconds()
	{
		return( ( avgTime / count ) * 1000000000.0 );
	}
	
// Command

	public void resetAverages()
	{
		count   = 0;
		avgTime = 0;
	}
	
	/**
	 * Start a frame measure.
	 */
	public void beginFrame()
	{
		t1 = System.nanoTime();
	}
	
	/**
	 * End a frame measure.
	 */
	public void endFrame()
	{
		if( count > 1000000 )
		{
			count   = 0;
			avgTime = 0;
		}
		
		t2       = System.nanoTime();
		time     = ( t2 - t1 );
		avgTime += time;
		count   += 1;
	}
}