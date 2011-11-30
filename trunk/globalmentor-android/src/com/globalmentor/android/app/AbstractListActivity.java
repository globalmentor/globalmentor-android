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

package com.globalmentor.android.app;

import com.globalmentor.android.R;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

/**
 * Abstract base class for an activity containing nothing but a list.
 * 
 * @author Garret Wilson
 * 
 * @param <I> The type of item in the list.
 */
public abstract class AbstractListActivity<I> extends BaseActivity
{

	private ListView listView;

	/** The list adapter being used. */
	private ListAdapter listAdapter;

	/** @return The list adapter being used. */
	protected ListAdapter getListAdapter()
	{
		return listAdapter;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_abstractlistactivity);
		listView = (ListView)findViewById(R.id.app_abstractlistactivity_list);
		listAdapter = createListAdapter(); //create the adapter
		listView.setAdapter(listAdapter); //set the adapter in the list view
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() //call onApplicationClick() with the application information
				{
					@Override
					public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id)
					{
						AbstractListActivity.this.onItemClick(position, id, getItem(position, id)); //get the item and delegate to the other onItemClick() method
					}
				});
	}

	/**
	 * Creates a list adapter for this activity. This method is called in the main thread.
	 * @return A new list adapter to be used in this activity.
	 */
	protected abstract ListAdapter createListAdapter();

	/**
	 * Retrieves the item at the given position with the given ID.
	 * <p>
	 * This default implementation delegates to {@link ListAdapter#getItem(int)}.
	 * @param position The position in the list.
	 * @param id The item ID.
	 * @return The item with the given position and ID, or <code>null</code> if no such item could be found.
	 * @throws IllegalArgumentException if the given position and/or ID is invalid.
	 */
	@SuppressWarnings("unchecked")
	protected I getItem(final int position, final long id)
	{
		return (I)getListAdapter().getItem(position);
	}

	/**
	 * Called when an item on the list is clicked.
	 * 
	 * <p>
	 * This version does nothing.
	 * </p>
	 * 
	 * @param position The position in the list that was clicked.
	 * @param id The ID of the item that was clicked.
	 * @param item The item that was clicked.
	 */
	public void onItemClick(final int position, final long id, final I item)
	{
	}

}
