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

import android.content.Context;
import android.content.pm.ApplicationInfo;

/**
 * Utilities for working with Android applications.
 * 
 * @author Garret Wilson
 */
public class Applications
{

	/**
	 * Determines whether the current application is in debug mode.
	 * @param context The current context.
	 * @return <code>true</code> if the application for this context is in debug mode.
	 * @see ApplicationInfo#FLAG_DEBUGGABLE
	 */
	public static boolean isDebug(final Context context)
	{
		return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
	}

}
