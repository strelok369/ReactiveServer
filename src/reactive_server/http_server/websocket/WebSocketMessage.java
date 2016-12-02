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

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.HashMap;

import reactive_server.http_server.Utils;

public class WebSocketMessage
{
	public static enum Opcode
	{
		TEXT(0x1),
		BINARY(0x2),
		CLOSE(0x8),
		PING(0x9),
		PONG(0xA),
		CONTINUATION(0x0),
		INVALID(-1);
		
		private byte code;
		public byte getCode(){return code;}
		private Opcode(int code)
		{
			this.code = (byte)code;
		}		
		
		public static Opcode fromCode(int code){return Utils.getFromMap(codeToOpcode, (byte)code, INVALID);}
		private final static HashMap<Byte, Opcode> codeToOpcode;
		static
		{
			codeToOpcode = new HashMap<Byte, Opcode>();
			for(Opcode o : Opcode.values())
				codeToOpcode.put(o.getCode(), o);
		}
	}
	private static Charset UTF8 = Charset.forName("UTF-8");
	
	public WebSocketMessage(Opcode code, byte[] data)
	{
		this.code = code;
		this.data = data;
	}
	public WebSocketMessage(String data)
	{
		code = Opcode.TEXT;
		this.data = data.getBytes(UTF8);
	}
	
	private Opcode code;
	public void setCode(Opcode code){this.code = code; dataOutDirty = true;}
	public Opcode getCode(){return code;}
	
	private byte[] data;
	public byte[] getData(){return data;}
	
	private byte[] dataOut = null;
	private boolean dataOutDirty = true;
	public void buildData()
	{
		if(!dataOutDirty)
			return;
		ByteArrayOutputStream dataOutStream = new ByteArrayOutputStream();
		byte codeByte = (byte)((code.getCode() & 0x0F) | 0x80);
		dataOutStream.write(codeByte);
		
		long len = data.length;
		if(len < 126)
			dataOutStream.write((byte)len);
		else
		{			
			ByteBuffer lenBuffer = ByteBuffer.allocate(8);
			lenBuffer.order(ByteOrder.BIG_ENDIAN);
			lenBuffer.putLong(len);
			lenBuffer.flip();
			
			if(len < 65536)
			{
				lenBuffer.position(6);
				dataOutStream.write(126);
			}
			else
			{
				lenBuffer.position(0);
				dataOutStream.write(127);
			}
			while(lenBuffer.hasRemaining())
				dataOutStream.write(lenBuffer.get());
		}
		dataOutStream.write(data,0,data.length);
		dataOut = dataOutStream.toByteArray();
		dataOutDirty = false;
	}
	
	private String text = null;	
	public String getText()
	{
		text = new String(data, UTF8);
		return text;
	}
	
	public ByteBuffer wrap()
	{
		buildData();
		return ByteBuffer.wrap(dataOut);
	}
}
