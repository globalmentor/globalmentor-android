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

import android.app.Activity;
import android.content.pm.ApplicationInfo;

/**
 * Activity with added functionality to serve as a base for most new activities.
 * 
 * @author Garret Wilson
 */
public class BaseActivity extends Activity
{

	/**
	 * @return Whether this application is in debug mode.
	 * @see ApplicationInfo#FLAG_DEBUGGABLE
	 */
	public boolean isDebug()
	{
		return Applications.isDebug(this);
	}

	/**
	 * Returns a log tag appropriate for this activity.
	 * <p>
	 * This implementation delegates to the {@link Class#getSimpleName()} of the activity class.
	 * </p>
	 * @return A log tag appropriate for this activity.
	 */
	public String getLogTag()
	{
		return getClass().getSimpleName();
	}

}
