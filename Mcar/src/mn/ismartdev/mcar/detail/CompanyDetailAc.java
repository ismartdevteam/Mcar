package mn.ismartdev.mcar.detail;

import java.sql.SQLException;

import mn.ismartdev.mcar.R;
import mn.ismartdev.mcar.model.Company;
import mn.ismartdev.mcar.model.DatabaseHelper;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class CompanyDetailAc extends ActionBarActivity implements
		BaseSliderView.OnSliderClickListener {
	private SliderLayout imageSlider;
	private DatabaseHelper helper;
	private Company company;
	private String[] images;
	private ActionBar bar;
	private TextView desc;
	private TextView contact;
	private TextView phone;
	private TextView address;
	private GoogleMap mMap;
	private UiSettings mUiSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.com_detail);

		helper = new DatabaseHelper(this);
		try {
			company = helper.getComDao().queryForId(
					getIntent().getExtras().getInt("com_id", 1));
			initView(company);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		bar = getSupportActionBar();
		bar.setDisplayShowHomeEnabled(true);
		bar.setHomeButtonEnabled(true);
		bar.setTitle(company.name);
	}

	private void setUpMapIfNeeded() {

		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				if (company.location.split(",").length == 2)
					setUpMap();
			}
		}
		// if (!fromBranch) {

		LocationManager lm = null;
		boolean gps_enabled = false, network_enabled = false;
		if (lm == null)
			lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		try {
			gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}
		try {
			network_enabled = lm
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}

		if (!gps_enabled && !network_enabled) {
			Builder dialog = new AlertDialog.Builder(this);
			dialog.setMessage("GPS унтраастай байна");
			dialog.setPositiveButton("Нээх",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(
								DialogInterface paramDialogInterface,
								int paramInt) {
							// TODO Auto-generated method stub
							Intent myIntent = new Intent(
									Settings.ACTION_SECURITY_SETTINGS);
							startActivity(myIntent);
						}
					});
			dialog.setNegativeButton("Болих",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(
								DialogInterface paramDialogInterface,
								int paramInt) {
							// TODO Auto-generated method stub

						}
					});
			dialog.show();

		}

	}

	private void setUpMap() {

		mUiSettings = mMap.getUiSettings();
		mMap.setMyLocationEnabled(true);

		mUiSettings.setMyLocationButtonEnabled(true);

		// mark
		String[] location = company.location.split(",");

		LatLng lng = new LatLng(Double.parseDouble(location[0]),
				Double.parseDouble(location[1]));
		mMap.addMarker(new MarkerOptions().alpha(0.7f).title(company.name)
				.position(lng).snippet(company.address));

		// mMap.setOnInfoWindowClickListener(this);
		mMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				marker.showInfoWindow();

				return false;
			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	private void initView(Company com) {
		// make views
		imageSlider = (SliderLayout) findViewById(R.id.com_slider);
		desc = (TextView) findViewById(R.id.com_det_desc);
		contact = (TextView) findViewById(R.id.com_det_contact);
		phone = (TextView) findViewById(R.id.com_det_phone);
		address = (TextView) findViewById(R.id.com_det_address);
		desc.setText(com.description + "");
		contact.setText(com.contact + "");
		phone.setText(com.phone + "");
		address.setText(com.address + "");
		images = com.images.split(",");
		TextSliderView logoSlide = new TextSliderView(this);
		// initialize a SliderLayout
		logoSlide.description(com.name).image(com.logo)
				.setScaleType(BaseSliderView.ScaleType.FitCenterCrop)
				.setOnSliderClickListener(this);

		// add your extra information
		logoSlide.getBundle().putString("extra", com.logo);

		imageSlider.addSlider(logoSlide);
		for (String url : images) {
			if (url.length() > 1) {
				TextSliderView textSliderView = new TextSliderView(this);
				// initialize a SliderLayout
				textSliderView.description(com.address).image(url)
						.setScaleType(BaseSliderView.ScaleType.FitCenterCrop)
						.setOnSliderClickListener(this);

				// add your extra information
				textSliderView.getBundle().putString("extra", url);

				imageSlider.addSlider(textSliderView);
			}
		}
		imageSlider.setPresetTransformer(SliderLayout.Transformer.Tablet);
		imageSlider
				.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
		imageSlider.setCustomAnimation(new DescriptionAnimation());
		imageSlider.setDuration(10000);
		setUpMapIfNeeded();
	}

	@Override
	public void onSliderClick(BaseSliderView slider) {
		// TODO Auto-generated method stub

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
