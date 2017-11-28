package org.graphstream.ui.viewerScala.test;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;
import org.graphstream.ui.graphicGraph.stylesheet.Values;
import org.graphstream.ui.scalaViewer.ScalaGraphRenderer;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteFactory;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.swingViewer.SwingViewer;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

public class TestStars implements  ViewerListener {
	public static void main(String[] args) {
		(new TestStars()).run();
	}

	boolean loop = true ;
	private void run() {
		MultiGraph	graph  = new MultiGraph( "TestSprites" );
		
		Viewer viewer = new SwingViewer( graph, SwingViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD );
		ViewerPipe pipeIn = viewer.newViewerPipe();
		viewer.addView( "view1", new ScalaGraphRenderer() );

		pipeIn.addAttributeSink( graph );
		pipeIn.addViewerListener( this );
		pipeIn.pump();

		graph.setAttribute( "ui.stylesheet", styleSheet );
		graph.setAttribute( "ui.antialias" );
		graph.setAttribute( "ui.quality" );
		
		Node A = graph.addNode( "A" );
		Node B = graph.addNode( "B" );
		Node C = graph.addNode( "C" );
		Node D = graph.addNode( "D" );
		Node E = graph.addNode( "E" );
		
		graph.addEdge( "AB", "A", "B" );
		graph.addEdge( "BC", "B", "C" );
		graph.addEdge( "CD", "C", "D" );
		graph.addEdge( "DA", "D", "A" );
		graph.addEdge( "DE", "D", "E" );
		graph.addEdge( "EB", "E", "B" );

		A.setAttribute("xyz", new double[] { 0, 1, 0 });
		B.setAttribute("xyz", new double[] { 1.5, 1, 0 });
		C.setAttribute("xyz", new double[] { 1, 0, 0 });
		D.setAttribute("xyz", new double[] { 0, 0, 0 });
		E.setAttribute("xyz", new double[] { 0.4, 0.6, 0 });

		SpriteManager sman = new SpriteManager( graph );

		sman.setSpriteFactory( new MySpriteFactory() );

		MySprite s1 = (MySprite)sman.addSprite( "S1" );
		MySprite s2 = (MySprite)sman.addSprite( "S2" );
		MySprite s3 = (MySprite)sman.addSprite( "S3" );

		s1.attachToEdge( "AB" );
		s2.attachToEdge( "CD" );
		s3.attachToEdge( "DA" );

		while( loop ) {
			pipeIn.pump();
			s1.move();
			s2.move();
			s3.move();
			sleep( 10 );
		}
		
		System.out.println( "bye bye" );
		System.exit(0);
	}
	
	protected void sleep( long ms ) {
		try {
			Thread.sleep( ms );
		} catch (InterruptedException e) { e.printStackTrace(); }
	}
	
	// Viewer Listener Interface
	
	public void viewClosed( String id ) { loop = false ;}

	public void buttonPushed( String id ) {
		if( id == "quit" )
 			loop = false;
	}

 	public void buttonReleased( String id ) {}

 	private String styleSheet = 
 			"graph {"+
			"	canvas-color: black;"+
			"		fill-mode: gradient-vertical;"+
			"		fill-color: black, #004;"+
			"		padding: 60px;"+
			"	}"+
			"node {"+
			"	shape: circle;"+
			"	size: 14px;"+
			"	fill-mode: gradient-radial;"+
			"	fill-color: #FFFA, #FFF0;"+
			"	stroke-mode: none;"+
			"	shadow-mode: gradient-radial;"+
			"	shadow-color: #FFF9, #FFF0;"+
			"	shadow-width: 10px;"+
			"	shadow-offset: 0px, 0px;"+
			"}"+
			"node:clicked {"+
			"	fill-color: #F00A, #F000;"+
			"}"+
			"node:selected {"+
			"	fill-color: #00FA, #00F0;"+
			"}"+
			"edge {"+
			"	shape: line;"+
			"	size: 1px;"+
			"	fill-color: #FFF3;"+
			"	fill-mode: plain;"+
			"	arrow-shape: none;"+
			"}"+
			"sprite {"+
			"	shape: circle;"+
			"	fill-mode: gradient-radial;"+
			"	fill-color: #FFF8, #FFF0;"+
			"}";
 	
 	class MySpriteFactory extends SpriteFactory {
 		
 		@Override
 		public Sprite newSprite(String identifier, SpriteManager manager, Values position) {
 			if( position != null )
				return new MySprite( identifier, manager, position );
 			else
 				return new MySprite( identifier, manager );
 		}
	}
 	
 	class MySprite extends Sprite {
 		public MySprite(String id, SpriteManager manager, Values pos) {
 			super(id, manager, pos);
 		}
 		
 		public MySprite(String id, SpriteManager manager) {
			this(id, manager, new Values(StyleConstants.Units.GU, 0, 0, 0));
		}
 		
 		double SPEED = 0.005f;
 		double speed = SPEED;
 		double off = 0f;
 		Units units = Units.PX;

 		public void setOffsetPx( float offset ) { off = offset; units = Units.PX ;}

 		public void move() {
 			double p = getX();

 			p += speed;

 			if( p < 0 || p > 1 ) {
 				Edge edge = (Edge) getAttachment();
 				
 				if( edge != null ) {
 					Node node = edge.getSourceNode(); 
 					if( p > 1 ) 
 						node = edge.getTargetNode(); 
 					
 					Edge other = randomOutEdge( node );
 							
 					if( node.getOutDegree() > 1 ) { 
 						while( other == edge ) 
 							other = randomOutEdge( node ); 
 					}

 					attachToEdge( other.getId() );
 					if( node == other.getSourceNode() ) {
 						setPosition( units, 0, off, 0 );
 						speed = SPEED;
 					} else {
 						setPosition( units, 1, off, 0 );
 						speed = -SPEED;
 					}
 				}
 			} 
 			else {
 				setPosition( units, p, off, 0 );
 			}
 		}

 		public Edge randomOutEdge(Node node) {
 			int min = 0 ;
 			int max = (int) node.leavingEdges().count();
 			
 			int rand = (int) (min + (Math.random() * (max - min)));
 			
 			return node.getLeavingEdge(rand);
 		}
 	}
 	
 	public void mouseOver(String id){}

	public void mouseLeft(String id){}
}
