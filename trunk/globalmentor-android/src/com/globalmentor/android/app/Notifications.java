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

import static com.globalmentor.android.os.Threads.*;
import static com.globalmentor.java.Conditions.*;
import static com.globalmentor.java.Objects.*;

import java.util.concurrent.atomic.AtomicInteger;

import com.globalmentor.android.R;
import com.globalmentor.time.Milliseconds;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.util.Log;
import android.widget.Toast;

/**
 * Utilities for working with notifying the user (not just {@link Notification}s specifically).
 * 
 * <p>
 * All these utility methods may be run from any thread; notifications will be posted later on the main thread if needed.
 * </p>
 * 
 * @author Garret Wilson
 */
public class Notifications
{

	/**
	 * The atomic integer representing the next ID to use for notifications. Because each application gets a separate instance of the Dalvik VM, maintaining the
	 * next notification ID statically does not deplete the available IDs more rapidly than a per-application record.
	 */
	private final static AtomicInteger nextNotificationID = new AtomicInteger();

	/**
	 * Generates a new ID for notifications.
	 * <p>
	 * This method can be called from any thread.
	 * </p>
	 * <p>
	 * This implementation is guaranteed to be unique for at least 2^{@value Integer#SIZE} calls.
	 * </p>
	 * @return A unique ID to be used for notifications.
	 */
	public static int generateNotificationID()
	{
		return nextNotificationID.getAndIncrement(); //return the current ID and increment the value
	}

	/**
	 * Logs and notifies the user of information using a {@link Toast#LENGTH_SHORT} toast. The tag is not included in the toast.
	 * @param context The current context, the {@link Class#getSimpleName()} of which is also used as a log tag.
	 * @param resId The resource ID of the string resource to use.
	 * @param formatArgs The format arguments that will be used for substitution, if any.
	 * @throws NullPointerException if the given context and/or resource ID is <code>null</code>.
	 * @throws NotFoundException if the given ID does not exist in the resources.
	 * @see Log#e(String, String)
	 * @see Toast#makeText(Context, CharSequence, int)
	 */
	public static void info(final Context context, final int resId, final Object... formatArgs) throws NotFoundException
	{
		info(context.getClass().getSimpleName(), context, resId, formatArgs);
	}

	/**
	 * Logs and notifies the user of information using a {@link Toast#LENGTH_SHORT} toast. The tag is not included in the toast. The string is retrieved from a
	 * quantity resource using the provided quantity.
	 * @param quantity The number used to get the correct string for the current language's plural rules.
	 * @param context The current context, the {@link Class#getSimpleName()} of which is also used as a log tag.
	 * @param resId The resource ID of the string resource to use.
	 * @param formatArgs The format arguments that will be used for substitution, if any.
	 * @throws NullPointerException if the given context and/or resource ID is <code>null</code>.
	 * @throws NotFoundException if the given ID does not exist in the resources.
	 * @see Log#e(String, String)
	 * @see Toast#makeText(Context, CharSequence, int)
	 */
	public static void info(final int quantity, final Context context, final int resId, final Object... formatArgs) throws NotFoundException
	{
		info(quantity, context.getClass().getSimpleName(), context, resId, formatArgs);
	}

	/**
	 * Logs and notifies the user of information using a {@link Toast#LENGTH_SHORT} toast. The tag not is included in the toast.
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param context The current context.
	 * @param resId The resource ID of the string resource to use.
	 * @param formatArgs The format arguments that will be used for substitution, if any.
	 * @throws NullPointerException if the given tag, context, and/or resource ID is <code>null</code>.
	 * @throws NotFoundException if the given ID does not exist in the resources.
	 * @see Log#e(String, String)
	 * @see Toast#makeText(Context, CharSequence, int)
	 */
	public static void info(final String tag, final Context context, final int resId, final Object... formatArgs) throws NotFoundException
	{
		info(tag, context, formatArgs.length > 0 ? context.getResources().getString(resId, formatArgs) : context.getResources().getString(resId));
	}

	/**
	 * Logs and notifies the user of information using a {@link Toast#LENGTH_SHORT} toast. The tag not is included in the toast. The string is retrieved from a
	 * quantity resource using the provided quantity.
	 * @param quantity The number used to get the correct string for the current language's plural rules.
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param context The current context.
	 * @param resId The resource ID of the string resource to use.
	 * @param formatArgs The format arguments that will be used for substitution, if any.
	 * @throws NullPointerException if the given tag, context, and/or resource ID is <code>null</code>.
	 * @throws NotFoundException if the given ID does not exist in the resources.
	 * @see Log#e(String, String)
	 * @see Toast#makeText(Context, CharSequence, int)
	 */
	public static void info(final int quantity, final String tag, final Context context, final int resId, final Object... formatArgs) throws NotFoundException
	{
		info(tag, context, formatArgs.length > 0 ? context.getResources().getQuantityString(resId, quantity, formatArgs) : context.getResources()
				.getQuantityString(resId, quantity));
	}

