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

import static com.globalmentor.java.Objects.*;
import static com.globalmentor.java.Conditions.*;

import java.io.IOException;

import android.accounts.*;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.*;

/**
 * Manages the authentication for an account, easing the process of acquiring and refreshing authentication tokens.
 * 
 * @author Garret Wilson
 */
public class AccountAuthManager {

	/**
	 * The default number of times to retry authentication.
	 * <p>
	 * By default, three tries (one try and two retries) are allowed to authenticate. This covers the following common condition:
	 * </p>
	 * <ol>
	 * <li>We have no record of an auth token; a try is made and fails.</li>
	 * <li>We get an auth token and try again, but the auth token given is a cached, expired token.</li>
	 * <li>We invalidate the token and get a new one, trying one last time; the attempt should succeed with the new token.</li>
	 * </ol>
	 * 
	 */
	public static final int DEFAULT_AUTHENTICATE_RETRY_COUNT = 2;

	//TODO fix for dialogs	private static final int REQUEST_AUTHENTICATE = 0;

	/** A handler associated with the main thread. */
	private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

	/** The Android account manager. */
	private final AccountManager accountManager;

	/** @return The Android account manager. */
	public AccountManager getAccountManager() {
		return accountManager;
	}

	/** The account the authentication of which is being managed. */
	private final Account account;

	/** @return The account the authentication of which is being managed. */
	public Account getAccount() {
		return account;
	}

	/** The auth token type---an authenticator-dependent string token. */
	private final String authTokenType;

	/** @return The auth token type---an authenticator-dependent string token. */
	public String getAuthTokenType() {
		return authTokenType;
	}

	/** The token indicating the successful authentication, or <code>null</code> if the account has not been authenticated. */
	private String authToken = null;

	/** @return The token indicating the successful authentication, or <code>null</code> if the account has not been authenticated. */
	public String getAuthToken() {
		return authToken;
	}

	/**
	 * Constructor.
	 * @param accountManager The Android account manager.
	 * @param account The account the authentication of which is being managed.
	 * @param authTokenType The auth token type---an authenticator-dependent string token.
	 * @throws NullPointerException if the given account manager, account, and/or auth token type is <code>null</code>.
	 */
	public AccountAuthManager(final AccountManager accountManager, final Account account, final String authTokenType) {
		this.accountManager = checkInstance(accountManager);
		this.account = checkInstance(account);
		this.authTokenType = checkInstance(authTokenType);
	}

	/**
	 * Called every time the account is successfully authenticated.
	 * <p>
	 * This version stores the auth token for later use.
	 * </p>
	 * @param authToken The token indicating the successful authentication.
	 * @see #getAuthToken()
	 */
	protected void onAuthenticated(final String authToken) {
		this.authToken = authToken;
	}

	/**
	 * Invalidates the current auth token, if any. If there is a current auth token, it will be invalidated with the account manager. If there is no auth token,
	 * no action occurs.
	 * <p>
	 * When this method finishes, {@link #getAuthToken()} will return <code>null</code>.
	 * </p>
	 * @see #getAuthToken()
	 */
	protected void invalidateAuthToken() {
		if(authToken != null) {
			getAccountManager().invalidateAuthToken(getAccount().type, authToken); //invalidate the auth token
			authToken = null; //discard the auth token
		}
	}

	/**
	 * Executes an authenticate operation.
	 * <p>
	 * Execution will occur in a separate thread. If the operation need authentication, a new authentication token will be acquired from the account manager and
	 * the authentication will be retried up to {@value #DEFAULT_AUTHENTICATE_RETRY_COUNT} times.
	 * @param operation The operation to execute.
	 * @throws NullPointerException if the given operation is <code>null</code>.
	 * @see #DEFAULT_AUTHENTICATE_RETRY_COUNT
	 */
	public void execute(final AuthenticatedOperation operation) {
		execute(operation, DEFAULT_AUTHENTICATE_RETRY_COUNT);
	}

	/**
	 * Executes an authenticate operation.
	 * <p>
	 * Execution will occur in a separate thread. If the operation need authentication, a new authentication token will be acquired from the account manager and
	 * the authentication will be retried.
	 * @param operation The operation to execute.
	 * @param retries The number of authentication and operation retries to re-attempt.
	 * @throws NullPointerException if the given operation is <code>null</code>.
	 * @throws IllegalArgumentException if the number of retries is negative.
	 */
	public void execute(final AuthenticatedOperation operation, final int retries) {
		new Thread(new AuthenticatedWorker(operation, retries)).start(); //try to perform the operation again, retrying authentication as requested
	}

	/**
	 * Attempts to authenticate the account and then retry the given operation.
	 * 
	 * @param operation The operation to retry after authentication.
	 * @param retries The number of authentication and operation retries to re-attempt.
	 * @throws IllegalArgumentException if the number of retries is negative.
	 */
	protected void authenticateRetry(final AuthenticatedOperation operation, final int retries) {
		checkArgument(retries >= 0, "Number of retries cannot be negative.");
		//create a new authenticate task---but it must be created in the UI thread
		mainThreadHandler.post(new Runnable() { //start a new authenticate task on the main thread

					@Override
					public void run() {
						new AuthenticateTask(operation, retries).execute();
					}
				});
	}

	/**
	 * Interface for an operation that will be authenticated.
	 * @author Garret Wilson
	 */
	public interface AuthenticatedOperation {

