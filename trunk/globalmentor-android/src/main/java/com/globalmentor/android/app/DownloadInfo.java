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

import static android.app.DownloadManager.*;
import static com.globalmentor.java.Objects.*;

import android.app.DownloadManager;
import android.database.Cursor;
import android.net.Uri;

/**
 * Provides convenient access to information from a download from the Android {@link DownloadManager}.
 * 
 * @author Garret Wilson
 * 
 * @see DownloadManager
 */
public class DownloadInfo
{
	/** An identifier for the download, unique across the system. */
	public final long id;

	/** The URI to be downloaded. */
	public final Uri uri;

	/** The client-supplied title for this download. */
	public final String title;

	/** The client-supplied description of this download. */
	public final String description;

	/** Current status of the download, as one of the <code>STATUS_*</code> constants. */
	public final int status;

	/** More detail on the status of the download. */
	public final String reason;

	/**
	 * Constructor.
	 * @param id An identifier for the download, unique across the system.
	 * @param uri The URI to be downloaded.
	 * @param title The client-supplied title for this download.
	 * @param description The client-supplied description of this download.
	 * @param status Current status of the download, as one of the <code>STATUS_*</code> constants.
	 * @param reason More detail on the status of the download.
	 * @throws NullPointerException if one of the given references is <code>null</code>.
	 */
	protected DownloadInfo(final long id, final Uri uri, final String title, final String description, final int status, final String reason)
	{
		this.id = checkInstance(id);
		this.uri = checkInstance(uri);
		this.title = checkInstance(title);
		this.description = checkInstance(description);
		this.status = status;
		this.reason = checkInstance(reason);
	}

	/**
	 * Retrieves download information from a given cursor of download information rows. The cursor must already be positioned at the appropriate row.
	 * @param cursor The cursor from which the download information should be retrieved.
	 * @return The retrieved download information.
	 */
	public static DownloadInfo getInfo(final Cursor cursor)
	{
		return new DownloadInfo(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)), Uri.parse(cursor.getString(cursor.getColumnIndex(COLUMN_URI))),
				cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)), cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)), cursor.getInt(cursor
						.getColumnIndex(DownloadManager.COLUMN_STATUS)), cursor.getString(cursor.getColumnIndex(COLUMN_REASON)));

	}

	/**
	 * Retrieves download information from the download identified by the given ID.
	 * @param downloadManager The download manager to query.
	 * @param downloadID The ID of the download to query
	 * @return Information on the identified download, or <code>null</code> if no info on the identified download could be found.
	 */
	public static DownloadInfo getInfo(final DownloadManager downloadManager, final long downloadID)
	{
		final Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(downloadID));
		try
		{
			if(!cursor.moveToFirst())
			{
				return null; //we can't find the download
			}
			return getInfo(cursor); //get information from the cursor and return the resulting info
		}
		finally
		{
			cursor.close();
		}
	}
}
