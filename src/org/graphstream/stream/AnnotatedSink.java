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
 * @since 2011-12-15
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream;

import org.graphstream.stream.SourceBase.ElementType;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.HashMap;

/**
 * A sink easily allowing a bind between attribute modifications and method
 * calls.
 * 
 * <pre>
 * public class MyObject extends AnnotatedSink {
 * 	String a1;
 * 	double a2;
 * 
 * 	&#064;Bind(&quot;myobject.set.a1&quot;)
 * 	public void setA1(String eventId, Object value) {
 * 		a1 = (String) value;
 * 	}
 * 
 * 	&#064;Bind(&quot;myobject.set.a2&quot;)
 * 	public void setA2(String eventId, Object value) {
 * 		a2 = (Double) value;
 * 	}
 * 
 * 	public static void main(String ... args) {
 * 			Graph g = ...;
 * 			MyObject obj = new MyObject();
 * 
 * 			g.addSink(obj);
 * 			
 * 			g.setAttribute("myobject.set.a1", "MyObject A1");
 * 			g.setAttribute("myobject.set.a2", 100.0);
 * 		}
 * }
 * </pre>
 */
public abstract class AnnotatedSink implements Sink {
	/**
	 * Annotation used to bind an event to a method. This bind is composed of a name
	 * (the attribute key) and an element type. For example, the annotation
	 * 
	 * <pre>
	 * &#64;Bind(value = &quot;test&quot;, type = ElementType.NODE)
	 * </pre>
	 * 
	 * will be triggered the annotated method when receiving 'nodeAttributeXXX()'
	 * methods.
	 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(java.lang.annotation.ElementType.METHOD)
	public static @interface Bind {
		/**
		 * Name of the attribute key that triggered the annotated method.
		 * 
		 * @return an attribute key
		 */
		String value();

		/**
		 * Type of element that triggered the annotated method. Default is GRAPH.
		 * 
		 * @return type of element in GRAPH, NODE or EDGE
		 */
		ElementType type() default ElementType.GRAPH;
	}

	private final EnumMap<ElementType, MethodMap> methods;

	protected AnnotatedSink() {
		methods = new EnumMap<ElementType, MethodMap>(ElementType.class);
		methods.put(ElementType.GRAPH, new MethodMap());
		methods.put(ElementType.EDGE, new MethodMap());
		methods.put(ElementType.NODE, new MethodMap());

		Method[] ms = getClass().getMethods();

		if (ms != null) {
			for (int i = 0; i < ms.length; i++) {
				Method m = ms[i];
				Bind b = m.getAnnotation(Bind.class);

				if (b != null)
					methods.get(b.type()).put(b.value(), m);
			}
		}
	}

	private void invoke(Method m, Object... args) {
		try {
			m.invoke(this, args);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId, String attribute, Object value) {
		Method m = methods.get(ElementType.EDGE).get(attribute);

		if (m != null)
			invoke(m, edgeId, attribute, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeChanged(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void edgeAttributeChanged(String sourceId, long timeId, String edgeId, String attribute, Object oldValue,
			Object newValue) {
		Method m = methods.get(ElementType.EDGE).get(attribute);

		if (m != null)
			invoke(m, edgeId, attribute, newValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeRemoved(java.lang.String,
	 * long, java.lang.String, java.lang.String)
	 */
	public void edgeAttributeRemoved(String sourceId, long timeId, String edgeId, String attribute) {
		Method m = methods.get(ElementType.EDGE).get(attribute);

		if (m != null)
			invoke(m, edgeId, attribute, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#graphAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.Object)
	 */
	public void graphAttributeAdded(String sourceId, long timeId, String attribute, Object value) {
		Method m = methods.get(ElementType.GRAPH).get(attribute);

		if (m != null)
			invoke(m, attribute, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#graphAttributeChanged(java.lang.String,
	 * long, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void graphAttributeChanged(String sourceId, long timeId, String attribute, Object oldValue,
			Object newValue) {
		Method m = methods.get(ElementType.GRAPH).get(attribute);

		if (m != null)
			invoke(m, attribute, newValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#graphAttributeRemoved(java.lang.String,
	 * long, java.lang.String)
	 */
	public void graphAttributeRemoved(String sourceId, long timeId, String attribute) {
		Method m = methods.get(ElementType.GRAPH).get(attribute);

		if (m != null)
			invoke(m, attribute, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId, String attribute, Object value) {
		Method m = methods.get(ElementType.NODE).get(attribute);

		if (m != null)
			invoke(m, nodeId, attribute, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeChanged(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void nodeAttributeChanged(String sourceId, long timeId, String nodeId, String attribute, Object oldValue,
			Object newValue) {
		Method m = methods.get(ElementType.NODE).get(attribute);

		if (m != null)
			invoke(m, nodeId, attribute, newValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeRemoved(java.lang.String,
	 * long, java.lang.String, java.lang.String)
	 */
	public void nodeAttributeRemoved(String sourceId, long timeId, String nodeId, String attribute) {
		Method m = methods.get(ElementType.NODE).get(attribute);

		if (m != null)
			invoke(m, nodeId, attribute, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#edgeAdded(java.lang.String, long,
	 * java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId,
			boolean directed) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#edgeRemoved(java.lang.String, long,
	 * java.lang.String)
	 */
	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#graphCleared(java.lang.String, long)
	 */
	public void graphCleared(String sourceId, long timeId) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#nodeAdded(java.lang.String, long,
	 * java.lang.String)
	 */
	public void nodeAdded(String sourceId, long timeId, String nodeId) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#nodeRemoved(java.lang.String, long,
	 * java.lang.String)
	 */
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#stepBegins(java.lang.String, long,
	 * double)
	 */
	public void stepBegins(String sourceId, long timeId, double step) {
	}

	private static class MethodMap extends HashMap<String, Method> {
		private static final long serialVersionUID = 1664854698109523697L;
	}
}
