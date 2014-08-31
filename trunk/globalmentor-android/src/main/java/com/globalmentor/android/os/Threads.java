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

import android.os.*;

/**
 * Various utilities for working with threads in Android.
 * 
 * @author Garret Wilson
 */
public class Threads {

	/** A handler associated with the main thread. */
	public static final Handler MAIN_THREAD_HANDLER = new Handler(Looper.getMainLooper());

	/** @return Whether the currently executing thread is the UI thread. */
	public static boolean isMainThread() {
		return Thread.currentThread() == Looper.getMainLooper().getThread();
	}

	/**
	 * Verifies that the code is running in the UI thread.
	 * @throws IllegalStateException if the code is executing in a non-UI thread.
	 * @see #isMainThread()
	 */
	public static void checkMainThread() {
		if(!isMainThread()) {
			throw new IllegalStateException("Code must be called from the UI thread; code was called from thread \"" + Thread.currentThread().getName() + "\".");
		}
	}

	/**
	 * Ensures that the given runnable is run on the main thread. If the current thread is the main thread, the runnable is run immediately. Otherwise, it is run
	 * some time in the future on the main thread.
	 * @param runnable The runnable to run on the main thread.
	 * @throws NullPointerException if the given runnable is <code>null</code>.
	 */
	public static void runOnMainThread(final Runnable runnable) {
		if(isMainThread()) { //if we're already on the main thread
			runnable.run(); //run the runnable now
		} else { //if we're not on the main thread
			MAIN_THREAD_HANDLER.post(runnable); //run the runnable on the main thread later
		}
	}

	/**
	 * Ensures that the given runnable is run on some thread besides the main thread. If the current thread is the main thread, a separate thread is created and
	 * started with the runnable. Otherwise, the runnable is run immediately.
	 * @param runnable The runnable to run off the main thread.
	 * @throws NullPointerException if the given runnable is <code>null</code>.
	 */
	public static void runOffMainThread(final Runnable runnable) {
		if(isMainThread()) { //if we're already on the main thread
			new Thread(runnable).start(); //start a new thread to run the runnable
		} else { //if we're not on the main thread
			runnable.run(); //run the runnable now
		}
	}

}
