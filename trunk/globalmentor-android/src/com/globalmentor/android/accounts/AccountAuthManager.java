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

import static com.google.common.base.Preconditions.*;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * Manages the authentication for an account, easing the process of acquiring and refreshing authentication tokens.
 * 
 * @author Garret Wilson
 */
public class AccountAuthManager
{

	//TODO fix for dialogs	private static final int REQUEST_AUTHENTICATE = 0;

	/** A handler associated with the main thread. */
	private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

	private final AccountManager accountManager;

	public AccountManager getAccountManager()
	{
		return accountManager;
	}

	/** The account the authentication of which is being managed. */
	private final Account account;

	/** @return The account the authentication of which is being managed. */
	public Account getAccount()
	{
		return account;
	}

	/** The auth token type---an authenticator-dependent string token. */
	private final String authTokenType;

	/** @return The auth token type---an authenticator-dependent string token. */
	public String getAuthTokenType()
	{
		return authTokenType;
	}

	/** The token indicating the successful authentication, or <code>null</code> if the account has not been authenticated. */
	private String authToken = null;

	/** @return The token indicating the successful authentication, or <code>null</code> if the account has not been authenticated. */
	public String getAuthToken()
	{
		return authToken;
	}

	public AccountAuthManager(final AccountManager accountManager, final Account account, final String authTokenType)
	{
		this.accountManager = checkNotNull(accountManager);
		this.account = checkNotNull(account);
		this.authTokenType = checkNotNull(authTokenType);
	}

	/**
	 * Called every time the account is successfully authenticated.
	 * <p>
	 * This version stores the auth token for later use.
	 * </p>
	 * @param authToken The token indicating the successful authentication.
	 * @see #getAuthToken()
	 */
	protected void onAuthenticated(final String authToken)
	{
		this.authToken = authToken;
		//TODO store this in the client in a subclass
	}

	/**
	 * Invalidates the current auth token, if any. If there is a current auth token, it will be invalidated with the account manager. If there is no auth token,
	 * no action occurs.
	 * <p>
	 * When this method finishes, {@link #getAuthToken()} will return <code>null</code>.
	 * </p>
	 * @see #getAuthToken()
	 */
	protected void invalidateAuthToken()
	{
		if(authToken != null)
		{
			getAccountManager().invalidateAuthToken(getAccount().type, authToken); //invalidate the auth token
			authToken = null; //discard the auth token
		}
	}

	public void execute(final AuthenticatedOperation operation)
	{
		new Thread(new AuthenticatedRunnable(operation)).start(); //try to perform the operation again, but don't retry authentication
	}

