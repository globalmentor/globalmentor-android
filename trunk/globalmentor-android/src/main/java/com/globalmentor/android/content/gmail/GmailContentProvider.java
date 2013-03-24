/*
 * Copyright © 2012 GlobalMentor, Inc. <http://www.globalmentor.com/>
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

package com.globalmentor.android.content.gmail;

import static android.content.pm.PackageManager.*;
import static android.content.pm.PermissionInfo.*;
import static com.globalmentor.android.content.ContentProviders.*;
import static com.globalmentor.net.URIs.*;

import android.accounts.*;
import android.content.Context;
import android.content.pm.*;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Constants and utility methods defining the contract with the Gmail content provider.
 * 
 * <p>
 * Most definitions were retrieved from <code>com.google.android.gm.contentprovider.GmailContract</code>, Copyright 2012 Google Inc.
 * </p>
 * 
 * @author Garret Wilson
 * @see <a href="https://developers.google.com/gmail/android/">Android Gmail</a>
 */
public class GmailContentProvider
{

	/** The Gmail content provider package name. */
	public final static String PACKAGE_NAME = "com.google.android.gm";

	/** The class name of the Gmail conversation list activity. */
	public final static String CONVERSATION_LIST_ACTIVITY_CLASS_NAME = PACKAGE_NAME + ".ConversationListActivity";

	/** Permission required to access the Gmail content provider. */
	public static final String READ_PERMISSION = "com.google.android.gm.permission.READ_CONTENT_PROVIDER";

	/** Authority for the Gmail content provider. */
	public static final String AUTHORITY = "com.google.android.gm";

	/** The path segment for labels. */
	public final static String LABELS_PATH_SEGMENT = "labels";

	/** The path segment for a label. */
	static final String LABEL_PARAM = "label";

	/** The base URI for Gmail. */
	public final static Uri BASE_URI = Uri.parse(CONTENT_URI_SCHEME + SCHEME_SEPARATOR + AUTHORITY_PREFIX + AUTHORITY + PATH_SEPARATOR);

	//label canonical names

	/** Canonical name for the Inbox label. */
	public static final String LABEL_CANONICAL_NAME_INBOX = "^i";

	/** Canonical name for the Priority Inbox label. */
	public static final String LABEL_CANONICAL_NAME_PRIORITY_INBOX = "^iim";

	/** Canonical name for the Starred label. */
	public static final String LABEL_CANONICAL_NAME_STARRED = "^t";

	/** Canonical name for the Sent label. */
	public static final String LABEL_CANONICAL_NAME_SENT = "^f";

	/** Canonical name for the Drafts label. */
	public static final String LABEL_CANONICAL_NAME_DRAFTS = "^r";

	/** Canonical name for the All Mail label. */
	public static final String LABEL_CANONICAL_NAME_ALL_MAIL = "^all";

	/** Canonical name for the Spam label. */
	public static final String LABEL_CANONICAL_NAME_SPAM = "^s";

	/** Canonical name for the Trash label. */
	public static final String LABEL_CANONICAL_NAME_TRASH = "^k";

	//label columns

	/**
	 * This string value is the canonical name of a label. Canonical names are not localized and are not user-facing.
	 * 
	 * <p>
	 * Type: TEXT
	 * </p>
	 */
	public static final String LABEL_COLUMN_CANONICAL_NAME = "canonicalName";

	/**
	 * This string value is the user-visible name of a label. Names of system labels (Inbox, Sent, Drafts...) are localized.
	 * 
	 * <p>
	 * Type: TEXT
	 * </p>
	 */
	public static final String LABEL_COLUMN_NAME = "name";

	/**
	 * This integer value is the number of conversations in this label.
	 * 
	 * <p>
	 * Type: INTEGER
	 * </p>
	 */
	public static final String LABEL_COLUMN_NUM_CONVERSATIONS = "numConversations";

