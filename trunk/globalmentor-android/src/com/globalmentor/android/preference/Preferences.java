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

package com.globalmentor.android.preference;

import static java.util.Collections.*;

import java.util.*;
import java.util.regex.Pattern;

import android.content.SharedPreferences;

/**
 * Utilities for working with Android preferences.
 * 
 * @author Garret Wilson
 * 
 * @see SharedPreferences
 */
public class Preferences
{

	/** The pattern for splitting a string into lines, accepting either <code>LF</code> or <code>CR+LF</code>. */
	public final static Pattern LINE_SPLIT_PATTERN = Pattern.compile("\\r?\\n");

	/**
	 * Returns a string preference value as a list of line, splitting the value on newlines.
	 * <p>
	 * This method accepts either <code>LF</code> or <code>CR+LF</code> as the newline separator. Each line is trimmed, and empty lines are ignored.
	 * </p>
	 * @param preferences The source of the preferences.
	 * @param key The name of the preference to retrieve.
	 * @param defValue Value to return if this preference does not exist.
	 * @return The preference value if it exists, or the default value if the value does not exist.
	 * @throws ClassCastException if there is a preference with this name that is not a string.
	 * @see #getStringLines(SharedPreferences, String, List, boolean)
	 * @see SharedPreferences#getString(String, String)
	 * @see #LINE_SPLIT_PATTERN
	 * @see String#trim()
	 */
	public static List<String> getStringLines(final SharedPreferences preferences, final String key, final List<String> defValue)
	{
		return getStringLines(preferences, key, defValue, false); //ignore empty lines
	}

	/**
	 * Returns a string preference value as a list of line, splitting the value on newlines.
	 * <p>
	 * This method accepts either <code>LF</code> or <code>CR+LF</code> as the newline separator. Each line is trimmed.
	 * </p>
	 * @param preferences The source of the preferences.
	 * @param key The name of the preference to retrieve.
	 * @param defValue Value to return if this preference does not exist.
	 * @param includeEmptyLines Whether if empty lines (after trimming, if any) should be included in the result.
	 * @return The preference value if it exists, or the default value if the value does not exist.
	 * @throws ClassCastException if there is a preference with this name that is not a string.
	 * @see #getStringLines(SharedPreferences, String, List, boolean, boolean)
	 * @see SharedPreferences#getString(String, String)
	 * @see #LINE_SPLIT_PATTERN
	 * @see String#trim()
	 */
	public static List<String> getStringLines(final SharedPreferences preferences, final String key, final List<String> defValue, final boolean includeEmptyLines)
	{
		return getStringLines(preferences, key, defValue, includeEmptyLines, true); //trim lines
	}

	/**
	 * Returns a string preference value as a list of line, splitting the value on newlines.
	 * <p>
	 * This method accepts Either <code>LF</code> or <code>CR+LF</code> as the newline separator.
	 * </p>
	 * @param preferences The source of the preferences.
	 * @param key The name of the preference to retrieve.
	 * @param defValue Value to return if this preference does not exist.
	 * @param includeEmptyLines Whether if empty lines (after trimming, if any) should be included in the result.
	 * @param trimLines Whether each line should first be trimmed of whitespace.
	 * @return The preference value if it exists, or the default value if the value does not exist.
	 * @throws ClassCastException if there is a preference with this name that is not a string.
	 * @see SharedPreferences#getString(String, String)
	 * @see #LINE_SPLIT_PATTERN
	 * @see String#trim()
	 */
	public static List<String> getStringLines(final SharedPreferences preferences, final String key, final List<String> defValue,
			final boolean includeEmptyLines, final boolean trimLines)
	{
		final String preferenceString = preferences.getString(key, null); //get the preference string
		if(preferenceString == null) //if there is no such value
		{
			return defValue; //return the provided default value
		}
		if(preferenceString.isEmpty())
		{
			return emptyList();
		}
		final List<String> lines = new ArrayList<String>();
		for(String line : LINE_SPLIT_PATTERN.split(preferenceString, -1)) //look at each line, including any trailing empty line
		{
			if(trimLines)
			{
				line = line.trim();
			}
			if(includeEmptyLines || !line.isEmpty()) //add the line unless it's empty and we shouldn't include empty lines
			{
				lines.add(line);
			}
		}
		return lines;
	}

}