	/**
	 * Logs and notifies the user of information using a {@link Toast#LENGTH_SHORT} toast. The tag is not included in the toast.
	 * @param context The current context, the {@link Class#getSimpleName()} of which is also used as a log tag.
	 * @param message The message to log.
	 * @throws NullPointerException if the given context and/or message is <code>null</code>.
	 * @see Log#e(String, String)
	 * @see Toast#makeText(Context, CharSequence, int)
	 */
	public static void info(final Context context, final CharSequence message)
	{
		info(context.getClass().getSimpleName(), context, message);
	}

	/**
	 * Logs and notifies the user of information using a {@link Toast#LENGTH_SHORT} toast. The tag is not included in the toast.
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param context The current context.
	 * @param message The message to log.
	 * @throws NullPointerException if the given tag, context, and/or message is <code>null</code>.
	 * @see Log#i(String, String)
	 * @see Toast#makeText(Context, CharSequence, int)
	 */
	public static void info(final String tag, final Context context, final CharSequence message)
	{
		Log.i(tag, message.toString()); //log the message
		runOnMainThread(new Runnable()
		{
			@Override
			public void run()
			{
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show(); //show the message in a toast on the main thread
			}
		});
	}

	/**
	 * Logs and notifies the user of an error using a {@link Toast#LENGTH_LONG} toast. The tag is included in the toast.
	 * @param context The current context, the {@link Class#getSimpleName()} of which is also used as a log tag.
	 * @param resId The resource ID of the string resource to use.
	 * @param formatArgs The format arguments that will be used for substitution, if any.
	 * @throws NullPointerException if the given context and/or resource ID is <code>null</code>.
	 * @throws NotFoundException if the given ID does not exist in the resources.
	 * @see Log#e(String, String)
	 * @see Toast#makeText(Context, CharSequence, int)
	 */
	public static void error(final Context context, final int resId, final Object... formatArgs) throws NotFoundException
	{
		error(context.getClass().getSimpleName(), context, resId, formatArgs);
	}

	/**
	 * Logs and notifies the user of an error using a {@link Toast#LENGTH_LONG} toast. The tag is included in the toast. The string is retrieved from a quantity
	 * resource using the provided quantity.
	 * @param quantity The number used to get the correct string for the current language's plural rules.
	 * @param context The current context, the {@link Class#getSimpleName()} of which is also used as a log tag.
	 * @param resId The resource ID of the string resource to use.
	 * @param formatArgs The format arguments that will be used for substitution, if any.
	 * @throws NullPointerException if the given context and/or resource ID is <code>null</code>.
	 * @throws NotFoundException if the given ID does not exist in the resources.
	 * @see Log#e(String, String)
	 * @see Toast#makeText(Context, CharSequence, int)
	 */
	public static void error(final int quantity, final Context context, final int resId, final Object... formatArgs) throws NotFoundException
	{
		error(quantity, context.getClass().getSimpleName(), context, resId, formatArgs);
	}

	/**
	 * Logs and notifies the user of an error using a {@link Toast#LENGTH_LONG} toast. The tag is included in the toast.
	 * @param context The current context, the {@link Class#getSimpleName()} of which is also used as a log tag.
	 * @param throwable The source of the error, providing a stack trace, or <code>null</code> if there is no available source throwable.
	 * @param resId The resource ID of the string resource to use.
	 * @param formatArgs The format arguments that will be used for substitution, if any.
	 * @throws NullPointerException if the given context and/or resource ID is <code>null</code>.
	 * @throws NotFoundException if the given ID does not exist in the resources.
	 * @see Log#e(String, String)
	 * @see Log#e(String, String, Throwable)
	 * @see Toast#makeText(Context, CharSequence, int)
	 */
	public static void error(final Context context, final Throwable throwable, final int resId, final Object... formatArgs) throws NotFoundException
	{
		error(context.getClass().getSimpleName(), context, throwable, resId, formatArgs);
	}

