/****************************************************************************
**                                                                         **
** Copyright (C) 2016 Smoliy Artem                                         **
** Contact: strelok369@yandex.ru                                           **
**                                                                         **
** This file is part of ReactiveServer library.                            **
**                                                                         **
** ReactiveServer is free software: you can redistribute it and/or modify  **
** it under the terms of the GNU General Public License as published by    **
** the Free Software Foundation, either version 3 of the License, or       **
** (at your option) any later version.                                     **
**                                                                         **
** ReactiveServer is distributed in the hope that it will be useful,       **
** but WITHOUT ANY WARRANTY; without even the implied warranty of          **
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the            **
** GNU General Public License for more details.                            **
**                                                                         **
** You should have received a copy of the GNU General Public License       **
** along with ReactiveServer. If not, see <http://www.gnu.org/licenses/>.  **
**                                                                         **
*****************************************************************************/

package reactive_server.http_server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Utils
{
	private Utils(){}
	
	public static <K,V> HashMap<V, K> reverseMap(Map<K, V> map)
	{
		HashMap<V,K> result = new HashMap<V, K>();
		Iterator<Entry<K, V>> it = map.entrySet().iterator();
		while(it.hasNext())
		{
			Entry<K, V> entry = it.next();
			if(!result.containsKey(entry.getValue()))
				result.put(entry.getValue(), entry.getKey());
		}
		return result;		
	}
	public static <K,V> V getFromMap(Map<K,V> map, K key, V defaultValue)
	{
		V result = map.get(key);
		if(result != null)
			return result;
		return defaultValue;
	}
	
	private static Class<?> mainClass;
	public static void setMainClass(Class<?> main){mainClass = main;}
	public static byte[] loadResource(String path) throws IOException
	{
		InputStream res = mainClass.getResourceAsStream(path);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while(res.available() > 0)
			bos.write(res.read());
		return bos.toByteArray();
	}
}
