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
 * Copyright 2006 - 2010
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */
package org.graphstream.stream.sync;

import java.util.HashMap;

public class SinkTime {
	/**
	 * Key used to disable synchro. Just run : java -DSYNC_DISABLE_KEY ...
	 */
	public static final String SYNC_DISABLE_KEY = "org.graphstream.stream.sync.disable";
	/**
	 * Flag used to disable sync.
	 */
	protected static final boolean disableSync = System
			.getProperty(SYNC_DISABLE_KEY) != null;

	/**
	 * Map storing times of sources.
	 */
	protected HashMap<String, Long> times = new HashMap<String, Long>();

	/**
	 * Update timeId for a source.
	 * 
	 * @param sourceId
	 * @param timeId
	 * @return true if time has been updated
	 */
	protected boolean setTimeFor(String sourceId, long timeId) {
		Long knownTimeId = times.get(sourceId);

		if (knownTimeId == null) {
			times.put(sourceId, timeId);
			return true;
		} else if (timeId > knownTimeId) {
			times.put(sourceId, timeId);
			return true;
		}

		return false;
	}

	/**
	 * Allow to know if event is new for this source. This updates the timeId
	 * mapped to the source.
	 * 
	 * @param sourceId
	 * @param timeId
	 * @return true if event is new for the source
	 */
	public boolean isNewEvent(String sourceId, long timeId) {
		return disableSync || setTimeFor(sourceId, timeId);
	}
}