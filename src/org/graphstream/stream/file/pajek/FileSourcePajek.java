/*
 * Copyright 2006 - 2011 
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
package org.graphstream.stream.file.pajek;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import org.graphstream.stream.SourceBase;
import org.graphstream.stream.file.FileSource;

import org.graphstream.utils.parser.ParseException;
import org.graphstream.utils.parser.Token;

/**
 * A parser for the Pajek NET format.
 * 
 * This should support most the NET file format. However this format seems
 * to have numerous derivative that may not be known to us. It parses the
 * edges/arcs, edgeslist/arclist, and matrix notations. It understands a
 * large part of the formating notation, and transform it to CSS "ui.style"
 * attribute stored on nodes and edges.
 * 
 * The standard extension for such files is ".net".
 */
public class FileSourcePajek extends SourceBase implements FileSource {
	
	protected PajekParser parser;

	protected boolean directed = false;
	
	protected String weightAttributeName = "weight";
	
	protected void setDirected(boolean on) {
		directed = on;
	}
	
	public void readAll(String fileName) throws IOException {
		begin(fileName);
		while(nextEvents()) {}
		end();
	}

	public void readAll(URL url) throws IOException {
		begin(url);
		while(nextEvents()) {}
		end();
	}

	public void readAll(InputStream stream) throws IOException {
		begin(stream);
		while(nextEvents()) {}
		end();
	}

	public void readAll(Reader reader) throws IOException {
		begin(reader);
		while(nextEvents()) {}
		end();
	}

	public void begin(String fileName) throws IOException {
		parser = new PajekParser(this, new BufferedReader(new FileReader(new File(fileName))));
		sourceId = String.format("%s%d", fileName, System.currentTimeMillis());
	}

	public void begin(URL url) throws IOException {
		parser = new PajekParser(this, url.openStream());
		sourceId = String.format("%s%d", url.toString(), System.currentTimeMillis());
	}

	public void begin(InputStream stream) throws IOException {
		parser = new PajekParser(this, stream);
		sourceId = String.format("<Pajek stream %d>", System.currentTimeMillis());
	}

	public void begin(Reader reader) throws IOException {
		parser = new PajekParser(this, reader);
		sourceId = String.format("<Pajek stream %d>", System.currentTimeMillis());
	}

	public boolean nextEvents() throws IOException {
		boolean ok = false;
		
		try {
			ok = parser.nextEvents();
		} catch(ParseException e) {
			throw new IOException(e);
		}
		
		return ok;
	}

	public boolean nextStep() throws IOException {
		while(nextEvents()) {}
		return false;
	}

	public void end() throws IOException {
		parser.close();
	}
	
	protected int addNodes(Token nb)
		throws ParseException {
		int n = getInt(nb);
		
		for(int i=1; i<=n; ++i) {
			sendNodeAdded(sourceId, String.format("%d", i));
		}
		
		return n;
	}
	
	protected void addGraphAttribute(String attr, String value) {
		sendGraphAttributeAdded(sourceId, attr, value);
	}
	
	protected void addNodeLabel(String nb, String label) {
		sendNodeAttributeAdded(sourceId, nb, "ui.label", label);
	}
	
	protected void addNodeGraphics(String id, NodeGraphics graphics) {
		//System.err.printf("node style %s%n", graphics.getStyle());
		sendNodeAttributeAdded(sourceId, id, "ui.style", graphics.getStyle());
	}
	
	protected void addNodePosition(String id, Token x, Token y, Token z) 
		throws ParseException {
		Object pos[] = new Object[3];
		pos[0] = (Double)getReal(x);
		pos[1] = (Double)getReal(y);
		pos[2] = z != null ? (Double)getReal(z) : 0;
		sendNodeAttributeAdded(sourceId, id, "xyz", pos);
	}
	
	protected String addEdge(String src, String trg) {
		String id =  String.format("%s_%s_%d", src, trg, (long)(Math.random()*100000)+System.currentTimeMillis());
		
		sendEdgeAdded(sourceId, id, src, trg, directed);

		return id;
	}
	
	protected void addEdges(EdgeMatrix mat) {
		int size = mat.size();
		int edgeid = 0;
		
		for(int line=0; line<size; line++) {
			for(int col=0; col<size; col++) {
				if(mat.hasEdge(line, col)) {
					String id = String.format("%d_%d_%d", line+1, col+1, edgeid++);
					if(mat.hasEdge(col, line)) {
						sendEdgeAdded(sourceId, id, String.format("%d", line+1), String.format("%d", col+1), false);
						mat.set(col, line, false);
					} else {
						sendEdgeAdded(sourceId, id, String.format("%d", line+1), String.format("%d", col+1), true);
					}
				}
			}
		}
	}
	
	protected void addEdgeWeight(String id, Token nb)
		throws ParseException {
		sendEdgeAttributeAdded(sourceId, id, weightAttributeName, getReal(nb));
	}
	
	protected void addEdgeGraphics(String id, EdgeGraphics graphics) {
		//System.err.printf("edge style %s%n", graphics.getStyle());
		sendEdgeAttributeAdded(sourceId, id, "ui.style", graphics.getStyle());
	}
	
