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

package org.graphstream.io;

import org.graphstream.graph.GraphListener;

/**
 * Sink of graph events.
 * 
 * <p>An output is something that can receive graph events. The output will send or transform
 * the graph events in another form: a file, a network stream, a visualisation, an algorithm,
 * a metric, etc.</p>
 * 
 * <p>The output can filter the stream of attribute events using {@link AttributePredicate}s.</p>
 * 
 * @see Source
 * @see Pipe
 */
public interface Sink extends GraphListener
{
}