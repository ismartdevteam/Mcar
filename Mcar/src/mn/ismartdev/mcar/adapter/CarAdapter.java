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
import java.util.List;

import mn.ismartdev.mcar.R;
import mn.ismartdev.mcar.model.Car;
import mn.ismartdev.mcar.model.CarMark;
import mn.ismartdev.mcar.model.CarModel;
import mn.ismartdev.mcar.model.DatabaseHelper;
import mn.ismartdev.mcar.util.EnumCar;
import mn.ismartdev.mcar.util.LruBitmapCache;
import mn.ismartdev.mcar.util.Utils;

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
		public final TextView fuel;
		public final TextView roller;
		public final TextView engine;

		public SimpleViewHolder(View view) {
			super(view);
			name = (TextView) view.findViewById(R.id.car_item_name);
			price = (TextView) view.findViewById(R.id.car_item_price);
			status = (TextView) view.findViewById(R.id.car_item_status);
			engine = (TextView) view.findViewById(R.id.car_item_engine);
			fuel = (TextView) view.findViewById(R.id.car_item_fuel);
			roller = (TextView) view.findViewById(R.id.car_item_roller_type);
			distance = (TextView) view.findViewById(R.id.car_item_distance);
			image = (NetworkImageView) view.findViewById(R.id.car_item_image);
			mRequestQueue = Volley.newRequestQueue(mContext);
			helper = new DatabaseHelper(mContext);
			mImageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache());
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
		int colorId = R.color.car_status_mglnew_trans;
		switch (car.status) {
		case 0:
			colorId = R.color.car_status_new_trans;
			break;

		case 2:
			colorId = R.color.car_status_old_trans;
			break;

		}
		holder.fuel.setText(EnumCar.fuel[car.fuel]);
		holder.engine.setText(car.engine + " л");
		holder.roller.setText(EnumCar.roller[car.roller_type]);
		holder.status.setBackgroundColor(mContext.getResources().getColor(
				colorId));
		holder.status.setText(EnumCar.status[car.status]);
		holder.distance.setText(Utils.numberToFormat(car.distance) + " "
				+ EnumCar.distance[car.distance_type]);
		if (car.image_url.length() > 0)
			holder.image.setImageUrl(car.image_url.split(",")[0], mImageLoader);
		CarModel mod = null;
		CarMark mark = null;
		try {
			mod = helper.getModelDao().queryForEq("id", car.model_id).get(0);
			mark = helper.getMarkDao().queryForEq("id", car.mark_id).get(0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		holder.price.setText(Utils.numberToFormat(car.price)+" ₮");
		holder.name.setText(car.year + " " + mark.name + " " + mod.name);
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


}