		/**
		 * The main execution method. The operation should occur in this method. If the operation has authentication problems, an {@link AuthenticatorException}
		 * should be thrown; an authentication token will be acquired and the operation will be retried.
		 * <p>
		 * This method is not guaranteed to be run on the main thread.
		 * </p>
		 * @throws IOException if there is an error executing the operation.
		 * @throws AuthenticatorException if the operation is not authenticated.
		 */
		public void execute() throws IOException, AuthenticatorException;

		/**
		 * Called after execution finishes successfully, or after execution has failed and will not be attempted further. This method is thus guaranteed to called
		 * once after execution begins.
		 * <p>
		 * This method is not guaranteed to be run on the main thread.
		 * </p>
		 * @param exception The error that occurred during execution, or <code>null</code> if execution completed with no error.
		 */
		public void postExecute(final Exception exception);

	}

	/**
	 * A worker that executes an operation, authenticating and retrying the operation if necessary.
	 * 
	 * @author Garret Wilson
	 * 
	 * @see AuthenticatedOperation
	 */
	private class AuthenticatedWorker implements Runnable {

		/** The operation to perform. */
		private final AuthenticatedOperation operation;

		/** The number of authentication and operation retries to re-attempt. */
		private final int retries;

		/**
		 * Constructor.
		 * @param operation The operation to perform.
		 * @param retries The number of authentication and operation retries to re-attempt.
		 * @throws IllegalArgumentException if the number of retries is negative.
		 */
		public AuthenticatedWorker(final AuthenticatedOperation operation, int retries) {
			this.operation = checkInstance(operation);
			checkArgument(retries >= 0, "Number of retries cannot be negative.");
			this.retries = retries;
		}

		@Override
		public void run() {
			try {
				operation.execute(); //try to execute the operation
				operation.postExecute(null); //call the post-operation functionality
			} catch(final IOException ioException) {
				operation.postExecute(ioException); //call the post-operation functionality
			} catch(final AuthenticatorException authenticatorException) { //if the operation needs authentication
				if(getAuthToken() != null) { //if we have an auth token, it isn't working
					invalidateAuthToken(); //invalidate our current auth token and get another one
				}
				if(retries > 0) { //if we're allowed to retry
					authenticateRetry(operation, retries - 1); //try to perform authentication and, if successful, retry the operation if allowed, noting that now one less retry is allowed
				} else { //if we're finished trying
					operation.postExecute(authenticatorException); //call the post-operation functionality
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

	/**
	 * The task that attempts authentication and then retries an operation.
	 * <p>
	 * If authentication does not succeed, the operation will not be retried, but {@link AuthenticatedOperation#postExecute(Exception)} will be called.
	 * </p>
	 * @author Garret Wilson
	 */
	private class AuthenticateTask extends AsyncTask<Void, Void, Bundle> {

		/** The operation to perform after successfully authenticating. */
		private final AuthenticatedOperation postAuthenticateOperation;

		/** The number of authentication and operation retries to re-attempt (after this one). */
		private int retries;

		/** The exception, if any, that occurred during authentication. */
		private Exception exception = null;

		/**
		 * Constructor.
		 * @param postAuthenticateOperation The operation to perform after successfully authenticating.
		 * @param retries The number of authentication and operation retries to re-attempt (after this one).
		 * @throws IllegalArgumentException if the number of retries is negative.
		 */
		public AuthenticateTask(final AuthenticatedOperation postAuthenticateOperation, final int retries) {
			this.postAuthenticateOperation = checkInstance(postAuthenticateOperation);
			checkArgument(retries >= 0, "Number of retries cannot be negative.");
			this.retries = retries;
		}

		@Override
		protected Bundle doInBackground(final Void... params) {
			while(true) {
				try {
					return getAccountManager().getAuthToken(getAccount(), getAuthTokenType(), true, null, null).getResult(); //try to get a new authentication token
				} catch(final OperationCanceledException operationCanceledException) { //if the user, for example, canceled authentication
					exception = operationCanceledException;
					return null;
				} catch(final AuthenticatorException authenticatorException) {
					exception = authenticatorException;
					return null;
				}
				/*TODO fix for dialogs
								catch(final AuthenticatorException authenticatorException) {	//if authentication failed, maybe our token is expired
									if(getAuthToken() == null) {	//if we have an auth token
										invalidateAuthToken(); //invalidate our auth token; fall through and try again
									}
									else {	//if we have no auth token, give up---we already tried to get above
										authenticatorException.printStackTrace();
										return null; //TODO fix
									}
								}
				*/
				catch(final IOException ioException) {
					exception = ioException;
					return null;
				}
			}
		}

		@Override
		protected void onPostExecute(final Bundle result) {
			super.onPostExecute(result);
			if(result != null) { //if we have a result (i.e. there was no error)
				if(result.containsKey(AccountManager.KEY_INTENT)) {
					Intent intent = result.getParcelable(AccountManager.KEY_INTENT);
					int flags = intent.getFlags();
					flags &= ~Intent.FLAG_ACTIVITY_NEW_TASK;
					intent.setFlags(flags);
					//TODO fix startActivityForResult(intent, REQUEST_AUTHENTICATE);
				} else if(result.containsKey(AccountManager.KEY_AUTHTOKEN)) { //if we got back an auth token
					onAuthenticated(result.getString(AccountManager.KEY_AUTHTOKEN)); //process and store the auth token
				}
				new Thread(new AuthenticatedWorker(postAuthenticateOperation, retries)).start(); //try to perform the operation again, specifying the number of further retries allowed
			} else { //if the task ran into an error
				postAuthenticateOperation.postExecute(exception); //call the post-execution method of the operation, indicating the error
			}
		}
	}

}
