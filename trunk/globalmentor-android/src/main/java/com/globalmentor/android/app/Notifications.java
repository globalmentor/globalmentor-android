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
import static com.globalmentor.java.Characters.*;
import static com.globalmentor.java.Conditions.*;
import static com.globalmentor.java.Objects.*;

import java.util.concurrent.atomic.AtomicInteger;

import com.globalmentor.android.R;
import com.globalmentor.time.Milliseconds;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
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
public class Notifications {

	/** A click handler that does nothing. */
	private final static DialogInterface.OnClickListener NOP_ON_CLICK_HANDLER = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(final DialogInterface dialog, final int which) {
		}
	};

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
	public static int generateNotificationID() {
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
	public static void info(final Context context, final int resId, final Object... formatArgs) throws NotFoundException {
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
	public static void info(final int quantity, final Context context, final int resId, final Object... formatArgs) throws NotFoundException {
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
	public static void info(final String tag, final Context context, final int resId, final Object... formatArgs) throws NotFoundException {
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
	public static void info(final int quantity, final String tag, final Context context, final int resId, final Object... formatArgs) throws NotFoundException {
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
	public static void info(final Context context, final CharSequence message) {
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
	public static void info(final String tag, final Context context, final CharSequence message) {
		Log.i(tag, message.toString()); //log the message
		runOnMainThread(new Runnable() {

			@Override
			public void run() {
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
	public static void error(final Context context, final int resId, final Object... formatArgs) throws NotFoundException {
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
	public static void error(final int quantity, final Context context, final int resId, final Object... formatArgs) throws NotFoundException {
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
	public static void error(final Context context, final Throwable throwable, final int resId, final Object... formatArgs) throws NotFoundException {
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
			throws NotFoundException {
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
	public static void error(final String tag, final Context context, final int resId, final Object... formatArgs) throws NotFoundException {
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
	public static void error(final int quantity, final String tag, final Context context, final int resId, final Object... formatArgs) throws NotFoundException {
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
			throws NotFoundException {
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
			throws NotFoundException {
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
	public static void error(final Context context, final CharSequence message) {
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
	public static void error(final Context context, final Throwable throwable) {
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
	public static void error(final Context context, final CharSequence message, final Throwable throwable) {
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
	public static void error(final String tag, final Context context, final CharSequence message) {
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
	public static void error(final String tag, final Context context, final Throwable throwable) {
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
	public static void error(final String tag, final Context context, final CharSequence message, final Throwable throwable) {
		final String messageString = message.toString();
		if(throwable != null) { //choose logging method based upon whether there is a throwable (unfortunately the Log.e() methods are written differently and do not delegate to one another)
			Log.e(tag, messageString, throwable);
		} else {
			Log.e(tag, messageString);
		}
		final StringBuilder toastMessageBuilder = new StringBuilder(context.getResources().getString(R.string.error_label)); //Error
		toastMessageBuilder.append(": (").append(tag).append(')'); //: (tag)
		if(!messageString.isEmpty()) { //add the message, if available
			toastMessageBuilder.append(' ').append(messageString);
		}
		if(throwable != null) { //add the throwable message, if available
			final String throwableMessage = throwable.getMessage();
			if(throwableMessage != null && !throwableMessage.isEmpty()) {
				toastMessageBuilder.append(' ').append(throwableMessage);
			}
		}
		runOnMainThread(new Runnable() {

			@Override
			public void run() {
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
	public static void notify(final Context context) {
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
	public static void notify(final Context context, final long duration) {
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
	public static void notify(final Context context, final long duration, final int tickerTextResId, final Object... formatArgs) {
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
			final Object... formatArgs) {
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
	public static void notify(final Context context, final long duration, final CharSequence tickerText) {
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
	public static void notify(final Context context, final long duration, CharSequence contentTitle, CharSequence contentText, CharSequence tickerText) {
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
	public static void notify(final String tag, final Context context, final long duration, final int tickerTextResId, final Object... formatArgs) {
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
			final int tickerTextResId, final Object... formatArgs) {
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
	public static void notify(final String tag, final Context context, final long duration, final CharSequence tickerText) {
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
			CharSequence tickerText) {
		checkArgumentNotNegative(duration);
		if(contentText == null) { //if no content text is given
			contentText = tickerText; //use the ticker text, if any
		}
		if(tickerText == null) { //if there was no given ticker text
			tickerText = getInstance(contentText, contentTitle); //use the content text or, if there is no content text, the content title 
		}
		if(tickerText != null) { //if there is log text
			checkInstance(tag, "A tag must be provided if notification text is given.");
			Log.i(tag, tickerText.toString()); //log the ticker text as an info message
		}
		int icon = context.getApplicationInfo().icon; //get the application's icon
		if(icon == 0) { //if the application has no icon
			icon = android.R.drawable.ic_menu_info_details; //use a default Android info icon
		}
		final Notification notification = new Notification(icon, tickerText, System.currentTimeMillis()); //create a new notification
		final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(), 0); //create a dummy pending intent; see http://stackoverflow.com/questions/7040742/android-notification-manager-having-a-notification-without-an-intent
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent); //set the notification values
		notification.flags |= Notification.FLAG_AUTO_CANCEL; //automatically cancel the event when clicked
		notification.defaults |= Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS; //turn on sound and lights
		if(Applications.hasPermission(context, Manifest.permission.VIBRATE)) { //if the application has vibrate permission
			notification.defaults |= Notification.DEFAULT_VIBRATE; //turn on vibration, if the application has that permission
		}
		final int notificationID = generateNotificationID(); //generate a unique notification ID
		final NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE); //get the notification manager
		notificationManager.notify(notificationID, notification); //start the notification
		if(duration < Long.MAX_VALUE) { //if we should limit the duration
			MAIN_THREAD_HANDLER.postDelayed(new Runnable() { //in the main thread, wait a while and then remove the notification

						@Override
						public void run() {
							notificationManager.cancel(notificationID); //cancel the notification
						}
					}, duration); //delay the notification cancellation
		}
	}

	/**
	 * Asks the user a question via a dialog, using the question default icon, expecting a "yes"/"no" answer.
	 * @param context The current context.
	 * @param messageResId The resource ID of the message to show, or <code>0</code> if no message should be shown.
	 * @param yesOnClickListener The handler for the "yes" response.
	 * @param messageFormatArgs The format arguments that will be used for substitution in the message, if any.
	 * @throws NullPointerException if the given context and/or "yes" click listener is <code>null</code>.
	 */
	public static void ask(final Context context, final int messageResId, final DialogInterface.OnClickListener yesOnClickListener,
			final Object... messageFormatArgs) {
		ask(context, messageResId, 0, yesOnClickListener, messageFormatArgs);
	}

	/**
	 * Asks the user a question via a dialog, using the question default icon, expecting a "yes"/"no" answer.
	 * @param context The current context.
	 * @param messageResId The resource ID of the message to show, or <code>0</code> if no message should be shown.
	 * @param titleResId The resource ID title to use, or <code>0</code> if no title should be used.
	 * @param yesOnClickListener The handler for the "yes" response.
	 * @param messageFormatArgs The format arguments that will be used for substitution in the message, if any.
	 * @throws NullPointerException if the given context and/or "yes" click listener is <code>null</code>.
	 */
	public static void ask(final Context context, final int messageResId, final int titleResId, final DialogInterface.OnClickListener yesOnClickListener,
			final Object... messageFormatArgs) {
		ask(context, messageResId, titleResId, 0, yesOnClickListener, messageFormatArgs);
	}

	/**
	 * Asks the user a question via a dialog, expecting a "yes"/"no" answer.
	 * @param context The current context.
	 * @param messageResId The resource ID of the message to show, or <code>0</code> if no message should be shown.
	 * @param titleResId The resource ID title to use, or <code>0</code> if no title should be used.
	 * @param iconResId The resource ID of the icon to use, or <code>0</code> if a default question icon should be used.
	 * @param yesOnClickListener The handler for the "yes" response.
	 * @param messageFormatArgs The format arguments that will be used for substitution in the message, if any.
	 * @throws NullPointerException if the given context and/or "yes" click listener is <code>null</code>.
	 */
	public static void ask(final Context context, final int messageResId, final int titleResId, final int iconResId,
			final DialogInterface.OnClickListener yesOnClickListener, final Object... messageFormatArgs) {
		ask(context, messageResId, titleResId, iconResId, yesOnClickListener, null, messageFormatArgs);
	}

	/**
	 * Asks the user a question via a dialog, expecting a "yes"/"no" answer.
	 * @param context The current context.
	 * @param messageResId The resource ID of the message to show, or <code>0</code> if no message should be shown.
	 * @param titleResId The resource ID title to use, or <code>0</code> if no title should be used.
	 * @param iconResId The resource ID of the icon to use, or <code>0</code> if a default question icon should be used.
	 * @param yesOnClickListener The handler for the "yes" response.
	 * @param noOnClickListener The handler for the "no" response, or <code>null</code> if no special action should be taken if the user selects "no".
	 * @param messageFormatArgs The format arguments that will be used for substitution in the message, if any.
	 * @throws NullPointerException if the given context and/or "yes" click listener is <code>null</code>.
	 */
	public static void ask(final Context context, final int messageResId, final int titleResId, final int iconResId,
			final DialogInterface.OnClickListener yesOnClickListener, final DialogInterface.OnClickListener noOnClickListener, final Object... messageFormatArgs) {
		final Resources resources = context.getResources(); //retrieve the values, if any from the resources
		final String message = messageResId == 0 ? null : (messageFormatArgs.length > 0 ? resources.getString(messageResId, messageFormatArgs) : resources
				.getString(messageResId));
		final String title = titleResId == 0 ? null : resources.getString(titleResId);
		final Drawable icon = iconResId == 0 ? null : resources.getDrawable(iconResId);
		ask(context, message, title, icon, yesOnClickListener, noOnClickListener);
	}

	/**
	 * Asks the user a question via a dialog, using the question default icon, expecting a "yes"/"no" answer.
	 * @param context The current context.
	 * @param message The message to show, or <code>null</code> if no message should be shown.
	 * @param yesOnClickListener The handler for the "yes" response.
	 * @throws NullPointerException if the given context and/or "yes" click listener is <code>null</code>.
	 */
	public static void ask(final Context context, final CharSequence message, final DialogInterface.OnClickListener yesOnClickListener) {
		ask(context, message, null, yesOnClickListener);
	}

	/**
	 * Asks the user a question via a dialog, using the question default icon, expecting a "yes"/"no" answer.
	 * @param context The current context.
	 * @param message The message to show, or <code>null</code> if no message should be shown.
	 * @param title The title to use, or <code>null</code> if no title should be used.
	 * @param yesOnClickListener The handler for the "yes" response.
	 * @throws NullPointerException if the given context and/or "yes" click listener is <code>null</code>.
	 */
	public static void ask(final Context context, final CharSequence message, final CharSequence title, final DialogInterface.OnClickListener yesOnClickListener) {
		ask(context, message, title, null, yesOnClickListener);
	}

	/**
	 * Asks the user a question via a dialog, expecting a "yes"/"no" answer.
	 * @param context The current context.
	 * @param message The message to show, or <code>null</code> if no message should be shown.
	 * @param title The title to use, or <code>null</code> if no title should be used.
	 * @param icon The icon to use, or <code>null</code> if a default question icon should be used.
	 * @param yesOnClickListener The handler for the "yes" response.
	 * @throws NullPointerException if the given context and/or "yes" click listener is <code>null</code>.
	 */
	public static void ask(final Context context, final CharSequence message, final CharSequence title, final Drawable icon,
			final DialogInterface.OnClickListener yesOnClickListener) {
		ask(context, message, title, icon, yesOnClickListener, null);
	}

	/**
	 * Asks the user a question via a dialog, expecting a "yes"/"no" answer.
	 * @param context The current context.
	 * @param message The message to show, or <code>null</code> if no message should be shown.
	 * @param title The title to use, or <code>null</code> if no title should be used.
	 * @param icon The icon to use, or <code>null</code> if a default question icon should be used.
	 * @param yesOnClickListener The handler for the "yes" response.
	 * @param noOnClickListener The handler for the "no" response, or <code>null</code> if no special action should be taken if the user selects "no".
	 * @throws NullPointerException if the given context and/or "yes" click listener is <code>null</code>.
	 */
	public static void ask(final Context context, final CharSequence message, final CharSequence title, Drawable icon,
			final DialogInterface.OnClickListener yesOnClickListener, DialogInterface.OnClickListener noOnClickListener) {
		if(icon == null) { //use a default icon if none is given
			icon = context.getResources().getDrawable(android.R.drawable.ic_menu_help);
		}
		final Resources resources = context.getResources();
		//use our own yes/no strings, because Android as of 4.2 maps them to "OK" and Cancel", which have different semantics
		//see http://code.google.com/p/android/issues/detail?id=3713
		final String yesText = resources.getString(R.string.yes);
		final String noText = resources.getString(R.string.no); //always provide a "no" option when asking
		alert(context, message, title, icon, yesText, yesOnClickListener, noText, noOnClickListener);
	}

	/**
	 * Confirms something with the user via a dialog, using the question default icon, expecting an "OK"/"cancel" answer.
	 * @param context The current context.
	 * @param messageResId The resource ID of the message to show, or <code>0</code> if no message should be shown.
	 * @param okOnClickListener The handler for the "OK" response.
	 * @param messageFormatArgs The format arguments that will be used for substitution in the message, if any.
	 * @throws NullPointerException if the given context and/or "OK" click listener is <code>null</code>.
	 */
	public static void confirm(final Context context, final int messageResId, final DialogInterface.OnClickListener okOnClickListener,
			final Object... messageFormatArgs) {
		confirm(context, messageResId, 0, okOnClickListener, messageFormatArgs);
	}

	/**
	 * Confirms something with the user via a dialog, using the question default icon, expecting an "OK"/"cancel" answer.
	 * @param context The current context.
	 * @param messageResId The resource ID of the message to show, or <code>0</code> if no message should be shown.
	 * @param titleResId The resource ID title to use, or <code>0</code> if no title should be used.
	 * @param okOnClickListener The handler for the "OK" response.
	 * @param messageFormatArgs The format arguments that will be used for substitution in the message, if any.
	 * @throws NullPointerException if the given context and/or "OK" click listener is <code>null</code>.
	 */
	public static void confirm(final Context context, final int messageResId, final int titleResId, final DialogInterface.OnClickListener okOnClickListener,
			final Object... messageFormatArgs) {
		confirm(context, messageResId, titleResId, 0, okOnClickListener, messageFormatArgs);
	}

	/**
	 * Confirms something with the user via a dialog, expecting an "OK"/"cancel" answer.
	 * @param context The current context.
	 * @param messageResId The resource ID of the message to show, or <code>0</code> if no message should be shown.
	 * @param titleResId The resource ID title to use, or <code>0</code> if no title should be used.
	 * @param iconResId The resource ID of the icon to use, or <code>0</code> if a default question icon should be used.
	 * @param okOnClickListener The handler for the "OK" response.
	 * @param messageFormatArgs The format arguments that will be used for substitution in the message, if any.
	 * @throws NullPointerException if the given context and/or "OK" click listener is <code>null</code>.
	 */
	public static void confirm(final Context context, final int messageResId, final int titleResId, final int iconResId,
			final DialogInterface.OnClickListener okOnClickListener, final Object... messageFormatArgs) {
		confirm(context, messageResId, titleResId, iconResId, okOnClickListener, null, messageFormatArgs);
	}

	/**
	 * Confirms something with the user via a dialog, expecting an "OK"/"cancel" answer.
	 * @param context The current context.
	 * @param messageResId The resource ID of the message to show, or <code>0</code> if no message should be shown.
	 * @param titleResId The resource ID title to use, or <code>0</code> if no title should be used.
	 * @param iconResId The resource ID of the icon to use, or <code>0</code> if a default question icon should be used.
	 * @param okOnClickListener The handler for the "OK" response.
	 * @param cancelOnClickListener The handler for the "cancel" response, or <code>null</code> if no special action should be taken if the user selects "cancel".
	 * @param messageFormatArgs The format arguments that will be used for substitution in the message, if any.
	 * @throws NullPointerException if the given context and/or "OK" click listener is <code>null</code>.
	 */
	public static void confirm(final Context context, final int messageResId, final int titleResId, final int iconResId,
			final DialogInterface.OnClickListener okOnClickListener, final DialogInterface.OnClickListener cancelOnClickListener, final Object... messageFormatArgs) {
		final Resources resources = context.getResources(); //retrieve the values, if any from the resources
		final String message = messageResId == 0 ? null : (messageFormatArgs.length > 0 ? resources.getString(messageResId, messageFormatArgs) : resources
				.getString(messageResId));
		final String title = titleResId == 0 ? null : resources.getString(titleResId);
		final Drawable icon = iconResId == 0 ? null : resources.getDrawable(iconResId);
		confirm(context, message, title, icon, okOnClickListener, cancelOnClickListener);
	}

	/**
	 * Confirms something with the user via a dialog, using the question default icon, expecting an "OK"/"cancel" answer.
	 * @param context The current context.
	 * @param message The message to show, or <code>null</code> if no message should be shown.
	 * @param okOnClickListener The handler for the "OK" response.
	 * @throws NullPointerException if the given context and/or "OK" click listener is <code>null</code>.
	 */
	public static void confirm(final Context context, final CharSequence message, final DialogInterface.OnClickListener okOnClickListener) {
		confirm(context, message, null, okOnClickListener);
	}

	/**
	 * Confirms something with the user via a dialog, using the question default icon, expecting an "OK"/"cancel" answer.
	 * @param context The current context.
	 * @param message The message to show, or <code>null</code> if no message should be shown.
	 * @param title The title to use, or <code>null</code> if no title should be used.
	 * @param okOnClickListener The handler for the "OK" response.
	 * @throws NullPointerException if the given context and/or "OK" click listener is <code>null</code>.
	 */
	public static void confirm(final Context context, final CharSequence message, final CharSequence title,
			final DialogInterface.OnClickListener okOnClickListener) {
		confirm(context, message, title, null, okOnClickListener);
	}

	/**
	 * Confirms something with the user via a dialog, expecting an "OK"/"cancel" answer.
	 * @param context The current context.
	 * @param message The message to show, or <code>null</code> if no message should be shown.
	 * @param title The title to use, or <code>null</code> if no title should be used.
	 * @param icon The icon to use, or <code>null</code> if a default question icon should be used.
	 * @param okOnClickListener The handler for the "OK" response.
	 * @throws NullPointerException if the given context and/or "OK" click listener is <code>null</code>.
	 */
	public static void confirm(final Context context, final CharSequence message, final CharSequence title, final Drawable icon,
			final DialogInterface.OnClickListener okOnClickListener) {
		confirm(context, message, title, icon, okOnClickListener, null);
	}

	/**
	 * Confirms something with the user via a dialog, expecting an "OK"/"cancel" answer.
	 * @param context The current context.
	 * @param message The message to show, or <code>null</code> if no message should be shown.
	 * @param title The title to use, or <code>null</code> if no title should be used.
	 * @param icon The icon to use, or <code>null</code> if a default question icon should be used.
	 * @param okOnClickListener The handler for the "OK" response.
	 * @param cancelOnClickListener The handler for the "cancel" response, or <code>null</code> if no special action should be taken if the user selects "cancel".
	 * @throws NullPointerException if the given context and/or "OK" click listener is <code>null</code>.
	 */
	public static void confirm(final Context context, final CharSequence message, final CharSequence title, Drawable icon,
			final DialogInterface.OnClickListener okOnClickListener, DialogInterface.OnClickListener cancelOnClickListener) {
		if(icon == null) { //use a default icon if none is given
			icon = context.getResources().getDrawable(android.R.drawable.ic_menu_help);
		}
		final Resources resources = context.getResources();
		final String okText = resources.getString(android.R.string.ok);
		final String cancelText = resources.getString(android.R.string.cancel); //always provide a "cancel" option when confirming
		alert(context, message, title, icon, okText, okOnClickListener, cancelText, cancelOnClickListener);
	}

	/**
	 * Alerts the user to something via a dialog, using the information default icon.
	 * @param context The current context.
	 * @param messageResId The resource ID of the message to show, or <code>0</code> if no message should be shown.
	 * @param messageFormatArgs The format arguments that will be used for substitution in the message, if any.
	 * @throws NullPointerException if the given context is <code>null</code>.
	 */
	public static void alert(final Context context, final int messageResId, final Object... messageFormatArgs) {
		alert(context, messageResId, 0, messageFormatArgs);
	}

	/**
	 * Alerts the user to something via a dialog, using the information default icon.
	 * @param context The current context.
	 * @param messageResId The resource ID of the message to show, or <code>0</code> if no message should be shown.
	 * @param titleResId The resource ID title to use, or <code>0</code> if no title should be used.
	 * @param messageFormatArgs The format arguments that will be used for substitution in the message, if any.
	 * @throws NullPointerException if the given context is <code>null</code>.
	 */
	public static void alert(final Context context, final int messageResId, final int titleResId, final Object... messageFormatArgs) {
		alert(context, messageResId, titleResId, 0, messageFormatArgs);
	}

	/**
	 * Alerts the user to something via a dialog, using the information default icon.
	 * @param context The current context.
	 * @param messageResId The resource ID of the message to show, or <code>0</code> if no message should be shown.
	 * @param okOnClickListener The handler for the "OK" response.
	 * @param messageFormatArgs The format arguments that will be used for substitution in the message, if any.
	 * @throws NullPointerException if the given context and/or "OK" click listener is <code>null</code>.
	 */
	public static void alert(final Context context, final int messageResId, final DialogInterface.OnClickListener okOnClickListener,
			final Object... messageFormatArgs) {
		alert(context, messageResId, 0, okOnClickListener, messageFormatArgs);
	}

	/**
	 * Alerts the user to something via a dialog, using the information default icon.
	 * @param context The current context.
	 * @param messageResId The resource ID of the message to show, or <code>0</code> if no message should be shown.
	 * @param titleResId The resource ID title to use, or <code>0</code> if no title should be used.
	 * @param okOnClickListener The handler for the "OK" response.
	 * @param messageFormatArgs The format arguments that will be used for substitution in the message, if any.
	 * @throws NullPointerException if the given context and/or "OK" click listener is <code>null</code>.
	 */
	public static void alert(final Context context, final int messageResId, final int titleResId, final DialogInterface.OnClickListener okOnClickListener,
			final Object... messageFormatArgs) {
		alert(context, messageResId, titleResId, 0, okOnClickListener, messageFormatArgs);
	}

	/**
	 * Alerts the user to something via a dialog.
	 * @param context The current context.
	 * @param messageResId The resource ID of the message to show, or <code>0</code> if no message should be shown.
	 * @param titleResId The resource ID title to use, or <code>0</code> if no title should be used.
	 * @param iconResId The resource ID of the icon to use, or <code>0</code> if a default information icon should be used.
	 * @param messageFormatArgs The format arguments that will be used for substitution in the message, if any.
	 * @throws NullPointerException if the given context is <code>null</code>.
	 */
	public static void alert(final Context context, final int messageResId, final int titleResId, final int iconResId, final Object... messageFormatArgs) {
		alert(context, messageResId, titleResId, iconResId, NOP_ON_CLICK_HANDLER, messageFormatArgs);
	}

	/**
	 * Alerts the user to something via a dialog.
	 * @param context The current context.
	 * @param messageResId The resource ID of the message to show, or <code>0</code> if no message should be shown.
	 * @param titleResId The resource ID title to use, or <code>0</code> if no title should be used.
	 * @param iconResId The resource ID of the icon to use, or <code>0</code> if a default information icon should be used.
	 * @param okOnClickListener The handler for the "OK" response.
	 * @param messageFormatArgs The format arguments that will be used for substitution in the message, if any.
	 * @throws NullPointerException if the given context and/or "OK" click listener is <code>null</code>.
	 */
	public static void alert(final Context context, final int messageResId, final int titleResId, final int iconResId,
			final DialogInterface.OnClickListener okOnClickListener, final Object... messageFormatArgs) {
		final Resources resources = context.getResources(); //retrieve the values, if any from the resources
		final String message = messageResId == 0 ? null : (messageFormatArgs.length > 0 ? resources.getString(messageResId, messageFormatArgs) : resources
				.getString(messageResId));
		final String title = titleResId == 0 ? null : resources.getString(titleResId);
		final Drawable icon = iconResId == 0 ? null : resources.getDrawable(iconResId);
		alert(context, message, title, icon, okOnClickListener);
	}

	/**
	 * Alerts the user to something via a dialog, using the information default icon.
	 * @param context The current context.
	 * @param message The message to show, or <code>null</code> if no message should be shown.
	 * @throws NullPointerException if the given context is <code>null</code>.
	 */
	public static void alert(final Context context, final CharSequence message) {
		alert(context, message, (CharSequence)null);
	}

	/**
	 * Alerts the user to something via a dialog, using the information default icon.
	 * @param context The current context.
	 * @param message The message to show, or <code>null</code> if no message should be shown.
	 * @param title The title to use, or <code>null</code> if no title should be used.
	 * @throws NullPointerException if the given context is <code>null</code>.
	 */
	public static void alert(final Context context, final CharSequence message, final CharSequence title) {
		alert(context, message, title, NOP_ON_CLICK_HANDLER);
	}

	/**
	 * Alerts the user to something via a dialog, using the information default icon.
	 * @param context The current context.
	 * @param message The message to show, or <code>null</code> if no message should be shown.
	 * @param okOnClickListener The handler for the "OK" response.
	 * @throws NullPointerException if the given context and/or "OK" click listener is <code>null</code>.
	 */
	public static void alert(final Context context, final CharSequence message, final DialogInterface.OnClickListener okOnClickListener) {
		alert(context, message, null, okOnClickListener);
	}

	/**
	 * Alerts the user to something via a dialog, using the information default icon.
	 * @param context The current context.
	 * @param message The message to show, or <code>null</code> if no message should be shown.
	 * @param title The title to use, or <code>null</code> if no title should be used.
	 * @param okOnClickListener The handler for the "OK" response.
	 * @throws NullPointerException if the given context and/or "OK" click listener is <code>null</code>.
	 */
	public static void alert(final Context context, final CharSequence message, final CharSequence title, final DialogInterface.OnClickListener okOnClickListener) {
		alert(context, message, title, null, okOnClickListener);
	}

	/**
	 * Alerts the user to something via a dialog.
	 * @param context The current context.
	 * @param message The message to show, or <code>null</code> if no message should be shown.
	 * @param title The title to use, or <code>null</code> if no title should be used.
	 * @param icon The icon to use, or <code>null</code> if a default information icon should be used.
	 * @throws NullPointerException if the given context is <code>null</code>.
	 */
	public static void alert(final Context context, final CharSequence message, final CharSequence title, Drawable icon) {
		alert(context, message, title, icon, null);
	}

	/**
	 * Alerts the user to something via a dialog.
	 * @param context The current context.
	 * @param message The message to show, or <code>null</code> if no message should be shown.
	 * @param title The title to use, or <code>null</code> if no title should be used.
	 * @param icon The icon to use, or <code>null</code> if a default information icon should be used.
	 * @param okOnClickListener The handler for the "OK" response.
	 * @throws NullPointerException if the given context and/or "OK" click listener is <code>null</code>.
	 */
	public static void alert(final Context context, final CharSequence message, final CharSequence title, Drawable icon,
			final DialogInterface.OnClickListener okOnClickListener) {
		if(icon == null) { //use a default icon if none is given
			icon = context.getResources().getDrawable(android.R.drawable.ic_menu_info_details);
		}
		final Resources resources = context.getResources();
		final String okText = resources.getString(android.R.string.ok);
		alert(context, message, title, icon, okText, okOnClickListener);
	}

	/**
	 * Alerts the user to something via a dialog, with a positive response.
	 * @param context The current context.
	 * @param messageResId The resource ID of the message to show, or <code>0</code> if no message should be shown.
	 * @param titleResId The resource ID title to use, or <code>0</code> if no title should be used.
	 * @param iconResId The resource ID of the icon to use, or <code>0</code> if no icon should be used.
	 * @param positiveTextResId The text for the positive option.
	 * @param positiveOnClickListener The handler for the positive response.
	 * @param messageFormatArgs The format arguments that will be used for substitution in the message, if any.
	 * @throws NullPointerException if the given context and/or positive click listener is <code>null</code>.
	 */
	public static void alert(final Context context, final int messageResId, final int titleResId, final int iconResId, final int positiveTextResId,
			final DialogInterface.OnClickListener positiveOnClickListener, final Object... messageFormatArgs) {
		alert(context, messageResId, titleResId, iconResId, positiveTextResId, positiveOnClickListener, messageFormatArgs);
	}

	/**
	 * Alerts the user to something via a dialog, with a positive and optional negative response.
	 * @param context The current context.
	 * @param messageResId The resource ID of the message to show, or <code>0</code> if no message should be shown.
	 * @param titleResId The resource ID title to use, or <code>0</code> if no title should be used.
	 * @param iconResId The resource ID of the icon to use, or <code>0</code> if no icon should be used.
	 * @param positiveTextResId The text for the positive option.
	 * @param positiveOnClickListener The handler for the positive response.
	 * @param negativeTextResId The text for the negative option, or <code>0</code> if no negative option should be shown.
	 * @param negativeOnClickListener The handler for the negative response, or <code>null</code> if no special action should be taken if the user selects the
	 *          negative option.
	 * @param messageFormatArgs The format arguments that will be used for substitution in the message, if any.
	 * @throws NullPointerException if the given context and/or positive click listener is <code>null</code>.
	 */
	public static void alert(final Context context, final int messageResId, final int titleResId, final int iconResId, final int positiveTextResId,
			final DialogInterface.OnClickListener positiveOnClickListener, final int negativeTextResId,
			final DialogInterface.OnClickListener negativeOnClickListener, final Object... messageFormatArgs) {
		final Resources resources = context.getResources(); //retrieve the values, if any from the resources
		final String message = messageResId == 0 ? null : (messageFormatArgs.length > 0 ? resources.getString(messageResId, messageFormatArgs) : resources
				.getString(messageResId));
		final String title = titleResId == 0 ? null : resources.getString(titleResId);
		final Drawable icon = iconResId == 0 ? null : resources.getDrawable(iconResId);
		final String positiveText = positiveTextResId == 0 ? null : resources.getString(positiveTextResId); //we'll check for null in the delegate method
		final String negativeText = negativeTextResId == 0 ? null : resources.getString(negativeTextResId);
		alert(context, message, title, icon, positiveText, positiveOnClickListener, negativeText, negativeOnClickListener);
	}

	/**
	 * Alerts the user to something via a dialog.
	 * @param context The current context.
	 * @param message The message to show, or <code>null</code> if no message should be shown.
	 * @param title The title to use, or <code>null</code> if no title should be used.
	 * @param icon The icon to use, or <code>null</code> if no icon should be used.
	 * @param positiveText The text for the positive option.
	 * @param positiveOnClickListener The handler for the positive response.
	 * @throws NullPointerException if the given context and/or positive click listener is <code>null</code>.
	 */
	public static void alert(final Context context, final CharSequence message, final CharSequence title, final Drawable icon, final CharSequence positiveText,
			final DialogInterface.OnClickListener positiveOnClickListener) {
		alert(context, message, title, icon, positiveText, positiveOnClickListener, null, null);
	}

	/**
	 * Alerts the user to something via a dialog, with a positive and optional negative response.
	 * <p>
	 * If no title is provided but an icon is given, a dummy title of a non-breaking space will be used so that the icon will appear.
	 * </p>
	 * <p>
	 * If a specific negative on-click listener is provided, canceling the dialog by using the back button will be prevented to ensure canceling does not occur
	 * without the caller's listener being invoked.
	 * </p>
	 * @param context The current context.
	 * @param message The message to show, or <code>null</code> if no message should be shown.
	 * @param title The title to use, or <code>null</code> if no title should be used.
	 * @param icon The icon to use, or <code>null</code> if no icon should be used.
	 * @param positiveText The text for the positive option.
	 * @param positiveOnClickListener The handler for the positive response.
	 * @param negativeText The text for the negative option, or <code>null</code> if no negative option should be shown.
	 * @param negativeOnClickListener The handler for the negative response, or <code>null</code> if no special action should be taken if the user selects the
	 *          negative option.
	 * @throws NullPointerException if the given context and/or positive click listener is <code>null</code>.
	 */
	public static void alert(final Context context, final CharSequence message, CharSequence title, final Drawable icon, final CharSequence positiveText,
			final DialogInterface.OnClickListener positiveOnClickListener, final CharSequence negativeText, DialogInterface.OnClickListener negativeOnClickListener) {
		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		if(message != null) {
			alertDialogBuilder.setMessage(message);
		}
		if(icon != null) {
			alertDialogBuilder.setIcon(icon);
			if(title == null) { //if there is no title, provide an empty title so the icon will appear
				title = String.valueOf(NO_BREAK_SPACE_CHAR);
			}
		}
		if(title != null) {
			alertDialogBuilder.setTitle(title);
		}
		alertDialogBuilder.setIcon(icon); //there will always be an icon, if only because we use a default one
		alertDialogBuilder.setPositiveButton(positiveText, checkInstance(positiveOnClickListener, "A response handler must be provided for the positive option."));
		boolean cancelable = true; //default to allowing the dialog to be cancelable
		if(negativeText != null) {
			if(negativeOnClickListener != null) { //if the caller wants to do something specifically when the negative option is chosen
				cancelable = false; //don't allow the back button to cancel the dialog, ensuring that the negative on-click listener gets called
			} else { //if no negative on-click listener was provided
				negativeOnClickListener = NOP_ON_CLICK_HANDLER; //use a dummy listener
			}
			alertDialogBuilder.setNegativeButton(negativeText, negativeOnClickListener);
		}
		final AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.setCancelable(cancelable); //specify whether the dialog can be canceled
		runOnMainThread(new Runnable() {

			@Override
			public void run() {
				alertDialog.show();
			}
		});
	}

}
