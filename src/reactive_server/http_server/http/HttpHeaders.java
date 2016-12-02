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

package reactive_server.http_server.http;

import java.util.ArrayList;
import java.util.HashMap;

public class HttpHeaders
{
	private HashMap<String, ArrayList<String>> headers = new HashMap<String, ArrayList<String>>();
	public HashMap<String, ArrayList<String>> getAllHeaders() {return headers;}
	public ArrayList<String> getHeaders(String name){return headers.get(name);}
	public String getFirstHeader(String name)
	{
		ArrayList<String> h = headers.get(name);
		if(h==null)
			return null;
		if(h.isEmpty())
			return null;
		return h.get(0);
	}
	
	public void putHeader(String name, String header)
	{
		ArrayList<String> h = headers.get(name);
		if(h == null)
		{
			h = new ArrayList<String>();
			headers.put(name, h);
		}
		h.add(header);
	}
}