	/**
	 * This integer value is the number of unread conversations in this label.
	 * 
	 * <p>
	 * Type: INTEGER
	 * </p>
	 */
	public static final String LABEL_COLUMN_NUM_UNREAD_CONVERSATIONS = "numUnreadConversations";

	/**
	 * This integer value is the label's foreground text color in 32-bit 0xAARRGGBB format.
	 * 
	 * <p>
	 * Type: INTEGER
	 * </p>
	 */
	public static final String LABEL_COLUMN_TEXT_COLOR = "text_color";

	/**
	 * This integer value is the label's background color in 32-bit 0xAARRGGBB format.
	 * 
	 * <p>
	 * Type: INTEGER
	 * </p>
	 */
	public static final String LABEL_COLUMN_BACKGROUND_COLOR = "background_color";

	/**
	 * This string column value is the URI that can be used in subsequent calls to {@link android.content.ContentProvider#query()} to query for information on the
	 * single label represented by this row.
	 * 
	 * <p>
	 * Type: TEXT
	 * </p>
	 */
	public static final String LABEL_COLUMN_URI = "labelUri";

	/**
	 * Determines if the installed Gmail content provider supports querying for label information.
	 * @param context The application context.
	 * @return <code>true</code> if the Gmail content provider supports label API queries.
	 */
	public static boolean canQueryLabels(final Context context)
	{
		boolean result = false;
		try
		{
			final PackageInfo packageInfo = context.getPackageManager().getPackageInfo(PACKAGE_NAME, GET_PROVIDERS | GET_PERMISSIONS);
			boolean allowRead = false;
			if(packageInfo.permissions != null) //check for the read permission
			{
				for(final PermissionInfo permissionInfo : packageInfo.permissions)
				{
					if(READ_PERMISSION.equals(permissionInfo.name) && permissionInfo.protectionLevel < PROTECTION_SIGNATURE)
					{
						allowRead = true;
						break;
					}
				}
			}
			if(allowRead && packageInfo.providers != null) //check for reading the Gmail content provider
			{
				for(int i = 0, len = packageInfo.providers.length; i < len; i++)
				{
					final ProviderInfo provider = packageInfo.providers[i];
					if(AUTHORITY.equals(provider.authority) && TextUtils.equals(READ_PERMISSION, provider.readPermission))
					{
						result = true;
					}
				}
			}
		}
		catch(final NameNotFoundException nameNotFoundException) //if the Gmail content provider was not found
		{
			//stay with the default response
		}
		return result;
	}

	/**
	 * Determines a URI that, when queried, will return the list of labels for an account.
	 * <p>
	 * <code>content://com.google.android.gm/<var>account</var>/labels</code>
	 * </p>
	 * @param account A valid Google account.
	 * @return The URI that can be queried to retrieve the the label list.
	 */
	public static Uri getLabelsQueryUri(final Account account)
	{
		return BASE_URI.buildUpon().appendEncodedPath(account.name + PATH_SEPARATOR + LABELS_PATH_SEGMENT).build();
	}

	/**
	 * Moves a label cursor to the next row with the given canonical name. The current row will be ignored.
	 * @param labelCursor The label cursor.
	 * @param labelCanonicalName The label canonical name to find.
	 * @throws IllegalArgumentException if no row could be found with the given label canonical name.
	 */
	public static void moveLabelCursorToNextCanonicalName(final Cursor labelCursor, final String labelCanonicalName)
	{
		final int canonicalNameIndex;
		try
		{
			canonicalNameIndex = labelCursor.getColumnIndexOrThrow(LABEL_COLUMN_CANONICAL_NAME);
		}
		catch(final IllegalArgumentException illegalArgumentException)
		{
			throw new IllegalStateException("Missing " + LABEL_COLUMN_CANONICAL_NAME + " label column.");
		}
		while(labelCursor.moveToNext())
		{
			if(labelCanonicalName.equals(labelCursor.getString(canonicalNameIndex)))
			{
				return;
			}
		}
		throw new IllegalArgumentException("Could not find row for label canonical name " + labelCanonicalName);
	}

}
