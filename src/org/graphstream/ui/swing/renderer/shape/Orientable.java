package org.graphstream.ui.swing.renderer.shape;

import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.geom.Vector2;
import org.graphstream.ui.graphicGraph.GraphicEdge;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.graphicGraph.GraphicSprite;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.SpriteOrientation;
import org.graphstream.ui.swing.SwingDefaultCamera;
import org.graphstream.ui.swing.renderer.ConnectorSkeleton;
import org.graphstream.ui.swing.renderer.Skeleton;

/** Trait for all shapes that points at a direction. */
public class Orientable {
    /** The shape orientation. */
	StyleConstants.SpriteOrientation orientation = null ;
	
	/** The shape target. */
	public Point3 target = new Point3();
	
	/** Configure the orientation mode for the group according to the style. */
	public void configureOrientableForGroup(Style style, SwingDefaultCamera camera) { orientation = style.getSpriteOrientation(); }
	
	/** Compute the orientation vector for the given element according to the orientation mode. */
	public void configureOrientableForElement(SwingDefaultCamera camera, GraphicSprite sprite) {
		if ( sprite.getAttachment() instanceof GraphicNode ) {
			switch (sprite.getStyle().getSpriteOrientation()) {
				case NONE: 
					target.set(0, 0); 
					break;
				case FROM: 
					target.set(((GraphicNode)sprite.getAttachment()).getX(), ((GraphicNode)sprite.getAttachment()).getY());
					break;
				case TO:
					target.set(((GraphicNode)sprite.getAttachment()).getX(), ((GraphicNode)sprite.getAttachment()).getY());
					break;
				case PROJECTION: 
					target.set(((GraphicNode)sprite.getAttachment()).getX(), ((GraphicNode)sprite.getAttachment()).getY());
					break;
				default:
					break;
			}
		}
		else if ( sprite.getAttachment() instanceof GraphicEdge ) {
			switch (sprite.getStyle().getSpriteOrientation()) {
				case NONE: 
					target.set(0, 0); 
					break;
				case FROM: 
					target.set(((GraphicEdge)sprite.getAttachment()).from.getX(), ((GraphicEdge)sprite.getAttachment()).from.getY());
					break;
				case TO:
					target.set(((GraphicEdge)sprite.getAttachment()).to.getX(), ((GraphicEdge)sprite.getAttachment()).to.getY());
					break;
				case PROJECTION: 
					ConnectorSkeleton ei = (ConnectorSkeleton)((GraphicEdge)sprite.getAttachment()).getAttribute(Skeleton.attributeName) ;
					if(ei != null)
					     ei.pointOnShape(sprite.getX(), target) ;
					else
						setTargetOnLineEdge(camera, sprite, (GraphicEdge)sprite.getAttachment()) ; 
					break;
				default:
					break;
			}
		}
		else {
			orientation = SpriteOrientation.NONE ;
		}
	}
	
	private void setTargetOnLineEdge(SwingDefaultCamera camera, GraphicSprite sprite, GraphicEdge ge) {
		Vector2 dir = new Vector2(ge.to.getX()-ge.from.getX(), ge.to.getY()-ge.from.getY());
		dir.scalarMult(sprite.getX());
		target.set(ge.from.getX() + dir.x(), ge.from.getY() + dir.y());
	}
}