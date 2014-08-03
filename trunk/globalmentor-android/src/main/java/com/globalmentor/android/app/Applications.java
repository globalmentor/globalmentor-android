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

import android.content.*;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * Utilities for working with Android applications.
 * 
 * @author Garret Wilson
 */
public class Applications {

	/**
	 * Checks whether the current application has the given permission.
	 * @param context The current context.
	 * @param permissionName The name of the permission to check.
	 * @return <code>true</code> if the application for the current context has the indicated permission.
	 * @throws NullPointerException if the given context and/or permission name is <code>null</code>.
	 * @see PackageManager#checkPermission(String, String)
	 */
	public static boolean hasPermission(final Context context, final String permissionName) {
		return context.getPackageManager().checkPermission(permissionName, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;

	}

	/**
	 * Determines whether the current application is in debug mode.
	 * @param context The current context.
	 * @return <code>true</code> if the application for this context is in debug mode.
	 * @see ApplicationInfo#FLAG_DEBUGGABLE
	 */
	public static boolean isDebug(final Context context) {
		return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
	}

	/**
	 * Launches an application.
	 * @param context The current context.
	 * @param applicationInfo A description of the application.
	 * @throws NullPointerException if the given context and/or application info is <code>null</code>.
	 * @throws NameNotFoundException if the given package name is not recognized.
	 * @throws ActivityNotFoundException if no activity could be found for the given package.
	 */
	public static void launch(final Context context, final ApplicationInfo applicationInfo) {
		launch(context, applicationInfo.packageName);
	}

	/**
	 * Launches an application.
	 * @param context The current context.
	 * @param packageName The name of the package to launch.
	 * @throws NullPointerException if the given context and/or package name.
	 * @throws NameNotFoundException if the given package name is not recognized.
	 * @throws ActivityNotFoundException if no activity could be found for the given package.
	 */
	public static void launch(final Context context, final String packageName) {
		final Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
		if(launchIntent != null) { //if there is a launch intent for this package
			context.startActivity(launchIntent); //start the activity from the launch intent
		}
	}

}
