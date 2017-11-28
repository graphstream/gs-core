package org.graphstream.ui.swing;

import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.StyleGroup;
import org.graphstream.ui.swing.renderer.GraphBackgroundRenderer;
import org.graphstream.ui.swing.renderer.shape.Shape;
import org.graphstream.ui.swing.renderer.shape.swing.advancedShapes.AngleShape;
import org.graphstream.ui.swing.renderer.shape.swing.advancedShapes.BlobShape;
import org.graphstream.ui.swing.renderer.shape.swing.advancedShapes.CubicCurveShape;
import org.graphstream.ui.swing.renderer.shape.swing.advancedShapes.FreePlaneEdgeShape;
import org.graphstream.ui.swing.renderer.shape.swing.advancedShapes.HorizontalSquareEdgeShape;
import org.graphstream.ui.swing.renderer.shape.swing.advancedShapes.LSquareEdgeShape;
import org.graphstream.ui.swing.renderer.shape.swing.advancedShapes.PieChartShape;
import org.graphstream.ui.swing.renderer.shape.swing.arrowShapes.ArrowOnEdge;
import org.graphstream.ui.swing.renderer.shape.swing.arrowShapes.CircleOnEdge;
import org.graphstream.ui.swing.renderer.shape.swing.arrowShapes.DiamondOnEdge;
import org.graphstream.ui.swing.renderer.shape.swing.arrowShapes.ImageOnEdge;
import org.graphstream.ui.swing.renderer.shape.swing.baseShapes.LineShape;
import org.graphstream.ui.swing.renderer.shape.swing.baseShapes.PolylineEdgeShape;
import org.graphstream.ui.swing.renderer.shape.swing.basicShapes.CircleShape;
import org.graphstream.ui.swing.renderer.shape.swing.basicShapes.CrossShape;
import org.graphstream.ui.swing.renderer.shape.swing.basicShapes.DiamondShape;
import org.graphstream.ui.swing.renderer.shape.swing.basicShapes.FreePlaneNodeShape;
import org.graphstream.ui.swing.renderer.shape.swing.basicShapes.PolygonShape;
import org.graphstream.ui.swing.renderer.shape.swing.basicShapes.RoundedSquareShape;
import org.graphstream.ui.swing.renderer.shape.swing.basicShapes.SquareShape;
import org.graphstream.ui.swing.renderer.shape.swing.basicShapes.TriangleShape;
import org.graphstream.ui.swing.renderer.shape.swing.spriteShapes.OrientableSquareShape;
import org.graphstream.ui.swing.renderer.shape.swing.spriteShapes.SpriteArrowShape;
import org.graphstream.ui.swing.renderer.shape.swing.spriteShapes.SpriteFlowShape;

public class BackendJ2D implements Backend {
	
	private Container surface ;
	private Graphics2D g2 ;
	private Stack<AffineTransform> matrixStack ;
	private AffineTransform Tx ;
	private AffineTransform xT ;
	private Point2D dummyPoint = new Point2D.Double();
	
	public BackendJ2D() {
		surface = null ;
		g2 = null ;
		matrixStack = new Stack<>() ;
		Tx = null ;
		xT = null ;
	}
	
	public void setGraphics2D(Graphics2D g) {
		this.g2 = g ;
	}
		
	@Override
	public void open(Container drawingSurface) {
		surface = drawingSurface ;
	}

	@Override
	public void close() {
		surface = null ;
	}

	@Override
	public void prepareNewFrame(Graphics2D g) {
		this.g2 = g ;
		Tx = g2.getTransform();
		matrixStack.clear();
	}

	@Override
	public Point3 transform(double x, double y, double z) {
		dummyPoint.setLocation(x, y);
		Tx.transform(dummyPoint, dummyPoint);
		return new Point3(dummyPoint.getX(), dummyPoint.getY(), 0);
	}

	@Override
	public Point3 inverseTransform(double x, double y, double z) {
		dummyPoint.setLocation(x, y);
        xT.transform(dummyPoint, dummyPoint);
        return new Point3(dummyPoint.getX(), dummyPoint.getY(), 0);
	}

	@Override
	public Point3 transform(Point3 p) {
		dummyPoint.setLocation(p.x, p.y);
		Tx.transform(dummyPoint, dummyPoint);
		p.set(dummyPoint.getX(), dummyPoint.getY(), 0);
		return p;
	}

	@Override
	public Point3 inverseTransform(Point3 p) {
		dummyPoint.setLocation(p.x, p.y);
		xT.transform(dummyPoint, dummyPoint);
		p.set(dummyPoint.getX(), dummyPoint.getY(), 0);
		return p;
	}

	@Override
	public void pushTransform() {
		matrixStack.push(g2.getTransform());
	}

	@Override
	public void beginTransform() {}

	@Override
	public void setIdentity() {
		Tx.setToIdentity();
	}

