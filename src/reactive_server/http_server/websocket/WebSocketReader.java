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

import reactive_server.http_server.HTTPReactiveHandler;
import reactive_server.http_server.HTTPReactiveReader;
import reactive_server.http_server.websocket.WebSocketMessage.Opcode;

public class WebSocketReader extends HTTPReactiveReader
{
	private WebSocketProcessor proc;
	public WebSocketReader(HTTPReactiveHandler handler, WebSocketProcessor proc)
	{
		super(handler);
		this.proc = proc;
	}
	
	private boolean readHeader = true;
	private boolean isFin = true;
	private Opcode opcode = Opcode.INVALID;
	private byte[] mask = {0,0,0,0};
	private int maskPos = 0;
	private boolean isMasked = false;	
	private long contentLength = 0;
	
	private ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
	
	@Override
	public void read(ByteBuffer read)
	{
		do
		{
			read.order(ByteOrder.BIG_ENDIAN);
			if(readHeader)
			{
				if(read.remaining() < 2)
					break;
	
				byte code = read.get();
				isFin = (code & 0x80) == 0x80;
				code &= 0x0F;
				Opcode op = Opcode.fromCode(code);
				if(op != Opcode.CONTINUATION)
					opcode = Opcode.fromCode(code);				
				if(opcode == Opcode.INVALID)
					break;
	
				byte payloadLen = read.get();
				isMasked = (payloadLen & 0x80) == 0x80;
				payloadLen &= ~0x80;
	
				ByteBuffer lenBuffer = ByteBuffer.allocate(8);
				lenBuffer.order(ByteOrder.BIG_ENDIAN);
	
				int lenToRead = 1;
				if(payloadLen == 126)
					lenToRead = 2;
				else if(payloadLen == 127)
					lenToRead = 8;
				if(read.remaining() < lenToRead)
					break;
	
				for(int i = lenToRead; i < 8; i++)
					lenBuffer.put((byte) 0);
				if(lenToRead == 1)
					lenBuffer.put(payloadLen);
				else
				{
					for(int i = 0; i < lenToRead; i++)
						lenBuffer.put(read.get());
				}
	
				lenBuffer.flip();
				contentLength = lenBuffer.getLong();
				if(contentLength < 0)
					break;
				
				if(isMasked)
				{
					if(read.remaining() < 4)
						break;
					for(int i=0; i<4; i++)
						mask[i] = read.get();
					maskPos = 0;
				}
				
				if(contentLength == 0)
				{
					process();
					if(read.hasRemaining())
						continue;
					else
						return;
				}
				else
					readHeader = false;
			}
			
			if(isMasked)
			{
				while(read.hasRemaining())
				{
					byte b = read.get();
					b ^= mask[maskPos++];
					if(maskPos == 4)
						maskPos = 0;
					dataStream.write(b);
					if(dataStream.size() >= contentLength)
					{
						process();
						readHeader = true;
						break;
					}
				}
			}
			else
			{
				while(read.hasRemaining())
				{
					dataStream.write(read.get());
					if(dataStream.size() >= contentLength)
					{
						process();
						readHeader = true;
						break;
					}
				}
			}
			if(readHeader && read.hasRemaining())
				continue;
			return;
		}
		while(true);
		getHandler().disconnect();
	}
	
	private void process()
	{
		if(isFin)
		{
			proc.processMessage(new WebSocketMessage(opcode, dataStream.toByteArray()));
			dataStream.reset();
		}
	}

	@Override
	public void onDisconnect(){proc.onDisconnect();}
}
