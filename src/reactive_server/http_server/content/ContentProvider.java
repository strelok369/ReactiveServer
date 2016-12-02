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

package reactive_server.http_server.content;

import java.io.IOException;
import java.util.HashMap;

import reactive_server.http_server.Utils;
import reactive_server.http_server.http.ContentType;
import reactive_server.http_server.http.HTTPStatus;
import reactive_server.http_server.http.HttpProcessor;

public class ContentProvider
{	
	private HashMap<String, HttpProcessor> contentMap = new HashMap<String, HttpProcessor>();	
	public HttpProcessor getProcessor(String path){return contentMap.get(path);}
	public HashMap<String, HttpProcessor> getAllProcessors(){return contentMap;}
	public void addContent(String path, HttpProcessor proc){contentMap.put(path, proc);}
	public void addContent(String path, byte[] data, ContentType type){contentMap.put(path, new DataContentProcessor(data, type));}
	public void addContentFromResource(String path, String resource, ContentType type) throws IOException
	{
		byte[] data = Utils.loadResource(resource);
		addContent(path, data, type);
	}
	
	private StatusFactory statusFactory = new DefaultStatusFactory();
	public void setStatusFactory(StatusFactory factory){statusFactory = factory;}
	public HttpProcessor getProcForStatus(HTTPStatus status)
	{
		return statusFactory.constructProcessor(status);
	}
}
