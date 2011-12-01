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

import java.util.*;

import com.globalmentor.android.widget.NameValueListAdapter;
import com.globalmentor.model.NameValuePair;

import android.content.pm.*;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.*;

/**
 * Activity for showing "about" information for the application in list form, including the application name, application version, and information about the
 * current system conditions.
 * 
 * @author Garret Wilson
 */
public class AboutListActivity extends AbstractListActivity<ApplicationInfo>
{

	@Override
	protected ListAdapter createListAdapter()
	{
		final List<NameValuePair<CharSequence, Object>> properties = new ArrayList<NameValuePair<CharSequence, Object>>();
		//application name and version
		final String packageName = getPackageName(); //get the name of the application package
		final PackageManager packageManager = getPackageManager();
		try
		{
			final ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA); //get information about the application itself from its package
			final CharSequence applicationName = packageManager.getApplicationLabel(applicationInfo); //get the application name (label)
			final PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0); //get information about the application package
			final CharSequence applicationVersion = packageInfo.versionName + " (" + packageInfo.versionCode + ")";
			properties.add(new NameValuePair<CharSequence, Object>(applicationName, applicationVersion));
		}
		catch(NameNotFoundException e)
		{
			throw new AssertionError("Could not find information for application package " + packageName); //we should always be able to retrieve information on the current application
		}
		return new NameValueListAdapter(this, properties);
	}

}
