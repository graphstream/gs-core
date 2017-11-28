package org.graphstream.ui.swing.renderer.shape.swing.spriteShapes;

import org.graphstream.ui.geom.Vector2;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicSprite;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.swing.Backend;
import org.graphstream.ui.swing.SwingDefaultCamera;
import org.graphstream.ui.swing.renderer.Skeleton;
import org.graphstream.ui.swing.renderer.shape.Orientable;
import org.graphstream.ui.swing.renderer.shape.swing.baseShapes.PolygonalShape;

public class SpriteArrowShape extends PolygonalShape {
	Orientable orientable ;
	
	public SpriteArrowShape() {
		this.orientable = new Orientable() ;
	}
	
	@Override
	public void configureForGroup(Backend bck, Style style, SwingDefaultCamera camera) {
		super.configureForGroup(bck, style, camera);
		orientable.configureOrientableForGroup(style, camera);
	}
	
	@Override
	public void configureForElement(Backend bck, GraphicElement element, Skeleton skel, SwingDefaultCamera camera) {
		super.configureForElement(bck, element, skel, camera);
		orientable.configureOrientableForElement(camera, (GraphicSprite)element);
	}

	@Override
	public void make(Backend backend, SwingDefaultCamera camera) {
		double x = area.theCenter.x;
		double y = area.theCenter.y;
		Vector2 dir = new Vector2(  orientable.target.x - x, orientable.target.y - y ); 

		dir.normalize();
		Vector2 per = new Vector2( dir.y(), -dir.x() );
		
		dir.scalarMult( area.theSize.x );
		per.scalarMult( area.theSize.y / 2 );

		theShape().reset();
		theShape().moveTo( x + per.x(), y + per.y() );
		theShape().lineTo( x + dir.x(), y + dir.y() );
		theShape().lineTo( x - per.x(), y - per.y() );
		theShape().closePath();
	}

	@Override
	public void makeShadow(Backend backend, SwingDefaultCamera camera) {
		double x = area.theCenter.x + shadowable.theShadowOff.x;
		double y = area.theCenter.y + shadowable.theShadowOff.y;
		Vector2 dir = new Vector2( orientable.target.x - x, orientable.target.y - y );
		dir.normalize();
		Vector2 per = new Vector2( dir.y(), -dir.x() );
		
		dir.scalarMult( area.theSize.x + shadowable.theShadowWidth.x );
		per.scalarMult( ( area.theSize.y + shadowable.theShadowWidth.y ) / 2 );

		theShape().reset();
		theShape().moveTo( x + per.x(), y + per.y() );
		theShape().lineTo( x + dir.x(), y + dir.y() );
		theShape().lineTo( x - per.x(), y - per.y() );
		theShape().closePath();
	}
}