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

import static com.google.common.base.Preconditions.*;

import java.util.List;

import com.globalmentor.android.R;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
public class ApplicationInfoAdapter extends BaseAdapter
{
	private final Context context;
	private final List<ApplicationInfo> list;

	/**
	 * Constructor.
	 * @param context The current context.
	 * @param list The list to adapt.
	 * @param packageManager The current
	 */
	public ApplicationInfoAdapter(final Context context, final List<ApplicationInfo> list)
	{
		this.context = checkNotNull(context);
		this.list = checkNotNull(list);
	}

	@Override
	public int getCount()
	{
		return list.size();
	}

	@Override
	public Object getItem(int position)
	{
		return list.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent)
	{
		final ApplicationInfo applicationInfo = list.get(position);
		final View view = convertView != null ? convertView : LayoutInflater.from(context).inflate(R.layout.app_applicationinfoadapter_item, null); //inflate a layout if needed
		final ImageView iconImageView = (ImageView)view.findViewById(R.id.app_applicationinfoadapter_item_icon);
		final PackageManager packageManager = context.getPackageManager();
		iconImageView.setImageDrawable(applicationInfo.loadIcon(packageManager));
		final TextView labelTextView = (TextView)view.findViewById(R.id.app_applicationinfoadapter_item_label);
		labelTextView.setText(applicationInfo.loadLabel(packageManager));
		final TextView descriptionTextView = (TextView)view.findViewById(R.id.app_applicationinfoadapter_item_description);
		descriptionTextView.setText(applicationInfo.loadDescription(packageManager));
		return view;
	}
}
