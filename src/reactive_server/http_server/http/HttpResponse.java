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

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;

public class HttpResponse
{
	private HTTPStatus status;
	private ContentType type;
	private byte[] data;	
	private HttpHeaders headers = new HttpHeaders();
	
	public HttpResponse(HTTPStatus status, byte[] data, ContentType type)
	{
		this.status = status;
		this.data = data;
		this.type = type;
	}
	
	public byte[] getData()
	{
		return data;
	}
	
	public void putHeader(String name, String value)
	{
		headers.putHeader(name, value);
	}
	
	public byte[] buildHeaderBytes()
	{		
		StringBuilder result = new StringBuilder();
		result.append("HTTP/1.1 ").append(status.getStr()).append(' ').append(status.getText()).append('\r').append('\n')
		      .append("Server: ReactiveServer\r\n")
		      .append("Date: ").append(getDate()).append('\r').append('\n');
		
		if(data != null)
		{
			if(data.length > 0)
			{
				result.append("Content-Type: ").append(type.getMIME());
				if(type == ContentType.CT_TXT_PLAIN)
					result.append("; charset=utf-8");
				result.append('\r').append('\n');
				result.append("Content-Length: ").append(data.length).append('\r').append('\n');
			}
		}
		
		Iterator<Entry<String, ArrayList<String>>> headIt = headers.getAllHeaders().entrySet().iterator();
		while(headIt.hasNext())
		{
			Entry<String, ArrayList<String>> e = headIt.next();
			if(e.getValue()==null)
				continue;
			Iterator<String> it = e.getValue().iterator();
			while(it.hasNext())
				result.append(e.getKey()).append(':').append(' ').append(it.next()).append('\r').append('\n');
		}
		result.append('\r').append('\n');
		return result.toString().getBytes(ISO8859_1);
	}
	private static final Charset ISO8859_1 = Charset.forName("ISO8859-1");
	
	public ByteBuffer wrap()
	{
		byte[] headerBytes = buildHeaderBytes();
		int len = headerBytes.length;
		if(data != null)
			len += data.length;
		ByteBuffer result = ByteBuffer.allocate(len);
		result.put(headerBytes);
		if(data != null)
			result.put(data);
		result.flip();
		return result;
	}
	
	public static String getDate()
	{
		return dateFormat.format(new Date());
	}
	
	private static final SimpleDateFormat dateFormat;
	static
	{
		dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
	}
	
	public static HttpResponse buildTextStatus(HTTPStatus status)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(status.getStr()).append('\n').append(status.getText()); 
		return new HttpResponse(status, sb.toString().getBytes(UTF8), ContentType.CT_TXT_PLAIN);
	}
	private static final Charset UTF8 = Charset.forName("UTF-8");
}
