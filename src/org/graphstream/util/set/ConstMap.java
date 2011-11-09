/*
 * Copyright 2006 - 2011 
 *     Stefan Balev 	<stefan.balev@graphstream-project.org>
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
package org.graphstream.util.set;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Utility map class that wraps a real map but disallow any modification.
 *
 * <p>
 * A constant map wraps a real map (like a hash map for example) and allows to
 * browse or iterate it, but not to modify it (elements cannot be added or
 * removed).
 * </p>
 *
 * @author Antoine Dutot
 * @author Yoann Pigné
 * @since 20070126
 * @param <K> The key type.
 * @param <T> The value type.
 */
public class ConstMap<K,T> implements Map<K,T>
{
// Attributes
	
	/**
	 * The wrapped map.
	 */
	protected Map<K,T> realMap;
	
// Constructors
	
	/**
	 * New constant map wrapping the given real map.
	 * @param map The map to wrap.
	 */
	public ConstMap( Map<K,T> map )
	{
		realMap = map;
	}
	
// Methods
	
	public void clear()
	{
		throw new RuntimeException( "cannot modifiy a ConstMap!" );
	}

	public boolean containsKey( Object arg0 )
	{
		return realMap.containsKey( arg0 );
	}

	public boolean containsValue( Object arg0 )
	{
		return realMap.containsValue( arg0 );
	}

	public Set<java.util.Map.Entry<K, T>> entrySet()
	{
		return realMap.entrySet();
	}

	public T get( Object arg0 )
	{
		return realMap.get( arg0 );
	}

	public boolean isEmpty()
	{
		return realMap.isEmpty();
	}

	public Set<K> keySet()
	{
		return realMap.keySet();
	}

	public T put( K arg0, T arg1 )
	{
		throw new RuntimeException( "cannot modifiy a ConstMap!" );
	}

	public void putAll( Map<? extends K, ? extends T> arg0 )
	{
		throw new RuntimeException( "cannot modifiy a ConstMap!" );
	}

	public T remove( Object arg0 )
	{
		throw new RuntimeException( "cannot modifiy a ConstMap!" );
	}

	public int size()
	{
		return realMap.size();
	}

	public Collection<T> values()
	{
		return realMap.values();
	}
}