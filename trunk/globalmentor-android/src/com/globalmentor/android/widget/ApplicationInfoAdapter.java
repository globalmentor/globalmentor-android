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

import android.content.Context;
import android.content.pm.*;
import android.graphics.drawable.Drawable;

/**
 * Adapter for showing installed applications in a list. The application icon, name, and description (if present) is shown.
 * 
 * <p>
 * Note that the application description is seldom provided by applications, so most list items have the description field blank.
 * </p>
 * 
 * <p>
 * This class was initially created following closely <a href="http://xjaphx.wordpress.com/2011/06/12/create-application-launcher-as-a-list/">Create Application
 * Launcher as a list</a> tutorial.
 * </p>
 * 
 * @author Garret Wilson
 * 
 * @see <a href="http://xjaphx.wordpress.com/2011/06/12/create-application-launcher-as-a-list/">Create Application Launcher as a list</a>
 */
public class ApplicationInfoAdapter extends AbstractNameDescriptionListAdapter<ApplicationInfo>
{
	/**
	 * Constructor.
	 * @param context The current context.
	 * @param list The list to adapt.
	 * @throws NullPointerException if the given context and/or list is <code>null</code>.
	 */
	public ApplicationInfoAdapter(final Context context, final List<ApplicationInfo> list)
	{
		super(context, list);
	}

	/** {@inheritDoc} This implementation delegates to {@link ApplicationInfo#loadLabel(PackageManager)}. */
	@Override
	protected CharSequence getItemName(final int position, final long id, final ApplicationInfo item)
	{
		return item.loadLabel(getContext().getPackageManager());
	}

	/** {@inheritDoc} This implementation delegates to {@link ApplicationInfo#loadDescription(PackageManager)}. */
	@Override
	protected CharSequence getItemDescription(final int position, final long id, final ApplicationInfo item)
	{
		return item.loadDescription(getContext().getPackageManager());
	}

	/** {@inheritDoc} This implementation delegates to {@link ApplicationInfo#loadIcon(PackageManager))}. */
	@Override
	protected Drawable getItemIcon(final int position, final long id, final ApplicationInfo item)
	{
		return item.loadIcon(getContext().getPackageManager());
	}
}
