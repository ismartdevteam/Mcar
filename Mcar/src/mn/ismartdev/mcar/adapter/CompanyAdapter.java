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
import mn.ismartdev.mcar.util.LruBitmapCache;

import org.lucasr.twowayview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

public class CompanyAdapter extends
		RecyclerView.Adapter<CompanyAdapter.SimpleViewHolder> {

	Context mContext;
	private final List<Company> mItems;
	private ImageLoader mImageLoader;
	private RequestQueue mRequestQueue;

	public class SimpleViewHolder extends RecyclerView.ViewHolder {
		public final NetworkImageView image;
		public final TextView name;

		public SimpleViewHolder(View view) {
			super(view);
			image = (NetworkImageView) view.findViewById(R.id.com_item_image);
			name = (TextView) view.findViewById(R.id.com_item_name);
			mRequestQueue = Volley.newRequestQueue(mContext);
			mImageLoader = new ImageLoader(mRequestQueue,
					new LruBitmapCache());
		}
	}

	public CompanyAdapter(Context context, List<Company> data) {
		mContext = context;
		mItems = data;

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
		holder.name.setText(com.name+"");

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
		if (itemId == 4 || itemId==3) {
			span = 2;
		} else {
			span = 1;
		}

		final int size = mContext.getResources().getDimensionPixelSize(dimenId);

		final StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) itemView
				.getLayoutParams();

		lp.span = span;
		lp.width = size;

		itemView.setLayoutParams(lp);

	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}
}
