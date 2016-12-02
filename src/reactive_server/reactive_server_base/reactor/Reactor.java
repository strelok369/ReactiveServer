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

package reactive_server.reactive_server_base.reactor;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Set;

public class Reactor implements Runnable
{
	private Selector selector;
	protected void setSelector(Selector selector){this.selector = selector;}
	
	/**************************************************************************/
	private ArrayDeque<Setuppable> setupQueue = new ArrayDeque<Setuppable>();
	public synchronized void enqueueSetupable(Setuppable attachement)
	{
		if(attachement == null)
			return;
		setupQueue.add(attachement);
		if(selector != null)
			selector.wakeup();
	}
	private synchronized void setup() throws IOException
	{
		if(setupQueue.isEmpty())
			return;
		Iterator<Setuppable> setupIt = setupQueue.iterator();
		while(setupIt.hasNext())
			setupIt.next().setup(selector);
		setupQueue.clear();
	}
	/**************************************************************************/
	
	/**************************************************************************/
	private Thread reactorThread;
	public void start() throws IOException
	{
		reactorThread = new Thread(this);
		reactorThread.start();
	}
	public void stop(){reactorThread.interrupt();}
	protected void onStop()
	{
		try{selector.close();}catch(IOException e){}
	}
	/**************************************************************************/
	
	@Override
	public void run()
	{
		while(!reactorThread.isInterrupted())
		{
			try
			{
				setup();
				selector.select();
				Set<SelectionKey> selected = selector.selectedKeys();
				Iterator<SelectionKey> selectIt = selected.iterator();
				while(selectIt.hasNext())
					dispatch(selectIt.next());
				selected.clear();
			}
			catch(IOException e){e.printStackTrace();}
		}
		onStop();
	}
	
	private void dispatch(SelectionKey key)
	{
		if(!key.isValid())
			return;
		Runnable r = (Runnable)key.attachment();
		if(r == null)
			return;
		r.run();
	}
}
