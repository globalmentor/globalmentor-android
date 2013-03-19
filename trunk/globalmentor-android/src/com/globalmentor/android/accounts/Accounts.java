/*
 * Copyright © 2011-2012 GlobalMentor, Inc. <http://www.globalmentor.com/>
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

import static com.globalmentor.java.Conditions.*;

import java.io.IOException;
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

	/** The feature of an account that supports mail. */
	public final static String MAIL_FEATURE = "service_mail";

	/**
	 * Returns a list of all Google accounts.
	 * @param context The Android context.
	 * @return A list of all Google accounts.
	 * @see #GOOGLE_ACCOUNT_TYPE
	 */
	public static List<Account> getGoogleAccounts(final Context context)
	{
		return Arrays.asList(AccountManager.get(context).getAccountsByType(GOOGLE_ACCOUNT_TYPE));
	}

	/**
	 * Returns a list of all Google accounts with the given features.
	 * <p>
	 * The returned {@link AccountManagerFuture}s must not be used on the main thread.
	 * </p>
	 * @param context The Android context.
	 * @return An account manager future for all Google accounts with the given features.
	 * @throws IllegalArgumentException if no features are given.
	 * @see #GOOGLE_ACCOUNT_TYPE
	 */
	public static AccountManagerFuture<Account[]> getGoogleAccountsByFeature(final Context context, final String... features)
	{
		checkArgument(features.length > 0, "No features provided.");
		return AccountManager.get(context).getAccountsByTypeAndFeatures(GOOGLE_ACCOUNT_TYPE, features, null, null);
	}

	/**
	 * Returns the registered Google account. If there are multiple registered Google accounts, the first one will be returned.
	 * <p>
	 * This method should be called only in circumstances where it is expected that a Google account be registered.
	 * </p>
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

	/**
	 * Returns the registered Google account that supports mail. If there are multiple registered Google mail accounts, the first one will be returned.
	 * <p>
	 * This method must not be used on the main thread, as it will block until all relevant accounts are retrieved.
	 * </p>
	 * @param context The Android context.
	 * @return The first registered Google mail account, or <code>null</code> if there is no registered Google mail account.
	 * @throws OperationCanceledException if the request was canceled for any reason.
	 * @throws AuthenticatorException if there was an error communicating with the authenticator or if the authenticator returned an invalid response.
	 * @throws IOException if the authenticator encountered an IOException while communicating with the authentication server.
	 * @see #getGoogleAccountsByFeature(Context, String...)
	 */
	public static Account getGoogleMailAccount(final Context context) throws OperationCanceledException, AuthenticatorException, IOException
	{
		final Account[] accounts = getGoogleAccountsByFeature(context, MAIL_FEATURE).getResult();
		return accounts.length > 0 ? accounts[0] : null;
	}

}
