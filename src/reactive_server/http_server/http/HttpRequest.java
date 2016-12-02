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

import java.nio.charset.Charset;
import java.util.HashMap;

import reactive_server.http_server.HTTPReactiveHandler;
import reactive_server.reactive_server_base.writer.Writable;

public class HttpRequest
{
	public static enum RequestType
	{
		GET,
		POST,
		PUT,
		INVALID;
		
		public static RequestType fromString(String s)
		{
			RequestType t = fromString.get(s);
			if(t == null)
				return INVALID;
			return t;
		}
		private final static HashMap<String, RequestType> fromString;
		static
		{
			fromString = new HashMap<String, RequestType>();
			fromString.put("GET", GET);
			fromString.put("POST", POST);
			fromString.put("PUT", PUT);
		}
	}
	
	private byte[] data = null;
	public void setData(byte[] data){this.data = data;}
	public byte[] getData() {return data;}
	private int contentLength=0;
	public int getContentLength(){return contentLength;}
	
	private HttpHeaders headers;
	public HttpHeaders getHeaders(){return headers;}
	
	private RequestType method = RequestType.INVALID;
	public RequestType getMethod(){return method;}
	
	private String path;
	public String getPath(){return path;}
	
	private HttpUrlParams params;
	public HttpUrlParams getParams(){return params;}
	
	private HTTPReactiveHandler handler;
	public void setHandler(HTTPReactiveHandler w){handler = w;}
	public HTTPReactiveHandler getHandler(){return handler;}
	public void disconnect(){handler.disconnect();}
		
	public HttpRequest(byte[] headerBytes)
	{
		do
		{
			String s = new String(headerBytes, UTF8);
			String[] lines = s.split("\r\n");
			if(lines.length < 3)
				break;
			
			String[] requestLineParts = lines[0].split(" ");
			if(requestLineParts.length != 3)
				break;
			
			method = RequestType.fromString(requestLineParts[0]);
			String pathPart = requestLineParts[1];
			String[] pathPartParts = pathPart.split("\\?");
			
			if(pathPartParts.length > 2)
				break;
			path = pathPartParts[0];
			params = new HttpUrlParams((pathPartParts.length > 1)?pathPartParts[1]:"");
			
			headers = new HttpHeaders();
			for(int i=1; i<lines.length; i++)
			{
				String line = lines[i].trim();
				int nameEnd = line.indexOf(':');
				if(nameEnd < 0)
					continue;
				String headerName = line.substring(0,nameEnd).trim();
				String headerValue = line.substring(nameEnd+1,line.length()).trim();
				headers.putHeader(headerName, headerValue);				
				if(headerName.equals("Content-Length"))
				{
					try{contentLength=Integer.parseInt(headerValue);}
					catch (NumberFormatException e){contentLength=0;}
				}
			}
			return;
		}
		while(false);
		method = RequestType.INVALID;
	}
	
	public void sendResponse(HttpResponse resp)
	{
		handler.enqueueWrite(new Writable(resp.wrap()));
	}
	private static final Charset UTF8 = Charset.forName("UTF-8");
}