	/**
	 * Logs and notifies the user of an error using a {@link Toast#LENGTH_LONG} toast. The tag is included in the toast. The string is retrieved from a quantity
	 * resource using the provided quantity.
	 * @param quantity The number used to get the correct string for the current language's plural rules.
	 * @param context The current context, the {@link Class#getSimpleName()} of which is also used as a log tag.
	 * @param throwable The source of the error, providing a stack trace, or <code>null</code> if there is no available source throwable.
	 * @param resId The resource ID of the string resource to use.
	 * @param formatArgs The format arguments that will be used for substitution, if any.
	 * @throws NullPointerException if the given context and/or resource ID is <code>null</code>.
	 * @throws NotFoundException if the given ID does not exist in the resources.
	 * @see Log#e(String, String)
	 * @see Log#e(String, String, Throwable)
	 * @see Toast#makeText(Context, CharSequence, int)
	 */
	public static void error(final int quantity, final Context context, final Throwable throwable, final int resId, final Object... formatArgs)
			throws NotFoundException
	{
		error(quantity, context.getClass().getSimpleName(), context, throwable, resId, formatArgs);
	}

	/**
	 * Logs and notifies the user of an error using a {@link Toast#LENGTH_LONG} toast. The tag is included in the toast.
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param context The current context.
	 * @param resId The resource ID of the string resource to use.
	 * @param formatArgs The format arguments that will be used for substitution, if any.
	 * @throws NullPointerException if the given tag, context, and/or resource ID is <code>null</code>.
	 * @throws NotFoundException if the given ID does not exist in the resources.
	 * @see Log#e(String, String)
	 * @see Toast#makeText(Context, CharSequence, int)
	 */
	public static void error(final String tag, final Context context, final int resId, final Object... formatArgs) throws NotFoundException
	{
		error(tag, context, null, resId, formatArgs);
	}

	/**
	 * Logs and notifies the user of an error using a {@link Toast#LENGTH_LONG} toast. The tag is included in the toast. The string is retrieved from a quantity
	 * resource using the provided quantity.
	 * @param quantity The number used to get the correct string for the current language's plural rules.
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param context The current context.
	 * @param resId The resource ID of the string resource to use.
	 * @param formatArgs The format arguments that will be used for substitution, if any.
	 * @throws NullPointerException if the given tag, context, and/or resource ID is <code>null</code>.
	 * @throws NotFoundException if the given ID does not exist in the resources.
	 * @see Log#e(String, String)
	 * @see Toast#makeText(Context, CharSequence, int)
	 */
	public static void error(final int quantity, final String tag, final Context context, final int resId, final Object... formatArgs) throws NotFoundException
	{
		error(quantity, tag, context, null, resId, formatArgs);
	}

	/**
	 * Logs and notifies the user of an error using a {@link Toast#LENGTH_LONG} toast. The tag is included in the toast.
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param context The current context.
	 * @param throwable The source of the error, providing a stack trace, or <code>null</code> if there is no available source throwable.
	 * @param resId The resource ID of the string resource to use.
	 * @param formatArgs The format arguments that will be used for substitution, if any.
	 * @throws NullPointerException if the given tag, context, and/or resource ID is <code>null</code>.
	 * @throws NotFoundException if the given ID does not exist in the resources.
	 * @see Log#e(String, String)
	 * @see Log#e(String, String, Throwable)
	 * @see Toast#makeText(Context, CharSequence, int)
	 */
	public static void error(final String tag, final Context context, final Throwable throwable, final int resId, final Object... formatArgs)
			throws NotFoundException
	{
		error(tag, context, formatArgs.length > 0 ? context.getResources().getString(resId, formatArgs) : context.getResources().getString(resId), throwable);
	}

	/**
	 * Logs and notifies the user of an error using a {@link Toast#LENGTH_LONG} toast. The tag is included in the toast. The string is retrieved from a quantity
	 * resource using the provided quantity.
	 * @param quantity The number used to get the correct string for the current language's plural rules.
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param context The current context.
	 * @param throwable The source of the error, providing a stack trace, or <code>null</code> if there is no available source throwable.
	 * @param resId The resource ID of the quantity string resource to use.
	 * @param formatArgs The format arguments that will be used for substitution, if any.
	 * @throws NullPointerException if the given tag, context, and/or resource ID is <code>null</code>.
	 * @throws NotFoundException if the given ID does not exist in the resources.
	 * @see Log#e(String, String)
	 * @see Log#e(String, String, Throwable)
	 * @see Toast#makeText(Context, CharSequence, int)
	 */
	public static void error(final int quantity, final String tag, final Context context, final Throwable throwable, final int resId, final Object... formatArgs)
			throws NotFoundException
	{
		error(tag, context, formatArgs.length > 0 ? context.getResources().getQuantityString(resId, quantity, formatArgs) : context.getResources()
				.getQuantityString(resId, quantity), throwable);
	}

