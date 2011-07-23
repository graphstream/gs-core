package org.graphstream.stream.file.pajek;

import java.util.ArrayList;
import java.util.Locale;

import org.graphstream.graph.implementations.AbstractElement.AttributeChangeEvent;
import org.graphstream.stream.SourceBase.ElementType;
import org.graphstream.stream.file.FileSourcePajek;
import org.graphstream.util.parser.ParseException;
import org.graphstream.util.parser.Token;

public class PajekContext {
	FileSourcePajek pajek;
	String sourceId;

	protected boolean directed = false;

	protected String weightAttributeName = "weight";

	public PajekContext(FileSourcePajek pajek) {
		this.pajek = pajek;
		this.sourceId = String.format("<Pajek stream %d>", System.currentTimeMillis());
	}

	protected void setDirected(boolean on) {
		directed = on;
	}

	protected int addNodes(Token nb) throws ParseException {
		int n = getInt(nb);

		for (int i = 1; i <= n; ++i) {
			pajek.sendNodeAdded(sourceId, String.format("%d", i));
		}

		return n;
	}

	protected void addGraphAttribute(String attr, String value) {
		pajek.sendAttributeChangedEvent(sourceId, sourceId, ElementType.GRAPH,
				attr, AttributeChangeEvent.ADD, null, value);
	}

	protected void addNodeLabel(String nb, String label) {
		pajek.sendAttributeChangedEvent(sourceId, nb, ElementType.NODE,
				"ui.label", AttributeChangeEvent.ADD, null, label);
	}

	protected void addNodeGraphics(String id, NodeGraphics graphics) {
		pajek.sendAttributeChangedEvent(sourceId, id, ElementType.NODE,
				"ui.style", AttributeChangeEvent.ADD, null, graphics.getStyle());
	}

	protected void addNodePosition(String id, Token x, Token y, Token z)
			throws ParseException {
		Object pos[] = new Object[3];
		pos[0] = (Double) getReal(x);
		pos[1] = (Double) getReal(y);
		pos[2] = z != null ? (Double) getReal(z) : 0;
		
		pajek.sendAttributeChangedEvent(sourceId, id, ElementType.NODE,
				"xyz", AttributeChangeEvent.ADD, null, pos);
	}

	protected String addEdge(String src, String trg) {
		String id = String.format("%s_%s_%d", src, trg,
				(long) (Math.random() * 100000) + System.currentTimeMillis());

		pajek.sendEdgeAdded(sourceId, id, src, trg, directed);

		return id;
	}

	protected void addEdges(EdgeMatrix mat) {
		int size = mat.size();
		int edgeid = 0;

		for (int line = 0; line < size; line++) {
			for (int col = 0; col < size; col++) {
				if (mat.hasEdge(line, col)) {
					String id = String.format("%d_%d_%d", line + 1, col + 1,
							edgeid++);
					if (mat.hasEdge(col, line)) {
						pajek.sendEdgeAdded(sourceId, id,
								String.format("%d", line + 1),
								String.format("%d", col + 1), false);
						mat.set(col, line, false);
					} else {
						pajek.sendEdgeAdded(sourceId, id,
								String.format("%d", line + 1),
								String.format("%d", col + 1), true);
					}
				}
			}
		}
	}

	protected void addEdgeWeight(String id, Token nb) throws ParseException {
		pajek.sendAttributeChangedEvent(sourceId, id, ElementType.EDGE,
				weightAttributeName, AttributeChangeEvent.ADD, null, getReal(nb));
	}

	protected void addEdgeGraphics(String id, EdgeGraphics graphics) {
		pajek.sendAttributeChangedEvent(sourceId, id, ElementType.EDGE,
				"ui.style", AttributeChangeEvent.ADD, null, graphics.getStyle());
	}

	protected static int getInt(Token nb) throws ParseException {
		try {
			return Integer.parseInt(nb.image);
		} catch (Exception e) {
			throw new ParseException(String.format("%d:%d: %s not an integer",
					nb.beginLine, nb.beginColumn, nb.image));
		}
	}

