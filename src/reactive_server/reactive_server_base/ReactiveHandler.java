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
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;

import reactive_server.reactive_server_base.reactor.Setuppable;
import reactive_server.reactive_server_base.writer.Writable;
import reactive_server.reactive_server_base.writer.Writer;

public abstract class ReactiveHandler implements Setuppable,Runnable,Writer
{
	private SocketChannel channel;
	protected SocketChannel getChannel(){return channel;}
	
	private SelectionKey key;
	protected Selector selector;
	
	private ReactiveServer server;
	public ReactiveServer getServer(){return server;}
	
	public ReactiveHandler(SocketChannel channel, ReactiveServer server)
	{
		this.channel = channel;
		this.server = server;
	}

	@Override
	public void setup(Selector selector) throws IOException
	{
		channel.configureBlocking(false);
		this.selector = selector;
		key = channel.register(selector, SelectionKey.OP_READ);
		key.attach(this);
	}
	
	protected synchronized void addInterestOp(int op)
	{
		if(!key.isValid())
			return;
		key.interestOps(key.interestOps() | op);
	}
	protected synchronized void clearInterestOp(int op)
	{
		if(!key.isValid())
			return;
		key.interestOps(key.interestOps() & ~op);
	}
	public void disconnect()
	{
		System.out.println("disconnect"); //TODO remove
		try{channel.close();} catch (IOException e){}
		key.cancel();
		onDisconnect();
	}
	public abstract void onDisconnect();
	
	@Override
	public void run()
	{
		try
		{
			if(!key.isValid())
				return;
			else if(key.isReadable())
				read();
			else if(key.isWritable())
				write();
		}
		catch(IOException e){e.printStackTrace();disconnect();}
	}		
	
	ByteBuffer readBuffer = ByteBuffer.allocate(2048);
	private void read() throws IOException
	{
		if(channel.read(readBuffer) != -1)
		{
			readBuffer.flip();
			onRead(readBuffer);
			readBuffer.clear();
		}
		else
		{
			System.out.println("read -1"); //TODO remove
			disconnect();
		}
	}
	private void write() throws IOException
	{
		Writable w = getWritable();
		do
		{
			if(w == null)
				break;
			
			ByteBuffer buffer = w.getBuffer();
			if(buffer == null)
				break;
			
			if(channel.write(buffer) == -1)
				break;
			
			if(!buffer.hasRemaining())
				endWriteActive();
			return;
		}
		while(false);
		disconnect();
	}
	
	protected abstract void onRead(ByteBuffer read);
	protected abstract void onWriteEnd();

	/*********************WRITE_QUEUE**********************/
	private ArrayDeque<Writable> writeQueue = new ArrayDeque<Writable>();
	private Writable activeWritable = null;
	@Override
	public void enqueueWrite(Writable w)
	{
		synchronized (writeQueue)
		{
			writeQueue.add(w);
		}		
		addInterestOp(SelectionKey.OP_WRITE);
		if(selector != null)
			selector.wakeup();
	}
	@Override
	public void enqueueWrite(ByteBuffer buffer){enqueueWrite(new Writable(buffer));}
	@Override
	public void enqueueWrite(byte[] bytes){enqueueWrite(new Writable(ByteBuffer.wrap(bytes)));}
	protected synchronized Writable getWritable()
	{
		if(activeWritable == null)
		{
			if(writeQueue.size() == 0)
				return null;
			activeWritable = writeQueue.poll();
		}
		return activeWritable;
	}
	protected synchronized void endWriteActive()
	{
		activeWritable = null;
		if(writeQueue.size() == 0)
		{
			clearInterestOp(SelectionKey.OP_WRITE);
			onWriteEnd();
		}
	}
	/*********************WRITE_QUEUE**********************/
}
