package org.graphstream.ui.geom;


/**
 * Pivot and bounding box.
 *
 * <p>A locator define the size (as a bounding box) and position (as a pivot
 * point) of a sprite inside the projection space of its parent sprite. The
 * values are always expressed in the units of the parent. The bounding box is
 * expressed with respect to the pivot, whereas the pivot is expressed with
 * respect to the parent's pivot.</p>
 *
 * @author Antoine Dutot
 * @since 20040110
 */
public class Locator
	extends Box3
{
// Attributes

	/**
	 * 
	 */
	private static final long serialVersionUID = 5629752956109593217L;

	/**
	 * Pivot point.
	 */
	protected Point3 pivot = new Point3();

// Constructors

	/**
	 * New locator with its pivot at (0,0,0), and size (1,1,1) around the
	 * pivot.
	 */
	public
	Locator()
	{
		this( 0, 0, 0, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f );
	}

	/**
	 * New locator with its pivot at <code>(x,y,z)</code>, and size (1,1,1)
	 * around the pivot.
	 */
	public
	Locator( float x, float y, float z )
	{
		this( x, y, z, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f );
	}

	/**
	 * New locator with its pivot at <code>(x,y,z)</code>, and size
	 * <code>(width,height,depht)</code> around the pivot.
	 */
	public
	Locator( float x, float y, float z, float width, float height, float depth )
	{
		this( x, y, z, -width/2, -height/2, -depth/2, width/2, height/2, depth/2 );
	}

	/**
	 * New locator with its pivot at <code>(x,y,z)</code> and lowest point at
	 * <code>(low_x,low_y,low_z)</code> and highest point at
	 * <code>(hi_x,hi_y,hi_z)</code>.
	 */
	public
	Locator( float x, float y, float z, float low_x, float low_y, float low_z,
		float hi_x, float hi_y, float hi_z )
	{
		super( low_x, low_y, low_z, hi_x, hi_y, hi_z );
		pivot.set( x, y, z );
	}

// Accessors

	/**
	 * Pivot point.
	 */
	public Point3
	getPivot()
	{
		return pivot;
	}

	/**
	 * Abscissa of the pivot.
	 */
	public float
	getX()
	{
		return pivot.x;
	}

	/**
	 * Ordinate of the pivot.
	 */
	public float
	getY()
	{
		return pivot.y;
	}

	/**
	 * Depth of the pivot.
	 */
	public float
	getZ()
	{
		return pivot.z;
	}

	@Override
	public String
	toString()
	{
		return "[pivot("+pivot.x+","+pivot.y+","+pivot.z+") lo("+lo.x+","+lo.y+","+lo.z+") hi("+hi.x+","+hi.y+","+hi.z+")]";
	}
}