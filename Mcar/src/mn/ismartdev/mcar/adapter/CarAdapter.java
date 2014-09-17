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

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

import mn.ismartdev.mcar.R;
import mn.ismartdev.mcar.model.Car;
import mn.ismartdev.mcar.model.CarMark;
import mn.ismartdev.mcar.model.CarModel;
import mn.ismartdev.mcar.model.DatabaseHelper;
import mn.ismartdev.mcar.model.EnumCar;

import org.lucasr.twowayview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

public class CarAdapter extends
		RecyclerView.Adapter<CarAdapter.SimpleViewHolder> {

	Context mContext;
	private final List<Car> mItems;
	private ImageLoader mImageLoader;
	private RequestQueue mRequestQueue;
	DatabaseHelper helper;

	public class SimpleViewHolder extends RecyclerView.ViewHolder {
		public final NetworkImageView image;
		public final TextView name;
		public final TextView price;
		public final TextView status;
		public final TextView distance;

		public SimpleViewHolder(View view) {
			super(view);
			name = (TextView) view.findViewById(R.id.car_item_name);
			price = (TextView) view.findViewById(R.id.car_item_price);
			status = (TextView) view.findViewById(R.id.car_item_status);
			distance = (TextView) view.findViewById(R.id.car_item_distance);
			image = (NetworkImageView) view.findViewById(R.id.car_item_image);
			mRequestQueue = Volley.newRequestQueue(mContext);
			helper = new DatabaseHelper(mContext);
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

	public CarAdapter(Context context, List<Car> data) {
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
				R.layout.car_item, parent, false);
		return new SimpleViewHolder(view);
	}

	@Override
	public void onBindViewHolder(SimpleViewHolder holder, int position) {
		Car car = mItems.get(position);
		EnumCar enums = new EnumCar();
		int colorId = R.color.car_status_mglnew_trans;
		switch (car.status) {
		case 0:
			colorId = R.color.car_status_new_trans;
			break;

		case 2:
			colorId = R.color.car_status_old_trans;
			break;

		}
		holder.status.setBackgroundColor(mContext.getResources().getColor(
				colorId));
		holder.status.setText(enums.status[car.status]);
		holder.distance.setText(numberToFormat(car.distance) + " "
				+ enums.distance[car.distance_type]);
		if(car.image_url.length()>0)
		holder.image.setImageUrl(car.image_url, mImageLoader);
		CarModel mod = null;
		CarMark mark = null;
		try {
			mod = helper.getModelDao().queryForEq("id", car.model_id).get(0);
			mark = helper.getMarkDao().queryForEq("id", mod.mark_id).get(0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		holder.price.setText(numberToFormat(car.price) + "₮");
		holder.name.setText(car.year + " " + mod.name + " " + mark.name);
		// boolean isVertical = (mRecyclerView.getOrientation() ==
		// TwoWayLayoutManager.Orientation.VERTICAL);
		final View itemView = holder.itemView;

		// final int itemId = com.car;

		// // if (itemId == 2) {
		// // dimenId = R.dimen.staggered_child_medium;
		// // } else if (itemId == 3) {
		// // dimenId = R.dimen.staggered_child_large;
		// // } else if (itemId == 4) {
		// // dimenId
		// // } else {
		// // dimenId = R.dimen.staggered_child_small;
		// // }
		//
		final int span = 2;
		// // if (itemId == 2) {
		// // span = 2;
		// // } else {
		// // span = 1;
		// // }
		//

		final StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) itemView
				.getLayoutParams();

		lp.span = span;
		lp.height = StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT;
		lp.width = StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT;
		itemView.setLayoutParams(lp);

	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	private String numberToFormat(int price) {
		DecimalFormat decimalFormat = new DecimalFormat("#");
		decimalFormat.setGroupingUsed(true);
		decimalFormat.setGroupingSize(3);
		return decimalFormat.format(price) + "";
	}
}
