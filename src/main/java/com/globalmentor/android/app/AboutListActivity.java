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

import static com.globalmentor.java.Conditions.*;

import java.text.DateFormat;
import java.util.*;

import com.globalmentor.android.R;
import com.globalmentor.android.widget.NameValueListAdapter;
import com.globalmentor.model.NameValuePair;
import com.globalmentor.si.SIUnit;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.*;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.provider.Settings.Secure;
import android.widget.*;

/**
 * Activity for showing "about" information for the application in list form, including the application name, application version, and information about the
 * current system conditions.
 * 
 * @author Garret Wilson
 */
public class AboutListActivity extends AbstractListActivity<ApplicationInfo> {

	@Override
	protected ListAdapter createListAdapter() {
		final Resources resources = getResources();
		final List<NameValuePair<CharSequence, Object>> properties = new ArrayList<NameValuePair<CharSequence, Object>>();
		final PackageManager packageManager = getPackageManager();
		final String packageName = getPackageName(); //get the name of the application package
		final ApplicationInfo applicationInfo;
		final PackageInfo packageInfo;
		try {
			applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA); //get information about the application itself from its package
			packageInfo = packageManager.getPackageInfo(packageName, 0); //get information about the application package
		} catch(final NameNotFoundException nameNotFoundException) {
			throw unexpected("Could not find information for application package " + packageName, nameNotFoundException);
		}
		//application name and version
		final CharSequence applicationName = packageManager.getApplicationLabel(applicationInfo); //get the application name (label)
		final CharSequence applicationVersion = packageInfo.versionName + " (" + packageInfo.versionCode + ")";
		properties.add(new NameValuePair<CharSequence, Object>(applicationName, applicationVersion));
		//first installed
		final DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(this);
		final DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(this);
		final Date firstInstalled = new Date(packageInfo.firstInstallTime);
		properties.add(new NameValuePair<CharSequence, Object>(resources.getString(R.string.app_aboutlistactivity_first_installed_label), dateFormat
				.format(firstInstalled) + ' ' + timeFormat.format(firstInstalled)));
		//last updated
		final Date lastUpdated = new Date(packageInfo.lastUpdateTime);
		properties.add(new NameValuePair<CharSequence, Object>(resources.getString(R.string.app_aboutlistactivity_last_updated_label), dateFormat
				.format(lastUpdated) + ' ' + timeFormat.format(lastUpdated)));
		//Android ID; see http://android-developers.blogspot.com/2011/03/identifying-app-installations.html, http://stackoverflow.com/questions/2785485/is-there-a-unique-android-device-id
		properties.add(new NameValuePair<CharSequence, Object>(resources.getString(R.string.app_aboutlistactivity_android_id_label), Secure.getString(
				getContentResolver(), Secure.ANDROID_ID)));
		//available memory
		final ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		final ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);
		properties.add(new NameValuePair<CharSequence, Object>(resources.getString(R.string.app_aboutlistactivity_available_memory_label), SIUnit.BYTE
				.format(memoryInfo.availMem)));
		return new NameValueListAdapter(this, properties);
	}

}
