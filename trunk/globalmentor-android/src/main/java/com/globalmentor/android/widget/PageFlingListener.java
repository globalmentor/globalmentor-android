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

package com.globalmentor.android.widget;

import com.globalmentor.android.view.Axis;

import android.content.Context;
import android.view.*;

/**
 * A gesture listener that detects flings and ensures that they fall within the system's thresholds for page fling events. That is, they check whether a fling
 * is of sufficiently high velocity and has traveled the minimum distance required for page flings. Child classes should override
 * {@link #onVerifiedFling(MotionEvent, MotionEvent, float, float)}.
 * 
 * @author Garret Wilson
 * 
 * @see <a href="http://www.codeshogun.com/blog/2009/04/16/how-to-implement-swipe-action-in-android/">How to implement Swipe action in Android</a>
 * @see <a href="http://stackoverflow.com/questions/937313/android-basic-gesture-detection">Android - basic gesture detection</a>
 */
public class PageFlingListener extends VerifiedFlingListener
{

	/**
	 * Context constructor.
	 * @param context The current context.
	 * @throws NullPointerException if the given context is <code>null</code>.
	 */
	public PageFlingListener(final Context context)
	{
		super(context);
	}

	/**
	 * Determines whether the given fling gesture falls within the appropriate limits.
	 * <p>
	 * This version ensures that the fling distance exceeds the threshold for page flings.
	 * </p>
	 * @see ViewConfiguration#getScaledPagingTouchSlop()
	 */
	@Override
	public boolean isFlingVerified(final Axis axis, final float distance, float velocity)
	{
		if(!super.isFlingVerified(axis, distance, velocity)) //make sure the fling passes the default tests
		{
			return false;
		}
		final ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
		if(Math.abs(distance) <= viewConfiguration.getScaledPagingTouchSlop()) //make sure the fling is sufficient for a page fling
		{
			return false;
		}
		return true;
	}

}
