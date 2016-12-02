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

import reactive_server.http_server.http.HTTPStatus;
import reactive_server.http_server.http.HttpProcessor;
import reactive_server.http_server.http.HttpRequest;
import reactive_server.http_server.http.HttpResponse;

public class DefaultStatusFactory implements StatusFactory
{
	public static class DefaultStatusProcessor extends HttpProcessor
	{
		private HTTPStatus status;
		public DefaultStatusProcessor(HTTPStatus status){this.status = status;}
		
		@Override
		public HttpResponse onGet(HttpRequest reqest){return HttpResponse.buildTextStatus(status);}
		@Override
		public HttpResponse onPost(HttpRequest request){return HttpResponse.buildTextStatus(status);}
		@Override
		public HttpResponse onPut(HttpRequest request){return HttpResponse.buildTextStatus(status);}
	}
	
	public HttpProcessor constructProcessor(HTTPStatus status)
	{
		return new DefaultStatusProcessor(status);
	}
}