	/**
	 * Attempts to authenticate the account and then retry the given operation.
	 * 
	 * @param operation The operation to retry after authentication.
	 * @param retries The number of authentication and operation retries to re-attempt.
	 */
	protected void authenticateRetry(final AuthenticatedOperation operation, final int retries)
	{
		//TODO check retries>=0
		//create a new authenticate task---but it must be created in the UI thread
		mainThreadHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				new AuthenticateTask(operation, retries).execute();
			}
		});
	}

	/**
	 * Interface for an operation that will be authenticated.
	 * @author Garret Wilson
	 */
	public interface AuthenticatedOperation
	{
		/**
		 * The main execution method. The operation should occur in this method. If the operation has authentication problems, an {@link AuthenticatorException}
		 * should be thrown; an authentication token will be acquired and the operation will be retried.
		 * @throws AuthenticatorException if the operation is not authenticated.
		 */
		public void execute() throws AuthenticatorException;
	}

	/**
	 * 
	 * 
	 * <p>
	 * By default, three tries (this try and two retries) are allowed to authenticate. This covers the following common condition:
	 * </p>
	 * <ol>
	 * <li>We have no record of an auth token; a try is made and fails.</li>
	 * <li>We get an auth token and try again, but the auth token given is a cached, expired token.</li>
	 * <li>We invalidate the token and get a new one, trying one last time; the attempt should succeed with the new token.</li>
	 * </ol>
	 * 
	 * @author Garret Wilson
	 * 
	 */
	private class AuthenticatedRunnable implements Runnable
	{

		private final AuthenticatedOperation operation;
		private final int retries;

		public AuthenticatedRunnable(final AuthenticatedOperation operation)
		{
			this(operation, 2); //TODO comment retries
		}

		public AuthenticatedRunnable(final AuthenticatedOperation operation, int retries)
		{
			this.operation = checkNotNull(operation);
			this.retries = retries;
		}

		@Override
		public void run()
		{
			try
			{
				operation.execute(); //try to execute the operation
			}
			catch(final AuthenticatorException authenticatorException) //if the operation needs authentication
			{
				if(getAuthToken() != null) //if we have an auth token, it isn't working
				{
					invalidateAuthToken(); //invalidate our current auth token and get another one
				}
				if(retries > 0) //if we're allowed to retry
				{
					authenticateRetry(operation, retries - 1); //try to perform authentication and, if successful, retry the operation if allowed, noting that now one less retry is allowed
				}
			}
		}
	}

	/*TODO fix for dialogs
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode)
		{
			case REQUEST_AUTHENTICATE:
				if(resultCode == RESULT_OK)
				{
					gotAccount(false);
				}
				else
				{
					showDialog(DIALOG_ACCOUNTS);
				}
				break;
		}
	}
	*/

	private class AuthenticateTask extends AsyncTask<Void, Void, Bundle>
	{

		/** The operation to perform after successfully authenticating. */
		private final AuthenticatedOperation postAuthenticateOperation;

		private int retries;

		public AuthenticateTask(final AuthenticatedOperation postAuthenticateOperation, final int retries)
		{
			this.postAuthenticateOperation = checkNotNull(postAuthenticateOperation);
			this.retries = retries; //TODO check to make sure retries >=0
		}

		@Override
		protected Bundle doInBackground(Void... params)
		{
			while(true) //TODO add support for retries on I/O failure
			{
				try
				{
					return getAccountManager().getAuthToken(getAccount(), getAuthTokenType(), true, null, null).getResult();
				}
				catch(OperationCanceledException e)
				{
					e.printStackTrace();
					return null; //TODO fix
				}
				catch(final AuthenticatorException authenticatorException) //if authentication failed, maybe our token is expired
				{
					authenticatorException.printStackTrace();
					return null; //TODO fix
				}
				/*TODO fix for dialogs
								catch(final AuthenticatorException authenticatorException) //if authentication failed, maybe our token is expired
								{
									if(getAuthToken() == null) //if we have an auth token
									{
										invalidateAuthToken(); //invalidate our auth token; fall through and try again
									}
									else
									//if we have no auth token, give up---we already tried to get above
									{
										authenticatorException.printStackTrace();
										return null; //TODO fix
									}
								}
				*/
				catch(IOException e)
				{
					Log.e("AccountAuthManager", "I/O error getting auth token", e);
					e.printStackTrace();
					return null; //TODO fix
				}
			}
		}

		@Override
		protected void onPostExecute(final Bundle result)
		{
			super.onPostExecute(result);
			if(result != null) //if we have a result (i.e. there was no error
			{
				if(result.containsKey(AccountManager.KEY_INTENT))
				{
					Intent intent = result.getParcelable(AccountManager.KEY_INTENT);
					int flags = intent.getFlags();
					flags &= ~Intent.FLAG_ACTIVITY_NEW_TASK;
					intent.setFlags(flags);
					//TODO fix startActivityForResult(intent, REQUEST_AUTHENTICATE);
				}
				else if(result.containsKey(AccountManager.KEY_AUTHTOKEN)) //if we got back an auth token
				{
					onAuthenticated(result.getString(AccountManager.KEY_AUTHTOKEN)); //process and store the auth token
				}
				new Thread(new AuthenticatedRunnable(postAuthenticateOperation, retries)).start(); //try to perform the operation again, specifying the number of further retires allowed
			}
			else
			//if the task ran into an error
			{
				//TODO handle error here
			}
		}
	}

}
