/*
 * Copyright © 2011-2013 GlobalMentor, Inc. <http://www.globalmentor.com/>
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

package com.globalmentor.android.net;

import static com.globalmentor.net.URIs.FILE_SCHEME;
import static com.globalmentor.net.URIs.FILE_URI_PATH_ROOT_PREFIX;

import java.io.File;
import java.net.*;

import com.globalmentor.io.Files;
import com.globalmentor.java.CharSequences;
import com.globalmentor.net.URIs;

import android.net.Uri;

/**
 * Utilities for working with Android {@link Uri}s. URI
 * <p>
 * Note that Android uses the file {@link Uri} form <code>file:///mnt/sdcard/...</code>, which is correct, instead of the Java {@link URI} form
 * <code>file:/mnt/sdcard/...</code>, which is incorrect.
 * </p>
 * @author Garret Wilson
 * @see Uri
 * @see <a href="http://blogs.msdn.com/b/ie/archive/2006/12/06/file-uris-in-windows.aspx">File URIs in Windows.</p>
 */
public class Uris
{

	/**
	 * Creates an Android URI from the given Java URI.
	 * <p>
	 * This method converts from the Java <code>file:/mnt/sdcard/...</code> form to the Android <code>file:///mnt/sdcard/...</code> form.
	 * </p>
	 * @param uri The Java URI instance.
	 * @return A new Android URI instance equivalent to the Java URI instance.
	 */
	public static Uri createUri(final URI uri)
	{
		String uriString = uri.toASCIIString(); //get the string version of the URI
		if(FILE_SCHEME.equals(uri.getScheme()) && URIs.isAbsolutePath(uri)) //if this was a file URI with an absolute path
		{
			final StringBuilder uriStringBuilder = new StringBuilder(uriString);
			uriStringBuilder.insert(FILE_SCHEME.length() + 1, FILE_URI_PATH_ROOT_PREFIX); //insert the special file URI prefix that Android expects
			uriString = uriStringBuilder.toString();

		}
		return Uri.parse(uriString);
	}

	/**
	 * Creates an Android URI from a file.
	 * <p>
	 * This ensures that the Android <code>file:///mnt/sdcard/...</code> form is used.
	 * </p>
	 * @param file The file which should be turned into an Android URI.
	 * @return An Android URI representation of the file.
	 */
	public static Uri fromFile(final File file)
	{
		return createUri(Files.toURI(file));
	}

	/**
	 * Creates a Java file from an Android URI.
	 * @param uri The URI to convert.
	 * @return The URI converted to a file.
	 * @throws NullPointerException if the given URI is <code>null</code>.
	 * @throws IllegalArgumentException if the given URI is not a valid file URI.
	 */
	public static File toFile(final Uri uri)
	{
		return new File(toURI(uri));
	}

	/**
	 * Creates a Java URL from an Android URI.
	 * @param uri The URI to convert.
	 * @return The Android URI converted to a file.
	 * @throws NullPointerException if the given URI is <code>null</code>.
	 * @throws IllegalArgumentException if the given URI is not a valid URL.
	 */
	public static URL toURL(final Uri uri)
	{
		try
		{
			return new URL(uri.toString());
		}
		catch(final MalformedURLException malformedURLException)
		{
			throw new IllegalArgumentException(malformedURLException);
		}
	}

	/**
	 * Converts an Android URI to a Java URI.
	 * <p>
	 * This method converts from the Android <code>file:///mnt/sdcard/...</code> form to the Java <code>file:/mnt/sdcard/...</code> form.
	 * </p>
	 * @param uri The Android URI to convert.
	 * @return A Java URI for the given Android URI.
	 * @throws IllegalArgumentException if the given Android URI does not represent a valid URI.
	 */
	public static URI toURI(final Uri uri)
	{
		String uriString = uri.toString(); //get the string version of the URI
		if(FILE_SCHEME.equals(uri.getScheme()) && CharSequences.equals(FILE_URI_PATH_ROOT_PREFIX, uriString, FILE_SCHEME.length() + 1)) //if this was a file URI starting with two forward slashes
		{
			final StringBuilder uriStringBuilder = new StringBuilder(uriString);
			uriStringBuilder.delete(FILE_SCHEME.length() + 1, FILE_SCHEME.length() + 1 + FILE_URI_PATH_ROOT_PREFIX.length()); //remove the Android file URI prefix
			uriString = uriStringBuilder.toString();

		}
		return URI.create(uri.toString());
	}
}