	protected static double getReal(Token nb) throws ParseException {
		try {
			return Double.parseDouble(nb.image);
		} catch (Exception e) {
			throw new ParseException(String.format("%d:%d: %s not a real",
					nb.beginLine, nb.beginColumn, nb.image));
		}
	}

	public static String toColorValue(Token R, Token G, Token B)
			throws ParseException {
		double r = getReal(R);
		double g = getReal(G);
		double b = getReal(B);

		return String.format("rgb(%d, %d, %d)", (int) (r * 255),
				(int) (g * 255), (int) (b * 255));
	}
}

abstract class Graphics {
	protected StringBuffer graphics = new StringBuffer();

	public abstract void addKey(String key, String value, Token tk)
			throws ParseException;

	public String getStyle() {
		return graphics.toString();
	}

	protected double getReal(String nb, Token tk) throws ParseException {
		try {
			return Double.parseDouble(nb);
		} catch (Exception e) {
			throw new ParseException(String.format("%d:%d: %s not a real",
					tk.beginLine, tk.beginColumn, nb));
		}
	}

	protected int getInt(String nb, Token tk) throws ParseException {
		try {
			return Integer.parseInt(nb);
		} catch (Exception e) {
			throw new ParseException(String.format("%d:%d: %s not an integer",
					tk.beginLine, tk.beginColumn, nb));
		}
	}
}

class NodeGraphics extends Graphics {
	@Override
	public void addKey(String key, String value, Token tk)
			throws ParseException {
		if (key.equals("shape")) {
			graphics.append(String.format("shape: %s;", value));
		} else if (key.equals("ic")) {
			graphics.append(String.format("fill-color: %s;", value));
		} else if (key.equals("bc")) {
			graphics.append(String.format(
					"stroke-color: %s; stroke-mode: plain;", value));
		} else if (key.equals("bw")) {
			graphics.append(String.format(Locale.US, "stroke-width: %fpx;",
					getReal(value, tk)));
		} else if (key.equals("s_size")) {
			graphics.append(String.format(Locale.US, "size: %fpx;",
					getReal(value, tk)));
		} else if (key.equals("lc")) {
			graphics.append(String.format("text-color: %s;", value));
		} else if (key.equals("fos")) {
			graphics.append(String.format("text-size: %d;", getInt(value, tk)));
		} else if (key.equals("font")) {
			graphics.append(String.format("text-font: %s;", value));
		}
	}
}

class EdgeGraphics extends Graphics {
	@Override
	public void addKey(String key, String value, Token tk)
			throws ParseException {
		if (key.equals("w")) {
			graphics.append(String.format(Locale.US, "size: %fpx;",
					getReal(value, tk)));
		} else if (key.equals("c")) {
			graphics.append(String.format("fill-color: %s;", value));
		} else if (key.equals("s")) {
			double s = getReal(value, tk);
			graphics.append(String.format("arrow-size: %spx, %spx;", s * 5,
					s * 3));
		} else if (key.equals("l")) {
			// ?
		} else if (key.equals("p")) {
			// ?
		} else if (key.equals("lc")) {
			graphics.append(String.format("text-color: %s;", value));
		} else if (key.equals("fos")) {
			graphics.append(String.format("text-size: %d;", getInt(value, tk)));
		} else if (key.equals("font")) {
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
		mat = new boolean[size][size]; // Horror !
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
		if (curLine < mat.length) {
			if (line.size() == mat.length) {
				for (int i = 0; i < mat.length; i++) {
					mat[curLine][i] = line.get(i).equals("1");
				}
				curLine++;
			} else if (line.size() == mat.length * mat.length) {
				int n = mat.length * mat.length;
				curLine = -1;
				for (int i = 0; i < n; i++) {
					if (i % mat.length == 0)
						curLine++;
					mat[curLine][i - (curLine * mat.length)] = line.get(i)
							.equals("1");
				}
			}
		}
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (int line = 0; line < mat.length; line++) {
			for (int col = 0; col < mat.length; col++) {
				buffer.append(String.format("%s ", mat[line][col] ? "1" : "0"));
			}
			buffer.append(String.format("%n"));
		}

		return buffer.toString();
	}
}
