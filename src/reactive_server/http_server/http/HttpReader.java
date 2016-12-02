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

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import reactive_server.http_server.HTTPReactiveHandler;
import reactive_server.http_server.HTTPReactiveReader;
import reactive_server.reactive_server_base.writer.Writable;

public class HttpReader extends HTTPReactiveReader
{
	public HttpReader(HTTPReactiveHandler handler){super(handler);}
	
	private long contentLength = 0;
	private static final int MAX_HEADER_SIZE = 4096;
	
	private ByteArrayOutputStream headerStream = new ByteArrayOutputStream();
	private ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
	
	private HttpRequest request = null;
	private int rnrnState = 0;
	private boolean readHeader = true;
	private boolean abort = false;
	
	@Override
	public void read(ByteBuffer read)
	{
		if(abort)
			return;
		while(read.hasRemaining())
		{
			byte b = read.get();
			if(readHeader)
			{
				headerStream.write(b);
				if((b=='\r' && (rnrnState==0 || rnrnState==2)) || (b=='\n' && (rnrnState==1 || rnrnState == 3)))
					rnrnState++;
				else
					rnrnState=0;
				
				if(rnrnState == 4)
				{	
					readHeader = false;
					rnrnState = 0;
					request = new HttpRequest(headerStream.toByteArray());
					request.setHandler(getHandler());
					headerStream.reset();
					if(request.getMethod() == HttpRequest.RequestType.INVALID)
					{
						HttpResponse resp = HttpResponse.buildTextStatus(HTTPStatus.ST400);
						getHandler().enqueueWrite(new Writable(resp.wrap()));
						abort = true;
						return;
					}
					contentLength = request.getContentLength();					
					if(contentLength==0)
					{						
						abort = true;
						getHandler().getServer().processHttpRequest(request);
						return;
					}
				}
				if(headerStream.size() > MAX_HEADER_SIZE || contentLength < 0)
					getHandler().disconnect();
			}
			else
			{
				dataStream.write(b);
				if(dataStream.size() >= contentLength)
				{
					request.setData(dataStream.toByteArray());
					dataStream.reset();
					abort = true;
					if(request != null)
						getHandler().getServer().processHttpRequest(request);					
					return;
				}
			}
		}
	}
	@Override
	public void onDisconnect(){}
}
