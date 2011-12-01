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

import android.app.ListActivity;
import android.content.Context;
import android.view.*;
import android.widget.*;

/**
 * Abstract base adapter for showing lists, adding generics and convenience methods.
 * 
 * @author Garret Wilson
 * 
 * @param <I> The type of item in the list.
 * 
 * @see ListActivity
 */
public abstract class AbstractListAdapter<I> extends BaseAdapter
{

	/** The current context. */
	private final Context context;

	/** @return The current context. */
	protected Context getContext()
	{
		return context;
	}

	/**
	 * Constructor.
	 * @param context The current context.
	 * @throws NullPointerException if the given context is <code>null</code>.
	 */
	public AbstractListAdapter(final Context context)
	{
		this.context = checkInstance(context);
	}

	/**
	 * {@inheritDoc} This method creates a new view if appropriate and then initializes it.
	 * @see #createView(int, long, Object, ViewGroup)
	 * @see #initializeView(View, int, long, Object, ViewGroup)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent)
	{
		final I item = (I)getItem(position); //get the item at this position
		final View view = convertView != null ? convertView : createView(position, getItemId(position), item, parent); //create the view if needed
		initializeView(view, position, getItemId(position), item, parent); //initialize the view
		return view;
	}

	/**
	 * Creates a new view for a particular item.
	 * @param position The position of the item in the list.
	 * @param id The ID of the item in the list.
	 * @param item The item in the list for which a view should be created.
	 * @param parent The parent to which this view will eventually be attached.
	 * @return A new view for the given item.
	 */
	protected abstract View createView(final int position, final long id, final I item, final ViewGroup parent);

	/**
	 * Initializes a view for a particular item.
	 * <p>
	 * This implementation does nothing.
	 * </p>
	 * @param view The view to initialize.
	 * @param position The position of the item in the list.
	 * @param id The ID of the item in the list.
	 * @param item The item in the list for which a view should be created.
	 * @param parent The parent to which this view will eventually be attached.
	 */
	protected void initializeView(final View view, final int position, final long id, final I item, final ViewGroup parent)
	{
	}

}
