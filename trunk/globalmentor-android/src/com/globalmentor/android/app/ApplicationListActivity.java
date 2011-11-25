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

import com.globalmentor.android.R;
import com.globalmentor.android.widget.ApplicationInfoAdapter;

import android.content.pm.*;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

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
public class ApplicationListActivity extends BaseActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_applicationlistactivity);
		final ListView listView = (ListView)findViewById(R.id.app_applicationlistactivity_list);
		final PackageManager packageManager = getPackageManager(); //get a list of applications
		final List<ApplicationInfo> applications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA); //show the applications using an adapter
		final ApplicationInfoAdapter adapter = new ApplicationInfoAdapter(this, applications);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() //call onApplicationClick() with the application information
				{
					@Override
					public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id)
					{
						final ApplicationInfoAdapter adapter = ((ApplicationInfoAdapter)parent.getAdapter()); //get the adapter...
						final ApplicationInfo applicationInfo = (ApplicationInfo)adapter.getItem(position); //...and get the application info from the adapter
						onApplicationClick(applicationInfo); //delegate to the application click method
					}
				});
	}

	/**
	 * Called when an application on the list is clicked.
	 * 
	 * <p>
	 * This version does nothing.
	 * </p>
	 * 
	 * @param applicationInfo Information on the application that was clicked.
	 */
	public void onApplicationClick(final ApplicationInfo applicationInfo)
	{
	}

}
