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

package reactive_server.reactive_server_base;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import reactive_server.reactive_server_base.reactor.Reactor;

public abstract class ReactiveServer extends Reactor
{
	private ServerSocketChannel serverChannel;
	private SelectionKey key;
	private String localAddress;
	private int port;
	
	public ReactiveServer(String localAddress, int port)
	{
		this.localAddress = localAddress;
		this.port = port;
	}
	
	protected abstract ReactiveHandler getReactiveHandler(SocketChannel channel);
	
	private class Acceptor implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				if(!key.isValid())
					return;
				else if(!key.isAcceptable())
					return;
				
				SocketChannel channel = serverChannel.accept();
				//System.out.println("Accept: "+channel.getRemoteAddress().toString());
				if(channel != null)
					enqueueSetupable(getReactiveHandler(channel));
			}
			catch(IOException e){e.printStackTrace();}
		}
	}
	
	public void start() throws IOException
	{
		serverChannel = ServerSocketChannel.open();
		serverChannel.bind(new InetSocketAddress(localAddress, port));
		serverChannel.configureBlocking(false);
		
		Selector accepSelector = Selector.open();
		setSelector(accepSelector);
		key = serverChannel.register(accepSelector, SelectionKey.OP_ACCEPT);
		key.attach(new Acceptor());
		super.start();
	}
}
