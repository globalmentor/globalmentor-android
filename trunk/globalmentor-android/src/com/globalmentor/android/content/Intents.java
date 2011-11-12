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

package com.globalmentor.android.content;

/**
 * Utilities and constants for working with intents.
 * 
 * @author Garret Wilson
 */
public class Intents
{

	/**
	 * Creates a new ID for an action in the form <code>com.example.package.intent.action.SIMPLE_NAME</code>. By convention the provided name is in uppercase.
	 * @param c The class the package for which the action ID should be created.
	 * @param simpleName The local name of the action.
	 * @return A new ID for an action.
	 * @throws NullPointerException if the given class and/or name is <code>null</code>.
	 */
	public static String createAction(final Class<?> c, final String simpleName)
	{
		return createAction(c.getPackage(), simpleName);
	}

	/**
	 * Creates a new ID for an action in the form <code>com.example.package.intent.action.SIMPLE_NAME</code>. By convention the provided name is in uppercase.
	 * @param p The package for which the action ID should be created.
	 * @param simpleName The local name of the action.
	 * @return A new ID for an action.
	 * @throws NullPointerException if the given package and/or name is <code>null</code>.
	 */
	public static String createAction(final Package p, final String simpleName)
	{
		return p.getName() + ".intent.action." + simpleName;
	}

}
