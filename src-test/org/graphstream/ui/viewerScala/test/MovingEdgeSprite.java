package org.graphstream.ui.viewerScala.test;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;
import org.graphstream.ui.spriteManager.Sprite;

public class MovingEdgeSprite extends Sprite {
	public double SPEED = 0.001f;
	public double speed = SPEED;
	public double off = 0f;
	public Units units = Units.PX;

	public void setOffsetPx( float offset ) { off = offset; units = Units.PX; }

	public void move() {
		double p = getX();

		p += speed;

		if( p < 0 || p > 1 ) {
			Edge edge = null ;
			if ( getAttachment() instanceof Edge)
				edge = (Edge)getAttachment();
		
			if( edge != null ) {				
				Node node = edge.getSourceNode();
				if( p > 1 )
					node = edge.getTargetNode() ;
				Edge other = randomOutEdge( node );

				if(other==null) {
				    System.err.println("node "+node.getId()+" out="+node.getOutDegree()+" null !!");
				}

				if( node.getOutDegree() > 1 ) { 
					while( other.equals(edge) )
						other = randomOutEdge( node );
				}

				attachToEdge( other.getId() );
				if( node.equals(other.getSourceNode()) ) {
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