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

import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import reactive_server.http_server.content.ContentProvider;
import reactive_server.http_server.http.HTTPStatus;
import reactive_server.http_server.http.HttpProcessor;
import reactive_server.http_server.http.HttpRequest;
import reactive_server.http_server.http.HttpResponse;
import reactive_server.reactive_server_base.ReactiveHandler;
import reactive_server.reactive_server_base.ReactiveServer;
import reactive_server.reactive_server_base.writer.Writable;

public class HTTPReactiveServer extends ReactiveServer
{
	private ExecutorService processPool;
	private ContentProvider contentProvider;
	public void setContentProvider(ContentProvider provider){contentProvider = provider;}
	public ContentProvider getContentProvider(){return contentProvider;}
	
	public HTTPReactiveServer(String localAddress, int port, int poolSize)
	{
		super(localAddress, port);
		processPool = Executors.newFixedThreadPool(poolSize);
	}

	@Override
	protected ReactiveHandler getReactiveHandler(SocketChannel channel)
	{
		return new HTTPReactiveHandler(channel, this);
	}
	
	private class Processor implements Runnable
	{
		private HttpRequest req;
		public Processor(HttpRequest req){this.req = req;}
		@Override
		public void run()
		{
			HttpProcessor proc = contentProvider.getProcessor(req.getPath());
			if(proc==null)
				proc = contentProvider.getProcForStatus(HTTPStatus.ST404);
						
			HttpResponse response = null;
			switch (req.getMethod())
			{
			case GET: response = proc.onGet(req); break;
			case POST: response = proc.onPost(req); break;
			case PUT: response = proc.onPut(req); break;
			default: response = HttpResponse.buildTextStatus(HTTPStatus.ST500);
			}
			if(response == null)
				response = HttpResponse.buildTextStatus(HTTPStatus.ST500);
			req.getHandler().enqueueWrite(new Writable(response.wrap()));
		}		
	}	
	public void processHttpRequest(HttpRequest request){processPool.execute(new Processor(request));}
	public void execInPool(Runnable r){processPool.execute(r);}
}
