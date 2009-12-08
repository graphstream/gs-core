/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.io.sync;

public class SourceTime
{
	protected long time = 0;
	
	protected String id;
	
	protected SinkTime myTime;
	
	public SourceTime( String id )
	{
		this.id = id;
	}
	
	public SourceTime( String id, SinkTime myTime )
	{
		this.id     = id;
		this.myTime = myTime;
	}
	
	public String newEvent()
	{
		time++;
		
		if( myTime != null )
			myTime.setTimeFor( id, time );
		
		return String.format( "%s:%d", id, time );
	}
}
