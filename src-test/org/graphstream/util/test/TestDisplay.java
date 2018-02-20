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
package org.graphstream.util.test;

import org.graphstream.ui.test.util.Display;
import org.graphstream.util.MissingDisplayException;
import org.junit.Assert;
import org.junit.Test;

import static org.graphstream.util.Display.getDefault;

public class TestDisplay {
	public void checkDisplay(String uiModule) {
		System.setProperty("org.graphstream.ui", uiModule);

		try {
			org.graphstream.util.Display d = getDefault();

			Assert.assertNotNull(d);
			Assert.assertTrue(d instanceof Display);
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void testDisplayLoadingByClassName() {
		checkDisplay(Display.class.getName());
	}

	@Test
	public void testDisplayLoadingByPackageName() {
		checkDisplay("org.graphstream.ui.test");
	}

	@Test
	public void testDisplayLoadingByModuleName() {
		checkDisplay("test");
	}

	@Test
	public void testMissingDisplayExceptionIfPropertyNotSet() {
		System.clearProperty("org.graphstream.ui");

		try {
			getDefault();
		} catch (MissingDisplayException e) {
			Assert.assertTrue(e.getMessage().startsWith("No UI package detected!"));
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void testMissingDisplayExceptionIfNoValidCandidate() {
		System.setProperty("org.graphstream.ui", TestDisplay.class.getName());

		try {
			getDefault();
		} catch (MissingDisplayException e) {
			Assert.assertTrue(e.getMessage().startsWith("No valid display found."));
		} catch (Exception e) {
			Assert.fail();
		}
	}
}
