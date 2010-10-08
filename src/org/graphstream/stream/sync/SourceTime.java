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

public class SourceTime {
	protected String sourceId;
	/**
	 * Current value of the time for this source.
	 */
	protected long currentTimeId;
	/**
	 * 
	 */
	protected SinkTime sinkTime;

	/**
	 * Create a new SourceTime for a given id. Current time id is set to 0.
	 */
	public SourceTime() {
		this(0);
	}

	/**
	 * Create a new SourceTime for a given id and a given time.
	 * 
	 * @param currentTimeId
	 */
	public SourceTime(long currentTimeId) {
		this(null, currentTimeId, null);
	}

	public SourceTime(String sourceId) {
		this(sourceId, 0, null);
	}

	public SourceTime(String sourceId, SinkTime sinkTime) {
		this(sourceId, 0, sinkTime);
	}

	/**
	 * Create a new SourceTime for a given id and a given time.
	 * 
	 * @param currentTimeId
	 */
	public SourceTime(String sourceId, long currentTimeId) {
		this(sourceId, currentTimeId, null);
	}

	public SourceTime(String sourceId, long currentTimeId, SinkTime sinkTime) {
		this.sourceId = sourceId;
		this.currentTimeId = currentTimeId;
		this.sinkTime = sinkTime;
	}

	public SinkTime getSinkTime() {
		return sinkTime;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public void setSinkTime(SinkTime st) {
		this.sinkTime = st;
	}

	public long newEvent() {
		currentTimeId++;

		if (sinkTime != null)
			sinkTime.setTimeFor(sourceId, currentTimeId);

		return currentTimeId;
	}
}
