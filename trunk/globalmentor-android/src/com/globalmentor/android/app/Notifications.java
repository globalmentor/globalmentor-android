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

import com.globalmentor.android.R;

import android.app.Notification;
import android.content.Context;
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
		info(tag, context, formatArgs.length > 0 ? context.getResources().getString(resId, formatArgs) : context.getResources().getString(resId, formatArgs));
	}

	/**
	 * Logs and notifies the user of information using a {@link Toast#LENGTH_SHORT} toast. The tag is not included in the toast.
	 * @param context The current context, the {@link Class#getSimpleName()} of which is also used as a log tag.
	 * @param message The message to log.
	 * @throws NullPointerException if the given context and/or message is <code>null</code>.
	 * @see Log#e(String, String)
	 * @see Toast#makeText(Context, CharSequence, int)
	 */
	public static void info(final Context context, final String message)
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
	public static void info(final String tag, final Context context, final String message)
	{
		Log.i(tag, message); //log the message
		runOnMainThread(new Runnable()
		{
			@Override
			public void run()
			{
				Toast.makeText(context, message, Toast.LENGTH_LONG); //show the message in a toast on the main thread
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
		error(tag, context, formatArgs.length > 0 ? context.getResources().getString(resId, formatArgs) : context.getResources().getString(resId, formatArgs),
				throwable);
	}

	/**
	 * Logs and notifies the user of an error using a {@link Toast#LENGTH_LONG} toast. The tag is included in the toast.
	 * @param context The current context, the {@link Class#getSimpleName()} of which is also used as a log tag.
	 * @param message The message to log.
	 * @throws NullPointerException if the given context and/or message is <code>null</code>.
	 * @see Log#e(String, String)
	 * @see Toast#makeText(Context, CharSequence, int)
	 */
	public static void error(final Context context, final String message)
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
	public static void error(final Context context, final String message, final Throwable throwable)
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
	public static void error(final String tag, final Context context, final String message)
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
	public static void error(final String tag, final Context context, final String message, final Throwable throwable)
	{
		if(throwable != null) //choose logging method based upon whether there is a throwable (unfortunately the Log.e() methods are written differently and do not delegate to one another)
		{
			Log.e(tag, message, throwable);
		}
		else
		{
			Log.e(tag, message);
		}
		final StringBuilder toastMessageBuilder = new StringBuilder(context.getResources().getString(R.string.error_label)); //Error
		toastMessageBuilder.append(": (").append(tag).append(')'); //: (tag)
		if(!message.isEmpty()) //add the message, if available
		{
			toastMessageBuilder.append(' ').append(message);
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
				Toast.makeText(context, toastMessageBuilder, Toast.LENGTH_LONG); //show the message in a toast on the main thread
			}
		});
	}

}
