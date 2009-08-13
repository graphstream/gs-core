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

package org.miv.graphstream.io2;

import org.miv.graphstream.graph.GraphListener;

/**
 * Sink and source of graph events.
 * 
 * <p>A filter is something that can receive graph events and produce graph events as a result
 * or transformation.</p>
 * 
 * <p>Some filters are paired with another filter from synchronisation. Synchronisation happens
 * when an input X pass through a filter F1 that goes to an output Y and when Y serves as
 * input for a filter F2 toward the output X. Clearly there is a synchronisation loop, Y listens
 * at X through F1 and X listens at Y through F2. Such a configuration is dangerous since this
 * can lead to a endless loop of events. To avoid this there exist two mechanisms : the first
 * one is that inputs generate events only if modified for a change. If they are edited but
 * nothing changed (an attribute was set to its same old value) no event is generated. In most
 * cases this avoids synchronisation loops. The other mechanism is to explicitly pair filters
 * that serve to synchronise inputs and outputs. This is done via the unique new method
 * of this filter : {@link #synchronizeWith( GraphListener, Input )}</p>
 * 
 * @see Input
 * @see Output
 */
public interface Filter extends Input, Output
{
}