	/**
	 * Logs and notifies the user of an error using a {@link Toast#LENGTH_LONG} toast. The tag is included in the toast.
	 * @param context The current context, the {@link Class#getSimpleName()} of which is also used as a log tag.
	 * @param message The message to log.
	 * @throws NullPointerException if the given context and/or message is <code>null</code>.
	 * @see Log#e(String, String)
	 * @see Toast#makeText(Context, CharSequence, int)
	 */
	public static void error(final Context context, final CharSequence message)
	{
		error(context.getClass().getSimpleName(), context, message);
	}

	/**
	 * Logs and notifies the user of an error using a {@link Toast#LENGTH_LONG} toast. The tag is included in the toast.
	 * @param context The current context, the {@link Class#getSimpleName()} of which is also used as a log tag.
	 * @param throwable The source of the error, providing an optional message and a stack trace.
	 * @throws NullPointerException if the given context and/or throwable is <code>null</code>.
	 * @see Log#e(String, String)
	 * @see Log#e(String, String, Throwable)
	 * @see Toast#makeText(Context, CharSequence, int)
	 */
	public static void error(final Context context, final Throwable throwable)
	{
		error(context.getClass().getSimpleName(), context, throwable);
	}

	/**
	 * Logs and notifies the user of an error using a {@link Toast#LENGTH_LONG} toast. The tag is included in the toast.
	 * @param context The current context, the {@link Class#getSimpleName()} of which is also used as a log tag.
	 * @param message The message to log.
	 * @param throwable The source of the error, providing a stack trace, or <code>null</code> if there is no available source throwable.
	 * @throws NullPointerException if the given context and/or message is <code>null</code>.
	 * @see Log#e(String, String)
	 * @see Log#e(String, String, Throwable)
	 * @see Toast#makeText(Context, CharSequence, int)
	 */
	public static void error(final Context context, final CharSequence message, final Throwable throwable)
	{
		error(context.getClass().getSimpleName(), context, message, throwable);
	}

	/**
	 * Logs and notifies the user of an error using a {@link Toast#LENGTH_LONG} toast. The tag is included in the toast.
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param context The current context.
	 * @param message The message to log.
	 * @throws NullPointerException if the given tag, context, and/or message is <code>null</code>.
	 * @see Log#e(String, String)
	 * @see Toast#makeText(Context, CharSequence, int)
	 */
	public static void error(final String tag, final Context context, final CharSequence message)
	{
		error(tag, context, message, null);
	}

	/**
	 * Logs and notifies the user of an error using a {@link Toast#LENGTH_LONG} toast. The tag is included in the toast.
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param context The current context.
	 * @param throwable The source of the error, providing an optional message and a stack trace.
	 * @throws NullPointerException if the given tag, context, and/or throwable is <code>null</code>.
	 * @see Log#e(String, String)
	 * @see Log#e(String, String, Throwable)
	 * @see Toast#makeText(Context, CharSequence, int)
	 */
	public static void error(final String tag, final Context context, final Throwable throwable)
	{
		error(tag, context, throwable.getMessage() != null ? throwable.getMessage() : "", throwable);
	}

	/**
	 * Logs and notifies the user of an error using a {@link Toast#LENGTH_LONG} toast. The tag is included in the toast. The {@link Throwable#getMessage()}, if
	 * any, will be appended to the given toast message.
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param context The current context.
	 * @param message The message to log.
	 * @param throwable The source of the error, providing a stack trace, or <code>null</code> if there is no available source throwable.
	 * @throws NullPointerException if the given tag, context, and/or message is <code>null</code>.
	 * @see Log#e(String, String)
	 * @see Log#e(String, String, Throwable)
	 * @see Toast#makeText(Context, CharSequence, int)
	 */
	public static void error(final String tag, final Context context, final CharSequence message, final Throwable throwable)
	{
		final String messageString = message.toString();
		if(throwable != null) //choose logging method based upon whether there is a throwable (unfortunately the Log.e() methods are written differently and do not delegate to one another)
		{
			Log.e(tag, messageString, throwable);
		}
		else
		{
			Log.e(tag, messageString);
		}
		final StringBuilder toastMessageBuilder = new StringBuilder(context.getResources().getString(R.string.error_label)); //Error
		toastMessageBuilder.append(": (").append(tag).append(')'); //: (tag)
		if(!messageString.isEmpty()) //add the message, if available
		{
			toastMessageBuilder.append(' ').append(messageString);
		}
		if(throwable != null) //add the throwable message, if available
		{
			final String throwableMessage = throwable.getMessage();
			if(throwableMessage != null && !throwableMessage.isEmpty())
			{
				toastMessageBuilder.append(' ').append(throwableMessage);
			}
		}
		runOnMainThread(new Runnable()
		{
			@Override
			public void run()
			{
				Toast.makeText(context, toastMessageBuilder, Toast.LENGTH_LONG).show(); //show the message in a toast on the main thread
			}
		});
	}

