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
 * @author Richard O. Legendi <richard.legendi@gmail.com>
 * @author Alex Bowen <bowen.a@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Representation of a set of parameters.
 * 
 * <p>
 * The environment class mimics the environment variables available in any shell
 * using a hash map of keys/values, the key being the variables names, excepted
 * here they are called parameters.
 * </p>
 * 
 * <p>
 * In addition, this class provides facilities to:
 * <ul>
 * <li>Read a parameter file and set the parameters from this file;</li>
 * <li>Write a parameter file from the parameter of this environment;</li>
 * <li>Parse the command line and get parameters from it;</li>
 * <li>Take a class as argument and set all its fields having the same name as
 * parameters in this class;</li>
 * </ul>
 * </p>
 * 
 * <p>
 * As in any shell, most of the time, the environment is global and accessible
 * from any part of the system. Here a singleton instance of this class is
 * created and accessible from anywhere in the JVM using the
 * {@link #getGlobalEnvironment()} method (indeed the singleton instance is
 * created at its first access). However, it is still possible to create a
 * private instance of this class for use in a specific part of a program.
 * </p>
 * 
 * <p>
 * To read a file of parameters, simply call the
 * {@link #readParameterFile(String)} method. In the same way, to write a set of
 * parameters to a file, call the {@link #writeParameterFile(String)} method.
 * The format of the parameter file is given in the description of these
 * methods.
 * </p>
 * 
 * <p>
 * To read parameters from he command line, call the
 * {@link #readCommandLine(String[])} or
 * {@link #readCommandLine(String[], Collection)} methods. These methods expect
 * a format for the command line that is described in there respective
 * documentations.
 * </p>
 * 
 * <p>
 * It is also possible to setup automatically the fields of an arbitrary object,
 * provided these fields have name that match parameters in this environment. To
 * do this call the {@link #initializeFieldsOf(Object)} method passing the
 * object to initialise as argument. The object to setup must provide methods of
 * the form "setThing(Type)" where "Thing" or "thing" is the name of the field
 * to set and "Type" is one of "int", "long", "float", "double", "String" and
 * "boolean". For the boolean type, the accepted values meaning true are "true",
 * "on", "1", and "yes", all other value are considered as false.
 * </p>
 * 
 * TODO: how (or when) does the default configuration file is read? TODO: how to
 * handle parameters that cannot be setup in the
 * {@link #initializeFieldsOf(Object)}?
 * 
 * @author Frédéric Guinand
 * @author Yoann Pigné
 * @author Antoine Dutot
 * @version 1.0 (jdk 1.5)
 */
public class Environment implements Cloneable {
	private static final Logger logger = Logger.getLogger(Environment.class.getSimpleName());

	// ---------- Attributes -----------

	/**
	 * Name of the configuration file. Default is "config"
	 */
	protected String configFileName = "config";

	/**
	 * Has the configuration file been read yet?.
	 */
	protected boolean configFileRead = false;

	/**
	 * Set of parameters. This is a hash table and not a hashmap since several
	 * thread may access this class at once.
	 */
	protected Hashtable<String, String> parameters = new Hashtable<String, String>();

	/**
	 * When locked the environment parameters value still can be changed but it is
	 * no more possible to add new parameters.
	 */
	protected boolean locked;

	// --------- Static attributes ---------

	/**
	 * Global environment for the whole JVM. This global environment is available
	 * <b>and editable</b> from everywhere. It is create as soon as the
	 * {@link #getGlobalEnvironment()} static method is called if this field was not
	 * yet initialized by any other mean.
	 * 
	 * @see #getGlobalEnvironment()
	 */
	public static Environment GLOBAL_ENV;

	// --------- Static methods -----------

	/**
	 * Access to the global shared environment for the whole JVM. This method allows
	 * to access a shared environment, that can be read and written from anywhere.
	 * 
	 * @return A singleton instance of the global environment.
	 */
	public static Environment getGlobalEnvironment() {
		if (GLOBAL_ENV == null)
			GLOBAL_ENV = new Environment();

		return GLOBAL_ENV;
	}

	// --------- Methods -------------

	/**
	 * Is the environment locked?.
	 * 
	 * @return True if the environment is locked.
	 * @see #lockEnvironment(boolean)
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * Access to a parameter in the environment.
	 * 
	 * @param parameter
	 *            The parameter name.
	 * @return The parameter value (empty string if not set).
	 */
	public String getParameter(String parameter) {
		String p = parameters.get(parameter);

		return (p == null) ? "" : p;
	}

	/**
	 * True if the given paramter exist.
	 * 
	 * @param parameter
	 *            The parameter name.
	 * @return True if the given paramter name points to a value.
	 */
	public boolean hasParameter(String parameter) {
		return (parameters.get(parameter) != null);
	}

	/**
	 * Check a parameter expected to be of boolean type. This method returns "true"
	 * if the parameter exists and has a value that is "1", "true", "on" or "yes"
	 * (with any possible combination of upper or lower-case letters). For any other
	 * values of the parameter or if the parameter does not exist in the
	 * environment, "false" is returned.
	 * 
	 * @param parameter
	 *            The parameter name.
	 * @return True if the parameter value means "true", false for any other value
	 *         or if the parameter does not exist.
	 * @see #getBooleanParameteri(String)
	 */
	public boolean getBooleanParameter(String parameter) {
		int val = getBooleanParameteri(parameter);

		return (val == 1);
	}

	/**
	 * Check a parameter expected to be of boolean type. This method returns the
	 * value 1 if the parameter has value "1", "true", "on", "yes" (the case does
	 * not matter). Else it returns 0. To account the case of non-existing
	 * parameters, this method returns -1 if the given parameter does not exist.
	 * 
	 * @param parameter
	 *            The parameter name.
	 * @return 1 if the parameter value means "true", 0 if it has any other value,
	 *         or -1 if it does not exist.
	 * @see #getBooleanParameter(String)
	 */
	public int getBooleanParameteri(String parameter) {
		String p = parameters.get(parameter);

		if (p != null) {
			p = p.toLowerCase();

			if (p.equals("1"))
				return 1;
			if (p.equals("true"))
				return 1;
			if (p.equals("on"))
				return 1;
			if (p.equals("yes"))
				return 1;

			return 0;
		}

		return -1;
	}

	/**
	 * Get the value of a parameter that is expected to be a number. If the
	 * parameter does not exist or is not a number, 0 is returned.
	 * 
	 * @param parameter
	 *            The parameter name.
	 * @return The numeric value of the parameter. 0 if the parameter does not exist
	 *         or is not a number.
	 */
	public double getNumberParameter(String parameter) {
		String p = parameters.get(parameter);

		if (p != null) {
			try {
				return Double.parseDouble(p);
			} catch (NumberFormatException e) {
				return 0;
			}
		}

		return 0;
	}

	/**
	 * Returns the number of parameters found in the configuration file.
	 * 
	 * @return The number of parameters found in the configuration file.
	 */
	public int getParameterCount() {
		return parameters.size();
	}

	/**
	 * Set of all parameter names.
	 * 
	 * @return A set of all the names identifying parameters in this environment.
	 */
	public Set<String> getParametersKeySet() {
		return parameters.keySet();
	}

	/**
	 * Generate a new Environment object with a deep copy of the elements this
	 * object.
	 * 
	 * @return An Environment object identical to this one
	 */
	@Override
	public Environment clone() {
		Environment e = new Environment();
		e.configFileName = configFileName;
		e.configFileRead = configFileRead;
		e.locked = locked;
		for (String key : parameters.keySet()) {
			e.parameters.put(key, parameters.get(key));
		}
		return e;
	}

	/**
	 * Set the value of a parameter. If the parameter already exists its old value
	 * is overwritten. This works only if the environment is not locked.
	 * 
	 * @param parameter
	 *            The parameter name.
	 * @param value
	 *            The new parameter value.
	 * @see #isLocked()
	 * @see #lockEnvironment(boolean)
	 */
	public void setParameter(String parameter, String value) {
		if (!locked) {
			parameters.put(parameter, value);
		} else {
			if (parameters.get(parameter) != null)
				parameters.put(parameter, value);
		}
	}

	/**
	 * Disallow the addition of new parameters. The already declared parameters are
	 * still modifiable, but no new parameter can be added.
	 * 
	 * @param on
	 *            If true the environment is locked.
	 */
	public void lockEnvironment(boolean on) {
		locked = on;
	}

	/**
	 * Initialize all the fields of the given object whose name correspond to
	 * parameters of this environment. This works only if the object to initialize
	 * provides methods that begins by "set". For example if the object provides a
	 * method named "setThing(int value)", and if there is a parameter named "thing"
	 * in this environment and its value is convertible to an integer, then the
	 * method "setThing()" will be invoked on the object with the correct value.
	 * 
	 * @see #initializeFieldsOf(Object, String[])
	 * @see #initializeFieldsOf(Object, Collection)
	 * @param object
	 *            The object to initialize.
	 */
	public void initializeFieldsOf(Object object) {
		Method[] methods = object.getClass().getMethods();

		for (Method method : methods) {
			if (method.getName().startsWith("set")) {
				Class<?> types[] = method.getParameterTypes();

				if (types.length == 1) {
					String name = method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4);
					String value = parameters.get(name);

					if (value != null) {
						invokeSetMethod(object, method, types, name, value);
					}
				}
			}
		}
	}

	/**
	 * Initialize all the fields of the given object that both appear in the given
	 * field list and whose name correspond to parameters of this environment. See
	 * the {@link #initializeFieldsOf(Object)} method description.
	 * 
	 * @see #initializeFieldsOf(Object)
	 * @see #initializeFieldsOf(Object, Collection)
	 * @param object
	 *            The object to initialize.
	 * @param fieldList
	 *            The name of the fields to initialize in the object.
	 */
	public void initializeFieldsOf(Object object, String... fieldList) {
		Method[] methods = object.getClass().getMethods();
		HashSet<String> names = new HashSet<String>();

		for (String s : fieldList)
			names.add(s);

		for (Method method : methods) {
			if (method.getName().startsWith("set")) {
				Class<?> types[] = method.getParameterTypes();

				if (types.length == 1) {
					String name = method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4);

					if (names.contains(name)) {
						String value = parameters.get(name);

						if (value != null) {
							invokeSetMethod(object, method, types, name, value);
						}
					}
				}
			}
		}
	}

	/**
	 * Initialize all the fields of the given object that both appear in the given
	 * field list and whose name correspond to parameters of this environment. See
	 * the {@link #initializeFieldsOf(Object)} method description.
	 * 
	 * @see #initializeFieldsOf(Object)
	 * @see #initializeFieldsOf(Object, String[])
	 * @param object
	 *            The object to initialize.
	 * @param fieldList
	 *            The name of the fields to initialize in the object.
	 */
	protected void initializeFieldsOf(Object object, Collection<String> fieldList) {
		Method[] methods = object.getClass().getMethods();

		for (Method method : methods) {
			if (method.getName().startsWith("set")) {
				Class<?> types[] = method.getParameterTypes();

				if (types.length == 1) {
					String name = method.getName().substring(3).toLowerCase();

					if (fieldList.contains(name)) {
						String value = parameters.get(name);

						if (value != null) {
							invokeSetMethod(object, method, types, name, value);
						}
					}
				}
			}
		}
	}

	protected void invokeSetMethod(Object object, Method method, Class<?> types[], String name, String value) {
		try {
			// XXX a way to avoid this overlong and repetitive
			// list of setters ?

			if (types[0] == Long.TYPE) {
				try {
					long val = Long.parseLong(value);
					method.invoke(object, new Long(val));
				} catch (NumberFormatException e) {
					logger.warning(String.format("cannot set '%s' to the value '%s', values is not a long%n",
							method.toString(), value));
				}
			} else if (types[0] == Integer.TYPE) {
				try {
					int val = (int) Double.parseDouble(value);
					method.invoke(object, new Integer(val));
				} catch (NumberFormatException e) {
					logger.warning(String.format("cannot set '%s' to the value '%s', values is not a int%n",
							method.toString(), value));
				}
			} else if (types[0] == Double.TYPE) {
				try {
					double val = Double.parseDouble(value);
					method.invoke(object, new Double(val));
				} catch (NumberFormatException e) {
					logger.warning(String.format("cannot set '%s' to the value '%s', values is not a double%n",
							method.toString(), value));
				}
			} else if (types[0] == Float.TYPE) {
				try {
					float val = Float.parseFloat(value);
					method.invoke(object, new Float(val));
				} catch (NumberFormatException e) {
					logger.warning(String.format("cannot set '%s' to the value '%s', values is not a float%n",
							method.toString(), value));
				}
			} else if (types[0] == Boolean.TYPE) {
				try {
					boolean val = false;
					value = value.toLowerCase();

					if (value.equals("1") || value.equals("true") || value.equals("yes") || value.equals("on"))
						val = true;

					method.invoke(object, new Boolean(val));
				} catch (NumberFormatException e) {
					logger.warning(String.format("cannot set '%s' to the value '%s', values is not a boolean%n",
							method.toString(), value));
				}
			} else if (types[0] == String.class) {
				method.invoke(object, value);
			} else {
				logger.warning(
						String.format("cannot match parameter '%s' and the method '%s'%n", value, method.toString()));
			}
		} catch (InvocationTargetException ite) {
			logger.warning(String.format("cannot invoke method '%s' : invocation targer error : %s%n",
					method.toString(), ite.getMessage()));
		} catch (IllegalAccessException iae) {
			logger.warning(String.format("cannot invoke method '%s' : illegal access error : %s%n", method.toString(),
					iae.getMessage()));
		}
	}

	/**
	 * Print all parameters to the given stream.
	 * 
	 * @param out
	 *            The output stream to use.
	 */
	public void printParameters(PrintStream out) {
		out.println(toString());
	}

	/**
	 * Print all parameters the stdout.
	 */
	public void printParameters() {
		printParameters(System.out);
	}

	@Override
	public String toString() {
		return parameters.toString();
	}

	/**
	 * Read the parameters from the given command line array. See the more complete
	 * {@link #readCommandLine(String[], Collection)} method.
	 * 
	 * @param args
	 *            The command line.
	 */
	public void readCommandLine(String[] args) {
		readCommandLine(args, null);
	}

	/**
	 * Read the parameters from the given command line array. The expected format of
	 * this array is the following:
	 * <ul>
	 * <li>a word beginning by a "-" is the parameter name (for example
	 * "-param");</li>
	 * <li>if this word is immediately followed by a "=" and another word, this word
	 * is considered as its string value (for example "-param=aValue");</li>
	 * <li>If the parameter name is not followed by "=", it is considered a boolean
	 * option and its value is set to the string "true" (to set this to false simply
	 * give the string "-param=false");</li>
	 * <li>If a word is found on the command line without any preceding "-" but is
	 * followed by a "=" and by another word, then it is considered as a key,value
	 * brace</li>
	 * <li>If a word is found on the command line without any preceding "-" and is
	 * not followed by any "=", the it is considered to be a filename for a
	 * configuration file. The method will try to open this file for reading. A
	 * configuration file is composed of lines. Each line is composed of a brace
	 * key/value separated by a "=". If a line starts with a "#", then it is
	 * considered as a comment. Finally if no format is recognized the line is
	 * inserted to the <code>trashcan</code>.</li>
	 * </ul>
	 * 
	 * @param args
	 *            The command line.
	 * @param trashcan
	 *            Will be filled by the set of unparsed strings (can be null if
	 *            these strings can be ignored).
	 */
	public void readCommandLine(String[] args, Collection<String> trashcan) {
		for (String arg : args) {
			boolean startsWithMinus = arg.startsWith("-");
			int equalPos = arg.indexOf('=');
			String value = "true";
			if (equalPos >= 0) {
				value = arg.substring(equalPos + 1);
				if (startsWithMinus) {
					arg = arg.substring(1, equalPos);
				} else {
					arg = arg.substring(0, equalPos);
				}
				parameters.put(arg, value);
			} else {
				if (startsWithMinus) {
					arg = arg.substring(1);
					parameters.put(arg, value);
				} else {
					readConfigFile(arg, trashcan);
				}
			}
		}
	}

	/**
	 * Internal method that reads a configuration file.
	 */
	protected void readConfigFile(String filename, Collection<String> trashcan) {
		BufferedReader br;
		int count = 0;
		try {
			br = new BufferedReader(new FileReader(filename));
			String str;
			while ((str = br.readLine()) != null) {
				count++;
				if (str.length() > 0 && !str.substring(0, 1).equals("#")) {
					String[] val = str.split("=");
					if (val.length != 2) {
						if (val.length == 1) {
							parameters.put(val[0].trim(), "true");
						} else {
							logger.warning(String.format(
									"Something is wrong with the configuration file \"%s\"near line %d :\n %s",
									filename, count, str));
							if (trashcan != null) {
								trashcan.add(str);
							}
						}
					} else {
						String s0 = val[0].trim();
						String s1 = val[1].trim();
						parameters.put(s0, s1);
					}
				}
			}

		} catch (FileNotFoundException fnfe) {
			System.err.printf("Tried to open \"%s\" as a config file: file not found.%n", filename);
			if (trashcan != null) {
				trashcan.add(filename);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * Save the curent parameters to a file.
	 * 
	 * @param fileName
	 *            Name of the file to save the config in.
	 * @throws IOException
	 *             For any output error on the given file name.
	 */
	public void writeParameterFile(String fileName) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
		Set<String> ks = parameters.keySet();

		for (String key : ks) {
			bw.write(key + " = " + parameters.get(key));
			bw.newLine();
		}

		bw.close();
	}

	/**
	 * Read the default configuration file. Once this file has been correctly
	 * parsed, the {@link #configFileRead} boolean is set to true.
	 * 
	 * @see #configFileName
	 */
	protected void readConfigurationFile() {
		try {
			readParameterFile(configFileName);
			configFileRead = true;
		} catch (IOException ioe) {
			logger.log(Level.WARNING, String.format("%-5s : %s : %s\n", "Warning", "Environment",
					"Something wrong while reading the configuration file."), ioe);
		}
	}

	/**
	 * Read a parameter file. The format of this file is as follows:
	 * <ul>
	 * <li>Each line contains a parameter setting or a comment;</li>
	 * <li>Lines beginning by a "#" are considered comments (be careful, a "#" in
	 * the middle of a line <b>is not</b> a comment);</li>
	 * <li>parameters settings are of the form "name=value", spaces are allowed, but
	 * space before and after the parameter name of value will be stripped.</li>
	 * </ul>
	 * 
	 * @param fileName
	 *            Name of the parameter file to read.
	 * @throws IOException
	 *             For any error with the given parameter file name.
	 */
	public void readParameterFile(String fileName) throws IOException {
		BufferedReader br;
		int count = 0;

		br = new BufferedReader(new FileReader(fileName));

		String str;

		while ((str = br.readLine()) != null) {
			count++;

			if (str.length() > 0 && !str.startsWith("#")) {
				String[] val = str.split("=");

				if (val.length != 2) {
					logger.warning(String.format("%-5s : %s : %s\n", "Warn", "Environment",
							"Something is wrong in your configuration file near line " + count + " : \n"
									+ Arrays.toString(val)));
				} else {
					String s0 = val[0].trim();
					String s1 = val[1].trim();

					setParameter(s0, s1);
				}
			}
		}

		br.close();
	}
}