	protected static int getInt(Token nb)
		throws ParseException {
		try {
			return Integer.parseInt(nb.image);
		} catch(Exception e) {
			throw new ParseException(String.format("%d:%d: %s not an integer", nb.beginLine, nb.beginColumn, nb.image));
		}
	}
	
	protected static double getReal(Token nb)
		throws ParseException {
		try {
			return Double.parseDouble(nb.image);
		} catch(Exception e) {
			throw new ParseException(String.format("%d:%d: %s not a real", nb.beginLine, nb.beginColumn, nb.image));
		}
	}
	
	public static String toColorValue(Token R, Token G, Token B) throws ParseException {
		double r = getReal(R);
		double g = getReal(G);
		double b = getReal(B);
		
		return String.format("rgb(%d, %d, %d)", (int)(r*255), (int)(g*255), (int)(b*255));
	}
}

abstract class Graphics {
	protected StringBuffer graphics = new StringBuffer();

	public abstract void addKey(String key, String value, Token tk) throws ParseException;
	
	public String getStyle() {
		return graphics.toString();
	}
	
	protected double getReal(String nb, Token tk) throws ParseException {
		try {
			return Double.parseDouble(nb);
		} catch(Exception e) {
			throw new ParseException(String.format("%d:%d: %s not a real", tk.beginLine, tk.beginColumn, nb));
		}
	}
	
	protected int getInt(String nb, Token tk) throws ParseException {
		try {
			return Integer.parseInt(nb);
		} catch(Exception e) {
			throw new ParseException(String.format("%d:%d: %s not an integer", tk.beginLine, tk.beginColumn, nb));
		}
	}
}

class NodeGraphics extends Graphics {
	@Override
	public void addKey(String key, String value, Token tk)
		throws ParseException {
		if(key.equals("shape")) {
			graphics.append(String.format("shape: %s;", value));
		} else if(key.equals("ic")) {
			graphics.append(String.format("fill-color: %s;", value));
		} else if(key.equals("bc")) {
			graphics.append(String.format("stroke-color: %s; stroke-mode: plain;", value));
		} else if(key.equals("bw")) {
			graphics.append(String.format(Locale.US, "stroke-width: %fpx;", getReal(value, tk)));
		} else if(key.equals("s_size")) {
			graphics.append(String.format(Locale.US, "size: %fpx;", getReal(value, tk)));
		} else if(key.equals("lc")) {
			graphics.append(String.format("text-color: %s;", value));
		} else if(key.equals("fos")) {
			graphics.append(String.format("text-size: %d;", getInt(value, tk)));
		} else if(key.equals("font")) {
			graphics.append(String.format("text-font: %s;", value));
		}
	}
}

class EdgeGraphics extends Graphics {
	@Override
	public void addKey(String key, String value, Token tk)
		throws ParseException {
		if(key.equals("w")) {
			graphics.append(String.format(Locale.US, "size: %fpx;", getReal(value, tk)));			
		} else if(key.equals("c")) {
			graphics.append(String.format("fill-color: %s;", value));			
		} else if(key.equals("s")) {
			double s = getReal(value, tk);
			graphics.append(String.format("arrow-size: %spx, %spx;", s*5, s*3));
		} else if(key.equals("l")) {
			// ?
		} else if(key.equals("p")) {
			// ?
		} else if(key.equals("lc")) {
			graphics.append(String.format("text-color: %s;", value));			
		} else if(key.equals("fos")) {
			graphics.append(String.format("text-size: %d;", getInt(value, tk)));			
		} else if(key.equals("font")) {
			graphics.append(String.format("text-font: %s;", value));			
		}
	}
}

class EdgeMatrix {
	// Line first, col second.
	// Line = from node, col = to node.
	protected boolean mat[][];
	
	protected int curLine = 0;
	
	public EdgeMatrix(int size) {
		mat = new boolean[size][size];	// Horror !
	}
	
	public int size() {
		return mat.length;
	}
	
	public boolean hasEdge(int line, int col) {
		return mat[line][col];
	}
	
	public void set(int line, int col, boolean value) {
		mat[line][col] = value;
	}
	
	public void addLine(ArrayList<String> line) {
		if(curLine < mat.length) {
			if(line.size() == mat.length) {
				for(int i=0; i<mat.length; i++) {
					mat[curLine][i] = line.get(i).equals("1");
				}
				curLine++;
			} else if(line.size() == mat.length*mat.length) {
				int n = mat.length * mat.length;
				curLine = -1;
				for(int i=0; i<n; i++) {
					if(i%mat.length == 0)
						curLine++;
					mat[curLine][i-(curLine*mat.length)] = line.get(i).equals("1");
				}
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for(int line=0; line<mat.length; line++) {
			for(int col=0; col<mat.length; col++) {
				buffer.append(String.format("%s ", mat[line][col] ? "1" : "0"));
			}
			buffer.append(String.format("%n"));
		}
		
		return buffer.toString();
	}
}