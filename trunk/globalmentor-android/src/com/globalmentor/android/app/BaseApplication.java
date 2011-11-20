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

package com.globalmentor.android.app;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.os.StrictMode;

/**
 * Application with added functionality to serve as a base for most new applications.
 * 
 * <p>
 * When in debug mode, this version turns on thread and virtual machine strict mode with logging.
 * </p>
 * 
 * @author Garret Wilson
 */
public class BaseApplication extends Application
{

	/** {@inheritDoc} This version turns on strict mode if in debug mode. */
	@Override
	public void onCreate()
	{
		super.onCreate();
		if(isDebug()) //if we're in debug mode, turn on strict mode
		{
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build()); //detect and log all thread violations
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build()); //detect and log all virtual machine violations
		}
	}

	/**
	 * @return Whether this application is in debug mode.
	 * @see ApplicationInfo#FLAG_DEBUGGABLE
	 */
	public boolean isDebug()
	{
		return Applications.isDebug(this);
	}

}
