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

package com.globalmentor.android.accounts;

import java.util.*;

import android.accounts.*;
import android.content.Context;

/**
 * Utilities and constants for working with accounts.
 * 
 * @author Garret Wilson
 */
public class Accounts
{

	/** The account type for listing Google accounts. */
	public final static String GOOGLE_ACCOUNT_TYPE = "com.google";

	/**
	 * Returns a list of all Google accounts.
	 * 
	 * @param context The Android context.
	 * @return A list of all Google accounts.
	 * @see #GOOGLE_ACCOUNT_TYPE
	 */
	public static List<Account> getGoogleAccounts(final Context context)
	{
		return Arrays.asList(AccountManager.get(context).getAccountsByType(GOOGLE_ACCOUNT_TYPE));
	}

	/**
	 * Returns the registered Google account. If there are multiple registered Google accounts, the first one will be returned.
	 * <p>
	 * This method should be called only in circumstances where it is expected that a Google account be registered.
	 * </p>
	 * 
	 * @param context The Android context.
	 * @return The first registered Google account.
	 * @throws IllegalStateException if no Google account is registered.
	 * @see #getGoogleAccounts(Context)
	 */
	public static Account getGoogleAccount(final Context context)
	{
		final List<Account> accounts = getGoogleAccounts(context);
		if(accounts.isEmpty())
		{
			throw new IllegalStateException("No Google account registered.");
		}
		return accounts.get(0);
	}

}
