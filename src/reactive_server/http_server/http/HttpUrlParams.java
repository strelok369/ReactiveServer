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

import java.util.HashMap;

public class HttpUrlParams
{
	private HashMap<String, String> params = new HashMap<String, String>();
	public HashMap<String, String> getAllParams(){return params;}
	public String getParam(String name){return params.get(name);}
	
	public HttpUrlParams(String p)
	{
		String[] parts = p.split("&");
		for(String s : parts)
		{
			int firstEq = s.indexOf('=');
			if(firstEq<0)
				continue;
			String paramName = s.substring(0, firstEq);
			String paramVal = s.substring(firstEq+1, s.length());
			params.put(paramName, paramVal);
		}
	}
}
