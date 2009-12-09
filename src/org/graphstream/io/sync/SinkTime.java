/*
 * This file is part of GraphStream.
 * 
 * GraphStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * GraphStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with GraphStream.  If not, see <http://www.gnu.org/licenses/>.
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
	protected HashMap<String,Long> times = new HashMap<String,Long>();

	protected boolean setTimeFor( String sourceId, long timeId )
	{
		Long knownTimeId = times.get(sourceId);
		
		if( knownTimeId == null )
		{
			times.put( sourceId, timeId );
			return true;
		}
		else if( timeId > knownTimeId )
		{
			times.put(sourceId,timeId);
			return true;
		}
		
		return false;
	}
	
	public boolean isNewEvent( String sourceId, long timeId )
	{
		return setTimeFor(sourceId,timeId);
	}
}