package org.graphstream.io.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FileSinkTikz extends FileSinkBase
{
	public enum NodeShape
	{
		triangle("triangle"),
		circle("circle"),
		rectangle("rectangle"),
		roundedRectangle("rectangle,rounded corners");

		String code;
		
		private NodeShape( String code )
		{
			this.code = code;
		}
		
		public String getCode()
		{
			return code;
		}
	}
	
	class TikZColor
	{
		float alpha;
		float red;
		float green;
		float blue;
	}
	
	class NodeStyle
	{
		NodeShape 	shape;
		float		width;
		String		label;
		float		opacity;

		TikZColor	fillColor;
		TikZColor	drawColor;
		TikZColor	textColor;
		
		public NodeStyle()
		{
			
		}
	}
	
	class EdgeStyle
	{
		float		width;
		TikZColor	color;
		String		src;
		String		trg;
		
		public EdgeStyle( String src, String trg, boolean directed )
		{
			
		}
	}
	
	Map<String,NodeStyle>	nodes;
	Map<String,EdgeStyle>	edges;
	
	Random	random;

	@Override
	protected void outputEndOfFile() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void outputHeader() throws IOException
	{
		random = new Random();
		
		nodes = new HashMap<String,NodeStyle>();
		edges = new HashMap<String,EdgeStyle>();
	}

	@Override
	public void edgeAttributeAdded(String graphId, long timeId, String edgeId,
			String attribute, Object value) {
		
	}

	@Override
	public void edgeAttributeChanged(String graphId, long timeId, String edgeId,
			String attribute, Object oldValue, Object newValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void edgeAttributeRemoved(String graphId, long timeId, String edgeId,
			String attribute) {
		// TODO Auto-generated method stub

	}

	@Override
	public void graphAttributeAdded(String graphId, long timeId, String attribute,
			Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void graphAttributeChanged(String graphId, long timeId, String attribute,
			Object oldValue, Object newValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void graphAttributeRemoved(String graphId, long timeId, String attribute) {
		// TODO Auto-generated method stub

	}

	@Override
	public void nodeAttributeAdded(String graphId, long timeId, String nodeId,
			String attribute, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void nodeAttributeChanged(String graphId, long timeId, String nodeId,
			String attribute, Object oldValue, Object newValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void nodeAttributeRemoved(String graphId, long timeId, String nodeId,
			String attribute) {
		// TODO Auto-generated method stub

	}

	@Override
	public void edgeAdded(String graphId, long timeId, String edgeId, String fromNodeId,
			String toNodeId, boolean directed) {
		if( ! edges.containsKey(edgeId) )
			edges.put(edgeId, new EdgeStyle(fromNodeId,toNodeId,directed));
	}

	@Override
	public void edgeRemoved(String graphId, long timeId, String edgeId) {
		if( edges.containsKey(edgeId) )
			edges.remove(edgeId);
	}

	@Override
	public void graphCleared(String graphId, long timeId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void nodeAdded(String graphId, long timeId, String nodeId)
	{
		if( ! nodes.containsKey(nodeId) )
			nodes.put(nodeId, new NodeStyle());
	}

	@Override
	public void nodeRemoved(String graphId, long timeId, String nodeId)
	{
		if( nodes.containsKey(nodeId) )
			nodes.remove(nodeId);
	}

	@Override
	public void stepBegins(String graphId, long timeId, double time) {
		// TODO Auto-generated method stub

	}

}
