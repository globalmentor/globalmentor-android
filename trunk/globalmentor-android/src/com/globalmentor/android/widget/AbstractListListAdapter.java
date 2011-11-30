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

import static com.globalmentor.java.Objects.*;

import java.util.List;

import android.content.Context;

/**
 * Abstract base adapter for showing lists backed by a {@link List}.</p>
 * 
 * @author Garret Wilson
 * 
 * @param <I> The type of item in the list.
 */
public abstract class AbstractListListAdapter<I> extends AbstractListAdapter<I>
{

	/** The backing list. */
	private final List<I> list;

	/** @return The backing list. */
	protected List<I> getList()
	{
		return list;
	}

	/**
	 * Constructor.
	 * @param context The current context.
	 * @param list The list to adapt.
	 * @throws NullPointerException if the given context and/or list is <code>null</code>.
	 */
	public AbstractListListAdapter(final Context context, final List<I> list)
	{
		super(context);
		this.list = checkInstance(list);
	}

	/** {@inheritDoc} This version delegates to {@link List#size()}. */
	@Override
	public int getCount()
	{
		return getList().size();
	}

	/** {@inheritDoc} This version delegates to {@link List#isEmpty()}. */
	@Override
	public boolean isEmpty()
	{
		return getList().isEmpty();
	}

	/** {@inheritDoc} This version delegates to {@link List#get(int)}. */
	@Override
	public I getItem(final int position)
	{
		return getList().get(position);
	}

	/** {@inheritDoc} This implementation returns the position of the item. */
	@Override
	public long getItemId(final int position)
	{
		return position;
	}

	/** {@inheritDoc} This version returns <code>false</code>, because this implementation uses list position for IDs. */
	@Override
	public boolean hasStableIds()
	{
		return false;
	}
}
