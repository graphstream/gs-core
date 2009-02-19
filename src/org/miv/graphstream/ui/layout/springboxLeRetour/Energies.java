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

package org.miv.graphstream.algorithm.layout2.springboxLeRetour;

/**
 * Represent the history of energy values for a layout algorithm. 
 * 
 * @author Antoine Dutot
 */
public class Energies
{
// Attributes
	
	/**
	 * Global current energy (maybe actually updated).
	 */
	protected float energy;
	
	/**
	 * The last computed energy.
	 */
	protected float lastEnergy;
	
	/**
	 * The number of energy values remembered.
	 */
	protected int energiesBuffer = 256;
	
	/**
	 * A circular array of the last values of energy.
	 */
	protected float[] energies = new float[energiesBuffer];
	
	/**
	 * The current position in the energies array.
	 */
	protected int energiesPos = 0;

// Constructor
	
	
// Access

	/**
	 * The last computed energy value.
	 * @return The actual level of energy.
	 */
	public float getEnergy()
	{
		return lastEnergy;
	}
	
	/**
	 * The number of energy values remembered.
	 */
	public int getBufferSize()
	{
		return energiesBuffer;
	}
	
	/**
	 * A number in [0..1] with 1 meaning fully stabilised.
	 * @return A value that indicates the level of stabilisation in [0-1].
	 */
	public float getStabilization()
	{
		// The stability is attained when the global energy of the graph do not vary anymore.
		
		int   range = 200;
		float max   = 1;
		float eprev = getPreviousEnergyValue( range );
		float diff  = (float) Math.abs( lastEnergy - eprev );

		diff = diff > max ? max : diff;
			
		if( diff < 0 )
			diff = 0;
			
		return 1 - ( diff / max );
	}
	
	/**
	 * A previous energy value.
	 * @param stepsBack The number of steps back in history.
	 * @return The energy value at stepsBack in time.
	 */
	public float getPreviousEnergyValue( int stepsBack )
	{
		if( stepsBack >= energies.length )
			stepsBack = energies.length - 1;
		
		int pos = ( energies.length + ( energiesPos - stepsBack ) ) % energies.length;
		
		return energies[pos];
	}	

// Command

	/**
	 * Accumulate some energy in the current energy.
	 * @param value The value to accumulate.
	 */
	public void accumulateEnergy( float value )
	{
		energy += value;
	}
	
	/**
	 * Add a the current accumulated energy value in the set.
	 */
	public void storeEnergy()
	{
		energiesPos = ( energiesPos + 1 ) % energies.length;

		energies[energiesPos] = energy;
		lastEnergy = energy;
		energy = 0;
	}
	
	/**
	 * Randomise the energies array.
	 */
	protected void clearEnergies()
	{
		for( int i=0; i<energies.length; ++i )
			energies[i] = (float) ( ( Math.random() * 2000 ) - 1000 );
	}
}