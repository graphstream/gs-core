/*
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

/**
 * @since 2009-04-17
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.util;

import java.io.IOException;

import org.graphstream.stream.SinkAdapter;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;

/**
 * Count the step of a stream.
 */
public class StepCounter extends SinkAdapter {
	/**
	 * Count step contains in a file.
	 * 
	 * @param path
	 *            path to the file
	 * @return count of step event in the file
	 * @throws IOException
	 * @see org.graphstream.stream.file.FileSourceFactory
	 */
	public static int countStepInFile(String path) throws IOException {
		StepCounter counter = new StepCounter();
		FileSource source = FileSourceFactory.sourceFor(path);

		source.addElementSink(counter);
		source.readAll(path);

		return counter.getStepCount();
	}

	protected int step;

	/**
	 * Default constructor. Count is set to zero.
	 */
	public StepCounter() {
		reset();
	}

	/**
	 * Reset the step count to zero.
	 */
	public void reset() {
		step = 0;
	}

	/**
	 * Get the step count.
	 * 
	 * @return the count of step
	 */
	public int getStepCount() {
		return step;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.SinkAdapter#stepBegins(java.lang.String, long,
	 * double)
	 */
	public void stepBegins(String sourceId, long timeId, double time) {
		step++;
	}
}
