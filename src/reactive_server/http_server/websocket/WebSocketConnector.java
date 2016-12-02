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

package reactive_server.http_server.websocket;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

import reactive_server.http_server.http.ContentType;
import reactive_server.http_server.http.HTTPStatus;
import reactive_server.http_server.http.HttpProcessor;
import reactive_server.http_server.http.HttpRequest;
import reactive_server.http_server.http.HttpResponse;

public abstract class WebSocketConnector extends HttpProcessor
{
	private final static Charset UTF8 = Charset.forName("UTF-8");
	@Override
	public HttpResponse onGet(HttpRequest request)
	{
		do
		{
			String websocketVer = request.getHeaders().getFirstHeader("Sec-WebSocket-Version");
			if(websocketVer == null)
				break;
			int ver = 0;
			try{ver = Integer.parseInt(websocketVer.trim());}catch(NumberFormatException e){}
			if(ver == 0)
				break;
			String key = request.getHeaders().getFirstHeader("Sec-WebSocket-Key");
			if(key == null)
				break;
			String respStr = null;
			try
			{
				MessageDigest digest = MessageDigest.getInstance("SHA1");
				byte[] respBytes = digest.digest((key.trim()+"258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes(UTF8));
				respStr = DatatypeConverter.printBase64Binary(respBytes);
			}
			catch (NoSuchAlgorithmException e){break;}
			if(respStr == null)
				break;
			
			HttpResponse resp = null;
			resp = new HttpResponse(HTTPStatus.ST101, null, ContentType.CT_TXT_PLAIN);
			resp.putHeader("Connection", "Upgrade");
			resp.putHeader("Upgrade", "websocket");
			resp.putHeader("Sec-WebSocket-Accept", respStr);
			resp.putHeader("Sec-WebSocket-Version", "13");
			request.getHandler().switchToWebsocket(getWebSocketProcessor());
			return resp;
		}
		while(false);
		return constructStatus(HTTPStatus.ST400, request);
	}
	
	public abstract WebSocketProcessor getWebSocketProcessor();

	@Override
	public HttpResponse onPost(HttpRequest request){return unsupportedMethod(request);}
	@Override
	public HttpResponse onPut(HttpRequest request){return unsupportedMethod(request);}
}
