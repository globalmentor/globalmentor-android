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

import com.globalmentor.android.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.*;
import android.widget.*;

/**
 * Abstract base adapter for showing a list of names and descriptions along with an icon. By default the icon is present.
 * 
 * <p>
 * This implementation uses a backing {@link List}.
 * </p>
 * 
 * @author Garret Wilson
 * 
 * @param <I> The type of item in the list.
 */
public abstract class AbstractNameDescriptionListAdapter<I> extends AbstractListListAdapter<I>
{

	/** Whether an icon should be present with the items. */
	private boolean iconPresent = true;

	/** @return Whether an icon should be present with the items. */
	public boolean isIconPresent()
	{
		return iconPresent;
	}

	/**
	 * Whether an icon should be present with the items.
	 * @param iconPresent Whether an icon should be present.
	 */
	public void setIconPresent(final boolean iconPresent)
	{
		this.iconPresent = iconPresent;
	}

	/**
	 * Context and list constructor. A defensive copy is made of the list.
	 * @param context The current context.
	 * @param list The list to adapt.
	 * @throws NullPointerException if the given context and/or list is <code>null</code>.
	 */
	public AbstractNameDescriptionListAdapter(final Context context, final List<I> list)
	{
		super(context, list);
	}

	@Override
	protected View createView(int position, long id, I item, ViewGroup parent)
	{
		return LayoutInflater.from(getContext()).inflate(R.layout.app_abstractnamedescriptionlistadapter_item, null);
	}

	@Override
	protected void initializeView(final View view, int position, long id, I item, ViewGroup parent)
	{
		final ImageView iconImageView = (ImageView)view.findViewById(R.id.app_abstractnamedescriptionlistadapter_item_icon);
		iconImageView.setVisibility(isIconPresent() ? ImageView.VISIBLE : ImageView.GONE); //set the visibility of the icon
		iconImageView.setImageDrawable(getItemIcon(position, id, item));
		final TextView labelTextView = (TextView)view.findViewById(R.id.app_abstractnamedescriptionlistadapter_item_label);
		labelTextView.setText(getItemName(position, id, item));
		final TextView descriptionTextView = (TextView)view.findViewById(R.id.app_abstractnamedescriptionlistadapter_item_description);
		descriptionTextView.setText(getItemDescription(position, id, item));
	}

	/**
	 * Returns the text for the name of a particular item.
	 * <p>
	 * This implementation return <code>null</code>.
	 * </p>
	 * @param position The position of the item in the list.
	 * @param id The ID of the item in the list.
	 * @param item The item in the list for which a name should be created.
	 * @return The text for the name of the item, or <code>null</code> if there is no name for the item.
	 */
	protected CharSequence getItemName(final int position, final long id, final I item)
	{
		return null;
	}

	/**
	 * Returns the text for the description of a particular item.
	 * <p>
	 * This implementation return <code>null</code>.
	 * </p>
	 * @param position The position of the item in the list.
	 * @param id The ID of the item in the list.
	 * @param item The item in the list for which a description should be created.
	 * @return The text for the description of the item, or <code>null</code> if there is no icon for the item.
	 */
	protected CharSequence getItemDescription(final int position, final long id, final I item)
	{
		return null;
	}

	/**
	 * Returns the drawable for the icon of a particular item.
	 * <p>
	 * This implementation return <code>null</code>.
	 * </p>
	 * @param position The position of the item in the list.
	 * @param id The ID of the item in the list.
	 * @param item The item in the list for which an icon should be created.
	 * @return The drawable for the icon of the item, or <code>null</code> if there is no icon for the item.
	 */
	protected Drawable getItemIcon(final int position, final long id, final I item)
	{
		return null;
	}

}
