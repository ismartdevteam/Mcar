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

import java.util.List;

import mn.ismartdev.mcar.R;
import mn.ismartdev.mcar.model.Company;

import org.lucasr.twowayview.TwoWayLayoutManager;
import org.lucasr.twowayview.TwoWayView;
import org.lucasr.twowayview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

public class CompanyAdapter extends
		RecyclerView.Adapter<CompanyAdapter.SimpleViewHolder> {

	Context mContext;
	private final TwoWayView mRecyclerView;
	private final List<Company> mItems;
	private ImageLoader mImageLoader;
	private RequestQueue mRequestQueue;

	public class SimpleViewHolder extends RecyclerView.ViewHolder {
		public final NetworkImageView image;

		public SimpleViewHolder(View view) {
			super(view);
			image = (NetworkImageView) view.findViewById(R.id.com_item_image);
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
	}

	public CompanyAdapter(Context context, TwoWayView recyclerView,
			List<Company> data) {
		mContext = context;
		mItems = data;

		mRecyclerView = recyclerView;
	}

	// public void addItem(int position) {
	// final int id = mCurrentItemId++;
	// mItems.add(position, id);
	// notifyItemInserted(position);
	// }

	public void removeItem(int position) {
		mItems.remove(position);
		notifyItemRemoved(position);
	}

	@Override
	public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View view = LayoutInflater.from(mContext).inflate(
				R.layout.company_item, parent, false);
		return new SimpleViewHolder(view);
	}

	@Override
	public void onBindViewHolder(SimpleViewHolder holder, int position) {
		Company com = mItems.get(position);
		holder.image.setImageUrl(com.logo, mImageLoader);

		boolean isVertical = (mRecyclerView.getOrientation() == TwoWayLayoutManager.Orientation.VERTICAL);
		final View itemView = holder.itemView;

		final int itemId = com.order;

		final int dimenId;
		if (itemId == 2) {
			dimenId = R.dimen.staggered_child_medium;
		} else if (itemId == 3) {
			dimenId = R.dimen.staggered_child_large;
		} else if (itemId == 4) {
			dimenId = R.dimen.staggered_child_xlarge;
		} else {
			dimenId = R.dimen.staggered_child_small;
		}

		final int span;
		if (itemId == 2) {
			span = 2;
		} else {
			span = 1;
		}

		final int size = mContext.getResources().getDimensionPixelSize(dimenId);

		final StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) itemView
				.getLayoutParams();

		if (!isVertical) {
			lp.span = span;
			lp.width = size;
			itemView.setLayoutParams(lp);
		} else {
			lp.span = span;
			lp.height = size;
			itemView.setLayoutParams(lp);
		}
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}
}