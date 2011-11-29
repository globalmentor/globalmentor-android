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

import static android.os.Environment.*;
import static com.globalmentor.java.Objects.*;

import java.io.File;

import android.content.Context;
import android.os.Environment;

/**
 * Utility methods for accessing external storage.
 * 
 * @author Garret Wilson
 */
public class ExternalStorage
{

	/**
	 * @return <code>true</code> if the external storage is mounted with read/write access.
	 * @see Environment#getExternalStorageState()
	 * @see Environment#MEDIA_MOUNTED
	 */
	public static boolean isWritable()
	{
		return MEDIA_MOUNTED.equals(getExternalStorageState());
	}

	/**
	 * @return <code>true</code> if the external storage is mounted with read-only access.
	 * @see Environment#getExternalStorageState()
	 * @see Environment#MEDIA_MOUNTED_READ_ONLY
	 */
	public static boolean isReadOnly()
	{
		return MEDIA_MOUNTED.equals(getExternalStorageState());
	}

	/**
	 * @return <code>true</code> if the external storage is mounted with read/write or read-only access.
	 * @see Environment#getExternalStorageState()
	 * @see Environment#MEDIA_MOUNTED
	 * @see Environment#MEDIA_MOUNTED_READ_ONLY
	 */
	public static boolean isReadable()
	{
		return isWritable() || isReadable();
	}

	/**
	 * Retrieves a subdirectory in the external cache directory for the given context. If the external storage is writable, this method ensures that the returned
	 * subdirectory exists by creating it if necessary.
	 * @param context The context for which the external subdirectory should be returned.
	 * @param subdirectory The subdirectory to return of the external cache directory.
	 * @return The indicated subdirectory in the cache directory for the given context.
	 * @throws NullPointerException if the given context or subdirectory is <code>null</code>.
	 */
	public static File getExternalCacheDirectory(final Context context, final String subdirectory)
	{
		final File externalCacheSubdirectory = new File(context.getExternalCacheDir(), checkInstance(subdirectory)); //create a file for the subdirectory
		if(!externalCacheSubdirectory.isDirectory() && isWritable()) //if the subdirectory doesn't exist and the external storage is writable
		{
			externalCacheSubdirectory.mkdirs(); //try to create the required subdirectory
		}
		return externalCacheSubdirectory;
	}
}