	/**
	 * Presents an audible, blank notification to the user for a few seconds. The current system time is used as the notification timestamp.
	 * <p>
	 * This method is useful as a convenience for providing an audible notification.
	 * </p>
	 * <p>
	 * If no icon is provided, the application's icon will be used. If the application has no icon, a default information icon will be used. The notification will
	 * be set to automatically cancel when clicked. The notification will be accompanied by the default sound and default LED lights. Vibration will only occur if
	 * the current application has vibration permission.
	 * </p>
	 * @param context The current context, the {@link Class#getSimpleName()} of which is also used as a log tag.
	 * @throws NullPointerException if the context is <code>null</code>.
	 * @throws IllegalArgumentException if the given duration is negative.
	 */
	public static void notify(final Context context)
	{
		notify(context, Milliseconds.fromSeconds(4)); //play the audio and a blank notification for several seconds
	}

	/**
	 * Presents an audible, blank notification to the user for a given duration. The current system time is used as the notification timestamp.
	 * <p>
	 * If no icon is provided, the application's icon will be used. If the application has no icon, a default information icon will be used. The notification will
	 * be set to automatically cancel when clicked. The notification will be accompanied by the default sound and default LED lights. Vibration will only occur if
	 * the current application has vibration permission.
	 * </p>
	 * @param context The current context, the {@link Class#getSimpleName()} of which is also used as a log tag.
	 * @param duration The length of time the notification should stay before automatically being dismissed, or {@link Long#MAX_VALUE} if the notification should
	 *          remain indefinitely.
	 * @throws NullPointerException if the context is <code>null</code>.
	 * @throws IllegalArgumentException if the given duration is negative.
	 */
	public static void notify(final Context context, final long duration)
	{
		notify(context, duration, null);
	}

	/**
	 * Presents an audible and visible notification to the user for a given duration, logging the notification text if any. The current system time is used as the
	 * notification timestamp. The ticker text, if any, will be used as the content text as well and logged as info.
	 * <p>
	 * If no icon is provided, the application's icon will be used. If the application has no icon, a default information icon will be used. The notification will
	 * be set to automatically cancel when clicked. The notification will be accompanied by the default sound and default LED lights. Vibration will only occur if
	 * the current application has vibration permission.
	 * </p>
	 * <p>
	 * If there is ticker text, that is used as info to be logged with {@link Log#i(String, String)}, in which case a tag must be provided.
	 * </p>
	 * @param context The current context, the {@link Class#getSimpleName()} of which is also used as a log tag.
	 * @param duration The length of time the notification should stay before automatically being dismissed, or {@link Long#MAX_VALUE} if the notification should
	 *          remain indefinitely.
	 * @param tickerTextResId The resource ID of the text to use as a ticker, or <code>0</code> to show no ticker text.
	 * @param formatArgs The format arguments that will be used for substitution of the resolved ticker text, if any.
	 * @throws NullPointerException if the context is <code>null</code>.
	 * @throws IllegalArgumentException if the given duration is negative.
	 * @see Log#i(String, String)
	 */
	public static void notify(final Context context, final long duration, final int tickerTextResId, final Object... formatArgs)
	{
		notify(context.getClass().getSimpleName(), context, duration, tickerTextResId, formatArgs);
	}

