package org.graphstream.ui.swing;

import java.awt.Container;
import java.awt.Graphics2D;

import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.StyleGroup;
import org.graphstream.ui.swing.renderer.GraphBackgroundRenderer;
import org.graphstream.ui.swing.renderer.shape.Shape;

public interface Backend {
    
    /** Called before any prior use of this back-end. */
    void open(Container drawingSurface);
    
    /** Called after finished using this object. */
    void close();
    
    /** Setup the back-end for a new rendering session. */
    void prepareNewFrame(Graphics2D g2);
    
    /** Transform a point in graph units into pixel units.
      * @return the transformed point. */
    Point3 transform(double x, double y, double z);
    
    /** Pass a point in transformed coordinates (pixels) into the reverse transform (into
      * graph units).
      * @return the transformed point. */
    Point3 inverseTransform(double x, double y, double z);
    
    /** Transform a point in graph units into pixel units, the given point is transformed in place
      * and also returned. */
    Point3 transform(Point3 p);
    
    /** Transform a point in pixel units into graph units, the given point is transformed in
      * place and also returned. */
    Point3 inverseTransform(Point3 p);
    
    /** Push the actual transformation on the matrix stack, installing
      * a copy of it on the top of the stack. */
    void pushTransform();
 
    /** Begin the work on the actual transformation matrix. */
    void beginTransform();

    /** Make the top-most matrix as an identity matrix. */
    void setIdentity();
    
    /** Multiply the to-most matrix by a translation matrix. */
    void translate(double tx, double ty, double tz);
    
    /** Multiply the top-most matrix by a rotation matrix. */
    void rotate(double angle, double ax, double ay, double az);
    
    /** Multiply the top-most matrix by a scaling matrix. */
    void scale(double sx, double sy, double sz);
    
    /** End the work on the actual transformation matrix, installing it as the actual modelview
     * matrix. If you do not call this method, all the scaling, translation and rotation are
     * lost. */
    void endTransform();
    
    /** Pop the actual transformation of the matrix stack, restoring
     * the previous one in the stack. */
    void popTransform();
     
    /** Enable or disable anti-aliasing. */
    void setAntialias(Boolean on);
    
    /** Enable or disable the hi-quality mode. */
    void setQuality(Boolean on);

    /** The Java2D graphics. */
    Graphics2D graphics2D();
    
    Shape chooseNodeShape(Shape oldShape, StyleGroup group);
    Shape chooseEdgeShape(Shape oldShape, StyleGroup group); 
    Shape chooseEdgeArrowShape(Shape oldShape, StyleGroup group); 
    Shape chooseSpriteShape(Shape oldShape, StyleGroup group); 
    GraphBackgroundRenderer chooseGraphBackgroundRenderer();
    
    /** The drawing surface.
     * The drawing surface may be different than the one passed as
     * argument to open(), the back-end is free to create a new surface
     * as it sees fit. */
    Container drawingSurface();
}	
