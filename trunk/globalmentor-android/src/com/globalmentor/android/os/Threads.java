/*
 * Copyright © 2011 GlobalMentor, Inc. <http://www.globalmentor.com/>
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

package com.globalmentor.android.os;

import android.os.Looper;

/**
 * Various utilities for working with threads in Android.
 * 
 * @author Garret Wilson
 */
public class Threads
{

	/** @return Whether the currently executing thread is the UI thread. */
	public static boolean isMainThread()
	{
		return Thread.currentThread() == Looper.getMainLooper().getThread();
	}

	/**
	 * Verifies that the code is running in the UI thread.
	 * @throws IllegalStateException if the code is executing in a non-UI thread.
	 * @see #isMainThread()
	 */
	public static void checkMainThread()
	{
		if(!isMainThread())
		{
			throw new IllegalStateException("Code must be called from the UI thread; code was called from thread \"" + Thread.currentThread().getName() + "\".");
		}

	}

}