	/**
	 * Presents an audible and visible notification to the user for a given duration, logging the notification text if any. The current system time is used as the
	 * notification timestamp.
	 * <p>
	 * If no icon is provided, the application's icon will be used. If the application has no icon, a default information icon will be used. The notification will
	 * be set to automatically cancel when clicked. The notification will be accompanied by the default sound and default LED lights. Vibration will only occur if
	 * the current application has vibration permission.
	 * </p>
	 * <p>
	 * If no content text is provided, it will be taken from the given ticker text, if any. If no ticker text is given, the content text, or the content title if
	 * there is no content text, will be used as the ticker text. If there is effective ticker text (that is, there is either ticker text, a content title, or
	 * content text, in that order of priority), that is used as info to be logged with {@link Log#i(String, String)}, in which case a tag must be provided.
	 * </p>
	 * @param context The current context, the {@link Class#getSimpleName()} of which is also used as a log tag.
	 * @param duration The length of time the notification should stay before automatically being dismissed, or {@link Long#MAX_VALUE} if the notification should
	 *          remain indefinitely.
	 * @param contentTitleResId The resource ID of the title of the content of the notification, or <code>0</code> if there should be no content title.
	 * @param contentTextResId The resource ID of the text of the content of the notification, or <code>0</code> if there should be no content text.
	 * @param tickerTextResId The resource ID of the text to use as a ticker, or <code>0</code> to show no ticker text.
	 * @param formatArgs The format arguments that will be used for substitution of the resolved ticker text, if any.
	 * @throws NullPointerException if the context is <code>null</code>.
	 * @throws IllegalArgumentException if the given duration is negative.
	 * @see Log#i(String, String)
	 */
	public static void notify(final Context context, final long duration, final int contentTitleResId, final int contentTextResId, final int tickerTextResId,
			final Object... formatArgs)
	{
		notify(context.getClass().getSimpleName(), context, duration, contentTitleResId, contentTextResId, tickerTextResId, formatArgs);
	}

	/**
	 * Presents an audible and visible notification to the user for a given duration, logging the notification text if any. The current system time is used as the
	 * notification timestamp. The ticker text, if any, will be used as the content text as well and logged as info.
	 * <p>
	 * If no icon is provided, the application's icon will be used. If the application has no icon, a default information icon will be used. The notification will
	 * be set to automatically cancel when clicked. The notification will be accompanied by the default sound and default LED lights. Vibration will only occur if
	 * the current application has vibration permission.
	 * </p>
	 * <p>
	 * If there is ticker text, that is used as info to be logged with {@link Log#i(String, String)}, in which case a tag must be provided.
	 * </p>
	 * @param context The current context, the {@link Class#getSimpleName()} of which is also used as a log tag.
	 * @param duration The length of time the notification should stay before automatically being dismissed, or {@link Long#MAX_VALUE} if the notification should
	 *          remain indefinitely.
	 * @param tickerText The text to use as a ticker, or <code>null</code> to show no ticker text.
	 * @throws NullPointerException if the context is <code>null</code>.
	 * @throws IllegalArgumentException if the given duration is negative.
	 * @see Log#i(String, String)
	 */
	public static void notify(final Context context, final long duration, final CharSequence tickerText)
	{
		notify(context.getClass().getSimpleName(), context, duration, tickerText);
	}

	/**
	 * Presents an audible and visible notification to the user for a given duration, logging the notification text if any. The current system time is used as the
	 * notification timestamp.
	 * <p>
	 * If no icon is provided, the application's icon will be used. If the application has no icon, a default information icon will be used. The notification will
	 * be set to automatically cancel when clicked. The notification will be accompanied by the default sound and default LED lights. Vibration will only occur if
	 * the current application has vibration permission.
	 * </p>
	 * <p>
	 * If no content text is provided, it will be taken from the given ticker text, if any. If no ticker text is given, the content text, or the content title if
	 * there is no content text, will be used as the ticker text. If there is effective ticker text (that is, there is either ticker text, a content title, or
	 * content text, in that order of priority), that is used as info to be logged with {@link Log#i(String, String)}, in which case a tag must be provided.
	 * </p>
	 * @param context The current context, the {@link Class#getSimpleName()} of which is also used as a log tag.
	 * @param duration The length of time the notification should stay before automatically being dismissed, or {@link Long#MAX_VALUE} if the notification should
	 *          remain indefinitely.
	 * @param contentTitle The title of the content of the notification, or <code>null</code> if there should be no content title.
	 * @param contentText The text of the content of the notification, or <code>null</code> if there should be no content text.
	 * @param tickerText The text to use as a ticker, or <code>null</code> to show no ticker text.
	 * @throws NullPointerException if the context is <code>null</code>.
	 * @throws IllegalArgumentException if the given duration is negative.
	 * @see Log#i(String, String)
	 */
	public static void notify(final Context context, final long duration, CharSequence contentTitle, CharSequence contentText, CharSequence tickerText)
	{
		notify(context.getClass().getSimpleName(), context, duration, contentTitle, contentText, tickerText);
	}

