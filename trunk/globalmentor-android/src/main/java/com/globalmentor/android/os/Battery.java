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

import static android.os.BatteryManager.*;
import android.content.*;

/**
 * Utilities for finding information about the battery and other power sources.
 * 
 * @author Garret Wilson
 */
public class Battery {

	/**
	 * Determines whether the devices is plugged into a power source (i.e. is not running solely on battery).
	 * @param context The current context.
	 * @return <code>true</code> if the device is connected to a power source.
	 */
	public static boolean isPlugged(final Context context) {
		final Intent batteryChangedIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED)); //don't actually register a receiver; just get the last sticky intent broadcast
		if(batteryChangedIntent == null) { //we always expect to get an intent, but no need to crash the application is Android screws up
			return false;
		}
		return batteryChangedIntent.getExtras().getInt(EXTRA_PLUGGED, 0) != 0; //zero means battery; other values are for other types of power sources
	}

}
