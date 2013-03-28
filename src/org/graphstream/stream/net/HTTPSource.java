/*
 * Copyright 2006 - 2013
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
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
package org.graphstream.stream.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedList;

import org.graphstream.stream.SourceBase;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * This source allows to control a graph from a web browser. Control is done
 * calling the following url :
 * <code>http://host/graphId/edit?q=ACTION&...</code>. ACTION is one of the
 * following action :
 * <ul>
 * <li>an : add node</li>
 * <li>cn : change node</li>
 * <li>dn : delete node</li>
 * <li>ae : add edge</li>
 * <li>ce : change edge</li>
 * <li>de : delete edge</li>
 * <li>cg : change graph</li>
 * <li>st : step begins</li>
 * <li>clear : clear the whole graph</li>
 * </ul>
 * 
 * Each of these actions needs some argument.
 * <dl>
 * <dt>an</dt>
 * <dd>
 * <ul>
 * <li>id</li>
 * </ul>
 * </dd>
 * <dt>cn</dt>
 * <dd>
 * <ul>
 * <li>id</li>
 * <li>key</li>
 * <li>value</li>
 * </ul>
 * </dd>
 * <dt>dn</dt>
 * <dd>
 * <ul>
 * <li>id</li>
 * </ul>
 * </dd>
 * <dt>ae</dt>
 * <dd>
 * <ul>
 * <li>id</li>
 * <li>from</li>
 * <li>to</li>
 * <li>[directed]</li>
 * </ul>
 * </dd>
 * <dt>ce</dt>
 * <dd>
 * <ul>
 * <li>id</li>
 * <li>key</li>
 * <li>value</li>
 * </ul>
 * </dd>
 * <dt>de</dt>
 * <dd>
 * <ul>
 * <li>id</li>
 * </ul>
 * </dd>
 * <dt>cg</dt>
 * <dd>
 * <ul>
 * <li>key</li>
 * <li>value</li>
 * </ul>
 * </dd>
 * <dt>st</dt>
 * <dd>
 * <ul>
 * <li>step</li>
 * </ul>
 * </dd>
 * </dl>
 */
public class HTTPSource extends SourceBase {

	/**
	 * Http server.
	 */
	protected final HttpServer server;

	/**
	 * Create a new http source. The source will be available on
	 * 'http://localhost/graphId' where graphId is passed as parameter of this
	 * constructor.
	 * 
	 * @param graphId
	 *            id of the graph
	 * @param port
	 *            port on which server will be bound
	 * @throws IOException
	 *             if server creation failed.
	 */
	public HTTPSource(String graphId, int port) throws IOException {
		super(String.format("http://%s", graphId));

		server = HttpServer.create(new InetSocketAddress(port), 4);
		server.createContext(String.format("/%s/edit", graphId),
				new EditHandler());

	}

	/**
	 * Start the http server.
	 */
	public void start() {
		server.start();
	}

	/**
	 * Stop the http server.
	 */
	public void stop() {
		server.stop(0);
	}

	private class EditHandler implements HttpHandler {

		public void handle(HttpExchange ex) throws IOException {
			HashMap<String, Object> get = GET(ex);
			Action a;

			try {
				a = Action.valueOf(get.get("q").toString().toUpperCase());
			} catch (Exception e) {
				error(ex, "invalid action");
				return;
			}

			switch (a) {
			case AN:
				HTTPSource.this.sendNodeAdded(sourceId, get.get("id")
						.toString());
				break;
			case CN:
				break;
			case DN:
				HTTPSource.this.sendNodeRemoved(sourceId, get.get("id")
						.toString());
				break;
			case AE:
				HTTPSource.this.sendEdgeAdded(sourceId, get.get("id")
						.toString(), get.get("from").toString(), get.get("to")
						.toString(), get.containsKey("directed"));
				break;
			case CE:
				break;
			case DE:
				HTTPSource.this.sendEdgeRemoved(sourceId, get.get("id")
						.toString());
				break;
			case CG:
				break;
			case ST:
				HTTPSource.this.sendStepBegins(sourceId, Double.valueOf(get
						.get("step").toString()));
				break;
			}

			ex.sendResponseHeaders(200, 0);
			ex.getResponseBody().close();
		}
	}

	protected static void error(HttpExchange ex, String message)
			throws IOException {
		byte[] data = message.getBytes();

		ex.sendResponseHeaders(400, data.length);
		ex.getResponseBody().write(data);
		ex.getResponseBody().close();
	}

	@SuppressWarnings("unchecked")
	protected static HashMap<String, Object> GET(HttpExchange ex) {
		HashMap<String, Object> get = new HashMap<String, Object>();
		String[] args = ex.getRequestURI().getRawQuery().split("[&]");

		for (String arg : args) {
			String[] kv = arg.split("[=]");
			String k, v;

			k = null;
			v = null;

			try {
				if (kv.length > 0)
					k = URLDecoder.decode(kv[0], System
							.getProperty("file.encoding"));

				if (kv.length > 1)
					v = URLDecoder.decode(kv[1], System
							.getProperty("file.encoding"));

				if (get.containsKey(k)) {
					Object o = get.get(k);

					if (o instanceof LinkedList<?>)
						((LinkedList<Object>) o).add(v);
					else {
						LinkedList<Object> l = new LinkedList<Object>();
						l.add(o);
						l.add(v);
						get.put(k, l);
					}
				} else {
					get.put(k, v);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return get;
	}

	static enum Action {
		AN, CN, DN, AE, CE, DE, CG, ST, CLEAR
	}
}
