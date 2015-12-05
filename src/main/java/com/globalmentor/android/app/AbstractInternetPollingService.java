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

import android.content.Context;
import android.util.Log;

import com.globalmentor.android.net.Network;

/**
 * Service that performs polling of the Internet a worker thread.
 * 
 * <p>
 * The class should set poll frequencies with the {@link #getPollFrequencyManager()} inside {@link #onCreate()}. Polling is performed inside
 * {@link #pollInternet()} after it is verified that there is an Internet connection.
 * </p>
 * 
 * @author Garret Wilson
 */
public abstract class AbstractInternetPollingService extends AbstractPollingService {

	/** {@inheritDoc} This method calls {@link #pollInternet()} if it is verified that an Internet connection is present. */
	@Override
	protected final void poll() {
		if(Network.isConnected(this)) { //only try to poll the Internet if we have an Internet connection
			Log.d(getLogTag(), "Polling Internet.");
			pollInternet();
		}
	}

	/**
	 * Performs Internet polling. This method is only called when an Internet connection is present.
	 * @see Network#isConnected(Context)
	 */
	protected abstract void pollInternet();

}
