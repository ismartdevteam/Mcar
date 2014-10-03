package mn.ismartdev.mcar.detail;

import java.sql.SQLException;
import java.util.List;

import mn.ismartdev.mcar.R;
import mn.ismartdev.mcar.model.Car;
import mn.ismartdev.mcar.model.CarMark;
import mn.ismartdev.mcar.model.CarModel;
import mn.ismartdev.mcar.model.DatabaseHelper;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
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

public class CarDetailAc extends ActionBarActivity {
	private DatabaseHelper helper;
	private List<Car> cars;
	private ViewPager pager;
	private int startId;
	private ActionBar bar;
	private int cat_id;
	Bundle b;

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
		pager = (ViewPager) findViewById(R.id.car_det_pager);
		helper = new DatabaseHelper(this);
		try {
			cars = helper.getCarDao().queryBuilder().orderBy("id", true)
					.where().ge("id", startId).and().eq("category_id", cat_id)
					.query();
			if (cars.size() > 0) {
				pager.setAdapter(new carTabAdapter(getSupportFragmentManager(),
						cars.size()));
//				pager.setPageTransformer(true, new ZoomOutPageTransformer());
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
				pager.setCurrentItem(0);
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

	public static class CarDet extends Fragment {
		int mID;
		private DatabaseHelper fragHelper;
		View v;
		Car data;
		private TextView markMod;

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
			markMod = (TextView) v.findViewById(R.id.car_det_markMod);
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
				try {
					mod = fragHelper.getModelDao()
							.queryForEq("id", data.model_id).get(0);
					mark = fragHelper.getMarkDao()
							.queryForEq("id", data.mark_id).get(0);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				markMod.setText(data.year + " " + mark.name+ " " + mod.name );
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.car_det_menu, menu);

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
