package mn.ismartdev.mcar.detail;

import java.sql.SQLException;
import java.util.List;

import mn.ismartdev.mcar.R;
import mn.ismartdev.mcar.model.Car;
import mn.ismartdev.mcar.model.CarBody;
import mn.ismartdev.mcar.model.CarMark;
import mn.ismartdev.mcar.model.CarModel;
import mn.ismartdev.mcar.model.DatabaseHelper;
import mn.ismartdev.mcar.util.CustomViewPager;
import mn.ismartdev.mcar.util.EnumCar;
import mn.ismartdev.mcar.util.Utils;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

public class CarDetailAc extends ActionBarActivity {
	private DatabaseHelper helper;
	private List<Car> cars;
	private CustomViewPager pager;
	private int startId;
	private ActionBar bar;
	private int cat_id;
	Bundle b;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.car_detail);
		b = getIntent().getExtras();
		bar = getSupportActionBar();
		bar.setDisplayShowHomeEnabled(true);
		bar.setHomeButtonEnabled(true);
		startId = b.getInt("car_id", 1);
		cat_id = b.getInt("type_id", 1);
		pager = (CustomViewPager) findViewById(R.id.car_det_pager);
		pager.setChildId(R.id.car_det_slider);
		pager.setOffscreenPageLimit(3);
		helper = new DatabaseHelper(this);
		try {
			cars = helper.getCarDao().queryBuilder().orderBy("id", true)
					.limit(10).orderBy("order", false).where()
					.ge("id", startId).and().eq("category_id", cat_id).query();
			if (cars.size() > 0) {
				pager.setAdapter(new carTabAdapter(getSupportFragmentManager(),
						cars.size()));
				// pager.setPageTransformer(true, new ZoomOutPageTransformer());
				pager.setOnPageChangeListener(new OnPageChangeListener() {

					@Override
					public void onPageSelected(int arg0) {
						// TODO Auto-generated method stub
						bar.setTitle(cars.get(arg0).model_name);
					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onPageScrollStateChanged(int arg0) {
						// TODO Auto-generated method stub

					}
				});
				bar.setTitle(cars.get(0).model_name);
			} else {
				Toast.makeText(this, getString(R.string.detail_error),
						Toast.LENGTH_SHORT).show();
				finish();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public class carTabAdapter extends FragmentStatePagerAdapter {
		int count;

		public carTabAdapter(FragmentManager fm, int size) {
			super(fm);
			this.count = size;
		}

		@Override
		public Fragment getItem(int position) {

			return CarDet.newInstance(cars.get(position).id);

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return count;
		}

	}

	public static class CarDet extends Fragment implements
			BaseSliderView.OnSliderClickListener {
		int mID;
		private DatabaseHelper fragHelper;
		View v;
		Car data;
		private TextView markMod;
		private TextView body_type;
		private TextView modiDate;
		private TextView cameDate;
		private TextView status;
		private TextView distance;
		private TextView roller;
		private TextView fuel;
		private TextView engine;

		private TextView trans;
		private TextView drivetrain;
		private TextView door;
		private TextView features;
		private TextView seller_notes_tv;
		private TextView seller_notes;
		private SliderLayout imageSlider;

		public static CarDet newInstance(int num) {

			CarDet f = new CarDet();
			// Supply num input as an argument.
			Bundle args = new Bundle();
			args.putInt("num", num);
			Log.i("id", num + "");
			f.setArguments(args);

			return f;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mID = getArguments() != null ? getArguments().getInt("num") : 1;

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			v = inflater.inflate(R.layout.car_detail_item, container, false);
			imageSlider = (SliderLayout) v.findViewById(R.id.car_det_slider);
			markMod = (TextView) v.findViewById(R.id.car_det_markMod);
			body_type = (TextView) v.findViewById(R.id.car_det_body);
			modiDate = (TextView) v.findViewById(R.id.car_det_modify);
			cameDate = (TextView) v.findViewById(R.id.car_det_cameDate);

			status = (TextView) v.findViewById(R.id.car_det_status);
			distance = (TextView) v.findViewById(R.id.car_det_distance);
			roller = (TextView) v.findViewById(R.id.car_det_roller);
			fuel = (TextView) v.findViewById(R.id.car_det_fuel);
			engine = (TextView) v.findViewById(R.id.car_det_engine);
			trans = (TextView) v.findViewById(R.id.car_det_trans);
			drivetrain = (TextView) v.findViewById(R.id.car_det_drivetrain);
			door = (TextView) v.findViewById(R.id.car_det_door);
			features = (TextView) v.findViewById(R.id.car_det_feautures);
			seller_notes = (TextView) v.findViewById(R.id.car_det_seller_notes);
			seller_notes_tv = (TextView) v
					.findViewById(R.id.car_det_seller_notes_tv);
			return v;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			fragHelper = new DatabaseHelper(getActivity());
			try {
				data = fragHelper.getCarDao().queryForId(mID);
				CarModel mod = null;
				CarMark mark = null;
				CarBody body = null;
				try {
					mod = fragHelper.getModelDao()
							.queryForEq("id", data.model_id).get(0);
					mark = fragHelper.getMarkDao()
							.queryForEq("id", data.mark_id).get(0);
					body = fragHelper.getBodyDao()
							.queryForEq("id", data.body_id).get(0);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String images[] = data.image_url.split(",");

				for (String url : images) {
					if (url.length() > 1) {
						TextSliderView textSliderView = new TextSliderView(
								getActivity());
						textSliderView
								.description(
										Utils.numberToFormat(data.price) + " ₮")
								.image(url)
								.setScaleType(
										BaseSliderView.ScaleType.CenterCrop)
								.setOnSliderClickListener(this);

						textSliderView.getBundle().putString("extra", url);

						imageSlider.addSlider(textSliderView);
					} else {
						TextSliderView textSliderView = new TextSliderView(
								getActivity());
						textSliderView
								.description(
										Utils.numberToFormat(data.price) + " ₮")
								.image(R.drawable.car_holder)
								.setScaleType(
										BaseSliderView.ScaleType.FitCenterCrop)
								.setOnSliderClickListener(this);

						textSliderView.getBundle().putString("extra",
								R.drawable.car_holder + "");

						imageSlider.addSlider(textSliderView);
						break;
					}
				}

				imageSlider
						.setPresetTransformer(SliderLayout.Transformer.Tablet);
				imageSlider
						.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
				imageSlider.setCustomAnimation(new DescriptionAnimation());
				imageSlider.setDuration(10000);

				markMod.setText(data.year + " " + mark.name + " " + mod.name
						+ " " + data.modification);
				body_type.setText(body.name + "");
				modiDate.setText(data.year + "");
				cameDate.setText(data.came_year + "");
				status.setText(EnumCar.status[data.status] + "");
				distance.setText(Utils.numberToFormat(data.distance)
						+ EnumCar.distance[data.distance_type]);
				roller.setText(EnumCar.roller[data.roller_type] + "");
				fuel.setText(EnumCar.fuel[data.fuel]);
				engine.setText(data.engine + "");
				trans.setText(EnumCar.transmission[data.transmission] + "");
				drivetrain.setText(data.drivetrain + "");
				door.setText(data.door + "");
				features.setText(data.features + "");
				if (data.seller_notes.length() < 1)
					seller_notes_tv.setVisibility(View.GONE);
				else
					seller_notes.setText(data.seller_notes + "");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onSliderClick(BaseSliderView slider) {
			// TODO Auto-generated method stub
			Log.i("position click", slider.getDescription());
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.com_det_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		// if (id == R.id.action_search_ad) {
		// mPagerAdapter.filterCar();
		// }
		if (id == android.R.id.home)
			onBackPressed();
		return true;
	}

}
