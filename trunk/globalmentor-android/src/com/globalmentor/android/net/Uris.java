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

package com.globalmentor.android.net;

import java.io.File;
import java.net.*;

import android.net.Uri;

/**
 * Utilities for working with Android {@link Uri}s.
 * 
 * @author Garret Wilson
 * 
 * @see Uri
 */
public class Uris
{

	/**
	 * Creates a Java file from an Android URI.
	 * @param uri The URI to convert.
	 * @return The URI converted to a file.
	 * @throws NullPointerException if the given URI is <code>null</code>.
	 * @throws IllegalArgumentException if the given URI is not a valid file URI.
	 */
	public static File toFile(final Uri uri)
	{
		return new File(URI.create(uri.toString()));
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

}
