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

import reactive_server.http_server.HTTPReactiveHandler;
import reactive_server.http_server.websocket.WebSocketMessage.Opcode;

public abstract class WebSocketProcessor
{
	private HTTPReactiveHandler handler;
	public void setHandler(HTTPReactiveHandler handler){this.handler = handler;}
	public HTTPReactiveHandler getHandler(){return handler;}
	
	private class Processor implements Runnable
	{
		WebSocketMessage msg;
		public Processor(WebSocketMessage msg){this.msg = msg;}
		@Override
		public void run()
		{
			switch (msg.getCode())
			{
			case CLOSE: onClose(msg); break;
			case PING: processPing(msg); break;
			default: onMessage(msg); break;
			}
		}		
	}	
	public void processMessage(WebSocketMessage msg)
	{
		handler.getServer().execInPool(new Processor(msg));
	}
	
	public abstract void onMessage(WebSocketMessage msg);
	public abstract void onClose(WebSocketMessage msg);
	public abstract void onDisconnect();
	
	public void send(WebSocketMessage msg)
	{
		handler.enqueueWrite(msg.wrap());
	}
	public void ping(String ping)
	{
		WebSocketMessage msg = new WebSocketMessage(ping);
		msg.setCode(Opcode.PING);
		send(msg);
	}
	public void close(String cause)
	{
		WebSocketMessage msg = new WebSocketMessage(cause);
		msg.setCode(Opcode.CLOSE);
		send(msg);
	}
	private void processPing(WebSocketMessage msg)
	{
		msg.setCode(Opcode.PONG);
		send(msg);
	}
}
