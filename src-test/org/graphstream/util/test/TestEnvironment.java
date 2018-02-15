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
 * @since 2011-07-22
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Stefan Balev <stefan.balev@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.util.test;

import java.io.File;
import java.util.ArrayList;

import org.graphstream.util.Environment;
import org.junit.Ignore;

/**
 * Tests the {@link org.util.Environment} class and shows an example of use.
 *
 * @author Yoann Pigné
 * @author Antoine Dutot
 * @since 20061108
 */

@Ignore
public class TestEnvironment {
	// Attributes

	protected Environment env;

	// Constructors

	public static void main(String args[]) {
		new TestEnvironment(args);
	}

	public TestEnvironment(String args[]) {
		firstTest(args);
		secondTest();
		thirdTest();
		fourthTest();
	}

	protected void firstTest(String args[]) {
		env = Environment.getGlobalEnvironment();

		ArrayList<String> trashcan = new ArrayList<String>();

		env.readCommandLine(args, trashcan);

		System.out.printf("Test1: I read the command line and here is my state :%n\t");
		env.printParameters();
		System.out.printf("Test1: Here are the unparsed parameters in the command line:%n");
		System.out.printf("\t%s%n", trashcan.toString());
	}

	protected void secondTest() {
		Environment env2 = new Environment();

		env2.setParameter("param1", "val1");
		env2.setParameter("param2", "value2");
		env2.setParameter("param3", "value3");
		env2.lockEnvironment(true);
		env2.setParameter("param1", "value1");
		env2.setParameter("param4", "value4");

		if (!env2.getParameter("param1").equals("value1"))
			System.err.printf("test2: error 1%n");
		if (!env2.getParameter("param2").equals("value2"))
			System.err.printf("test2: error 2%n");
		if (!env2.getParameter("param3").equals("value3"))
			System.err.printf("test2: error 3%n");
		if (!env2.getParameter("param4").equals(""))
			System.err.printf("test2: error 4%n");

		System.out.printf("Test2: env = %s%n", env2.toString());

		try {
			env2.writeParameterFile("TOTO");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void thirdTest() {
		Environment env3 = new Environment();

		try {
			env3.readParameterFile("TOTO");

			System.out.printf("Test3: env = %s%n", env3.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		File file = new File("TOTO");
		file.delete();
	}

	protected void fourthTest() {
		Environment env4 = new Environment();
		TestContainer tc = new TestContainer();

		env4.setParameter("param1", "value1");
		env4.setParameter("param2", "12345678");
		env4.setParameter("param3", "12345678");
		env4.setParameter("param4", "1234.5678");
		env4.setParameter("param5", "1234.5678");
		env4.setParameter("param6", "true");
		env4.setParameter("param7", "invalid!!");

		env4.initializeFieldsOf(tc);

		System.out.printf("Test4: env = %s%n", env4.toString());
		System.out.printf("Test4: tc  = %s%n", tc.toString());
	}

	// Nested classes

	protected static class TestContainer {
		protected String param1;
		protected int param2;
		protected long param3;
		protected float param4;
		protected double param5;
		protected boolean param6;
		protected Object param7nonFunctional;

		public void setParam1(String value) {
			param1 = value;
		}

		public void setParam2(int value) {
			param2 = value;
		}

		public void setParam3(long value) {
			param3 = value;
		}

		public void setParam4(float value) {
			param4 = value;
		}

		public void setParam5(double value) {
			param5 = value;
		}

		public void setParam6(boolean value) {
			param6 = value;
		}

		public void setParam7(Object value) {
			param7nonFunctional = value;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();

			sb.append("param1=" + param1);
			sb.append(", param2=" + param2);
			sb.append(", param3=" + param3);
			sb.append(", param4=" + param4);
			sb.append(", param5=" + param5);
			sb.append(", param6=" + param6);

			return sb.toString();
		}
	}

}