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
import java.util.Map;

import reactive_server.http_server.Utils;

public enum ContentType
{
	CT_APP_JS, CT_APP_JSON,
	
	CT_TXT_PLAIN, CT_TXT_HTML, CT_TXT_CSS,
	
	CT_PIC_PNG, CT_PIC_JPG, CT_PIC_SVG;
	
	public enum Category{TXT, PIC, APP, UNSUPPORTED}
	
	public String getMIME()
	{
		return Utils.getFromMap(CTtoMIME, this, "text/plain");
	}
	
	public Category getCategory()
	{
		switch (this)
		{
		case CT_APP_JS: case CT_APP_JSON:
			return Category.APP;
		case CT_PIC_JPG: case CT_PIC_PNG: case CT_PIC_SVG:
			return Category.PIC;
		case CT_TXT_CSS: case CT_TXT_HTML: case CT_TXT_PLAIN:
			return Category.TXT;
		default:
			return Category.UNSUPPORTED;
		}
	}
	
	public String getExtension()
	{
		return Utils.getFromMap(CTtoEXT, this, "txt");
	}
	
	public static ContentType fromExtension(String ext)
	{
		return Utils.getFromMap(ExtToCT, ext, CT_TXT_PLAIN);
	}
	
	private static final Map<ContentType, String> CTtoMIME;
	private static final Map<String, ContentType> ExtToCT;
	private static final Map<ContentType, String> CTtoEXT;
	
	static
	{
		CTtoMIME = new HashMap<ContentType, String>();
		CTtoMIME.put(CT_APP_JS,   "application/javascript");
		CTtoMIME.put(CT_APP_JSON, "application/json");
		
		CTtoMIME.put(CT_TXT_PLAIN, "text/plain");
		CTtoMIME.put(CT_TXT_CSS,   "text/css");
		CTtoMIME.put(CT_TXT_HTML,  "text/html");
		
		CTtoMIME.put(CT_PIC_PNG, "image/png");
		CTtoMIME.put(CT_PIC_JPG, "image/jpeg");
		CTtoMIME.put(CT_PIC_SVG, "image/svg+xml");
		
		ExtToCT = new HashMap<String, ContentType>();
		ExtToCT.put("js",   CT_APP_JS);
		ExtToCT.put("json", CT_APP_JSON);

		ExtToCT.put("txt",  CT_TXT_PLAIN);
		ExtToCT.put("css",  CT_TXT_CSS);
		ExtToCT.put("html", CT_TXT_HTML);
		
		ExtToCT.put("png",  CT_PIC_PNG);
		ExtToCT.put("jpg",  CT_PIC_JPG);
		ExtToCT.put("jpeg", CT_PIC_JPG);
		ExtToCT.put("svg",  CT_PIC_SVG);
		
		CTtoEXT = Utils.reverseMap(ExtToCT);
	}
}
