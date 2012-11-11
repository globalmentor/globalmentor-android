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

import android.util.Log;

import com.globalmentor.model.PollStateManager;

/**
 * Service that performs polling a worker thread.
 * 
 * <p>
 * The class should set poll frequencies with the {@link #getPollFrequencyManager()} inside {@link #onCreate()}. Polling is performed inside {@link #poll()}.
 * </p>
 * 
 * @author Garret Wilson
 */
public abstract class AbstractPollingService extends AbstractWorkerService
{

	/** The class managing the frequency between polls. */
	private PollStateManager<Long> pollFrequencyManager;

	/** @return The class managing the frequency between polls. */
	protected PollStateManager<Long> getPollFrequencyManager()
	{
		return pollFrequencyManager;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		pollFrequencyManager = new PollStateManager<Long>();
	}

	/**
	 * Performs polling until the worker thread is interrupted.
	 * @see #poll()
	 */
	@Override
	protected final void work()
	{
		boolean poll = true;
		do
		{
			try
			{
				poll();
				Thread.sleep(getPollFrequencyManager().pollState()); //wait for a while
			}
			catch(final InterruptedException interruptedException)
			{
				poll = false;
			}
			catch(final Exception exception)
			{
				Log.e(getLogTag(), "Error during polling.", exception);
			}
		}
		while(poll);
	}

	/** Performs polling. */
	protected abstract void poll();

}
