/*
 * Copyright © 2012 GlobalMentor, Inc. <http://www.globalmentor.com/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.globalmentor.android.app;

import android.app.Service;
import android.content.Intent;
import android.util.Log;

/**
 * Service that performs some work in a worker thread.
 * 
 * <p>
 * Work is performed inside {@link #work()}.
 * </p>
 * 
 * <p>
 * The service starts with setting {@link Service#START_STICKY}.
 * </p>
 * 
 * @author Garret Wilson
 */
public abstract class AbstractWorkerService extends AbstractBaseService
{

	/** The thread that does the actual working. */
	private Thread workerThread = null;

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId)
	{
		super.onStartCommand(intent, flags, startId);
		if(workerThread == null) //if we haven't started the worker thread, yet
		{
			workerThread = new Thread(new Worker(), getClass().getSimpleName() + Worker.class.getSimpleName());
			workerThread.start(); //start the worker in a separate thread
		}
		return START_STICKY; //request that the service continue running until it is explicitly stopped
	}

	@Override
	public void onDestroy()
	{
		if(workerThread != null)
		{
			workerThread.interrupt(); //interrupt the worker so that it can end
		}
		super.onDestroy();
	}

	/** Performs work in the worker thread. When this method exits, the worker thread completes. */
	protected abstract void work();

	/**
	 * The worker that executes in a separate thread, waking from time to time to check for new photos.
	 * 
	 * <p>
	 * To end the thread, it need merely be interrupted.
	 * </p>
	 * 
	 * @author Garret Wilson
	 */
	private class Worker implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				work();
			}
			catch(final Exception exception)
			{
				Log.e(getLogTag(), "Error in worker thread.", exception);
			}
		}
	}

}