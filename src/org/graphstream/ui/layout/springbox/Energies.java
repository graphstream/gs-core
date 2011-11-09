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
package org.graphstream.ui.layout.springbox;

/**
 * Represent the history of energy values for a layout algorithm.
 */
public class Energies {
	// Attributes

	/**
	 * Global current energy (maybe actually updated).
	 */
	protected double energy;

	/**
	 * The last computed energy.
	 */
	protected double lastEnergy;

	/**
	 * The number of energy values remembered.
	 */
	protected int energiesBuffer = 256;

	/**
	 * A circular array of the last values of energy.
	 */
	protected double[] energies = new double[energiesBuffer];

	/**
	 * The current position in the energies array.
	 */
	protected int energiesPos = 0;
	
	protected double energySum = 0;

	// Constructor

	// Access

	/**
	 * The last computed energy value.
	 * 
	 * @return The actual level of energy.
	 */
	public double getEnergy() {
		return lastEnergy;
	}

	/**
	 * The number of energy values remembered.
	 */
	public int getBufferSize() {
		return energiesBuffer;
	}

	/**
	 * A number in [0..1] with 1 meaning fully stabilised.
	 * 
	 * @return A value that indicates the level of stabilisation in [0-1].
	 */
	public double getStabilization() {
		// The stability is attained when the global energy of the graph do not
		// vary anymore.

		int    range  = 200;
		double eprev1 = getPreviousEnergyValue(range);
		double eprev2 = getPreviousEnergyValue(range-10);
		double eprev3 = getPreviousEnergyValue(range-20);
		double eprev  = (eprev1+eprev2+eprev3)/3.0;
		double diff   = Math.abs(lastEnergy - eprev);

		diff = diff < 1 ? 1 : diff;
		
		return 1.0/diff;
	}
	
	/**
	 * The average energy in the whole buffer.
	 * @return The average energy.
	 */
	public double getAverageEnergy() {
		return energySum / energies.length;
	}

	/**
	 * A previous energy value.
	 * 
	 * @param stepsBack
	 *            The number of steps back in history.
	 * @return The energy value at stepsBack in time.
	 */
	public double getPreviousEnergyValue(int stepsBack) {
		if (stepsBack >= energies.length)
			stepsBack = energies.length - 1;

		int pos = (energies.length + (energiesPos - stepsBack))
				% energies.length;

		return energies[pos];
	}

	// Command

	/**
	 * Accumulate some energy in the current energy.
	 * 
	 * @param value
	 *            The value to accumulate.
	 */
	public void accumulateEnergy(double value) {
		energy += value;
	}

	/**
	 * Add a the current accumulated energy value in the set.
	 */
	public void storeEnergy() {
		energiesPos = (energiesPos + 1) % energies.length;

		energySum -= energies[energiesPos];
		energies[energiesPos] = energy;
		energySum += energy;
		lastEnergy = energy;
		energy = 0;
	}

	/**
	 * Randomise the energies array.
	 */
	protected void clearEnergies() {
		for (int i = 0; i < energies.length; ++i)
			energies[i] = ((Math.random() * 2000) - 1000);
	}
}