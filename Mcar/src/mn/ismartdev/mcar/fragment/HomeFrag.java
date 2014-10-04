package mn.ismartdev.mcar.fragment;

import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mn.ismartdev.mcar.MainActivity;
import mn.ismartdev.mcar.R;
import mn.ismartdev.mcar.adapter.CarAdapter;
import mn.ismartdev.mcar.adapter.CompanyAdapter;
import mn.ismartdev.mcar.detail.CarDetailAc;
import mn.ismartdev.mcar.detail.CompanyDetailAc;
import mn.ismartdev.mcar.model.Car;
import mn.ismartdev.mcar.model.CarCategory;
import mn.ismartdev.mcar.model.CarMark;
import mn.ismartdev.mcar.model.CarModel;
import mn.ismartdev.mcar.model.Company;
import mn.ismartdev.mcar.model.DatabaseHelper;
import mn.ismartdev.mcar.util.EnumCar;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.ItemClickSupport.OnItemClickListener;
import org.lucasr.twowayview.TwoWayView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

public class HomeFrag extends Fragment implements OnClickListener {
	private TwoWayView companyList;
	private DatabaseHelper helper;
	private List<Company> comData;
	private CompanyAdapter comAdapter;
	private List<CarCategory> catData;
	private ViewPager carPager;
	private List<CarModel> modelData;
	private PagerSlidingTabStrip tabs;
	// scrool company
	private int verticalScrollMax;
	private Timer scrollTimer = null;
	private TimerTask clickSchedule;
	private TimerTask scrollerSchedule;
	private TimerTask faceAnimationSchedule;
	private int scrollPos = 0;
	private Timer clickTimer = null;
	private Timer faceTimer = null;

	private int currentScroll = 1;
	private Dialog filterdialog;
	private String all = "Бүгд";

	private EditText year;
	private List<CarMark> markData;
	private Spinner model;
	private Spinner status;
	private Spinner mark;
	private LinearLayout searchLin;
	private carTabAdapter tabAdapter;

	public void getScrollMaxAmount() {
		int actualWidth = (companyList.getMeasuredWidth() - (256 * 3));
		verticalScrollMax = actualWidth;
	}

	public void startAutoScrolling() {
		if (scrollTimer == null) {
			scrollTimer = new Timer();
			final Runnable Timer_Tick = new Runnable() {
				public void run() {
					moveScrollView();
				}
			};

			if (scrollerSchedule != null) {
				scrollerSchedule.cancel();
				scrollerSchedule = null;
			}
			scrollerSchedule = new TimerTask() {
				@Override
				public void run() {
					getActivity().runOnUiThread(Timer_Tick);
				}
			};

			scrollTimer.schedule(scrollerSchedule, 70, 70);
		}
	}

	public void moveScrollView() {
		scrollPos = (int) (companyList.getScrollX() + currentScroll);
		if (scrollPos >= verticalScrollMax) {
			scrollPos = 0;
		}
		companyList.scrollBy(currentScroll, 0);
	}

	public void stopAutoScrolling() {
		if (scrollTimer != null) {
			scrollTimer.cancel();
			scrollTimer = null;
		}
	}

	public void onDestroy() {
		clearTimerTaks(clickSchedule);
		clearTimerTaks(scrollerSchedule);
		clearTimerTaks(faceAnimationSchedule);
		clearTimers(scrollTimer);
		clearTimers(clickTimer);
		clearTimers(faceTimer);

		clickSchedule = null;
		scrollerSchedule = null;
		faceAnimationSchedule = null;
		scrollTimer = null;
		clickTimer = null;
		faceTimer = null;
		super.onDestroy();
	}

	private void clearTimers(Timer timer) {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	private void clearTimerTaks(TimerTask timerTask) {
		if (timerTask != null) {
			timerTask.cancel();
			timerTask = null;
		}
	}

	public HomeFrag() {
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);

		helper = new DatabaseHelper(getActivity());
		try {
			comData = helper.getComDao().queryBuilder().orderBy("order", false)
					.orderBy("type_id", true).query();
			comAdapter = new CompanyAdapter(getActivity(), comData);

			companyList.setAdapter(comAdapter);
			final ItemClickSupport itemClick = ItemClickSupport
					.addTo(companyList);

			itemClick.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(RecyclerView parent, View child,
						int position, long id) {
					Bundle b = new Bundle();
					b.putInt("com_id", comData.get(position).id);
					Intent comDet = new Intent(getActivity(),
							CompanyDetailAc.class);
					comDet.putExtras(b);
					startActivity(comDet);
				}
			});