	@Override
	public void translate(double tx, double ty, double tz) {
		g2.translate(tx, ty);
	}

	@Override
	public void rotate(double angle, double ax, double ay, double az) {
		g2.rotate(angle);
	}

	@Override
	public void scale(double sx, double sy, double sz) {
		g2.scale(sx, sy);
	}

	@Override
	public void endTransform() {
		Tx = g2.getTransform();
		computeInverse();
	}

	private void computeInverse() {
		try {
			xT = new AffineTransform(Tx);
			xT.invert();
		}
		catch (NoninvertibleTransformException e) {
			 Logger.getLogger(this.getClass().getSimpleName()).log(Level.WARNING, "Cannot inverse matrix.", e);
		}
	}

	@Override
	public void popTransform() {
		assert(!matrixStack.isEmpty());
		g2.setTransform(matrixStack.pop());
	}

	@Override
	public void setAntialias(Boolean on) {
		if(on) {
			g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,    RenderingHints.VALUE_STROKE_PURE);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
		}
		else {
			g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,    RenderingHints.VALUE_STROKE_DEFAULT);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_OFF);
		}
	}

	@Override
	public void setQuality(Boolean on) {
		if(on) {
			g2.setRenderingHint(RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_QUALITY);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,       RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,     RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		} else {
			g2.setRenderingHint(RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_SPEED);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,       RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,     RenderingHints.VALUE_COLOR_RENDER_SPEED);
			g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		}
	}

	@Override
	public Graphics2D graphics2D() {
		return g2 ;
	}

	@Override
	public Shape chooseNodeShape(Shape oldShape, StyleGroup group) {
		switch (group.getShape()) {
			case CIRCLE:
				if(oldShape instanceof CircleShape)	
					return oldShape ;
				else 								
					return new CircleShape();
			case BOX:
				if(oldShape instanceof SquareShape)
					return oldShape ;
				else
					return new SquareShape();
			case ROUNDED_BOX:
				if(oldShape instanceof RoundedSquareShape)	
					return oldShape ;
				else
					return new RoundedSquareShape();
			case DIAMOND:
				if(oldShape instanceof DiamondShape)	
					return oldShape ;
				else
					return new DiamondShape();
			case TRIANGLE:
				if(oldShape instanceof TriangleShape)
					return oldShape ;
				else
					return new TriangleShape();
			case CROSS:
				if(oldShape instanceof CrossShape)
					return oldShape ;
				else
					return new CrossShape();
			case FREEPLANE:
				if(oldShape instanceof FreePlaneNodeShape)	
					return oldShape ;
				else
					return new FreePlaneNodeShape();
			case PIE_CHART:
				if(oldShape instanceof PieChartShape)
					return oldShape ;
				else
					return new PieChartShape();
			case POLYGON:
				if(oldShape instanceof PolygonShape)	
					return oldShape ;
				else
					return new PolygonShape();
			//------
			case TEXT_BOX:
				Logger.getLogger(this.getClass().getSimpleName()).warning("** SORRY text-box shape not yet implemented **");  
				return new SquareShape();
			case TEXT_PARAGRAPH:
				Logger.getLogger(this.getClass().getSimpleName()).warning("** SORRY text-para shape not yet implemented **");  
				return new SquareShape();
			case TEXT_CIRCLE:
				Logger.getLogger(this.getClass().getSimpleName()).warning("** SORRY text-circle shape not yet implemented **");  
				return new CircleShape();
			case TEXT_DIAMOND:
				Logger.getLogger(this.getClass().getSimpleName()).warning("** SORRY text-diamond shape not yet implemented **");  
				return new CircleShape();
			case ARROW:
				Logger.getLogger(this.getClass().getSimpleName()).warning("** SORRY arrow shape not yet implemented **");  
				return new CircleShape();
			case IMAGES:
				Logger.getLogger(this.getClass().getSimpleName()).warning("** SORRY images shape not yet implemented **");  
				return new SquareShape();
			//----
			case JCOMPONENT:
				throw new RuntimeException("Jcomponent should have its own renderer");
			default:
				throw new RuntimeException(group.getShape().toString()+" shape cannot be set for nodes");
		}
	}

	@Override
	public Shape chooseEdgeShape(Shape oldShape, StyleGroup group) {
		switch(group.getShape()) {
			case LINE:
				if(oldShape instanceof LineShape)	
					return oldShape ;
				else 								
					return new LineShape();
			case ANGLE:
				if(oldShape instanceof AngleShape)	
					return oldShape ;
				else 								
					return new AngleShape();
			case BLOB:
				if(oldShape instanceof BlobShape)	
					return oldShape ;
				else 								
					return new BlobShape();
			case CUBIC_CURVE:
				if(oldShape instanceof CubicCurveShape)	
					return oldShape ;
				else 								
					return new CubicCurveShape();
			case FREEPLANE:
				if(oldShape instanceof FreePlaneEdgeShape)	
					return oldShape ;
				else 								
					return new FreePlaneEdgeShape();
			case POLYLINE:
				if(oldShape instanceof PolylineEdgeShape)	
					return oldShape ;
				else 								
					return new PolylineEdgeShape();
			case SQUARELINE:
				Logger.getLogger(this.getClass().getSimpleName()).warning("** SORRY square-line shape not yet implemented **");
				return new HorizontalSquareEdgeShape() ;
			case LSQUARELINE:
				if(oldShape instanceof LSquareEdgeShape)	
					return oldShape ;
				else 								
					return new LSquareEdgeShape();
			case HSQUARELINE:
				if(oldShape instanceof HorizontalSquareEdgeShape)	
					return oldShape ;
				else 								
					return new HorizontalSquareEdgeShape();
			case VSQUARELINE:
				Logger.getLogger(this.getClass().getSimpleName()).warning("** SORRY square-line shape not yet implemented **");
				return new HorizontalSquareEdgeShape() ;
			default:
				throw new RuntimeException(group.getShape()+" shape cannot be set for edges");
		}
	}

	@Override
	public Shape chooseEdgeArrowShape(Shape oldShape, StyleGroup group) {
		switch (group.getArrowShape()) {
			case NONE:
				return null ;
			case ARROW:
				if(oldShape instanceof ArrowOnEdge)	
					return oldShape ;
				else 								
					return new ArrowOnEdge();
			case CIRCLE:
				if(oldShape instanceof CircleOnEdge)	
					return oldShape ;
				else 								
					return new CircleOnEdge();
			case DIAMOND:
				if(oldShape instanceof DiamondOnEdge)	
					return oldShape ;
				else 								
					return new DiamondOnEdge();
			case IMAGE:
				if(oldShape instanceof ImageOnEdge)	
					return oldShape ;
				else 								
					return new ImageOnEdge();
			default:
				throw new RuntimeException(group.getArrowShape().toString()+" shape cannot be set for edge arrows");
		}
	}

	@Override
	public Shape chooseSpriteShape(Shape oldShape, StyleGroup group) {
		switch (group.getShape()) {
		case CIRCLE:
			if(oldShape instanceof CircleShape)	
				return oldShape ;
			else 								
				return new CircleShape();
		case BOX:
			if(oldShape instanceof OrientableSquareShape)
				return oldShape ;
			else
				return new OrientableSquareShape();
		case ROUNDED_BOX:
			if(oldShape instanceof RoundedSquareShape)	
				return oldShape ;
			else
				return new RoundedSquareShape();
		case DIAMOND:
			if(oldShape instanceof DiamondShape)	
				return oldShape ;
			else
				return new DiamondShape();
		case TRIANGLE:
			if(oldShape instanceof TriangleShape)
				return oldShape ;
			else
				return new TriangleShape();
		case CROSS:
			if(oldShape instanceof CrossShape)
				return oldShape ;
			else
				return new CrossShape();
		case ARROW:
			if(oldShape instanceof SpriteArrowShape)
				return oldShape ;
			else
				return new SpriteArrowShape();
		case FLOW:
			if(oldShape instanceof SpriteFlowShape)	
				return oldShape ;
			else
				return new SpriteFlowShape();
		case PIE_CHART:
			if(oldShape instanceof PieChartShape)
				return oldShape ;
			else
				return new PieChartShape();
		case POLYGON:
			if(oldShape instanceof PolygonShape)	
				return oldShape ;
			else
				return new PolygonShape();
		//------
		case TEXT_BOX:
			Logger.getLogger(this.getClass().getSimpleName()).warning("** SORRY text-box shape not yet implemented **");  
			return new SquareShape();
		case TEXT_PARAGRAPH:
			Logger.getLogger(this.getClass().getSimpleName()).warning("** SORRY text-para shape not yet implemented **");  
			return new SquareShape();
		case TEXT_CIRCLE:
			Logger.getLogger(this.getClass().getSimpleName()).warning("** SORRY text-circle shape not yet implemented **");  
			return new CircleShape();
		case TEXT_DIAMOND:
			Logger.getLogger(this.getClass().getSimpleName()).warning("** SORRY text-diamond shape not yet implemented **");  
			return new CircleShape();
		case IMAGES:
			Logger.getLogger(this.getClass().getSimpleName()).warning("** SORRY images shape not yet implemented **");  
			return new SquareShape();
		//----
		case JCOMPONENT:
			throw new RuntimeException("Jcomponent should have its own renderer");
		default:
			throw new RuntimeException(group.getShape().toString()+" shape cannot be set for nodes");
	}
	}

	@Override
	public GraphBackgroundRenderer chooseGraphBackgroundRenderer() {
		return null ;
	}

	@Override
	public Container drawingSurface() {
		return surface ;
	}

}
