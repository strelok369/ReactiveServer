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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import reactive_server.http_server.Utils;

public enum HTTPStatus
{
	ST100(100, "Continue"),
	ST101(101, "Switching Protocols"),
	
	ST200(200, "OK"),
	ST201(201, "Created"),
	ST202(202, "Accepted"),
	ST203(203, "Non-Authoritative Information"),
	ST204(204, "No Content"),
	ST205(205, "Reset Content"),
	ST206(206, "Partial Content"),
	
	ST300(300, "Multiple Choices"),
	ST301(301, "Moved Permanently"),
	ST302(302, "Found"),
	ST303(303, "See Other"),
	ST304(304, "Not Modified"),
	ST305(305, "Use Proxy"),
	ST307(307, "Temporary Redirect"),
	
	ST400(400, "Bad Request"),
	ST401(401, "Unauthorized"),
	ST402(402, "Payment Required"),
	ST403(403, "Forbidden"),
	ST404(404, "Not Found"),
	ST405(405, "Method Not Allowed"),
	ST406(406, "Not Acceptable"),
	ST407(407, "Proxy Authentication Required"),
	ST408(408, "Request Time-out"),
	ST409(409, "Conflict"),
	ST410(410, "Gone"),
	ST411(411, "Length Required"),
	ST412(412, "Precondition Failed"),
	ST413(413, "Request Entity Too Large"),
	ST414(414, "Request-URI Too Large"),
	ST415(415, "Unsupported Media Type"),
	ST416(416, "Requested range not satisfiable"),
	ST417(417, "Expectation Failed"),
	ST418(418, "I'm a teapot"),
	
	ST500(500, "Internal Server Error"),
	ST501(501, "Not Implemented"),
	ST502(502, "Bad Gateway"),
	ST503(503, "Service Unavailable"),
	ST504(504, "Gateway Time-out"),
	ST505(505, "HTTP Version not supported");
	
	private final int value;
	private final String text;
	public int getValue(){return value;}
	
	private HTTPStatus(final int value, final String text)
	{
		this.value = value;
		this.text = text;
	}
	
	public String getText()
	{
		return text;
	}
	
	public String getStr()
	{
		return Integer.toString(value);
	}
	
	public static HTTPStatus fromString(String str)
	{
		return Utils.getFromMap(StrToST, str, ST400);
	}
	
	public static HTTPStatus fromInt(int status)
	{
		return Utils.getFromMap(IntToSt, status, ST400);
	}
	
	public static boolean isStringStatus(String str)
	{
		Iterator<Entry<String, HTTPStatus>> it = StrToST.entrySet().iterator();
		while(it.hasNext())
		{
			if(str.matches(it.next().getKey()))
				return true;
		}
		return false;
	}
	
	private static final Map<String, HTTPStatus> StrToST;
	private static final Map<Integer, HTTPStatus> IntToSt;
	static
	{
		StrToST = new HashMap<String, HTTPStatus>();
		IntToSt = new HashMap<Integer, HTTPStatus>();
		for(HTTPStatus st : HTTPStatus.values())
		{
			StrToST.put(st.text, st);
			IntToSt.put(st.value, st);
		}
	}
}
