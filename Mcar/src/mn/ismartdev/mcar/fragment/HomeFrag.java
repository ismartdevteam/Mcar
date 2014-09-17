package mn.ismartdev.mcar.fragment;

import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mn.ismartdev.mcar.R;
import mn.ismartdev.mcar.adapter.CarAdapter;
import mn.ismartdev.mcar.adapter.CompanyAdapter;
import mn.ismartdev.mcar.model.Car;
import mn.ismartdev.mcar.model.CarCategory;
import mn.ismartdev.mcar.model.Company;
import mn.ismartdev.mcar.model.DatabaseHelper;

import org.lucasr.twowayview.TwoWayView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

public class HomeFrag extends Fragment {
	private TwoWayView companyList;
	private DatabaseHelper helper;
	private List<Company> comData;
	private CompanyAdapter comAdapter;
	private List<CarCategory> catData;
	private ViewPager carPager;
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
	private int minScroll = 1;
	private int maxScroll = 4;
	private int currentScroll = 1;
	private boolean isScrolling = false;

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

			scrollTimer.schedule(scrollerSchedule, 60, 60);
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
		helper = new DatabaseHelper(getActivity());
		try {
			comData = helper.getComDao().queryBuilder().orderBy("order", false)
					.orderBy("type_id", true).query();
			comAdapter = new CompanyAdapter(getActivity(), comData);

			companyList.setAdapter(comAdapter);
			startAutoScrolling();
			catData = helper.getCarCatDao().queryForAll();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		carPager.setAdapter(new carTabAdapter(getActivity()
				.getSupportFragmentManager(), catData.size()));
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

	public class carTabAdapter extends FragmentStatePagerAdapter {
		int count;

		public carTabAdapter(FragmentManager fm, int size) {

			super(fm);
			this.count = size;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			// TODO Auto-generated method stub
			return catData.get(position).name;
		}

		@Override
		public Fragment getItem(int position) {
			return new CarsFrag().newInstance(position,
					catData.get(position).id);
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
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
}