	/**
	 * Presents an audible and visible notification to the user for a given duration, logging the notification text if any. The current system time is used as the
	 * notification timestamp. The ticker text, if any, will be used as the content text as well and logged as info.
	 * <p>
	 * If no icon is provided, the application's icon will be used. If the application has no icon, a default information icon will be used. The notification will
	 * be set to automatically cancel when clicked. The notification will be accompanied by the default sound and default LED lights. Vibration will only occur if
	 * the current application has vibration permission.
	 * </p>
	 * <p>
	 * If there is ticker text, that is used as info to be logged with {@link Log#i(String, String)}, in which case a tag must be provided.
	 * </p>
	 * @param tag Used to identify the source of the notification. It usually identifies the class or activity where the log call occurs. Required if there is a
	 *          non-<code>null</code> ticker text, content title, and/or content text.
	 * @param context The current context.
	 * @param duration The length of time the notification should stay before automatically being dismissed, or {@link Long#MAX_VALUE} if the notification should
	 *          remain indefinitely.
	 * @param tickerTextResId The resource ID of the text to use as a ticker, or <code>0</code> to show no ticker text.
	 * @param formatArgs The format arguments that will be used for substitution of the resolved ticker text, if any.
	 * @throws NullPointerException if the context is <code>null</code>, or if the tag is <code>null</code> and there is non-<code>null</code> ticker text.
	 * @throws IllegalArgumentException if the given duration is negative.
	 * @see Log#i(String, String)
	 */
	public static void notify(final String tag, final Context context, final long duration, final int tickerTextResId, final Object... formatArgs)
	{
		notify(tag, context, duration, 0, 0, tickerTextResId, formatArgs);
	}

	/**
	 * Presents an audible and visible notification to the user for a given duration, logging the notification text if any. The current system time is used as the
	 * notification timestamp.
	 * <p>
	 * If no icon is provided, the application's icon will be used. If the application has no icon, a default information icon will be used. The notification will
	 * be set to automatically cancel when clicked. The notification will be accompanied by the default sound and default LED lights. Vibration will only occur if
	 * the current application has vibration permission.
	 * </p>
	 * <p>
	 * If no content text is provided, it will be taken from the given ticker text, if any. If no ticker text is given, the content text, or the content title if
	 * there is no content text, will be used as the ticker text. If there is effective ticker text (that is, there is either ticker text, a content title, or
	 * content text, in that order of priority), that is used as info to be logged with {@link Log#i(String, String)}, in which case a tag must be provided.
	 * </p>
	 * @param tag Used to identify the source of the notification. It usually identifies the class or activity where the log call occurs. Required if there is a
	 *          non-<code>null</code> ticker text, content title, and/or content text.
	 * @param context The current context.
	 * @param duration The length of time the notification should stay before automatically being dismissed, or {@link Long#MAX_VALUE} if the notification should
	 *          remain indefinitely.
	 * @param contentTitleResId The resource ID of the title of the content of the notification, or <code>0</code> if there should be no content title.
	 * @param contentTextResId The resource ID of the text of the content of the notification, or <code>0</code> if there should be no content text.
	 * @param tickerTextResId The resource ID of the text to use as a ticker, or <code>0</code> to show no ticker text.
	 * @param formatArgs The format arguments that will be used for substitution of the resolved ticker text, if any.
	 * @throws NullPointerException if the context is <code>null</code>, or if the tag is <code>null</code> and there is a non-<code>null</code> ticker text,
	 *           content title, and/or content text.
	 * @throws IllegalArgumentException if the given duration is negative.
	 * @see Log#i(String, String)
	 */
	public static void notify(final String tag, final Context context, final long duration, final int contentTitleResId, final int contentTextResId,
			final int tickerTextResId, final Object... formatArgs)
	{
		final Resources resources = context.getResources(); //retrieve the values, if any from the resources
		final String contentTitle = contentTitleResId == 0 ? null : resources.getString(contentTitleResId);
		final String contentText = contentTextResId == 0 ? null : resources.getString(contentTextResId);
		final String tickerText = tickerTextResId == 0 ? null : (formatArgs.length > 0 ? resources.getString(tickerTextResId, formatArgs) : resources
				.getString(tickerTextResId));
		notify(tag, context, duration, contentTitle, contentText, tickerText);
	}

	/**
	 * Presents an audible and visible notification to the user for a given duration, logging the notification text if any. The current system time is used as the
	 * notification timestamp. The ticker text, if any, will be used as the content text as well and logged as info.
	 * <p>
	 * If no icon is provided, the application's icon will be used. If the application has no icon, a default information icon will be used. The notification will
	 * be set to automatically cancel when clicked. The notification will be accompanied by the default sound and default LED lights. Vibration will only occur if
	 * the current application has vibration permission.
	 * </p>
	 * <p>
	 * If there is ticker text, that is used as info to be logged with {@link Log#i(String, String)}, in which case a tag must be provided.
	 * </p>
	 * @param tag Used to identify the source of the notification. It usually identifies the class or activity where the log call occurs. Required if there is a
	 *          non-<code>null</code> ticker text, content title, and/or content text.
	 * @param context The current context.
	 * @param duration The length of time the notification should stay before automatically being dismissed, or {@link Long#MAX_VALUE} if the notification should
	 *          remain indefinitely.
	 * @param tickerText The text to use as a ticker, or <code>null</code> to show no ticker text.
	 * @throws NullPointerException if the context is <code>null</code>, or if the tag is <code>null</code> and there is non-<code>null</code> ticker text.
	 * @throws IllegalArgumentException if the given duration is negative.
	 * @see Log#i(String, String)
	 */
	public static void notify(final String tag, final Context context, final long duration, final CharSequence tickerText)
	{
		notify(tag, context, duration, null, null, tickerText);
	}

