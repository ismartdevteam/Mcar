/*
 * Copyright (C) 2014 Lucas Rocha
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

package mn.ismartdev.mcar.adapter;

import java.text.DecimalFormat;
import java.util.List;

import mn.ismartdev.mcar.R;
import mn.ismartdev.mcar.model.Ad;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

public class AdAdapter extends ArrayAdapter<Ad> {
	Context mContext;
	private ImageLoader mImageLoader;
	private RequestQueue mRequestQueue;

	public AdAdapter(Context context, List<Ad> objects) {
		super(context, 0, 0, objects);
		this.mContext = context;
		// TODO Auto-generated constructor stub

	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		// TODO Auto-generated method stub
		Ad ad = getItem(position);
		Holder hol = null;
		if (v == null) {
			v = ((Activity) mContext).getLayoutInflater().inflate(
					R.layout.ad_item, null);
			hol = new Holder();
			hol.title = (TextView) v.findViewById(R.id.test);
			v.setTag(hol);
		} else
			hol = (Holder) v.getTag();
		hol.title.setText(ad.title);
		return v;
	}

	class Holder {
		TextView title;
	}

	private String numberToFormat(int price) {
		DecimalFormat decimalFormat = new DecimalFormat("#");
		decimalFormat.setGroupingUsed(true);
		decimalFormat.setGroupingSize(3);
		return decimalFormat.format(price) + "";
	}
}
