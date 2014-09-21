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
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

public class AdAdapter extends ArrayAdapter<Ad> {
	Context mContext;
	private ImageLoader mImageLoader;
	private RequestQueue mRequestQueue;
	private int lastPosition = -1;

	public AdAdapter(Context context, List<Ad> objects) {
		super(context, 0, 0, objects);
		this.mContext = context;
		// TODO Auto-generated constructor stub
		mRequestQueue = Volley.newRequestQueue(mContext);

		mImageLoader = new ImageLoader(mRequestQueue,
				new ImageLoader.ImageCache() {
					private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(
							10);

					public void putBitmap(String url, Bitmap bitmap) {
						mCache.put(url, bitmap);
					}

					public Bitmap getBitmap(String url) {
						return mCache.get(url);
					}
				});
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		// TODO Auto-generated method stub
		Ad ad = getItem(position);
		Holder hol = null;
		if (v == null) {
			v = ((Activity) mContext).getLayoutInflater().inflate(
					R.layout.ad_item, parent, false);
			hol = new Holder();
			hol.title = (TextView) v.findViewById(R.id.ad_item_title);
			hol.desc = (TextView) v.findViewById(R.id.ad_item_desc);
			hol.price = (TextView) v.findViewById(R.id.ad_item_price);
			hol.image = (NetworkImageView) v.findViewById(R.id.ad_item_image);
			v.setTag(hol);
		} else
			hol = (Holder) v.getTag();
		hol.title.setText(ad.title + "");
		hol.desc.setText(ad.description + "");
		hol.price.setText(numberToFormat(ad.price) + "₮");
		if (ad.images.length() > 0)
			hol.image.setImageUrl(ad.images, mImageLoader);
		Animation animation = AnimationUtils.loadAnimation(getContext(),
				(position > lastPosition) ? R.anim.up_from_bottom
						: R.anim.down_from_top);
		v.startAnimation(animation);
		lastPosition = position;
		return v;
	}

	class Holder {
		NetworkImageView image;
		TextView title;
		TextView desc;
		TextView price;
	}

	private String numberToFormat(int price) {
		DecimalFormat decimalFormat = new DecimalFormat("#");
		decimalFormat.setGroupingUsed(true);
		decimalFormat.setGroupingSize(3);
		return decimalFormat.format(price) + "";
	}
}
