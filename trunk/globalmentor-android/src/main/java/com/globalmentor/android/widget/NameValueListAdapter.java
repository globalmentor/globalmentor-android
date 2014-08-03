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

package com.globalmentor.android.widget;

import java.util.List;

import com.globalmentor.model.*;

import android.content.Context;

/**
 * Adapter for showing a list of names and values with no icon.
 * 
 * <p>
 * The name shown is a string. The value can be of any type, as long as it correctly implements {@link Labeled#getLabel()} or {@link Object#toString()}, and
 * <code>null</code> values are supported.
 * </p>
 * 
 * @author Garret Wilson
 */
public class NameValueListAdapter extends AbstractNameDescriptionListAdapter<NameValuePair<CharSequence, Object>> {

	/**
	 * Context and list constructor. A defensive copy is made of the list.
	 * @param context The current context.
	 * @param list The list to adapt.
	 * @throws NullPointerException if the given context and/or list is <code>null</code>.
	 */
	public NameValueListAdapter(final Context context, final List<NameValuePair<CharSequence, Object>> list) {
		super(context, list);
	}

	/** {@inheritDoc} This implementation delegates to {@link NameValuePair#getName()}. */
	@Override
	protected CharSequence getItemName(final int position, final long id, final NameValuePair<CharSequence, Object> item) {
		return item.getName();
	}

	/**
	 * {@inheritDoc} This implementation delegates to {@link NameValuePair#getValue()}. If the value implements {@link Labeled}, its label will be returned.
	 */
	@Override
	protected CharSequence getItemDescription(final int position, final long id, final NameValuePair<CharSequence, Object> item) {
		final Object value = item.getValue();
		return value != null ? Labels.getLabel(value) : null; //return an appropriate label for the object

	}

}
