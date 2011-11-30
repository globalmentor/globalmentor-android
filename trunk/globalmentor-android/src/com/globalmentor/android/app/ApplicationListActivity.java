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

import java.util.List;

import com.globalmentor.android.widget.ApplicationInfoAdapter;

import android.content.pm.*;
import android.widget.*;

/**
 * Activity for showing a list of installed applications.
 * 
 * <p>
 * This class merely displays a list of applications; it does not perform any functionality when an application in the list is clicked. Such functionality is
 * left to subclasses to perform by overriding e.g. {@link #onApplicationClick(ApplicationInfo)}.
 * </p>
 * 
 * <p>
 * This class was initially created following closely <a href="http://xjaphx.wordpress.com/2011/06/12/create-application-launcher-as-a-list/">Create Application
 * Launcher as a list</a> tutorial.
 * </p>
 * 
 * @author Garret Wilson
 * 
 * @see <a href="http://xjaphx.wordpress.com/2011/06/12/create-application-launcher-as-a-list/">Create Application Launcher as a list</a>
 */
public class ApplicationListActivity extends AbstractListActivity<ApplicationInfo>
{

	@Override
	protected ListAdapter createListAdapter()
	{
		final PackageManager packageManager = getPackageManager(); //get a list of applications
		final List<ApplicationInfo> applications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA); //show the applications using an adapter
		return new ApplicationInfoAdapter(this, applications);
	}

	/**
	 * Called when an application on the list is clicked.
	 * <p>
	 * This version does nothing.
	 * </p>
	 * @param applicationInfo Information on the application that was clicked.
	 */
	public void onApplicationClick(final ApplicationInfo applicationInfo)
	{
	}

}
