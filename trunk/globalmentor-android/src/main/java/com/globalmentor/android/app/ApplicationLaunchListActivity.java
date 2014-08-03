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

import android.content.pm.ApplicationInfo;

/**
 * Activity for showing a list of installed applications, and launching one when clicked.
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
public class ApplicationLaunchListActivity extends ApplicationListActivity {

	@Override
	public void onItemClick(final int position, final long id, final ApplicationInfo item) {
		super.onItemClick(position, id, item);
		try {
			Applications.launch(this, item);
		} catch(final Throwable throwable) {
			Notifications.error(this, throwable);
		}
	}

}
