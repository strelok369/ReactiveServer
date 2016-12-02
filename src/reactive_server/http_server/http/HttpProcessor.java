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

public abstract class HttpProcessor
{	
	public abstract HttpResponse onGet(HttpRequest request);	
	public abstract HttpResponse onPost(HttpRequest request);	
	public abstract HttpResponse onPut(HttpRequest request);
	
	public HttpResponse unsupportedMethod(HttpRequest req)		
	{
		return constructStatus(HTTPStatus.ST405, req);
	}
	
	public HttpResponse constructStatus(HTTPStatus status, HttpRequest req)
	{
		HttpProcessor st = req.getHandler().getServer().getContentProvider().getProcForStatus(status);		
		HttpResponse resp = null;
		switch(req.getMethod())
		{
		case GET:  resp = st.onGet(req);  break;
		case POST: resp = st.onPost(req); break;
		case PUT:  resp = st.onPut(req);  break;
		default: HttpResponse.buildTextStatus(HTTPStatus.ST500);break;
		}
		if(resp == null)
			resp = HttpResponse.buildTextStatus(HTTPStatus.ST500);
		return resp;
	}
}