	/**
	 * Presents an audible and visible notification to the user for a given duration, logging the notification text if any. The current system time is used as the
	 * notification timestamp.
	 * <p>
	 * If no icon is provided, the application's icon will be used. If the application has no icon, a default information icon will be used. The notification will
	 * be set to automatically cancel when clicked. The notification will be accompanied by the default sound and default LED lights. Vibration will only occur if
	 * the current application has vibration permission.
	 * </p>
	 * <p>
	 * If no content text is provided, it will be taken from the given ticker text, if any. If no ticker text is given, the content text, or the content title if
	 * there is no content text, will be used as the ticker text. If there is effective ticker text (that is, there is either ticker text, a content title, or
	 * content text, in that order of priority), that is used as info to be logged with {@link Log#i(String, String)}, in which case a tag must be provided.
	 * </p>
	 * @param tag Used to identify the source of the notification. It usually identifies the class or activity where the log call occurs. Required if there is a
	 *          non-<code>null</code> ticker text, content title, and/or content text.
	 * @param context The current context.
	 * @param duration The length of time the notification should stay before automatically being dismissed, or {@link Long#MAX_VALUE} if the notification should
	 *          remain indefinitely.
	 * @param contentTitle The title of the content of the notification, or <code>null</code> if there should be no content title.
	 * @param contentText The text of the content of the notification, or <code>null</code> if there should be no content text.
	 * @param tickerText The text to use as a ticker, or <code>null</code> to show no ticker text.
	 * @throws NullPointerException if the context is <code>null</code>, or if the tag is <code>null</code> and there is a non-<code>null</code> ticker text,
	 *           content title, and/or content text.
	 * @throws IllegalArgumentException if the given duration is negative.
	 * @see Log#i(String, String)
	 */
	public static void notify(final String tag, final Context context, final long duration, CharSequence contentTitle, CharSequence contentText,
			CharSequence tickerText)
	{
		checkArgumentNotNegative(duration);
		if(contentText == null) //if no content text is given
		{
			contentText = tickerText; //use the ticker text, if any
		}
		if(tickerText == null) //if there was no given ticker text
		{
			tickerText = getInstance(contentText, contentTitle); //use the content text or, if there is no content text, the content title 
		}
		if(tickerText != null) //if there is log text
		{
			checkInstance(tag, "A tag must be provided if notification text is given.");
			Log.i(tag, tickerText.toString()); //log the ticker text as an info message
		}
		int icon = context.getApplicationInfo().icon; //get the application's icon
		if(icon == 0) //if the application has no icon
		{
			icon = android.R.drawable.ic_menu_info_details; //use a default Android info icon
		}
		final Notification notification = new Notification(icon, tickerText, System.currentTimeMillis()); //create a new notification
		final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(), 0); //create a dummy pending intent; see http://stackoverflow.com/questions/7040742/android-notification-manager-having-a-notification-without-an-intent
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent); //set the notification values
		notification.flags |= Notification.FLAG_AUTO_CANCEL; //automatically cancel the event when clicked
		notification.defaults |= Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS; //turn on sound and lights
		if(Applications.hasPermission(context, Manifest.permission.VIBRATE)) //if the application has vibrate permission
		{
			notification.defaults |= Notification.DEFAULT_VIBRATE; //turn on vibration, if the application has that permission
		}
		final int notificationID = generateNotificationID(); //generate a unique notification ID
		final NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE); //get the notification manager
		notificationManager.notify(notificationID, notification); //start the notification
		if(duration < Long.MAX_VALUE) //if we should limit the duration
		{
			MAIN_THREAD_HANDLER.postDelayed(new Runnable() //in the main thread, wait a while and then remove the notification
					{
						@Override
						public void run()
						{
							notificationManager.cancel(notificationID); //cancel the notification
						}
					}, duration); //delay the notification cancellation
		}
	}

}
