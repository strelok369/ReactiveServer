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

import reactive_server.http_server.http.ContentType;
import reactive_server.http_server.http.HTTPStatus;
import reactive_server.http_server.http.HttpProcessor;
import reactive_server.http_server.http.HttpRequest;
import reactive_server.http_server.http.HttpResponse;

public class DataContentProcessor extends HttpProcessor
{
	private byte[] data;
	private ContentType type;
	public DataContentProcessor(byte[] data, ContentType type)
	{
		this.data = data;
		this.type = type;
	}
	
	@Override
	public HttpResponse onGet(HttpRequest reqest)
	{
		return new HttpResponse(HTTPStatus.ST200, data, type);
	}

	@Override
	public HttpResponse onPost(HttpRequest request)
	{
		HttpProcessor stProc = request.getHandler().getServer().getContentProvider().getProcForStatus(HTTPStatus.ST405);
		return stProc.onPost(request);
	}

	@Override
	public HttpResponse onPut(HttpRequest request)
	{
		HttpProcessor stProc = request.getHandler().getServer().getContentProvider().getProcForStatus(HTTPStatus.ST405);
		return stProc.onPost(request);
	}
}
