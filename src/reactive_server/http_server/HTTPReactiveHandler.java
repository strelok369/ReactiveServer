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

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import reactive_server.http_server.http.HttpReader;
import reactive_server.http_server.websocket.WebSocketProcessor;
import reactive_server.http_server.websocket.WebSocketReader;
import reactive_server.reactive_server_base.ReactiveHandler;
import reactive_server.reactive_server_base.ReactiveServer;

public class HTTPReactiveHandler extends ReactiveHandler
{
	enum Protocol
	{
		HTTP,
		WEBSOCKET
	}
	
	private HTTPReactiveReader reader = new HttpReader(this);
	
	public HTTPReactiveHandler(SocketChannel channel, ReactiveServer server){super(channel, server);}
	public HTTPReactiveServer getServer(){return (HTTPReactiveServer)super.getServer();}

	private boolean keepAlive = false;
	
	public void switchToWebsocket(WebSocketProcessor proc)
	{
		keepAlive = true;
		proc.setHandler(this);
		reader = new WebSocketReader(this, proc);
	}
	
	@Override
	protected void onRead(ByteBuffer read)
	{
		reader.read(read);
	}

	@Override
	protected void onWriteEnd()
	{
		//System.out.println("Keep: "+keepAlive);
		if(!keepAlive)
			disconnect();
		//System.out.println("Keep: ---");
	}
	@Override
	public void onDisconnect(){reader.onDisconnect();}
}