			startAutoScrolling();
			catData = helper.getCarCatDao().queryForAll();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tabAdapter = new carTabAdapter(getActivity()
				.getSupportFragmentManager(), catData.size(), 1);
		carPager.setAdapter(tabAdapter);
		carPager.setOffscreenPageLimit(1);
		tabs.setViewPager(carPager);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		companyList = (TwoWayView) rootView.findViewById(R.id.company_list);
		companyList.setHasFixedSize(true);
		companyList.setLongClickable(true);
		carPager = (ViewPager) rootView.findViewById(R.id.carPager);
		tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.carTab);

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (!MainActivity.mNavigationDrawerFragment.isDrawerOpen()) {
			inflater.inflate(R.menu.main, menu);

			if (!companyList.isShown()) {
				menu.getItem(0).setIcon(R.drawable.ic_action_expand);
			}
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_search) {

			try {
				initFilterDialog();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		if (item.getItemId() == R.id.action_collapse) {
			if (companyList.isShown()) {
				companyList.setVisibility(View.GONE);
				item.setIcon(R.drawable.ic_action_expand);
			} else {
				item.setIcon(R.drawable.ic_action_collapse);
				companyList.setVisibility(View.VISIBLE);
			}

		}
		return true;
	}

	private void initFilterDialog() throws SQLException {
		filterdialog = new Dialog(getActivity(),
				android.R.style.Theme_Translucent_NoTitleBar);
		filterdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		filterdialog.setContentView(R.layout.car_search_dialog);
		filterdialog.setCanceledOnTouchOutside(true);
		filterdialog.setCancelable(true);
		status = (Spinner) filterdialog.findViewById(R.id.car_filter_status);
		mark = (Spinner) filterdialog.findViewById(R.id.car_filter_mark);
		model = (Spinner) filterdialog.findViewById(R.id.car_filter_model);
		year = (EditText) filterdialog.findViewById(R.id.car_filter_year);
		searchLin = (LinearLayout) filterdialog
				.findViewById(R.id.car_filter_search);
		searchLin.setOnClickListener(this);
		ArrayAdapter<String> status_adapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item,
				EnumCar.status);
		status_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		status.setAdapter(status_adapter);
		status.setSelection(1);
		markData = helper.getMarkDao().queryForAll();
		CarMark allMark = new CarMark();
		allMark.id = 0;
		allMark.name = all;
		markData.add(0, allMark);
		mark.setAdapter(new MyMarkAdapter(getActivity(), markData));

		mark.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Log.e("sdadas", "asdas");
				if (position != 0) {
					try {
						modelData = helper.getModelDao().queryForEq("mark_id",
								markData.get(position).id);
						CarModel allModel = new CarModel();
						allModel.id = 0;
						allModel.name = all;
						allModel.mark_id = markData.get(position).id;
						modelData.add(0, allModel);
						model.setAdapter(new MyModelAdapter(getActivity(),
								modelData));

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		filterdialog.show();
	}

	public class carTabAdapter extends FragmentStatePagerAdapter {
		int count;
		int type;

		public carTabAdapter(FragmentManager fm, int size, int type) {
			super(fm);
			this.count = size;
			this.type = type;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			// TODO Auto-generated method stub

			return catData.get(position).name;
		}

		@Override
		public Fragment getItem(int position) {
			if (type == 1)
				return CarsFrag.newInstance(position, catData.get(position).id);
			else {
				int selModel = 0;
				if (modelData != null)
					selModel = modelData.get(model.getSelectedItemPosition()).id;
				return new filterFrag().newInstance(position,
						catData.get(position).id,
						status.getSelectedItemPosition(),
						markData.get(mark.getSelectedItemPosition()).id,
						selModel, year.getText() + "");
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return count;
		}

	}

	public static class CarsFrag extends Fragment {
		int mNum;
		private DatabaseHelper helper;
		private List<Car> carsList;
		private CarAdapter adapter;
		View v;
		private TwoWayView carList;
		private int type_id;

		public static CarsFrag newInstance(int num, int type_id) {

			CarsFrag f = new CarsFrag();
			// Supply num input as an argument.
			Bundle args = new Bundle();
			args.putInt("num", num + 1);
			args.putInt("type", type_id);
			f.setArguments(args);

			return f;
		}

		/**
		 * When creating, retrieve this instance's number from its arguments.
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mNum = getArguments() != null ? getArguments().getInt("num") : 1;
			type_id = getArguments() != null ? getArguments().getInt("type")
					: 1;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			v = inflater.inflate(R.layout.cars, container, false);
			carList = (TwoWayView) v.findViewById(R.id.car_list);
			carList.setHasFixedSize(true);
			carList.setLongClickable(true);
			carList.setItemAnimator(new DefaultItemAnimator());
			return v;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			helper = new DatabaseHelper(getActivity());
			try {
				carsList = helper.getCarDao().queryBuilder()
						.orderBy("order", false).where()
						.eq("category_id", type_id).query();
				adapter = new CarAdapter(getActivity(), carsList);
				carList.setAdapter(adapter);
				final ItemClickSupport itemClick = ItemClickSupport
						.addTo(carList);

				itemClick.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(RecyclerView parent, View child,
							int position, long id) {
						Bundle b = new Bundle();
						Log.e("car id", carsList.get(position).id + " id");
						b.putInt("car_id", carsList.get(position).id);
						b.putInt("type_id", carsList.get(position).category_id);
						
//						CarDetailAc de = new CarDetailAc(carsList);
						Intent carDet = new Intent(getActivity(),
								CarDetailAc.class);
						carDet.putExtras(b);
						startActivity(carDet);
					}
				});
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public class filterFrag extends Fragment {
		int mNum;
		public DatabaseHelper helper;
		public List<Car> carsList;
		public CarAdapter adapter;
		View v;
		public TwoWayView carList;
		public int type_id;
		private int mark_id;
		private int model_id;
		private int status_id;
		private String yearStr;

		public filterFrag newInstance(int num, int type_id, int status,
				int mark, int model, String year) {

			filterFrag f = new filterFrag();
			// Supply num input as an argument.
			Bundle args = new Bundle();
			args.putInt("num", num + 1);
			args.putInt("type", type_id);
			args.putInt("status", status);
			args.putInt("mark", mark);
			args.putInt("model", model);
			args.putString("year", year);
			f.setArguments(args);

			return f;
		}

		/**
		 * When creating, retrieve this instance's number from its arguments.
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			Log.d("title", "onCreate");
			mNum = getArguments() != null ? getArguments().getInt("num") : 1;
			type_id = getArguments() != null ? getArguments().getInt("type")
					: 1;
			status_id = getArguments() != null ? getArguments()
					.getInt("status") : 0;
			mark_id = getArguments() != null ? getArguments().getInt("mark")
					: 0;
			model_id = getArguments() != null ? getArguments().getInt("model")
					: 0;
			yearStr = getArguments() != null ? getArguments().getString("year")
					: "2014";

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			Log.d("title", "onView");
			v = inflater.inflate(R.layout.cars, container, false);
			carList = (TwoWayView) v.findViewById(R.id.car_list);
			carList.setHasFixedSize(true);
			carList.setLongClickable(true);

			return v;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			helper = new DatabaseHelper(getActivity());
			try {
				QueryBuilder<Car, Integer> carBuilder = helper.getCarDao()
						.queryBuilder();
				Where<Car, Integer> where = carBuilder.where();
				where.eq("status", status_id);
				where.and().eq("category_id", type_id);
				if (mark_id != 0) {
					where.and();
					where.eq("mark_id", mark_id);
				}
				if (model_id != 0) {
					where.and();
					where.eq("model_id", model_id);
				}
				where.and();
				where.gt("year", yearStr);
				PreparedQuery<Car> preparedQuery = carBuilder.prepare();
				carsList = helper.getCarDao().query(preparedQuery);
				if (carsList.size() == 0)
					carList.setVisibility(View.GONE);
				else {
					adapter = new CarAdapter(getActivity(), carsList);
					carList.setAdapter(adapter);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == searchLin) {
			int selPos = carPager.getCurrentItem();
			tabAdapter = new carTabAdapter(getActivity()
					.getSupportFragmentManager(), catData.size(), 0);
			carPager.setAdapter(tabAdapter);
			tabs.setViewPager(carPager);
			carPager.setCurrentItem(selPos, false);
			filterdialog.dismiss();
		}
	}

	private class MyMarkAdapter extends ArrayAdapter<CarMark> {
		Context mContext;

		class Holder {
			TextView title;
		}

		public MyMarkAdapter(Context context, List<CarMark> objects) {
			super(context, 0, 0, objects);
			this.mContext = context;
			// TODO Auto-generated constructor stub
		}

		public View getCustomView(int position, View v, ViewGroup parent) {
			CarMark data = getItem(position);
			Holder hol;
			if (v == null) {
				hol = new Holder();
				v = ((Activity) mContext).getLayoutInflater().inflate(
						R.layout.list_item, parent, false);
				hol.title = (TextView) v.findViewById(R.id.spinner_item);
				v.setTag(hol);
			} else
				hol = (Holder) v.getTag();
			hol.title.setText(data.name);

			return v;
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			return getCustomView(position, convertView, parent);
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			return getCustomView(position, v, parent);
		}

	}

	private class MyModelAdapter extends ArrayAdapter<CarModel> {
		Context mContext;

		class Holder {
			TextView title;
		}

		public MyModelAdapter(Context context, List<CarModel> objects) {
			super(context, 0, 0, objects);
			this.mContext = context;
			// TODO Auto-generated constructor stub
		}

		public View getCustomView(int position, View v, ViewGroup parent) {
			CarModel data = getItem(position);
			Holder hol;
			if (v == null) {
				hol = new Holder();
				v = ((Activity) mContext).getLayoutInflater().inflate(
						R.layout.list_item, parent, false);
				hol.title = (TextView) v.findViewById(R.id.spinner_item);
				v.setTag(hol);
			} else
				hol = (Holder) v.getTag();
			hol.title.setText(data.name);

			return v;
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			return getCustomView(position, convertView, parent);
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			return getCustomView(position, v, parent);
		}

	}

}
