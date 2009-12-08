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

import java.util.HashMap;

public class SinkTime
{
	protected HashMap<String,SourceTime> times = new HashMap<String,SourceTime>();

	public static class SourceTime
	{
		public String sourceId;
		public long time;
		
		public SourceTime( String id )
		{
			sourceId = id;
			time     = 0;
		}
		
		public String getSourceId()
		{
			return sourceId;
		}
		
		public long getTime()
		{
			return time;
		}
		
		public void setTime( long t )
		{
			time = t;
		}
	}
	
	public boolean setTimeFor( String id, long ti )
	{
		SourceTime st = times.get( id );
		
		if( st == null )
		{
			st = new SourceTime( id );
			st.setTime( ti );
			times.put( id, st );
			return true;
		}
		else if( ti > st.time )
		{
			st.setTime( ti );
			return true;
		}
		
		return false;
	}
	
	public boolean isNewEvent( String sourceId )
	{
		int pos = sourceId.indexOf( ':' );
		
		if( pos < 0 )
			throw new RuntimeException(  "!!!" );
		
		String id = sourceId.substring( 0, pos );
		long   ti = Long.parseLong( sourceId.substring( pos+1 ) );
		
		return setTimeFor( id, ti );
	}
}